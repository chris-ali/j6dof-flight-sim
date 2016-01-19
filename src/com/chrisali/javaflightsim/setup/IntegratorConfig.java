package com.chrisali.javaflightsim.setup;

public enum IntegratorConfig {
	STARTTIME 	("startTime"),
	DT 	  		("dt"),
	ENDTIME 	("endTime");
	
	private final String integratorConfig;
	
	IntegratorConfig(String integratorConfig) {this.integratorConfig = integratorConfig;}
	
	public String toString() {return integratorConfig;}
}
