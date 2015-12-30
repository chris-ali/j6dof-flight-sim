package com.chrisali.javaflightsim.utilities.plotting;

import com.chrisali.javaflightsim.utilities.integration.Integrate6DOFEquations;

/*
 * This class implements threading to generate plot windows of simulation states following
 * the completion of simulation
 * 
 * The following must be passed in:
 * String[] simPlotCetegories
 * Integrate6DOFEquations integration
 */
public class MakePlots implements Runnable {
	private String[] simPlotCategories;
	private Integrate6DOFEquations integration;
	
	public MakePlots(Integrate6DOFEquations integration, String[] simPlotCategories) {
		this.integration 	   = integration;
		this.simPlotCategories = simPlotCategories;
	}
	
	@Override
	public void run() {
		for (String plot : simPlotCategories) 
			new SimulationPlots(integration.getLogsOut(), plot);
	}

}
