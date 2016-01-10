package com.chrisali.javaflightsim.controls;

import java.util.ArrayList;
import java.util.EnumMap;

import net.java.games.input.Controller;

public abstract class SimulationController {
	protected ArrayList<Controller> controllerList;
	
	// Add these trim values to getControlDeflection method call to emulate trim deflections
	protected static double trimElevator = 0.0;
	protected static double trimAileron  = 0.0;
	protected static double trimRudder   = 0.0;
	
	protected abstract void searchForControllers();
	
	protected abstract EnumMap<FlightControls, Double> calculateControllerValues(EnumMap<FlightControls, Double> controls);
	
	// Use maximum and minimum values defined in FlightControls enum to convert normalized joystick
	// axis value to actual control deflection 
	protected double calculateControlDeflection(FlightControls controlType, double axisValue) {
		// Calculate positive and negative slope
		// (elevator has different values for positive/negative max)
		if (axisValue <= 0) 
			return (controlType.getMaximum()*Math.abs(axisValue));
		else
			return (controlType.getMinimum()*axisValue);
	}
	
	// Updates values for controls in controls EnumMap, limiting their max/min via limitControls method
	public EnumMap<FlightControls, Double> updateFlightControls(EnumMap<FlightControls, Double> controls) {		
		return FlightControlsUtilities.limitControls(calculateControllerValues(controls));
	}
	
}
