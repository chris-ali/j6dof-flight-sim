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

/**
 *	Used by the CameraConfiguration to select an appropriate camera mode in the Out the Window display
 */
public enum CameraMode {
	COCKPIT_2D ("2D Cockpit View"),
	COCKPIT_3D ("3D Cockpit View"),
	CHASE 	   ("Chase View"),
	FLYBY 	   ("Fly-by View");
	
	private String option;
	
	private CameraMode(String option) {this.option = option;}
	
	public String toString() {return option;}
}
