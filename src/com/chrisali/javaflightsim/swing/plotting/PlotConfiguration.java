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
	
	public PlotConfiguration() { }
	
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
