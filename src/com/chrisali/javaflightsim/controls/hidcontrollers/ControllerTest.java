package com.chrisali.javaflightsim.controls.hidcontrollers;

import java.util.EnumMap;

import com.chrisali.javaflightsim.controls.FlightControls;
import com.chrisali.javaflightsim.setup.IntegrationSetup;

public class ControllerTest implements Runnable {
	@Override
	public void run() {
		EnumMap<FlightControls, Double> controls = IntegrationSetup.gatherInitialControls("InitialControls");
		double[] integratorConfig 				 = IntegrationSetup.gatherIntegratorConfig("IntegratorConfig");
		SimulationController joystick 			 = new Mouse(controls);
		
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
	
	public static void main(String[] args) {
		Thread testJoystick = new Thread(new ControllerTest());
		testJoystick.start();	
	}

}
