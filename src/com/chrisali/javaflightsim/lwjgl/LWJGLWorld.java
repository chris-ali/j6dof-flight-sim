/*******************************************************************************
 * Copyright (C) 2016-2020 Christopher Ali
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import static org.lwjgl.glfw.GLFW.*;

import com.chrisali.javaflightsim.interfaces.OTWWorld;
import com.chrisali.javaflightsim.interfaces.SimulationController;
import com.chrisali.javaflightsim.lwjgl.audio.AudioMaster;
import com.chrisali.javaflightsim.lwjgl.audio.SoundCollection;
import com.chrisali.javaflightsim.lwjgl.entities.Camera;
import com.chrisali.javaflightsim.lwjgl.entities.EntityCollections;
import com.chrisali.javaflightsim.lwjgl.entities.Light;
import com.chrisali.javaflightsim.lwjgl.entities.Ownship;
import com.chrisali.javaflightsim.lwjgl.events.WindowClosedListener;
import com.chrisali.javaflightsim.lwjgl.input.InputMaster;
import com.chrisali.javaflightsim.lwjgl.interfaces.gauges.InstrumentPanel;
import com.chrisali.javaflightsim.lwjgl.interfaces.text.FontType;
import com.chrisali.javaflightsim.lwjgl.interfaces.text.SimulationTexts;
import com.chrisali.javaflightsim.lwjgl.interfaces.text.TextMaster;
import com.chrisali.javaflightsim.lwjgl.interfaces.ui.InterfaceTexture;
import com.chrisali.javaflightsim.lwjgl.loader.Loader;
import com.chrisali.javaflightsim.lwjgl.loader.OBJLoader;
import com.chrisali.javaflightsim.lwjgl.models.TexturedModel;
import com.chrisali.javaflightsim.lwjgl.particles.Cloud;
import com.chrisali.javaflightsim.lwjgl.particles.ParticleMaster;
import com.chrisali.javaflightsim.lwjgl.particles.ParticleTexture;
import com.chrisali.javaflightsim.lwjgl.renderengine.DisplayManager;
import com.chrisali.javaflightsim.lwjgl.renderengine.InterfaceRenderer;
import com.chrisali.javaflightsim.lwjgl.renderengine.MasterRenderer;
import com.chrisali.javaflightsim.lwjgl.terrain.TerrainCollection;
import com.chrisali.javaflightsim.lwjgl.textures.ModelTexture;
import com.chrisali.javaflightsim.lwjgl.utilities.OTWDirectories;
import com.chrisali.javaflightsim.simulation.SimulationRunner;
import com.chrisali.javaflightsim.simulation.datatransfer.EnvironmentData;
import com.chrisali.javaflightsim.simulation.datatransfer.EnvironmentDataListener;
import com.chrisali.javaflightsim.simulation.datatransfer.FlightData;
import com.chrisali.javaflightsim.simulation.datatransfer.FlightDataListener;
import com.chrisali.javaflightsim.simulation.datatransfer.FlightDataType;
import com.chrisali.javaflightsim.simulation.datatransfer.InputDataListener;
import com.chrisali.javaflightsim.simulation.setup.CameraMode;
import com.chrisali.javaflightsim.simulation.setup.KeyCommand;
import com.chrisali.javaflightsim.simulation.setup.SimulationConfiguration;
import com.chrisali.javaflightsim.simulation.utilities.FileUtilities;

/**
 * Out the window display for Java Flight Sim. It utilizes LWJGL to create a 3D world in OpenGL. 
 * This runs in a simulation runner thread that receives data from {@link FlightData} via {@link FlightDataListener} 
 * 
 * @author Christopher Ali
 *
 */
public class LWJGLWorld implements FlightDataListener, OTWWorld {
	
	private static final Logger logger = LogManager.getLogger(LWJGLWorld.class);
	
	private Loader loader;
	private MasterRenderer masterRenderer;
	
	// Lighting
	private List<Light> lights;
	
	// Collections for in-game objects
	private TerrainCollection terrainCollection;
	private EntityCollections entities;
	private SoundCollection soundCollection;
	
	// Ownship is the "player" that moves around the world based on data received from FlightData
	private Ownship ownship;
	private Camera camera;
	
