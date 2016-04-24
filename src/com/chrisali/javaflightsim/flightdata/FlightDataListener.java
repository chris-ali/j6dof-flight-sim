package com.chrisali.javaflightsim.flightdata;

import java.util.EventListener;

public interface FlightDataListener extends EventListener {
	public void onFlightDataReceived(FlightData flightData);
}
