package com.chrisali.javaflightsim.menus;

import java.util.EventListener;

public interface SimulationSetupListener extends EventListener {
	public void EventOccurred (SimulationSetupEvent ev);
}
