/*******************************************************************************
 * Copyright (C) 2016-2018 Christopher Ali
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *  If you have any questions about this project, you can visit
 *  the project's GitHub repository at: http://github.com/chris-ali/j6dof-flight-sim/
 ******************************************************************************/
package com.chrisali.javaflightsim.swing.plotting;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.chrisali.javaflightsim.simulation.integration.SimOuts;
import com.chrisali.javaflightsim.swing.plotting.PlotConfiguration.SubPlotBundle;
import com.chrisali.javaflightsim.swing.plotting.PlotConfiguration.SubPlotOptions;

/**
 * Contains a {@link CombinedDomainXYPlot} object, consisting of group of {@link XYPlot} objects.   
 * It generates a plot in Swing as a JComponent used in the JTabbedPane of {@link PlotWindow}. 
 * The plot created depends on the settings contained in {@link SubPlotBundle}
 */
public class SimulationPlot extends JComponent {

	private static final long serialVersionUID = -1885385597969791076L;

	private static final Logger logger = LogManager.getLogger(SimulationPlot.class);
	
	private List<XYPlot> plotList;
	
	private Map<SimOuts, XYSeries> xySeriesData;
	
	private Map<String, XYSeriesCollection> xyCollections;

	private NumberAxis domainAxis;
	
	private Map<String, NumberAxis> rangeAxes;
			
	private CombinedDomainXYPlot combinedDomPlot;

	/**
	 * Creates plots for data contained in the logsOut List using configuration defined in
	 * bundle 
	 * 
	 * @param logsOut
	 * @param bundle
	 */
	public SimulationPlot(List<Map<SimOuts, Double>> logsOut, SubPlotBundle bundle) {
		logger.debug("Generating a subplot bundle for " + bundle.getTitle() + "...");
				
		plotList = new LinkedList<>();
		xySeriesData = new LinkedHashMap<>();
		xyCollections = new LinkedHashMap<>();
		rangeAxes = new LinkedHashMap<>();
		
		try {
			createPlots(logsOut, bundle);			
		} catch (Exception e) {
			logger.error("An error occurred while trying to create the " + bundle.getTitle() + " plots!", e);
		}
				
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(bundle.getSizeXPixels(), bundle.getSizeYPixels()));
		
		add(generateChartPanel(bundle), BorderLayout.CENTER);
		
		logger.debug("...done!");
	}
	
	/**
	 *  Generates a {@link ChartPanel} object using the list of {@link XYPlot} objects generated in 
	 *  this class and settings found in {@link SubPlotBundle} 
	 *
	 * @param bundle
	 * @return ChartPanel object
	 */
	private ChartPanel generateChartPanel(SubPlotBundle bundle) {
		for (XYPlot plot : plotList)
			combinedDomPlot.add(plot, 1);
			
		combinedDomPlot.setOrientation(PlotOrientation.VERTICAL);
		combinedDomPlot.setGap(20);
		
		JFreeChart chart = new JFreeChart(bundle.getTitle(), 
								 	      JFreeChart.DEFAULT_TITLE_FONT, 
								 	      combinedDomPlot, 
								          true);
		return new ChartPanel(chart);
	}
	
	/**
	 * Populates the {@link plotLists} List with {@link XYPlot} objects created from the logsOut ArrayList 
	 * argument. It first creates {@link XYSeries} objects with data from logsOut, adds those to 
	 * {@link XYSeriesCollection}, adds those series collections to {@link XYPlot} objects, and finally 
	 * puts the XYPlot objects into {@link plotList}. The types of {@link XYPlot} objects generated 
	 * comes from settings in {@link SubPlotBundle}
	 * 
	 * @param logsOut
	 * @param bundle
	 */
	private void createPlots(List<Map<SimOuts, Double>> logsOut, SubPlotBundle bundle) {		
		for (SubPlotOptions option : bundle.getSubPlots()) {
			XYSeriesCollection collection = new XYSeriesCollection();
			
			for (SimOuts simout : option.getyData()) {
				XYSeries series = new XYSeries(simout.toString());
				xySeriesData.put(simout, series);
				collection.addSeries(series);				
			}
						
			domainAxis = new NumberAxis(option.getxAxisName());
			rangeAxes.put(option.getTitle(), new NumberAxis(option.getyAxisName()));
			
			xyCollections.put(option.getTitle(), collection);
		}
		
		combinedDomPlot = new CombinedDomainXYPlot(domainAxis);
		
		updateXYSeriesData(logsOut, bundle);

		for (Map.Entry<String, XYSeriesCollection> entry : xyCollections.entrySet()) {
			logger.debug("Creating a subplot called: " + entry.getKey() + "...");
			
			XYPlot subPlot = new XYPlot(entry.getValue(),
										domainAxis,
										rangeAxes.get(entry.getKey()), 
										new StandardXYItemRenderer());
						
			plotList.add(subPlot);
		}
	}
	
	/**
	 * Update {@link XYSeries} objects with new data from a thread-safe logsOut list
	 * 
	 * @param logsOut
	 * @param bundle
	 */
	protected void updateXYSeriesData(List<Map<SimOuts, Double>> logsOut, SubPlotBundle bundle) {
		// Clear old XV series values 
		for (Map.Entry<SimOuts, XYSeries> entry : xySeriesData.entrySet())
			entry.getValue().clear();
		
		// Only notify of a SeriesChangeEvent at the end of the loop
		for (Iterator<Map<SimOuts, Double>> logsOutItr = logsOut.iterator(); logsOutItr.hasNext();) {
			Map<SimOuts, Double> simOut = logsOutItr.next();
			
			for (Map.Entry<SimOuts, XYSeries> entry : xySeriesData.entrySet()) {
				SimOuts yVal = entry.getKey();
				SimOuts xVal = bundle.getSubPlots().get(0).getxData();
			
				entry.getValue().add(simOut.get(xVal), simOut.get(yVal), !logsOutItr.hasNext());
			}						
		}
		
		// Bound the minimum X Axis value to the first time value in the data series
		for (Map.Entry<SimOuts, XYSeries> entry : xySeriesData.entrySet()) {
			XYSeries series = entry.getValue();
			domainAxis.setRange(series.getMinX(), series.getMaxX());
		}
				
		// Update with new time axis
		combinedDomPlot.setDomainAxis(domainAxis);
		for (int i = 0; i < combinedDomPlot.getRangeAxisCount(); i++) {
			if (combinedDomPlot.getRangeAxis(i) != null) {
				combinedDomPlot.getRangeAxis(i).setAutoRange(false);
				combinedDomPlot.getRangeAxis(i).resizeRange(3);
			}
		}
	}
}
