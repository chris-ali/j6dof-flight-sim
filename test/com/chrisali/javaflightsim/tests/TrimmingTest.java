package com.chrisali.javaflightsim.tests;

import com.chrisali.javaflightsim.simulation.aircraft.AircraftBuilder;
import com.chrisali.javaflightsim.simulation.setup.Trimming;

public class TrimmingTest {
	public static void main(String[] args) {
		new TrimmingTest("Navion");
		new TrimmingTest("TwinNavion");
	}
	
	private TrimmingTest(String aircraftName) {
		AircraftBuilder ab = new AircraftBuilder(aircraftName);
		Trimming.trimSim(ab, true);
	}
}
