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
package com.chrisali.javaflightsim.simulation.setup;

/**
 * Enums to be used in {@link ControlsConfiguration} to configure key or button presses
 * 
 * @author Christopher
 *
 */
public enum KeyCommand {
	ELEVATOR_DOWN 		("Elevator Down"),
	ELEVATOR_UP 		("Elevator Up"),
	AILERON_LEFT		("Aileron Left"),
	AILERON_RIGHT		("Aileron Right"),
	RUDDER_LEFT			("Rudder Left"),
	RUDDER_RIGHT		("Rudder Right"),
	CENTER_CONTROLS		("Center Flight Controls"),
	GEAR_UP_DOWN		("Elevator Up/Down"),
	INCREASE_FLAPS		("Increase Flaps"),
	DECREASE_FLAPS		("Decrease Flaps"),
	INCREASE_THROTTLE	("Increase Throttle"),
	DECREASE_THROTTLE	("Decrease Throttle"),
	INCREASE_PROPELLER	("Increase Propeller"),
	DECREASE_PROPELLER	("Decrease Propeller"),
	INCREASE_MIXTURE	("Increase Mixture"),
	DECREASE_MIXTURE	("Decrease Mixture"),
	BRAKES				("Apply Brakes"),
	EXIT_SIMULATION		("Exit Simulation"),
	GENERATE_PLOTS		("Generate Plots"),
	PAUSR_UNPAUSE_SIM	("Pause/Unpause Simulation"),
	RESET_SIM			("Reset Simulation");
	
	private String name;
	
	KeyCommand(String name) { this.name = name; }
	
	public String toString() { return name; }
}
