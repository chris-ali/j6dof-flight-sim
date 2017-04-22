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

import java.util.Arrays;
import java.util.HashSet;

import javax.swing.JFrame;

import com.chrisali.javaflightsim.controllers.SimulationController;
import com.chrisali.javaflightsim.plotting.PlotWindow;
import com.chrisali.javaflightsim.simulation.aircraft.AircraftBuilder;
import com.chrisali.javaflightsim.simulation.controls.FlightControls;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.setup.Options;
import com.chrisali.javaflightsim.simulation.setup.SimulationConfiguration;
import com.chrisali.javaflightsim.simulation.setup.Trimming;

/**
 * Runs a test of the flight simulation module in Analysis mode to test an aircraft and the simulation 
 * workings; uses {@link SimulationController} to set the options, {@link AircraftBuilder} to initalize 
 * the aircraft to test, {@link FlightControls} thread to run doublet inputs and {@link PlotWindow} to
 * plot the simulation results at the end
 * 
 * @author Christopher Ali
 *
 */
public class SimulationTest {
	
	private FlightControls flightControls;
	private Thread flightControlsThread;
	
	private Integrate6DOFEquations runSim;
	private Thread simulationThread;
	
	private SimulationController controller;
	private SimulationConfiguration configuration;
	
	private PlotWindow plots;
	
	public SimulationTest() {
		controller = new SimulationController();
		configuration = new SimulationConfiguration();
		
		configuration.getSimulationOptions().clear();
		configuration.getSimulationOptions().add(Options.ANALYSIS_MODE);
		configuration.setAircraftBuilder(new AircraftBuilder("TwinNavion")); // Twin Navion with 2 Lycoming IO-360
		//configuration.setAircraftBuilder(new AircraftBuilder()); // Default Navion with Lycoming IO-360
		//configuration.setAircraftBuilder(new AircraftBuilder("Navion")); // Navion with lookup tables with Lycoming IO-360
		
		Trimming.trimSim(configuration, false);

		this.flightControls = new FlightControls(controller);
		this.flightControlsThread = new Thread(flightControls);
		
		this.runSim = new Integrate6DOFEquations(flightControls, configuration);
		this.simulationThread = new Thread(runSim);

		flightControlsThread.start();
		simulationThread.start();
		
		try {Thread.sleep(1000);} 
		catch (InterruptedException e) {}
		
		this.plots = new PlotWindow(new HashSet<String>(Arrays.asList("Controls", "Instruments", "Position", "Rates", "Miscellaneous")),
						 			controller);
		plots.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		FlightControls.setRunning(false);
	}
	
	public static void main(String[] args) {new SimulationTest();}
}
