package com.chrisali.javaflightsim.tests;

import java.util.EnumMap;

import org.apache.commons.lang3.ArrayUtils;

import com.chrisali.javaflightsim.simulation.controls.FlightControls;
import com.chrisali.javaflightsim.simulation.controls.hidcontrollers.AbstractController;
import com.chrisali.javaflightsim.simulation.controls.hidcontrollers.CHControls;
import com.chrisali.javaflightsim.simulation.setup.IntegrationSetup;

public class ControllerTest implements Runnable {
	@Override
	public void run() {
		EnumMap<FlightControls, Double> controls = IntegrationSetup.gatherInitialControls("InitialControls");
		double[] integratorConfig 				 = ArrayUtils.toPrimitive(IntegrationSetup.gatherIntegratorConfig("IntegratorConfig").values()
				   											 							  .toArray(new Double[3]));
		AbstractController joystick 			 = new CHControls(controls);
		
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
