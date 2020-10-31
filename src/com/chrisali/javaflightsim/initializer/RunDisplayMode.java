/*******************************************************************************
 * Copyright (C) 2016-2020 Christopher Ali
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *  If you have any questions about this project, you can visit
 *  the project's GitHub repository at: http://github.com/chris-ali/j6dof-flight-sim/
 ******************************************************************************/
package com.chrisali.javaflightsim.initializer;

/**
 * Provides Enums for the options EnumSet to configure the display and GUI mode to use:
 *  
 *	<p>LWJGL_SWING - Uses the LWJGL engine with Swing GUI, administered by {@link LWJGLSwingSimulationController}</p>
 *	<p>LWJGL_JAVAFX - Uses the LWJGL engine with JavaFX GUI, administered by {@link LWJGLJavaFXSimulationController}</p>
 *  <p>SWING_ONLY - Only uses the legacy Swing GUI for menus and for simulation output, administered by *controller goes here*</p>
 *  <p>JMONKEYENGINE - Uses the JMonkeyEngine implementation for menus and disply, administered by *controller goes here*</p>
 *  <p>NETWORK - Uses a TCP/UDP repeater to remotely transmit simulation data to another program such as X-Plane</p>
 */
public enum RunDisplayMode {
	LWJGL_SWING   ("LWJGL Swing"),
	LWJGL_JAVAFX  ("LWJGL JavaFX"),
	SWING_ONLY    ("Swing Only"),
	JMONKEYENGINE ("JMonkeyEngine"),
	NETWORK       ("Network");
	
	private String displayMode;
	
	private RunDisplayMode(String displayMode) {this.displayMode = displayMode;}
	
	public String toString() {return displayMode;}
}
