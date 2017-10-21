/*******************************************************************************
 * Copyright (C) 2016-2017 Christopher Ali
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

import java.util.List;
import java.util.Map;

import com.chrisali.javaflightsim.simulation.integration.SimOuts;
import com.chrisali.javaflightsim.simulation.interfaces.Saveable;
import com.chrisali.javaflightsim.simulation.utilities.FileUtilities;
import com.chrisali.javaflightsim.simulation.utilities.SimDirectories;

public class PlotConfiguration implements Saveable {

	Map<String, SubPlotBundle> subPlotBundles;
	
	public PlotConfiguration() { 
		/*
		SubPlotOptions linVelPlot = new SubPlotOptions();
		linVelPlot.setTitle("Velocity");
		linVelPlot.setxAxisName("Time [sec]");
		linVelPlot.setyAxisName("Body Velocity [ft/sec]");
		linVelPlot.setxData(SimOuts.TIME);
		List<SimOuts> velYData = new ArrayList<>();
		velYData.add(SimOuts.U);
		velYData.add(SimOuts.V);
		velYData.add(SimOuts.W);
		linVelPlot.setyData(velYData);
		
		SubPlotOptions posPlot = new SubPlotOptions();
		posPlot.setTitle("Position");
		posPlot.setxAxisName("East [ft]");
		posPlot.setyAxisName("North [ft]");
		posPlot.setxData(SimOuts.EAST);
		List<SimOuts> posYData = new ArrayList<>();
		posYData.add(SimOuts.NORTH);
		posPlot.setyData(posYData);
		
		SubPlotOptions altPlot = new SubPlotOptions();
		altPlot.setTitle("Altitude");
		altPlot.setxAxisName("Time [sec]");
		altPlot.setyAxisName("Altitude [ft]");
		altPlot.setxData(SimOuts.TIME);
		List<SimOuts> altYData = new ArrayList<>();
		altYData.add(SimOuts.ALT);
		altPlot.setyData(altYData);

		SubPlotOptions altDotPlot = new SubPlotOptions();
		altDotPlot.setTitle("VerticalSpeed");
		altDotPlot.setxAxisName("Time [sec]");
		altDotPlot.setyAxisName("Vertical Speed [ft/min]");
		altDotPlot.setxData(SimOuts.TIME);
		List<SimOuts> altDotYData = new ArrayList<>();
		altDotYData.add(SimOuts.ALT_DOT);
		altDotPlot.setyData(altDotYData);
		
		SubPlotOptions psiPlot = new SubPlotOptions();
		psiPlot.setTitle("Heading");
		psiPlot.setxAxisName("Time [sec]");
		psiPlot.setyAxisName("Heading [rad]");
		psiPlot.setxData(SimOuts.TIME);
		List<SimOuts> psiYData = new ArrayList<>();
		psiYData.add(SimOuts.PSI);
		psiPlot.setyData(psiYData);

		SubPlotOptions eulerPlot = new SubPlotOptions();
		eulerPlot.setTitle("Euler Angles");
		eulerPlot.setxAxisName("Time [sec]");
		eulerPlot.setyAxisName("Angle [rad]");
		eulerPlot.setxData(SimOuts.TIME);
		List<SimOuts> eulerYData = new ArrayList<>();
		eulerYData.add(SimOuts.PHI);
		eulerYData.add(SimOuts.THETA);
		eulerYData.add(SimOuts.PSI);
		eulerPlot.setyData(eulerYData);

		SubPlotOptions angularRatesPlot = new SubPlotOptions();
		angularRatesPlot.setTitle("Angular Rate");
		angularRatesPlot.setxAxisName("Time [sec]");
		angularRatesPlot.setyAxisName("Angular Rate [rad/sec]");
		angularRatesPlot.setxData(SimOuts.TIME);
		List<SimOuts> angularRateYData = new ArrayList<>();
		angularRateYData.add(SimOuts.P);
		angularRateYData.add(SimOuts.Q);
		angularRateYData.add(SimOuts.R);
		angularRatesPlot.setyData(angularRateYData);

		SubPlotOptions linAccelPlot = new SubPlotOptions();
		linAccelPlot.setTitle("Acceleration");
		linAccelPlot.setxAxisName("Time [sec]");
		linAccelPlot.setyAxisName("Acceleration [g]");
		linAccelPlot.setxData(SimOuts.TIME);
		List<SimOuts> linAccelYData = new ArrayList<>();
		linAccelYData.add(SimOuts.AN_X);
		linAccelYData.add(SimOuts.AN_Y);
		linAccelYData.add(SimOuts.AN_Z);
		linAccelPlot.setyData(linAccelYData);

		SubPlotOptions momentPlot = new SubPlotOptions();
		momentPlot.setTitle("Moment");
		momentPlot.setxAxisName("Time [sec]");
		momentPlot.setyAxisName("Moment [ft*lbf]");
		momentPlot.setxData(SimOuts.TIME);
		List<SimOuts> momentYData = new ArrayList<>();
		momentYData.add(SimOuts.L);
		momentYData.add(SimOuts.M);
		momentYData.add(SimOuts.N);
		momentPlot.setyData(momentYData);
		
		SubPlotOptions tasPlot = new SubPlotOptions();
		tasPlot.setTitle("True Airspeed");
		tasPlot.setxAxisName("Time [sec]");
		tasPlot.setyAxisName("True Airspeed [ft/sec]");
		tasPlot.setxData(SimOuts.TIME);
		List<SimOuts> tasYData = new ArrayList<>();
		tasYData.add(SimOuts.TAS);
		tasPlot.setyData(tasYData);
		
		SubPlotOptions windParamPlot = new SubPlotOptions();
		windParamPlot.setTitle("Wind Parameters");
		windParamPlot.setxAxisName("Time [sec]");
		windParamPlot.setyAxisName("Angle [rad]");
		windParamPlot.setxData(SimOuts.TIME);
		List<SimOuts> windParamYData = new ArrayList<>();
		windParamYData.add(SimOuts.ALPHA);
		windParamYData.add(SimOuts.BETA);
		windParamPlot.setyData(windParamYData);

		SubPlotOptions elevPlot = new SubPlotOptions();
		elevPlot.setTitle("Elevator");
		elevPlot.setxAxisName("Time [sec]");
		elevPlot.setyAxisName("Deflection [rad]");
		elevPlot.setxData(SimOuts.TIME);
		List<SimOuts> elevYData = new ArrayList<>();
		elevYData.add(SimOuts.ELEVATOR);
		elevPlot.setyData(elevYData);

		SubPlotOptions ailPlot = new SubPlotOptions();
		ailPlot.setTitle("Aileron");
		ailPlot.setxAxisName("Time [sec]");
		ailPlot.setyAxisName("Deflection [rad]");
		ailPlot.setxData(SimOuts.TIME);
		List<SimOuts> ailYData = new ArrayList<>();
		ailYData.add(SimOuts.AILERON);
		ailPlot.setyData(ailYData);

		SubPlotOptions rudderPlot = new SubPlotOptions();
		rudderPlot.setTitle("Rudder");
		rudderPlot.setxAxisName("Time [sec]");
		rudderPlot.setyAxisName("Deflection [rad]");
		rudderPlot.setxData(SimOuts.TIME);
		List<SimOuts> rudderYData = new ArrayList<>();
		rudderYData.add(SimOuts.RUDDER);
		rudderPlot.setyData(rudderYData);

		SubPlotOptions throttlePlot = new SubPlotOptions();
		throttlePlot.setTitle("Throttle");
		throttlePlot.setxAxisName("Time [sec]");
		throttlePlot.setyAxisName("Position [norm]");
		throttlePlot.setxData(SimOuts.TIME);
		List<SimOuts> throttleYData = new ArrayList<>();
		throttleYData.add(SimOuts.THROTTLE_1);
		throttleYData.add(SimOuts.THROTTLE_2);
		throttleYData.add(SimOuts.THROTTLE_3);
		throttleYData.add(SimOuts.THROTTLE_4);
		throttlePlot.setyData(throttleYData);

		SubPlotOptions flapPlot = new SubPlotOptions();
		flapPlot.setTitle("Flaps");
		flapPlot.setxAxisName("Time [sec]");
		flapPlot.setyAxisName("Deflection [rad]");
		flapPlot.setxData(SimOuts.TIME);
		List<SimOuts> flapYData = new ArrayList<>();
		flapYData.add(SimOuts.FLAPS);
		flapPlot.setyData(flapYData);

		SubPlotOptions alphaDotPlot = new SubPlotOptions();
		alphaDotPlot.setTitle("Alpha Dot");
		alphaDotPlot.setxAxisName("Time [sec]");
		alphaDotPlot.setyAxisName("Rate [rad/sec]");
		alphaDotPlot.setxData(SimOuts.TIME);
		List<SimOuts> alphaDotYData = new ArrayList<>();
		alphaDotYData.add(SimOuts.ALPHA_DOT);
		alphaDotPlot.setyData(alphaDotYData);

		SubPlotOptions machPlot = new SubPlotOptions();
		machPlot.setTitle("Mach");
		machPlot.setxAxisName("Time [sec]");
		machPlot.setyAxisName("Mach Number");
		machPlot.setxData(SimOuts.TIME);
		List<SimOuts> machYData = new ArrayList<>();
		machYData.add(SimOuts.MACH);
		machPlot.setyData(machYData);		

		SubPlotBundle ratesBundle = new SubPlotBundle();
		List<SubPlotOptions> ratesSubPlots = new ArrayList<>();
		ratesSubPlots.add(angularRatesPlot);
		ratesSubPlots.add(linVelPlot);
		ratesSubPlots.add(linAccelPlot);
		ratesBundle.setTitle("Rates");
		ratesBundle.setSubPlots(ratesSubPlots);
		ratesBundle.setSizeXPixels(1000);
		ratesBundle.setSizeYPixels(950);
		
		SubPlotBundle positionBundle = new SubPlotBundle();
		List<SubPlotOptions> positionSubPlots = new ArrayList<>();
		positionSubPlots.add(posPlot);
		positionBundle.setTitle("Position");
		positionBundle.setSubPlots(positionSubPlots);
		positionBundle.setSizeXPixels(750);
		positionBundle.setSizeYPixels(750);
		
		SubPlotBundle instrumentsBundle = new SubPlotBundle();
		List<SubPlotOptions> instrumentsSubPlots = new ArrayList<>();
		instrumentsSubPlots.add(eulerPlot);
		instrumentsSubPlots.add(tasPlot);
		instrumentsSubPlots.add(psiPlot);
		instrumentsSubPlots.add(altPlot);
		instrumentsSubPlots.add(altDotPlot);
		instrumentsBundle.setTitle("Instruments");
		instrumentsBundle.setSubPlots(instrumentsSubPlots);
		instrumentsBundle.setSizeXPixels(1000);
		instrumentsBundle.setSizeYPixels(950);
		
		SubPlotBundle miscBundle = new SubPlotBundle();
		List<SubPlotOptions> miscSubPlots = new ArrayList<>();
		miscSubPlots.add(windParamPlot);
		miscSubPlots.add(alphaDotPlot);
		miscSubPlots.add(machPlot);
		miscBundle.setTitle("Miscellaneous");
		miscBundle.setSubPlots(miscSubPlots);
		miscBundle.setSizeXPixels(1000);
		miscBundle.setSizeYPixels(950);
		
		SubPlotBundle controlsBundle = new SubPlotBundle();
		List<SubPlotOptions> controlsSubPlots = new ArrayList<>();
		controlsSubPlots.add(elevPlot);
		controlsSubPlots.add(ailPlot);
		controlsSubPlots.add(rudderPlot);
		controlsSubPlots.add(throttlePlot);
		controlsSubPlots.add(flapPlot);
		controlsBundle.setTitle("Controls");
		controlsBundle.setSubPlots(controlsSubPlots);
		controlsBundle.setSizeXPixels(1000);
		controlsBundle.setSizeYPixels(950);
		
		Map<String, SubPlotBundle> subPlotBundles = new HashMap<>();
		subPlotBundles.put(ratesBundle.getTitle(), ratesBundle);
		subPlotBundles.put(positionBundle.getTitle(), positionBundle);
		subPlotBundles.put(instrumentsBundle.getTitle(), instrumentsBundle);
		subPlotBundles.put(miscBundle.getTitle(), miscBundle);
		subPlotBundles.put(controlsBundle.getTitle(), controlsBundle);
		
		this.subPlotBundles = subPlotBundles;
		
		save();
		*/
	}
	
	@Override
	public void save() {
		FileUtilities.serializeJson(SimDirectories.SIM_CONFIG.toString(), this.getClass().getSimpleName(), this);
	}

	public Map<String, SubPlotBundle> getSubPlotBundles() { return subPlotBundles; }

	public void setSubPlotBundles(Map<String, SubPlotBundle> subPlotBundles) { this.subPlotBundles = subPlotBundles; }

	/**
	 * Contains a "bundle" of subplots that populate a single chart that appears om a plot window tab  
	 * 
	 * @author Christopher
	 *
	 */
	public static class SubPlotBundle {
		private String title;
		
		private List<SubPlotOptions> subPlots;
				
		private int sizeXPixels;
		
		private int sizeYPixels;
		
		public SubPlotBundle() { }
		
		public String getTitle() { return title; }

		public void setTitle(String title) { this.title = title; }

		public List<SubPlotOptions> getSubPlots() { return subPlots; }

		public void setSubPlots(List<SubPlotOptions> subPlots) { this.subPlots = subPlots; }

		public int getSizeXPixels() { return sizeXPixels; }

		public void setSizeXPixels(int sizeXPixels) { this.sizeXPixels = sizeXPixels; }

		public int getSizeYPixels() { return sizeYPixels; }

		public void setSizeYPixels(int sizeYPixels) { this.sizeYPixels = sizeYPixels; }
	}
	
	/**
	 * Contains X and Y axis information to construct a sub plot that exists as a "bundle" in
	 *  {@link SubPlotBundle}
	 * 
	 * @author Christopher
	 *
	 */
	public static class SubPlotOptions {
		private String title;
		
		private List<SimOuts> yData;
		
		private SimOuts xData;
		
		private String xAxisName;
		
		private String yAxisName;
		
		public SubPlotOptions() {}
		
		public String getTitle() { return title; }

		public void setTitle(String title) { this.title = title; }

		public List<SimOuts> getyData() { return yData; }

		public void setyData(List<SimOuts> yData) { this.yData = yData; }

		public SimOuts getxData() { return xData; }

		public void setxData(SimOuts xData) { this.xData = xData; }

		public String getxAxisName() { return xAxisName; }

		public void setxAxisName(String xAxisName) { this.xAxisName = xAxisName; }

		public String getyAxisName() { return yAxisName; }

		public void setyAxisName(String yAxisName) { this.yAxisName = yAxisName; }
	}
}
