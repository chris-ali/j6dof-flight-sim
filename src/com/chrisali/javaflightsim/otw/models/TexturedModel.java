package com.chrisali.javaflightsim.otw.models;

import com.chrisali.javaflightsim.otw.textures.ModelTexture;

/**
 * Object that contains an OBJ {@link RawModel} loaded into memory with a {@link ModelTexture}
 * 
 * @author Christopher Ali
 *
 */
public class TexturedModel {
	private RawModel rawModel;
	private ModelTexture texture;
	
	public TexturedModel(RawModel model, ModelTexture texture) {
		this.rawModel = model;
		this.texture = texture;
	}

	public RawModel getRawModel() {
		return rawModel;
	}

	public ModelTexture getTexture() {
		return texture;
	}
}
