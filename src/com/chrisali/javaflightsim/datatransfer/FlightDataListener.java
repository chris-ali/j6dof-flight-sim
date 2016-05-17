package com.chrisali.javaflightsim.datatransfer;

import java.util.EventListener;

public interface FlightDataListener extends EventListener {
	public void onFlightDataReceived(FlightData flightData);
}
