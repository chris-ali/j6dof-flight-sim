/*******************************************************************************
 * Copyright (C) 2016-2020 Christopher Ali
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
package com.chrisali.javaflightsim.simulation.setup;

import com.chrisali.javaflightsim.simulation.flightcontrols.ControlParameter;

/**
 * Enums to be used in {@link ControlsConfiguration} to configure key or button presses
 * 
 * @author Christopher
 *
 */
public enum KeyCommand implements ControlParameter {
	ELEVATOR_DOWN 		("Elevator Down", 1),
	ELEVATOR_UP 		("Elevator Up", 0),
	AILERON_LEFT		("Aileron Left", 0),
	AILERON_RIGHT		("Aileron Right", 1),
	RUDDER_LEFT			("Rudder Left", 0),
	RUDDER_RIGHT		("Rudder Right", 1),
	ELEVATOR_TRIM_DOWN 	("Elevator Trim Down", 1),
	ELEVATOR_TRIM_UP 	("Elevator Trim Up", 0),
	AILERON_TRIM_LEFT	("Aileron Trim Left", 0),
	AILERON_TRIM_RIGHT	("Aileron Trim Right", 1),
	RUDDER_TRIM_LEFT	("Rudder Trim Left", 0),
	RUDDER_TRIM_RIGHT	("Rudder Trim Right", 1),
	CENTER_CONTROLS		("Center Flight Controls", 0.5),
	GEAR_UP				("Extend Landing Gear", 1),
	GEAR_DOWN			("Retract Landing Gear", 0),
	GEAR_UP_DOWN		("Landing Gear Extend/Retract", 0.5),
	INCREASE_FLAPS		("Increase Flaps", 1),
	DECREASE_FLAPS		("Decrease Flaps", 0),
	INCREASE_THROTTLE	("Increase Throttle", 1),
	DECREASE_THROTTLE	("Decrease Throttle", 0),
	INCREASE_PROPELLER	("Increase Propeller", 1),
	DECREASE_PROPELLER	("Decrease Propeller", 0),
	INCREASE_MIXTURE	("Increase Mixture", 1),
	DECREASE_MIXTURE	("Decrease Mixture", 0),
	BRAKES				("Apply Brakes", 1),
	EXIT_SIMULATION		("Exit Simulation", 0),
	GENERATE_PLOTS		("Generate Plots", 0),
	PAUSE_UNPAUSE_SIM	("Pause/Unpause Simulation", 0),
	RESET_SIM			("Reset Simulation", 0),
	USE_COCKPIT	  	    ("Use No Panel Cockpit View", 0),
	USE_COCKPIT_2D 		("Use 2D Panel Cockpit View", 0),
	USE_COCKPIT_3D 		("Use 3D Panel Cockpit View", 0),
	USE_CHASE 	   		("Use Chase View", 0),
	USE_FLYBY			("Use Fly-by View", 0);
	
	private String name;
	private final double minimum;
	private final double maximum;
	
	KeyCommand(String name, double value) { 
		this.name = name; 
		this.minimum = value; 
		this.maximum = value; 
	}
	
	@Override
	public double getMinimum() { return minimum; }

	@Override
	public double getMaximum() { return maximum; }
			
	@Override
	public boolean isRelative() { return true;	}

	public String toString() { return name; }
}
