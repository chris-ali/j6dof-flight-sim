package com.chrisali.javaflightsim.aircraft;

public enum MassProperties {
	WEIGHT_FUEL     ("weightFuel"),
	WEIGHT_PAYLOAD  ("weightPayload"),
	WEIGHT_EMPTY    ("weightEmpty"),
	J_X				("jx"),
	J_Y				("jy"),
	J_Z				("jz"),
	J_XZ			("jxz"),
	CG_X			("cgX"),
	CG_Y			("cgY"),
	CG_Z			("cgZ");
	
	private final String massProperty;
	
	MassProperties(String massProperty) {this.massProperty = massProperty;}
	
	public String toString() {return massProperty;}
}
