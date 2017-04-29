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
package com.chrisali.javaflightsim.otw.terrain;

import java.util.TreeMap;

import com.chrisali.javaflightsim.otw.entities.Ownship;
import com.chrisali.javaflightsim.otw.renderengine.Loader;
import com.chrisali.javaflightsim.otw.textures.TerrainTexture;
import com.chrisali.javaflightsim.otw.textures.TerrainTexturePack;
import com.chrisali.javaflightsim.otw.utilities.OTWDirectories;

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
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap", OTWDirectories.TERRAIN.toString()));
	
		for (int i = 0; i < numTerrains; i++) {
			for (int j = 0; j < numTerrains; j++) {
				terrainTree.put(i + "-" + j, new Terrain(i, j, "heightMap", OTWDirectories.TERRAIN.toString(), loader, texturePack, blendMap, ownship));
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
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture(backgroundTextureName, OTWDirectories.TERRAIN.toString()));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture(rTextureName, OTWDirectories.TERRAIN.toString()));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture(gTextureName, OTWDirectories.TERRAIN.toString()));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture(bTextureName, OTWDirectories.TERRAIN.toString()));
		
		return new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture); 
	}

	public TreeMap<String, Terrain> getTerrainTree() {
		return terrainTree;
	}
}
