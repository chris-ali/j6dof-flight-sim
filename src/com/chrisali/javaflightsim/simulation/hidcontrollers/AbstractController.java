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
package com.chrisali.javaflightsim.simulation.hidcontrollers;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chrisali.javaflightsim.interfaces.SimulationController;
import com.chrisali.javaflightsim.simulation.aircraft.Aerodynamics;
import com.chrisali.javaflightsim.simulation.flightcontrols.FlightControl;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.setup.ControlsConfiguration;
import com.chrisali.javaflightsim.simulation.setup.KeyCommand;
import com.chrisali.javaflightsim.simulation.setup.Options;

import net.java.games.input.Controller;

/**
 * Contains methods implemented {@link Joystick}, {@link Keyboard} and {@link Mouse} to take controller inputs
 * given by JInput, and convert them to actual control deflections used by the simulation in {@link Integrate6DOFEquations}
 * and {@link Aerodynamics} 
 */
public abstract class AbstractController {
	
	protected static final Logger logger = LogManager.getLogger(AbstractController.class);
	
	protected Set<Options> options;
	
	protected SimulationController simController;
	
	protected ControlsConfiguration controlsConfig;
	
	protected Map<FlightControl, Double> flightControls;
	
	protected List<Controller> controllerList;

	public abstract void searchForControllers();
	
	public abstract void calculateControllerValues(Map<FlightControl, Double> flightControls);
		
	/**
	 * Given a {@link KeyCommand}, call a flight control event method defined in {@link Events}
	 * 
	 * @param command
	 * @param isPressed
	 */
	protected void executeKeyButtonEventForCommand(KeyCommand command, boolean isPressed) {
		switch (command) {
		case AILERON_LEFT:
			if (isPressed) Events.aileronLeft(flightControls);
			break;
		case AILERON_RIGHT:
			if (isPressed) Events.aileronRight(flightControls);
			break;
		case AILERON_TRIM_LEFT:
			if (isPressed) Events.aileronTrimLeft();
			break;
		case AILERON_TRIM_RIGHT:
			if (isPressed) Events.aileronTrimRight();
			break;
		case BRAKES:
			Events.brakeLeft(flightControls, FlightControl.BRAKE_L.getMaximum());
			Events.brakeRight(flightControls, FlightControl.BRAKE_R.getMaximum());
			break;
		case CENTER_CONTROLS:
			if (isPressed) Events.centerControls(flightControls);
			break;
		case DECREASE_FLAPS:
			if (isPressed) Events.retractFlaps(flightControls);
			break;
		case DECREASE_MIXTURE:
			break;
		case DECREASE_PROPELLER:
			break;
		case DECREASE_THROTTLE:
			if (isPressed) Events.decreaseThrottle(flightControls);
			break;
		case ELEVATOR_DOWN:
			if (isPressed) Events.elevatorDown(flightControls);
			break;
		case ELEVATOR_UP:
			if (isPressed) Events.elevatorUp(flightControls);
			break;
		case ELEVATOR_TRIM_DOWN:
			if (isPressed) Events.elevatorTrimDown();
			break;
		case ELEVATOR_TRIM_UP:
			if (isPressed) Events.elevatorTrimUp();
			break;
		case EXIT_SIMULATION:
			if (isPressed) Events.stopSimulation(simController);
			break;
		case GEAR_UP_DOWN:
			Events.cycleGear(flightControls, isPressed);
			break;
		case GEAR_DOWN:
			if (isPressed) Events.extendGear(flightControls);
			break;
		case GEAR_UP:
			if (isPressed) Events.retractGear(flightControls);
			break;
		case GENERATE_PLOTS:
			if (isPressed) Events.plotSimulation(simController);
			break;
		case INCREASE_FLAPS:
			if (isPressed) Events.extendFlaps(flightControls);
			break;
		case INCREASE_MIXTURE:
			break;
		case INCREASE_PROPELLER:
			break;
		case INCREASE_THROTTLE:
			if (isPressed) Events.increaseThrottle(flightControls);
			break;
		case PAUSE_UNPAUSE_SIM:
			Events.pauseSimulation(options, isPressed);
			break;
		case RESET_SIM:
			Events.resetSimulation(options, isPressed);
			break;
		case RUDDER_LEFT:
			if (isPressed) Events.rudderLeft(flightControls);
			break;
		case RUDDER_RIGHT:
			if (isPressed) Events.rudderRight(flightControls);
			break;
		case RUDDER_TRIM_LEFT:
			if (isPressed) Events.rudderTrimLeft();
			break;
		case RUDDER_TRIM_RIGHT:
			if (isPressed) Events.rudderRight(flightControls);
			break;
		default:
			break;
		}
	}
	
	protected void executeAxisEventForCommand(FlightControl control, float axisValue) {
		switch (control) {
		case AILERON:
			Events.aileron(flightControls, axisValue);
			break;
		case BRAKE_L:
			Events.brakeLeft(flightControls, axisValue);
			break;
		case BRAKE_R:
			Events.brakeRight(flightControls, axisValue);
			break;
		case ELEVATOR:
			Events.elevator(flightControls, axisValue);
			break;
		case FLAPS:
			break;
		case GEAR:
			break;
		case MIXTURE_1:
			Events.mixture1(flightControls, axisValue);
			break;
		case MIXTURE_2:
			Events.mixture2(flightControls, axisValue);
			break;
		case MIXTURE_3:
			break;
		case MIXTURE_4:
			break;
		case PROPELLER_1:
			Events.propeller1(flightControls, axisValue);
			break;
		case PROPELLER_2:
			Events.propeller2(flightControls, axisValue);
			break;
		case PROPELLER_3:
			break;
		case PROPELLER_4:
			break;
		case RUDDER:
			Events.rudder(flightControls, axisValue);
			break;
		case THROTTLE_1:
			Events.throttle1(flightControls, axisValue);
			break;
		case THROTTLE_2:
			Events.throttle2(flightControls, axisValue);
			break;
		case THROTTLE_3:
			break;
		case THROTTLE_4:
			break;
		default:
			break;
		}
	}
}
