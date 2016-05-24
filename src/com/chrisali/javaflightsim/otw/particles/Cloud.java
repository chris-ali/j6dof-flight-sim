package com.chrisali.javaflightsim.otw.particles;

import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

public class Cloud extends Particle {
	
	private int textureIndex;
	private int textureIndices;

	public Cloud(ParticleTexture texture, Vector3f position, Vector3f velocity, float rotation, float scale) {
		super(texture, position, velocity, 0, Float.POSITIVE_INFINITY, rotation, scale);
		
		Random random = new Random();
		textureIndices = getTexture().getNumberOfAtlasRows() * getTexture().getNumberOfAtlasRows();
		textureIndex = random.nextInt(textureIndices - 1);
	}

	@Override
	protected void updateTextureCoordinateInfo() {
		int index1 = textureIndex;
		int index2 = index1 < textureIndices - 1 ? index1 + 1 : index1;
		
		this.textureBlend = 1.0f;
		
		setTextureOffset(getTextureOffset1(), index1);
		setTextureOffset(getTextureOffset2(), index2);
	}
}
