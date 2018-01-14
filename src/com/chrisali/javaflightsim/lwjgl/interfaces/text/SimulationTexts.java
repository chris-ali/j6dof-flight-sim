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
package com.chrisali.javaflightsim.lwjgl.interfaces.text;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.chrisali.javaflightsim.simulation.datatransfer.FlightDataType;
import com.chrisali.javaflightsim.simulation.setup.Options;

/**
 * Contains text strings that are rendered by LWJGL to display various simulation info
 * 
 * @author Christopher
 *
 */
public class SimulationTexts {

	private Map<String, GUIText> texts = new HashMap<>();
	
	public SimulationTexts(FontType font) {
		texts.put("FlightData", new GUIText("", 0.85f, font, new Vector2f(0, 0), 1f, true));
		texts.put("Paused", new GUIText("PAUSED", 1.15f, font, new Vector2f(0.5f, 0.5f), 1f, false, new Vector3f(1,0,0)));
	}
	
	/**
	 * Updates each GUIText in this object based on received flight data and selected options  
	 * 
	 * @param flightData
	 * @param options
	 */
	public void update(Map<FlightDataType, Double> flightData, Set<Options> options) {
		if (!options.contains(Options.INSTRUMENT_PANEL) && texts.get("FlightData") != null)
			texts.get("FlightData").setTextString(setTelemetryText(flightData));
		
		texts.get("Paused").setTextString(options.contains(Options.PAUSED) ? "PAUSED" : "");
	}
	
	/**
	 * Prepares a string of flight data from the flightData Map output using the {@link GUIText} object
	 * 
	 * @param flightData
	 * @return string displaying flight data output 
	 */
	private String setTelemetryText(Map<FlightDataType, Double> flightData) {
		DecimalFormat df4 = new DecimalFormat("0.0000");
		DecimalFormat df2 = new DecimalFormat("0.00");
		DecimalFormat df0 = new DecimalFormat("0");
		
		StringBuffer sb = new StringBuffer();

		try {
			sb.append("AIRSPEED: ").append(df0.format(flightData.get(FlightDataType.IAS))).append(" KIAS | ")
			  .append("HEADING: ").append(df0.format(flightData.get(FlightDataType.HEADING))).append(" DEG | ")
			  .append("ALTITUDE: ").append(df0.format(flightData.get(FlightDataType.ALTITUDE))).append(" FT | ")
			  .append("LATITUDE: ").append(df4.format(flightData.get(FlightDataType.LATITUDE))).append(" DEG | ")
			  .append("LONGITUDE: ").append(df4.format(flightData.get(FlightDataType.LONGITUDE))).append(" DEG | ")
			  .append("G-FORCE: ").append(df2.format(flightData.get(FlightDataType.GFORCE))).append(" G ");
		} catch (Exception e) {
			sb.append("AIRSPEED: ").append("---").append(" KIAS | ")
			  .append("HEADING: ").append("---").append(" DEG | ")
			  .append("ALTITUDE: ").append("---").append(" FT | ")
			  .append("LATITUDE: ").append("--.----").append(" DEG | ")
			  .append("LONGITUDE: ").append("--.----").append(" DEG | ")
			  .append("G-FORCE: ").append("-.--").append(" G ");
		}
		
		return sb.toString();
	}

	public Map<String, GUIText> getTexts() { return texts; }

	public void setTexts(Map<String, GUIText> texts) { this.texts = texts; }
}
