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
package com.chrisali.javaflightsim.lwjgl.interfaces.gauges;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.util.vector.Vector2f;

import com.chrisali.javaflightsim.lwjgl.interfaces.ui.InterfaceTexture;
import com.chrisali.javaflightsim.lwjgl.loader.Loader;
import com.chrisali.javaflightsim.simulation.datatransfer.FlightData;
import com.chrisali.javaflightsim.simulation.datatransfer.FlightDataType;
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
	
	private Vector2f panelPosition;
	
	private Vector2f panelScale;
	
	/**
	 * Generic constructor; call {@link InstrumentPanel#loadAndGetTextures(Loader)} after initializing to load
	 * all {@link InterfaceTexture} objects into memory
	 */
	public InstrumentPanel() { }
	
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
		
		InterfaceTexture panelBase = new InterfaceTexture(loader.loadTexture(SimDirectories.AIRCRAFT.toString(), getClass().getSimpleName(), aircraftName), 
														  panelPosition, 
														  0.0f, 
														  panelScale);
		
		interfaceTextures.add(panelBase);
		
		for (AbstractGauge gauge : gauges) {
			gauge.loadTextures(loader);
			interfaceTextures.addAll(gauge.getTextures());
		}
		
		logger.debug("...done!");
		
		return interfaceTextures;
	}
	
	/**
	 * Updates each gauge in this instrument panel with {@link FlightData} received from the simulation 
	 * 
	 * @param flightData
	 */
	public void update(Map<FlightDataType, Double> flightData) {
		for (AbstractGauge gauge : gauges)
			gauge.setGaugeValue(flightData);
	}

	public List<AbstractGauge> getGauges() { return gauges;	}

	public void setGauges(List<AbstractGauge> gauges) { this.gauges = gauges; }

	public Vector2f getPanelPosition() { return panelPosition; }

	public void setPanelPosition(Vector2f panelPosition) { this.panelPosition = panelPosition; }

	public Vector2f getPanelScale() { return panelScale; }

	public void setPanelScale(Vector2f panelScale) { this.panelScale = panelScale; }	
}
