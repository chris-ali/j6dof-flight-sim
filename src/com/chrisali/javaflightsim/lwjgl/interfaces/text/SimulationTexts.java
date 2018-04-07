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

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.chrisali.javaflightsim.lwjgl.entities.Camera;
import com.chrisali.javaflightsim.lwjgl.entities.Entity;
import com.chrisali.javaflightsim.simulation.datatransfer.FlightDataType;
import com.chrisali.javaflightsim.simulation.setup.Options;
import com.chrisali.javaflightsim.simulation.setup.SimulationConfiguration;

/**
 * Contains text strings that are rendered by LWJGL to display various simulation info
 * 
 * @author Christopher
 *
 */
public class SimulationTexts {

	private Map<String, GUIText> texts = new HashMap<>();
	
	private DecimalFormat df4 = new DecimalFormat("0.0000");
	private DecimalFormat df2 = new DecimalFormat("0.00");
	private DecimalFormat df0 = new DecimalFormat("0");
	
	public SimulationTexts(FontType font) {
		texts.put("FlightData", new GUIText("", 0.5f, font, new Vector2f(0.01f, 0.01f), 1f, false));
		texts.put("Camera", new GUIText("", 0.5f, font, new Vector2f(0.01f, 0.05f), 1f, false));
		texts.put("Entity", new GUIText("", 0.5f, font, new Vector2f(0.01f, 0.09f), 1f, false));
		texts.put("Paused", new GUIText("PAUSED", 1.15f, font, new Vector2f(0.5f, 0.5f), 1f, false, new Vector3f(1,0,0)));
	}
	
	/**
	 * Updates each GUIText in this object based on received flight data and selected options  
	 * 
	 * @param flightData
	 * @param options
	 */
	public void update(Map<FlightDataType, Double> flightData, SimulationConfiguration config, Camera camera, Entity entity) {
		if (!config.getCameraConfiguration().isShowPanel()) {
			texts.get("FlightData").setTextString(setTelemetryText(flightData));
			
			if (camera.isChaseView()) {
				texts.get("Camera").setTextString(setCameraPosText(camera));
				texts.get("Entity").setTextString(setOwnshipPosText(entity));
			}
		}
				
		texts.get("Paused").setTextString(config.getSimulationOptions().contains(Options.PAUSED) ? "PAUSED" : "");
	}
	
	/**
	 * Prepares a string of flight data from the flightData Map output using the {@link GUIText} object
	 * 
	 * @param flightData
	 * @return string displaying flight data output 
	 */
	private String setTelemetryText(Map<FlightDataType, Double> flightData) {	
		StringBuffer sb = new StringBuffer();

		try {
			sb.append("AIRSPEED: ").append(df0.format(flightData.get(FlightDataType.IAS))).append(" KIAS | ")
			  .append("ROLL: ").append(df4.format(flightData.get(FlightDataType.ROLL))).append(" DEG | ")
			  .append("PITCH: ").append(df4.format(flightData.get(FlightDataType.PITCH))).append(" DEG | ")
			  .append("HEADING: ").append(df0.format(flightData.get(FlightDataType.HEADING))).append(" DEG | ")
			  .append("ALTITUDE: ").append(df0.format(flightData.get(FlightDataType.ALTITUDE))).append(" FT | ")
			  .append("LATITUDE: ").append(df4.format(flightData.get(FlightDataType.LATITUDE))).append(" DEG | ")
			  .append("LONGITUDE: ").append(df4.format(flightData.get(FlightDataType.LONGITUDE))).append(" DEG | ")
			  .append("G-FORCE: ").append(df2.format(flightData.get(FlightDataType.GFORCE))).append(" G | ");
		} catch (Exception e) {
			sb.append("AIRSPEED: ").append("---").append(" KIAS | ")
			  .append("ROLL: ").append("--.----").append(" DEG | ")  
			  .append("PITCH: ").append("--.----").append(" DEG | ")
			  .append("HEADING: ").append("---").append(" DEG | ")
			  .append("ALTITUDE: ").append("---").append(" FT | ")
			  .append("LATITUDE: ").append("--.----").append(" DEG | ")
			  .append("LONGITUDE: ").append("--.----").append(" DEG | ")
			  .append("G-FORCE: ").append("-.--").append(" G | ");
		}
		
		return sb.toString();
	}
	
	/**
	 * Prepares a string of {@link Camera} position data using the {@link GUIText} object
	 * 
	 * @param flightData
	 * @return string displaying camera data output 
	 */
	private String setCameraPosText(Camera camera) {	
		StringBuffer sb = new StringBuffer();

		sb.append("CAMERA:\n")
		  .append("ROLL: ").append(df0.format(camera.getRoll())).append(" DEG | ")
		  .append("PITCH: ").append(df0.format(camera.getPitch())).append(" DEG | ")
		  .append("YAW: ").append(df0.format(camera.getYaw())).append(" DEG | ").append("\n")
		  .append("X POS: ").append(df4.format(camera.getPosition().x*15)).append(" FT | ")
		  .append("Y POS: ").append(df4.format(camera.getPosition().y*15)).append(" FT | ")
		  .append("Z POS: ").append(df2.format(camera.getPosition().z*15)).append(" FT ");
				
		return sb.toString();
	}
	
	/**
	 * Prepares a string of {@link Entity} position data using the {@link GUIText} object
	 * 
	 * @param flightData
	 * @return string displaying entity data output 
	 */
	private String setOwnshipPosText(Entity entity) {	
		StringBuffer sb = new StringBuffer();

		sb.append(entity.getClass().getSimpleName().toUpperCase()).append(":\n")
		  .append("ROLL: ").append(df0.format(entity.getRotX())).append(" DEG | ")
		  .append("PITCH: ").append(df0.format(entity.getRotZ())).append(" DEG | ")
		  .append("YAW: ").append(df0.format(entity.getRotY())).append(" DEG | ").append("\n")
		  .append("X POS: ").append(df4.format(entity.getPosition().x*15)).append(" FT | ")
		  .append("Y POS: ").append(df4.format(entity.getPosition().y*15)).append(" FT | ")
		  .append("Z POS: ").append(df2.format(entity.getPosition().z*15)).append(" FT ");
				
		return sb.toString();
	}

	public Map<String, GUIText> getTexts() { return texts; }

	public void setTexts(Map<String, GUIText> texts) { this.texts = texts; }
}
