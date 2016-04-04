package com.chrisali.javaflightsim.simulation.enviroment;

/**
 * Used in {@link Environment} as the key to the environmentParameters EnumMap
 */
public enum EnvironmentParameters {
	GRAVITY		   ("gravity"),
	T  			   ("temperature"),
	P			   ("pressure"),
	RHO			   ("density"),
	A			   ("speedOfSound"),
	WIND_SPEED_N   ("windSpeedN"),
	WIND_SPEED_E   ("windSpeedE"),
	WIND_SPEED_D   ("windSpeedD"),
	TURBULENCE	   ("turbulence");
	
	private final String environmentParameter;
	
	EnvironmentParameters(String environmentParameter) {this.environmentParameter = environmentParameter;}
	
	public String toString() {return environmentParameter;}	
}
