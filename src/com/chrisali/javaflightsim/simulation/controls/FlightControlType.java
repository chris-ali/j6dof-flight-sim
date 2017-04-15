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
package com.chrisali.javaflightsim.simulation.controls;

import com.chrisali.javaflightsim.simulation.controls.hidcontrollers.AbstractController;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.setup.IntegrationSetup;

/**
 * Enum that provides the keys for the controls EnumMap in {@link Integrate6DOFEquations}. Also used to parse 
 * InitialControls text file in {@link IntegrationSetup} class. Minima and maxima are also defined in this class 
 * as constants to be used in HID controller classes that implement {@link AbstractController}
 */
public enum FlightControlType {
	ELEVATOR    ("elevator", 		Math.toRadians(-25), Math.toRadians(15)), // "Minimum" is up elevator
	AILERON		("aileron",  		Math.toRadians(-15), Math.toRadians(15)), // "Minimum" is left aileron up
	RUDDER		("rudder",   		Math.toRadians(-15), Math.toRadians(15)), // "Minimum" is right rudder
	THROTTLE_1	("throttle_1",	 	0.0, 				 1.0),
	THROTTLE_2	("throttle_2", 		0.0, 				 1.0),
	THROTTLE_3	("throttle_3", 		0.0, 				 1.0),
	THROTTLE_4	("throttle_4", 		0.0, 				 1.0),
	PROPELLER_1	("propeller_1", 	0.0, 				 1.0),
	PROPELLER_2	("propeller_2", 	0.0, 				 1.0),
	PROPELLER_3	("propeller_3", 	0.0, 				 1.0),
	PROPELLER_4	("propeller_4", 	0.0, 				 1.0),
	MIXTURE_1	("mixture_1", 		0.0, 				 1.0),
	MIXTURE_2	("mixture_2",	 	0.0, 				 1.0),
	MIXTURE_3	("mixture_3", 		0.0, 				 1.0),
	MIXTURE_4	("mixture_4",	 	0.0, 				 1.0),
	FLAPS		("flaps", 			0.0, 				 Math.toRadians(30)),
	GEAR		("gear", 			0.0, 				 1.0),
	BRAKE_L		("leftBrake", 		0.0, 				 1.0),
	BRAKE_R		("rightBrake", 		0.0, 				 1.0);
	
	private final String control;
	private final double minimum;
	private final double maximum;
	
	FlightControlType(String control, double minimum, double maximum) {
		this.control = control;
		this.minimum = minimum;
		this.maximum = maximum;
	}
	
	public String toString() {return control;}
	
	public double getMinimum() {return minimum;}
	
	public double getMaximum() {return maximum;}
}
