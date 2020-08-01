package com.chrisali.javaflightsim.interfaces;

/**
 * Interface to support various implementations of an OTW (out-the-window) display
 * for Java Flight Simulator
 */
public interface OTWWorld extends Steppable {

	/**
	 * Initializes all assets and rendering processes for the OTW engine
	 */
	public void init();
}