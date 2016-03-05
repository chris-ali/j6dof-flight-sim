package com.chrisali.javaflightsim.menus;

import java.util.EnumSet;
import java.util.EventListener;

import com.chrisali.javaflightsim.simulation.setup.Options;

public interface OptionsConfigurationListener extends EventListener {
	public void optionsConfigured(EnumSet<Options> options, int stepSize);
}
