package com.chrisali.javaflightsim.plotting;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.EnumMap;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;

import com.chrisali.javaflightsim.simulation.integration.SimOuts;

/**
 * This object contains a {@link CombinedDomainXYPlot} object, consisting of group of {@link XYPlot} objects.   
 * It generates a plot in Swing as a JFrame window. The plot displayed depends on the windowTitle String argument passed in. 
 * @see MakePlots
 */
public class SimulationPlots extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private PlotCloseListener plotCloseListener;
	
	private  EnumMap<PlotType, XYPlot> plotLists = new EnumMap<PlotType, XYPlot>(PlotType.class);
	
	// Creates plots for variables monitored in the logsOut ArrayList
	public SimulationPlots(List<EnumMap<SimOuts, Double>> logsOut, String windowTitle, String aircraftName) {
		super(aircraftName + " " + windowTitle);
		
		makePlotLists(logsOut);
		
		// Select from methods below to create a chart panels to populate AWT window 
		switch (windowTitle) {
			case "Rates":
				ChartPanel ratePlotPanel = new ChartPanel(makeRatesPlots(plotLists));
				ratePlotPanel.setPreferredSize(new Dimension(1000, 950));
				setContentPane(ratePlotPanel);
				break;
			case "Position":
				ChartPanel posPlotPanel = new ChartPanel(makePositionPlot(plotLists));
				posPlotPanel.setPreferredSize(new Dimension(750, 750));
				setContentPane(posPlotPanel);
				break;
			case "Instruments":
				ChartPanel instPlotPanel = new ChartPanel(makeInstrumentPlots(plotLists));
				instPlotPanel.setPreferredSize(new Dimension(1000, 950));
				setContentPane(instPlotPanel);
				break;
			case "Miscellaneous":
				ChartPanel miscPlotPanel = new ChartPanel(makeMiscPlots(plotLists));
				miscPlotPanel.setPreferredSize(new Dimension(1000, 400));
				setContentPane(miscPlotPanel);
				break;
			case "Controls":
				ChartPanel controlPlotPanel = new ChartPanel(makeControlsPlots(plotLists));
				controlPlotPanel.setPreferredSize(new Dimension(1000, 950));
				setContentPane(controlPlotPanel);
				break;	
			default:
				System.err.println("Invalid plot type selected!");
				break;
		}
		
		//================== Window Settings ====================================
		
		pack();
		RefineryUtilities.centerFrameOnScreen(this);
		setVisible(true);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				setVisible(false);
				
				if (plotCloseListener != null)
					plotCloseListener.plotWindowClosed();
			}
		});
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
	
	/**
	 * Populates the {@link plotLists} EnumMap with XYPlot objects created from the logsOut ArrayList that is passed in. 
	 * It first creates {@link XYSeries} objects with data from logsOut, adds those series to {@link XYSeriesCollection}, adds those 
	 * series collections to {@link XYPlot} objects, and finally puts the XYPlot objects into {@link plotLists}
	 */
	protected void makePlotLists(List<EnumMap<SimOuts, Double>> logsOut) {
		
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
	
	public void setPlotCloseListner(PlotCloseListener plotCloseListener) {
		this.plotCloseListener = plotCloseListener;		
	}
}
