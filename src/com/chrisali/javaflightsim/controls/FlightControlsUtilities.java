package com.chrisali.javaflightsim.controls;

import java.math.BigDecimal;
import java.util.EnumMap;

import com.chrisali.javaflightsim.setup.Options;

/**
 * Contains methods to modify the aircraft's flight controls to generate doublets for dynamic stability analysis,
 * or limiting flight control deflections
 */
public class FlightControlsUtilities {
	/**
	 * Generates a control doublet in the positive and then negative direction, returning to trim value. The start
	 * time defines when the double should start, the duration indicates how long the control is held in that direction,
	 * and the amplitude the amount of deflection in one direction. controlInput uses {@link FlightControls} to select
	 * the desired control to use as a doublet 
	 * 
	 * @param controls
	 * @param t
	 * @param startTime
	 * @param duration
	 * @param amplitude
	 * @param controlInput
	 * @return flightControls EnumMap 
	 */
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
	
	/**
	 *  Creates a series of doublets (aileron, rudder and then elevator) using the makeDoublet methods. It is used
	 *  when the simulation is set to {@link Options#ANALYSIS_MODE} to examine the transient dynamic response of 
	 *  the aircraft in the simulation
	 *  
	 * @param controls
	 * @param t
	 * @return flightControls EnumMap 
	 */
	public static EnumMap<FlightControls, Double> doubletSeries(EnumMap<FlightControls, Double> controls, double t) {
		// Update controls with an aileron doublet
		controls = FlightControlsUtilities.makeDoublet(controls, 
													   t, 
													   10, 
													   0.5, 
													   0.035, 
													   FlightControls.AILERON);
		// Update controls with a rudder doublet
		controls = FlightControlsUtilities.makeDoublet(controls, 
													   t, 
													   13, 
													   0.5, 
													   0.035, 
													   FlightControls.RUDDER);
		// Update controls with an elevator doublet
		controls = FlightControlsUtilities.makeDoublet(controls, 
													   t, 
													   50, 
													   0.5, 
													   0.035, 
													   FlightControls.ELEVATOR);
		return controls;
	}
	
	
	/**
	 *  Limit control inputs to sensible deflection values based on the minimum and maximum values defines for 
	 *  each member of {@link FlightControls}
	 *  
	 * @param controls
	 * @return flightControls EnumMap 
	 */
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
