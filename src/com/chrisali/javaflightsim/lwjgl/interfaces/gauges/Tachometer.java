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
 * Port of the Swing Tachometer object created in com.chrisali.javaflightsim.swing.instrumentpanel into the LWJGL engine
 * 
 * @author Christopher
 *
 */
public class Tachometer extends AbstractGauge {

	public static final String BASE      = "Gauge_Base";
	public static final String BACK      = "Tach_Back";
	public static final String POINTER_L = "Tach_Pointer_L";
	public static final String POINTER_R = "Tach_Pointer_R";

	/**
	 * Constructor that keeps a heiarchy of all texture names part of this object but does not load them into memory; 
	 * call {@link AbstractGauge#loadTextures(Loader)} afterwards. These files should be PNG in Resources/Gauges
	 * 
	 * @param position - center of the gauge; (-1.0, 1.0) is the top left of the screen, (1.0, -1.0) is the bottom right
	 * @param scale
	 */
	@JsonCreator
	public Tachometer(@JsonProperty("position") Vector2f position, @JsonProperty("scale") float scale) {
		super(position, scale);

		gaugeTextures.put(BASE, new InterfaceTexture(0, position, 0.0f, new Vector2f(scale, scale)));
		gaugeTextures.put(BACK, new InterfaceTexture(0, position, 0.0f, new Vector2f(scale, scale)));
		gaugeTextures.put(POINTER_R, new InterfaceTexture(0, position, 0.0f, new Vector2f(scale, scale)));
		gaugeTextures.put(POINTER_L, new InterfaceTexture(0, position, 0.0f, new Vector2f(scale, scale)));
	}
	
	@Override
	public void setGaugeValue(Map<FlightDataType, Double> flightData) {
		if (flightData != null) {
			double rpmLeft  = flightData.get(FlightDataType.RPM_1),
			       rpmRight = flightData.get(FlightDataType.RPM_2);
							
			double leftRotationAngle  = Math.PI/1.45,
				   rightRotationAngle = Math.PI/1.45;
						
			if (rpmLeft <= 3500)
				leftRotationAngle  = - ((1.8 * Math.PI / 4300) * rpmLeft) + Math.PI/1.45;
			else if (rpmLeft > 3500)
				leftRotationAngle  = - ((1.8 * Math.PI / 4300) * 3500) + Math.PI/1.45;

			if (rpmRight <= 3500)
	    		rightRotationAngle = - ((1.8 * Math.PI / 4300) * rpmRight) + Math.PI/1.45;
			else if (rpmRight > 3500)
				rightRotationAngle = - ((1.8 * Math.PI / 4300) * 3500) + Math.PI/1.45;
			
			InterfaceTexture pointerLeft  = gaugeTextures.get(POINTER_L),
							 pointerRight = gaugeTextures.get(POINTER_R);

			if (pointerLeft != null)
				pointerLeft.setRotation((float)  Math.toDegrees(leftRotationAngle));

			if (pointerRight != null)
				pointerRight.setRotation((float) Math.toDegrees(rightRotationAngle));
		}
	}
}
