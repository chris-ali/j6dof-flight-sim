package com.chrisali.javaflightsim;

import java.util.EnumMap;

import com.chrisali.javaflightsim.aircraft.Aircraft;
import com.chrisali.javaflightsim.propulsion.FixedPitchPropEngine;
import com.chrisali.javaflightsim.setup.Options;
import com.chrisali.javaflightsim.utilities.integration.Integrate6DOFEquations;

public class RunSimulation {

	public static void main(String[] args) {

		// TODO gather all initial conditions/controls from trim routine

		// TODO Put settings into own class 
		EnumMap<Options, Boolean> options = new EnumMap<Options, Boolean>(Options.class);
		options.put(Options.ANALYSIS_MODE, true);
		options.put(Options.PAUSED, false);
		options.put(Options.RESET, false);
		options.put(Options.UNLIMITED_FLIGHT, false);
		options.put(Options.CONSOLE_DISPLAY, false);
		options.put(Options.USE_JOYSTICK, false);
		options.put(Options.USE_MOUSE, false);
		
		// Create simulation thread using default aircraft, and start it
		new Thread(new Integrate6DOFEquations(new Aircraft(), 			   // Default to Navion
											  new FixedPitchPropEngine(),  // Default to Lycoming IO-360
											  options)).start();
	}
}
