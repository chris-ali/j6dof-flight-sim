package com.chrisali.javaflightsim.otw;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.chrisali.javaflightsim.instrumentpanel.flightdata.FlightData;
import com.chrisali.javaflightsim.instrumentpanel.flightdata.FlightDataListener;
import com.chrisali.javaflightsim.instrumentpanel.flightdata.FlightDataType;
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
import com.chrisali.javaflightsim.otw.terrain.TerrainCollection;
import com.chrisali.javaflightsim.otw.textures.ModelTexture;

public class RunWorld implements Runnable, FlightDataListener {
	
	private Loader loader;
	private MasterRenderer masterRenderer;
	private List<Light> lights;
	
	private TerrainCollection terrainCollection;
	private EntityCollections entities;
	
	private Camera camera;
	private Ownship ownship;
	private Vector3f ownshipPosition;
	private Vector3f ownshipRotation;
	
	private GUIText text;
	
	public static void main(String[] args) {
		new RunWorld().run();
	}

	public RunWorld() {
		
		//=================================== Set Up ==========================================================
		
		DisplayManager.createDisplay();
		DisplayManager.setHeight(900);
		DisplayManager.setWidth(1440);
		
		masterRenderer = new MasterRenderer();
		MasterRenderer.setSkyColor(new Vector3f(0.0f, 0.75f, 0.95f));
		MasterRenderer.setFogDensity(0.0015f);
		MasterRenderer.setFogGradient(1.5f);
		
		loader = new Loader();
		
		ParticleMaster.init(loader, masterRenderer.getProjectionMatrix());
		TextMaster.init(loader);
		
		//==================================== Sun ===========================================================
		
		lights = new ArrayList<>();
		lights.add(new Light(new Vector3f(20000, 40000, 20000), new Vector3f(0.8f, 0.8f, 0.8f)));
		
		//================================= Terrain ==========================================================
		
		terrainCollection = new TerrainCollection(4, loader);
		
		//================================= Entities ==========================================================
		
		entities = new EntityCollections(lights, terrainCollection.getTerrainArray(), loader);
		entities.createRandomStaticEntities();
		
		//================================= Ownship ===========================================================
		
		TexturedModel bunny =  new TexturedModel(OBJLoader.loadObjModel("bunny", "Entities", loader), 
			    								new ModelTexture(loader.loadTexture("bunny", "Entities")));
		
		ownshipPosition = new Vector3f(0, 0, 0);
		ownshipRotation = new Vector3f(0, 0, 0);
		ownship = new Ownship(bunny, ownshipPosition, ownshipRotation.x, ownshipRotation.y, ownshipRotation.z, 0.5f);
		
		entities.addToStaticEntities(ownship);
		
		camera = new Camera(ownship);
		camera.setChaseView(false);
		camera.setPilotPosition(new Vector3f(0, 0, 0));
		
		//=============================== Particles ==========================================================
		
		ParticleTexture clouds = new ParticleTexture(loader.loadTexture("clouds", "Particles"), 4, true);
		
		Random random = new Random();
		for (int i = 0; i < 1000; i++)
			new Cloud(clouds, new Vector3f(random.nextInt(800*5), 200, i*5), new Vector3f(0, 0, 0), 0, 200);
		
		//=============================== Interface ==========================================================
		
		FontType font = new FontType(loader.loadTexture("arial", "Fonts"), new File("Resources\\Fonts\\arial.fnt"));
		text = new GUIText("", 1, font, new Vector2f(0, 0), 1f, true);
	}	
	
	@Override
	public void run() {

		//=============================== Main Loop ==========================================================

		while (!Display.isCloseRequested()) {
			//--------- Movement ----------------
			camera.move();
			ownship.move(ownshipPosition, ownshipRotation.x, ownshipRotation.y, ownshipRotation.z);
			
			//--------- Particles ---------------
			ParticleMaster.update(camera);
			
			//----------- UI --------------------
			text.setTextString(String.valueOf(ownship.getPosition().y));
			TextMaster.loadText(text);

			//------ Render Everything -----------
			masterRenderer.renderWholeScene(entities, terrainCollection.getTerrainArray(), 
											lights, camera, new Vector4f(0, 1, 0, 0));
			ParticleMaster.renderParticles(camera);
			TextMaster.render();
			
			DisplayManager.updateDisplay();
			
			try {Thread.sleep(5);} 
			catch (InterruptedException e) {}
		}
		
		//================================ Clean Up ==========================================================
		
		ParticleMaster.cleanUp();
		TextMaster.cleanUp();
		masterRenderer.cleanUp();
		loader.cleanUp();
		
		DisplayManager.closeDisplay();
	}

	@Override
	public void onFlightDataReceived(FlightData flightData) {
		ownshipPosition.x = (float) Double.parseDouble(flightData.getFlightData().get(FlightDataType.NORTH).toString());
		ownshipPosition.y = (float) Double.parseDouble(flightData.getFlightData().get(FlightDataType.ALTITUDE).toString());
		ownshipPosition.z = (float) Double.parseDouble(flightData.getFlightData().get(FlightDataType.EAST).toString());
		
		ownshipRotation.x = (float) Double.parseDouble(flightData.getFlightData().get(FlightDataType.ROLL).toString());
		ownshipRotation.y = (float) Double.parseDouble(flightData.getFlightData().get(FlightDataType.HEADING).toString());
		ownshipRotation.z = (float) Double.parseDouble(flightData.getFlightData().get(FlightDataType.PITCH).toString());
	}
}
