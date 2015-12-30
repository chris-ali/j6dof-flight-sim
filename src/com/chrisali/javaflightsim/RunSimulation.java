package com.chrisali.javaflightsim;

import com.chrisali.javaflightsim.aircraft.Aircraft;
import com.chrisali.javaflightsim.propulsion.FixedPitchPropEngine;
import com.chrisali.javaflightsim.utilities.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.utilities.plotting.MakePlots;

public class RunSimulation {

	public static void main(String[] args) {

		// TODO gather all initial conditions/controls from trim routine
		// TODO integrate joystick
		// TODO enable/disable debug mode
		
		String[] simPlotCategories = {"Controls", "Instruments", "Position", "Rates", "Miscellaneous"};
		
		// Create simulation using 
		Integrate6DOFEquations integration = new Integrate6DOFEquations(new Aircraft(), 			 // Default to Navion
																		new FixedPitchPropEngine()); // Default to Lycoming IO-360
		
		// Create threads for simulation and plotting
		Thread runSim  = new Thread(integration);
		Thread plotSim = new Thread(new MakePlots(integration, simPlotCategories));
		
		// Start threads
		runSim.start();
		plotSim.start();
		
	}
}
