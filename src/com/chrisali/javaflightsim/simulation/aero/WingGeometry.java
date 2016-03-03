package com.chrisali.javaflightsim.simulation.aero;

import com.chrisali.javaflightsim.simulation.aircraft.Aircraft;

/**
 * Provides Enum values to define the geometric parameters that make up the wing of an {@link Aircraft}. 
 * The String field is used to parse the WingGeometry.txt file of an aircraft in the constructor
 * 
 *   @see Aerodynamics 
 */
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
