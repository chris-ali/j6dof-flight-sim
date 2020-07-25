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
package com.chrisali.javaflightsim.simulation;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.chrisali.javaflightsim.interfaces.SimulationController;
import com.chrisali.javaflightsim.interfaces.Steppable;
import com.chrisali.javaflightsim.simulation.datatransfer.EnvironmentData;
import com.chrisali.javaflightsim.simulation.datatransfer.EnvironmentDataListener;
import com.chrisali.javaflightsim.simulation.datatransfer.FlightData;
import com.chrisali.javaflightsim.simulation.datatransfer.FlightDataListener;
import com.chrisali.javaflightsim.simulation.flightcontrols.ExternalFlightControlsStateManager;
import com.chrisali.javaflightsim.simulation.flightcontrols.FlightControlsState;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.setup.IntegratorConfig;
import com.chrisali.javaflightsim.simulation.setup.Options;
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

	private ExternalFlightControlsStateManager flightControlsManager;
	private Integrate6DOFEquations simulation;
	
	private FlightData flightData;
	private EnvironmentData environmentData;
	
	private Map<IntegratorConfig, Double> integratorConfig;
	private Set<Options> options;	
		
	private AtomicInteger timeMS = new AtomicInteger(0);
	private int frameStepMS;
	
	private boolean running = false;
	
	/**
	 * Constructor that initialize main simulation ({@link Integrate6DOFEquations} and {@link FlightControlsState}) components and 
	 * configures simulation time
	 * 
	 * @param simController
	 */
	public SimulationStepper(SimulationController simController) {
		SimulationConfiguration configuration = simController.getConfiguration();
		integratorConfig = configuration.getIntegratorConfig();
		options = configuration.getSimulationOptions();
		
		// Set up running parameters for simulation
		timeMS = new AtomicInteger(integratorConfig.get(IntegratorConfig.STARTTIME).intValue() * TO_MILLISEC);
		
		// Pause thread for frameStepMS milliseconds to emulate real time operation in normal mode
		frameStepMS = (int) (integratorConfig.get(IntegratorConfig.DT) * TO_MILLISEC);
		
		logger.debug("Initializing flight controls manager...");
		flightControlsManager = new ExternalFlightControlsStateManager(simController, timeMS);
		
		logger.debug("Initializing simulation...");
		simulation = new Integrate6DOFEquations(flightControlsManager.getControlsState(), configuration);
		
		/*
		logger.debug("Initializing flight data transfer...");
		flightData = new FlightData(simulation);
		flightData.addFlightDataListener(outTheWindow);

		logger.debug("Initializing environment data transfer...");
		environmentData = new EnvironmentData(outTheWindow);
		environmentData.addEnvironmentDataListener(simulation);
		*/

		if (options.contains(Options.CONSOLE_DISPLAY))
			simController.initializeConsole();
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
			
			if (flightData != null && flightData.canStepNow(timeMS.get()))
				flightData.step();
			
			if (environmentData != null && environmentData.canStepNow(timeMS.get()))
				environmentData.step();

			timeMS.addAndGet(frameStepMS);
		} catch (Exception ez) {
			logger.error("Exception encountered while iteration of simulation. Attempting to continue...", ez);
		} 
	}

	/**
	 * Adds {@link FlightDataListener} objects external to {@link SimulationStepper} to flightData's listener list
	 * 
	 * @param listener
	 */
	public void addFlightDataListener(FlightDataListener listener) {
		if (flightData != null) 
			flightData.addListener(listener);
	}
	
	/**
	 * Adds {@link EnvironmentDataListener} objects external to {@link SimulationStepper} to environmentDataListener's listener list
	 * 
	 * @param listener
	 */
	public void addEnvironmentDataListener(EnvironmentDataListener listener) {
		if (environmentData != null) 
			environmentData.addListener(listener);
	}
		
	public Integrate6DOFEquations getSimulation() { return simulation; }
	
	public FlightControlsState getFlightControls() { return flightControlsManager.getControlsState(); }

	public AtomicInteger getTimeMS() { return timeMS; }
	
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
