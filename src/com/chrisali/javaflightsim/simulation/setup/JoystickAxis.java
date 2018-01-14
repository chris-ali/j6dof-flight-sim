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
package com.chrisali.javaflightsim.simulation.setup;

import com.chrisali.javaflightsim.simulation.flightcontrols.FlightControl;

/**
 * Contains user-defined {@link FlightControl} axis, dead zone and sensitivity settings for an axis assignment.
 * Used with {@link ControlsConfiguration} to populate a map of a list of axes for each joystick connected
 * 
 * @author Christopher
 *
 */
public class JoystickAxis {

	private FlightControl axisAssignment;
	
	private double deadZone;
	
	private double sensitivity;
	
	public JoystickAxis() { }
	
	public JoystickAxis(FlightControl axisAssignment) { 
		this.axisAssignment = axisAssignment;
		deadZone = 0.0;
		sensitivity = 1.0;
	}
	
	public JoystickAxis(FlightControl axisAssignment, double deadZone, double sensitivity) {
		this(axisAssignment);
		this.deadZone = deadZone;
		this.sensitivity = sensitivity;
	}
	
	public FlightControl getAxisAssignment() { return axisAssignment; }
	
	public void setAxisAssignment(FlightControl axisAssignment) { this.axisAssignment = axisAssignment; }

	public double getDeadZone() { return deadZone; }

	/**
	 * Sets the dead zone of this axis to a value between 0.0 and 1.0 inclusive; 0.0 means this axis has no dead zone, 
	 * and vice-versa for 1.0
	 * 
	 * @param deadZone
	 */
	public void setDeadZone(double deadZone) { 
		this.deadZone = deadZone > 1.0 ? 1.0 : 
						deadZone < 0.0 ? 0.0 : deadZone; 
	}

	public double getSensitivity() { return sensitivity; }

	/**
	 * Sets the sensitivity of this axis to a value between 1.0 and 2.0; 1.0 means this axis is linear, and 2.0 means the axis' 
	 * response is quadratic in nature  
	 * 
	 * @param sensitivity
	 */
	public void setSensitivity(double sensitivity) { 
		this.sensitivity = sensitivity > 2.0 ? 2.0 : 
						   sensitivity < 1.0 ? 1.0 : sensitivity; 
	}
}
