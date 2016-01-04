package com.chrisali.javaflightsim.controls;

public enum FlightControls {
	ELEVATOR    ("elevator", 		Math.toRadians(15), Math.toRadians(-25)), // "Minimum" is down elevator
	AILERON		("aileron",  		Math.toRadians(15), Math.toRadians(-15)), // "Minimum" is right aileron up
	RUDDER		("rudder",   		Math.toRadians(15), Math.toRadians(-15)), // "Minimum" is left rudder
	THROTTLE_L	("leftThrottle", 	0.0, 				1.0),
	THROTTLE_R	("rightThrottle", 	0.0, 				1.0),
	PROPELLER_L	("leftPropeller", 	0.0, 				1.0),
	PROPELLER_R	("rightPropeller", 	0.0, 				1.0),
	MIXTURE_L	("leftMixture", 	0.0, 				1.0),
	MIXTURE_R	("rightMixture", 	0.0, 				1.0),
	FLAPS		("flaps", 			0.0, 				Math.toRadians(30)),
	GEAR		("gear", 			0.0, 				1.0),
	BRAKE_L		("leftBrake", 		0.0, 				1.0),
	BRAKE_R		("rightBrake", 		0.0, 				1.0);
	
	private final String control;
	private final double minimum;
	private final double maximum;
	
	FlightControls(String control, double minimum, double maximum) {
		this.control = control;
		this.minimum = minimum;
		this.maximum = maximum;
	}
	
	public String toString() {return control;}
	
	public double getMinimum() {return minimum;}
	
	public double getMaximum() {return maximum;}
}
