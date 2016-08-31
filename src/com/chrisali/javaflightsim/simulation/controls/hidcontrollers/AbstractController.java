package com.chrisali.javaflightsim.simulation.controls.hidcontrollers;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

import com.chrisali.javaflightsim.simulation.aero.Aerodynamics;
import com.chrisali.javaflightsim.simulation.controls.FlightControlType;
import com.chrisali.javaflightsim.simulation.controls.FlightControlsUtilities;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.setup.IntegrationSetup;
import com.chrisali.javaflightsim.simulation.setup.IntegratorConfig;

import net.java.games.input.Controller;

/**
 * Contains methods implemented {@link Joystick}, {@link Keyboard} and {@link Mouse} to take controller inputs
 * given by JInput, and convert them to actual control deflections used by the simulation in {@link Integrate6DOFEquations}
 * and {@link Aerodynamics} 
 */
public abstract class AbstractController {
	protected ArrayList<Controller> controllerList;

	// Gets the frame time DT from IntegratorConfig.txt
	protected double dt = IntegrationSetup.gatherIntegratorConfig("IntegratorConfig").get(IntegratorConfig.DT);
	
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
