package com.chrisali.javaflightsim.otw.textures;

public class ModelTexture {
	private int textureID;
	
	private float shineDamper = 1;
	private float reflectivity = 0;
	
	private boolean hasTransparency = false;
	private boolean useFakeLighting = false;
	
	private int numberOfAtlasRows = 1;
	
	public ModelTexture(int id) {
		this.textureID = id;
	}

	public int getNumberOfAtlasRows() {
		return numberOfAtlasRows;
	}

	public int getTextureID() {
		return textureID;
	}

	public float getShineDamper() {
		return shineDamper;
	}
	
	public boolean isHasTransparency() {
		return hasTransparency;
	}
	
	public boolean isUseFakeLighting() {
		return useFakeLighting;
	}
	
	public void setNumberOfAtlasRows(int numberOfAtlasRows) {
		this.numberOfAtlasRows = numberOfAtlasRows;
	}

	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}
	
	public void setHasTransparency(boolean hasTransparency) {
		this.hasTransparency = hasTransparency;
	}

	public void setUseFakeLighting(boolean useFakeLighting) {
		this.useFakeLighting = useFakeLighting;
	}
}
