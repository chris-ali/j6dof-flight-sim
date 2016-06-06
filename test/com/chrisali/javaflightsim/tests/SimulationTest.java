package com.chrisali.javaflightsim.tests;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;

import javax.swing.JFrame;

import com.chrisali.javaflightsim.plotting.PlotWindow;
import com.chrisali.javaflightsim.simulation.aircraft.AircraftBuilder;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.setup.Options;
import com.chrisali.javaflightsim.simulation.setup.Trimming;

public class SimulationTest {
	public static void main(String[] args) {
		EnumSet<Options> runOptions = EnumSet.of(Options.ANALYSIS_MODE);
		
		//AircraftBuilder ab = new AircraftBuilder(); // Navion with lookup tables and Lycoming IO-360
		//AircraftBuilder ab = new AircraftBuilder("Navion"); // Custom Navion with Lycoming IO-360
		AircraftBuilder ab = new AircraftBuilder("TwinNavion"); // Twin Navion with 2 Lycoming IO-360
		
		Trimming.trimSim(ab, false);
		Integrate6DOFEquations runSim = new Integrate6DOFEquations(ab, runOptions);
		Thread simulationThread = new Thread(runSim);
		simulationThread.start();
		
		PlotWindow plots = new PlotWindow(runSim.getLogsOut(), 
										  new HashSet<String>(Arrays.asList("Controls", "Instruments", "Position", "Rates", "Miscellaneous")),
						 				  ab.getAircraft());
		plots.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
