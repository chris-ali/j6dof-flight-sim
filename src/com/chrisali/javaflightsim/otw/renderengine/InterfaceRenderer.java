package com.chrisali.javaflightsim.otw.renderengine;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import com.chrisali.javaflightsim.otw.interfaces.ui.InterfaceTexture;
import com.chrisali.javaflightsim.otw.models.RawModel;
import com.chrisali.javaflightsim.otw.shaders.InterfaceShader;
import com.chrisali.javaflightsim.utilities.RenderingUtilities;

public class InterfaceRenderer {
	
	private final RawModel quad;
	private InterfaceShader shader;
	
	public InterfaceRenderer(Loader loader) {
		float[] positions = {-1, 1, -1, -1, 1, 1, 1, -1};
		quad = loader.loadToVAO(positions);
		shader = new InterfaceShader();
	}
	
	public void render(List<InterfaceTexture> interfaceTextures) {
		shader.start();
		
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		for (InterfaceTexture interfaceTexture : interfaceTextures) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, interfaceTexture.getTexture());
			
			Matrix4f matrix = RenderingUtilities.createTransformationMatrix(interfaceTexture.getPosition(), 
					interfaceTexture.getScale());
			shader.loadTransformation(matrix);
			
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		
		shader.stop();
	}
	
	public void cleanUp() {
		shader.cleanUp();
	}
}
