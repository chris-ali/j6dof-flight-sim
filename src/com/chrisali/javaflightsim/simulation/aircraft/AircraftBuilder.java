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
package com.chrisali.javaflightsim.simulation.aircraft;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.interfaces.Savable;
import com.chrisali.javaflightsim.simulation.propulsion.Engine;
import com.chrisali.javaflightsim.simulation.propulsion.FixedPitchPropEngine;
import com.chrisali.javaflightsim.simulation.utilities.FileUtilities;
import com.chrisali.javaflightsim.simulation.utilities.SaturationUtilities;
import com.chrisali.javaflightsim.simulation.utilities.SimDirectories;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Wrapper class to build a complete aircraft with a "body" ({@link Aircraft}) and a LinkedHashSet of {@link Engine}(s). This object is used by {@link Integrate6DOFEquations}
 * in the simulation of the aircraft. 
 */
public class AircraftBuilder implements Savable {
	
	@JsonIgnore
	private static final Logger logger = LogManager.getLogger(AircraftBuilder.class);
	
	private Set<Engine> engineList = new LinkedHashSet<>();
	private Aircraft aircraft;
	
	/**
	 *  Default AircraftBuilder constructor, using the default constructors of {@link Aircraft} and {@link FixedPitchPropEngine}
	 *  to create a generic Navion aircraft with one Lycoming IO-360 engine
	 */
	public AircraftBuilder() {
		this.aircraft = new Aircraft();
		this.engineList.add(new FixedPitchPropEngine());
	}
	
	/**
	 * Custom AircraftBuilder constructor that uses a set of text files in a folder with a name matching the aircraftName argument. This folder is located in:
	 * <br><code>Aircraft\</code></br>
	 * An example of the proper file structure and syntax can be seen in the sample aircraft (LookupNavion, Navion and TwinNavion) within this folder.  
	 * @param aircraftName
	 */
	public AircraftBuilder(String aircraftName) {
		logger.debug("Building a " + aircraftName + "...");
		this.aircraft = new Aircraft(aircraftName);
	}
	
	public Aircraft getAircraft() {return this.aircraft;}
	
	public Set<Engine> getEngineList() {return this.engineList;}

	/**
	 * Updates the MassProperties config file with weight percentages
	 * 
	 * @param fuelWeightPercent (0.0 - 1.0)
	 * @param payloadWeightPercent (0.0 - 1.0)
	 */
	public void setMassProps(double fuelWeightPercent, double payloadWeightPercent) {
		fuelWeightPercent = SaturationUtilities.saturatePercentage(fuelWeightPercent);
		payloadWeightPercent = SaturationUtilities.saturatePercentage(payloadWeightPercent);
		
		logger.debug("Updating weights for " + aircraft.getName() + " to " + fuelWeightPercent 
				+ " percent fuel and " + payloadWeightPercent + " percent payload...");
		
		try {	
			aircraft.getMassProps().put(MassProperties.WEIGHT_FUEL, fuelWeightPercent);
			aircraft.getMassProps().put(MassProperties.WEIGHT_PAYLOAD, payloadWeightPercent);
		} catch (Exception e) {
			logger.error("Error updating mass properties!", e);
		}
	}
	
	/**
	 * Saves all properties in this instance to a JSON file in "Aircraft/{aircraft.getName()}" 
	 * via {@link FileUtilities#serializeJson(String, String, Object)}
	 */
	@Override
	public void save() { 
		FileUtilities.serializeJson(SimDirectories.AIRCRAFT.toString() + File.separator + aircraft.getName(), 
									this.getClass().getSimpleName(), 
									this); 
	}
}
