package com.chrisali.javaflightsim.simulation.propulsion;

import com.chrisali.javaflightsim.simulation.aircraft.AircraftBuilder;

/**
 *	Provides keys for engineParams EnumMap in {@link AircraftBuilder} class, used to parse Propulsion 
 *	text file to create an engine object for an aircraft
 */
public enum EngineParameters {
	NAME			("engineName"),
	TYPE			("engineType"),
	POS_X			("engX"),
	POS_Y			("engY"),
	POS_Z			("engZ"),
	MAX_BHP			("maxBHP"),
	MAX_RPM			("maxRPM"),
	PROP_DIAMETER	("propDiameter"),
	FUEL_FLOW		("fuelFlow"),
	RPM				("rpm"),
	BHP				("bhp"),
	N1				("n1"),
	N2				("n2"),
	ITT				("itt");
	
	private String engineParameter;
	
	EngineParameters(String engineParameter) {this.engineParameter = engineParameter;};
	
	public String toString() {return engineParameter;}
}
