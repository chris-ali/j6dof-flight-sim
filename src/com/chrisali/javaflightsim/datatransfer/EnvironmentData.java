package com.chrisali.javaflightsim.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.chrisali.javaflightsim.otw.RunWorld;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;

/**
 *	Interacts with {@link RunWorld} and any registered listeners to pass data from the out the window display back to
 *	the simulation {@link Integrate6DOFEquations}. Uses threading to obtain data at a reasonable rate
 */
public class EnvironmentData implements Runnable {
	
	private static boolean running;
	private Map<EnvironmentDataType, Double> environmentData = Collections.synchronizedMap(new EnumMap<EnvironmentDataType, Double>(EnvironmentDataType.class));
	
	private RunWorld outTheWindow;
	private List<EnvironmentDataListener> dataListenerList;
	
	/**
	 * Creates an instance of {@link EnvironmentData} with a reference to {@link RunWorld} so
	 * that the thread in this class knows when the out the window display is running
	 * 
	 * @param outTheWindow
	 */
	public EnvironmentData(RunWorld outTheWindow) {
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
			while (!RunWorld.isRunning()) 
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
	 * Lets registered listeners know that data has arrived from the {@link RunWorld} thread
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
