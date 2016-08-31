package com.chrisali.javaflightsim.plotting;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JComponent;

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

/**
 * Contains a {@link CombinedDomainXYPlot} object, consisting of group of {@link XYPlot} objects.   
 * It generates a plot in Swing as a JComponent used in the JTabbedPane of {@link PlotWindow}. 
 * The plot created depends on the windowTitle String argument passed in. 
 */
public class SimulationPlot extends JComponent {

	private static final long serialVersionUID = 1L;
	
	private static Map<PlotType, XYPlot> plotLists = new EnumMap<PlotType, XYPlot>(PlotType.class);
	private ChartPanel chartPanel;
	
	// XY series objects for each set of data
	
	private static	XYSeries uData        = new XYSeries("u");
	private static	XYSeries vData        = new XYSeries("v");
	private	static	XYSeries wData        = new XYSeries("w");
			
	private	static	XYSeries posData      = new XYSeries("Position");
	private	static	XYSeries altData      = new XYSeries("Altitude");
			
	private	static	XYSeries altDotData   = new XYSeries("Alt Dot");
			
	private	static	XYSeries phiData      = new XYSeries("Phi");
	private	static	XYSeries thetaData    = new XYSeries("Theta");
	private	static	XYSeries psiData      = new XYSeries("Psi");
			
	private	static	XYSeries pData        = new XYSeries("p");
	private	static	XYSeries qData        = new XYSeries("q");
	private	static	XYSeries rData        = new XYSeries("r");
					
	private	static	XYSeries axData       = new XYSeries("a_x");
	private	static	XYSeries ayData       = new XYSeries("a_y");
	private	static	XYSeries azData       = new XYSeries("a_z");		
			
	private	static	XYSeries lData        = new XYSeries("L");
	private	static	XYSeries mData        = new XYSeries("M");
	private	static	XYSeries nData        = new XYSeries("N");
			
	private	static	XYSeries tasData      = new XYSeries("TAS");
			
	private	static	XYSeries betaData     = new XYSeries("Beta");
	private	static	XYSeries alphaData    = new XYSeries("Alpha");
			
	private	static	XYSeries elevData     = new XYSeries("Elevator");
	private	static	XYSeries ailData      = new XYSeries("Aileron");
	private	static	XYSeries rudData      = new XYSeries("Rudder");
	private	static	XYSeries throtData    = new XYSeries("Throttle");
	private	static	XYSeries flapData     = new XYSeries("Flaps");
			
	private	static	XYSeries alphaDotData = new XYSeries("Alpha Dot");
	private static	XYSeries machData     = new XYSeries("Mach");
	
	// Domain Axes
	
	private static	NumberAxis timeAxis   = new NumberAxis("Time [sec]");
	private static	NumberAxis eastAxis   = new NumberAxis("East [ft]");
	
	// Range Axes
	
	private static NumberAxis linVelAxis 		= new NumberAxis("Body Velocity [ft/sec]");
	private static NumberAxis northAxis  		= new NumberAxis("North [ft]");
	private static NumberAxis altAxis 			= new NumberAxis("Altitude [ft]");
	private static NumberAxis altDotAxis 		= new NumberAxis("Vertical Speed [ft/min]"); 
	private static NumberAxis psiAxis 			= new NumberAxis("Heading [rad]");
	private static NumberAxis eulerAnglesAxis 	= new NumberAxis("Angle [rad]");
	private static NumberAxis angularRatesAxis  = new NumberAxis("Angular Rate [rad/sec]");
	private static NumberAxis linearAccelAxis 	= new NumberAxis("Acceleration [g]");
	private static NumberAxis totalMomentAxis 	= new NumberAxis("Moment [ft*lb]");
	private static NumberAxis tasAxis 			= new NumberAxis("True Airspeed [ft/sec]");
	private static NumberAxis windParamAxis 	= new NumberAxis("Angle [rad]");
	private static NumberAxis elevAxis 			= new NumberAxis("Deflection [rad]");
	private static NumberAxis ailAxis 			= new NumberAxis("Deflection [rad]");
	private static NumberAxis rudAxis 			= new NumberAxis("Deflection [rad]");
	private static NumberAxis throtAxis 		= new NumberAxis("Position [norm]");
	private static NumberAxis flapAxis 			= new NumberAxis("Deflection [rad]");
	private static NumberAxis alphDotAxis 		= new NumberAxis("Rate [rad/sec]");
	private static NumberAxis machAxis 			= new NumberAxis("Mach Number");
	
