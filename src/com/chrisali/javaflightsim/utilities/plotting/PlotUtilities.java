package com.chrisali.javaflightsim.utilities.plotting;

import java.util.EnumMap;
import java.util.List;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;

import com.chrisali.javaflightsim.utilities.integration.SimOuts;

/**
 * Contains methods to create plots from simulation data. {@link MakePlots} and {@link SimulationPlots} call on methods from this class to generate the plot objects on AWT windows
 */
public class PlotUtilities {
	private static EnumMap<PlotType, XYPlot> plotLists = new EnumMap<PlotType, XYPlot>(PlotType.class);

	/**
	 * Populates the {@link PlotUtilities#plotLists} EnumMap with XYPlot objects created from the logsOut ArrayList that is passed in. 
	 * It first creates {@link XYSeries} objects with data from logsOut, adds those series to {@link XYSeriesCollection}, adds those 
	 * series collections to {@link XYPlot} objects, and finally puts the XYPlot objects into {@link PlotUtilities#plotLists}
	 */
	protected static void makePlotLists(List<EnumMap<SimOuts, Double>> logsOut) {
		
		// Create XY series for each set of data
		
		XYSeries uData        = new XYSeries("u");
		XYSeries vData        = new XYSeries("v");
		XYSeries wData        = new XYSeries("w");
		
		XYSeries posData      = new XYSeries("Position");
		XYSeries altData      = new XYSeries("Altitude");
		
		XYSeries altDotData   = new XYSeries("Alt Dot");
		
		XYSeries phiData      = new XYSeries("Phi");
		XYSeries thetaData    = new XYSeries("Theta");
		XYSeries psiData      = new XYSeries("Psi");
		
		XYSeries pData        = new XYSeries("p");
		XYSeries qData        = new XYSeries("q");
		XYSeries rData        = new XYSeries("r");
				
		XYSeries axData       = new XYSeries("a_x");
		XYSeries ayData       = new XYSeries("a_y");
		XYSeries azData       = new XYSeries("a_z");		
		
		XYSeries lData        = new XYSeries("L");
		XYSeries mData        = new XYSeries("M");
		XYSeries nData        = new XYSeries("N");
		
		XYSeries tasData      = new XYSeries("TAS");
		
		XYSeries betaData     = new XYSeries("Beta");
		XYSeries alphaData    = new XYSeries("Alpha");
		
		XYSeries elevData     = new XYSeries("Elevator");
		XYSeries ailData      = new XYSeries("Aileron");
		XYSeries rudData      = new XYSeries("Rudder");
		XYSeries throtData    = new XYSeries("Throttle");
		XYSeries flapData     = new XYSeries("Flaps");
		
		XYSeries alphaDotData = new XYSeries("Alpha Dot");
		XYSeries machData     = new XYSeries("Mach");
		
		// Add data from logsOut to each XYSeries
		
		for (EnumMap<SimOuts, Double> simOut : logsOut) {
			uData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.U));
			vData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.V));
			wData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.W));
			
			posData.add(simOut.get(SimOuts.EAST),simOut.get(SimOuts.NORTH));
			altData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.ALT));
			
			altDotData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.ALT_DOT));
			
			phiData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.PHI));
			thetaData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.THETA));
			psiData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.PSI));
			
			pData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.P));
			qData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.Q));
			rData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.R));
			
			axData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.AN_X));
			ayData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.AN_Y));
			azData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.AN_Z));
			
			lData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.L));
			mData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.M));
			nData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.N));
			
			tasData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.TAS));
			
			betaData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.BETA));
			alphaData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.ALPHA));
			
			elevData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.ELEVATOR));
			ailData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.AILERON));
			rudData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.RUDDER));
			throtData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.THROTTLE_1));
			flapData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.FLAPS));
			
			alphaDotData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.ALPHA_DOT));
			machData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.MACH));
		}
		
		// Create XYSeriesCollections for each desired plot and add series to them
		
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
		
		XYSeriesCollection elevSeries		  = new XYSeriesCollection();
		elevSeries.addSeries(elevData);
		
		XYSeriesCollection ailSeries		  = new XYSeriesCollection();
		ailSeries.addSeries(ailData);
		
		XYSeriesCollection rudSeries		  = new XYSeriesCollection();
		rudSeries.addSeries(rudData);
		
		XYSeriesCollection throtSeries		  = new XYSeriesCollection();
		throtSeries.addSeries(throtData);
		
		XYSeriesCollection flapSeries		  = new XYSeriesCollection();
		flapSeries.addSeries(flapData);
		
		XYSeriesCollection alphaDotSeries	  = new XYSeriesCollection();
		alphaDotSeries.addSeries(alphaDotData);
		
		XYSeriesCollection machSeries		  = new XYSeriesCollection();
		machSeries.addSeries(machData);
		
		// Create plots, add series collections to them and put the plots into a HashMap with an enum key
		
		XYPlot linearVelPlot    = new XYPlot(linearVelSeries,    
											 null,
											 new NumberAxis("Body Velocity [ft/sec]"), 
									 		 new StandardXYItemRenderer()); 
		
		plotLists.put(PlotType.VELOCITY, linearVelPlot);
		
		XYPlot positionPlot     = new XYPlot(positionSeries, 
											 new NumberAxis("East [ft]"), 
											 new NumberAxis("North [ft]"), 
											 new StandardXYItemRenderer());
		
		plotLists.put(PlotType.POSITION, positionPlot);
		
		XYPlot altitudePlot     = new XYPlot(altitudeSeries, 
											 null, 
											 new NumberAxis("Altitude [ft]"), 
											 new StandardXYItemRenderer());

		plotLists.put(PlotType.ALTITUDE, altitudePlot);
		
		XYPlot altDotPlot       = new XYPlot(altDotSeries, 
										     null, 
										     new NumberAxis("Vertical Speed [ft/min]"), 
										     new StandardXYItemRenderer());

		plotLists.put(PlotType.VERT_SPEED, altDotPlot);
		
		XYPlot headingPlot      = new XYPlot(headingSeries, 
											 null, 
											 new NumberAxis("Heading [rad]"), 
											 new StandardXYItemRenderer());
							
		plotLists.put(PlotType.HEADING, headingPlot);
		
		XYPlot eulerAnglesPlot  = new XYPlot(eulerAnglesSeries, 
											 null, 
											 new NumberAxis("Angle [rad]"), 
											 new StandardXYItemRenderer());
		
		plotLists.put(PlotType.EULER_ANGLES, eulerAnglesPlot);
		
		XYPlot angularRatesPlot = new XYPlot(angularRatesSeries, 
											 null, 
									     	 new NumberAxis("Angular Rate [rad/sec]"), 
									     	 new StandardXYItemRenderer());
		
		plotLists.put(PlotType.ANGULAR_RATE, angularRatesPlot);
		
		XYPlot linearAccelPlot  = new XYPlot(linearAccelSeries, 
										     null, 
											 new NumberAxis("Acceleration [g]"), 
											 new StandardXYItemRenderer());
		
		plotLists.put(PlotType.ACCELERATION, linearAccelPlot);
		
		XYPlot totalMomentPlot  = new XYPlot(totalMomentSeries, 
											 null, 
											 new NumberAxis("Moment [ft*lb]"), 
											 new StandardXYItemRenderer()); 
		
		plotLists.put(PlotType.MOMENT, totalMomentPlot);
		
		XYPlot tasPlot          = new XYPlot(tasSeries, 
										     null, 
										     new NumberAxis("True Airspeed [ft/sec]"), 
										     new StandardXYItemRenderer());

		plotLists.put(PlotType.TAS, tasPlot);
		
		XYPlot windParamPlot    = new XYPlot(windParamSeries, 
											 null, 
											 new NumberAxis("Angle [rad]"), 
											 new StandardXYItemRenderer());
		
		plotLists.put(PlotType.WIND_PARAM, windParamPlot);
		
		XYPlot elevPlot    		= new XYPlot(elevSeries, 
											 null, 
											 new NumberAxis("Deflection [rad]"), 
											 new StandardXYItemRenderer());

		plotLists.put(PlotType.ELEVATOR, elevPlot);
		
		XYPlot ailPlot		    = new XYPlot(ailSeries, 
											 null, 
											 new NumberAxis("Deflection [rad]"), 
											 new StandardXYItemRenderer());
		
		plotLists.put(PlotType.AILERON, ailPlot);
		
		XYPlot rudPlot    		= new XYPlot(rudSeries, 
											 null, 
											 new NumberAxis("Deflection [rad]"), 
											 new StandardXYItemRenderer());
		
		plotLists.put(PlotType.RUDDER, rudPlot);
		
		XYPlot throtPlot        = new XYPlot(throtSeries, 
											 null, 
											 new NumberAxis("Position [norm]"), 
											 new StandardXYItemRenderer());
		
		plotLists.put(PlotType.THROTTLE, throtPlot);
		
		XYPlot flapPlot         = new XYPlot(flapSeries, 
											 null, 
											 new NumberAxis("Deflection [rad]"), 
											 new StandardXYItemRenderer());
		
		plotLists.put(PlotType.FLAPS, flapPlot);
		
		XYPlot alphaDotPlot     = new XYPlot(alphaDotSeries, 
											 null, 
											 new NumberAxis("Rate [rad/sec]"), 
											 new StandardXYItemRenderer());
							
		plotLists.put(PlotType.ALPHA_DOT, alphaDotPlot);

		XYPlot machPlot         = new XYPlot(machSeries, 
											 null, 
											 new NumberAxis("Mach Number"), 
											 new StandardXYItemRenderer());

		plotLists.put(PlotType.MACH, machPlot);
	}
	
	/**
	 * Used to fit the {@link SimulationPlots} objects to AWT windows, and then make those windows visible to the user 
	 */
	protected static void generatePlotWindows(SimulationPlots simPlots) {
		simPlots.pack();
		RefineryUtilities.centerFrameOnScreen(simPlots);
		simPlots.setVisible(true);
	}
	
	/** 
	 * Returns an EnumMap of {@link XYPlot} objects
	 * @see SimulationPlots
	 * @see PlotUtilities
	 */
	public static EnumMap<PlotType, XYPlot> getPlotLists() {return plotLists;}
}
