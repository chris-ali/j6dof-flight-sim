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
package com.chrisali.javaflightsim.lwjgl.entities;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.util.vector.Vector3f;

import com.chrisali.javaflightsim.lwjgl.loader.Loader;
import com.chrisali.javaflightsim.lwjgl.loader.OBJLoader;
import com.chrisali.javaflightsim.lwjgl.models.TexturedModel;
import com.chrisali.javaflightsim.lwjgl.terrain.Terrain;
import com.chrisali.javaflightsim.lwjgl.textures.ModelTexture;
import com.chrisali.javaflightsim.lwjgl.utilities.OTWDirectories;
import com.chrisali.javaflightsim.lwjgl.utilities.OTWFiles;

/**
 * Contains methods to create {@link Entity} objects to world, which are rendered by OpenGL; these objects are
 * normally tied to a {@link Terrain} object and spawned using an autogen texture mapping scheme, but other objects can also
 * be generated at specific locations in the world. These are added instead to a local list inside of this class using 
 * provided static methods 
 * 
 * @author Christopher Ali
 *
 */
public class EntityCollections {
	
	//Logging
	private static final Logger logger = LogManager.getLogger(EntityCollections.class);
	
	private List<Entity> miscStaticEntities = new ArrayList<>();
	private List<Entity> miscLitEntities = new ArrayList<>();
	private List<Light> lights;
	
	private TreeMap<String, Terrain> terrainTree;
	private Loader loader;
	
	//==================== Models =======================
	// Static
	private static TexturedModel planatusForest;
	private static TexturedModel pineForest;
	private static TexturedModel oakForest;
	
	// Lit
	private static TexturedModel lamp;
	
	/**
	 * Creates {@link EntityCollections} object with list of lights, loader to load entities, and
	 * call to initialize all {@link Entity} objects
	 * 
	 * @param lights
	 * @param loader
	 */
	public EntityCollections(List<Light> lights, Loader loader) {
		this.loader = loader;
		this.lights = lights;
		
		initializeEntities();
	}
	
	public void setTerrainTree(TreeMap<String, Terrain> terrainTree) {
		this.terrainTree = terrainTree;
	}

	/**
	 * Initializes all {@link TexturedModel} objects for in methods, which create entities
	 */
	private void initializeEntities() {
		// Create models
		// Static
		planatusForest = new TexturedModel(OBJLoader.loadObjModel("grassModel", OTWDirectories.ENTITIES.toString(), loader), 
											new ModelTexture(loader.loadTexture("platanusforest", OTWDirectories.ENTITIES.toString())));
		pineForest = new TexturedModel(OBJLoader.loadObjModel("grassModel", OTWDirectories.ENTITIES.toString(), loader), 
											new ModelTexture(loader.loadTexture("pineforest", OTWDirectories.ENTITIES.toString())));
		oakForest = new TexturedModel(OBJLoader.loadObjModel("grassModel", OTWDirectories.ENTITIES.toString(), loader), 
											new ModelTexture(loader.loadTexture("oakforest", OTWDirectories.ENTITIES.toString())));
		// Lit
		lamp =  new TexturedModel(OBJLoader.loadObjModel("lamp", OTWDirectories.ENTITIES.toString(), loader), 
			      							new ModelTexture(loader.loadTexture("lamp", OTWDirectories.ENTITIES.toString())));
		// Model settings
		// Static
		planatusForest.getTexture().setHasTransparency(true);
		planatusForest.getTexture().setUseFakeLighting(true);
		pineForest.getTexture().setHasTransparency(true);
		pineForest.getTexture().setUseFakeLighting(true);
		oakForest.getTexture().setHasTransparency(true);
		oakForest.getTexture().setUseFakeLighting(true);
		
		// Lit
		lamp.getTexture().setUseFakeLighting(true);
	}
	
	/**
	 * Creates static {@link Entity} objects from an autogen image file that maps specifically what type of entity should
	 * be generated at a given location. The image file should be filled in black with the exception of 
	 * red, green or blue pixels, which each determine buildings, trees and airports, respectively.
	 * 
	 * <p>The {@link Terrain} argument specifies to which terrain the autogen objects should be "bound" to, which
	 * involves adding the objects to a List in the terrain object. This list is then iterated through to render
	 * each entity 
	 * 
	 * <p>Each pixel adds a new object, so they should be added to the autogen image (autogen.png) judiciously
	 * 
	 * @param terrain
	 * @param fileName
	 * @param directory
	 */
	public static void createAutogenImageEntities(Terrain terrain, String fileName, String directory) {
		
		BufferedImage image = null;
		
		try {image = ImageIO.read(new File(OTWDirectories.RESOURCES.toString() + File.separator + directory + File.separator + fileName + OTWFiles.TEXTURE_EXT.toString()));} 
		catch (IOException e) {logger.error("Could not load autogen file: " + fileName + OTWFiles.TEXTURE_EXT.toString() + "!", e);}
		
		float imageScale = Terrain.getSize()/image.getHeight();
		float scaledX, scaledZ;
		Color readColor;
		
		for (int x = 0; x < image.getWidth(); x+=6) {
			for (int z = 0; z < image.getHeight(); z+=6) {
				readColor = new Color(image.getRGB(x, z));
				scaledX = x * imageScale;
				scaledZ = z * imageScale;
				
				if(readColor.getRed() > 250) {
					// Create buildings here
				} else if(readColor.getGreen() > 250) {
					createRandomTrees(terrain, scaledX, scaledZ);
				} else if(readColor.getBlue() > 250) {
					// Create airport here
				}
			}
		}
	}
	
