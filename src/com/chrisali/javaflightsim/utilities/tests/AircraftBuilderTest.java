package com.chrisali.javaflightsim.utilities.tests;

import com.chrisali.javaflightsim.aircraft.AircraftBuilder;
import com.chrisali.javaflightsim.propulsion.Engine;

public class AircraftBuilderTest {	
	public AircraftBuilderTest() {
		
		System.out.println("============================");
		System.out.println("============================");
		System.out.println("Default Builder Test");
		System.out.println("============================");
		System.out.println("============================\n");
		
		AircraftBuilder abDefault = new AircraftBuilder();
		
		System.out.println(abDefault.getAircraft().toString() + "\n");
		
		for (Engine engine : abDefault.getEngineList()) {
			System.out.println(engine);
			System.out.println();
		}
	}
	
	public AircraftBuilderTest(String aircraftName) {
		
		System.out.println("============================");
		System.out.println("============================");
		System.out.println( aircraftName + " Builder Test");
		System.out.println("============================");
		System.out.println("============================\n");

		AircraftBuilder ab = new AircraftBuilder(aircraftName);

		System.out.println(ab.getAircraft().toString() + "\n");
		
		for (Engine engine : ab.getEngineList()) {
			System.out.println(engine);
			System.out.println();
		}
	}

	public static void main(String[] args) {
		new AircraftBuilderTest();
		new AircraftBuilderTest("Navion");
		new AircraftBuilderTest("TwinNavion");
	}
}
