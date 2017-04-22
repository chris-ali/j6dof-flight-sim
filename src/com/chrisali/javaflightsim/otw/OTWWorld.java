package com.chrisali.javaflightsim.otw;

/**
 * Interface to support various implementations of an OTW (out-the-window) display
 * for Java Flight Simulator
 */
public interface OTWWorld {

	/**
	 * @return Height of terrain at the aircraft's current position
	 */
	public float getTerrainHeight();
}