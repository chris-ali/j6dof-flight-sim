/*******************************************************************************
 * Copyright (C) 2016-2017 Christopher Ali
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chrisali.javaflightsim.simulation.datatransfer.EnvironmentData;
import com.chrisali.javaflightsim.simulation.datatransfer.EnvironmentDataListener;
import com.chrisali.javaflightsim.simulation.datatransfer.FlightData;
import com.chrisali.javaflightsim.simulation.datatransfer.FlightDataListener;
import com.chrisali.javaflightsim.simulation.flightcontrols.FlightControls;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.interfaces.OTWWorld;
import com.chrisali.javaflightsim.simulation.interfaces.SimulationController;
import com.chrisali.javaflightsim.simulation.setup.IntegratorConfig;
import com.chrisali.javaflightsim.simulation.setup.Options;
import com.chrisali.javaflightsim.simulation.setup.SimulationConfiguration;

public class SimulationRunner implements Runnable {
	
	private static final Logger logger = LogManager.getLogger(SimulationRunner.class);
	private static final int TO_MILLISEC = 1000;

	private FlightControls flightControls;
	private Integrate6DOFEquations simulation;
	private FlightData flightData;
	private EnvironmentData environmentData;
	
	private Map<IntegratorConfig, Double> integratorConfig;
	private Set<Options> options;	
		
	private AtomicInteger timeMS;
	private int frameStepMS;
	private int endTimeMS;
	
	private boolean running = false;
			
	public SimulationRunner(SimulationController simController) {
		
		SimulationConfiguration configuration = simController.getConfiguration();
		integratorConfig = configuration.getIntegratorConfig();
		options = configuration.getSimulationOptions();
		
		logger.debug("Initializing flight controls...");
		flightControls = new FlightControls(simController, this);
		
		logger.debug("Initializing simulation...");
		simulation = new Integrate6DOFEquations(flightControls, configuration);
						
		// Set up running parameters for simulation
		timeMS = new AtomicInteger(integratorConfig.get(IntegratorConfig.STARTTIME).intValue() * TO_MILLISEC);
		frameStepMS = (int) (integratorConfig.get(IntegratorConfig.DT) * TO_MILLISEC);
				
		// Run forever as a pilot in the loop simulation 
		if (!options.contains(Options.ANALYSIS_MODE) && options.contains(Options.UNLIMITED_FLIGHT))
			endTimeMS = Integer.MAX_VALUE;
		else
			endTimeMS = integratorConfig.get(IntegratorConfig.ENDTIME).intValue() * TO_MILLISEC;
	}
	
	public SimulationRunner(SimulationController simController, OTWWorld outTheWindow) {
		this(simController);
		
		if (!options.contains(Options.ANALYSIS_MODE) && outTheWindow != null) {
			logger.debug("Initializing flight data transfer...");
			flightData = new FlightData(simulation);
			
			logger.debug("Initializing environment data transfer...");
			environmentData = new EnvironmentData(outTheWindow);
			environmentData.addEnvironmentDataListener(simulation);
		}
	}
	
	@Override
	public void run() {
		running = true;
		
		while (running && timeMS.get() < endTimeMS) {
			try {
				// Step update each component
				if (flightControls.canStepNow(timeMS.get()))
					flightControls.step();
					
				if (simulation.canStepNow(timeMS.get()))
					simulation.step();
				
				if (flightData != null && flightData.canStepNow(timeMS.get()))
					flightData.step();
				
				if (environmentData != null && environmentData.canStepNow(timeMS.get()))
					environmentData.step();
				
				// Pause for frameStepMS milliseconds to emulate real time operation in analysis mode
				if (!options.contains(Options.ANALYSIS_MODE))
					Thread.sleep((long)(frameStepMS));
				else
					Thread.sleep(1);

				// Increment time
				timeMS.addAndGet(frameStepMS);
			} catch (InterruptedException ex) {
				logger.warn("Simulation Runner thread was interrupted! Ignoring...");
				
				continue;
			} catch (Exception ez) {
				logger.error("Exception encountered while running Simulation Runner thread. Attempting to continue...", ez);
				
				continue;
			} 
		}
		
		running = false;
	}
	
	/**
	 * Adds {@link FlightDataListener} objects external to {@link SimulationRunner} to flightData's listener list
	 * 
	 * @param listener
	 */
	public void addFlightDataListener(FlightDataListener listener) {
		if (flightData != null) 
			flightData.addFlightDataListener(listener);
	}
	
	/**
	 * Adds {@link EnvironmentDataListener} objects external to {@link SimulationRunner} to environmentDataListener's listener list
	 * 
	 * @param listener
	 */
	public void addEnvironmentDataListener(EnvironmentDataListener listener) {
		if (environmentData != null) 
			environmentData.addEnvironmentDataListener(listener);
	}
		
	public Integrate6DOFEquations getSimulation() { return simulation; }
	
	public FlightControls getFlightControls() { return flightControls; }

	public AtomicInteger getTimeMS() { return timeMS; }
	
	/**
	 * @return If out the window display is running
	 */
	public synchronized boolean isRunning() { return running; }
	
	/**
	 * Lets other objects request to stop the flow of flight data by setting running to false
	 * 
	 * @param running
	 */
	public synchronized void setRunning(boolean running) { this.running = running; }
}
