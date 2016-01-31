package com.chrisali.javaflightsim.utilities.plotting;

import java.util.ArrayList;
import java.util.EnumMap;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.ApplicationFrame;

import com.chrisali.javaflightsim.utilities.integration.SimOuts;

/**
 * This object contains a {@link CombinedDomainXYPlot} object, consisting of group of {@link XYPlot} objects. It generates a plot windwow in AWT using {@link PlotUtilities#generatePlotWindows(SimulationPlots)}.   
 * an AWT window with a plot. The plot window displayed depends on the windowTitle String argument passed in. 
 * @see MakePlots
 */
public class SimulationPlots extends ApplicationFrame {

	private static final long serialVersionUID = 1L;
	
	// Creates plots for variables monitored in the logsOut ArrayList
	public SimulationPlots(ArrayList<EnumMap<SimOuts, Double>> logsOut, String windowTitle, String aircraftName) {
		super(aircraftName + " " + windowTitle);
		
		PlotUtilities.makePlotLists(logsOut);
		
		// Select from methods below to create a chart panels to populate AWT window 
		switch (windowTitle) {
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
	
	/**
	 *  Generates a {@link JFreeChart} object associated with rates and accelerations (Angular Rates, Linear Velocities and Linear Accelerations) on a {@link CombinedDomainXYPlot}.
	 */
	private JFreeChart makeRatesPlots(EnumMap<PlotType, XYPlot> plotLists) {
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
	
	/**
	 *  Generates a {@link JFreeChart} object associated with aircraft position (North vs East) on a {@link CombinedDomainXYPlot}.
	 */
	private JFreeChart makePositionPlot(EnumMap<PlotType, XYPlot> plotLists) {
		CombinedDomainXYPlot simulationPlot = new CombinedDomainXYPlot(new NumberAxis("East [ft]"));
		
		simulationPlot.add(plotLists.get(PlotType.POSITION), 1);
		
		simulationPlot.setOrientation(PlotOrientation.VERTICAL);
		simulationPlot.setGap(20);
		
		return new JFreeChart("Position", 
					 	      JFreeChart.DEFAULT_TITLE_FONT, 
					          simulationPlot, 
					          true);
	}
	
	/**
	 *  Generates a {@link JFreeChart} object associated with instrumentation data (Pitch, Roll, Airspeed, Heading, Altitude and Vertical Speed) on a {@link CombinedDomainXYPlot}.
	 */
	private JFreeChart makeInstrumentPlots(EnumMap<PlotType, XYPlot> plotLists) {
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
	
	/**
	 *  Generates a {@link JFreeChart} object associated with miscellaneous air data (Alpha, Beta, Alphadot and Mach) on a {@link CombinedDomainXYPlot}.
	 */
	private JFreeChart makeMiscPlots(EnumMap<PlotType, XYPlot> plotLists) {
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
	
	/**
	 *  Generates a {@link JFreeChart} object associated with aircraft controls (Elevator, Aileron, Rudder, Throttle, Flaps) on a {@link CombinedDomainXYPlot}.
	 */
	private JFreeChart makeControlsPlots(EnumMap<PlotType, XYPlot> plotLists) {
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
