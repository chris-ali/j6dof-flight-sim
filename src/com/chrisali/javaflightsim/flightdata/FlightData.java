package com.chrisali.javaflightsim.flightdata;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.integration.SimOuts;
import com.chrisali.javaflightsim.utilities.Utilities;

/**
 *	Interacts with {@link Integrate6DOFEquations} and any registered listeners to pass flight data from the simulation
 *	listeners. Uses threading to obtain data from the simulation at a reasonable rate
 */
public class FlightData implements Runnable {
	
	private List<FlightDataListener> dataListenerList;
	private Map<FlightDataType, Double> flightData = new EnumMap<FlightDataType, Double>(FlightDataType.class);
	Integrate6DOFEquations runSim;
	
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
		
		flightData.put(FlightDataType.IAS, Utilities.toKnots(simOut.get(SimOuts.TAS)*TAS_TO_IAS));
		
		flightData.put(FlightDataType.VERT_SPEED, simOut.get(SimOuts.ALT_DOT));
		
		flightData.put(FlightDataType.ALTITUDE, simOut.get(SimOuts.ALT));
		
		flightData.put(FlightDataType.ROLL, Math.toDegrees(simOut.get(SimOuts.PHI)));
		flightData.put(FlightDataType.PITCH, Math.toDegrees(simOut.get(SimOuts.THETA)));
		
		flightData.put(FlightDataType.HEADING, Math.toDegrees(simOut.get(SimOuts.PSI)));
		
		flightData.put(FlightDataType.TURN_RATE, Math.toDegrees(simOut.get(SimOuts.PSI_DOT)));
		flightData.put(FlightDataType.TURN_COORD, simOut.get(SimOuts.AN_Y));
		
		flightData.put(FlightDataType.LATITUDE, Math.toDegrees(simOut.get(SimOuts.LAT)));
		flightData.put(FlightDataType.LONGITUDE, Math.toDegrees(simOut.get(SimOuts.LON)));
		
		flightData.put(FlightDataType.NORTH, simOut.get(SimOuts.NORTH));
		flightData.put(FlightDataType.EAST, simOut.get(SimOuts.EAST));
		
		fireDataArrived();
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

	@Override
	public void run() {
		try {
			Thread.sleep(250);
			
			while (runSim.isRunning()) {
				Thread.sleep(12);
				
				if(runSim.getSimOut() != null)
					updateData(runSim.getSimOut());
			}
		} catch (InterruptedException e) {}
	}
}
