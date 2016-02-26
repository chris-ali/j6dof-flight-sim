package com.chrisali.javaflightsim.instrumentpanel.panel;

import java.util.EventListener;

import com.chrisali.javaflightsim.instrumentpanel.flightdata.FlightData;

public interface FlightDataListener extends EventListener {
	public void onFlightDataReceived(FlightData flightData);
}
