package com.chrisali.javaflightsim.otw.terrain;

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
	
	private Terrain[][] terrainArray;
	
	/**
	 * Creates an array of {@link Terrain} objects, with texture blending and height maps
	 * 
	 * @param numTerrains
	 * @param loader
	 */
	public TerrainCollection(int numTerrains, Loader loader) {
		terrainArray = new Terrain[numTerrains/2][numTerrains/2];

		TerrainTexturePack texturePack = createTexturePack("fields", "town", "forest", "water", loader);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap", "Terrain"));
	
		for (int i = 0; i < terrainArray.length; i++) {
			for (int j = 0; j < terrainArray.length; j++) {
				terrainArray[i][j] = new Terrain(i, j, "heightMap", "Terrain", loader, texturePack, blendMap);
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

	public Terrain[][] getTerrainArray() {
		return terrainArray;
	}
}
