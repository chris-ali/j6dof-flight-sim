package com.chrisali.javaflightsim.instrumentpanel.flightdata;

import java.util.EventListener;

public interface FlightDataListener extends EventListener {
	public void onFlightDataReceived(FlightData flightData);
}
