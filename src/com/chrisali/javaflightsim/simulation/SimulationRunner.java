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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chrisali.javaflightsim.interfaces.SimulationController;
import com.chrisali.javaflightsim.interfaces.Steppable;
import com.chrisali.javaflightsim.lwjgl.LWJGLWorld;
import com.chrisali.javaflightsim.lwjgl.events.WindowClosedListener;
import com.chrisali.javaflightsim.simulation.datatransfer.EnvironmentData;
import com.chrisali.javaflightsim.simulation.datatransfer.EnvironmentDataListener;
import com.chrisali.javaflightsim.simulation.datatransfer.FlightData;
import com.chrisali.javaflightsim.simulation.datatransfer.FlightDataListener;
import com.chrisali.javaflightsim.simulation.flightcontrols.FlightControls;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.setup.IntegratorConfig;
import com.chrisali.javaflightsim.simulation.setup.Options;
import com.chrisali.javaflightsim.simulation.setup.SimulationConfiguration;

/**
 * Main runner thread for JavaFlightSimulator that combines all {@link Steppable} components into a single thread so that they can run
 * synchronously and not cause concurrency issues with each other
 * 
 * @author Christopher
 *
 */
public class SimulationRunner implements Runnable, WindowClosedListener {
	
	private static final Logger logger = LogManager.getLogger(SimulationRunner.class);
	private static final int TO_MILLISEC = 1000;

	private SimulationController simController;
	
	private FlightControls flightControls;
	private Integrate6DOFEquations simulation;
	private LWJGLWorld outTheWindow;
	
	private FlightData flightData;
	private EnvironmentData environmentData;
	
	private Map<IntegratorConfig, Double> integratorConfig;
	private Set<Options> options;	
		
	private AtomicInteger timeMS = new AtomicInteger(0);
	private int frameStepMS;
	private int endTimeMS;
	private int threadPauseMS;
	
	private boolean running = false;
	
	/**
	 * Constructor that initialize main simulation ({@link Integrate6DOFEquations} and {@link FlightControls}) components and 
	 * configrures simulation time
	 * 
	 * @param simController
	 */
	public SimulationRunner(SimulationController simController) {
		this.simController = simController;
		
		SimulationConfiguration configuration = simController.getConfiguration();
		integratorConfig = configuration.getIntegratorConfig();
		options = configuration.getSimulationOptions();
		
		configureSimulationTime();
		
		logger.debug("Initializing flight controls...");
		flightControls = new FlightControls(simController, timeMS);
		
		logger.debug("Initializing simulation...");
		simulation = new Integrate6DOFEquations(flightControls, configuration);
	}
	
	/**
	 * Sets running parameters (start/end time and frame step time) for the timulation. Time is kept as an AtomicInteger to ensure
	 * atomic incrementation
	 */
	public void configureSimulationTime() {
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
	 * Depending on the presence of ANALYSIS_MODE in options EnumMap, configures the runner to initialize the OTW display and all necessary listeners
	 */
	private void configureAnalysisNormalMode() {
		if (options.contains(Options.ANALYSIS_MODE)) {
			logger.debug("Running simulation in Analysis Mode...");
		} else {
			logger.debug("Running simulation in Normal Mode...");
						
			logger.debug("Initializing LWJGL world...");
			outTheWindow = new LWJGLWorld(simController);
			outTheWindow.addWindowClosedListener(this);
			outTheWindow.init();

			logger.debug("Initializing flight data transfer...");
			flightData = new FlightData(simulation);
			flightData.addFlightDataListener(outTheWindow);

			logger.debug("Initializing environment data transfer...");
			environmentData = new EnvironmentData(outTheWindow);
			environmentData.addEnvironmentDataListener(simulation);		
		}
	}
	
	/**
	 * Main runner loop where {@link Steppable} components are step updated each iteration of the loop depending on the current value of time
	 */
	@Override
	public void run() {
		running = true;
				
		configureAnalysisNormalMode();
		
		if (options.contains(Options.CONSOLE_DISPLAY))
			simController.initializeConsole();

		while (running && timeMS.get() < endTimeMS) {
			try {
				// Step update each component if allowed to based on the current time 
				if (flightControls.canStepNow(timeMS.get()))
					flightControls.step();
					
				if (simulation.canStepNow(timeMS.get()))
					simulation.step();
				
				if (flightData != null && flightData.canStepNow(timeMS.get()))
					flightData.step();
				
				if (environmentData != null && environmentData.canStepNow(timeMS.get()))
					environmentData.step();
				
				if (outTheWindow != null && outTheWindow.canStepNow(timeMS.get()))
					outTheWindow.step();
				
				Thread.sleep((long)(threadPauseMS));

				timeMS.addAndGet(frameStepMS);
			} catch (Exception ez) {
				logger.error("Exception encountered while running Simulation Runner thread. Attempting to continue...", ez);
				
				continue;
			} 
		}
		
		if (options.contains(Options.ANALYSIS_MODE))
			simController.plotSimulation();
		
		running = false;
	}
			
	/**
	 * When LWJGL OTW window is closed, this event is fired
	 */
	@Override
	public void onWindowClosed() {
		simController.stopSimulation();	
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
	 * Lets other objects request to stop the simulation by setting running to false
	 * 
	 * @param running
	 */
	public synchronized void setRunning(boolean running) { this.running = running; }
}
