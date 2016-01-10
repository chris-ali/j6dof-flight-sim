package com.chrisali.javaflightsim.utilities.plotting;

import java.util.EnumMap;

import com.chrisali.javaflightsim.setup.Options;
import com.chrisali.javaflightsim.utilities.integration.Integrate6DOFEquations;

/*
 * This class implements threading to generate plot windows of simulation states
 * 
 * The following must be passed in:
 * String[] simPlotCetegories
 * Integrate6DOFEquations integration
 * EnumMap<Options, Boolean> option
 */
public class MakePlots implements Runnable {
	private String[] simPlotCategories;
	private Integrate6DOFEquations integration;
	
	public MakePlots(Integrate6DOFEquations integration, 
					 String[] simPlotCategories,
					 EnumMap<Options, Boolean> options) {
		this.integration 	   = integration;
		this.simPlotCategories = simPlotCategories;
	}
	
	@Override
	public void run() {
		try {
			for (String plot : simPlotCategories) { 
				new SimulationPlots(integration.getLogsOut(), plot);
				Thread.sleep(20);
			}
		} catch (InterruptedException e) {System.err.println("Warning! Plotting interrupted!");}
	}

}
