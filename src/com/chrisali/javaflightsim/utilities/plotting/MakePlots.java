package com.chrisali.javaflightsim.utilities.plotting;

import java.util.EnumSet;

import com.chrisali.javaflightsim.aircraft.Aircraft;
import com.chrisali.javaflightsim.setup.Options;
import com.chrisali.javaflightsim.utilities.integration.Integrate6DOFEquations;

/*
 * This class implements threading to generate plot windows of simulation states. It loops through a String array
 * and creates a SimulationPlots objects using the data from the logsOut EnumMap list in Integrate6DOFEquations  
 * 
 * The following must be passed in:
 * String[] simPlotCetegories
 * Integrate6DOFEquations integration
 * EnumMap<Options, Boolean> options
 * Aircraft aircraft
 */
public class MakePlots implements Runnable {
	private String[] simPlotCategories;
	private Integrate6DOFEquations integration;
	private Aircraft aircraft;
	
	public MakePlots(Integrate6DOFEquations integration, 
					 String[] simPlotCategories,
					 EnumSet<Options> options,
					 Aircraft aircraft) {
		this.integration 	   = integration;
		this.simPlotCategories = simPlotCategories;
		this.aircraft 		   = aircraft;
	}
	
	@Override
	public void run() {
		try {
			for (String plot : simPlotCategories) { 
				new SimulationPlots(integration.getLogsOut(), plot, aircraft.getName());
				Thread.sleep(100);
			}
		} catch (InterruptedException e) {System.err.println("Warning! Plotting interrupted!");}
	}

}
