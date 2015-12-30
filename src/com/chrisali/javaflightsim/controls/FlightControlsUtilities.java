package com.chrisali.javaflightsim.controls;

import java.math.BigDecimal;
import java.util.EnumMap;

public class FlightControlsUtilities {
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
}
