package com.chrisali.javaflightsim.tests;

import com.chrisali.javaflightsim.simulation.aircraft.Aircraft;
/*
 * This class tests the parsing methods defined in the Aircraft class. It helps ensure that the data needed to define an aircraft is being
 * parsed correctly, and allows for debugging of file integrity checking methods. It runs the default constructor first, followed by the 
 * constructor using file parsing. The Aircraft class toString method outputs the stability derivatives, mass properties, and wing geometry
 * of each aircraft
 */
public class AircraftTest {
	public AircraftTest(String aircraftName) {
		System.out.println("Default Aircraft:\n");
		System.out.println(new Aircraft().toString());
		
		System.out.println(aircraftName + " File Parsing:\n");
		System.out.println(new Aircraft(aircraftName).toString());
	}
	
	public static void main(String[] args) {new AircraftTest("Navion");}
}
