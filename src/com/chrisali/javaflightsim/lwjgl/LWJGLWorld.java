/*******************************************************************************
 * Copyright (C) 2016-2017 Christopher Ali
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *  If you have any questions about this project, you can visit
 *  the project's GitHub repository at: http://github.com/chris-ali/j6dof-flight-sim/
 ******************************************************************************/
package com.chrisali.javaflightsim.lwjgl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.chrisali.javaflightsim.initializer.LWJGLSwingSimulationController;
import com.chrisali.javaflightsim.lwjgl.audio.AudioMaster;
import com.chrisali.javaflightsim.lwjgl.audio.SoundCollection;
import com.chrisali.javaflightsim.lwjgl.audio.SoundCollection.SoundCategory;
import com.chrisali.javaflightsim.lwjgl.entities.Camera;
import com.chrisali.javaflightsim.lwjgl.entities.EntityCollections;
import com.chrisali.javaflightsim.lwjgl.entities.Light;
import com.chrisali.javaflightsim.lwjgl.entities.Ownship;
import com.chrisali.javaflightsim.lwjgl.interfaces.font.FontType;
import com.chrisali.javaflightsim.lwjgl.interfaces.font.GUIText;
import com.chrisali.javaflightsim.lwjgl.interfaces.font.TextMaster;
import com.chrisali.javaflightsim.lwjgl.models.TexturedModel;
import com.chrisali.javaflightsim.lwjgl.particles.Cloud;
import com.chrisali.javaflightsim.lwjgl.particles.ParticleMaster;
import com.chrisali.javaflightsim.lwjgl.particles.ParticleTexture;
import com.chrisali.javaflightsim.lwjgl.renderengine.DisplayManager;
import com.chrisali.javaflightsim.lwjgl.renderengine.Loader;
import com.chrisali.javaflightsim.lwjgl.renderengine.MasterRenderer;
import com.chrisali.javaflightsim.lwjgl.renderengine.OBJLoader;
import com.chrisali.javaflightsim.lwjgl.terrain.Terrain;
import com.chrisali.javaflightsim.lwjgl.terrain.TerrainCollection;
import com.chrisali.javaflightsim.lwjgl.textures.ModelTexture;
import com.chrisali.javaflightsim.lwjgl.utilities.OTWDirectories;
import com.chrisali.javaflightsim.simulation.datatransfer.FlightData;
import com.chrisali.javaflightsim.simulation.datatransfer.FlightDataListener;
import com.chrisali.javaflightsim.simulation.datatransfer.FlightDataType;
import com.chrisali.javaflightsim.simulation.interfaces.OTWWorld;
import com.chrisali.javaflightsim.simulation.setup.InitialConditions;
import com.chrisali.javaflightsim.simulation.setup.Options;
import com.chrisali.javaflightsim.simulation.setup.SimulationConfiguration;
import com.chrisali.javaflightsim.swing.GuiFrame;
import com.chrisali.javaflightsim.swing.SimulationWindow;
import com.chrisali.javaflightsim.swing.optionspanel.AudioOptions;
import com.chrisali.javaflightsim.swing.optionspanel.DisplayOptions;

/**
 * Runner class for out the window display for Java Flight Sim. It utilizes LWJGL to create a 3D world in OpenGL. 
 * The display runs in a separate thread that receives data from {@link FlightData} via {@link FlightDataListener} 
 * 
 * @author Christopher Ali
 *
 */
public class LWJGLWorld implements Runnable, FlightDataListener, OTWWorld {
	
	//Logging
	private static final Logger logger = LogManager.getLogger(LWJGLWorld.class);
	
	private Loader loader;
	private MasterRenderer masterRenderer;
	private List<Light> lights;
	
	private LWJGLSwingSimulationController controller;
	private SimulationConfiguration configuration;
	
	// Sound Fields
	private Map<SoundCategory, Double> soundValues = new EnumMap<>(SoundCategory.class);
	private boolean recordPrev = true; // Used in FlightDataListener to record soundValues data to PREV_STEP_* enums
	
	// Collections for in-game objects
	private TerrainCollection terrainCollection;
	private EntityCollections entities;
	
	// Ownship is the "player" that moves around the world based on data received from FlightData
	private Ownship ownship;
	private Vector3f ownshipPosition;
	private Vector3f ownshipRotation;
	private Camera camera;
	
