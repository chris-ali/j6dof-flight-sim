package com.chrisali.javaflightsim.plotting;

import org.jfree.chart.plot.XYPlot;

/**
 * Contains enum values used in {@link PlotUtilities} to identify types of plots that can be displayed. Acts as the key to an EnumMap in {@link PlotUtilities} that contains {@link XYPlot} objects.
 */
public enum PlotType {
	VELOCITY, 
	POSITION, 
	ALTITUDE,
	VERT_SPEED,
	HEADING, 
	ACCELERATION, 
	MOMENT, 
	EULER_ANGLES,
	ANGULAR_RATE, 
	WIND_PARAM, 
	TAS,
	ALPHA_DOT,
	MACH,
	ELEVATOR,
	RUDDER,
	AILERON,
	THROTTLE,
	FLAPS;
}
