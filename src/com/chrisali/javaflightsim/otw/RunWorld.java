package com.chrisali.javaflightsim.otw;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.chrisali.javaflightsim.datatransfer.FlightData;
import com.chrisali.javaflightsim.datatransfer.FlightDataListener;
import com.chrisali.javaflightsim.datatransfer.FlightDataType;
import com.chrisali.javaflightsim.menus.optionspanel.DisplayOptions;
import com.chrisali.javaflightsim.otw.audio.AudioMaster;
import com.chrisali.javaflightsim.otw.audio.SoundCollection;
import com.chrisali.javaflightsim.otw.audio.SoundCollection.SoundCategory;
import com.chrisali.javaflightsim.otw.entities.Camera;
import com.chrisali.javaflightsim.otw.entities.EntityCollections;
import com.chrisali.javaflightsim.otw.entities.Light;
import com.chrisali.javaflightsim.otw.entities.Ownship;
import com.chrisali.javaflightsim.otw.interfaces.font.FontType;
import com.chrisali.javaflightsim.otw.interfaces.font.GUIText;
import com.chrisali.javaflightsim.otw.interfaces.font.TextMaster;
import com.chrisali.javaflightsim.otw.models.TexturedModel;
import com.chrisali.javaflightsim.otw.particles.Cloud;
import com.chrisali.javaflightsim.otw.particles.ParticleMaster;
import com.chrisali.javaflightsim.otw.particles.ParticleTexture;
import com.chrisali.javaflightsim.otw.renderengine.DisplayManager;
import com.chrisali.javaflightsim.otw.renderengine.Loader;
import com.chrisali.javaflightsim.otw.renderengine.MasterRenderer;
import com.chrisali.javaflightsim.otw.renderengine.OBJLoader;
import com.chrisali.javaflightsim.otw.terrain.Terrain;
import com.chrisali.javaflightsim.otw.terrain.TerrainCollection;
import com.chrisali.javaflightsim.otw.textures.ModelTexture;
import com.chrisali.javaflightsim.simulation.aircraft.AircraftBuilder;

/**
 * Runner class for out the window display for Java Flight Sim. It utilizes LWJGL to create a 3D world in OpenGL. 
 * The display runs in a separate thread that receives data from {@link FlightData} via {@link FlightDataListener} 
 * 
 * @author Christopher Ali
 *
 */
public class RunWorld implements Runnable, FlightDataListener {
	
	private Loader loader;
	private MasterRenderer masterRenderer;
	private List<Light> lights;
	private Map<DisplayOptions, Integer> displayOptions;
	
	private Map<SoundCategory, Double> soundValues = new EnumMap<>(SoundCategory.class);
	private boolean recordPrev = true; // Used in FlightDataListener to record soundValues data to PREV_STEP_* enums
	private AircraftBuilder ab;
	
	private TerrainCollection terrainCollection;
	private EntityCollections entities;
	
	// Ownship is the "player" that moves around the world based on data received from FlightData
	private Ownship ownship;
	private Vector3f ownshipPosition;
	private Vector3f ownshipRotation;
	private Camera camera;
	
	private GUIText text;
	
	private static boolean running = false;

	public RunWorld(Map<DisplayOptions, Integer> displayOptions, AircraftBuilder ab) {
		this.displayOptions = displayOptions;
		this.ab = ab;
	}	
	
	@Override
	public void run() {
		
		//=================================== Set Up ==========================================================
		
		DisplayManager.createDisplay();
		DisplayManager.setHeight(displayOptions.get(DisplayOptions.DISPLAY_HEIGHT));
		DisplayManager.setWidth(displayOptions.get(DisplayOptions.DISPLAY_WIDTH));
		
		loader = new Loader();
		
		masterRenderer = new MasterRenderer();
		MasterRenderer.setSkyColor(new Vector3f(0.70f, 0.90f, 1.0f));
		MasterRenderer.setFogDensity(0.0005f);
		MasterRenderer.setFogGradient(3.5f);
		
		AudioMaster.init();
		AudioMaster.setListenerData(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));
		
		ParticleMaster.init(loader, masterRenderer.getProjectionMatrix());
		TextMaster.init(loader);
		
		loadAssets();
		
		running = true;

		//=============================== Main Loop ==========================================================

		while (!Display.isCloseRequested()) {
			
			//--------- Movement ----------------
			camera.move(ownshipPosition, ownshipRotation.x, ownshipRotation.y, ownshipRotation.z);
			ownship.move(ownshipPosition, ownshipRotation.x, ownshipRotation.y, ownshipRotation.z);
			
			//--------- Particles ---------------
			ParticleMaster.update(camera);
			
			//--------- Audio--------------------
			SoundCollection.update(soundValues, ab);
			
			//----------- UI --------------------
			text.setTextString(String.valueOf(ownship.getPosition().y));
			TextMaster.loadText(text);

			//------ Render Everything -----------
			masterRenderer.renderWholeScene(entities, terrainCollection.getTerrainArray(), 
											lights, camera, new Vector4f(0, 1, 0, 0));
			ParticleMaster.renderParticles(camera);
			TextMaster.render();
			
			DisplayManager.updateDisplay();
		}
		
		running = false;
		
		//================================ Clean Up ==========================================================
		
