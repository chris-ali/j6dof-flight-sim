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
package com.chrisali.javaflightsim.lwjgl.interfaces.gauges;

import java.util.LinkedHashMap;
import java.util.Map;

import org.lwjgl.util.vector.Vector2f;

import com.chrisali.javaflightsim.lwjgl.interfaces.ui.InterfaceTexture;
import com.chrisali.javaflightsim.lwjgl.renderengine.Loader;
import com.chrisali.javaflightsim.simulation.datatransfer.FlightDataType;

/**
 * Port of the Swing ArtificialHorizon object created in com.chrisali.javaflightsim.swing.instrumentpanel into the LWJGL engine
 * 
 * @author Christopher
 *
 */
public class ArtificialHorizon extends AbstractGauge {
	
	public static final String RING          = "Gauge_Ring";
	public static final String HORIZON_INNER = "AH_Horizon_Inner";
	public static final String HORIZON_OUTER = "AH_Horizon_Outer";
	public static final String POINTER       = "AH_Pointer";

	/**
	 * Constructor that keeps a heiarchy of all texture names part of this object but does not load them into memory; 
	 * call {@link AbstractGauge#loadTextures(Loader)} afterwards. These files should be PNG in Resources/Gauges
	 * 
	 * @param position - center of the gauge; (-1.0, 1.0) is the top left of the screen, (1.0, -1.0) is the bottom right
	 * @param scale
	 */
	public ArtificialHorizon(Vector2f position, float scale) {
		super(position, scale);
		
		gaugeTextures = new LinkedHashMap<>();
		gaugeTextures.put(HORIZON_INNER, new InterfaceTexture(0, position, 0.0f, new Vector2f(scale, scale)));
		gaugeTextures.put(HORIZON_OUTER, new InterfaceTexture(0, position, 0.0f, new Vector2f(scale, scale)));
		gaugeTextures.put(POINTER, new InterfaceTexture(0, position, 0.0f, new Vector2f(scale, scale)));
		gaugeTextures.put(RING, new InterfaceTexture(0, position, 0.0f, new Vector2f(scale, scale)));
	}
	
	@Override
	public void setGaugeValue(Map<FlightDataType, Double> flightData) {
		if (flightData != null) {
			double pitch = flightData.get(FlightDataType.PITCH);
			double roll = flightData.get(FlightDataType.ROLL);
						
			if (pitch > 90)
	            pitch = 90 - (pitch - 90);
	        else if (pitch < -90)
	            pitch = -90 + (-90 - pitch);
	        			
			InterfaceTexture horizonInner = gaugeTextures.get(HORIZON_INNER),
							 horizonOuter = gaugeTextures.get(HORIZON_OUTER);

			if (horizonInner != null)
				horizonInner.setRotation((float) roll);

			if (horizonOuter != null)
				horizonOuter.setRotation((float) roll);

		}
	}
}
