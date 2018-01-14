/*******************************************************************************
 * Copyright (C) 2016-2018 Christopher Ali
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
package com.chrisali.javaflightsim.simulation.flightcontrols;

import com.chrisali.javaflightsim.simulation.hidcontrollers.AbstractController;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;

/**
 * Enum that provides the keys for the controls EnumMap in {@link Integrate6DOFEquations}. 
 * Minima and maxima are also defined in this class as constants to be used in HID controller 
 * classes that implement {@link AbstractController}
 */
public enum FlightControl {
	ELEVATOR    ("Elevator", 		Math.toRadians(-25), Math.toRadians(15)), // "Minimum" is up elevator
	AILERON		("Aileron",  		Math.toRadians(-15), Math.toRadians(15)), // "Minimum" is left aileron up
	RUDDER		("Rudder",   		Math.toRadians(-15), Math.toRadians(15)), // "Minimum" is right rudder
	THROTTLE_1	("Throttle 1",	 	0.0, 				 1.0),
	THROTTLE_2	("Throttle 2", 		0.0, 				 1.0),
	THROTTLE_3	("Throttle 3", 		0.0, 				 1.0),
	THROTTLE_4	("Throttle 4", 		0.0, 				 1.0),
	PROPELLER_1	("Propeller 1", 	0.0, 				 1.0),
	PROPELLER_2	("Propeller 2", 	0.0, 				 1.0),
	PROPELLER_3	("Propeller 3", 	0.0, 				 1.0),
	PROPELLER_4	("Propeller 4", 	0.0, 				 1.0),
	MIXTURE_1	("Mixture 1", 		0.0, 				 1.0),
	MIXTURE_2	("Mixture 2",	 	0.0, 				 1.0),
	MIXTURE_3	("Mixture 3", 		0.0, 				 1.0),
	MIXTURE_4	("Mixture 4",	 	0.0, 				 1.0),
	FLAPS		("Flaps", 			0.0, 				 Math.toRadians(30)),
	GEAR		("Gear", 			0.0, 				 1.0),
	BRAKE_L		("Left Brake", 		0.0, 				 1.0),
	BRAKE_R		("Right Brake", 	0.0, 				 1.0);
	
	private final String control;
	private final double minimum;
	private final double maximum;
	
	FlightControl(String control, double minimum, double maximum) {
		this.control = control;
		this.minimum = minimum;
		this.maximum = maximum;
	}
	
	public String toString() {return control;}
	
	public double getMinimum() {return minimum;}
	
	public double getMaximum() {return maximum;}
}
