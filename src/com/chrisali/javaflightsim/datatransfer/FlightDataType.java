/*******************************************************************************
 * Copyright (C) 2016-2017 Christopher Ali
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *  If you have any questions about this project, you can visit
 *  the project's GitHub repository at: http://github.com/chris-ali/j6dof-flight-sim/
 ******************************************************************************/
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
