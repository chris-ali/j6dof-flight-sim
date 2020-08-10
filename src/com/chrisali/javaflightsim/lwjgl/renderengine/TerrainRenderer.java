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
package com.chrisali.javaflightsim.lwjgl.renderengine;

import java.util.TreeSet;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import com.chrisali.javaflightsim.lwjgl.models.RawModel;
import com.chrisali.javaflightsim.lwjgl.shaders.TerrainShader;
import com.chrisali.javaflightsim.lwjgl.terrain.Terrain;
import com.chrisali.javaflightsim.lwjgl.textures.TerrainTexturePack;
import com.chrisali.javaflightsim.lwjgl.utilities.RenderingUtilities;

public class TerrainRenderer {
	private TerrainShader terrainShader;

	public TerrainRenderer(TerrainShader shader, Matrix4f projectionMatrix) {
		this.terrainShader = shader;
		terrainShader.start();
		terrainShader.loadProjectionMatrix(projectionMatrix);
		terrainShader.connectTextureUnits();
		terrainShader.stop();
	}
	
	public void render(TreeSet<Terrain> terrainTree) {
		for(Terrain terrain : terrainTree) {
			// Render only terrain objects that are within a certain distance of ownship
			if (terrain.getDistanceFromOwnship() < MasterRenderer.getDrawDistance()) {
				prepareTerrain(terrain);
				loadModelMatrix(terrain);
				glDrawElements(GL_TRIANGLES, terrain.getModel().getVertexCount(), GL_UNSIGNED_INT, 0);
				unbindTexturedModel();
				//System.out.printf("%5.0f - %5.0f is %5.0f from ownship\n", terrain.getX(), terrain.getZ(), terrain.getDistanceFromOwnship());
			}
		}
		//System.out.println("---------------------------------------");
		//System.out.printf("Terrain tree has %d items\n", terrainTree.size());
	}
	
	private void prepareTerrain(Terrain terrain) {
		RawModel rawModel = terrain.getModel();
		
		glBindVertexArray(rawModel.getVaoID());
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		
		bindTextures(terrain);
		terrainShader.loadShineVariables(1, 0); //TODO
	}
	
	private void bindTextures(Terrain terrain) {
		TerrainTexturePack texturePack = terrain.getTexturePack();
		
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, texturePack.getBackgroundTexture().getTextureID());
		
		glActiveTexture(GL_TEXTURE1);
		glBindTexture(GL_TEXTURE_2D, texturePack.getrTexture().getTextureID());
		
		glActiveTexture(GL_TEXTURE2);
		glBindTexture(GL_TEXTURE_2D, texturePack.getgTexture().getTextureID());
		
		glActiveTexture(GL_TEXTURE3);
		glBindTexture(GL_TEXTURE_2D, texturePack.getbTexture().getTextureID());
		
		glActiveTexture(GL_TEXTURE4);
		glBindTexture(GL_TEXTURE_2D, terrain.getBlendMap().getTextureID());
	}
	
	private void unbindTexturedModel() {
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		
		glBindVertexArray(0);
	}
	
	private void loadModelMatrix(Terrain terrain) {
		Matrix4f transformationMatrix = RenderingUtilities.createTransformationMatrix(new Vector3f(terrain.getX(), 0, terrain.getZ()), 0, 0, 0, 1);
		terrainShader.loadTransformationMatrix(transformationMatrix);
	}
}
