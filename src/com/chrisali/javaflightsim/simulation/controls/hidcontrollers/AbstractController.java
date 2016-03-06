package com.chrisali.javaflightsim.simulation.controls.hidcontrollers;

import java.util.ArrayList;
import java.util.EnumMap;

import com.chrisali.javaflightsim.simulation.aero.Aerodynamics;
import com.chrisali.javaflightsim.simulation.controls.FlightControls;
import com.chrisali.javaflightsim.simulation.controls.FlightControlsUtilities;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;

import net.java.games.input.Controller;

/**
 * Contains methods implemented {@link Joystick}, {@link Keyboard} and {@link Mouse} to take controller inputs
 * given by JInput, and convert them to actual control deflections used by the simulation in {@link Integrate6DOFEquations}
 * and {@link Aerodynamics} 
 */
public abstract class AbstractController {
	protected ArrayList<Controller> controllerList;
	
	// Add these trim values to getControlDeflection method call to emulate trim deflections
	protected static double trimElevator = 0.0;
	protected static double trimAileron  = 0.0;
	protected static double trimRudder   = 0.0;
	
	protected abstract void searchForControllers();
	
	protected abstract EnumMap<FlightControls, Double> calculateControllerValues(EnumMap<FlightControls, Double> controls);
	
	/**
	 *  Uses maximum and minimum values defined in {@link FlightControls} to convert normalized 
	 *  joystick axis value to actual control deflection 
	 *  
	 * @param controlType
	 * @param axisValue
	 * @return Actual control deflection
	 */
	protected double calculateControlDeflection(FlightControls controlType, double axisValue) {
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
	public EnumMap<FlightControls, Double> updateFlightControls(EnumMap<FlightControls, Double> controls) {		
		return FlightControlsUtilities.limitControls(calculateControllerValues(controls));
	}
	
}
