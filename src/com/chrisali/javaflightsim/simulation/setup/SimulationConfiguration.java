/**
 * 
 */
package com.chrisali.javaflightsim.simulation.setup;

import java.util.EnumMap;
import java.util.EnumSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chrisali.javaflightsim.interfaces.Saveable;
import com.chrisali.javaflightsim.simulation.flightcontrols.FlightControl;
import com.chrisali.javaflightsim.simulation.utilities.FileUtilities;
import com.chrisali.javaflightsim.simulation.utilities.SimDirectories;
import com.chrisali.javaflightsim.simulation.utilities.SixDOFUtilities;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Contains collections and methods used to configure the simulation and save/load 
 * configuration to/from external files
 */
public class SimulationConfiguration implements Saveable {
	
	@JsonIgnore
	private static final Logger logger = LogManager.getLogger(SimulationConfiguration.class);
	
	private EnumSet<Options> simulationOptions;
	private EnumMap<InitialConditions, Double> initialConditions;
	private EnumMap<IntegratorConfig, Double> integratorConfig;
	private EnumMap<FlightControl, Double> initialControls; 
	private String selectedAircraft;

	private DisplayConfiguration displayConfiguration;
	
	private AudioConfiguration audioConfiguration;
	
	private CameraConfiguration cameraConfiguration;

	public SimulationConfiguration() { }
		
	/**
	 * Saves all configuration fields in this instance to a JSON file via {@link FileUtilities#serializeJson(String, String, Object)}
	 */
	@Override
	public void save() { 
		FileUtilities.serializeJson(SimDirectories.SIM_CONFIG.toString(), this.getClass().getSimpleName(), this); 
	}
	
	public EnumSet<Options> getSimulationOptions() { return simulationOptions; }
	
	/**
	 * Updates simulation and display options
	 * 
	 * @param newOptions
	 */
	public void updateOptions(EnumSet<Options> newOptions) {
		logger.info("Updating simulation options...");
		
		try { simulationOptions = EnumSet.copyOf(newOptions); } 
		catch (Exception e) { logger.error("Error updating simulation options!", e); }
	}
		
	public EnumMap<IntegratorConfig, Double> getIntegratorConfig() { return integratorConfig; }
	
	public void setIntegratorConfig(EnumMap<IntegratorConfig, Double> integratorConfig) { this.integratorConfig = integratorConfig;	}

	@JsonIgnore
	public int getSimulationRateHz() {
		return (int)(1/integratorConfig.get(IntegratorConfig.DT));
	}

	@JsonIgnore
	public void setSimulationRateHz(int simulationRateHz) {
		if (simulationRateHz == 0) {
			logger.warn("Attempted to set simulation rate to 0 Hz, ignoring...");			
			return;
		}
		
		integratorConfig.put(IntegratorConfig.DT, (1/((double)simulationRateHz)));
	}
	
	public CameraConfiguration getCameraConfiguration() { return cameraConfiguration; }
		
	public AudioConfiguration getAudioConfiguration() { return audioConfiguration; }

	public DisplayConfiguration getDisplayConfiguration() { return displayConfiguration; }

	public EnumMap<FlightControl, Double> getInitialControls() { return initialControls; }

	public void setInitialControls(EnumMap<FlightControl, Double> initialControls) { this.initialControls = initialControls; }
		
	public EnumMap<InitialConditions, Double> getInitialConditions() { return initialConditions; }
	
	public void setInitialConditions(EnumMap<InitialConditions, Double> initialConditions) { this.initialConditions = initialConditions; }

	/**
	 * Updates initialConditions file with the following arguments, converted to radians and ft/sec:
	 * 
	 * @param coordinates [latitude, longitude]
	 * @param heading 
	 * @param altitude 
	 * @param airspeed
	 */
	public void setInitialConditions(double[] coordinates, double heading, double altitude, double airspeed) {
		logger.info("Updating simulation intitial conditions...");
		
		try {	
			initialConditions.put(InitialConditions.INITLAT, Math.toRadians(coordinates[0]));
			initialConditions.put(InitialConditions.INITLON, Math.toRadians(coordinates[1]));
			initialConditions.put(InitialConditions.INITPSI, Math.toRadians(heading));
			initialConditions.put(InitialConditions.INITU,   SixDOFUtilities.toFtPerSec(airspeed));
			initialConditions.put(InitialConditions.INITD,   altitude);
			
			// Temporary method to calcuate north/east position from lat/lon position 
			initialConditions.put(InitialConditions.INITN, (Math.sin(Math.toRadians(coordinates[0])) * 20903520));
			initialConditions.put(InitialConditions.INITE, (Math.sin(Math.toRadians(coordinates[1])) * 20903520));
		} catch (Exception e) {
			logger.error("Error updating simulation initial conditions!", e);
		}
	}

	public String getSelectedAircraft() { return selectedAircraft; }

	public void setSelectedAircraft(String selectedAircraft) { this.selectedAircraft = selectedAircraft; }	
}