		AudioMaster.cleanUp();
		ParticleMaster.cleanUp();
		TextMaster.cleanUp();
		masterRenderer.cleanUp();
		loader.cleanUp();
		
		DisplayManager.closeDisplay();
	}
	
	/**
	 * Initalizes and generates all assets needed to render lights, entities, particles terrain and text
	 */
	private void loadAssets() {
		
		//==================================== Sun ===========================================================
		
		lights = new ArrayList<>();
		lights.add(new Light(new Vector3f(20000, 40000, 20000), new Vector3f(0.95f, 0.95f, 0.95f)));
		
		//================================= Terrain ==========================================================
		
		terrainCollection = new TerrainCollection(6, loader);
		
		//================================= Entities ==========================================================
		
		entities = new EntityCollections(lights, terrainCollection.getTerrainArray(), loader);
		//entities.createAutogenImageEntities("autogen", "Terrain");
		//entities.createRandomStaticEntities();
		
		//================================= Ownship ===========================================================
		
		TexturedModel bunny =  new TexturedModel(OBJLoader.loadObjModel("bunny", "Entities", loader), 
			    								new ModelTexture(loader.loadTexture("bunny", "Entities")));
		
		ownshipPosition = new Vector3f(800, 150, 800);
		ownshipRotation = new Vector3f(0, 0, 135);
		ownship = new Ownship(bunny, ownshipPosition, ownshipRotation.z, ownshipRotation.z, ownshipRotation.x, 0.000f);
		
		entities.addToStaticEntities(ownship);
		
		camera = new Camera(ownship);
		camera.setChaseView(false);
		camera.setPilotPosition(new Vector3f(0, 0, 0));
		
		//=============================== Particles ==========================================================
		
		ParticleTexture clouds = new ParticleTexture(loader.loadTexture("clouds", "Particles"), 4, true);
		
		Random random = new Random();
		for (int i = 0; i < 2000; i++)
			new Cloud(clouds, new Vector3f(random.nextInt(800*10), 300, i*10), new Vector3f(0, 0, 0), 0, 200);
		
		//=============================== Interface ==========================================================
		
		FontType font = new FontType(loader.loadTexture("arial", "Fonts"), new File("Resources\\Fonts\\arial.fnt"));
		text = new GUIText("", 1, font, new Vector2f(0, 0), 1f, true);
		
		//==================================== Audio =========================================================
		
		SoundCollection.initializeSounds(ab);
		
	}
	
	/**
	 * @return Height of terrain at the ownship's current position
	 */
	public synchronized float getTerrainHeight() {
		if (running) {
			Terrain[][] terrainArray = terrainCollection.getTerrainArray();
			Vector3f position = ownship.getPosition();
			
			return Terrain.getCurrentTerrain(terrainArray, position.x, position.z)
									 		.getTerrainHeight(position.x, position.z);
		} else { 
			return 0.0f; 
		}
	}
	
	/**
	 * @return If out the window display is running
	 */
	public static synchronized boolean isRunning() {return running;}

	@Override
	public void onFlightDataReceived(FlightData flightData) {
		
		Map<FlightDataType, Double> receivedFlightData = flightData.getFlightData();
		
		if (!receivedFlightData.containsValue(null) && (ownshipPosition != null || ownshipRotation != null)) {
			ownshipPosition.x = (float)  ((receivedFlightData.get(FlightDataType.NORTH)+800)/15);
			ownshipPosition.y = (float)   (receivedFlightData.get(FlightDataType.ALTITUDE)  /15);
			ownshipPosition.z = (float)  ((receivedFlightData.get(FlightDataType.EAST)+800) /15);
		
			ownshipRotation.x = (float)  -(receivedFlightData.get(FlightDataType.ROLL));
			ownshipRotation.y = (float)  -(receivedFlightData.get(FlightDataType.PITCH));
			ownshipRotation.z = (float)   (receivedFlightData.get(FlightDataType.HEADING)-270); // rotate to follow camera translation
			
			soundValues.put(SoundCategory.RPM_1, receivedFlightData.get(FlightDataType.RPM_1));
			soundValues.put(SoundCategory.RPM_2, receivedFlightData.get(FlightDataType.RPM_2));
			soundValues.put(SoundCategory.RPM_3, receivedFlightData.get(FlightDataType.RPM_3));
			soundValues.put(SoundCategory.RPM_4, receivedFlightData.get(FlightDataType.RPM_4));
			soundValues.put(SoundCategory.WIND, receivedFlightData.get(FlightDataType.TAS));
			soundValues.put(SoundCategory.FLAPS, receivedFlightData.get(FlightDataType.FLAPS));
			soundValues.put(SoundCategory.GEAR, receivedFlightData.get(FlightDataType.GEAR));
			soundValues.put(SoundCategory.STALL_HORN, receivedFlightData.get(FlightDataType.AOA));
			
			if (recordPrev) { // record every other step to ensure a difference between previous and current values
				soundValues.put(SoundCategory.PREV_STEP_FLAPS, receivedFlightData.get(FlightDataType.FLAPS));
				soundValues.put(SoundCategory.PREV_STEP_GEAR, receivedFlightData.get(FlightDataType.GEAR));
			} recordPrev ^= true; 
		}
	}
}
