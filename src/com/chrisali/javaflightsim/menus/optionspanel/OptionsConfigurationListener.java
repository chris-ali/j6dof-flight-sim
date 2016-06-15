package com.chrisali.javaflightsim.menus.optionspanel;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.EventListener;

import com.chrisali.javaflightsim.simulation.setup.Options;

public interface OptionsConfigurationListener extends EventListener {
	public void simulationOptionsConfigured(EnumSet<Options> options, int stepSize, 
											EnumMap<DisplayOptions, Integer> displayOptions,
											EnumMap<AudioOptions, Float> audioOptions);
}
