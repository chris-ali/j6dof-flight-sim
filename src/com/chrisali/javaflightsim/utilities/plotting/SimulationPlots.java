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

/*
 * This class allows the user to specify from a group of various types of plot groups by using the 
 * applicationTitle String variable in the constructor:
 * 
 * Rates :	Subplot 1: (p, q, r) vs. Time
 * 			Subplot 2: (u, v, w) vs. Time
 * 			Subplot 3: (an_x, an_y, an_z) vs, Time
 * 
 * Position : North vs. East (ft)
 * 
 * Instrumentation : 	Subplot 1: (phi, theta) vs. Time
 * 						Subplot 2: TAS vs. Time
 * 						Subplot 3: psi vs. Time
 * 						Subplot 4: Altitude vs. Time
 * 						Subplot 5: Vertical Speed vs. Time
 * 
 * Miscellaneous : 		Subplot 1: (alpha, beta) vs. Time
 */
public class SimulationPlots extends ApplicationFrame {

	private static final long serialVersionUID = 1L;
	
	// Creates plots for variables monitored in the logsOut ArrayList
	public SimulationPlots(ArrayList<Double[]> logsOut, String applicationTitle) {
		super(applicationTitle);
		
		PlotUtilities.makePlotLists(logsOut);
		
		// Select from methods below to create a chart panels to populate AWT window 
		switch (applicationTitle) {
			case "Rates":
				ChartPanel ratePlotPanel = new ChartPanel(makeRatesPlots(PlotUtilities.getPlotLists()));
				ratePlotPanel.setPreferredSize(new java.awt.Dimension(1000, 950));
				setContentPane(ratePlotPanel);
				break;
			case "Position":
				ChartPanel posPlotPanel = new ChartPanel(makePositionPlot(PlotUtilities.getPlotLists()));
				posPlotPanel.setPreferredSize(new java.awt.Dimension(750, 750));
				setContentPane(posPlotPanel);
				break;
			case "Instruments":
				ChartPanel instPlotPanel = new ChartPanel(makeInstrumentPlots(PlotUtilities.getPlotLists()));
				instPlotPanel.setPreferredSize(new java.awt.Dimension(1000, 950));
				setContentPane(instPlotPanel);
				break;
			case "Miscellaneous":
				ChartPanel miscPlotPanel = new ChartPanel(makeMiscPlots(PlotUtilities.getPlotLists()));
				miscPlotPanel.setPreferredSize(new java.awt.Dimension(1000, 400));
				setContentPane(miscPlotPanel);
				break;
			case "Controls":
				ChartPanel controlPlotPanel = new ChartPanel(makeControlsPlots(PlotUtilities.getPlotLists()));
				controlPlotPanel.setPreferredSize(new java.awt.Dimension(1000, 950));
				setContentPane(controlPlotPanel);
				break;	
			default:
				System.err.println("Invalid plot type selected!");
				break;
		}
		
		PlotUtilities.generatePlotWindows(this);
	}
	
	// Plots associated with rates and accelerations
	private JFreeChart makeRatesPlots(HashMap<PlotType, XYPlot> plotLists) {
		CombinedDomainXYPlot simulationPlot = new CombinedDomainXYPlot(new NumberAxis("Time [sec]"));
		
		simulationPlot.add(plotLists.get(PlotType.ANGULAR_RATE), 1);
		simulationPlot.add(plotLists.get(PlotType.VELOCITY),     1);
		simulationPlot.add(plotLists.get(PlotType.ACCELERATION), 1);
		simulationPlot.setOrientation(PlotOrientation.VERTICAL);
		simulationPlot.setGap(20);
		
		return new JFreeChart("Rates", 
					 	      JFreeChart.DEFAULT_TITLE_FONT, 
					          simulationPlot, 
					          true);
	}
	
	// North vs East plot of aircraft position
	private JFreeChart makePositionPlot(HashMap<PlotType, XYPlot> plotLists) {
		CombinedDomainXYPlot simulationPlot = new CombinedDomainXYPlot(new NumberAxis("East [ft]"));
		
		simulationPlot.add(plotLists.get(PlotType.POSITION), 1);
		
		simulationPlot.setOrientation(PlotOrientation.VERTICAL);
		simulationPlot.setGap(20);
		
		return new JFreeChart("Position", 
					 	      JFreeChart.DEFAULT_TITLE_FONT, 
					          simulationPlot, 
					          true);
	}
	
	// Plots associated with typical aircraft instrumentation
	private JFreeChart makeInstrumentPlots(HashMap<PlotType, XYPlot> plotLists) {
		CombinedDomainXYPlot simulationPlot = new CombinedDomainXYPlot(new NumberAxis("Time [sec]"));
		
		simulationPlot.add(plotLists.get(PlotType.EULER_ANGLES), 1);
		simulationPlot.add(plotLists.get(PlotType.TAS), 		 1);
		simulationPlot.add(plotLists.get(PlotType.HEADING),      1);
		simulationPlot.add(plotLists.get(PlotType.ALTITUDE),     1);
		simulationPlot.add(plotLists.get(PlotType.VERT_SPEED),   1);
		
		simulationPlot.setOrientation(PlotOrientation.VERTICAL);
		simulationPlot.setGap(20);
		
		return new JFreeChart("Instruments", 
					 	      JFreeChart.DEFAULT_TITLE_FONT, 
					          simulationPlot, 
					          true);
	}
	
	// Miscellaneous plots such as alpha, beta
	private JFreeChart makeMiscPlots(HashMap<PlotType, XYPlot> plotLists) {
		CombinedDomainXYPlot simulationPlot = new CombinedDomainXYPlot(new NumberAxis("Time [sec]"));
		
		simulationPlot.add(plotLists.get(PlotType.WIND_PARAM), 1);
		simulationPlot.add(plotLists.get(PlotType.ALPHA_DOT),  1);
		simulationPlot.add(plotLists.get(PlotType.MACH),  1);
		
		simulationPlot.setOrientation(PlotOrientation.VERTICAL);
		simulationPlot.setGap(20);
		
		return new JFreeChart("Miscellaneous", 
					 	      JFreeChart.DEFAULT_TITLE_FONT, 
					          simulationPlot, 
					          true);
	}
	
	// Plots associated with typical aircraft instrumentation
	private JFreeChart makeControlsPlots(HashMap<PlotType, XYPlot> plotLists) {
		CombinedDomainXYPlot simulationPlot = new CombinedDomainXYPlot(new NumberAxis("Time [sec]"));
		
		simulationPlot.add(plotLists.get(PlotType.ELEVATOR), 1);
		simulationPlot.add(plotLists.get(PlotType.AILERON),  1);
		simulationPlot.add(plotLists.get(PlotType.RUDDER),   1);
		simulationPlot.add(plotLists.get(PlotType.THROTTLE), 1);
		simulationPlot.add(plotLists.get(PlotType.FLAPS),    1);
		
		simulationPlot.setOrientation(PlotOrientation.VERTICAL);
		simulationPlot.setGap(20);
		
		return new JFreeChart("Controls", 
					 	      JFreeChart.DEFAULT_TITLE_FONT, 
					          simulationPlot, 
					          true);
	}
	
}
