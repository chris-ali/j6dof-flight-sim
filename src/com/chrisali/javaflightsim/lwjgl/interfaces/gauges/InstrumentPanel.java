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

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.util.vector.Vector2f;

import com.chrisali.javaflightsim.lwjgl.interfaces.ui.InterfaceTexture;
import com.chrisali.javaflightsim.lwjgl.renderengine.DisplayManager;
import com.chrisali.javaflightsim.lwjgl.renderengine.Loader;
import com.chrisali.javaflightsim.simulation.utilities.SimDirectories;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Contains a collection of AbstractGauge objects that portray an instrument panel for the aircraft, serialized into a 
 * config file called InstrumentPanel.json for each in the directory ./Aircraft/
 * 
 * @author Christopher
 *
 */
public class InstrumentPanel {
	
	@JsonIgnore
	protected static final Logger logger = LogManager.getLogger(InstrumentPanel.class);

	private List<AbstractGauge> gauges;
	
	/**
	 * Generic constructor; call {@link InstrumentPanel#loadAndGetTextures(Loader)} after initializing to load
	 * all {@link InterfaceTexture} objects into memory
	 */
	public InstrumentPanel() {
		/*
		gauges = new ArrayList<>();

		AbstractGauge abstractGauge = new AirspeedIndicator(new Vector2f(-0.9f, -0.25f), 0.125f);
		gauges.add(abstractGauge);
		
		abstractGauge = new ArtificialHorizon(new Vector2f(-0.6f, -0.25f), 0.125f);
		gauges.add(abstractGauge);
		
		abstractGauge = new Altimeter(new Vector2f(-0.3f, -0.25f), 0.125f);
		gauges.add(abstractGauge);
		
		abstractGauge = new TurnCoordinator(new Vector2f(-0.9f, -0.75f), 0.125f);
		gauges.add(abstractGauge);
				
		abstractGauge = new DirectionalGyro(new Vector2f(-0.6f, -0.75f), 0.125f);
		gauges.add(abstractGauge);
		
		abstractGauge = new VerticalSpeed(new Vector2f(-0.3f, -0.75f), 0.125f);
		gauges.add(abstractGauge);
		
		abstractGauge = new Tachometer(new Vector2f(-0.0f, -0.25f), 0.125f);
		gauges.add(abstractGauge);
		
		FileUtilities.serializeJson(SimDirectories.AIRCRAFT + File.separator + "Navion", getClass().getSimpleName(), this);
		FileUtilities.serializeJson(SimDirectories.AIRCRAFT + File.separator + "TwinNavion", getClass().getSimpleName(), this);*/
	}
	
	/**
	 * Loads all {@link InterfaceTexture} objects associated with each {@link AbstractGauge} in this class to be
	 * rendered. Call this method after initializing this object
	 * 
	 * @param loader
	 * @return List of {@link InterfaceTexture} objects
	 */
	public List<InterfaceTexture> loadAndGetTextures(Loader loader, String aircraftName) {
		List<InterfaceTexture> interfaceTextures = new ArrayList<>();
		
		logger.debug("Initializing instrument panel...");
		/*
		InterfaceTexture panelBase = new InterfaceTexture(loader.loadTexture(SimDirectories.AIRCRAFT.toString(), getClass().getSimpleName(), aircraftName), 
														  new Vector2f(-1.0f, -1.0f), 
														  0.f, 
														  new Vector2f(DisplayManager.getAspectRatio() * 1.0f, 1.0f));
		
		interfaceTextures.add(panelBase);
		*/
		for (AbstractGauge gauge : gauges) {
			gauge.loadTextures(loader);
			interfaceTextures.addAll(gauge.getTextures());
		}
		
		logger.debug("...done!");
		
		return interfaceTextures;
	}

	public List<AbstractGauge> getGauges() { return gauges;	}

	public void setGauges(List<AbstractGauge> gauges) { this.gauges = gauges; }	
}
