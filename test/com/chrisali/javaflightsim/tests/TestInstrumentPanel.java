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

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.chrisali.javaflightsim.initializer.LWJGLSwingSimulationController;
import com.chrisali.javaflightsim.simulation.datatransfer.FlightData;
import com.chrisali.javaflightsim.simulation.flightcontrols.FlightControls;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.setup.Options;
import com.chrisali.javaflightsim.simulation.setup.SimulationConfiguration;
import com.chrisali.javaflightsim.simulation.utilities.FileUtilities;
import com.chrisali.javaflightsim.swing.instrumentpanel.InstrumentPanel;

/**
 * Creates a real-time pilot in the loop simulation using {@link Integrate6DOFEquations}, and
 * creates an {@link InstrumentPanel} object in JFrame object to test all gauges with the 
 * simulation; uses CH controls running on separate thread in {@link FlightControls} for flight controls
 * 
 * @author Christopher Ali
 *
 */
public class TestInstrumentPanel {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {runApp();}
		});
	}

	private static void runApp() {
		SimulationConfiguration configuration = FileUtilities.readSimulationConfiguration();;
		LWJGLSwingSimulationController controller = new LWJGLSwingSimulationController(configuration);
		
		configuration.getSimulationOptions().clear();
		configuration.getSimulationOptions().add(Options.UNLIMITED_FLIGHT);
		configuration.getSimulationOptions().add(Options.USE_CH_CONTROLS);
		
		FlightControls flightControls = new FlightControls(controller);
		
		Integrate6DOFEquations runSim = new Integrate6DOFEquations(flightControls, configuration);
		FlightData flightData = new FlightData(runSim);

		new Thread(runSim).start();
		new Thread(flightData).start();
		
		InstrumentPanel panel = new InstrumentPanel();
		flightData.addFlightDataListener(panel);
		
		JFrame panelWindow = new JFrame("Instrument Panel Test");
		panelWindow.setLayout(new BorderLayout());
		panelWindow.add(panel, BorderLayout.CENTER);
		
		panelWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panelWindow.setVisible(true);
		panelWindow.setSize(810, 500);
		panelWindow.setResizable(false);
	}
}
