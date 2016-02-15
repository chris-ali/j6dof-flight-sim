package com.chrisali.javaflightsim.instrumentpanel.flightdata;

import java.util.EnumMap;

import com.chrisali.javaflightsim.instrumentpanel.panel.FlightDataListener;
import com.chrisali.javaflightsim.utilities.integration.SimOuts;

public class FlightData {
	private static final double FT_S_TO_KTS = 1/1.68;
	
	private FlightDataListener dataListener;
	private EnumMap<FlightDataType, Double> flightData = new EnumMap<FlightDataType, Double>(FlightDataType.class);
	
	public EnumMap<FlightDataType, Double> getFlightData() {return flightData;}
	
	public void updateData(EnumMap<SimOuts, Double> simOut) {
		final Double TAS_TO_IAS = 1/(1+((simOut.get(SimOuts.ALT)/1000)*0.02));
		
		flightData.put(FlightDataType.IAS, simOut.get(SimOuts.TAS)*FT_S_TO_KTS*TAS_TO_IAS);
		
		flightData.put(FlightDataType.VERT_SPEED, simOut.get(SimOuts.ALT_DOT));
		
		flightData.put(FlightDataType.ALTITUDE, simOut.get(SimOuts.ALT));
		
		flightData.put(FlightDataType.ROLL, Math.toDegrees(simOut.get(SimOuts.PHI)));
		flightData.put(FlightDataType.PITCH, Math.toDegrees(simOut.get(SimOuts.THETA)));
		
		
		flightData.put(FlightDataType.HEADING, Math.toDegrees(simOut.get(SimOuts.PSI)));
		
		flightData.put(FlightDataType.TURN_RATE, Math.toDegrees(simOut.get(SimOuts.PSI_DOT)));
		flightData.put(FlightDataType.TURN_COORD, simOut.get(SimOuts.AN_Y));
		
		fireDataArrived();
	}
	
	private void fireDataArrived() {
		if(dataListener != null)
			dataListener.onFlightDataReceived();
	}
}