	private Map<String, GUIText> texts = new HashMap<>();
	
	private boolean running = false;
	
	/**
	 * Sets up OTW display with {@link DisplayOptions} and {@link AudioOptions}. If {@link LWJGLSwingSimulationController}
	 * object specified, display will embed itself within {@link SimulationWindow} in {@link GuiFrame} 
	 * 
	 * @param controller
	 */
	public LWJGLWorld(LWJGLSwingSimulationController controller) {
		this.controller = controller;
		configuration = controller.getConfiguration();
	}	
	
	@Override
	public void run() {
		
		//=================================== Set Up =========================================================
		
		try { setUp(); } 
		catch (Exception e) {logger.fatal("Error encountered when setting up LWJGL display!", e);}
		
		try {loadAssets(); }
		catch (Exception e) {logger.fatal("Error encountered when loading assets for LWJGL display!", e);}
		
		running = true;

		//=============================== Main Loop ==========================================================

		while (!Display.isCloseRequested() && running) {
			try {
				//--------- Movement ----------------
				camera.move(ownshipPosition, ownshipRotation.x, ownshipRotation.y, ownshipRotation.z);
				ownship.move(ownshipPosition, ownshipRotation.x, ownshipRotation.y, ownshipRotation.z);
				
				//--------- Particles ---------------
				ParticleMaster.update(camera);
				
				//--------- Audio--------------------
				SoundCollection.update(soundValues);
				
				//----------- UI --------------------
				texts.get("Paused").setTextString(configuration.getSimulationOptions()
																.contains(Options.PAUSED) ?
																		  "PAUSED" : "");
				
				//------ Render Everything -----------
				masterRenderer.renderWholeScene(entities, terrainCollection.getTerrainTree(), 
												lights, camera, new Vector4f(0, 1, 0, 0));
				ParticleMaster.renderParticles(camera);
				TextMaster.render(texts);
				
				DisplayManager.updateDisplay();
				
				Thread.sleep(15);
			} catch (Exception e) {
				logger.error("Error encountered while running LWJGL display! Attempting to continue...", e);
				
				continue;
			}
		}
		
		running = false;
		
		//================================ Clean Up =========================================================
		
		try { cleanUp(); }
		catch (Exception e) {logger.fatal("Error encountered when cleaning up LWJGL display!", e);}
	}
	
	/**
	 * Sets up all display and rendering processes and prepares them to run
	 */
	private void setUp() {
		
		// Initialize display window depending on presence of SimulationController's MainFrame object,
		// set in RunJavaFlightSimulator
		
		logger.debug("Starting up LWJGL display...");

		if (controller.getGuiFrame() != null)
			DisplayManager.createDisplay(controller.getGuiFrame().getSimulationWindow());
		else
			DisplayManager.createDisplay();
		
		loader = new Loader();
		
		// Set up renderer with fog and sky config

		logger.debug("Generating fog and sky...");
		
		masterRenderer = new MasterRenderer();
		MasterRenderer.setSkyColor(new Vector3f(0.70f, 0.90f, 1.0f));
		MasterRenderer.setFogDensity(0.0005f);
		MasterRenderer.setFogGradient(3.5f);
		
		// Initialize sounds and position of listener

		logger.debug("Initializing audio...");
		
		AudioMaster.init();
		AudioMaster.setListenerData(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));
		
		// Load particles and on-screen text
		
		logger.debug("Loading on-screen text and particles...");
		
		ParticleMaster.init(loader, masterRenderer.getProjectionMatrix());
		TextMaster.init(loader);
		
		// Load all entities (lights, entities, particles, etc)
		
