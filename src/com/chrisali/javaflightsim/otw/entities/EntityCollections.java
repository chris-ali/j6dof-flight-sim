package com.chrisali.javaflightsim.otw.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

import com.chrisali.javaflightsim.otw.models.TexturedModel;
import com.chrisali.javaflightsim.otw.renderengine.Loader;
import com.chrisali.javaflightsim.otw.renderengine.OBJLoader;
import com.chrisali.javaflightsim.otw.terrain.Terrain;
import com.chrisali.javaflightsim.otw.textures.ModelTexture;

/**
 * Randomly generates numerous objects in the world, spawning them to a list of entities
 * rendered by OpenGL; Static methods are also provided to allow the user to add custom entities in
 * specific locations to these lists
 * 
 * @author Christopher Ali
 *
 */
public class EntityCollections {
	
	private List<Entity> staticEntities = new ArrayList<>();
	private List<Entity> litEntities = new ArrayList<>();
	private List<Light> lights;
	
	private Terrain[][] terrainArray;
	private Loader loader;
	
	public EntityCollections(List<Light> lights, Terrain[][] terrainArray, Loader loader) {
		this.terrainArray = terrainArray;
		this.loader = loader;
		this.lights = lights;
	}
	
	/**
	 * Creates numerous tree {@link Entity} objects to populate the world
	 */
	public void createRandomStaticEntities() {
		
		TexturedModel planatusforest = new TexturedModel(OBJLoader.loadObjModel("grassModel", "Entities", loader), 
											new ModelTexture(loader.loadTexture("platanusforest", "Entities")));
		TexturedModel pineforest = new TexturedModel(OBJLoader.loadObjModel("grassModel", "Entities", loader), 
											new ModelTexture(loader.loadTexture("pineforest", "Entities")));
		TexturedModel oakforest = new TexturedModel(OBJLoader.loadObjModel("grassModel", "Entities", loader), 
											new ModelTexture(loader.loadTexture("oakforest", "Entities")));
		
		planatusforest.getTexture().setHasTransparency(true);
		planatusforest.getTexture().setUseFakeLighting(true);
		pineforest.getTexture().setHasTransparency(true);
		pineforest.getTexture().setUseFakeLighting(true);
		oakforest.getTexture().setHasTransparency(true);
		oakforest.getTexture().setUseFakeLighting(true);
		
		Random random = new Random();
		for (int i=0; i<9600; i++) {
			float x, y, z;
			
			if (i % 7 == 0) {
				x = random.nextFloat() * Terrain.getSize()*terrainArray.length;
				z = random.nextFloat() * Terrain.getSize()*terrainArray.length;
				y = Terrain.getCurrentTerrain(terrainArray, x, z).getTerrainHeight(x, z);
				
				staticEntities.add(new Entity(pineforest, new Vector3f(x, y-2, z), 
					0, random.nextFloat()*360, 0, random.nextFloat() + 6));
			}
			
			if (i % 5 == 0) {
				x = random.nextFloat() * Terrain.getSize()*terrainArray.length;
				z = random.nextFloat() * Terrain.getSize()*terrainArray.length;
				y = Terrain.getCurrentTerrain(terrainArray, x, z).getTerrainHeight(x, z);
				
				staticEntities.add(new Entity(oakforest, new Vector3f(x, y-2, z), 
					0, random.nextFloat()*360, 0, random.nextFloat()* 1 + 5));
			}
			
			if (i % 8 == 0) {
				x = random.nextFloat() * Terrain.getSize()*terrainArray.length;
				z = random.nextFloat() * Terrain.getSize()*terrainArray.length;
				y = Terrain.getCurrentTerrain(terrainArray, x, z).getTerrainHeight(x, z);
				
				staticEntities.add(new Entity(oakforest, new Vector3f(x, y-2, z), 
					0, random.nextFloat()*360, 0, random.nextFloat()* 1 + 5));
			}
		}
	}
	
	/**
	 *  Generates lit {@link Entity} objects along with {@link Light} objects in various locations  
	 */
	public void createRandomLitEntities() {
		float x, y, z;
		TexturedModel lamp =  new TexturedModel(OBJLoader.loadObjModel("lamp", "Entities", loader), 
						      new ModelTexture(loader.loadTexture("lamp", "Entities")));
		
		lamp.getTexture().setUseFakeLighting(true);
		
		x = 185;
		z = 293;	
		y = Terrain.getCurrentTerrain(terrainArray, x, z).getTerrainHeight(x, z);
		
		litEntities.add(new Entity(lamp, new Vector3f(x, y, z), 0, 0, 0, 1));
		lights.add(new Light(new Vector3f(x, y + 15, z), new Vector3f(0, 2, 2), new Vector3f(1, 0.01f, 0.002f)));
		
		x = 370;
		z = 300;	
		y = Terrain.getCurrentTerrain(terrainArray, x, z).getTerrainHeight(x, z);
		
		litEntities.add(new Entity(lamp, new Vector3f(x, y, z), 0, 0, 0, 1));
		lights.add(new Light(new Vector3f(x, y + 15, z), new Vector3f(2, 0, 0), new Vector3f(1, 0.01f, 0.002f)));
		
		x = 100;
		z = 200;	
		y = Terrain.getCurrentTerrain(terrainArray, x, z).getTerrainHeight(x, z);
		
		litEntities.add(new Entity(lamp, new Vector3f(x, y, z), 0, 0, 0, 1));
		lights.add(new Light(new Vector3f(x, y + 15, z), new Vector3f(2, 2, 0), new Vector3f(1, 0.01f, 0.002f)));
	}
	
	
	/**
	 * Creates a single static entity based on the position vector specified
	 * 
	 * @param entityName
	 * @param position
	 * @param xRot
	 * @param yRot
	 * @param zRot
	 * @param scale
	 */
	public void createStaticEntity(String entityName, Vector3f position, float xRot, float yRot, float zRot, float scale) {
		TexturedModel staticEntity =  new TexturedModel(OBJLoader.loadObjModel(entityName, "Entities", loader), 
														new ModelTexture(loader.loadTexture(entityName, "Entities")));
		
		staticEntities.add(new Entity(staticEntity, position, xRot, yRot, zRot, scale));
	}
	
