package com.chrisali.javaflightsim.otw.particles;

public class ParticleTexture {
	private int textureID;
	private int numberOfAtlasRows;
	private boolean additiveBlending;
	
	public ParticleTexture(int textureID, int numberOfAtlasRows, boolean additiveBlending) {
		this.textureID = textureID;
		this.numberOfAtlasRows = numberOfAtlasRows;
		this.additiveBlending = additiveBlending;
	}
	
	public boolean usesAdditiveBlending() {
		return additiveBlending;
	}

	public int getTextureID() {
		return textureID;
	}
	
	public int getNumberOfAtlasRows() {
		return numberOfAtlasRows;
	}
}
