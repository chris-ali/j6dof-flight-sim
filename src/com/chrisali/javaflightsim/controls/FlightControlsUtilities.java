package com.chrisali.javaflightsim.controls;

import java.math.BigDecimal;
import java.util.EnumMap;

public class FlightControlsUtilities {
	// Generates a control doublet in the positive and then negative direction, returning to trim value
	public static EnumMap<FlightControls, Double> makeDoublet(EnumMap<FlightControls, Double> controls,
															  double t,
															  double startTime, 
															  double duration, 
															  double amplitude, 
															  FlightControls controlInput) {
		
		Double shortT = new BigDecimal(t).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
		
		if (shortT == startTime)
			controls.put(controlInput,controls.get(controlInput)+amplitude);
		else if (shortT == startTime+duration)
			controls.put(controlInput,controls.get(controlInput)-2*amplitude);
		else if (shortT == startTime+2*duration)
			controls.put(controlInput,controls.get(controlInput)+amplitude);
		
		return controls;
	}
	
	// Limit control inputs to sensible deflection values
	public static EnumMap<FlightControls, Double> limitControls(EnumMap<FlightControls, Double> controls) {
		// Loop through enum list; if value in EnumMap controls is greater/less than max/min specified in FlightControls enum, 
		// set that EnumMap value to Enum's max/min value
		for (FlightControls flc : FlightControls.values()) {
			if (controls.get(flc) > flc.getMaximum())
				controls.put(flc, flc.getMaximum());
			else if (controls.get(flc) < flc.getMinimum())
				controls.put(flc, flc.getMinimum());		
		}	
		return controls;
	}
}
