package com.chrisali.javaflightsim.aero;

public enum WingGeometry {
	C_BAR  ("c_bar"),
	S_WING ("s_wing"),
	B_WING ("b_wing"),
	AC_X   ("acX"),
	AC_Y   ("acY"),
	AC_Z   ("acZ");
	
	private final String wingGeometry;
	
	WingGeometry(String wingGeometry) {this.wingGeometry = wingGeometry;}
	
	public String toString() {return wingGeometry;}
}