	// Combined Domain Plots
	
	private static CombinedDomainXYPlot ratesPlot      = new CombinedDomainXYPlot(timeAxis);
	private static CombinedDomainXYPlot instrumentPlot = new CombinedDomainXYPlot(timeAxis);
	private static CombinedDomainXYPlot miscPlot       = new CombinedDomainXYPlot(timeAxis);
	private static CombinedDomainXYPlot controlsPlot   = new CombinedDomainXYPlot(timeAxis);

	/**
	 * Creates plots for variables monitored in the logsOut ArrayList
	 * 
	 * @param logsOut
	 * @param windowTitle
	 */
	public SimulationPlot(List<Map<SimOuts, Double>> logsOut, String windowTitle) {
		
		// Only run makePlotLists() once
		if(plotLists.isEmpty())
			makePlotLists(logsOut);
		
		// Use BorderLayout to place all graphs
		setLayout(new BorderLayout());
		
		// Select from methods below to create a chart panels to populate AWT window 
		switch (windowTitle) {
			case "Rates":
				chartPanel = new ChartPanel(makeRatesPlots());
				setPreferredSize(new Dimension(1000, 950));
				add(chartPanel, BorderLayout.CENTER);
				break;
			case "Position":
				chartPanel = new ChartPanel(makePositionPlot());
				setPreferredSize(new Dimension(750, 750));
				add(chartPanel, BorderLayout.CENTER);
				break;
			case "Instruments":
				chartPanel = new ChartPanel(makeInstrumentPlots());
				setPreferredSize(new Dimension(1000, 950));
				add(chartPanel, BorderLayout.CENTER);
				break;
			case "Miscellaneous":
				chartPanel = new ChartPanel(makeMiscPlots());
				setPreferredSize(new Dimension(1000, 950));
				add(chartPanel, BorderLayout.CENTER);
				break;
			case "Controls":
				chartPanel = new ChartPanel(makeControlsPlots());
				setPreferredSize(new Dimension(1000, 950));
				add(chartPanel, BorderLayout.CENTER);
				break;	
			default:
				System.err.println("Invalid plot type selected!");
				break;
		}
	}
	
	/**
	 *  Generates a {@link JFreeChart} object associated with rates and accelerations (Angular Rates, Linear Velocities and Linear Accelerations) on a {@link CombinedDomainXYPlot}.
	 */
	private JFreeChart makeRatesPlots() {
		ratesPlot.add(plotLists.get(PlotType.ANGULAR_RATE), 1);
		ratesPlot.add(plotLists.get(PlotType.VELOCITY),     1);
		ratesPlot.add(plotLists.get(PlotType.ACCELERATION), 1);
		ratesPlot.setOrientation(PlotOrientation.VERTICAL);
		ratesPlot.setGap(20);
		
		return new JFreeChart("Rates", 
					 	      JFreeChart.DEFAULT_TITLE_FONT, 
					 	      ratesPlot, 
					          true);
	}
	
	/**
	 *  Generates a {@link JFreeChart} object associated with aircraft position (North vs East) on an {@link XYPlot}.
	 */
	private JFreeChart makePositionPlot() {
		return new JFreeChart("Position", 
					 	      JFreeChart.DEFAULT_TITLE_FONT, 
					 	      plotLists.get(PlotType.POSITION), // Get the XYplot directly from the Map of XYPlots
					          true);
	}
	
