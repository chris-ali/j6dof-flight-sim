package com.chrisali.javaflightsim.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.integration.SimOuts;
import com.chrisali.javaflightsim.utilities.FileUtilities;

/**
 *	Interacts with {@link Integrate6DOFEquations} and any registered listeners to pass flight data from the simulation
 *	listeners. Uses threading to obtain data from the simulation at a reasonable rate
 */
public class FlightData implements Runnable {
	
	private static boolean running;
	private Map<FlightDataType, Double> flightData = Collections.synchronizedMap(new EnumMap<FlightDataType, Double>(FlightDataType.class));
	
	private Integrate6DOFEquations runSim;
	private List<FlightDataListener> dataListenerList;
	
	/**
	 * Creates an instance of {@link FlightData} with a reference to {@link Integrate6DOFEquations} so
	 * that the thread in this class knows when the simulation is running
	 * 
	 * @param runSim
	 */
	public FlightData(Integrate6DOFEquations runSim) {
		this.runSim = runSim;
		this.dataListenerList = new ArrayList<>();
	}
	
	public Map<FlightDataType, Double> getFlightData() {return flightData;}
	
	/**
	 * Polls simOut for data, and assigns and converts the values needed to the flightData EnumMap  
	 * 
	 * @param simOut
	 */
	public void updateData(Map<SimOuts, Double> simOut) {
		final Double TAS_TO_IAS = 1/(1+((simOut.get(SimOuts.ALT)/1000)*0.02));
		
		synchronized (flightData) {
			flightData.put(FlightDataType.IAS, FileUtilities.toKnots(simOut.get(SimOuts.TAS)*TAS_TO_IAS));
			flightData.put(FlightDataType.TAS, FileUtilities.toKnots(simOut.get(SimOuts.TAS)));
			
			flightData.put(FlightDataType.VERT_SPEED, simOut.get(SimOuts.ALT_DOT));
			
			flightData.put(FlightDataType.ALTITUDE, simOut.get(SimOuts.ALT));
			
			flightData.put(FlightDataType.ROLL, Math.toDegrees(simOut.get(SimOuts.PHI)));
			flightData.put(FlightDataType.PITCH, Math.toDegrees(simOut.get(SimOuts.THETA)));
			
			flightData.put(FlightDataType.HEADING, Math.toDegrees(simOut.get(SimOuts.PSI)));
			
			flightData.put(FlightDataType.TURN_RATE, Math.toDegrees(simOut.get(SimOuts.PSI_DOT)));
			flightData.put(FlightDataType.TURN_COORD, simOut.get(SimOuts.AN_Y));
			
			flightData.put(FlightDataType.GFORCE, simOut.get(SimOuts.AN_Z));
			
			flightData.put(FlightDataType.LATITUDE, Math.toDegrees(simOut.get(SimOuts.LAT)));
			flightData.put(FlightDataType.LONGITUDE, Math.toDegrees(simOut.get(SimOuts.LON)));
			
			flightData.put(FlightDataType.NORTH, simOut.get(SimOuts.NORTH));
			flightData.put(FlightDataType.EAST, simOut.get(SimOuts.EAST));
			
			flightData.put(FlightDataType.RPM_1, simOut.get(SimOuts.RPM_1));
			flightData.put(FlightDataType.RPM_2, simOut.get(SimOuts.RPM_2));
			
			flightData.put(FlightDataType.GEAR, simOut.get(SimOuts.GEAR));
			flightData.put(FlightDataType.FLAPS, Math.toDegrees(simOut.get(SimOuts.FLAPS)));
			
			flightData.put(FlightDataType.AOA, Math.abs(simOut.get(SimOuts.ALPHA)));
			
			flightData.put(FlightDataType.PITCH_RATE, Math.toDegrees(simOut.get(SimOuts.Q)));
		}
		fireDataArrived();
	}
	
	@Override
	public void run() {
		running = true;
		
		try {
			Thread.sleep(5000);
			
			while (Integrate6DOFEquations.isRunning() && running) {
				Thread.sleep(12);
				
				if(runSim.getSimOut() != null)
					updateData(runSim.getSimOut());
			}
		} catch (InterruptedException e) {
		} finally {running = false;} 
	}
	
	/**
	 * Adds a listener that implements {@link FlightDataListener} to a list of listeners that can listen
	 * to {@link FlightData} 
	 * 
	 * @param dataListener
	 */
	public void addFlightDataListener(FlightDataListener dataListener) {
		dataListenerList.add(dataListener);
	}
	
	/**
	 * Lets registered listeners know that data has arrived from the {@link Integrate6DOFEquations} thread
	 * so that they can use it as needed
	 */
	private void fireDataArrived() {
		for (FlightDataListener listener : dataListenerList) {
			if(listener != null) 
				listener.onFlightDataReceived(this);
		}
	}
	
	/**
	 * Lets other objects know if the {@link FlightData} thread is running
	 * 
	 * @return Running status of flight data
	 */
	public static synchronized boolean isRunning() {return running;}
	
	
	/**
	 * Lets other objects request to stop the flow of flight data by setting running to false
	 * 
	 * @param running
	 */
	public static synchronized void setRunning(boolean running) {FlightData.running = running;}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		for (Map.Entry<FlightDataType, Double> entry : flightData.entrySet()) {
			 sb.append(entry.getKey().toString()).append(": ").append(entry.getValue())
			   .append(" ").append(entry.getKey().getUnit()).append("\n");
		}
		sb.append("\n");
		
		return sb.toString();
	}
}
