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

import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.propulsion.Engine;
import com.chrisali.javaflightsim.simulation.propulsion.EngineParameters;
import com.chrisali.javaflightsim.simulation.propulsion.FixedPitchPropEngine;
import com.chrisali.javaflightsim.simulation.utilities.FileUtilities;
import com.chrisali.javaflightsim.simulation.utilities.SimDirectories;
import com.chrisali.javaflightsim.simulation.utilities.SimFiles;
import com.fasterxml.jackson.annotation.JsonIgnoreType;

/**
 * Wrapper class to build a complete aircraft with a "body" ({@link Aircraft}) and a LinkedHashSet of {@link Engine}(s). This object is used by {@link Integrate6DOFEquations}
 * in the simulation of the aircraft. 
 */
@JsonIgnoreType
public class AircraftBuilder {
	
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
		
		logger.debug("Getting propulsion settings for " + aircraftName + "...");
		List<String[]> readPropulsionFile = FileUtilities.readFileAndSplit(aircraftName, SimDirectories.AIRCRAFT.toString(), SimFiles.PROPULSION.toString());

		// Gets the number of engines on the aircraft from the first line of the
		// String[] ArrayList
		int numEngines = Integer.parseInt(readPropulsionFile.get(0)[1]);
		
		if (numEngines > 0 & numEngines < 5) {	
			for (int i = 1; i <= numEngines; i++) {
				Map<EngineParameters, String> engineParams = new EnumMap<EngineParameters, String>(EngineParameters.class);
				
				// Iterate through propulsion file, assign engine parameters to EnumMap from 
				// lines of readPropulsionFile that match the engine number (engX_1, maxBHP_2, etc)
				// using .startsWith to get the parameter (engX) and .endsWith to get the strapping (1)
				for (String[] line : readPropulsionFile) {
					for (EngineParameters engineParam : EngineParameters.values()) {
						if (line[0].endsWith(String.valueOf(i)) & line[0].startsWith(engineParam.toString()))
							engineParams.put(engineParam, line[1]);
					}
				}
				
				double[] enginePosition = new double[]{Double.parseDouble(engineParams.get(EngineParameters.POS_X)), 
													   Double.parseDouble(engineParams.get(EngineParameters.POS_Y)), 
													   Double.parseDouble(engineParams.get(EngineParameters.POS_Z))};
				
				// Currently only one type of engine, so just default to creating a fixedPitchPropEngine 
				switch (engineParams.get(EngineParameters.TYPE)) {
					case "fixedPitchPropEngine":
					default:
					logger.debug("Adding a propeller engine at station: (" + enginePosition[0] + ", " + enginePosition[1] + ", " + enginePosition[2] + ") ft...");
					engineList.add(new FixedPitchPropEngine(engineParams.get(EngineParameters.NAME), 
															Double.parseDouble(engineParams.get(EngineParameters.MAX_BHP)), 
															Double.parseDouble(engineParams.get(EngineParameters.MAX_RPM)), 
															Double.parseDouble(engineParams.get(EngineParameters.PROP_DIAMETER)), 
															enginePosition, 
															i));	
					break;
				}
			}
		} else {
			logger.error("Invalid number of engines! Defaulting to a single engine propeller engine at (0, 0, 0) ft...");
			engineList.add(new FixedPitchPropEngine());
		}
	}
	
	public Aircraft getAircraft() {return this.aircraft;}
	
	public Set<Engine> getEngineList() {return this.engineList;}
}
