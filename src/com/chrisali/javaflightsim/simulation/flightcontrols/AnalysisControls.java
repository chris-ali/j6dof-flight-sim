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
package com.chrisali.javaflightsim.simulation.flightcontrols;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chrisali.javaflightsim.simulation.interfaces.Saveable;
import com.chrisali.javaflightsim.simulation.utilities.FileUtilities;
import com.chrisali.javaflightsim.simulation.utilities.SimDirectories;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Contains a serialized collection of injectable control inputs used during Analysis Mode to analyze the 
 * transient flight dynamics of an aircraft
 * 
 * @author Christopher
 *
 */
public class AnalysisControls implements Saveable {
	
	@JsonIgnore
	private static final Logger logger = LogManager.getLogger(AnalysisControls.class);
	
	@JsonIgnore
	private Map<FlightControl, Double> trimControls;

	private List<AnalysisControlInput> analysisInputs;
	
	public AnalysisControls() {
		trimControls = FileUtilities.readSimulationConfiguration().getInitialControls();
		
		analysisInputs = new LinkedList<AnalysisControlInput>();
		analysisInputs.add(new Doublet(FlightControl.AILERON, 10000, 500, 0.035));
		analysisInputs.add(new Doublet(FlightControl.RUDDER, 14000, 500, 0.035));
		analysisInputs.add(new Doublet(FlightControl.ELEVATOR, 52000, 500, 0.035));
		
		analysisInputs.add(new Singlet(FlightControl.ELEVATOR, 62000, 500, 0.035));
		analysisInputs.add(new Singlet(FlightControl.ELEVATOR, 72000, 500, -0.035));
		
		save();
	}
	
	public AnalysisControls(Map<FlightControl, Double> trimControls) {
		this.trimControls = trimControls;
	}
	
	/**
	 * Given a list of {@link AnalysisControlInput} objects, update the controls Map as appropriate with a control input at the 
	 * 
	 * @param timeMS
	 * @param controls
	 */
	public void updateFlightControls(AtomicInteger timeMS, Map<FlightControl, Double> controls) {
		for (AnalysisControlInput input : analysisInputs) {
			// Only consider an input if simulation time is within a time window (from 7/8 of input start time to 9/8 of end time)
			boolean withinTimeWindow = timeMS.get() > (7 * input.getStartTimeMS() / 8) &&
									   timeMS.get() < (9 * (2 * input.getDurationMS() + timeMS.get()) / 8);
			
			if (withinTimeWindow)
				input.generate(controls, trimControls, timeMS);
		}
	}
	
	@Override
	public void save() {
		FileUtilities.serializeJson(SimDirectories.SIM_CONFIG.toString(), this.getClass().getSimpleName(), this);
	}

	public Map<FlightControl, Double> getTrimControls() { return trimControls; }

	public void setTrimControls(Map<FlightControl, Double> trimControls) { this.trimControls = trimControls; }

	public List<AnalysisControlInput> getAnalysisInputs() { return analysisInputs; }

	public void setAnalysisInputs(List<AnalysisControlInput> analysisInputs) { this.analysisInputs = analysisInputs; }
}
