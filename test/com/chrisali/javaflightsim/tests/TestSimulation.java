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

import javax.swing.JFrame;

import com.chrisali.javaflightsim.initializer.LWJGLSwingSimulationController;
import com.chrisali.javaflightsim.simulation.aircraft.AircraftBuilder;
import com.chrisali.javaflightsim.simulation.setup.Options;
import com.chrisali.javaflightsim.simulation.setup.SimulationConfiguration;
import com.chrisali.javaflightsim.simulation.utilities.FileUtilities;

/**
 * Runs a test of the flight simulation module in Analysis mode to test an aircraft and the simulation 
 * workings; uses {@link LWJGLSwingSimulationController} to configure and run all threads necessary, and 
 * then plots the simulation results at the end
 * 
 * @author Christopher Ali
 *
 */
public class TestSimulation {
	
	public TestSimulation() {
		SimulationConfiguration configuration = FileUtilities.readSimulationConfiguration();
		LWJGLSwingSimulationController controller = new LWJGLSwingSimulationController(configuration);
		
		configuration.getSimulationOptions().clear();
		configuration.getSimulationOptions().add(Options.ANALYSIS_MODE);
		configuration.setAircraftBuilder(new AircraftBuilder("TwinNavion")); // Twin Navion with 2 Lycoming IO-360
		//configuration.setAircraftBuilder(new AircraftBuilder()); // Default Navion with Lycoming IO-360
		//configuration.setAircraftBuilder(new AircraftBuilder("Navion")); // Navion with lookup tables with Lycoming IO-360
		
		controller.startSimulation();
		controller.getPlotWindow().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args) {new TestSimulation();}
}
