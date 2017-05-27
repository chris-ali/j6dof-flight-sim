package com.chrisali.javaflightsim.simulation.interfaces;

/**
 * Interface to support various implementations of an OTW (out-the-window) display
 * for Java Flight Simulator
 */
public interface OTWWorld {

	/**
	 * @return Height of terrain at the aircraft's current position
	 */
	public float getTerrainHeight();

	/**
	 * @return If out the window display is running
	 */
	boolean isRunning();
}