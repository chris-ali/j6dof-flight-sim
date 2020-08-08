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
package com.chrisali.javaflightsim.simulation.aircraft;

public enum GroundReaction {
	NOSE_X  		("nose_x"),
	NOSE_Y  		("nose_y"),
	NOSE_Z  		("nose_z"),
	NOSE_DAMPING 	("nose_damping"),
	NOSE_SPRING 	("nose_spring"),
	LEFT_X  		("left_x"),
	LEFT_Y  		("left_y"),
	LEFT_Z  		("left_z"),
	LEFT_DAMPING 	("left_damping"),
	LEFT_SPRING 	("left_spring"),
	RIGHT_X 		("right_x"),
	RIGHT_Y 		("right_y"),
	RIGHT_Z 		("right_z"),
	RIGHT_DAMPING 	("right_damping"),
	RIGHT_SPRING 	("right_spring"),
	BRAKING_FORCE   ("braking_force");
	
	private String groundReactionParam;
	
	private GroundReaction(String groundReactionParam) {this.groundReactionParam = groundReactionParam;}

	public String toString() {return groundReactionParam;}
}
