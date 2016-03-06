package com.chrisali.javaflightsim.simulation.aircraft;

/**
 * Provides Enum values to define the parameters that make up the mass properties of an {@link Aircraft}. 
 * The String field is used to parse the MassProperties.txt file of an aircraft in the constructor
 */
public enum MassProperties {
	TOTAL_MASS      	("totalMass"),
	MAX_WEIGHT_FUEL     ("maxWeightFuel"),
	WEIGHT_FUEL     	("weightFuel"),
	MAX_WEIGHT_PAYLOAD  ("maxWeightPayload"),
	WEIGHT_PAYLOAD  	("weightPayload"),
	WEIGHT_EMPTY    	("weightEmpty"),
	J_X					("jx"),
	J_Y					("jy"),
	J_Z					("jz"),
	J_XZ				("jxz"),
	CG_X				("cgX"),
	CG_Y				("cgY"),
	CG_Z				("cgZ");
	
	private final String massProperty;
	
	MassProperties(String massProperty) {this.massProperty = massProperty;}
	
	public String toString() {return massProperty;}
}
