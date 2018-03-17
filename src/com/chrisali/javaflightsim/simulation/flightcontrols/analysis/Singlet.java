/*******************************************************************************
 * Copyright (C) 2016-2018 Christopher Ali
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

import com.chrisali.javaflightsim.simulation.flightcontrols.FlightControl;
import com.chrisali.javaflightsim.simulation.flightcontrols.FlightControlsState;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class that contains time and flight control information to generate a singlet control input 
 * 
 * @author Christopher
 *
 */
public class Singlet extends AnalysisControlInput {

	@JsonCreator
	public Singlet(@JsonProperty("controlType") FlightControl controlType, @JsonProperty("startTimeMS") int startTimeMS, 
				   @JsonProperty("durationMS") int durationMS, @JsonProperty("amplitude") double amplitude) {
		super(controlType, startTimeMS, durationMS, amplitude);
	}

	/**
	 * Generates a control singlet and then returns to trim value in trimControls. The start time defines when the singlet should start, the duration indicates how long the 
	 * control is held in that direction, and the amplitude the amount of deflection in one direction. controlInput uses {@link FlightControl} to select
	 * the desired control to use as a singlet 
	 * 
	 * @param timeMS
	 * @param flightControls
	 */
	@Override
	public void generate(AtomicInteger timeMS, FlightControlsState flightControls) {
		Integer time = timeMS.get();
		Integer endTimeMS = startTimeMS + durationMS;
		
		double trimVal = flightControls.getTrimValue(controlType);
		
		boolean started = time.compareTo(startTimeMS) == 1 || time.compareTo(startTimeMS) == 0;
		boolean ended   = time.compareTo(endTimeMS) == 1 || time.compareTo(endTimeMS) == 0;
				
		if (started && !ended)
			flightControls.set(controlType, trimVal + amplitude);
		else 
			flightControls.set(controlType, trimVal);
	}	
}