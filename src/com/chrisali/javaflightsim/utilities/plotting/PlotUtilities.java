package com.chrisali.javaflightsim.utilities.plotting;

import java.util.ArrayList;
import java.util.HashMap;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;

public class PlotUtilities {
	private static HashMap<String, XYPlot> plotLists = new HashMap<>();
	
	static void makePlotLists(ArrayList<Double[]> logsOut) {
		XYSeries uData      = new XYSeries("u");
		XYSeries vData      = new XYSeries("v");
		XYSeries wData      = new XYSeries("w");
		
		XYSeries posData    = new XYSeries("Position");
		XYSeries altData    = new XYSeries("Altitude");
		
		XYSeries altDotData = new XYSeries("Alt Dot");
		
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
		
		XYSeries tasData    = new XYSeries("TAS");
		
		XYSeries betaData   = new XYSeries("Beta");
		XYSeries alphaData  = new XYSeries("Alpha");
		
		for (Double[] y : logsOut) {
			uData.add(y[0],y[1]);        // u
			vData.add(y[0],y[2]);        // v
			wData.add(y[0],y[3]);        // w
			
			posData.add(y[5],y[4]);      // NE Position
			altData.add(y[0],y[6]);      // Altitude
			
			altDotData.add(y[0], y[25]); // Alt Dot
			
			phiData.add(y[0],y[7]);      // phi
			thetaData.add(y[0],y[8]);    // theta
			psiData.add(y[0],y[9]);      // psi
			
			pData.add(y[0],y[10]);       // p
			qData.add(y[0],y[11]);       // q
			rData.add(y[0],y[12]);       // r
			
			axData.add(y[0],y[22]);      // a_x
			ayData.add(y[0],y[23]);      // a_y
			azData.add(y[0],y[24]);      // a_z
			
			lData.add(y[0],y[19]);       // L
			mData.add(y[0],y[20]);       // M
			nData.add(y[0],y[21]);       // N
			
			tasData.add(y[0], y[13]);    // TAS
			
			betaData.add(y[0],y[14]);    // beta
			alphaData.add(y[0],y[15]);   // alpha
		}
		
		XYSeriesCollection linearVelSeries    = new XYSeriesCollection();
		linearVelSeries.addSeries(uData);
		linearVelSeries.addSeries(vData);
		linearVelSeries.addSeries(wData);
		
		XYSeriesCollection positionSeries     = new XYSeriesCollection();
		positionSeries.addSeries(posData);
		
		XYSeriesCollection altitudeSeries     = new XYSeriesCollection();
		altitudeSeries.addSeries(altData);
		
		XYSeriesCollection altDotSeries       = new XYSeriesCollection();
		altDotSeries.addSeries(altDotData);
		
		XYSeriesCollection headingSeries      = new XYSeriesCollection();
		headingSeries.addSeries(psiData);
		
		XYSeriesCollection eulerAnglesSeries  = new XYSeriesCollection();
		eulerAnglesSeries.addSeries(phiData);
		eulerAnglesSeries.addSeries(thetaData);
		
		XYSeriesCollection angularRatesSeries = new XYSeriesCollection();
		angularRatesSeries.addSeries(pData);
		angularRatesSeries.addSeries(qData);
		angularRatesSeries.addSeries(rData);
		
		XYSeriesCollection linearAccelSeries  = new XYSeriesCollection();
		linearAccelSeries.addSeries(axData);
		linearAccelSeries.addSeries(ayData);
		linearAccelSeries.addSeries(azData);
		
		XYSeriesCollection totalMomentSeries  = new XYSeriesCollection();
		totalMomentSeries.addSeries(lData);
		totalMomentSeries.addSeries(mData);
		totalMomentSeries.addSeries(nData);
		
		XYSeriesCollection tasSeries          = new XYSeriesCollection();
		tasSeries.addSeries(tasData);
		
		XYSeriesCollection windParamSeries    = new XYSeriesCollection();
		windParamSeries.addSeries(betaData);
		windParamSeries.addSeries(alphaData);
		
		XYPlot linearVelPlot    = new XYPlot(linearVelSeries,    
											 null,
											 new NumberAxis("Velocity [ft/sec]"), 
									 		 new StandardXYItemRenderer()); 
		
		plotLists.put("Velocity", linearVelPlot);
		
		XYPlot positionPlot     = new XYPlot(positionSeries, 
											 new NumberAxis("East [ft]"), 
											 new NumberAxis("North [ft]"), 
											 new StandardXYItemRenderer());
		
		plotLists.put("Position", positionPlot);
		
		XYPlot altitudePlot     = new XYPlot(altitudeSeries, 
											 null, 
											 new NumberAxis("Altitude [ft]"), 
											 new StandardXYItemRenderer());

		plotLists.put("Altitude", altitudePlot);
		
		XYPlot altDotPlot       = new XYPlot(altDotSeries, 
										     null, 
										     new NumberAxis("Vertical Speed [ft/sec]"), 
										     new StandardXYItemRenderer());

		plotLists.put("Vertical Speed", altDotPlot);
		
		XYPlot headingPlot      = new XYPlot(headingSeries, 
											 null, 
											 new NumberAxis("Heading [rad]"), 
											 new StandardXYItemRenderer());
							
		plotLists.put("Heading", headingPlot);
		
		XYPlot eulerAnglesPlot  = new XYPlot(eulerAnglesSeries, 
											 null, 
											 new NumberAxis("Angle [rad]"), 
											 new StandardXYItemRenderer());
		
		plotLists.put("Euler Angles", eulerAnglesPlot);
		
		XYPlot angularRatesPlot = new XYPlot(angularRatesSeries, 
											 null, 
									     	 new NumberAxis("Rate [rad/sec]"), 
									     	 new StandardXYItemRenderer());
		
		plotLists.put("Angular Rates", angularRatesPlot);
		
		XYPlot linearAccelPlot  = new XYPlot(linearAccelSeries, 
										     null, 
											 new NumberAxis("Acceleration [ft/sec^2]"), 
											 new StandardXYItemRenderer());
		
		plotLists.put("Accelerations", linearAccelPlot);
		
		XYPlot totalMomentPlot  = new XYPlot(totalMomentSeries, 
											 null, 
											 new NumberAxis("Moment [ft*lb]"), 
											 new StandardXYItemRenderer()); 
		
		plotLists.put("Total Moments", totalMomentPlot);
		
		XYPlot tasPlot          = new XYPlot(tasSeries, 
										     null, 
										     new NumberAxis("True Airspeed [ft/sec]"), 
										     new StandardXYItemRenderer());

		plotLists.put("Wind Parameters", tasPlot);
		
		XYPlot windParamPlot    = new XYPlot(windParamSeries, 
											 null, 
											 new NumberAxis("Angle [rad]"), 
											 new StandardXYItemRenderer());
		
		plotLists.put("TAS", windParamPlot);
	}
	
	public static HashMap<String, XYPlot> getPlotLists() {return plotLists;}
	
	static void generatePlotWindows(SimulationPlots simPlots) {
		simPlots.pack();
		RefineryUtilities.centerFrameOnScreen(simPlots);
		simPlots.setVisible(true);
	}
}
