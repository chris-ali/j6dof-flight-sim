package com.chrisali.javaflightsim.simulation.setup;

import com.chrisali.javaflightsim.simulation.controls.FlightControlsUtilities;
import com.chrisali.javaflightsim.simulation.controls.hidcontrollers.Joystick;
import com.chrisali.javaflightsim.simulation.controls.hidcontrollers.Mouse;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;

/**
 * Provides Enums for the options EnumSet to provide the following options:
 * 
 *	<p>ANALYSIS_MODE - Removes real-time aspect of the simulation, generates 3 doublets (aileron, rudder and elevator) using {@link FlightControlsUtilities#doubletSeries(java.util.EnumMap, double)} 
 *  into controls, and generates plots at the end of the run; used to analyze transient dynamics of the aircraft </p>
 *	<p>TRIM_MODE - Removes real-time aspect of the simulation and runs the simulation without plotting or control doublets; used for trimming the controls for straight-and-level flight</p>
 *	<p>UNLIMITED_FLIGHT - Removes the end of the simulation to allow for infinite flight; data logging is limited to the last 100 seconds of simulation</p>
 *	<p>PAUSED - Pauses the integration and therefore the simulation; used in combination with RESET to return the simulation to initial conditions</p>
 *	<p>RESET - Resets the integration to initial conditions using {@link IntegrationSetup#gatherInitialConditions(String)}</p>
 *	<p>CONSOLE_DISPLAY - Displays every piece of data in {@link Integrate6DOFEquations#getSimOut()} in the console for each step of integration</p>
 *	<p>USE_JOYSTICK - Uses JInput to integrate a {@link Joystick} to allow pilot in the loop simulation</p>
 *	<p>USE_MOUSE - Uses JInput to integrate a {@link Mouse} to allow pilot in the loop simulation</p>
 */
public enum Options {
	ANALYSIS_MODE,
	TRIM_MODE,
	UNLIMITED_FLIGHT,
	PAUSED,
	RESET,
	CONSOLE_DISPLAY,
	USE_JOYSTICK,
	USE_MOUSE,
}