	/**
	 *  Generates a {@link JFreeChart} object associated with instrumentation data (Pitch, Roll, Airspeed, Heading, Altitude and Vertical Speed) on a {@link CombinedDomainXYPlot}.
	 */
	private JFreeChart makeInstrumentPlots() {
		instrumentPlot.add(plotLists.get(PlotType.EULER_ANGLES), 1);
		instrumentPlot.add(plotLists.get(PlotType.TAS), 		 1);
		instrumentPlot.add(plotLists.get(PlotType.HEADING),      1);
		instrumentPlot.add(plotLists.get(PlotType.ALTITUDE),     1);
		instrumentPlot.add(plotLists.get(PlotType.VERT_SPEED),   1);
		
		instrumentPlot.setOrientation(PlotOrientation.VERTICAL);
		instrumentPlot.setGap(20);
		
		return new JFreeChart("Instruments", 
					 	      JFreeChart.DEFAULT_TITLE_FONT, 
					 	      instrumentPlot, 
					          true);
	}
	
	/**
	 *  Generates a {@link JFreeChart} object associated with miscellaneous air data (Alpha, Beta, Alphadot and Mach) on a {@link CombinedDomainXYPlot}.
	 */
	private JFreeChart makeMiscPlots() {
		miscPlot.add(plotLists.get(PlotType.WIND_PARAM), 1);
		miscPlot.add(plotLists.get(PlotType.ALPHA_DOT),  1);
		miscPlot.add(plotLists.get(PlotType.MACH),  1);
		
		miscPlot.setOrientation(PlotOrientation.VERTICAL);
		miscPlot.setGap(20);
		
		return new JFreeChart("Miscellaneous", 
					 	      JFreeChart.DEFAULT_TITLE_FONT, 
					 	      miscPlot, 
					          true);
	}
	
	/**
	 *  Generates a {@link JFreeChart} object associated with aircraft controls (Elevator, Aileron, Rudder, Throttle, Flaps) on a {@link CombinedDomainXYPlot}.
	 */
	private JFreeChart makeControlsPlots() {
		controlsPlot.add(plotLists.get(PlotType.ELEVATOR), 1);
		controlsPlot.add(plotLists.get(PlotType.AILERON),  1);
		controlsPlot.add(plotLists.get(PlotType.RUDDER),   1);
		controlsPlot.add(plotLists.get(PlotType.THROTTLE), 1);
		controlsPlot.add(plotLists.get(PlotType.FLAPS),    1);
		
		controlsPlot.setOrientation(PlotOrientation.VERTICAL);
		controlsPlot.setGap(20);
		
		return new JFreeChart("Controls", 
					 	      JFreeChart.DEFAULT_TITLE_FONT, 
					 	      controlsPlot, 
					          true);
	}
	
	/**
	 * Populates the {@link plotLists} EnumMap with XYPlot objects created from the logsOut ArrayList that is passed in. 
	 * It first creates {@link XYSeries} objects with data from logsOut, adds those series to {@link XYSeriesCollection}, adds those 
	 * series collections to {@link XYPlot} objects, and finally puts the XYPlot objects into {@link plotLists}
	 */
	private static void makePlotLists(List<Map<SimOuts, Double>> logsOut) {
		
		updateXYSeriesData(logsOut);
		
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
											 timeAxis,
											 linVelAxis, 
									 		 new StandardXYItemRenderer()); 
		
		plotLists.put(PlotType.VELOCITY, linearVelPlot);
		
		XYPlot positionPlot     = new XYPlot(positionSeries, 
											 eastAxis, 
											 northAxis, 
											 new StandardXYItemRenderer());
		
		plotLists.put(PlotType.POSITION, positionPlot);
		
		XYPlot altitudePlot     = new XYPlot(altitudeSeries, 
											 timeAxis, 
											 altAxis, 
											 new StandardXYItemRenderer());

		plotLists.put(PlotType.ALTITUDE, altitudePlot);
		
		XYPlot altDotPlot       = new XYPlot(altDotSeries, 
											 timeAxis, 
										     altDotAxis, 
										     new StandardXYItemRenderer());

		plotLists.put(PlotType.VERT_SPEED, altDotPlot);
		
		XYPlot headingPlot      = new XYPlot(headingSeries, 
											 timeAxis, 
											 psiAxis, 
											 new StandardXYItemRenderer());
							
		plotLists.put(PlotType.HEADING, headingPlot);
		
		XYPlot eulerAnglesPlot  = new XYPlot(eulerAnglesSeries, 
											 timeAxis, 
											 eulerAnglesAxis, 
											 new StandardXYItemRenderer());
		
