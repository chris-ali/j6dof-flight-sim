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
package com.chrisali.javaflightsim.lwjgl.terrain;

import java.util.TreeMap;

import com.chrisali.javaflightsim.lwjgl.entities.Ownship;
import com.chrisali.javaflightsim.lwjgl.loader.Loader;
import com.chrisali.javaflightsim.lwjgl.textures.TerrainTexture;
import com.chrisali.javaflightsim.lwjgl.textures.TerrainTexturePack;
import com.chrisali.javaflightsim.lwjgl.utilities.OTWDirectories;

import org.lwjgl.util.vector.Vector3f;

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
	
		int median = Math.abs(numTerrains/2);
		
		for (int i = -median; i < median; i++) {
			for (int j = -median; j < median; j++) {
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

	/**
	 * 
	 * @return TreeMap of terrains as part of this collection
	 */
	public TreeMap<String, Terrain> getTerrainTree() {
		return terrainTree;
	}

	/**
	 * Calculates the height of the terrain in the exaxt spot that the ownship is currently over 
	 * 
	 * @return terrain height [ft]
	 */
	public synchronized float getTerrainHeight(Ownship ownship) {
		Vector3f position = ownship.getPosition();
		
		// Terrain object ownship is currently on
		Terrain currentTerrain = Terrain.getCurrentTerrain(terrainTree, position.x, position.z);
		
		// If outside world bounds, return 0 as terrain height
		return (currentTerrain == null) ? 0.0f : currentTerrain.getTerrainHeight(position.x, position.z);
	}
}
