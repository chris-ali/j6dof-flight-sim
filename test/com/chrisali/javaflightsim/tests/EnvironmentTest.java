package com.chrisali.javaflightsim.tests;

import java.util.EnumMap;
import java.util.Map;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import com.chrisali.javaflightsim.simulation.enviroment.Environment;
import com.chrisali.javaflightsim.simulation.enviroment.EnvironmentParameters;

public class EnvironmentTest extends ApplicationFrame {
	private static final long serialVersionUID = 1L;

	public EnvironmentTest() {
		super("Environment Test");
		
		Map<EnvironmentParameters, Double> envData = new EnumMap<EnvironmentParameters, Double>(EnvironmentParameters.class);
		
		XYSeries tData       = new XYSeries("T");
		XYSeries pData       = new XYSeries("P");
		XYSeries rhoData     = new XYSeries("Rho");
		XYSeries aData     	 = new XYSeries("Speed of Sound");
		
		XYSeries gravData    = new XYSeries("Gravity");
		
		XYSeries windSpdNData = new XYSeries("N Wind Speed");
		XYSeries windSpdEData = new XYSeries("E Wind Speed");
		XYSeries windSpdDData = new XYSeries("D Wind Speed");
		
		XYSeriesCollection tSeries    = new XYSeriesCollection();
		XYSeriesCollection pSeries    = new XYSeriesCollection();
		XYSeriesCollection rhoSeries  = new XYSeriesCollection();
		XYSeriesCollection aSeries    = new XYSeriesCollection();
		XYSeriesCollection gravSeries = new XYSeriesCollection();
		XYSeriesCollection windSeries = new XYSeriesCollection();
		
		for (double alt=0; alt<60000; alt+=10) {
			Environment.setWindDir(alt*6/1000);
			Environment.setWindSpeed(alt/6000);
			envData = Environment.updateEnvironmentParams(new double[] {0, 0, alt});
			
			// Add envData to each XYSeries
			tData.add(alt,envData.get(EnvironmentParameters.T));
			pData.add(alt,envData.get(EnvironmentParameters.P));
			rhoData.add(alt,envData.get(EnvironmentParameters.RHO));
			aData.add(alt,envData.get(EnvironmentParameters.A));
			
			gravData.add(alt,envData.get(EnvironmentParameters.GRAVITY));
			
			windSpdNData.add(alt,envData.get(EnvironmentParameters.WIND_SPEED_N));
			windSpdEData.add(alt,envData.get(EnvironmentParameters.WIND_SPEED_E));
			windSpdDData.add(alt,envData.get(EnvironmentParameters.WIND_SPEED_D));
		}
		
		// Add series data to XYSeriesCollections
		tSeries.addSeries(tData);
		pSeries.addSeries(pData);
		rhoSeries.addSeries(rhoData);
		aSeries.addSeries(aData);
		
		gravSeries.addSeries(gravData);
		
		windSeries.addSeries(windSpdNData);
		windSeries.addSeries(windSpdEData);
		windSeries.addSeries(windSpdDData);
	
		// Create plots and add XYSeriesCollections to them		
		XYPlot tPlot    = new XYPlot(tSeries,    
							 		 null,
									 new NumberAxis("Temperature [R]"), 
							 		 new StandardXYItemRenderer()); 
		
		XYPlot pPlot    = new XYPlot(pSeries,    
							 		 null,
									 new NumberAxis("Pressure [Slug/ft^3]"), 
							 		 new StandardXYItemRenderer()); 
		
		XYPlot rhoPlot  = new XYPlot(rhoSeries,    
							 		 null,
									 new NumberAxis("Density [lbf/ft^3]"), 
							 		 new StandardXYItemRenderer()); 
		
		XYPlot aPlot    = new XYPlot(aSeries,    
							 		 null,
									 new NumberAxis("Speed of Sound [ft/sec]"), 
							 		 new StandardXYItemRenderer()); 

		XYPlot gravPlot = new XYPlot(gravSeries, 
									 null, 
									 new NumberAxis("Gravity [ft/sec^2]"), 
									 new StandardXYItemRenderer());

		XYPlot windPlot = new XYPlot(windSeries, 
									 null, 
									 new NumberAxis("Wind Speed"), 
									 new StandardXYItemRenderer());

		// Create CombinedDomainXYPlots and add XYPlots to them
		CombinedDomainXYPlot environmentPlot = new CombinedDomainXYPlot(new NumberAxis("Altitude [ft]"));
		
		environmentPlot.add(tPlot,   3);
		environmentPlot.add(pPlot,   3);
		environmentPlot.add(rhoPlot, 3);
		environmentPlot.add(aPlot,   3);
		
		environmentPlot.add(gravPlot, 1);
		environmentPlot.add(windPlot, 2);
		
		environmentPlot.setOrientation(PlotOrientation.VERTICAL);
		environmentPlot.setGap(20);
				
		// Creates a chart panel to populate AWT window 
		ChartPanel envPlotPanel = new ChartPanel(new JFreeChart("Environment Parameters", 
													 	        JFreeChart.DEFAULT_TITLE_FONT, 
													 	        environmentPlot, 
													            true));
		
		envPlotPanel.setPreferredSize(new java.awt.Dimension(1000, 950));
		setContentPane(envPlotPanel);
		
		this.pack();
		RefineryUtilities.centerFrameOnScreen(this);
		this.setVisible(true);
	}

	public static void main(String[] args) {new EnvironmentTest();}

}
