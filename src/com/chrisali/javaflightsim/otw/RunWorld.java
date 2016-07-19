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
import com.chrisali.javaflightsim.menus.optionspanel.AudioOptions;
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
	
	// Sound Fields
	private Map<AudioOptions, Float> audioOptions;
	private Map<SoundCategory, Double> soundValues = new EnumMap<>(SoundCategory.class);
	private boolean recordPrev = true; // Used in FlightDataListener to record soundValues data to PREV_STEP_* enums
	private AircraftBuilder ab;
	
	// Collections for in-game objects
	private TerrainCollection terrainCollection;
	private EntityCollections entities;
	
	// Ownship is the "player" that moves around the world based on data received from FlightData
	private Ownship ownship;
	private Vector3f ownshipPosition;
	private Vector3f ownshipRotation;
	private Camera camera;
	
	private GUIText text;
	
	private static boolean running = false;
	
	/**
	 * Sets up OTW display with {@link DisplayOptions} and {@link AudioOptions}, as well as a link to
	 * {@link AircraftBuilder} to determine if multiple engines in aircraft
	 * 
	 * @param displayOptions
	 * @param audioOptions
	 * @param ab
	 */
	public RunWorld(Map<DisplayOptions, Integer> displayOptions, 
					Map<AudioOptions, Float> audioOptions, AircraftBuilder ab) {
		this.displayOptions = displayOptions;
		this.audioOptions = audioOptions;
		this.ab = ab;
	}	
	
	@Override
	public void run() {
		
		//=================================== Set Up ==========================================================
		
		// Initializes display window
		DisplayManager.createDisplay();
		DisplayManager.setHeight(displayOptions.get(DisplayOptions.DISPLAY_HEIGHT));
		DisplayManager.setWidth(displayOptions.get(DisplayOptions.DISPLAY_WIDTH));
		
		loader = new Loader();
		
		// Sets up renderer with fog and sky config
		masterRenderer = new MasterRenderer();
		MasterRenderer.setSkyColor(new Vector3f(0.70f, 0.90f, 1.0f));
		MasterRenderer.setFogDensity(0.0005f);
		MasterRenderer.setFogGradient(3.5f);
		
		// Initialize sounds and position of listener
		AudioMaster.init();
		AudioMaster.setListenerData(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));
		
		// Load particles and on-screen text
		ParticleMaster.init(loader, masterRenderer.getProjectionMatrix());
		TextMaster.init(loader);
		
		// Load all entities (lights, entities, particles, etc)
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
			masterRenderer.renderWholeScene(entities, terrainCollection.getTerrainMap(), 
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
		
		entities = new EntityCollections(lights, terrainCollection.getTerrainMap(), loader);
		//entities.createAutogenImageEntities("autogen", "Terrain");
		//entities.createRandomStaticEntities();
		
		//================================= Ownship ===========================================================
		
		// Model used for aircraft; scale set to 0 to be invisible for now
		TexturedModel bunny =  new TexturedModel(OBJLoader.loadObjModel("bunny", "Entities", loader), 
			    								new ModelTexture(loader.loadTexture("bunny", "Entities")));
		// Initial position of ownship
		ownshipPosition = new Vector3f(800, 150, 800);
		ownshipRotation = new Vector3f(0, 0, 135);
		ownship = new Ownship(bunny, ownshipPosition, ownshipRotation.z, ownshipRotation.z, ownshipRotation.x, 0.000f);
		
		entities.addToStaticEntities(ownship);
		
		// Camera tied to ownship as first person view
		camera = new Camera(ownship);
		camera.setChaseView(false);
		camera.setPilotPosition(new Vector3f(0, 0, 0));
		
		//=============================== Particles ==========================================================
		
		ParticleTexture clouds = new ParticleTexture(loader.loadTexture("clouds", "Particles"), 4, true);
		
		// Generates clouds at random positions along terrain map
		Random random = new Random();
		for (int i = 0; i < 2000; i++)
			new Cloud(clouds, new Vector3f(random.nextInt(800*10), 300, i*10), new Vector3f(0, 0, 0), 0, 200);
		
		//=============================== Interface ==========================================================
		
		// Generates font and on screen text
		FontType font = new FontType(loader.loadTexture("arial", "Fonts"), new File("Resources\\Fonts\\arial.fnt"));
		text = new GUIText("", 1, font, new Vector2f(0, 0), 1f, true);
		
		//==================================== Audio =========================================================
		
		SoundCollection.initializeSounds(ab, audioOptions);
	}
	
	/**
	 * @return Height of terrain at the ownship's current position
	 */
	public synchronized float getTerrainHeight() {
		if (running) {
			Map<String, Terrain> terrainMap = terrainCollection.getTerrainMap();
			Vector3f position = ownship.getPosition();
			// Terrain object ownship is currently on
			Terrain currentTerrain = Terrain.getCurrentTerrain(terrainMap, position.x, position.z);
			// If outside world bounds, return 0 as terrain height
			return (currentTerrain == null) ? 0.0f : currentTerrain.getTerrainHeight(position.x, position.z);
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
			// Scale distances from simulation to OTW
			ownshipPosition.x = (float)  ((receivedFlightData.get(FlightDataType.NORTH)+800)/15);
			ownshipPosition.y = (float)   (receivedFlightData.get(FlightDataType.ALTITUDE)  /15);
			ownshipPosition.z = (float)  ((receivedFlightData.get(FlightDataType.EAST)+800) /15);
			
			// Convert right-handed coordinates from simulation to left-handed coordinates of OTW
			ownshipRotation.x = (float)  -(receivedFlightData.get(FlightDataType.ROLL));
			ownshipRotation.y = (float)  -(receivedFlightData.get(FlightDataType.PITCH));
			ownshipRotation.z = (float)   (receivedFlightData.get(FlightDataType.HEADING)-270); 
			
			soundValues.put(SoundCategory.RPM_1, receivedFlightData.get(FlightDataType.RPM_1));
			soundValues.put(SoundCategory.RPM_2, receivedFlightData.get(FlightDataType.RPM_2));
			soundValues.put(SoundCategory.RPM_3, receivedFlightData.get(FlightDataType.RPM_3));
			soundValues.put(SoundCategory.RPM_4, receivedFlightData.get(FlightDataType.RPM_4));
			soundValues.put(SoundCategory.WIND, receivedFlightData.get(FlightDataType.TAS));
			soundValues.put(SoundCategory.FLAPS, receivedFlightData.get(FlightDataType.FLAPS));
			soundValues.put(SoundCategory.GEAR, receivedFlightData.get(FlightDataType.GEAR));
			soundValues.put(SoundCategory.STALL_HORN, receivedFlightData.get(FlightDataType.AOA));
			
			// Record value every other step to ensure a difference between previous and current values; used to 
			// trigger flaps and gear sounds
			if (recordPrev) { 
				soundValues.put(SoundCategory.PREV_STEP_FLAPS, receivedFlightData.get(FlightDataType.FLAPS));
				soundValues.put(SoundCategory.PREV_STEP_GEAR, receivedFlightData.get(FlightDataType.GEAR));
			} recordPrev ^= true; 
		}
	}
}
