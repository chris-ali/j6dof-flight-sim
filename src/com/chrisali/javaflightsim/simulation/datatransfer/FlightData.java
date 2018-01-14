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

import com.chrisali.javaflightsim.interfaces.Steppable;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.integration.SimOuts;
import com.chrisali.javaflightsim.simulation.utilities.SixDOFUtilities;

/**
 *	Interacts with {@link Integrate6DOFEquations} and any registered listeners to pass flight data from the simulation
 *	listeners. Uses threading to obtain data from the simulation at a reasonable rate
 */
public class FlightData implements Steppable {
	
	private static final Logger logger = LogManager.getLogger(FlightData.class);
	
	private Map<FlightDataType, Double> flightData = Collections.synchronizedMap(new EnumMap<FlightDataType, Double>(FlightDataType.class));
	
	private Integrate6DOFEquations simulation;
	private List<FlightDataListener> dataListenerList;
	
	/**
	 * Creates an instance of {@link FlightData} with a reference to {@link Integrate6DOFEquations} so
	 * that the thread in this class knows when the simulation is running
	 * 
	 * @param simulation
	 */
	public FlightData(Integrate6DOFEquations simulation) {
		this.simulation = simulation;
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
			flightData.put(FlightDataType.IAS, SixDOFUtilities.toKnots(simOut.get(SimOuts.TAS)*TAS_TO_IAS));
			flightData.put(FlightDataType.TAS, SixDOFUtilities.toKnots(simOut.get(SimOuts.TAS)));
			
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
	public boolean canStepNow(int simTimeMS) {
		return simTimeMS % 1 == 0;
	}

	@Override
	public void step() {
		try {
			if(simulation.getSimOut() != null)
				updateData(simulation.getSimOut());
		} catch (Exception ez) {
			logger.error("Exception encountered in Flight Data Listener!", ez);
		}
	}
	
	/**
	 * Adds a listener that implements {@link FlightDataListener} to a list of listeners that can listen
	 * to {@link NewFlightData} 
	 * 
	 * @param dataListener
	 */
	public void addFlightDataListener(FlightDataListener dataListener) {
		logger.debug("Adding flight data listener: " + dataListener.getClass());
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
}
