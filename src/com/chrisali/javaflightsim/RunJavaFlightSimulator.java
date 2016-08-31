package com.chrisali.javaflightsim;

import javax.swing.SwingUtilities;

import com.chrisali.javaflightsim.controllers.SimulationController;
import com.chrisali.javaflightsim.menus.MainFrame;

/**
 * Runner class to start Java Flight Simulator
 * 
 * @author Christopher Ali
 *
 */
public class RunJavaFlightSimulator {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {runApp();}
		});
	}
	
	/**
	 * Initializes {@link SimulationController} and {@link MainFrame}; due to cross-referencing
	 * needed with both objects, {@link SimulationController#setMainFrame(MainFrame)} needs to be
	 * called
	 */
	private static void runApp() {
		SimulationController controller = new SimulationController(); 
		MainFrame mainFrame = new MainFrame(controller);
		
		// Pass in mainFrame reference so that OTW display can get Canvas
		// reference
		controller.setMainFrame(mainFrame);
	}
}
