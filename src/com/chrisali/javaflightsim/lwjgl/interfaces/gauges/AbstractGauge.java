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

import java.util.Collection;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

import com.chrisali.javaflightsim.lwjgl.interfaces.ui.InterfaceTexture;
import com.chrisali.javaflightsim.lwjgl.renderengine.Loader;
import com.chrisali.javaflightsim.lwjgl.utilities.OTWDirectories;
import com.chrisali.javaflightsim.simulation.datatransfer.FlightDataType;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Abstract representation of an instrument panel gauge modeled as a heiarchy of {@link InterfaceTexture} objects 
 * that are rotated and translated using flight data
 * 
 * @author Christopher
 *
 */
public abstract class AbstractGauge {

	@JsonIgnore
	protected static final Logger logger = LogManager.getLogger(AbstractGauge.class);
	
	/**
	 * Order is important; textures at the end of this list are rendered last and display
	 * on top of all preceeding items
	 */
	protected Map<String, InterfaceTexture> gaugeTextures;
	protected Vector2f position;
	protected float scale;
		
	/**
	 * Constructor that keeps a heiarchy of all texture names part of this object but does not load them into memory; 
	 * call {@link AbstractGauge#loadTextures(Loader)} afterwards
	 * 
	 * @param position - center of the gauge; (-1.0, 1.0) is the top left of the screen, (1.0, -1.0) is the bottom right
	 * @param scale
	 * @param textureNames - files should be PNG in Resources/Gauges 
	 */
	public AbstractGauge(Vector2f position, float scale, Map<String, InterfaceTexture> gaugeTextures) {
		this.position = position;
		this.scale = scale;
		this.gaugeTextures = gaugeTextures; 
	}

	/**
	 * Depending on the gauge type, set the rotation and position of each texture (pointer, horizon, etc) as needed; 
	 * textures can be easily moved relative to the gauge's position using {@link Vector2f#translate(float, float)}
	 */
	public abstract void setGaugeValue(Map<FlightDataType, Double> flightData);
	
	/**
	 * After the gauge has been deserialized, call this method to load all textures in to memory
	 * in the textureNames list
	 * 
	 * @param loader
	 */
	public void loadTextures(Loader loader) {
		if (gaugeTextures.size() == 0) {
			logger.error("No texture information stored in class!");
			return;
		}
		
		logger.debug("Loading gauge's associated textures...");
		
		for (Map.Entry<String, InterfaceTexture> entry : gaugeTextures.entrySet()) {
			Texture texture = loader.loadAndGetTexture(entry.getKey(), OTWDirectories.GAUGES.toString()); 

			InterfaceTexture interFaceTexture = entry.getValue();	
			interFaceTexture.setTexture(texture.getTextureID());
		}
	}
	
	/**
	 * Gets a collection of textures from gaugeTextures LinkedHashMap; order is important, as textures at the end 
	 * of this list are rendered last and display on top of all preceeding items
	 * 
	 * @return collection of gauge textures
	 */
	public Collection<InterfaceTexture> getTextures() { return gaugeTextures.values(); }

	public float getScale() { return scale; }

	public void setScale(float scale) { this.scale = scale; }

	public Vector2f getPosition() { return position; }

	public void setPosition(Vector2f position) { this.position = position; }
}
