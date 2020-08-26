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
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chrisali.javaflightsim.interfaces.Steppable;
import com.chrisali.javaflightsim.lwjgl.LWJGLWorld;
import com.chrisali.javaflightsim.simulation.flightcontrols.FlightControlsStateManager;
import com.chrisali.javaflightsim.simulation.flightcontrols.SimulationEventListener;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.setup.IntegratorConfig;
import com.chrisali.javaflightsim.simulation.setup.Options;
import com.chrisali.javaflightsim.simulation.setup.SimulationConfiguration;

/**
 * Main runner thread for JavaFlightSimulator that combines all {@link Steppable} components into a single thread
 * 
 * @author Christopher
 *
 */
public class SimulationRunner implements Runnable {
	
	private static final Logger logger = LogManager.getLogger(SimulationRunner.class);
	private static final int TO_MILLISEC = 1000;

	private FlightControlsStateManager flightControlsManager;
	private Integrate6DOFEquations simulation;
	private LWJGLWorld outTheWindow;

	private List<SimulationEventListener> simulationEventListeners = new ArrayList<>();
	
	private AtomicInteger timeMS = new AtomicInteger(0);
	private int frameStepMS;
	private int endTimeMS;
	private int threadPauseMS;
	
	private boolean running = false;
	
	/**
	 * Constructor that initialize main simulation components, their event listeners and configures simulation run time parameters
	 * 
	 * @param configuration
	 */
	public SimulationRunner(SimulationConfiguration configuration) {
		Map<IntegratorConfig, Double> integratorConfig = configuration.getIntegratorConfig();
		Set<Options> options = configuration.getSimulationOptions();
		
		configureSimulationTime(options, integratorConfig);
		
		logger.info("Initializing flight controls manager...");
		flightControlsManager = new FlightControlsStateManager(configuration, timeMS);
		
		logger.info("Initializing simulation...");
		simulation = new Integrate6DOFEquations(flightControlsManager.getControlsState(), configuration);

		if (options.contains(Options.ANALYSIS_MODE)) {
			logger.info("Will run simulation in Analysis Mode...");
		} else {
			logger.info("Will run simulation in Normal Mode...");
						
			logger.info("Instantiating LWJGL world...");
			outTheWindow = new LWJGLWorld(configuration);
			outTheWindow.addEnvironmentDataListener(simulation);
			outTheWindow.addinputDataListener(flightControlsManager);
			
			simulation.addFlightDataListener(outTheWindow);
		}
	}
	
	/**
	 * Sets running parameters (start/end time and frame step time) for the simulation. Time is kept as an AtomicInteger to ensure
	 * atomic incrementation
	 * 
	 * @param options
	 * @param integratorConfig
	 */
	public void configureSimulationTime(Set<Options> options, Map<IntegratorConfig, Double> integratorConfig) {
		// Set up running parameters for simulation
		timeMS = new AtomicInteger(integratorConfig.get(IntegratorConfig.STARTTIME).intValue() * TO_MILLISEC);
		
		// Pause thread for frameStepMS milliseconds to emulate real time operation in normal mode
		frameStepMS = (int) (integratorConfig.get(IntegratorConfig.DT) * TO_MILLISEC);
		threadPauseMS = (!options.contains(Options.ANALYSIS_MODE)) ? frameStepMS : 1;
		
		// Run forever as a pilot in the loop simulation 
		if (!options.contains(Options.ANALYSIS_MODE) && options.contains(Options.UNLIMITED_FLIGHT))
			endTimeMS = Integer.MAX_VALUE;
		else
			endTimeMS = integratorConfig.get(IntegratorConfig.ENDTIME).intValue() * TO_MILLISEC;		
	}

	/**
	 * Main runner loop where {@link Steppable} components are step updated each iteration of the loop depending on the current value of time
	 */
	@Override
	public void run() {
		running = true;

		// Must init GLFW window from same thread as update method
		if (outTheWindow != null) {
			logger.info("Initializing LWJGL world...");
			outTheWindow.init();
		}
		
		while (running && timeMS.get() < endTimeMS) {
			try {
				// Step update each component if allowed to based on the current time 
				if (flightControlsManager.canStepNow(timeMS.get()))
					flightControlsManager.step();
					
				if (simulation.canStepNow(timeMS.get()))
					simulation.step();

				if (outTheWindow != null && outTheWindow.canStepNow(timeMS.get()))
					outTheWindow.step();
				
				Thread.sleep((long)(threadPauseMS));

				timeMS.addAndGet(frameStepMS);
			} catch (Exception ez) {
				logger.error("Exception encountered while running Simulation Runner thread. Attempting to continue...", ez);
				
				continue;
			} 
		}
		
		running = false;

		simulationEventListeners.forEach(listener -> listener.onStopSimulation());
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

			if (outTheWindow != null)
				outTheWindow.addSimulationEventListener(listener);

			if (flightControlsManager.getActuator() != null)
				flightControlsManager.getActuator().addSimulationEventListener(listener);
		}
	}
	
	public Integrate6DOFEquations getSimulation() { return simulation; }
	
	/**
	 * @return If out sumulation is running
	 */
	public synchronized boolean isRunning() { return running; }
	
	/**
	 * Lets other objects request to stop the simulation by setting running to false
	 * 
	 * @param running
	 */
	public synchronized void setRunning(boolean running) { this.running = running; }
}
