package com.chrisali.javaflightsim.otw.shaders;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class FontShader extends ShaderProgram {

	private static final String VERTEX_FILE = SHADER_ROOT_PATH + "fontVertexShader.txt";
	private static final String FRAGMENT_FILE = SHADER_ROOT_PATH + "fontFragmentShader.txt";
	
	private int location_color;
	private int location_translation;
	
	public FontShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_color = super.getUniformLocation("color");
		location_translation = super.getUniformLocation("translation");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoordinates");
	}
	
	public void loadColor(Vector3f color) {
		super.loadVector(location_color, color);
	}
	
	public void loadTranslation(Vector2f translation) {
		super.loadVector(location_translation, translation);
	}
}
