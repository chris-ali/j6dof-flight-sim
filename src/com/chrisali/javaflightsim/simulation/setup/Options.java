package com.chrisali.javaflightsim.simulation.setup;

import com.chrisali.javaflightsim.instrumentpanel.InstrumentPanel;
import com.chrisali.javaflightsim.menus.SimulationWindow;
import com.chrisali.javaflightsim.simulation.controls.FlightControlsUtilities;
import com.chrisali.javaflightsim.simulation.controls.hidcontrollers.Joystick;
import com.chrisali.javaflightsim.simulation.controls.hidcontrollers.Keyboard;
import com.chrisali.javaflightsim.simulation.controls.hidcontrollers.Mouse;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;

/**
 * Provides Enums for the options EnumSet to provide the following options:
 * 
 *	<p>ANALYSIS_MODE - Removes real-time aspect of the simulation, generates 3 doublets (aileron, rudder and elevator) using {@link FlightControlsUtilities#doubletSeries(java.util.EnumMap, double)} 
 *  into controls, and generates plots at the end of the run; used to analyze transient dynamics of the aircraft </p>
 *	<p>UNLIMITED_FLIGHT - Removes the end of the simulation to allow for infinite flight; data logging is limited to the last 100 seconds of simulation</p>
 *	<p>PAUSED - Pauses the integration and therefore the simulation; used in combination with RESET to return the simulation to initial conditions</p>
 *	<p>RESET - Resets the integration to initial conditions using {@link IntegrationSetup#gatherInitialConditions(String)}</p>
 *	<p>CONSOLE_DISPLAY - Displays every piece of data in {@link Integrate6DOFEquations#getSimOut()} in the console for each step of integration</p>
 *	<p>USE_JOYSTICK - Uses JInput to integrate a {@link Joystick} and {@link Keyboard} to allow pilot in the loop simulation</p>
 *	<p>USE_MOUSE - Uses JInput to integrate a {@link Mouse} and {@link Keyboard} to allow pilot in the loop simulation</p>
 *  <p>USE_CH_CONTROLS - Uses JInput to integrate a {@link CHControls} and {@link Keyboard} to allow pilot in the loop simulation</p>
 *	<p>USE_KEYBOARD_ONLY - Uses JInput to integrate only a {@link Keyboard} to allow pilot in the loop simulation</p>
 *	<p>INSTRUMENT_PANEL - Displays {@link InstrumentPanel} view in {@link SimulationWindow}</p>
 */
public enum Options {
	ANALYSIS_MODE     ("Analysis Mode"),
	UNLIMITED_FLIGHT  ("Unlimited Flight"),
	PAUSED			  ("Paused"),
	RESET			  ("Reset"),
	CONSOLE_DISPLAY	  ("Console Display"),
	USE_JOYSTICK	  ("Use Joystick"),
	USE_MOUSE		  ("Use Mouse"),
	USE_CH_CONTROLS	  ("Use CH Controls"),
	USE_KEYBOARD_ONLY ("Use Keyboard Only"),
	INSTRUMENT_PANEL  ("Show Instrument Panel");
	
	private String option;
	
	private Options(String option) {this.option = option;}
	
	public String toString() {return option;}
}
