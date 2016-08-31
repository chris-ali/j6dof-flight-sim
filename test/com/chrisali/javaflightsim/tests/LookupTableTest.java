package com.chrisali.javaflightsim.tests;

import java.util.EnumMap;

import com.chrisali.javaflightsim.simulation.aero.Aerodynamics;
import com.chrisali.javaflightsim.simulation.aero.StabilityDerivatives;
import com.chrisali.javaflightsim.simulation.aircraft.AircraftBuilder;
import com.chrisali.javaflightsim.simulation.controls.FlightControlType;
import com.chrisali.javaflightsim.simulation.setup.IntegrationSetup;

public class LookupTableTest {
	private EnumMap<FlightControlType, Double> controls = IntegrationSetup.gatherInitialControls("InitialControls");
	private double[] alpha = new double[] {-14, -12, -10, -8, -6, -4, -2, 0, 2, 4, 6, 8, 10, 12, 14, 16};
	private double[] dFlap = new double[] {0, 10, 20, 30, 40};
	AircraftBuilder ab;
	private Aerodynamics aero;
	
	public LookupTableTest(String aircraftName) {								 
		this.ab = new AircraftBuilder("LookupNavion");
		this.aero = new Aerodynamics(ab.getAircraft());
		double clAlpha = 0.0;

		for (int j=0; j<dFlap.length; j++) {
			controls.put(FlightControlType.FLAPS, Math.toRadians(dFlap[j]));
			
			for (double aoa=alpha[0]; aoa<=alpha[alpha.length-1]; aoa+=1) {
				clAlpha = aero.calculateInterpStabDer(new double[] {0.0, 0.0, Math.toRadians(aoa)}, controls, StabilityDerivatives.CM_ALPHA);
				
				System.out.printf("Flaps: %2.0f | Alpha: %2.1f | CL_Alpha: %4.3f%n", dFlap[j], aoa, clAlpha);
				System.out.println("-----------------------------------------");
			}
			System.out.println();
		}
	}

	public static void main(String[] args) {new LookupTableTest("LookupNavion");}
}
