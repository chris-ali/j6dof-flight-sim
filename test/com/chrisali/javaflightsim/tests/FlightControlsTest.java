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

import com.chrisali.javaflightsim.initializer.LWJGLSwingSimulationController;
import com.chrisali.javaflightsim.initializer.SimulationConfiguration;
import com.chrisali.javaflightsim.simulation.flightcontrols.FlightControls;
import com.chrisali.javaflightsim.simulation.setup.Options;

/**
 * Test class for {@link FlightControls}. Creates flight controls object and thread to
 * run, and outputs values for each flight control deflection/setting
 * 
 * @author Christopher Ali
 *
 */
public class FlightControlsTest implements Runnable {
	private FlightControls flightControls;
	private Thread flightControlsThread;
	private SimulationConfiguration configuation;
	private LWJGLSwingSimulationController simController;
	
	public FlightControlsTest() {
		configuation = simController.getConfiguration();
		simController = new LWJGLSwingSimulationController(configuation);
		configuation.getSimulationOptions().add(Options.USE_CH_CONTROLS);
		flightControls = new FlightControls(simController);
		flightControlsThread = new Thread(flightControls);
	}
	
	@Override
	public void run() {
		flightControlsThread.start();
		
		try {
			Thread.sleep(500);
			
			while (FlightControls.isRunning()) {
				System.out.println(flightControls.toString());
				System.out.println();
				System.out.println(configuation.getSimulationOptions());
				Thread.sleep((long) (100));
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {new Thread(new FlightControlsTest()).start();}
}
