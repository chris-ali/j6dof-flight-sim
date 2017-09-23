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

import java.util.EnumMap;

import com.chrisali.javaflightsim.simulation.aircraft.Aerodynamics;
import com.chrisali.javaflightsim.simulation.aircraft.Aircraft;
import com.chrisali.javaflightsim.simulation.aircraft.StabilityDerivatives;
import com.chrisali.javaflightsim.simulation.flightcontrols.FlightControl;
import com.chrisali.javaflightsim.simulation.setup.SimulationConfiguration;
import com.chrisali.javaflightsim.simulation.utilities.FileUtilities;

public class TestLookupTable {
	private SimulationConfiguration configuration = FileUtilities.readSimulationConfiguration();
	private EnumMap<FlightControl, Double> controls = configuration.getInitialControls();
	private double[] alpha = new double[] {-14, -12, -10, -8, -6, -4, -2, 0, 2, 4, 6, 8, 10, 12, 14, 16};
	private double[] dFlap = new double[] {0, 10, 20, 30, 40};
	Aircraft aircraft;
	private Aerodynamics aero;
	
	public TestLookupTable(String aircraftName) {								 
		aircraft = new Aircraft(aircraftName);
		aero = new Aerodynamics(aircraft);
		double clAlpha = 0.0;

		for (int j=0; j<dFlap.length; j++) {
			controls.put(FlightControl.FLAPS, Math.toRadians(dFlap[j]));
			
			for (double aoa=alpha[0]; aoa<=alpha[alpha.length-1]; aoa+=1) {
				clAlpha = aero.calculateInterpStabDer(new double[] {0.0, 0.0, Math.toRadians(aoa)}, controls, StabilityDerivatives.CM_ALPHA);
				
				System.out.printf("Flaps: %2.0f | Alpha: %2.1f | CL_Alpha: %4.3f%n", dFlap[j], aoa, clAlpha);
				System.out.println("-----------------------------------------");
			}
			System.out.println();
		}
	}

	public static void main(String[] args) {new TestLookupTable("TwinNavion");}
}
