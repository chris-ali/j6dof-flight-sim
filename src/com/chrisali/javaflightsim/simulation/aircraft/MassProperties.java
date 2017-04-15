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
package com.chrisali.javaflightsim.simulation.aircraft;

/**
 * Provides Enum values to define the parameters that make up the mass properties of an {@link Aircraft}. 
 * The String field is used to parse the MassProperties.txt file of an aircraft in the constructor
 */
public enum MassProperties {
	TOTAL_MASS      	("totalMass"),
	MAX_WEIGHT_FUEL     ("maxWeightFuel"),
	WEIGHT_FUEL     	("weightFuel"),
	MAX_WEIGHT_PAYLOAD  ("maxWeightPayload"),
	WEIGHT_PAYLOAD  	("weightPayload"),
	WEIGHT_EMPTY    	("weightEmpty"),
	J_X					("jx"),
	J_Y					("jy"),
	J_Z					("jz"),
	J_XZ				("jxz"),
	CG_X				("cgX"),
	CG_Y				("cgY"),
	CG_Z				("cgZ");
	
	private final String massProperty;
	
	MassProperties(String massProperty) {this.massProperty = massProperty;}
	
	public String toString() {return massProperty;}
}
