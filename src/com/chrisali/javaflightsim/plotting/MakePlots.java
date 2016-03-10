package com.chrisali.javaflightsim.plotting;

import java.util.EnumMap;
import java.util.List;

import com.chrisali.javaflightsim.simulation.aircraft.Aircraft;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.integration.SimOuts;

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
	private List<EnumMap<SimOuts, Double>> logsOut;
	private Aircraft aircraft;
	
	public MakePlots(List<EnumMap<SimOuts, Double>> logsOut, 
					 String[] simPlotCategories,
					 Aircraft aircraft) {
		this.logsOut 	   	   = logsOut;
		this.simPlotCategories = simPlotCategories;
		this.aircraft 		   = aircraft;
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep(250);
			for (String plot : simPlotCategories) { 
				new SimulationPlots(logsOut, plot, aircraft.getName());
				Thread.sleep(100);
			}
		} catch (InterruptedException e) {}
	}

}
