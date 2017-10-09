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

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chrisali.javaflightsim.simulation.datatransfer.FlightData;
import com.chrisali.javaflightsim.simulation.datatransfer.FlightDataListener;
import com.chrisali.javaflightsim.simulation.hidcontrollers.AbstractController;
import com.chrisali.javaflightsim.simulation.hidcontrollers.Events;
import com.chrisali.javaflightsim.simulation.hidcontrollers.Joystick;
import com.chrisali.javaflightsim.simulation.hidcontrollers.Keyboard;
import com.chrisali.javaflightsim.simulation.hidcontrollers.Mouse;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.interfaces.SimulationController;
import com.chrisali.javaflightsim.simulation.interfaces.Steppable;
import com.chrisali.javaflightsim.simulation.setup.IntegratorConfig;
import com.chrisali.javaflightsim.simulation.setup.Options;
import com.chrisali.javaflightsim.simulation.setup.SimulationConfiguration;

/**
 * Handles flight controls actuated by human interface devices, such as {@link Joystick}, {@link Keyboard}, 
 * {@link Mouse} or by P/PD controllers such as autopilots and stability augmentation sytems. Also contains 
 * method to inject doublets into controls when simulation is run as analysis. Uses {@link FlightDataListener} 
 * to feed back {@link FlightData} to use in P/PD controllers
 * 
 * @author Christopher Ali
 *
 */
public class FlightControls implements Steppable, FlightDataListener {

	private static final Logger logger = LogManager.getLogger(FlightControls.class);
	
	private Map<FlightControl, Double> flightControls;
	private Map<FlightControl, Double> trimflightControls;
	private EnumSet<Options> options;
	//private Map<FlightDataType, Double> flightData;
	
	private SimulationController simController;
	private SimulationConfiguration configuration; 
	
	private AbstractController hidController;
	private Keyboard hidKeyboard;
	//private AnalysisControls analysisControls;
	
	/**
	 * Constructor for {@link FlightControls}; {@link SimulationConfiguration} argument to initialize {@link IntegratorConfig} 
	 * EnumMap, the {@link Options} EnumSet, as well as to update simulation options and call simulation methods
	 * 
	 * @param simController
	 */
	public FlightControls(SimulationController simController) {
		logger.debug("Initializing flight controls...");
				
		this.simController = simController;
		
		configuration = simController.getConfiguration();
		flightControls = new EnumMap<FlightControl, Double>(configuration.getInitialControls());
		trimflightControls = configuration.getInitialControls();
		options = configuration.getSimulationOptions();
		//analysisControls = FileUtilities.readAnalysisControls();
				
		// initializes static EnumMap that contains trim values of controls for doublets 
		DoubletGenerator.init();
		Events.init(configuration);

		// Use controllers for pilot in loop simulation if ANALYSIS_MODE not enabled 
		if (!options.contains(Options.ANALYSIS_MODE)) {
			if (options.contains(Options.USE_JOYSTICK) || options.contains(Options.USE_CH_CONTROLS)) {
				logger.debug("Joystick controller selected");
				hidController = new Joystick(flightControls, simController);
			}
			else if (options.contains(Options.USE_MOUSE)){
				logger.debug("Mouse controller selected");
				hidController = new Mouse(flightControls, simController);
			}
			
			hidKeyboard = new Keyboard(flightControls, simController);
		}
	}
	
	@Override
	public void step() {
		try {
			Integrate6DOFEquations simulation = simController.getSimulation();
			
			// if not running in analysis mode, controls and options are updated with pilot input
			// otherwise, controls updated using generated doublets
			if (!options.contains(Options.ANALYSIS_MODE) && simulation.isRunning()) {
				if (hidController != null) 
					hidController.calculateControllerValues(flightControls);
				
				if (hidKeyboard != null)
					hidKeyboard.calculateControllerValues(flightControls);
			} else {
				DoubletGenerator.doubletSeries(flightControls, simulation.getTime());
			}
			
			limitControls(flightControls);
		} catch (Exception e) {
			logger.error("Flight controls encountered an error!", e);
		}
	}
	
	@Override
	public boolean canStepNow(int time) {
		return time % 100 == 0;
	}
	
	/**
	 * Resets flightControls back to initial trim values
	 */
	public void reset() {
		for (Map.Entry<FlightControl, Double> entry : flightControls.entrySet())
			flightControls.put(entry.getKey(), trimflightControls.get(entry.getKey()));
	}
	
	/**
	 *  Limit control inputs to sensible deflection values based on the minimum and maximum values defined for 
	 *  each member of {@link FlightControl}
	 *  
	 * @param controls 
	 */
	public void limitControls(Map<FlightControl, Double> controls) {		
		// Loop through enum values; if value in EnumMap controls is greater/less than max/min specified in FlightControls enum, 
		// set that EnumMap value to Enum's max/min value
		for (FlightControl flc : FlightControl.values()) {
			if (controls.get(flc) > flc.getMaximum())
				controls.put(flc, flc.getMaximum());
			else if (controls.get(flc) < flc.getMinimum())
				controls.put(flc, flc.getMinimum());		
		}
	}

	/**
	 * Receive fed back flight data to be used with P/PD controllers
	 */
	@Override
	public void onFlightDataReceived(FlightData flightData) {
//		if (flightData!= null)
//			this.flightData = flightData.getFlightData();
	}
	
	public Map<FlightControl, Double> getFlightControls() { return flightControls; }

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		for (Map.Entry<FlightControl, Double> entry : flightControls.entrySet()) {
			sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
		}
		sb.append("\n");
		
		return sb.toString();
	}
}
