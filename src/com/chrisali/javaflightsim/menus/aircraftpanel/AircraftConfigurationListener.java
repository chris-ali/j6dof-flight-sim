package com.chrisali.javaflightsim.menus.aircraftpanel;

import java.util.EventListener;

public interface AircraftConfigurationListener extends EventListener {
	public void aircraftConfigured(String aircraftName);
}
