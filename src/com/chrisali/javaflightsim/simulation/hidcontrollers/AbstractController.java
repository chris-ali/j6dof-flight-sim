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
package com.chrisali.javaflightsim.simulation.hidcontrollers;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chrisali.javaflightsim.simulation.aircraft.Aerodynamics;
import com.chrisali.javaflightsim.simulation.flightcontrols.Events;
import com.chrisali.javaflightsim.simulation.flightcontrols.FlightControl;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.setup.KeyCommand;

import net.java.games.input.Controller;

/**
 * Contains methods implemented {@link Joystick}, {@link Keyboard} and {@link Mouse} to take controller inputs
 * given by JInput, and convert them to actual control deflections used by the simulation in {@link Integrate6DOFEquations}
 * and {@link Aerodynamics} 
 */
public abstract class AbstractController {
	
	protected static final Logger logger = LogManager.getLogger(AbstractController.class);
	
	protected List<Controller> controllerList;

	public abstract void searchForControllers();
	
	public abstract Map<FlightControl, Double> calculateControllerValues(Map<FlightControl, Double> controls);
		
	/**
	 *  Limit control inputs to sensible deflection values based on the minimum and maximum values defined for 
	 *  each member of {@link FlightControl}
	 *  
	 * @param controls
	 * @return flightControls EnumMap 
	 */
	protected Map<FlightControl, Double> limitControls(Map<FlightControl, Double> controls) {		
		// Loop through enum values; if value in EnumMap controls is greater/less than max/min specified in FlightControls enum, 
		// set that EnumMap value to Enum's max/min value
		for (FlightControl flc : FlightControl.values()) {
			if (controls.get(flc) > flc.getMaximum())
				controls.put(flc, flc.getMaximum());
			else if (controls.get(flc) < flc.getMinimum())
				controls.put(flc, flc.getMinimum());		
		}
		
		return controls;
	}
	
	/**
	 * Given a {@link KeyCommand}, call a flight control event method defined in {@link Events}
	 * 
	 * @param command
	 * @param controls
	 * @param componentPollData
	 */
	public void executeKeyButtonEventForCommand(KeyCommand command, Map<FlightControl, Double> controls, float componentPollData) {
		switch (command) {
		case AILERON_LEFT:
			Events.aileronLeft(controls);
			break;
		case AILERON_RIGHT:
			Events.aileronRight(controls);
			break;
		case AILERON_TRIM_LEFT:
			Events.aileronTrimLeft();
			break;
		case AILERON_TRIM_RIGHT:
			Events.aileronTrimRight();
			break;
		case BRAKES:
			Events.brakeLeft(controls, FlightControl.BRAKE_L.getMaximum());
			Events.brakeRight(controls, FlightControl.BRAKE_R.getMaximum());
			break;
		case CENTER_CONTROLS:
			Events.centerControls(controls);
			break;
		case DECREASE_FLAPS:
			Events.retractFlaps(controls);
			break;
		case DECREASE_MIXTURE:
			break;
		case DECREASE_PROPELLER:
			break;
		case DECREASE_THROTTLE:
			Events.decreaseThrottle(controls);
			break;
		case ELEVATOR_DOWN:
			Events.elevatorDown(controls);
			break;
		case ELEVATOR_UP:
			Events.elevatorUp(controls);
			break;
		case ELEVATOR_TRIM_DOWN:
			Events.elevatorTrimDown();
			break;
		case ELEVATOR_TRIM_UP:
			Events.elevatorTrimUp();
			break;
		case EXIT_SIMULATION:
			break;
		case GEAR_UP_DOWN:
			Events.cycleGear(controls, componentPollData == 1.0f);
			break;
		case GEAR_DOWN:
			Events.extendGear(controls);
			break;
		case GEAR_UP:
			Events.retractGear(controls);
			break;
		case GENERATE_PLOTS:
			break;
		case INCREASE_FLAPS:
			Events.extendFlaps(controls);
			break;
		case INCREASE_MIXTURE:
			break;
		case INCREASE_PROPELLER:
			break;
		case INCREASE_THROTTLE:
			Events.increaseThrottle(controls);
			break;
		case PAUSE_UNPAUSE_SIM:
			break;
		case RESET_SIM:
			break;
		case RUDDER_LEFT:
			Events.rudderLeft(controls);
			break;
		case RUDDER_RIGHT:
			Events.rudderRight(controls);
			break;
		case RUDDER_TRIM_LEFT:
			Events.rudderTrimLeft();
			break;
		case RUDDER_TRIM_RIGHT:
			Events.rudderRight(controls);
			break;
		default:
			break;
		}
	}
}
