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
package com.chrisali.javaflightsim.swing.plotting;

import org.jfree.chart.plot.XYPlot;

/**
 * Contains enum values used in {@link PlotUtilities} to identify types of plots that can be displayed. Acts as the key to an EnumMap in {@link PlotUtilities} that contains {@link XYPlot} objects.
 */
public enum PlotType {
	VELOCITY 		("Velocity"), 
	POSITION 		("Position"), 
	ALTITUDE 		("Altitude"),
	VERT_SPEED 		("Vertical Speed"),
	HEADING 		("Heading"), 
	ACCELERATION 	("Accelerations"), 
	MOMENT 			("Moments"), 
	EULER_ANGLES 	("Euler Angles"),
	ANGULAR_RATE 	("Angular Rates"), 
	WIND_PARAM 		("Wind Parameters"), 
	TAS 			("True Airspeed"),
	ALPHA_DOT 		("Alpha Dot"),
	MACH 			("Mach"),
	ELEVATOR 		("Elevator"),
	RUDDER 			("Rudder"),
	AILERON 		("Aileron"),
	THROTTLE 		("Throttle"),
	FLAPS           ("Flaps");
	
	private final String plotType;
	
	private PlotType(String plotType) {this.plotType = plotType;}
	
	public String toString() {return plotType;};
}