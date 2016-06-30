package com.chrisali.javaflightsim.otw.entities;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

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
	
	private Map<String, Terrain> terrainMap;
	private Loader loader;
	
	public EntityCollections(List<Light> lights, Map<String, Terrain> terrainMap, Loader loader) {
		this.terrainMap = terrainMap;
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
			
			int terrainMapWidth = (int)Math.sqrt(terrainMap.size());
			
			if (i % 7 == 0) {
				x = random.nextFloat() * Terrain.getSize()*terrainMapWidth;
				z = random.nextFloat() * Terrain.getSize()*terrainMapWidth;
				y = Terrain.getCurrentTerrain(terrainMap, x, z).getTerrainHeight(x, z);
				
				staticEntities.add(new Entity(pineforest, new Vector3f(x, y-2, z), 
					0, random.nextFloat()*360, 0, random.nextFloat() + 6));
			}
			
			if (i % 5 == 0) {
				x = random.nextFloat() * Terrain.getSize()*terrainMapWidth;
				z = random.nextFloat() * Terrain.getSize()*terrainMapWidth;
				y = Terrain.getCurrentTerrain(terrainMap, x, z).getTerrainHeight(x, z);
				
				staticEntities.add(new Entity(oakforest, new Vector3f(x, y-2, z), 
					0, random.nextFloat()*360, 0, random.nextFloat()* 1 + 5));
			}
			
			if (i % 8 == 0) {
				x = random.nextFloat() * Terrain.getSize()*terrainMapWidth;
				z = random.nextFloat() * Terrain.getSize()*terrainMapWidth;
				y = Terrain.getCurrentTerrain(terrainMap, x, z).getTerrainHeight(x, z);
				
				staticEntities.add(new Entity(oakforest, new Vector3f(x, y-2, z), 
					0, random.nextFloat()*360, 0, random.nextFloat()* 1 + 5));
			}
		}
	}
	
	/**
	 * Creates static entities from an autogen image file that maps specifically what type of entity should
	 * be generated at a given location. The image file should be filled in black with the exception of 
	 * red, green or blue pixels, which each determine buildings, trees and airports, respectively.
	 * 
	 * <p>Each pixel adds a new object, so they should be added to the autogen image (autogen.png) judiciously
	 * 
	 * @param fileName
	 * @param directory
	 */
	public void createAutogenImageEntities(String fileName, String directory) {
		
		BufferedImage image = null;
		
		try {image = ImageIO.read(new File("Resources\\" + directory + "\\" + fileName + ".png"));} 
		catch (IOException e) {System.err.println("Could not load autogen file: " + fileName + ".png");}
		
		float imageScale = Terrain.getSize()/image.getHeight();
		float scaledX, scaledZ;
		Color readColor;
		
		for (int x = 0; x < image.getWidth(); x+=16) {
			for (int z = 0; z < image.getHeight(); z+=16) {
				readColor = new Color(image.getRGB(x, z));
				scaledX = x * imageScale;
				scaledZ = z * imageScale;
				
				if(readColor.getRed() > 250) {
					// Create buildings here
				} else if(readColor.getGreen() > 250) {
					createRandomTrees(scaledX, scaledZ);
				} else if(readColor.getBlue() > 250) {
					// Create airport here
				}
			}
		}
	}
	
	/**
	 * Creates a random group of trees at the specified x and z position
	 * 
	 * @param x
	 * @param z
	 */
	public void createRandomTrees(float x, float z) {
//		TexturedModel planatusforest = new TexturedModel(OBJLoader.loadObjModel("grassModel", "Entities", loader), 
//											new ModelTexture(loader.loadTexture("platanusforest", "Entities")));
		TexturedModel pineforest = new TexturedModel(OBJLoader.loadObjModel("grassModel", "Entities", loader), 
											new ModelTexture(loader.loadTexture("pineforest", "Entities")));
//		TexturedModel oakforest = new TexturedModel(OBJLoader.loadObjModel("grassModel", "Entities", loader), 
//											new ModelTexture(loader.loadTexture("oakforest", "Entities")));
		
//		planatusforest.getTexture().setHasTransparency(true);
//		planatusforest.getTexture().setUseFakeLighting(true);
		pineforest.getTexture().setHasTransparency(true);
		pineforest.getTexture().setUseFakeLighting(true);
//		oakforest.getTexture().setHasTransparency(true);
//		oakforest.getTexture().setUseFakeLighting(true);
		
		Random random = new Random();
		float y = Terrain.getCurrentTerrain(terrainMap, x, z).getTerrainHeight(x, z);
		
		staticEntities.add(new Entity(pineforest, new Vector3f(x, y-2, z), 
				0, random.nextFloat()*360, 0, random.nextFloat() + 6));
		
		/*
		if (random.nextInt(100) % 3 == 0) {
			staticEntities.add(new Entity(pineforest, new Vector3f(x, y-2, z), 
				0, random.nextFloat()*360, 0, random.nextFloat() + 6));
		} else if (random.nextInt(100) % 9 == 0) {
			staticEntities.add(new Entity(oakforest, new Vector3f(x, y-2, z), 
				0, random.nextFloat()*360, 0, random.nextFloat()* 1 + 5));
		} else if (random.nextInt(100) % 10 == 0) {
			staticEntities.add(new Entity(oakforest, new Vector3f(x, y-2, z), 
				0, random.nextFloat()*360, 0, random.nextFloat()* 1 + 5));
		}
		*/
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
		y = Terrain.getCurrentTerrain(terrainMap, x, z).getTerrainHeight(x, z);
		
		litEntities.add(new Entity(lamp, new Vector3f(x, y, z), 0, 0, 0, 1));
		lights.add(new Light(new Vector3f(x, y + 15, z), new Vector3f(0, 2, 2), new Vector3f(1, 0.01f, 0.002f)));
		
		x = 370;
		z = 300;	
		y = Terrain.getCurrentTerrain(terrainMap, x, z).getTerrainHeight(x, z);
		
		litEntities.add(new Entity(lamp, new Vector3f(x, y, z), 0, 0, 0, 1));
		lights.add(new Light(new Vector3f(x, y + 15, z), new Vector3f(2, 0, 0), new Vector3f(1, 0.01f, 0.002f)));
		
		x = 100;
		z = 200;	
		y = Terrain.getCurrentTerrain(terrainMap, x, z).getTerrainHeight(x, z);
		
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
		float yPos = Terrain.getCurrentTerrain(terrainMap, xPos, zPos).getTerrainHeight(xPos, zPos);
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
		float yPos = Terrain.getCurrentTerrain(terrainMap, xPos, zPos).getTerrainHeight(xPos, zPos);
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
