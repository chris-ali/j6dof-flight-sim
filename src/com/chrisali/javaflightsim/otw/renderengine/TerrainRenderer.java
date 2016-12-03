package com.chrisali.javaflightsim.otw.renderengine;

import java.util.TreeSet;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.chrisali.javaflightsim.otw.models.RawModel;
import com.chrisali.javaflightsim.otw.shaders.TerrainShader;
import com.chrisali.javaflightsim.otw.terrain.Terrain;
import com.chrisali.javaflightsim.otw.textures.TerrainTexturePack;
import com.chrisali.javaflightsim.utilities.RenderingUtilities;

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
				GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
				unbindTexturedModel();
				//System.out.printf("%5.0f - %5.0f is %5.0f from ownship\n", terrain.getX(), terrain.getZ(), terrain.getDistanceFromOwnship());
			}
		}
		//System.out.println("---------------------------------------");
		//System.out.printf("Terrain tree has %d items\n", terrainTree.size());
	}
	
	private void prepareTerrain(Terrain terrain) {
		RawModel rawModel = terrain.getModel();
		
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		
		bindTextures(terrain);
		terrainShader.loadShineVariables(1, 0); //TODO
	}
	
	private void bindTextures(Terrain terrain) {
		TerrainTexturePack texturePack = terrain.getTexturePack();
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getBackgroundTexture().getTextureID());
		
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getrTexture().getTextureID());
		
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getgTexture().getTextureID());
		
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getbTexture().getTextureID());
		
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getBlendMap().getTextureID());
	}
	
	private void unbindTexturedModel() {
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		
		GL30.glBindVertexArray(0);
	}
	
	private void loadModelMatrix(Terrain terrain) {
		Matrix4f transformationMatrix = RenderingUtilities.createTransformationMatrix(new Vector3f(terrain.getX(), 0, terrain.getZ()), 0, 0, 0, 1);
		terrainShader.loadTransformationMatrix(transformationMatrix);
	}
}
