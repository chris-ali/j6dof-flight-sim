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
package com.chrisali.javaflightsim.simulation.flightcontrols;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chrisali.javaflightsim.simulation.datatransfer.FlightData;
import com.chrisali.javaflightsim.simulation.datatransfer.FlightDataListener;
import com.chrisali.javaflightsim.simulation.hidcontrollers.AbstractController;
import com.chrisali.javaflightsim.simulation.hidcontrollers.CHControls;
import com.chrisali.javaflightsim.simulation.hidcontrollers.Joystick;
import com.chrisali.javaflightsim.simulation.hidcontrollers.Keyboard;
import com.chrisali.javaflightsim.simulation.hidcontrollers.Mouse;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.interfaces.SimulationController;
import com.chrisali.javaflightsim.simulation.setup.IntegratorConfig;
import com.chrisali.javaflightsim.simulation.setup.Options;
import com.chrisali.javaflightsim.simulation.setup.SimulationConfiguration;

/**
 * Contains the Flight Controls thread used to handle flight controls actuated by human interface devices, such as
 * {@link Joystick}, {@link Keyboard}, {@link Mouse} or {@link CHControls}, or by P/PD controllers such as autopilots
 * and stability augmentation sytems. Also contains method to inject doublets into controls when simulation is run
 * as analysis. Uses {@link FlightDataListener} to feed back {@link FlightData} to use in P/PD controllers
 * 
 * 
 * @author Christopher Ali
 *
 */
public class FlightControls implements Runnable, FlightDataListener {

	private static final Logger logger = LogManager.getLogger(FlightControls.class);
	
	private boolean running;
	
	private Map<FlightControl, Double> controls;
	private Map<IntegratorConfig, Double> integratorConfig;
	private EnumSet<Options> options;
//	private Map<FlightDataType, Double> flightData;
	
	private Integrate6DOFEquations simulation;
	
	private AbstractController hidController;
	private Keyboard hidKeyboard;
	
	/**
	 * Constructor for {@link FlightControls}; {@link SimulationConfiguration} argument to initialize {@link IntegratorConfig} 
	 * EnumMap, the {@link Options} EnumSet, as well as to update simulation options and call simulation methods. Ensure that
	 * {@link FlightControls#setIntegrate6DOFEquations(Integrate6DOFEquations)}
	 * 
	 * @param simController
	 */
	public FlightControls(SimulationController simController) {
		logger.debug("Initializing flight controls...");
		
		SimulationConfiguration configuration = simController.getConfiguration();
		
		controls = configuration.getInitialControls();
		integratorConfig = configuration.getIntegratorConfig();
		options = configuration.getSimulationOptions();
		
		hidKeyboard = new Keyboard(controls, simController);
		
		// initializes static EnumMap that contains trim values of controls for doublets 
		DoubletGenerator.init();
		Events.init(configuration);
	}
	
	@Override
	public void run() {
		if (simulation == null)
			logger.error("Unable to get a valid reference to the simulation! Some keyboard hotkeys will be disabled.");
		
		// Use controllers for pilot in loop simulation if ANALYSIS_MODE not enabled 
		if (!options.contains(Options.ANALYSIS_MODE)) {
			if (options.contains(Options.USE_JOYSTICK)) {
				logger.debug("Joystick controller selected");
				hidController = new Joystick(controls);
			}
			else if (options.contains(Options.USE_MOUSE)){
				logger.debug("Mouse controller selected");
				hidController = new Mouse(controls);
			}
			else if (options.contains(Options.USE_CH_CONTROLS)) {
				logger.debug("CH Controller suite selected");
				hidController = new CHControls(controls);
			}
		}
		
		running = true;
		
		while (running) {
			try {
				// if not running in analysis mode, controls and options should be updated using updateFlightControls()/updateOptions()
				// otherwise, controls updated using generated doublets instead of pilot input
				if (!options.contains(Options.ANALYSIS_MODE)) {
					if (hidController != null) 
						controls = hidController.calculateControllerValues(controls);
					
					controls = hidKeyboard.calculateControllerValues(controls);
					
					if (simulation != null && simulation.isRunning())
						hidKeyboard.hotKeys();
					
					Thread.sleep((long) (integratorConfig.get(IntegratorConfig.DT)*1000));
				} else {
					if (simulation != null) {
						controls = DoubletGenerator.doubletSeries(controls, simulation.getTime());
						Thread.sleep(1);
					}
				}
			} catch (InterruptedException e) {
				logger.warn("Flight controls thread interrupted, ignoring.");
				
				continue;
			} catch (Exception e) {
				logger.error("Flight controls encountered an error! Attempting to continue...", e);
				
				continue;
			}
		}
		
		running = false;
	}

	/**
	 * Receive fed back flight data to be used with P/PD controllers
	 */
	@Override
	public void onFlightDataReceived(FlightData flightData) {
//		if (flightData!= null)
//			this.flightData = flightData.getFlightData();
	}
	
	public void setIntegrate6DOFEquations(Integrate6DOFEquations simulation) {
		this.simulation = simulation;
	}
	
	/**
	 * Returns thread-safe map containing flight controls data, with {@link FlightControl} as the keys 
	 * 
	 * @return controls
	 */
	public synchronized Map<FlightControl, Double> getFlightControls() {return Collections.unmodifiableMap(controls);}

	/**
	 * Lets other objects know if {@link FlightControls} thread is running
	 * 
	 * @return if {@link FlightControls} thread is running
	 */
	public synchronized boolean isRunning() {return running;}

	/**
	 * Lets other objects request to stop the {@link FlightControls} thread by setting running to false
	 * 
	 * @param running
	 */
	public synchronized void setRunning(boolean running) {this.running = running;}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		for (Map.Entry<FlightControl, Double> entry : controls.entrySet()) {
			sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
		}
		sb.append("\n");
		
		return sb.toString();
	}
}
