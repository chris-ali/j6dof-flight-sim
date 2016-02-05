package com.chrisali.javaflightsim.enviroment;

/**
 * Used in {@link Environment} as the key to the environmentParameters EnumMap
 */
public enum EnvironmentParameters {
	GRAVITY		   ("gravity"),
	T  			   ("temperature"),
	P			   ("pressure"),
	RHO			   ("density"),
	A			   ("speedOfSound"),
	WIND_SPEED	   ("windSpeed"),
	WIND_DIRECTION ("windDirection"),
	TURBULENCE	   ("turbulence");
	
	private final String environmentParameter;
	
	EnvironmentParameters(String environmentParameter) {this.environmentParameter = environmentParameter;}
	
	public String toString() {return environmentParameter;}	
}
