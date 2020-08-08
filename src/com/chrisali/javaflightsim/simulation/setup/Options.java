/*******************************************************************************
 * Copyright (C) 2016-2018 Christopher Ali
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
package com.chrisali.javaflightsim.simulation.setup;

import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;

/**
 * Provides Enums for the options EnumSet to provide the following options:
 * 
 *	<p>ANALYSIS_MODE - Removes real-time aspect of the simulation, injects doublet flight control inputs and generates plots at the end of the run; 
 *	used to analyze transient dynamics of the aircraft </p>
 *	<p>UNLIMITED_FLIGHT - Removes the end of the simulation to allow for infinite flight; data logging is limited to the last 100 seconds of simulation</p>
 *	<p>PAUSED - Pauses the integration and therefore the simulation; used in combination with RESET to return the simulation to initial conditions</p>
 *	<p>RESET - Resets the integration to initial conditions using {@link IntegrationSetup#gatherInitialConditions(String)}</p>
 *	<p>CONSOLE_DISPLAY - Displays every piece of data in {@link Integrate6DOFEquations#getSimOut()} in the console for each step of integration</p>
 *	<p>USE_JOYSTICK - Uses a joystick and keyboard while running pilot in the loop simulation</p>
 *	<p>USE_MOUSE - Uses mouse and keyboard while running pilot in the loop simulation</p>
 */
public enum Options {
	ANALYSIS_MODE     ("Analysis Mode"),
	UNLIMITED_FLIGHT  ("Unlimited Flight"),
	PAUSED			  ("Paused"),
	RESET			  ("Reset"),
	CONSOLE_DISPLAY	  ("Console Display"),
	USE_JOYSTICK	  ("Use Joystick"),
	USE_MOUSE		  ("Use Mouse");
	
	private String option;
	
	private Options(String option) {this.option = option;}
	
	public String toString() {return option;}
}