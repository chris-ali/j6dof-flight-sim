package com.chrisali.javaflightsim;

import javax.swing.SwingUtilities;

import com.chrisali.javaflightsim.controllers.SimulationController;
import com.chrisali.javaflightsim.menus.MainFrame;

public class RunSimulation {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {runApp();}
		});
	}
	
	private static void runApp() {new MainFrame(new SimulationController());}
}
