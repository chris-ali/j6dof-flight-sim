package com.chrisali.javaflightsim.tests;

import java.util.EnumMap;
import java.util.Map;

import com.chrisali.javaflightsim.menus.optionspanel.DisplayOptions;
import com.chrisali.javaflightsim.otw.RunWorld;
import com.chrisali.javaflightsim.simulation.aircraft.AircraftBuilder;

public class WorldTest {
	
	public static void main(String[] args) {
		Map<DisplayOptions, Integer> displayOptions = new EnumMap<>(DisplayOptions.class);
		displayOptions.put(DisplayOptions.DISPLAY_HEIGHT, 900);
		displayOptions.put(DisplayOptions.DISPLAY_WIDTH, 1440);
		
		RunWorld world = new RunWorld(displayOptions, new AircraftBuilder());
		Thread worldThread = new Thread(world);
		worldThread.start();
	}
	
}
