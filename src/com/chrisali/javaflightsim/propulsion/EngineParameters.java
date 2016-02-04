package com.chrisali.javaflightsim.propulsion;

public enum EngineParameters {
	NAME			("engineName"),
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