		plotLists.put(PlotType.EULER_ANGLES, eulerAnglesPlot);
		
		XYPlot angularRatesPlot = new XYPlot(angularRatesSeries, 
											 timeAxis, 
									     	 angularRatesAxis, 
									     	 new StandardXYItemRenderer());
		
		plotLists.put(PlotType.ANGULAR_RATE, angularRatesPlot);
		
		XYPlot linearAccelPlot  = new XYPlot(linearAccelSeries, 
										   	 timeAxis, 
											 linearAccelAxis, 
											 new StandardXYItemRenderer());
		
		plotLists.put(PlotType.ACCELERATION, linearAccelPlot);
		
		XYPlot totalMomentPlot  = new XYPlot(totalMomentSeries, 
											 timeAxis, 
											 totalMomentAxis, 
											 new StandardXYItemRenderer()); 
		
		plotLists.put(PlotType.MOMENT, totalMomentPlot);
		
		XYPlot tasPlot          = new XYPlot(tasSeries, 
											 timeAxis, 
										     tasAxis, 
										     new StandardXYItemRenderer());

		plotLists.put(PlotType.TAS, tasPlot);
		
		XYPlot windParamPlot    = new XYPlot(windParamSeries, 
											 timeAxis, 
											 windParamAxis, 
											 new StandardXYItemRenderer());
		
		plotLists.put(PlotType.WIND_PARAM, windParamPlot);
		
		XYPlot elevPlot    		= new XYPlot(elevSeries, 
											 timeAxis, 
											 elevAxis, 
											 new StandardXYItemRenderer());

		plotLists.put(PlotType.ELEVATOR, elevPlot);
		
		XYPlot ailPlot		    = new XYPlot(ailSeries, 
											 timeAxis, 
											 ailAxis, 
											 new StandardXYItemRenderer());
		
		plotLists.put(PlotType.AILERON, ailPlot);
		
		XYPlot rudPlot    		= new XYPlot(rudSeries, 
											 timeAxis, 
											 rudAxis, 
											 new StandardXYItemRenderer());
		
		plotLists.put(PlotType.RUDDER, rudPlot);
		
		XYPlot throtPlot        = new XYPlot(throtSeries, 
											 timeAxis, 
											 throtAxis, 
											 new StandardXYItemRenderer());
		
		plotLists.put(PlotType.THROTTLE, throtPlot);
		
		XYPlot flapPlot         = new XYPlot(flapSeries, 
											 timeAxis, 
											 flapAxis, 
											 new StandardXYItemRenderer());
		
		plotLists.put(PlotType.FLAPS, flapPlot);
		
		XYPlot alphaDotPlot     = new XYPlot(alphaDotSeries, 
											 timeAxis, 
											 alphDotAxis, 
											 new StandardXYItemRenderer());
							
		plotLists.put(PlotType.ALPHA_DOT, alphaDotPlot);

		XYPlot machPlot         = new XYPlot(machSeries, 
											 timeAxis, 
											 machAxis, 
											 new StandardXYItemRenderer());

