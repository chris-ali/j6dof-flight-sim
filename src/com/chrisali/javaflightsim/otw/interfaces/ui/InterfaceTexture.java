package com.chrisali.javaflightsim.otw.interfaces.ui;

import org.lwjgl.util.vector.Vector2f;

public class InterfaceTexture {
	
	private int texture;
	private Vector2f position;
	private Vector2f scale;
	
	public InterfaceTexture(int texture, Vector2f position, Vector2f scale) {
		this.texture = texture;
		this.position = position;
		this.scale = scale;
	}

	public int getTexture() {
		return texture;
	}

	public Vector2f getPosition() {
		return position;
	}

	public Vector2f getScale() {
		return scale;
	}
}
