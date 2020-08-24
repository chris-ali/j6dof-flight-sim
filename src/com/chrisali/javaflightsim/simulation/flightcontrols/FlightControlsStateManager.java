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
package com.chrisali.javaflightsim.simulation.flightcontrols;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chrisali.javaflightsim.interfaces.SimulationController;
import com.chrisali.javaflightsim.interfaces.Steppable;
import com.chrisali.javaflightsim.simulation.datatransfer.InputData;
import com.chrisali.javaflightsim.simulation.datatransfer.InputDataListener;
import com.chrisali.javaflightsim.simulation.flightcontrols.analysis.AnalysisControlInput;
import com.chrisali.javaflightsim.simulation.flightcontrols.analysis.AnalysisControls;
import com.chrisali.javaflightsim.simulation.setup.KeyCommand;
import com.chrisali.javaflightsim.simulation.setup.Options;
import com.chrisali.javaflightsim.simulation.setup.SimulationConfiguration;
import com.chrisali.javaflightsim.simulation.utilities.FileUtilities;

/**
 * Handles flight controls actuated by external human interface devices that provide their input values using 
 * {@link InputDataListener}. Also contains {@link AnalysisControlInput} functionality when simulation runs 
 * in Analysis Mode
 * 
 * @author Christopher Ali
 *
 */
public class FlightControlsStateManager implements Steppable, InputDataListener {

	private static final Logger logger = LogManager.getLogger(FlightControlsStateManager.class);

	private FlightControlsState controlsState;
	private AnalysisControls analysisControls;
	private ControlParameterActuator actuator;

	private EnumSet<Options> options;
	private AtomicInteger simTimeMS;

	public FlightControlsStateManager(SimulationController simController, AtomicInteger simTimeMS) {
		logger.info("Initializing flight controls...");

		SimEvents.init(simController);

		SimulationConfiguration simConfig = simController.getConfiguration();
		options = simConfig.getSimulationOptions();
		controlsState = new FlightControlsState(simConfig);
		actuator = new FlightControlActuator(simConfig, controlsState);

		this.simTimeMS = simTimeMS;

		analysisControls = FileUtilities.readAnalysisControls();
		if (analysisControls != null) {
			logger.info(analysisControls.getAnalysisInputs().size() + " analysis flight control inputs found:");
			logger.info(analysisControls.toString());
		}
	}

	@Override
	public void step() {
		try {
			if (options.contains(Options.ANALYSIS_MODE))
				analysisControls.updateFlightControls(simTimeMS, actuator);
			
			limitControls(controlsState);
		} catch (Exception e) {
			logger.error("Flight controls encountered an error!", e);
		}
	}

	@Override
	public void onInputDataReceived(InputData inputData) {
		try {
			List<KeyCommand> keyCommands = Collections.synchronizedList(inputData.getKeyCommands());
			Map<FlightControl, Float> joystickInputs = Collections.synchronizedMap(inputData.getJoystickInputs());

			synchronized (keyCommands) {
				Iterator<KeyCommand> i = keyCommands.iterator();
				while (i.hasNext()) {
					actuator.handleParameterChange(i.next(), 1.0f);
				}
			}

			Set<FlightControl> s = joystickInputs.keySet();
			synchronized (joystickInputs) {
				Iterator<FlightControl> i = s.iterator();
				while (i.hasNext()) {
					FlightControl axis = i.next();
					actuator.handleParameterChange(axis, joystickInputs.get(axis));
				}
			}
		} catch (Exception e) {
			logger.error("Flight controls encountered an error!", e);
		}
	}

	@Override
	public boolean canStepNow(int timeMS) {
		return timeMS % 1 == 0;
	}

	public void setSimTimeMS(AtomicInteger simTimeMS) {
		this.simTimeMS = simTimeMS;
	}

	public AtomicInteger getSimTimeMS() {
		return simTimeMS;
	}

	public FlightControlsState getControlsState() {
		return controlsState;
	}

	/**
	 * Limit control inputs to sensible deflection values based on the minimum and
	 * maximum values defined for each member of {@link FlightControl}
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
