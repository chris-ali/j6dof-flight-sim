package com.chrisali.javaflightsim.controls;

public enum FlightControls {
	ELEVATOR    ("elevator", 		Math.toRadians(-25), Math.toRadians(15)), // "Minimum" is up elevator
	AILERON		("aileron",  		Math.toRadians(-15), Math.toRadians(15)), // "Minimum" is left aileron up
	RUDDER		("rudder",   		Math.toRadians(-15), Math.toRadians(15)), // "Minimum" is right rudder
	THROTTLE_1	("throttle_1",	 	0.0, 				 1.0),
	THROTTLE_2	("throttle_2", 		0.0, 				 1.0),
	THROTTLE_3	("throttle_3", 		0.0, 				 1.0),
	THROTTLE_4	("throttle_4", 		0.0, 				 1.0),
	PROPELLER_1	("propeller_1", 	0.0, 				 1.0),
	PROPELLER_2	("propeller_2", 	0.0, 				 1.0),
	PROPELLER_3	("propeller_3", 	0.0, 				 1.0),
	PROPELLER_4	("propeller_4", 	0.0, 				 1.0),
	MIXTURE_1	("mixture_1", 		0.0, 				 1.0),
	MIXTURE_2	("mixture_2",	 	0.0, 				 1.0),
	MIXTURE_3	("mixture_3", 		0.0, 				 1.0),
	MIXTURE_4	("mixture_4",	 	0.0, 				 1.0),
	FLAPS		("flaps", 			0.0, 				 Math.toRadians(30)),
	GEAR		("gear", 			0.0, 				 1.0),
	BRAKE_L		("leftBrake", 		0.0, 				 1.0),
	BRAKE_R		("rightBrake", 		0.0, 				 1.0);
	
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
