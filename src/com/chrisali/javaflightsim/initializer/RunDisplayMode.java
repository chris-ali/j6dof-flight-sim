package com.chrisali.javaflightsim.initializer;

/**
 * Provides Enums for the options EnumSet to configure the display and GUI mode to use:
 *  
 *	<p>LWJGL_SWING - Uses the legacy LWJGL engine with Swing GUI, administered by {@link LWJGLSwingSimulationController}</p>
 *  <p>SWING_ONLY - Only uses the legacy Swing GUI for menus and for simulation output, administered by *controller goes here*</p>
 *  <p>JMONKEYENGINE - Uses the JMonkeyEngine implementation for menus and disply, administered by *controller goes here*</p>
 *  <p>NETWORK - Uses a TCP/UDP repeater to remotely transmit simulation data to another program such as X-Plane</p>
 */
public enum RunDisplayMode {
	LWJGL_SWING   ("LWJGL Swing"),
	SWING_ONLY    ("Swing Only"),
	JMONKEYENGINE ("JMonkeyEngine"),
	NETWORK       ("Network");
	
	private String displayMode;
	
	private RunDisplayMode(String displayMode) {this.displayMode = displayMode;}
	
	public String toString() {return displayMode;}
}
