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
package com.chrisali.javaflightsim.simulation.flightcontrols.analysis;

import java.util.concurrent.atomic.AtomicInteger;

import com.chrisali.javaflightsim.simulation.flightcontrols.ControlParameterActuator;
import com.chrisali.javaflightsim.simulation.flightcontrols.FlightControl;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * Abstract class for an injectable control input that can be used to test an aircraft's flight dynamics in Analysis Mode. 
 * Each time a new subclass is added to the project, be sure to add its type to the JsonSubTypes annotation for this class 
 * 
 * @author Christopher
 *
 */
@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
	@Type(value = Doublet.class, name = "Doublet"),
	@Type(value = Singlet.class, name = "Singlet"),
})
public abstract class AnalysisControlInput implements Comparable<AnalysisControlInput> {

	/**
	 * Which flight control this input should affect
	 */
	protected FlightControl controlType;
	
	/**
	 * When during the simulation this input should begin in milliseconds
	 */
	protected int startTimeMS;
	
	/**
	 * How long this a single control input should last in milliseconds 
	 */
	protected int durationMS;
	
	/**
	 * The mmaximum value (and direction) of control input that this input should reach from trim value in radians
	 */
	protected double amplitude; 

	public AnalysisControlInput(FlightControl controlType, int startTimeMS, int durationMS, double amplitude) {
		this.controlType = controlType;
		this.startTimeMS = startTimeMS;
		this.durationMS = durationMS;
		this.amplitude = amplitude;
	}

	public abstract void generate(AtomicInteger timeMS, ControlParameterActuator actuator);

	public FlightControl getControlType() { return controlType;	}

	public void setControlType(FlightControl controlType) { this.controlType = controlType;	}

	public int getStartTimeMS() { return startTimeMS; }

	public void setStartTimeMS(int startTimeMS) { this.startTimeMS = startTimeMS; }

	public int getDurationMS() { return durationMS;	}

	public void setDurationMS(int durationMS) { this.durationMS = durationMS; }

	public double getAmplitude() { return amplitude; }

	public void setAmplitude(double amplitude) { this.amplitude = amplitude; }

	@Override
	public int compareTo(AnalysisControlInput o) {
		if (this.startTimeMS > o.startTimeMS)
			return 1;
		else if (this.startTimeMS < o.startTimeMS)
			return -1;
		else
			return 0;
	}

	@Override
	public String toString() {
		return "[Type: " + getClass().getSimpleName() + ", Flight Control: " + controlType.toString() + 
				", Start Time (ms): " + startTimeMS + ", Duration (ms): " + durationMS + ", Amplitude (rad): " + amplitude + "]";
	}
}
