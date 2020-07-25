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
package com.chrisali.javaflightsim.simulation.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chrisali.javaflightsim.interfaces.OTWWorld;
import com.chrisali.javaflightsim.interfaces.Steppable;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;

/**
 *	Interacts with {@link OTWWorld} and any registered listeners to pass data from the out the window display back to
 *	the simulation {@link Integrate6DOFEquations}. Relatively thread safe.
 */
public class EnvironmentData implements Steppable {
	
	private static final Logger logger = LogManager.getLogger(EnvironmentData.class);
	
	private Map<EnvironmentDataType, Double> environmentData = Collections.synchronizedMap(new EnumMap<EnvironmentDataType, Double>(EnvironmentDataType.class));
	
	private OTWWorld outTheWindow;
	private List<EnvironmentDataListener> dataListenerList;
	
	public EnvironmentData(OTWWorld outTheWindow) {
		this.outTheWindow = outTheWindow;
		this.dataListenerList = new ArrayList<>();
	}
	
	public Map<EnvironmentDataType, Double> getEnvironmentData() { return environmentData; }
	
	/**
	 * Polls OTW for environment data, then assigns and converts the values needed for the environmentData EnumMap  
	 * 
	 * @param simOut
	 */
	public void updateData(float terrainHeight) {
		synchronized (environmentData) {
			environmentData.put(EnvironmentDataType.TERRAIN_HEIGHT, (double) terrainHeight);
		}
		
		fireDataArrived();
	}
		
	@Override
	public boolean canStepNow(int simTimeMS) {
		return simTimeMS % 1 == 0;
	}

	@Override
	public void step() {
		try {
			if (outTheWindow != null)
				updateData(outTheWindow.getTerrainHeight());
		} catch (Exception e) {
			logger.error("Exception encountered while running environment data listener!", e);
		}
	}
	
	/**
	 * Adds a listener that implements {@link EnvironmentDataListener} to a list of listeners that can listen
	 * to {@link EnvironmentData} 
	 * 
	 * @param dataListener
	 */
	public void addListener(EnvironmentDataListener dataListener) {
		logger.debug("Adding environment data listener: " + dataListener.getClass());
		dataListenerList.add(dataListener);
	}
	
	/**
	 * Lets registered listeners know that data has arrived from {@link OTWWorld}
	 * so that they can use it as needed
	 */
	private void fireDataArrived() {
		for (EnvironmentDataListener listener : dataListenerList) {
			if(listener != null) 
				listener.onEnvironmentDataReceived(this);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		for (Map.Entry<EnvironmentDataType, Double> entry : environmentData.entrySet()) {
			 sb.append(entry.getKey().toString()).append(": ").append(entry.getValue())
			   .append(" ").append(entry.getKey().getUnit()).append("\n");
		}
		sb.append("\n");
		
		return sb.toString();
	}
}