	// Interface
	private SimulationTexts simTexts;
	private Map<String, List<InterfaceTexture>> interfaceTextures;
	private InterfaceRenderer interfaceRenderer;
	private InstrumentPanel panel;
	
	// Fields that interface with the simulation
	private SimulationConfiguration configuration;
	private EnvironmentData environmentData;
	
	// Event Listeners
	private List<WindowClosedListener> windowClosedListeners = new ArrayList<>();
	private List<InputDataListener> inputDataListeners = new ArrayList<>();
	private List<EnvironmentDataListener> environmentDataListeners = new ArrayList<>();
			
	/**
	 * Sets up OTW display with {@link SimulationConfiguration} provided by {@link SimulationController} 
	 * 
	 * @param controller
	 */
	public LWJGLWorld(SimulationController controller) {
		configuration = controller.getConfiguration();
	}	
	
	@Override
	public boolean canStepNow(int simTimeMS) {
		return simTimeMS % 1 == 0;
	}

	/**
	 * Main game loop of the LWJGL process, whose stepping is controlled by the {@link SimulationRunner} object's thread
	 */
	@Override
	public void step() {
		try {
			ParticleMaster.update(camera);
			
			masterRenderer.renderWholeScene(entities, terrainCollection.getTerrainTree(), 
											lights, camera, new Vector4f(0, 1, 0, 0));
			
			ParticleMaster.renderParticles(camera);
			
			interfaceRenderer.render(configuration, interfaceTextures);

			TextMaster.render(simTexts.getTexts());

			InputMaster.update();
			fireInputDataReceived();
						
			environmentData.updateData(terrainCollection.getTerrainHeight(ownship));
			fireEnvironmentDataReceived();
			
			DisplayManager.updateDisplay();
		} catch (Exception e) {
			logger.error("Error encountered while running LWJGL display!", e);
		}
		
		if(glfwWindowShouldClose(DisplayManager.getWindow()) || InputMaster.getInputData().getKeyCommands().contains(KeyCommand.EXIT_SIMULATION)) {
			cleanUp();
		}
	}
	
	/**
	 * Called just before main simulation loop runs, initializes all assets and processes
	 */
	@Override
	public void init() {
		try { 
			startUp(); 
			loadAssets(); 
		} catch (Exception e) {
			logger.fatal("Error encountered when setting up LWJGL display!", e);
			cleanUp();
		}
	}
	
	/**
	 * Closes all display and rendering processes, and closes display window 
	 */
	private void cleanUp() {
		fireWindowClosed();

		try {  
			logger.debug("Cleaning up and closing LWJGL display...");
			
			AudioMaster.cleanUp();
			ParticleMaster.cleanUp();
			TextMaster.cleanUp();
			masterRenderer.cleanUp();
			interfaceRenderer.cleanUp();
			loader.cleanUp();			
		}
		catch (NullPointerException e) {
			logger.warn("Unable to clean up LWJGL display. Assets may not have been initialized in the first place!");
		}
		catch (Exception e) {
			logger.fatal("Error encountered when cleaning up LWJGL display!", e);
		} finally {			
			DisplayManager.closeDisplay();
		}
	}
	
	/**
	 * Sets up all display and rendering processes and prepares them to run
	 */
	private void startUp() {
		logger.debug("Starting up LWJGL display...");
		DisplayManager.createDisplay();
		DisplayManager.setFrameRateLimit(configuration.getSimulationRateHz());
		DisplayManager.setHeight(configuration.getDisplayConfiguration().getDisplayHeight());
		DisplayManager.setWidth(configuration.getDisplayConfiguration().getDisplayWidth());
		DisplayManager.setAaSamples(configuration.getDisplayConfiguration().isUseAntiAliasing() ? 2 : 0);
		
		loader = new Loader();
		
		logger.debug("Generating fog and sky...");
		
		masterRenderer = new MasterRenderer();
		MasterRenderer.setSkyColor(new Vector3f(0.70f, 0.90f, 1.0f));
		MasterRenderer.setFogDensity(0.0005f);
		MasterRenderer.setFogGradient(3.5f);
		MasterRenderer.setFov(configuration.getCameraConfiguration().getFieldOfView());
		
		logger.debug("Initializing audio...");
		
		AudioMaster.init();
		AudioMaster.setListenerData(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));
		
		logger.debug("Loading on-screen text and particles...");
		
		ParticleMaster.init(loader, masterRenderer.getProjectionMatrix());
		TextMaster.init(loader);