	/**
	 * Creates a random group of trees at the specified x and z position. Uses {@link Terrain} object to get
	 * the absolute world position of the "origin" of this terrain object, which is used to determine the 
	 * relative position to add a tree. Then it adds this entity to a list of static {@link Entity} objects
	 * contained in terrain object.
	 * 
	 * @param terrain
	 * @param x
	 * @param z
	 */
	private static void createRandomTrees(Terrain terrain, float x, float z) {
		
		Random random = new Random();
		
		float y = terrain.getTerrainHeight(x, z);
		// (absolute world position of terrain's origin) + (position relative to origin) 
		x += terrain.getX();
		z += terrain.getZ();
		
		Entity staticEntity;
		
		if (random.nextInt(100) % 3 == 0) {
			staticEntity = new Entity(pineForest, new Vector3f(x, y-2, z), 
									  0, random.nextFloat()*360, 0, 
									  random.nextFloat() + 6);
			
			terrain.getStaticEntities().add(staticEntity);
		} else if (random.nextInt(100) % 9 == 0) {
			staticEntity = new Entity(oakForest, new Vector3f(x, y-2, z), 
									  0, random.nextFloat()*360, 0, 
									  random.nextFloat() + 6);
			
			terrain.getStaticEntities().add(staticEntity);
		} else if (random.nextInt(100) % 10 == 0) {
			staticEntity = new Entity(planatusForest, new Vector3f(x, y-2, z), 
									  0, random.nextFloat()*360, 0, 
									  random.nextFloat() + 6);
			
			terrain.getStaticEntities().add(staticEntity);
		}
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
		logger.debug("Generating a(n)" + entityName + " at: (" + position.x + ", " + position.y + ", " + position.z + ")...");
		
		TexturedModel staticEntity =  new TexturedModel(OBJLoader.loadObjModel(entityName, OTWDirectories.ENTITIES.toString(), loader), 
														new ModelTexture(loader.loadTexture(entityName, OTWDirectories.ENTITIES.toString())));
		
		miscStaticEntities.add(new Entity(staticEntity, position, xRot, yRot, zRot, scale));
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
		float yPos = Terrain.getCurrentTerrain(terrainTree, xPos, zPos).getTerrainHeight(xPos, zPos);
		
		createStaticEntity(entityName, new Vector3f(xPos, yPos, zPos), 0, yRot, 0, scale);
	}
	
	/**
	 * Creates a single static entity based on the player's position
	 * 
	 * @param entityName
	 * @param player
	 * @param scale
	 */
	public void createStaticEntity(String entityName, Player player, float scale) {
		createStaticEntity(entityName, player.getPosition(), player.getRotX(), player.getRotY(), player.getRotZ(), scale);
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
		logger.debug("Generating a(n)" + entityName + " at: (" + position.x + ", " + position.y + ", " + position.z + ")...");
		
		TexturedModel litEntity =  new TexturedModel(OBJLoader.loadObjModel(entityName, OTWDirectories.ENTITIES.toString(), loader), 
													 new ModelTexture(loader.loadTexture(entityName, OTWDirectories.ENTITIES.toString())));
		
		miscLitEntities.add(new Entity(litEntity, position, xRot, yRot, zRot, scale));
		
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
		float yPos = Terrain.getCurrentTerrain(terrainTree, xPos, zPos).getTerrainHeight(xPos, zPos);
		
		createLitEntity(entityName, new Vector3f(xPos, yPos, zPos), 0, yRot, 0, scale, color, attenuation, lightPosOffset);
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
		createLitEntity(entityName, player.getPosition(), player.getRotX(), player.getRotY(), player.getRotZ(), 
						scale, color, attenuation, lightPosOffset);
	}
	
	public void addToStaticEntities(Entity entity) {
		miscStaticEntities.add(entity);
	}
	
	public List<Entity> getLitEntities() {
		return miscLitEntities;
	}

	public List<Entity> getStaticEntities() {
		return miscStaticEntities;
	}
}
