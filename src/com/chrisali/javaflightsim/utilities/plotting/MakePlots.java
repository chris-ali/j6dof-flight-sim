package com.chrisali.javaflightsim.utilities.plotting;

import java.util.concurrent.CountDownLatch;

import com.chrisali.javaflightsim.utilities.integration.Integrate6DOFEquations;

/*
 * This class implements threading to generate plot windows of simulation states following
 * the completion of simulation
 * 
 * The following must be passed in:
 * String[] simPlotCetegories
 * Integrate6DOFEquations integration
 * CountDownLatch latch
 */
public class MakePlots implements Runnable {
	private String[] simPlotCategories;
	private Integrate6DOFEquations integration;
	private CountDownLatch latch;
	
	public MakePlots(Integrate6DOFEquations integration, 
					 String[] simPlotCategories,
					 CountDownLatch latch) {
		this.integration 	   = integration;
		this.simPlotCategories = simPlotCategories;
		this.latch			   = latch;
	}
	
	@Override
	public void run() {
		try {
			latch.await(); // Waits for latch to count down to 0 before executing this thread 
			for (String plot : simPlotCategories) 
				new SimulationPlots(integration.getLogsOut(), plot);
		} catch (InterruptedException e) {System.err.println("Warning! Plotting interrupted!");}
	}

}
