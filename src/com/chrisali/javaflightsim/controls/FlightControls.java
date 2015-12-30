package com.chrisali.javaflightsim.controls;

public enum FlightControls {
	ELEVATOR    ("elevator"),
	AILERON		("aileron"),
	RUDDER		("rudder"),
	THROTTLE_L	("leftThrottle"),
	THROTTLE_R	("rightThrottle"),
	PROPELLER_L	("leftPropeller"),
	PROPELLER_R	("rightPropeller"),
	MIXTURE_L	("leftMixture"),
	MIXTURE_R	("rightMixture"),
	FLAPS		("flaps"),
	GEAR		("gear"),
	BRAKE_L		("leftBrake"),
	BRAKE_R		("rightBrake");
	
	private final String control;
	
	FlightControls(String control) {this.control = control;}
	
	public String toString() {return control;}
}
