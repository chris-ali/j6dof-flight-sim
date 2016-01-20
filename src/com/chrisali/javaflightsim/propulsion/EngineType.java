package com.chrisali.javaflightsim.propulsion;

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
