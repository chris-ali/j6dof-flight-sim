package com.chrisali.javaflightsim.utilities.tests;

import java.util.EnumSet;

import com.chrisali.javaflightsim.simulation.aircraft.AircraftBuilder;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.setup.Options;

public class SimulationTest {
	public static void main(String[] args) {
		EnumSet<Options> runOptions = EnumSet.of(Options.ANALYSIS_MODE);
		
		//new Thread(new Integrate6DOFEquations(new AircraftBuilder(), runOptions)).start(); 			// Default to Navion with Lycoming IO-360
		//new Thread(new Integrate6DOFEquations(new AircraftBuilder("Navion"), runOptions)).start(); 		// Custom Navion with Lycoming IO-360
		//new Thread(new Integrate6DOFEquations(new AircraftBuilder("TwinNavion"), runOptions)).start();  // Twin Navion with 2 Lycoming IO-360
		new Thread(new Integrate6DOFEquations(new AircraftBuilder("LookupNavion"), runOptions)).start();  // Navion with lookup tables and Lycoming IO-360
	}
}