		logger.debug("Loading entities...");
	}
	
	/**
	 * Closes all display and rendering processes, and closes display window 
	 */
	private void cleanUp() {

		logger.debug("Cleaning up and closing LWJGL display...");
		
		AudioMaster.cleanUp();
		ParticleMaster.cleanUp();
		TextMaster.cleanUp();
		masterRenderer.cleanUp();
		loader.cleanUp();
		
		DisplayManager.closeDisplay();
	}
	
	/**
	 * Initializes and generates all assets needed to render lights, entities, particles terrain and text
	 */
	private void loadAssets() {
		
		//==================================== Sun ===========================================================
		
		logger.debug("Generating sun...");
		
		lights = new ArrayList<>();
		lights.add(new Light(new Vector3f(20000, 40000, 20000), new Vector3f(0.95f, 0.95f, 0.95f)));
		
		//================================= Entities ==========================================================
		
		logger.debug("Generating collections of entities...");
		
		entities = new EntityCollections(lights, loader);
		
		//================================= Ownship ===========================================================
		
		logger.debug("Creating ownship...");
		
		// Model used for aircraft; scale set to 0 to be invisible for now
		TexturedModel bunny =  new TexturedModel(OBJLoader.loadObjModel("bunny", OTWDirectories.ENTITIES.toString(), loader), 
			    								new ModelTexture(loader.loadTexture("bunny", OTWDirectories.ENTITIES.toString())));
		// Initial position of ownship
		Map<InitialConditions, Double> initialConditions = configuration.getInitialConditions();
		ownshipPosition = new Vector3f((float)initialConditions.get(InitialConditions.INITN).doubleValue() / 15,
									   (float)initialConditions.get(InitialConditions.INITD).doubleValue() / 15, 
									   (float)initialConditions.get(InitialConditions.INITE).doubleValue() / 15); //(800, 150, 800)
		ownshipRotation = new Vector3f((float)Math.toDegrees(initialConditions.get(InitialConditions.INITPHI)),
									   (float)Math.toDegrees(initialConditions.get(InitialConditions.INITTHETA)), 
									   (float)Math.toDegrees(initialConditions.get(InitialConditions.INITPSI)) - 270); // (0, 0, 135)
		ownship = new Ownship(bunny, ownshipPosition, ownshipRotation.z, ownshipRotation.z, ownshipRotation.x, 0.000f);
		
		entities.addToStaticEntities(ownship);
		
		logger.debug("Setting up camera...");
		
		// Camera tied to ownship as first person view
		camera = new Camera(ownship);
		camera.setChaseView(false);
		camera.setPilotPosition(new Vector3f(0, 0, 0));

		//================================= Terrain ==========================================================
		
		logger.debug("Generating terrain...");
		
		terrainCollection = new TerrainCollection(10, loader, ownship);
		entities.setTerrainTree(terrainCollection.getTerrainTree());
		
		//=============================== Particles ==========================================================
		
		logger.debug("Generating clouds...");
		
		ParticleTexture clouds = new ParticleTexture(loader.loadTexture("clouds", OTWDirectories.PARTICLES.toString()), 4, true);
		
		// Generates clouds at random positions along terrain map
		Random random = new Random();
		for (int i = 0; i < 2000; i++)
			new Cloud(clouds, new Vector3f(random.nextInt(800*10), 300, i*10), new Vector3f(0, 0, 0), 0, 200);
		
		//=============================== Interface ==========================================================
		
		logger.debug("Generating on-screen text...");
		
		// Generates font and on screen text
		FontType font = new FontType(loader, "ubuntu");
		texts.put("FlightData", new GUIText("", 0.85f, font, new Vector2f(0, 0), 1f, true));
		texts.put("Paused", new GUIText("PAUSED", 1.15f, font, new Vector2f(0.5f, 0.5f), 1f, false, new Vector3f(1,0,0)));
		
		//==================================== Audio =========================================================
		
		SoundCollection.initializeSounds(configuration);
	}
	
	@Override
	public synchronized float getTerrainHeight() {
		if (running) {
			TreeMap<String, Terrain> terrainTree = terrainCollection.getTerrainTree();
			Vector3f position = ownship.getPosition();
			// Terrain object ownship is currently on
			Terrain currentTerrain = Terrain.getCurrentTerrain(terrainTree, position.x, position.z);
			// If outside world bounds, return 0 as terrain height
			return (currentTerrain == null) ? 0.0f : currentTerrain.getTerrainHeight(position.x, position.z);
		} else { 
			return 0.0f; 
		}
	}
	
	//=============================== Synchronization ======================================================
	
	/**
	 * @return If out the window display is running
	 */
	@Override
	public synchronized boolean isRunning() {return running;}
	
	/**
	 * Sets running boolean in {@link LWJGLWorld} to false to begin the display clean up process
	 */
	public synchronized void requestClose() {running = false;}
	
	//===================================== Text ============================================================
	
	/**
	 * Prepares a string of flight data that is output on the OTW using the {@link GUIText} object
	 * 
	 * @param receivedFlightData
	 * @return string displaying flight data output 
	 */
	private String setTextInfo(Map<FlightDataType, Double> receivedFlightData) {
		DecimalFormat df4 = new DecimalFormat("0.0000");
		DecimalFormat df2 = new DecimalFormat("0.00");
		DecimalFormat df0 = new DecimalFormat("0");
		
		StringBuffer sb = new StringBuffer();

		try {
			sb.append("AIRSPEED: ").append(df0.format(receivedFlightData.get(FlightDataType.IAS))).append(" KIAS | ")
			  .append("HEADING: ").append(df0.format(receivedFlightData.get(FlightDataType.HEADING))).append(" DEG | ")
			  .append("ALTITUDE: ").append(df0.format(receivedFlightData.get(FlightDataType.ALTITUDE))).append(" FT | ")
			  .append("LATITUDE: ").append(df4.format(receivedFlightData.get(FlightDataType.LATITUDE))).append(" DEG | ")
			  .append("LONGITUDE: ").append(df4.format(receivedFlightData.get(FlightDataType.LONGITUDE))).append(" DEG | ")
			  .append("G-FORCE: ").append(df2.format(receivedFlightData.get(FlightDataType.GFORCE))).append(" G ");
		} catch (Exception e) {
			sb.append("AIRSPEED: ").append("---").append(" KIAS | ")
			  .append("HEADING: ").append("---").append(" DEG | ")
			  .append("ALTITUDE: ").append("---").append(" FT | ")
			  .append("LATITUDE: ").append("--.----").append(" DEG | ")
			  .append("LONGITUDE: ").append("--.----").append(" DEG | ")
			  .append("G-FORCE: ").append("-.--").append(" G ");
		}
		
		return sb.toString();
	}

	@Override
	public void onFlightDataReceived(FlightData flightData) {
		
		Map<FlightDataType, Double> receivedFlightData = flightData.getFlightData();
		
		if (!receivedFlightData.containsValue(null) && (ownshipPosition != null || ownshipRotation != null)
				&& receivedFlightData != null) {
			// Scale distances from simulation to OTW
			ownshipPosition.x = (float) (receivedFlightData.get(FlightDataType.NORTH)    / 15);
			ownshipPosition.y = (float) (receivedFlightData.get(FlightDataType.ALTITUDE) / 15);
			ownshipPosition.z = (float) (receivedFlightData.get(FlightDataType.EAST)     / 15);
			
			// Convert right-handed coordinates from simulation to left-handed coordinates of OTW
			ownshipRotation.x = (float) -(receivedFlightData.get(FlightDataType.ROLL));
			ownshipRotation.y = (float) -(receivedFlightData.get(FlightDataType.PITCH));
			ownshipRotation.z = (float)  (receivedFlightData.get(FlightDataType.HEADING)-270); 
			
			// Set values for each sound in the simulation that depends on flight data
			soundValues.put(SoundCategory.RPM_1, receivedFlightData.get(FlightDataType.RPM_1));
			soundValues.put(SoundCategory.RPM_2, receivedFlightData.get(FlightDataType.RPM_2));
			soundValues.put(SoundCategory.RPM_3, receivedFlightData.get(FlightDataType.RPM_3));
			soundValues.put(SoundCategory.RPM_4, receivedFlightData.get(FlightDataType.RPM_4));
			soundValues.put(SoundCategory.WIND, receivedFlightData.get(FlightDataType.TAS));
			soundValues.put(SoundCategory.FLAPS, receivedFlightData.get(FlightDataType.FLAPS));
			soundValues.put(SoundCategory.GEAR, receivedFlightData.get(FlightDataType.GEAR));
			soundValues.put(SoundCategory.STALL_HORN, receivedFlightData.get(FlightDataType.AOA));
			
			// Record flight data into text string to display on OTW screen 
			if (!configuration.getSimulationOptions().contains(Options.INSTRUMENT_PANEL) && texts.get("FlightData") != null)
				texts.get("FlightData").setTextString(setTextInfo(receivedFlightData));
			
			// Record value every other step to ensure a difference between previous and current values; used to 
			// trigger flaps and gear sounds
			if (recordPrev) { 
				soundValues.put(SoundCategory.PREV_STEP_FLAPS, receivedFlightData.get(FlightDataType.FLAPS));
				soundValues.put(SoundCategory.PREV_STEP_GEAR, receivedFlightData.get(FlightDataType.GEAR));
			} recordPrev ^= true; 
		}
	}
}