	/**
	 * Creates a single static entity based on the X and Z coordinates specified; the Y coordinate is the height of the terrain
	 * at the given X and Z coordinates
	 * 
	 * @param entityName
	 * @param xPos
	 * @param zPos
	 * @param yRot
	 * @param scale
	 */
	public void createStaticEntity(String entityName, float xPos, float zPos, float yRot, float scale) {
		TexturedModel staticEntity =  new TexturedModel(OBJLoader.loadObjModel(entityName, "Entities", loader), 
														new ModelTexture(loader.loadTexture(entityName, "Entities")));
		float yPos = Terrain.getCurrentTerrain(terrainArray, xPos, zPos).getTerrainHeight(xPos, zPos);
		Vector3f position = new Vector3f(xPos, yPos, zPos);
		
		staticEntities.add(new Entity(staticEntity, position, 0, yRot, 0, scale));
	}
	
	/**
	 * Creates a single static entity based on the player's position
	 * 
	 * @param entityName
	 * @param player
	 * @param scale
	 */
	public void createStaticEntity(String entityName, Player player, float scale) {
		TexturedModel staticEntity =  new TexturedModel(OBJLoader.loadObjModel(entityName, "Entities", loader), 
														new ModelTexture(loader.loadTexture(entityName, "Entities")));

		staticEntities.add(new Entity(staticEntity, player.getPosition(), player.getRotX(), player.getRotY(), player.getRotZ(), scale));
	}
	
	/**
	 * <p>Creates a single lit entity based on the position vector specified. 
	 * <p>lightPosOffset refers to the position offset from the entity's position that the light is centered at 
	 * (e.g. a lamp post whose light is far above the post's position) </p>
	 * 
	 * @param entityName
	 * @param position
	 * @param xRot
	 * @param yRot
	 * @param zRot
	 * @param scale
	 * @param color
	 * @param attenuation
	 * @param lightPosOffset
	 */
	public void createLitEntity(String entityName, Vector3f position, float xRot, float yRot, float zRot, float scale, 
								 Vector3f color, Vector3f attenuation, Vector3f lightPosOffset) {
		TexturedModel litEntity =  new TexturedModel(OBJLoader.loadObjModel(entityName, "Entities", loader), 
													 new ModelTexture(loader.loadTexture(entityName, "Entities")));
		
		litEntities.add(new Entity(litEntity, position, xRot, yRot, zRot, scale));
		
		Light light = new Light(Vector3f.add(position, lightPosOffset, position), color, attenuation);
		lights.add(light);
	}
	
	/**
	 * <p>Creates a single lit entity based on the X and Z coordinates specified; the Y coordinate is the height of the terrain
	 * at the given X and Z coordinates; </p> 
	 *<p>lightPosOffset refers to the position offset 
	 * from the entity's position that the light is centered at (e.g. a lamp post whose light is far above the post's position);
	 * attenuation is the brightness of the light </p>
	 * 
	 * @param entityName
	 * @param xPos
	 * @param zPos
	 * @param yRot
	 * @param scale
	 * @param color
	 * @param attenuation
	 * @param lightPosOffset
	 */
	public void createLitEntity(String entityName, float xPos, float zPos, float yRot, float scale, 
								 Vector3f color, Vector3f attenuation, Vector3f lightPosOffset) {
		TexturedModel litEntity =  new TexturedModel(OBJLoader.loadObjModel(entityName, "Entities", loader), 
													 new ModelTexture(loader.loadTexture(entityName, "Entities")));
		float yPos = Terrain.getCurrentTerrain(terrainArray, xPos, zPos).getTerrainHeight(xPos, zPos);
		Vector3f position = new Vector3f(xPos, yPos, zPos);
		
		litEntities.add(new Entity(litEntity, position, 0, yRot, 0, scale));
		
		Light light = new Light(Vector3f.add(position, lightPosOffset, position), color, attenuation);
		lights.add(light);
	}
	
	/**
	 * <p>Creates a single static entity based on the player's position</p>
	 * <p>lightPosOffset refers to the position offset 
	 * from the entity's position that the light is centered at (e.g. a lamp post whose light is far above the post's position);
	 * attenuation is the brightness of the light </p>
	 * 
	 * @param entityName
	 * @param player
	 * @param scale
	 * @param color
	 * @param attenuation
	 * @param lightPosOffset
	 */
	public void createLitEntity(String entityName, Player player, float scale, 
								Vector3f color, Vector3f attenuation, Vector3f lightPosOffset) {
		TexturedModel litEntity =  new TexturedModel(OBJLoader.loadObjModel(entityName, "Entities", loader), 
													 new ModelTexture(loader.loadTexture(entityName, "Entities")));
		Vector3f position = player.getPosition();
		
		litEntities.add(new Entity(litEntity, position, player.getRotX(), player.getRotY(), player.getRotZ(), scale));
		
		Light light = new Light(Vector3f.add(position, lightPosOffset, position), color, attenuation);
		lights.add(light);
	}
	
	public void addToStaticEntities(Entity entity) {
		staticEntities.add(entity);
	}
	
	public List<Entity> getLitEntities() {
		return litEntities;
	}

	public List<Entity> getStaticEntities() {
		return staticEntities;
	}
}
