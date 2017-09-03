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
package com.chrisali.javaflightsim.tests;

import com.chrisali.javaflightsim.simulation.aircraft.Aircraft;
/**
 * This class tests the parsing methods defined in the Aircraft class. It helps ensure that the data needed to define an aircraft is being
 * parsed correctly, and allows for debugging of file integrity checking methods. It runs the default constructor first, followed by the 
 * constructor using file parsing. The Aircraft class toString method outputs the stability derivatives, mass properties, and wing geometry
 * of each aircraft
 */
public class TestAircraft {
	public TestAircraft(String aircraftName) {
		System.out.println("Default Aircraft:\n");
		System.out.println(new Aircraft().toString());
		
		System.out.println(aircraftName + " File Parsing:\n");
		System.out.println(new Aircraft(aircraftName).toString());
	}
	
	public static void main(String[] args) {new TestAircraft("Navion");}
}
