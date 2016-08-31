package com.chrisali.javaflightsim.simulation.controls;

import java.util.Map;

import com.chrisali.javaflightsim.simulation.setup.IntegrationSetup;
import com.chrisali.javaflightsim.simulation.setup.Options;

/**
 * Contains methods to modify the aircraft's flight controls to generate doublets for dynamic stability analysis,
 * or limiting flight control deflections
 */
public class FlightControlsUtilities {
	
	/**
	 * Main trim values of flight controls to determine default value if doublet input not underway
	 */
	private static Map<FlightControlType, Double> trimControls; 

	/**
	 * Initializes trimControls EnumMap in {@link FlightControlsUtilities}; needs to be called each time controls and
	 * initial conditions are changed so that new trim values can be read from InitialControls.txt
	 */
	public static void init() {trimControls = IntegrationSetup.gatherInitialControls("InitialControls");}
	
	/**
	 * Generates a control doublet in the positive and then negative direction, returning to trim value. The start
	 * time defines when the double should start, the duration indicates how long the control is held in that direction,
	 * and the amplitude the amount of deflection in one direction. controlInput uses {@link FlightControlType} to select
	 * the desired control to use as a doublet 
	 * 
	 * @param controls
	 * @param t
	 * @param startTime
	 * @param duration
	 * @param amplitude
	 * @param controlType
	 * @return flightControls EnumMap 
	 */
	public static Map<FlightControlType, Double> makeDoublet(Map<FlightControlType, Double> controls,
															  double t,
															  double startTime, 
															  double duration, 
															  double amplitude, 
															  FlightControlType controlType) {
		
		if (t > startTime && t < (startTime+duration))
			controls.put(controlType,trimControls.get(controlType)+amplitude);
		else if (t > (startTime+duration) && t < (startTime+(2*duration)))
			controls.put(controlType,trimControls.get(controlType)-amplitude);
		else 
			controls.put(controlType,trimControls.get(controlType));

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
	public static Map<FlightControlType, Double> doubletSeries(Map<FlightControlType, Double> controls, double t) {
		// Update controls with an aileron doublet
		controls = makeDoublet(controls, 
							   t, 
							   10.0, 
							   0.5, 
							   0.035, 
							   FlightControlType.AILERON);
		// Update controls with a rudder doublet
		controls = makeDoublet(controls, 
							   t, 
							   13.0, 
							   0.5, 
							   0.035, 
							   FlightControlType.RUDDER);
		// Update controls with an elevator doublet
		controls = makeDoublet(controls, 
							   t, 
							   50.0, 
							   0.5, 
							   0.035, 
							   FlightControlType.ELEVATOR);
		return controls;
	}
	
	
	/**
	 *  Limit control inputs to sensible deflection values based on the minimum and maximum values defines for 
	 *  each member of {@link FlightControlType}
	 *  
	 * @param map
	 * @return flightControls EnumMap 
	 */
	public static Map<FlightControlType, Double> limitControls(Map<FlightControlType, Double> map) {
		// Loop through enum list; if value in EnumMap controls is greater/less than max/min specified in FlightControls enum, 
		// set that EnumMap value to Enum's max/min value
		for (FlightControlType flc : FlightControlType.values()) {
			if (map.get(flc) > flc.getMaximum())
				map.put(flc, flc.getMaximum());
			else if (map.get(flc) < flc.getMinimum())
				map.put(flc, flc.getMinimum());		
		}	
		return map;
	}
}
