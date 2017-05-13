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

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chrisali.javaflightsim.simulation.aero.Aerodynamics;
import com.chrisali.javaflightsim.simulation.flightcontrols.FlightControlType;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.setup.IntegrationSetup;
import com.chrisali.javaflightsim.simulation.setup.IntegratorConfig;
import com.chrisali.javaflightsim.simulation.utilities.FlightControlsUtilities;
import com.chrisali.javaflightsim.simulation.utilities.SimFiles;

import net.java.games.input.Controller;

/**
 * Contains methods implemented {@link Joystick}, {@link Keyboard} and {@link Mouse} to take controller inputs
 * given by JInput, and convert them to actual control deflections used by the simulation in {@link Integrate6DOFEquations}
 * and {@link Aerodynamics} 
 */
public abstract class AbstractController {
	
	protected static final Logger logger = LogManager.getLogger(AbstractController.class);
	
	protected ArrayList<Controller> controllerList;

	// Gets the frame time DT from IntegratorConfig.txt
	protected double dt = IntegrationSetup.gatherIntegratorConfig(SimFiles.INTEGRATOR_CONFIG.toString()).get(IntegratorConfig.DT);
	
	// Add these trim values to getControlDeflection method call to emulate trim deflections
	protected static double trimElevator = 0.0;
	protected static double trimAileron  = 0.0;
	protected static double trimRudder   = 0.0;
	
	// Flaps deflection
	protected static double flaps   	 = 0.0;

	protected abstract void searchForControllers();
	
	protected abstract Map<FlightControlType, Double> calculateControllerValues(Map<FlightControlType, Double> controls);
	
	/**
	 * Standardizes rate of control deflection of keyboard and joystick button inputs regardless of the 
	 * simulation update rate based on the {@link FlightControlType} argument provided and the 
	 * 
	 * @param type
	 */
	protected double getDeflectionRate(FlightControlType type) {
		switch (type) {
		case AILERON:
		case ELEVATOR:
		case RUDDER:
			return 0.12 * dt;
		case THROTTLE_1:
		case THROTTLE_2:
		case THROTTLE_3:
		case THROTTLE_4:
		case PROPELLER_1:
		case PROPELLER_2:
		case PROPELLER_3:
		case PROPELLER_4:
		case MIXTURE_1:
		case MIXTURE_2:
		case MIXTURE_3:
		case MIXTURE_4:
			return 0.5 * dt;
		case FLAPS:
			return 0.15 * dt;
		default:
			return 0;
		}
	}
		
	/**
	 *  Uses maximum and minimum values defined in {@link FlightControlType} to convert normalized 
	 *  joystick axis value to actual control deflection 
	 *  
	 * @param controlType
	 * @param axisValue
	 * @return Actual control deflection
	 */
	protected double calculateControlDeflection(FlightControlType controlType, double axisValue) {
		// Calculate positive and negative slope
		// (elevator has different values for positive/negative max)
		if (axisValue <= 0) 
			return (controlType.getMaximum()*Math.abs(axisValue));
		else
			return (controlType.getMinimum()*axisValue);
	}
	
	/**
	 * Squares a value without removing its sign if negative
	 * 
	 * @param value
	 * @return value squared that retains its original sign
	 */
	protected double negativeSquare(double value) {
		if (value < 0)
			return -(Math.pow(value, 2));
		else
			return Math.pow(value, 2);
	}
	
	/**
	 *  Updates values for controls in controls EnumMap, limiting their max/min via limitControls method
	 * @param controls
	 * @return flightControls EnumMap limited by {@link FlightControlsUtilities#limitControls(EnumMap)}
	 */
	public Map<FlightControlType, Double> updateFlightControls(Map<FlightControlType, Double> controls) {		
		return FlightControlsUtilities.limitControls(calculateControllerValues(controls));
	}
	
}
