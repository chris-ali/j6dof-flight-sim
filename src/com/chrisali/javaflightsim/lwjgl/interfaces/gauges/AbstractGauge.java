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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

import com.chrisali.javaflightsim.loader.Loader;
import com.chrisali.javaflightsim.lwjgl.interfaces.ui.InterfaceTexture;
import com.chrisali.javaflightsim.lwjgl.utilities.OTWDirectories;
import com.chrisali.javaflightsim.simulation.datatransfer.FlightDataType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * Abstract representation of an instrument panel gauge modeled as a heiarchy of {@link InterfaceTexture} objects 
 * that are rotated and translated using flight data
 * 
 * @author Christopher
 *
 */
@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
	@Type(value = Altimeter.class, name = "Altimeter"),
	@Type(value = VerticalSpeed.class, name = "VerticalSpeed"),
	@Type(value = AirspeedIndicator.class, name = "AirspeedIndicator"),
	@Type(value = ArtificialHorizon.class, name = "ArtificialHorizon"),
	@Type(value = DirectionalGyro.class, name = "DirectionalGyro"),
	@Type(value = TurnCoordinator.class, name = "TurnCoordinator"),
	@Type(value = Tachometer.class, name = "Tachometer"),
})
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
	 * call {@link AbstractGauge#loadTextures(Loader)} afterwards; texture files should be of PNG type 
	 * 
	 * @param position - center of the gauge; (-1.0, 1.0) is the top left of the screen, (1.0, -1.0) is the bottom right
	 * @param scale
	 * @param 
	 */
	public AbstractGauge(Vector2f position, float scale) {
		this.position = position;
		this.scale = scale;
		gaugeTextures = new LinkedHashMap<String, InterfaceTexture>();
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
		if (gaugeTextures == null || gaugeTextures.size() == 0) {
			logger.error("No texture information stored in class!");
			return;
		}
		
		logger.debug("Loading "+ getClass().getSimpleName() +"'s associated textures...");

		for (Map.Entry<String, InterfaceTexture> entry : gaugeTextures.entrySet()) {
			Texture texture = loader.loadAndGetTexture(entry.getKey(), OTWDirectories.GAUGES.toString()); 

			entry.getValue().setTexture(texture.getTextureID());
		}
	}
	
	/**
	 * Gets a collection of textures from gaugeTextures LinkedHashMap; order is important, as textures at the end 
	 * of this list are rendered last and display on top of all preceeding items
	 * 
	 * @return collection of gauge textures
	 */
	@JsonIgnore
	public Collection<InterfaceTexture> getTextures() { return gaugeTextures.values(); }

	public float getScale() { return scale; }

	public void setScale(float scale) { this.scale = scale; }

	public Vector2f getPosition() { return position; }

	public void setPosition(Vector2f position) { this.position = position; }

	public Map<String, InterfaceTexture> getGaugeTextures() { return gaugeTextures;	}

	public void setGaugeTextures(Map<String, InterfaceTexture> gaugeTextures) { this.gaugeTextures = gaugeTextures;	}
}
