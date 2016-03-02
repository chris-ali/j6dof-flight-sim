package com.chrisali.javaflightsim.utilities.plotting;

import java.util.EnumSet;

import com.chrisali.javaflightsim.aircraft.Aircraft;
import com.chrisali.javaflightsim.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.setup.Options;

/**
 * This class is the runner class to plot data from the simulation in AWT windows. It implements threading to accomplish this, 
 * looping through the {@link MakePlots#simPlotCategories} array to create {@link SimulationPlots} objects using the data 
 * from {@link Integrate6DOFEquations#getLogsOut()}. 
 * 
 * @param String simPlotCetegories
 * @param Integrate6DOFEquations integration
 * @param EnumMap options
 * @param Aircraft aircraft
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
