package com.chrisali.javaflightsim.utilities.tests;

import java.util.EnumMap;

import com.chrisali.javaflightsim.controls.FlightControls;
import com.chrisali.javaflightsim.controls.hidcontrollers.Keyboard;
import com.chrisali.javaflightsim.controls.hidcontrollers.SimulationController;
import com.chrisali.javaflightsim.setup.IntegrationSetup;

public class ControllerTest implements Runnable {
	@Override
	public void run() {
		EnumMap<FlightControls, Double> controls = IntegrationSetup.gatherInitialControls("InitialControls");
		double[] integratorConfig 				 = IntegrationSetup.unboxDoubleArray(IntegrationSetup.gatherIntegratorConfig("IntegratorConfig"));
		SimulationController joystick 			 = new Keyboard(controls);
		
		for (double t = integratorConfig[0]; t < integratorConfig[2]; t += integratorConfig[1]) {
			try {
				controls = joystick.updateFlightControls(controls);
				
				for (double control : controls.values()) {
					System.out.printf("%7.3f |", control);
				}
				System.out.println();
				
				Thread.sleep((long)(integratorConfig[1]*1000));
			} catch (InterruptedException e) {System.err.println("Thread interrupted!");}
		}
	}
	
	public static void main(String[] args) {new Thread(new ControllerTest()).start();}

}
