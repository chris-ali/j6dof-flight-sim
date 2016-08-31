package com.chrisali.javaflightsim.tests;

import com.chrisali.javaflightsim.controllers.SimulationController;
import com.chrisali.javaflightsim.simulation.controls.FlightControls;
import com.chrisali.javaflightsim.simulation.setup.Options;

/**
 * Test class for {@link FlightControls}. Creates flight controls object and thread to
 * run, and outputs values for each flight control deflection/setting
 * 
 * @author Christopher Ali
 *
 */
public class FlightControlsTest implements Runnable {
	private FlightControls flightControls;
	private Thread flightControlsThread;
	private SimulationController simController;
	
	public FlightControlsTest() {
		this.simController = new SimulationController();
		simController.getSimulationOptions().add(Options.USE_CH_CONTROLS);
		this.flightControls = new FlightControls(simController);
		this.flightControlsThread = new Thread(flightControls);
	}
	
	@Override
	public void run() {
		flightControlsThread.start();
		
		try {
			Thread.sleep(500);
			
			while (FlightControls.isRunning()) {
				System.out.println(flightControls.toString());
				System.out.println();
				System.out.println(simController.getSimulationOptions());
				Thread.sleep((long) (100));
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {new Thread(new FlightControlsTest()).start();}
}
