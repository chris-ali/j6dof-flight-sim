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
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class that contains time and flight control information to generate a doublet control input. The start time defines when the doublet should start, 
 * the duration indicates how long the control is held in that direction, and the amplitude of the deflection (rad) in one direction. 
 * 
 * @author Christopher
 *
 */
public class Doublet extends AnalysisControlInput {

	@JsonCreator
	public Doublet(@JsonProperty("controlType") FlightControl controlType, @JsonProperty("startTimeMS") int startTimeMS, 
				   @JsonProperty("durationMS") int durationMS, @JsonProperty("amplitude") double amplitude) {
		super(controlType, startTimeMS, durationMS, amplitude);
	}

	/**
	 * Generates a control doublet in the positive and then negative direction (or opposite, if a negative value is specified for amplitude), 
	 * and then returns to trim value. 
	 * 
	 * @param timeMS
	 * @param actuator
	 */
	@Override
	public void generate(AtomicInteger timeMS, ControlParameterActuator actuator) {
		Integer time = timeMS.get();
		Integer firstHalfEndTimeMS = startTimeMS + durationMS;
		Integer doubletEndTimeMS   = startTimeMS + (2 * durationMS);
		
		boolean startedFirstHalf = time.compareTo(startTimeMS) == 1 || time.compareTo(startTimeMS) == 0;
		boolean endedFirstHalf   = time.compareTo(firstHalfEndTimeMS) == 1 || time.compareTo(firstHalfEndTimeMS) == 0;
		
		boolean startedSecondHalf = time.compareTo(firstHalfEndTimeMS) == 1 || time.compareTo(firstHalfEndTimeMS) == 0;
		boolean endedSecondHalf   = time.compareTo(doubletEndTimeMS)   == 1 || time.compareTo(doubletEndTimeMS)   == 0;
		
		if (startedFirstHalf && !endedFirstHalf)
			actuator.handleParameterChange(controlType, (float)(amplitude/controlType.getMaximum()));
		else if (startedSecondHalf && !endedSecondHalf)
			actuator.handleParameterChange(controlType, (float)(-amplitude/controlType.getMaximum()));
		else 
			actuator.handleParameterChange(controlType, 0.0f);
	}	
}
