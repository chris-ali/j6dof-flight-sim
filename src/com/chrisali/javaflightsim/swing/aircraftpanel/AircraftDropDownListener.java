package com.chrisali.javaflightsim.swing.aircraftpanel;

import java.util.EventListener;

public interface AircraftDropDownListener extends EventListener {
	public void aircraftSelected(String aircraftName);
}