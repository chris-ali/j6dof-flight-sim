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
package com.chrisali.javaflightsim.simulation.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.chrisali.javaflightsim.lwjgl.LWJGLWorld;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.interfaces.OTWWorld;

/**
 *	Interacts with {@link LWJGLWorld} and any registered listeners to pass data from the out the window display back to
 *	the simulation {@link Integrate6DOFEquations}. Uses threading to obtain data at a reasonable rate
 */
public class EnvironmentData implements Runnable {
	
	private static boolean running;
	private Map<EnvironmentDataType, Double> environmentData = Collections.synchronizedMap(new EnumMap<EnvironmentDataType, Double>(EnvironmentDataType.class));
	
	private OTWWorld outTheWindow;
	private List<EnvironmentDataListener> dataListenerList;
	
	/**
	 * Creates an instance of {@link EnvironmentData} with a reference to {@link LWJGLWorld} so
	 * that the thread in this class knows when the out the window display is running
	 * 
	 * @param outTheWindow
	 */
	public EnvironmentData(OTWWorld outTheWindow) {
		this.outTheWindow = outTheWindow;
		this.dataListenerList = new ArrayList<>();
	}
	
	public Map<EnvironmentDataType, Double> getEnvironmentData() {return environmentData;}
	
	/**
	 * Polls simOut for data, and assigns and converts the values needed to the flightData EnumMap  
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
	public void run() {
		running = true;
		
		try {
			while (!LWJGLWorld.isRunning()) 
				Thread.sleep(25);
			
			while (running) {
				Thread.sleep(10);
				
				if(outTheWindow != null)
					updateData(outTheWindow.getTerrainHeight());
			}
		} catch (InterruptedException e) {
		} finally {running = false;} 
	}
	
	/**
	 * Adds a listener that implements {@link EnvironmentDataListener} to a list of listeners that can listen
	 * to {@link EnvironmentData} 
	 * 
	 * @param dataListener
	 */
	public void addEnvironmentDataListener(EnvironmentDataListener dataListener) {
		dataListenerList.add(dataListener);
	}
	
	/**
	 * Lets registered listeners know that data has arrived from the {@link LWJGLWorld} thread
	 * so that they can use it as needed
	 */
	private void fireDataArrived() {
		for (EnvironmentDataListener listener : dataListenerList) {
			if(listener != null) 
				listener.onEnvironmentDataReceived(this);
		}
	}
	
	/**
	 * Lets other objects know if the {@link EnvironmentData} thread is running
	 * 
	 * @return Running status of flight data
	 */
	public static synchronized boolean isRunning() {return running;}
	
	
	/**
	 * Lets other objects request to stop the the flow of environment data by setting running to false
	 * 
	 * @param running
	 */
	public static synchronized void setRunning(boolean running) {EnvironmentData.running = running;}

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
