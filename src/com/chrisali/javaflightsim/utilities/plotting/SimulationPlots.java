package com.chrisali.javaflightsim.utilities.plotting;

import java.util.ArrayList;
import java.util.HashMap;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.ApplicationFrame;

public class SimulationPlots extends ApplicationFrame {

	private static final long serialVersionUID = 1L;
	

	// Creates plots for variables monitored in the logsOut ArrayList
	public SimulationPlots(ArrayList<Double[]> logsOut, String applicationTitle) {
		super(applicationTitle);
		
		PlotUtilities.makePlotLists(logsOut);
		
		HashMap<String, XYPlot> plotLists = PlotUtilities.getPlotLists();
		
		CombinedDomainXYPlot simulationPlot = new CombinedDomainXYPlot(new NumberAxis("Time [sec]"));
		simulationPlot.add(plotLists.get("TAS"),            1);
		simulationPlot.add(plotLists.get("Altitude"),       1);
		simulationPlot.add(plotLists.get("Heading"),        1);
		simulationPlot.add(plotLists.get("Euler Angles"),   1);
		simulationPlot.add(plotLists.get("Accelerations"),  1);
		simulationPlot.add(plotLists.get("Wind Parameters"),1);
		simulationPlot.setOrientation(PlotOrientation.VERTICAL);
		simulationPlot.setGap(20);
		
		JFreeChart sixDOFPlots = new JFreeChart("Simulaton States", 
										 	    JFreeChart.DEFAULT_TITLE_FONT, 
										        simulationPlot, 
										        true);
		
		// Create Chart Panels to populate AWT window 
		ChartPanel plotPanel = new ChartPanel(sixDOFPlots);
		plotPanel.setPreferredSize(new java.awt.Dimension(1000, 950));
		setContentPane(plotPanel);
		
		PlotUtilities.generatePlotWindows(this);
	}

}
