package com.chrisali.javaflightsim.simulation.interfaces;

import java.util.concurrent.atomic.AtomicInteger;

import com.chrisali.javaflightsim.initializer.LWJGLSwingSimulationController;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.setup.SimulationConfiguration;

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
	
	public SimulationConfiguration getConfiguration();
	
	public Integrate6DOFEquations getSimulation();
	
	/**
	 * @return current elapsed time in the simulation in millisec
	 */
	public AtomicInteger getTime();
	
	public void plotSimulation();
	
	public boolean isPlotWindowVisible();
	
	public void startSimulation();

	public void stopSimulation();
}