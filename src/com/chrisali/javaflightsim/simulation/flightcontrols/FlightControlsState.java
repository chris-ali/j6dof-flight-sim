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
package com.chrisali.javaflightsim.simulation.flightcontrols;

import java.util.EnumMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chrisali.javaflightsim.simulation.setup.SimulationConfiguration;

/**
 * Handles the exact current, trim and transient states of flight controls that other aspects of the simulation (engine, aerodynamics, ground reaction)
 * will see. {@link FlightControlsStateManager} 
 * 
 * @author Christopher Ali
 *
 */
public class FlightControlsState {

	private static final Logger logger = LogManager.getLogger(FlightControlsState.class);
	
	private Map<FlightControl, Double> flightControls;
	private Map<FlightControl, Double> trimflightControls;
	private Map<FlightControl, Double> transientFlightControls;
	
	/**
	 * Initializes the object with a flightControls map of values equal to the initial controls values
	 * 
	 * @param simConfig
	 */
	public FlightControlsState(SimulationConfiguration simConfig) {
		this(null, simConfig);
	}
	
	public FlightControlsState(Map<FlightControl, Double> aFlightControls, SimulationConfiguration simConfig) {
		logger.debug("Initializing flight controls state...");
		
		flightControls = new EnumMap<FlightControl, Double>((aFlightControls != null) ? aFlightControls : simConfig.getInitialControls());
		
		trimflightControls = simConfig.getInitialControls();
		
		transientFlightControls = new EnumMap<FlightControl, Double>(flightControls);
	}

	/**
	 * Resets flightControls back to initial trim values
	 */
	public void reset() {
		for (Map.Entry<FlightControl, Double> entry : flightControls.entrySet())
			flightControls.put(entry.getKey(), trimflightControls.get(entry.getKey()));
	}

	public Map<FlightControl, Double> getFlightControls() { return flightControls; }
	
	public double get(FlightControl parameter) { return flightControls.get(parameter); }
	
	public void set(FlightControl parameter, Double value) { flightControls.put(parameter, value); }
	
	public double getTransientValue(FlightControl parameter) { return transientFlightControls.get(parameter); }
	
	public void setTransientValue(FlightControl parameter, Double value) { transientFlightControls.put(parameter, value); }

	public double getTrimValue(FlightControl parameter) { return trimflightControls.get(parameter); }
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		for (Map.Entry<FlightControl, Double> entry : flightControls.entrySet()) {
			sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
		}
		sb.append("\n");
		
		return sb.toString();
	}
}
