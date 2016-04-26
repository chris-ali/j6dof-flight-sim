package com.chrisali.javaflightsim.otw.renderengine;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.chrisali.javaflightsim.otw.entities.Camera;
import com.chrisali.javaflightsim.otw.models.RawModel;
import com.chrisali.javaflightsim.otw.shaders.WaterShader;
import com.chrisali.javaflightsim.otw.water.WaterFrameBuffers;
import com.chrisali.javaflightsim.otw.water.WaterTile;
import com.chrisali.javaflightsim.utilities.RenderingUtilities;

public class WaterRenderer {
	
	private static final String DUDV_MAP = "waterDUDV";

	private static float fogDensity = MasterRenderer.getFogDensity();
	private static float fogGradient = MasterRenderer.getFogGradient();
	
	private static float waveStrength = 0.02f;
	private static float waveSpeed = 0.02f;
	private static float waveFactor = 0;
	
	private int dudvTexture;
	private RawModel quad;
	private WaterShader shader;
	private WaterFrameBuffers waterFrameBuffers;

	public WaterRenderer(Loader loader, WaterShader shader, Matrix4f projectionMatrix, WaterFrameBuffers waterFrameBuffers) {
		this.shader = shader;
		this.waterFrameBuffers = waterFrameBuffers;
		dudvTexture = loader.loadTexture(DUDV_MAP, "water");
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
		setUpVAO(loader);
	}

	public void render(List<WaterTile> water, Camera camera) {
		prepareRender(camera);	
		for (WaterTile tile : water) {
			Matrix4f modelMatrix = RenderingUtilities.createTransformationMatrix(
					new Vector3f(tile.getX(), tile.getHeight(), tile.getZ()), 0, 0, 0,
					WaterTile.TILE_SIZE);
			shader.loadModelMatrix(modelMatrix);
			shader.loadFog(fogDensity, fogGradient);
			shader.loadSkyColor(MasterRenderer.getSkyColor().x, 
								MasterRenderer.getSkyColor().y, 
								MasterRenderer.getSkyColor().z);
			shader.connectTextures();
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, quad.getVertexCount());
		}
		unbind();
	}
	
	private void prepareRender(Camera camera){
		shader.start();
		shader.loadViewMatrix(camera);
		
		waveFactor += waveSpeed * DisplayManager.getFrameTimeSeconds();
		waveFactor %= 1;
		shader.loadWaves(waveStrength, waveFactor);
		
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, waterFrameBuffers.getReflectionTexture());
		
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, waterFrameBuffers.getRefractionTexture());
		
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, dudvTexture);
	}
	
	private void unbind(){
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}

	private void setUpVAO(Loader loader) {
		// Just x and z vectex positions here, y is set to 0 in v.shader
		float[] vertices = { -1, -1, -1, 1, 1, -1, 1, -1, -1, 1, 1, 1 };
		quad = loader.loadToVAO(vertices, 2);
	}

	public static void setWaveStrength(float waveStrength) {
		WaterRenderer.waveStrength = waveStrength;
	}

	public static void setWaveSpeed(float waveSpeed) {
		WaterRenderer.waveSpeed = waveSpeed;
	}
}
