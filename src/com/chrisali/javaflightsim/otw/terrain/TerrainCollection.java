package com.chrisali.javaflightsim.otw.terrain;

import java.util.TreeMap;

import com.chrisali.javaflightsim.otw.entities.Ownship;
import com.chrisali.javaflightsim.otw.renderengine.Loader;
import com.chrisali.javaflightsim.otw.textures.TerrainTexture;
import com.chrisali.javaflightsim.otw.textures.TerrainTexturePack;

/**
 * An array of {@link Terrain} objects used to model out the world 
 * 
 * @author Christopher Ali
 *
 */
public class TerrainCollection {
	
	private TreeMap<String, Terrain> terrainTree;
	
	/**
	 * Creates a TreeMao of {@link Terrain} objects, with texture blending and height maps. Each key to the tree map consists of
	 * the string "xGrid-zGrid", which represents the terrain object's position relative to other terrains in an array fashion
	 * 
	 * @param numTerrains
	 * @param loader
	 * @param ownship
	 */
	public TerrainCollection(int numTerrains, Loader loader, Ownship ownship) {
		terrainTree = new TreeMap<>();

		TerrainTexturePack texturePack = createTexturePack("fields", "town", "forest", "water", loader);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap", "Terrain"));
	
		for (int i = 0; i < numTerrains; i++) {
			for (int j = 0; j < numTerrains; j++) {
				terrainTree.put(i + "-" + j, new Terrain(i, j, "heightMap", "Terrain", loader, texturePack, blendMap, ownship));
			}
		}
	}
	
	/**
	 * Creates a texture blending "pack," which creates a blend map to paint the terrain with 4 texture types
	 * assigned to black, red, green and blue colors
	 * 
	 * @param backgroundTextureName
	 * @param rTextureName
	 * @param gTextureName
	 * @param bTextureName
	 * @param loader
	 * @return
	 */
	private TerrainTexturePack createTexturePack(String backgroundTextureName, 
							String rTextureName, String gTextureName, String bTextureName, Loader loader) {
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture(backgroundTextureName, "Terrain"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture(rTextureName, "Terrain"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture(gTextureName, "Terrain"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture(bTextureName, "Terrain"));
		
		return new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture); 
	}

	public TreeMap<String, Terrain> getTerrainTree() {
		return terrainTree;
	}
}
