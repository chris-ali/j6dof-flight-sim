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

import com.chrisali.javaflightsim.simulation.aircraft.AircraftBuilder;
import com.chrisali.javaflightsim.simulation.propulsion.Engine;

public class AircraftBuilderTest {	
	public AircraftBuilderTest() {
		
		System.out.println("============================");
		System.out.println("============================");
		System.out.println("Default Builder Test");
		System.out.println("============================");
		System.out.println("============================\n");
		
		AircraftBuilder abDefault = new AircraftBuilder();
		
		System.out.println(abDefault.getAircraft().toString() + "\n");
		
		for (Engine engine : abDefault.getEngineList()) {
			System.out.println(engine);
			System.out.println();
		}
	}
	
	public AircraftBuilderTest(String aircraftName) {
		
		System.out.println("============================");
		System.out.println("============================");
		System.out.println( aircraftName + " Builder Test");
		System.out.println("============================");
		System.out.println("============================\n");

		AircraftBuilder ab = new AircraftBuilder(aircraftName);

		System.out.println(ab.getAircraft().toString() + "\n");
		
		for (Engine engine : ab.getEngineList()) {
			System.out.println(engine);
			System.out.println();
		}
	}

	public static void main(String[] args) {
		new AircraftBuilderTest();
		new AircraftBuilderTest("Navion");
		new AircraftBuilderTest("TwinNavion");
	}
}
