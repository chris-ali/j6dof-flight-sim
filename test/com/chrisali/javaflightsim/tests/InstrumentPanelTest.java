package com.chrisali.javaflightsim.tests;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.chrisali.javaflightsim.controllers.SimulationController;
import com.chrisali.javaflightsim.datatransfer.FlightData;
import com.chrisali.javaflightsim.instrumentpanel.InstrumentPanel;
import com.chrisali.javaflightsim.simulation.controls.FlightControls;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.setup.Options;

/**
 * Creates a real-time pilot in the loop simulation using {@link Integrate6DOFEquations}, and
 * creates an {@link InstrumentPanel} object in JFrame object to test all gauges with the 
 * simulation; uses CH controls running on separate thread in {@link FlightControls} for flight controls
 * 
 * @author Christopher Ali
 *
 */
public class InstrumentPanelTest {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {runApp();}
		});
	}

	private static void runApp() {
		SimulationController simController = new SimulationController();
		simController.getSimulationOptions().clear();
		simController.getSimulationOptions().add(Options.UNLIMITED_FLIGHT);
		simController.getSimulationOptions().add(Options.USE_CH_CONTROLS);
		
		FlightControls flightControls = new FlightControls(simController);
		
		Integrate6DOFEquations runSim = new Integrate6DOFEquations(flightControls, simController);
		FlightData flightData = new FlightData(runSim);

		new Thread(flightControls).start();
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