package com.chrisali.javaflightsim.otw.shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.chrisali.javaflightsim.otw.entities.Camera;
import com.chrisali.javaflightsim.utilities.RenderingUtilities;

public class WaterShader extends ShaderProgram {

	private final static String VERTEX_FILE = SHADER_ROOT_PATH + "waterVertexShader.txt";
	private final static String FRAGMENT_FILE = SHADER_ROOT_PATH + "waterFragmentShader.txt";

	private int location_modelMatrix;
	private int location_viewMatrix;
	private int location_projectionMatrix;
	private int location_density;
	private int location_gradient;
	private int location_skyColor;
	private int location_reflectionTexture;
	private int location_refractionTexture;
	private int location_dudvMap;
	private int location_waveStrength;
	private int location_waveFactor;

	public WaterShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_modelMatrix = super.getUniformLocation("modelMatrix");
		location_density = super.getUniformLocation("density");
		location_gradient = super.getUniformLocation("gradient");
		location_skyColor = super.getUniformLocation("skyColor");
		location_reflectionTexture = super.getUniformLocation("reflectionTexture");
		location_refractionTexture = super.getUniformLocation("refractionTexture");
		location_dudvMap = super.getUniformLocation("dudvMap");
		location_waveStrength = super.getUniformLocation("waveStrength");
		location_waveFactor = super.getUniformLocation("waveFactor");
	}
	
	public void loadWaves(float waveStrength, float waveFactor) {
		super.loadFloat(location_waveStrength, waveStrength);
		super.loadFloat(location_waveFactor, waveFactor);
	}
	
	public void connectTextures() {
		super.loadInt(location_reflectionTexture, 0);
		super.loadInt(location_refractionTexture, 1);
		super.loadInt(location_dudvMap, 2);
	}
	
	public void loadSkyColor(float r, float g, float b) {
		super.loadVector(location_skyColor, new Vector3f(r, g, b));
	}
	
	public void loadFog(float density, float gradient) {
		super.loadFloat(location_density, density);
		super.loadFloat(location_gradient, gradient);
	}

	public void loadProjectionMatrix(Matrix4f projection) {
		super.loadMatrix(location_projectionMatrix, projection);
	}
	
	public void loadViewMatrix(Camera camera){
		Matrix4f viewMatrix = RenderingUtilities.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}

	public void loadModelMatrix(Matrix4f modelMatrix){
		super.loadMatrix(location_modelMatrix, modelMatrix);
	}

}
