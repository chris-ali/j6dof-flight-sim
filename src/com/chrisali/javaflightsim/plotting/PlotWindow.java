package com.chrisali.javaflightsim.plotting;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.ui.RefineryUtilities;

import com.chrisali.javaflightsim.simulation.aircraft.Aircraft;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.integration.SimOuts;

/**
 * Generates a window of JFreeChart plots in tabs containing relevant data from the simulation
 */
public class PlotWindow extends JFrame {

	private static final long serialVersionUID = -4197697777449504415L;
	
	private JTabbedPane tabPane;
	private List<SimulationPlot> plotList;
	private PlotCloseListener plotCloseListener;
	
	/**
	 * Plots data from the simulation in a Swing window. It loops through 
	 * the {@link PlotWindow#simPlotCategories} set to create {@link SimulationPlot} objects using the data 
	 * from {@link Integrate6DOFEquations#getLogsOut()}, and assigns them to tabs in a JTabbedPane. 
	 * 
	 * @param String simPlotCetegories
	 * @param List<EnumMap<SimOuts, Double>> logsOut
	 * @param Aircraft aircraft
	 */
	public PlotWindow(List<EnumMap<SimOuts, Double>> logsOut, 
					 HashSet<String> simPlotCategories,
					 Aircraft aircraft) {
		super(aircraft.getName() + " Plots");
		setLayout(new BorderLayout());
		
		plotList = new ArrayList<>();
		
		//------------------ Tab Pane ------------------------------
		
		tabPane = new JTabbedPane();
		for (String plotTitle : simPlotCategories) {
			try {Thread.sleep(125);} 
			catch (InterruptedException e) {}
			
			SimulationPlot plotObject = new SimulationPlot(logsOut, plotTitle);
			tabPane.add(plotTitle, plotObject);
			plotList.add(plotObject);
		}
		tabPane.setSelectedIndex(0);
		tabPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				PlotWindow.this.setSize(tabPane.getSelectedComponent().getPreferredSize());
			}
		});
		add(tabPane, BorderLayout.CENTER);
		
		//================== Window Settings ====================================
		
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				setVisible(false);
				
				if (plotCloseListener != null)
					plotCloseListener.plotWindowClosed();
			}
		});
		
		setSize(tabPane.getSelectedComponent().getPreferredSize());
		RefineryUtilities.centerFrameOnScreen(this);
		setVisible(true);
		
	}
	
	/**
	 * Commands each plot in the plotList ArrayList to update its XYSeries values using data from logsOut. 
	 * This will also set the plot window visible again if it has been closed.
	 * 
	 * @param logsOut
	 */
	public void refreshPlots(List<EnumMap<SimOuts, Double>> logsOut) {
		for (SimulationPlot plot : plotList) {
			plot.updateXYSeriesData(logsOut);
			plot.getChartPanel().repaint();
		}
		
		if (!isVisible())
			setVisible(true);
	}

	public void setPlotCloseListener (PlotCloseListener plotCloseListener) {
		this.plotCloseListener = plotCloseListener;
	}
}
