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
package com.chrisali.javaflightsim.tests;

import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import com.chrisali.javaflightsim.simulation.aircraft.Aircraft;
import com.chrisali.javaflightsim.simulation.flightcontrols.FlightControl;
import com.chrisali.javaflightsim.simulation.integration.IntegrateGroundReaction;
import com.chrisali.javaflightsim.simulation.setup.SimulationConfiguration;
import com.chrisali.javaflightsim.simulation.utilities.FileUtilities;

public class TestGroundReaction {
	private SimulationConfiguration configuration = FileUtilities.readSimulationConfiguration();
	private IntegrateGroundReaction groundReaction;
	private Aircraft aircraft = new Aircraft("Navion");
	private double terrainHeight;
	double[] integratorConfig 				 = ArrayUtils.toPrimitive(configuration.getIntegratorConfig().values()
				  																	  .toArray(new Double[3]));
	private double t;
	private Map<FlightControl, Double> controls = configuration.getInitialControls();
	
	// 6DOF Integration Results
	private double[] linearVelocities 		  = new double[]{5,0,0};
	private double[] NEDPosition      		  = new double[]{0,0,0};
	private double[] eulerAngles      		  = new double[]{0,0,0};
	private double[] angularRates     		  = new double[]{0,0,0};
	private double[] windParameters			  = new double[]{0,0,0};
	
	private double[] sixDOFDerivatives		  = new double[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	
	public static void main(String[] args) {
		TestGroundReaction test = new TestGroundReaction();
		test.setup();
		test.run();
	}
	
	private void setup() {
		terrainHeight = 0;
		t = 0;
		
		groundReaction = new IntegrateGroundReaction(linearVelocities,
													 NEDPosition,
													 eulerAngles,
													 angularRates,
													 windParameters,
													 integratorConfig,
													 sixDOFDerivatives,
													 aircraft,
													 controls);
	}
	
	private void run() {
		while (t <= (integratorConfig[2]-95)) {
			
			NEDPosition[2] = 1.75;
			//controls.put(FlightControls.BRAKE_L, 0.8);
			controls.put(FlightControl.RUDDER, -0.0);
			
			groundReaction.integrateStep(terrainHeight);
			
			System.out.println(groundReaction.toString());
			
			t += integratorConfig[1];
		}
	}

}
