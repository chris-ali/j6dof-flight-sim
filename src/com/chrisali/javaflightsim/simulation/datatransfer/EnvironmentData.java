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
package com.chrisali.javaflightsim.simulation.datatransfer;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 *	Container of data to transmit values such as terrain height back into the simulation. Relatively thread safe.
 */
public class EnvironmentData {

	private Map<EnvironmentDataType, Double> environmentData = Collections.synchronizedMap(new EnumMap<EnvironmentDataType, Double>(EnvironmentDataType.class));

	public EnvironmentData() {}
	
	public Map<EnvironmentDataType, Double> getEnvironmentData() { 
		return environmentData; 
	}
	
	/**
	 * Assigns and converts the values needed for the environmentData EnumMap  
	 * 
	 * @param terrainHeight
	 */
	public void updateData(float terrainHeight) {
		synchronized (environmentData) {
			environmentData.put(EnvironmentDataType.TERRAIN_HEIGHT, (double) terrainHeight);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		for (Map.Entry<EnvironmentDataType, Double> entry : environmentData.entrySet()) {
			 sb.append(entry.getKey().toString()).append(": ").append(entry.getValue())
			   .append(" ").append(entry.getKey().getUnit()).append("\n");
		}
		sb.append("\n");
		
		return sb.toString();
	}
}
