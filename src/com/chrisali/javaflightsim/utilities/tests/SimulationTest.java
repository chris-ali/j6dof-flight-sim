package com.chrisali.javaflightsim.utilities.tests;

import java.util.EnumSet;

import com.chrisali.javaflightsim.plotting.MakePlots;
import com.chrisali.javaflightsim.simulation.aircraft.AircraftBuilder;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.setup.Options;

public class SimulationTest {
	public static void main(String[] args) {
		EnumSet<Options> runOptions = EnumSet.of(Options.ANALYSIS_MODE);
		
		AircraftBuilder ab = new AircraftBuilder(); // Navion with lookup tables and Lycoming IO-360
		//AircraftBuilder ab = new AircraftBuilder("Navion"); // Custom Navion with Lycoming IO-360
		//AircraftBuilder ab = new AircraftBuilder("LookupNavion"); // Navion with lookup tables and Lycoming IO-360
		//AircraftBuilder ab = new AircraftBuilder("TwinNavion"); // Twin Navion with 2 Lycoming IO-360
		
		Integrate6DOFEquations runSim = new Integrate6DOFEquations(ab, runOptions);
		Thread simulationThread = new Thread(runSim);
		simulationThread.start();
		
		new Thread(new MakePlots(runSim.getLogsOut(), 
				 				 new String[] {"Controls", "Instruments", "Position", "Rates", "Miscellaneous"},
				 				 ab.getAircraft())).start();
	}
}
