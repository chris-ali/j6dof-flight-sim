package com.chrisali.javaflightsim.datatransfer;

/**
 *	Used by {@link FlightData} as the key to its flightData EnumMap
 */
public enum FlightDataType {
	PITCH        ("Pitch Angle", "deg"),
	PITCH_RATE   ("Pitch Rate", "deg/sec"),
	ROLL		 ("Roll Angle", "deg"),
	IAS			 ("Indicated Airspeed", "kts"),
	TAS			 ("True Airspeed", "kts"),
	VERT_SPEED	 ("Vertical Speed", "ft/min"),
	HEADING		 ("Heading Angle", "deg"),
	TURN_RATE	 ("Turn Rate", "deg/sec"),
	TURN_COORD	 ("Lateral Acceleration", "g"),
	ALTITUDE	 ("Altitude", "ft"),
	LATITUDE	 ("Latitude", "deg"),
	LONGITUDE	 ("Longitude", "deg"),
	NORTH	 	 ("North", "ft"),
	EAST	 	 ("East", "ft"),
	RPM_1		 ("Engine 1 RPM", "1/min"),
	RPM_2		 ("Engine 2 RPM", "1/min"),
	RPM_3		 ("Engine 3 RPM", "1/min"),
	RPM_4		 ("Engine 4 RPM", "1/min"),
	GEAR		 ("Gear Position", "norm"),
	FLAPS		 ("Flaps Position", "deg"),
	AOA			 ("Angle of Attack", "deg"),
	GFORCE		 ("G Force", "g");
	
	private final String dataType;
	private final String unit;
	
	FlightDataType(String dataType, String unit) {
		this.dataType = dataType;
		this.unit = unit;
	}
	
	public String toString() {return dataType;}
	
	public String getUnit() {return unit;}
}