		plotLists.put(PlotType.MACH, machPlot);
	}
	
	/**
	 * Updates all XYSeries objects with new data from logsOut list
	 * 
	 * @param oldlogsOut
	 */
	protected static void updateXYSeriesData(List<Map<SimOuts, Double>> oldLogsOut) {
		
		// Clear all data from series
		
		uData.clear();
		vData.clear();
		wData.clear();
		
		posData.clear();
		altData.clear();
		
		altDotData.clear();
		
		phiData.clear();
		thetaData.clear();
		psiData.clear();
		
		pData.clear();
		qData.clear();
		rData.clear();
		
		axData.clear();
		ayData.clear();
		azData.clear();
		
		lData.clear();
		mData.clear();
		nData.clear();
		
		tasData.clear();
		
		betaData.clear();
		alphaData.clear();
		
		elevData.clear();
		ailData.clear();
		rudData.clear();
		throtData.clear();
		flapData.clear();
		
		alphaDotData.clear();
		machData.clear();
		
		// Copy to thread-safe ArrayList for iteration
		
		CopyOnWriteArrayList<Map<SimOuts, Double>> logsOut = new CopyOnWriteArrayList<>(oldLogsOut);
		
		// Add data from logsOut to each XYSeries; only notify of a SeriesChangeEvent at the end of the loop
		
		for (Iterator<Map<SimOuts, Double>> logsOutItr = logsOut.iterator(); logsOutItr.hasNext();) {
			Map<SimOuts, Double> simOut = logsOutItr.next();
			
			uData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.U), !logsOutItr.hasNext());
			vData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.V), !logsOutItr.hasNext());
			wData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.W), !logsOutItr.hasNext());
			
			posData.add(simOut.get(SimOuts.EAST),simOut.get(SimOuts.NORTH), !logsOutItr.hasNext());
			altData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.ALT), !logsOutItr.hasNext());
			
			altDotData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.ALT_DOT), !logsOutItr.hasNext());
			
			phiData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.PHI), !logsOutItr.hasNext());
			thetaData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.THETA), !logsOutItr.hasNext());
			psiData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.PSI), !logsOutItr.hasNext());
			
			pData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.P), !logsOutItr.hasNext());
			qData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.Q), !logsOutItr.hasNext());
			rData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.R), !logsOutItr.hasNext());
			
			axData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.AN_X), !logsOutItr.hasNext());
			ayData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.AN_Y), !logsOutItr.hasNext());
			azData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.AN_Z), !logsOutItr.hasNext());
			
			lData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.L), !logsOutItr.hasNext());
			mData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.M), !logsOutItr.hasNext());
			nData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.N), !logsOutItr.hasNext());
			
			tasData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.TAS), !logsOutItr.hasNext());
			
			betaData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.BETA), !logsOutItr.hasNext());
			alphaData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.ALPHA), !logsOutItr.hasNext());
			
			elevData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.ELEVATOR), !logsOutItr.hasNext());
			ailData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.AILERON), !logsOutItr.hasNext());
			rudData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.RUDDER), !logsOutItr.hasNext());
			throtData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.THROTTLE_1), !logsOutItr.hasNext());
			flapData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.FLAPS), !logsOutItr.hasNext());
			
			alphaDotData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.ALPHA_DOT), !logsOutItr.hasNext());
			machData.add(simOut.get(SimOuts.TIME),simOut.get(SimOuts.MACH), !logsOutItr.hasNext());
		}
		
		// Bounds the minimum X Axis value to the first time value in the data series 
		
		timeAxis.setRange(uData.getMinX(), uData.getMaxX());
		
		// Updates each CombinedDomainXYPlot with the new time axis
		
		ratesPlot.setDomainAxis(timeAxis);
		for (int i = 0; i < ratesPlot.getRangeAxisCount(); i++) {
			if (ratesPlot.getRangeAxis(i) != null) {
				ratesPlot.getRangeAxis(i).setAutoRange(false);
				ratesPlot.getRangeAxis(i).resizeRange(3);
			}
		}
		
		instrumentPlot.setDomainAxis(timeAxis);
		for (int i = 0; i < instrumentPlot.getRangeAxisCount(); i++) {
			if (instrumentPlot.getRangeAxis(i) != null)	{
				instrumentPlot.getRangeAxis(i).setAutoRange(false);
				instrumentPlot.getRangeAxis(i).resizeRange(3);
			}
		}
		
		miscPlot.setDomainAxis(timeAxis);
		for (int i = 0; i < miscPlot.getRangeAxisCount(); i++) {
			if (miscPlot.getRangeAxis(i) != null) {
				miscPlot.getRangeAxis(i).setAutoRange(false);
				miscPlot.getRangeAxis(i).resizeRange(3);
			}
		}
		
		controlsPlot.setDomainAxis(timeAxis);
		for (int i = 0; i < controlsPlot.getRangeAxisCount(); i++) {
			if (controlsPlot.getRangeAxis(i) != null) {
				controlsPlot.getRangeAxis(i).setAutoRange(false);
				controlsPlot.getRangeAxis(i).resizeRange(3);
			}
		}
	}
	
	/**
	 * @return ChartPanel object so that it can be updated with new data
	 */
	protected ChartPanel getChartPanel() {return chartPanel;}
}
