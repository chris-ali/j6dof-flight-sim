package com.chrisali.javaflightsim.simulation.propulsion;

import com.chrisali.javaflightsim.simulation.aircraft.AircraftBuilder;

/**
 *	Provides the switch cases for {@link AircraftBuilder} to determine which type of engine is 
 *	defined in the Propulsion text file when parsing it
 */
public enum EngineType {
	FIXEDPITCHPROP  ("fixedPitchProp"),
	CONSTSPEEDPROP	("constSpeedProp"),
	TURBOPROP		("turboprop"),
	JET				("jet"),
	ELECTRIC		("electric");
	
	private final String engineType;
	
	EngineType(String engineType) {this.engineType = engineType;}
	
	public String toString() {return engineType;}
}