		logger.debug("Initializing control inputs and environment data transfer...");
		
		InputMaster.init();
		environmentData = new EnvironmentData();
		
		interfaceRenderer = new InterfaceRenderer(loader);
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
		
		TexturedModel airplane =  new TexturedModel(OBJLoader.loadObjModel("airplane", OTWDirectories.ENTITIES.toString(), loader), 
			    								new ModelTexture(loader.loadTexture("airplane", OTWDirectories.ENTITIES.toString())));

		ownship = new Ownship(airplane, configuration.getInitialConditions(), 1.25f);
		ownship.setRender(configuration.getCameraConfiguration().getMode() == CameraMode.CHASE);
		entities.addToStaticEntities(ownship);
		
		logger.debug("Setting up camera...");
		
		camera = new Camera(ownship, configuration.getCameraConfiguration());
		inputDataListeners.add(camera);
				
		//================================= Terrain ==========================================================
		
		logger.debug("Generating terrain...");
		
		terrainCollection = new TerrainCollection(10, loader, ownship);
		entities.setTerrainTree(terrainCollection.getTerrainTree());
		
		//=============================== Particles ==========================================================
		
		logger.debug("Generating clouds...");
		
		ParticleTexture clouds = new ParticleTexture(loader.loadTexture("clouds", OTWDirectories.PARTICLES.toString()), 4, true);
		
		// Generates clouds at random positions along terrain map
		Random random = new Random();
		for (int i = -1000; i < 1000; i++)
			new Cloud(clouds, new Vector3f(random.nextInt(800*10), 300, i*10), new Vector3f(0, 0, 0), 0, 200);
		
		//=============================== Interface ==========================================================
		
		logger.debug("Generating on-screen text and panel...");
		
		// On-screen text
		simTexts = new SimulationTexts(new FontType(loader, "ubuntu"), configuration);
		inputDataListeners.add(simTexts);
		
		// Instrument Panel and Gauges
		interfaceTextures = new HashMap<String, List<InterfaceTexture>>();
		panel = FileUtilities.readInstrumentPanelConfiguration(configuration.getSelectedAircraft());
		interfaceTextures.put(InstrumentPanel.class.getSimpleName(), panel.loadAndGetTextures(loader, configuration.getSelectedAircraft()));

		//==================================== Audio =========================================================
		
		logger.debug("Generating sound collection...");
		
		soundCollection = new SoundCollection(configuration);
	}
	
	// =============================== Events =====================================
	
	@Override
	public void onFlightDataReceived(FlightData flightData) {
		Map<FlightDataType, Double> receivedFlightData = flightData.getFlightData();
		
		if (!receivedFlightData.containsValue(null) && receivedFlightData != null) {
			// Update sound gains/volumes with flight data
			soundCollection.update(receivedFlightData);
			
			// Ownship movement; let camera track ownhip 1-1 for now
			ownship.move(receivedFlightData);
			camera.move();

			// Record flight data into text string to display on OTW screen 
			simTexts.update(receivedFlightData, camera, ownship);
			
			// Instrument Panel
			panel.update(receivedFlightData);
		}
	}
	
	public void addWindowClosedListener(WindowClosedListener listener) {
		if (windowClosedListeners != null) {
			logger.debug("Adding window closed listener: " + listener.getClass());
			windowClosedListeners.add(listener);
		}
	}

	public void addinputDataListener(InputDataListener listener) {
		if (inputDataListeners != null) {
			logger.debug("Adding input data listener: " + listener.getClass());
			inputDataListeners.add(listener);
		}
	}

	public void addEnvironmentDataListener(EnvironmentDataListener listener) {
		if (environmentDataListeners != null) {
			logger.debug("Adding environment data listener: " + listener.getClass());
			environmentDataListeners.add(listener);
		}
	}
	
	private void fireWindowClosed() {
		for (WindowClosedListener listener : windowClosedListeners)
			listener.onWindowClosed();
	}

	private void fireInputDataReceived() {
		for (InputDataListener listener : inputDataListeners)
			listener.onInputDataReceived(InputMaster.getInputData());
	}

	private void fireEnvironmentDataReceived() {
		for (EnvironmentDataListener listener : environmentDataListeners)
			listener.onEnvironmentDataReceived(environmentData);
	}
}
