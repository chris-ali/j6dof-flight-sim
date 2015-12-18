package com.chrisali.javaflightsim.utilities.plotting;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.*;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SimulationPlots extends ApplicationFrame {

	private static final long serialVersionUID = 1L;
	
	// Creates plots of 3 ([phi, theta, psi], [u, v, w], etc) for all variables 
	// monitored in the logsOut ArrayList
	public SimulationPlots(ArrayList<Double[]> logsOut, String applicationTitle) {
		super(applicationTitle);
		
		XYSeries uData      = new XYSeries("u");
		XYSeries vData      = new XYSeries("v");
		XYSeries wData      = new XYSeries("w");
		
		XYSeries northData  = new XYSeries("North");
		XYSeries eastData   = new XYSeries("East");
		XYSeries downData   = new XYSeries("Down");
		
		XYSeries phiData    = new XYSeries("Phi");
		XYSeries thetaData  = new XYSeries("Theta");
		XYSeries psiData    = new XYSeries("Psi");
		
		XYSeries pData      = new XYSeries("p");
		XYSeries qData      = new XYSeries("q");
		XYSeries rData      = new XYSeries("r");
				
		XYSeries axData     = new XYSeries("a_x");
		XYSeries ayData     = new XYSeries("a_y");
		XYSeries azData     = new XYSeries("a_z");		
		
		XYSeries lData      = new XYSeries("L");
		XYSeries mData      = new XYSeries("M");
		XYSeries nData      = new XYSeries("N");
		
		XYSeries betaData   = new XYSeries("Beta");
		XYSeries alphaData  = new XYSeries("Alpha");
		
		for (Double[] y : logsOut) {
			uData.add(y[0],y[1]);      // u
			vData.add(y[0],y[2]);      // v
			wData.add(y[0],y[3]);      // w
			
			northData.add(y[0],y[19]); // N
			eastData.add(y[0],y[20]);  // E
			downData.add(y[0],y[21]);  // D
			
			phiData.add(y[0],y[7]);    // phi
			thetaData.add(y[0],y[8]);  // theta
			psiData.add(y[0],y[9]);    // psi
			
			pData.add(y[0],y[4]);      // p
			qData.add(y[0],y[5]);      // q
			rData.add(y[0],y[6]);      // r
			
			axData.add(y[0],y[13]);    // a_x
			ayData.add(y[0],y[14]);    // a_y
			azData.add(y[0],y[15]);    // a_z
			
			lData.add(y[0],y[16]);     // L
			mData.add(y[0],y[17]);     // M
			nData.add(y[0],y[18]);     // N
			
			betaData.add(y[0],y[11]);  // beta
			alphaData.add(y[0],y[12]); // alpha
		}
		
		XYSeriesCollection linearVelSeries = new XYSeriesCollection();
		linearVelSeries.addSeries(uData);
		linearVelSeries.addSeries(vData);
		linearVelSeries.addSeries(wData);
		
		XYSeriesCollection nedPositionSeries = new XYSeriesCollection();
		nedPositionSeries.addSeries(northData);
		nedPositionSeries.addSeries(eastData);
		nedPositionSeries.addSeries(downData);
		
		XYSeriesCollection eulerAnglesSeries = new XYSeriesCollection();
		eulerAnglesSeries.addSeries(phiData);
		eulerAnglesSeries.addSeries(thetaData);
		eulerAnglesSeries.addSeries(psiData);
		
		XYSeriesCollection angularRatesSeries = new XYSeriesCollection();
		angularRatesSeries.addSeries(pData);
		angularRatesSeries.addSeries(qData);
		angularRatesSeries.addSeries(rData);
		
		XYSeriesCollection linearAccelSeries = new XYSeriesCollection();
		linearAccelSeries.addSeries(axData);
		linearAccelSeries.addSeries(ayData);
		linearAccelSeries.addSeries(azData);
		
		XYSeriesCollection totalMomentSeries = new XYSeriesCollection();
		totalMomentSeries.addSeries(lData);
		totalMomentSeries.addSeries(mData);
		totalMomentSeries.addSeries(nData);
		
		XYSeriesCollection windParamSeries = new XYSeriesCollection();
		windParamSeries.addSeries(betaData);
		windParamSeries.addSeries(alphaData);
		
		XYPlot linearVelPlot    = new XYPlot(linearVelSeries,    
											 null, 
											 new NumberAxis("Linear Velocities [ft/sec]"), 
									 		 new StandardXYItemRenderer()); 
		XYPlot nedPositionPlot  = new XYPlot(nedPositionSeries, 
											 null, 
											 new NumberAxis("NED Position [ft]"), 
											 new StandardXYItemRenderer());
		XYPlot eulerAnglesPlot  = new XYPlot(eulerAnglesSeries, 
											 null, 
											 new NumberAxis("Euler Angles [rad]"), 
											 new StandardXYItemRenderer());
		XYPlot angularRatesPlot = new XYPlot(angularRatesSeries, 
										     null, 
									     	 new NumberAxis("Angular Rates [rad/sec]"), 
									     	 new StandardXYItemRenderer());
		XYPlot linearAccelPlot  = new XYPlot(linearAccelSeries, 
											 null, 
											 new NumberAxis("Linear Accelerations [ft/sec^2]"), 
											 new StandardXYItemRenderer());
		XYPlot totalMomentPlot  = new XYPlot(totalMomentSeries, 
											 null, 
											 new NumberAxis("Total Moments [fl*lb]"), 
											 new StandardXYItemRenderer()); 
		XYPlot windParamPlot    = new XYPlot(windParamSeries, 
											 null, 
											 new NumberAxis("Wind Parameters [rad]"), 
											 new StandardXYItemRenderer());
		
		CombinedDomainXYPlot simulationPlot1 = new CombinedDomainXYPlot(new NumberAxis("Time [sec]"));
		simulationPlot1.add(linearVelPlot,    1);
		simulationPlot1.add(nedPositionPlot,  1);
		simulationPlot1.add(eulerAnglesPlot,  1);
		simulationPlot1.add(angularRatesPlot, 1);
		simulationPlot1.add(linearAccelPlot,  1);
		simulationPlot1.add(totalMomentPlot,  1);
		simulationPlot1.add(windParamPlot,    1);
		simulationPlot1.setOrientation(PlotOrientation.VERTICAL);
		simulationPlot1.setGap(10);
		
		JFreeChart sixDOFPlots = new JFreeChart("6DOF States Plots", 
										 	    JFreeChart.DEFAULT_TITLE_FONT, 
										        simulationPlot1, 
										        true);
		
		// Create Chart Panels to populate AWT window 
		ChartPanel plotPanel = new ChartPanel(sixDOFPlots);
		plotPanel.setPreferredSize(new java.awt.Dimension(1000, 900));
		setContentPane(plotPanel);
	}
	
	public static void generatePlotWindows(SimulationPlots simPlots) {
		simPlots.pack();
		RefineryUtilities.centerFrameOnScreen(simPlots);
		simPlots.setVisible(true);
	}
	
	// Save plot as a jpg file in project folder
	public static void savePlotJPG(JFreeChart xyPlot)  throws IOException  {
		File plotPic = new File("xyPlot.jpeg");
		ChartUtilities.saveChartAsJPEG(plotPic, xyPlot, 640, 480);
	}

}
