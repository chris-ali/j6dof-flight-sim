package com.chrisali.javaflightsim.menus;

import java.util.EventListener;

public interface AircraftConfigurationListener extends EventListener {
	public void aircraftConfigured(String aircraftName);
}
