package com.chrisali.javaflightsim.simulation.interfaces;

import com.chrisali.javaflightsim.initializer.LWJGLSwingSimulationController;

/**
 * Controls the starting and stopping of the simulation component of JavaFlightSimulator.
 * Add configuration and OTW/network repeater initialization options to an implementation
 * of this interface to as needed.
 * 
 * @see LWJGLSwingSimulationController
 * @author Chris Ali
 *
 */
public interface SimulationController {
	
	void startSimulation();

	void stopSimulation();
}