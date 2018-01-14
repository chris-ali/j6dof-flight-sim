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
package com.chrisali.javaflightsim.lwjgl.interfaces.gauges;

import java.util.Map;

import org.lwjgl.util.vector.Vector2f;

import com.chrisali.javaflightsim.lwjgl.interfaces.ui.InterfaceTexture;
import com.chrisali.javaflightsim.lwjgl.renderengine.Loader;
import com.chrisali.javaflightsim.simulation.datatransfer.FlightDataType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Port of the Swing Altimeter object created in com.chrisali.javaflightsim.swing.instrumentpanel into the LWJGL engine
 * 
 * @author Christopher
 *
 */
public class Altimeter extends AbstractGauge {

	public static final String BASE          = "Gauge_Base";
	public static final String BACK          = "Altimeter_Back";
	public static final String POINTER_100   = "Altimeter_Pointer_100";
	public static final String POINTER_1000  = "Altimeter_Pointer_1000";
	public static final String POINTER_10000 = "Altimeter_Pointer_10000";

	/**
	 * Constructor that keeps a heiarchy of all texture names part of this object but does not load them into memory; 
	 * call {@link AbstractGauge#loadTextures(Loader)} afterwards. These files should be PNG in Resources/Gauges
	 * 
	 * @param position - center of the gauge; (-1.0, 1.0) is the top left of the screen, (1.0, -1.0) is the bottom right
	 * @param scale
	 */
	@JsonCreator
	public Altimeter(@JsonProperty("position") Vector2f position, @JsonProperty("scale") float scale) {
		super(position, scale);

		gaugeTextures.put(BASE, new InterfaceTexture(0, position, 0.0f, new Vector2f(scale, scale)));
		gaugeTextures.put(BACK, new InterfaceTexture(0, position, 0.0f, new Vector2f(scale, scale)));
		gaugeTextures.put(POINTER_10000, new InterfaceTexture(0, position, 0.0f, new Vector2f(scale, scale)));
		gaugeTextures.put(POINTER_1000, new InterfaceTexture(0, position, 0.0f, new Vector2f(scale, scale)));
		gaugeTextures.put(POINTER_100, new InterfaceTexture(0, position, 0.0f, new Vector2f(scale, scale)));
	}
	
	@Override
	public void setGaugeValue(Map<FlightDataType, Double> flightData) {
		if (flightData != null) {
			double altitude = flightData.get(FlightDataType.ALTITUDE);
			
			double
				angleStep100ft   = (2 * Math.PI)   / 10.0,
				angleStep1000ft  = angleStep100ft  / 10.0,
				angleStep10000ft = angleStep1000ft / 10.0;
			
			double
			    angleRad100ft   = -((altitude % 1000)  / 100)  * angleStep100ft,
	    		angleRad1000ft  = -((altitude % 10000) / 100)  * angleStep1000ft,
				angleRad10000ft = -((altitude % 100000) / 100) * angleStep10000ft;
			
			InterfaceTexture 
				pointer100ft   = gaugeTextures.get(POINTER_100),
				pointer1000ft  = gaugeTextures.get(POINTER_1000),
				pointer10000ft = gaugeTextures.get(POINTER_10000);

			if (pointer100ft != null)
				pointer100ft.setRotation((float)   Math.toDegrees(angleRad100ft));

			if (pointer1000ft != null)
				pointer1000ft.setRotation((float)  Math.toDegrees(angleRad1000ft));

			if (pointer10000ft != null)
				pointer10000ft.setRotation((float) Math.toDegrees(angleRad10000ft));
		}
	}
}
