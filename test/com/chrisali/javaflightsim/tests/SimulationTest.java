package com.chrisali.javaflightsim.tests;

import java.util.Arrays;
import java.util.HashSet;

import javax.swing.JFrame;

import com.chrisali.javaflightsim.controllers.SimulationController;
import com.chrisali.javaflightsim.plotting.PlotWindow;
import com.chrisali.javaflightsim.simulation.aircraft.AircraftBuilder;
import com.chrisali.javaflightsim.simulation.controls.FlightControls;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.setup.Options;
import com.chrisali.javaflightsim.simulation.setup.Trimming;

/**
 * Runs a test of the flight simulation module in Analysis mode to test an aircraft and the simulation 
 * workings; uses {@link SimulationController} to set the options, {@link AircraftBuilder} to initalize 
 * the aircraft to test, {@link FlightControls} thread to run doublet inputs and {@link PlotWindow} to
 * plot the simulation results at the end
 * 
 * @author Christopher Ali
 *
 */
public class SimulationTest {
	
	private FlightControls flightControls;
	private Thread flightControlsThread;
	
	private Integrate6DOFEquations runSim;
	private Thread simulationThread;
	
	private SimulationController simController;
	
	private PlotWindow plots;
	
	public SimulationTest() {
		this.simController = new SimulationController();
		simController.getSimulationOptions().clear();
		simController.getSimulationOptions().add(Options.ANALYSIS_MODE);
		simController.setAircraftBuilder(new AircraftBuilder("TwinNavion")); // Twin Navion with 2 Lycoming IO-360
		//simController.setAircraftBuilder(new AircraftBuilder()); // Default Navion with Lycoming IO-360
		//simController.setAircraftBuilder(new AircraftBuilder("Navion")); // Navion with lookup tables with Lycoming IO-360
		
		Trimming.trimSim(simController, false);

		this.flightControls = new FlightControls(simController);
		this.flightControlsThread = new Thread(flightControls);
		
		this.runSim = new Integrate6DOFEquations(flightControls, simController);
		this.simulationThread = new Thread(runSim);

		flightControlsThread.start();
		simulationThread.start();
		
		try {Thread.sleep(1000);} 
		catch (InterruptedException e) {}
		
		this.plots = new PlotWindow(new HashSet<String>(Arrays.asList("Controls", "Instruments", "Position", "Rates", "Miscellaneous")),
						 			simController);
		plots.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		FlightControls.setRunning(false);
	}
	
	public static void main(String[] args) {new SimulationTest();}
}
