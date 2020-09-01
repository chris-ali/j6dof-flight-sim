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
package com.chrisali.javaflightsim.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.chrisali.javaflightsim.interfaces.Steppable;
import com.chrisali.javaflightsim.simulation.flightcontrols.FlightControlsState;
import com.chrisali.javaflightsim.simulation.flightcontrols.FlightControlsStateManager;
import com.chrisali.javaflightsim.simulation.flightcontrols.SimulationEventListener;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.integration.SimOuts;
import com.chrisali.javaflightsim.simulation.setup.IntegratorConfig;
import com.chrisali.javaflightsim.simulation.setup.SimulationConfiguration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Main stepper for JavaFlightSimulator that combines all {@link Steppable} components into a single function call. 
 * This can be used when the main update loop is controlled by a different process
 * 
 * @author Christopher
 *
 */
public class SimulationStepper {
	
	private static final Logger logger = LogManager.getLogger(SimulationStepper.class);
	private static final int TO_MILLISEC = 1000;

	private FlightControlsStateManager flightControlsManager;
	private Integrate6DOFEquations simulation;
	
	private List<SimulationEventListener> simulationEventListeners = new ArrayList<>();	

	private AtomicInteger timeMS = new AtomicInteger(0);
	private int frameStepMS;
	
	private boolean running = false;
	
	/**
	 * Constructor that initialize main simulation ({@link Integrate6DOFEquations} and {@link FlightControlsState}) components and 
	 * configures simulation time
	 * 
	 * @param configuration
	 */
	public SimulationStepper(SimulationConfiguration configuration) {
		Map<IntegratorConfig, Double> integratorConfig = configuration.getIntegratorConfig();
		
		// Set up running parameters for simulation
		timeMS = new AtomicInteger(integratorConfig.get(IntegratorConfig.STARTTIME).intValue() * TO_MILLISEC);
		
		// Pause thread for frameStepMS milliseconds to emulate real time operation in normal mode
		frameStepMS = (int) (integratorConfig.get(IntegratorConfig.DT) * TO_MILLISEC);
		
		logger.info("Initializing flight controls manager...");
		flightControlsManager = new FlightControlsStateManager(configuration, timeMS);
		
		logger.info("Initializing simulation...");
		simulation = new Integrate6DOFEquations(flightControlsManager.getControlsState(), configuration);;
		//simulation.addFlightDataListener(outTheWindow);
	}
	
	/**
	 * Main call where {@link Steppable} components are step updated each time this is called depending on the current value of time
	 */
	public void stepAll() {			
		if (!running)
			return;

		try {
			// Step update each component if allowed to based on the current time 
			if (flightControlsManager.canStepNow(timeMS.get()))
				flightControlsManager.step();
				
			if (simulation.canStepNow(timeMS.get()))
				simulation.step();

			timeMS.addAndGet(frameStepMS);
		} catch (Exception ez) {
			logger.error("Exception encountered while iteration of simulation. Attempting to continue...", ez);
		} 
	}

	/**
	 * Adds SimulationEventListener objects to listener list for the out the window (if Normal mode) 
	 * and flight controls manager
	 * 
	 * @param listener
	 */
	public void addSimulationEventListener(SimulationEventListener listener) {
		if (listener != null) {
			logger.info("Adding simulation event listener: " + listener.getClass());
			simulationEventListeners.add(listener);

			//if (outTheWindow != null)
			//	outTheWindow.addSimulationEventListener(listener);

			if (flightControlsManager.getActuator() != null)
				flightControlsManager.getActuator().addSimulationEventListener(listener);
		}
	}
	
	/**
	 * @return List of simulation outputs during run time
	 */
	public List<Map<SimOuts, Double>> getLogsOut() {
		return (simulation != null) ? simulation.getLogsOut() : null;
	}
	
	/**
	 * @return If stepper is running
	 */
	public synchronized boolean isRunning() { return running; }
	
	/**
	 * Lets other objects request to stop the simulation by setting running to false
	 * 
	 * @param running
	 */
	public synchronized void setRunning(boolean running) { this.running = running; }
}
