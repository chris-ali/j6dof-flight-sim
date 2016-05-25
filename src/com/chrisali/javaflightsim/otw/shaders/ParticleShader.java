package com.chrisali.javaflightsim.otw.shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class ParticleShader extends ShaderProgram {
	
	private static final String VERTEX_FILE = SHADER_ROOT_PATH + "particleVertexShader.txt";
	private static final String FRAGMENT_FILE = SHADER_ROOT_PATH + "particleFragmentShader.txt";

	private int location_numberOfAtlasRows;
	private int location_projectionMatrix;
	private int location_skyColor;
	private int location_density;
	private int location_gradient;

	public ParticleShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_numberOfAtlasRows = super.getUniformLocation("numberOfAtlasRows");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_skyColor = super.getUniformLocation("skyColor");
		location_density = super.getUniformLocation("density");
		location_gradient = super.getUniformLocation("gradient");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "modelViewMatrix");
		super.bindAttribute(5, "textureOffsets");
		super.bindAttribute(6, "blendFactor");
		
	}
	
	public void loadSkyColor(float r, float g, float b) {
		super.loadVector(location_skyColor, new Vector3f(r, g, b));
	}
	
	public void loadFog(float density, float gradient) {
		super.loadFloat(location_density, density);
		super.loadFloat(location_gradient, gradient);
	}
	
	public void loadNumberOfAtlasRows(float numberRows) {
		super.loadFloat(location_numberOfAtlasRows, numberRows);
	}

	public void loadProjectionMatrix(Matrix4f projectionMatrix) {
		super.loadMatrix(location_projectionMatrix, projectionMatrix);
	}

}
