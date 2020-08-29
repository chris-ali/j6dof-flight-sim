/*******************************************************************************
 * Copyright (C) 2016-2020 Christopher Ali
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
package com.chrisali.javaflightsim.simulation.aircraft;

import org.apache.commons.math3.analysis.interpolation.PiecewiseBicubicSplineInterpolatingFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Wrapper for the Apache Commons' PiecewiseBicubicSplineInterpolatingFunction that allows a double value to be
 * interpolated for two given breakpoints. It is also capable of returning a single value if no interpolation 
 * arrays are specified upon construction
 * 
 * @author Christopher
 *
 */
@JsonInclude(Include.NON_NULL)
public class LookupTable {
	
	@JsonIgnore
	private static final Logger logger = LogManager.getLogger(LookupTable.class);
	
	private String name;
	
	private double[] breakPointFlap;
	
	private double[] breakPointAngle;
	
	private double[][] lookupValues;

	private Double value;
	
	@JsonIgnore
	private PiecewiseBicubicSplineInterpolatingFunction pbsif;
	
	public LookupTable() {}

	/**
	 * Creates a LookupTable without any interpolation capabilities, returning only a single value
	 * 
	 * @param value
	 */
	public LookupTable(double value, String name) {
		this.value = value;
		this.name = name;
	}
		
	/**
	 * Creates a LookupTable capable of interpolating in two dimensions; one for an angle of deflection, the other
	 * for deflection of a control surface
	 * 
	 * @param breakPointAngle
	 * @param breakPointFlap
	 * @param lookupValues
	 * @param defaultValue
	 * @param name
	 */
	@JsonCreator
	public LookupTable(@JsonProperty(required=false, value="breakPointAngle") double[] breakPointAngle, 
					   @JsonProperty(required=false, value="breakPointFlap") double[] breakPointFlap, 
					   @JsonProperty(required=false, value="lookupValues") double[][] lookupValues, 
					   @JsonProperty(required=false, value="value") double defaultValue,
					   @JsonProperty(required=false, value="name") String name) {
		this.breakPointAngle = breakPointAngle;
		this.breakPointFlap = breakPointFlap;
		this.lookupValues = lookupValues;
		this.value = defaultValue;
		this.name = name;
		
		if (breakPointAngle != null && breakPointFlap != null && lookupValues != null) {
			logger.info("Creating an interpolating lookup table for " + name + "...");
			pbsif = new PiecewiseBicubicSplineInterpolatingFunction(breakPointAngle, breakPointFlap, lookupValues);
		}
	}

	/**
	 * @param angle
	 * @param flap
	 * @return an interpolated Double value if an interpolating function has been initialized, otherwise returns a constant value
	 */
	public Double interpolate(double angle, double flap) {	
		Double interp = value;
		
		if (pbsif != null)
			interp = pbsif.value(angle, flap);
		
		if (interp == null) {
			logger.error("Null value encountered in interpolation of " + name + "! Returning 0...");			
			interp = 0.0;
		}
		
		return interp;
	}
		
	public String getName() { return name; }

	public void setName(String name) { this.name = name; }

	/**
	 * @return constant Double value specified upon construction of a non-interpolating LookupTable
	 */
	public Double getValue() { return value; }

	public void setValue(Double value) { this.value = value; }

	public double[] getBreakPointFlap() { return breakPointFlap; }

	public void setBreakPointFlap(double[] breakPointFlap) { this.breakPointFlap = breakPointFlap; }

	public double[] getBreakPointAngle() { return breakPointAngle; }

	public void setBreakPointAngle(double[] breakPointAngle) { this.breakPointAngle = breakPointAngle; }

	public double[][] getLookupValues() { return lookupValues; }

	public void setLookupValues(double[][] lookUpValues) { this.lookupValues = lookUpValues; }	
}
