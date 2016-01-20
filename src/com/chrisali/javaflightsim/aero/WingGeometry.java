package com.chrisali.javaflightsim.aero;

public enum WingGeometry {
	C_BAR  ("weightFuel"),
	S_WING ("weightPayload"),
	B_WING ("weightEmpty"),
	AC_X   ("acX"),
	AC_Y   ("acY"),
	AC_Z   ("acZ");
	
	private final String wingGeometry;
	
	WingGeometry(String wingGeometry) {this.wingGeometry = wingGeometry;}
	
	public String toString() {return wingGeometry;}
}
