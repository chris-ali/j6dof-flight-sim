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

import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chrisali.javaflightsim.interfaces.SimulationController;
import com.chrisali.javaflightsim.interfaces.Steppable;
import com.chrisali.javaflightsim.simulation.datatransfer.FlightData;
import com.chrisali.javaflightsim.simulation.datatransfer.FlightDataListener;
import com.chrisali.javaflightsim.simulation.flightcontrols.analysis.AnalysisControls;
import com.chrisali.javaflightsim.simulation.inputdevices.AbstractDevice;
import com.chrisali.javaflightsim.simulation.inputdevices.Joystick;
import com.chrisali.javaflightsim.simulation.inputdevices.JoystickHandler;
import com.chrisali.javaflightsim.simulation.inputdevices.Keyboard;
import com.chrisali.javaflightsim.simulation.inputdevices.KeyboardHandler;
import com.chrisali.javaflightsim.simulation.inputdevices.Mouse;
import com.chrisali.javaflightsim.simulation.inputdevices.MouseHandler;
import com.chrisali.javaflightsim.simulation.setup.ControlsConfiguration;
import com.chrisali.javaflightsim.simulation.setup.Options;
import com.chrisali.javaflightsim.simulation.setup.SimulationConfiguration;
import com.chrisali.javaflightsim.simulation.utilities.FileUtilities;

/**
 * Handles flight controls actuated by human interface devices, such as {@link Joystick}, {@link Keyboard}, 
 * {@link Mouse} or by P/PD controllers such as autopilots and stability augmentation sytems. Also contains 
 * method to inject doublets into controls when simulation is run as analysis. Uses {@link FlightDataListener} 
 * to feed back {@link FlightData} to use in P/PD controllers
 * 
 * @author Christopher Ali
 *
 */
public class FlightControlsStateManager implements Steppable {

	private static final Logger logger = LogManager.getLogger(FlightControlsStateManager.class);

	private FlightControlsState controlsState;
	
	private EnumSet<Options> options;
	
	private AtomicInteger simTimeMS;
	
	private AbstractDevice hidController;
	private Keyboard hidKeyboard;
	private AnalysisControls analysisControls;

	// Visitors are used to poll devices for data and handle the results in an actuator object within the visitor
	private JoystickHandler joystickVisitor;
    private KeyboardHandler keyboardVisitor;
    private MouseHandler mouseVisitor;
	
	public FlightControlsStateManager(SimulationController simController, AtomicInteger simTimeMS) {
		logger.debug("Initializing flight controls...");
		
		SimulationConfiguration simConfig = simController.getConfiguration();
		options = simConfig.getSimulationOptions();
		this.simTimeMS = simTimeMS;
		
		SimEvents.init(simController);

		ControlsConfiguration controlsConfig = FileUtilities.readControlsConfiguration();
		analysisControls = FileUtilities.readAnalysisControls();
		
		controlsState = new FlightControlsState(simConfig);

		ControlParameterActuator actuator = new FlightControlActuator(simConfig, controlsState);

		// Use controllers for pilot in loop simulation if ANALYSIS_MODE not enabled 
		if (!options.contains(Options.ANALYSIS_MODE)) {
			if (options.contains(Options.USE_JOYSTICK) || options.contains(Options.USE_CH_CONTROLS)) {
				logger.debug("Joystick controller selected");
				hidController = new Joystick();
				joystickVisitor = new JoystickHandler(controlsConfig.getJoystickAssignments(), actuator);
			}
			else if (options.contains(Options.USE_MOUSE)){
				logger.debug("Mouse controller selected");
				hidController = new Mouse();
				mouseVisitor = new MouseHandler(controlsState, actuator);
			}
			
			hidKeyboard = new Keyboard();
			keyboardVisitor = new KeyboardHandler(controlsConfig.getKeyboardAssignments(), actuator);
		}
	}
	
	@Override
	public void step() {
		try {
			// if not running in analysis mode, controls and options are updated with pilot input
			// otherwise, controls updated using generated doublets
			if (!options.contains(Options.ANALYSIS_MODE)) {
				if (hidController != null) 
					hidController.collectControlDeviceValues(joystickVisitor != null ? joystickVisitor : mouseVisitor);
		
				if (hidKeyboard != null)
					hidKeyboard.collectControlDeviceValues(keyboardVisitor);
			} else {
				analysisControls.updateFlightControls(simTimeMS, controlsState);
			}
			
			limitControls(controlsState);
		} catch (Exception e) {
			logger.error("Flight controls encountered an error!", e);
		}
	}
	
	@Override
	public boolean canStepNow(int timeMS) {
		return timeMS % 1 == 0;
	}
		
	public void setSimTimeMS(AtomicInteger simTimeMS) { this.simTimeMS = simTimeMS;	}

	public AtomicInteger getSimTimeMS() { return simTimeMS;	}
	
	public FlightControlsState getControlsState() { return controlsState; }

	/**
	 *  Limit control inputs to sensible deflection values based on the minimum and maximum values defined for 
	 *  each member of {@link FlightControl}
	 *  
	 * @param controlsState 
	 */
	private void limitControls(FlightControlsState controlsState) {		
		for (FlightControl flc : FlightControl.values()) {
            if (controlsState.get(flc) > flc.getMaximum())
                controlsState.set(flc, flc.getMaximum());
            else if (controlsState.get(flc) < flc.getMinimum())
                controlsState.set(flc, flc.getMinimum());
        }
	}
}
