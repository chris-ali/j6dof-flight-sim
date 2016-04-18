package com.chrisali.javaflightsim.menus.aircraftpanel;

import java.util.EventListener;

public interface WeightConfiguredListener extends EventListener {
	public void weightConfigured(String aircraftName, double fuelWeight, double payloadWeight);
}
