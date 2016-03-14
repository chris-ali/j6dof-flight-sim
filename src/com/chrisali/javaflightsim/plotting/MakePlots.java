package com.chrisali.javaflightsim.plotting;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;

import com.chrisali.javaflightsim.simulation.aircraft.Aircraft;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.integration.SimOuts;

/**
 * This class is the runner class to plot data from the simulation in Swing windows. It loops through 
 * the {@link MakePlots#simPlotCategories} set to create {@link SimulationPlots} objects using the data 
 * from {@link Integrate6DOFEquations#getLogsOut()}. 
 * 
 * @param String simPlotCetegories
 * @param List<EnumMap<SimOuts, Double>> logsOut
 * @param Aircraft aircraft
 */
public class MakePlots {
	
	private PlotCloseListener plotCloseListener;
	private List<SimulationPlots> plotList = new ArrayList<>();
	
	public MakePlots(List<EnumMap<SimOuts, Double>> logsOut, 
					 HashSet<String> simPlotCategories,
					 Aircraft aircraft) {
		
		for (String plotTitle : simPlotCategories) {
			try {Thread.sleep(125);} 
			catch (InterruptedException e) {}
			
			SimulationPlots plotObject = new SimulationPlots(logsOut, plotTitle, aircraft.getName());
			plotList.add(plotObject);
			plotObject.setPlotCloseListner(plotCloseListener);
		}
	}
		
	public List<SimulationPlots> getPlotList() {return plotList;}

	public void setPlotCloseListener (PlotCloseListener plotCloseListener) {
		this.plotCloseListener = plotCloseListener;
	}
	
}
