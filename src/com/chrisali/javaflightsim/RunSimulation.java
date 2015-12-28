package com.chrisali.javaflightsim;

import com.chrisali.javaflightsim.aircraft.Aircraft;
import com.chrisali.javaflightsim.propulsion.FixedPitchPropEngine;
import com.chrisali.javaflightsim.utilities.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.utilities.plotting.SimulationPlots;

public class RunSimulation {

	public static void main(String[] args) {

		// TODO gather all initial conditions/controls from trim routine
		// TODO integrate joystick

		//---------------------
		// Start Simulation Here
		//---------------------
		Integrate6DOFEquations integration = new Integrate6DOFEquations(new Aircraft(), // Default to Navion
																		new FixedPitchPropEngine()); // Default to Lycoming IO-360
		Thread runSim = new Thread(integration);
		runSim.start();
		
		//TODO enable/disable debug mode
		String[] simPlotCategories = {"Controls", "Instruments", "Position", "Rates", "Miscellaneous"};
		for (String plot : simPlotCategories) 
			new SimulationPlots(integration.getLogsOut(), plot);
	}
}
