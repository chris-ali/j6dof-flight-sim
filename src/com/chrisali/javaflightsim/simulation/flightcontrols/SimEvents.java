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
package com.chrisali.javaflightsim.simulation.flightcontrols;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chrisali.javaflightsim.interfaces.SimulationController;
import com.chrisali.javaflightsim.simulation.setup.Options;

/**
 * Contains static methods that handle all simulation events (pause, reset, quit) that can happen in JavaFlightSimulator
 * 
 * @author Christopher
 *
 */
public class SimEvents {
	
	private static final Logger logger = LogManager.getLogger(SimEvents.class);
	
	// Keep track if button is pressed, so events occur only once if button held down 
	private static boolean pausePressed = false;
	private static boolean resetPressed = false;
	
	// Keep track of reset, so that it can only be run once per pause
	private static boolean wasReset = false;
	
	private static Set<Options> options;
	
	private static SimulationController simController;
	
	public static void init(SimulationController controller) {
		simController = controller;
		options = simController.getConfiguration().getSimulationOptions();
	}
	
	/**
	 * Pauses and unpauses the simulation 
	 * 
	 * @param isPressed
	 */
	public static void pauseUnpauseSimulation(boolean isPressed) {
		if(isPressed && !options.contains(Options.PAUSED) && !pausePressed) {
			options.add(Options.PAUSED);
			logger.debug("Simulation Paused!");
			pausePressed = true;
		} else if(isPressed && options.contains(Options.PAUSED) && !pausePressed) {
			options.remove(Options.PAUSED);
			wasReset = false;
			pausePressed = true;
		} else if(!isPressed && pausePressed) {
			pausePressed = false;
		}
	}
	
	/**
	 * When the simulation is paused, it can be reset back to initial conditions once per pause with this method 
	 * @param isPressed
	 */
	public static void resetSimulation(boolean isPressed) {
		if(isPressed && options.contains(Options.PAUSED) && !options.contains(Options.RESET) && !resetPressed && !wasReset) {
			options.add(Options.RESET);
			logger.debug("Resetting simulation...");
			wasReset = true;
			resetPressed = true;
		} else if (!isPressed && resetPressed) {
			logger.debug("...done!");
			resetPressed = false;
		}
	}
	
	/**
	 * Commands {@link SimulationController} to stop the simulation
	 */
	public static void stopSimulation() {
		simController.stopSimulation();
	}
	
	/**
	 * Commands {@link SimulationController} to generate plots of the simulation thus far
	 */
	public static void plotSimulation() {
		if(!simController.isPlotWindowVisible()) {
			simController.plotSimulation();
		}
	}
}
