package com.chrisali.javaflightsim.controls;

import java.math.BigDecimal;

public class FlightControlsUtilities {
	public static double[] makeDoublet(double[] controls,
									   double t,
									   double startTime, 
									   double duration, 
									   double amplitude, 
									   FlightControlType controlInput) {
		
		Double shortT = new BigDecimal(t).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
		
		if (shortT == startTime) {
			switch (controlInput) {
				case ELEVATOR:
					controls[0] += amplitude;
					break;
				case AILERON:
					controls[1] += amplitude;
					break;
				case RUDDER:
					controls[2] += amplitude;
					break;
				default:
					break;
			}
		}
		else if (shortT == startTime+duration) {
			switch (controlInput) {
				case ELEVATOR:
					controls[0] -= 2*amplitude;
					break;
				case AILERON:
					controls[1] -= 2*amplitude;
					break;
				case RUDDER:
					controls[2] -= 2*amplitude;
					break;
				default:
					break;
			}
		}
		else if (shortT == startTime+2*duration) {
			switch (controlInput) {
				case ELEVATOR:
					controls[0] += amplitude;
					break;
				case AILERON:
					controls[1] += amplitude;
					break;
				case RUDDER:
					controls[2] += amplitude;
					break;
				default:
					break;
			}
		}
		
		return controls;
	}
}
