/**
 * 
 */
package com.chrisali.javaflightsim.simulation.setup;

import java.io.File;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chrisali.javaflightsim.simulation.aircraft.AircraftBuilder;
import com.chrisali.javaflightsim.simulation.aircraft.MassProperties;
import com.chrisali.javaflightsim.simulation.flightcontrols.FlightControlType;
import com.chrisali.javaflightsim.simulation.utilities.FileUtilities;
import com.chrisali.javaflightsim.simulation.utilities.SimDirectories;
import com.chrisali.javaflightsim.simulation.utilities.SimFiles;
import com.chrisali.javaflightsim.simulation.utilities.SixDOFUtilities;
import com.chrisali.javaflightsim.swing.optionspanel.AudioOptions;
import com.chrisali.javaflightsim.swing.optionspanel.DisplayOptions;

/**
 * Contains collections and methods used to configure the simulation and save/load 
 * configuration to/from external files
 */
public class SimulationConfiguration {
	
	//Logging
	private static final Logger logger = LogManager.getLogger(SimulationConfiguration.class);
	
	// Configuration
	private EnumMap<DisplayOptions, Integer> displayOptions;
	private EnumMap<AudioOptions, Float> audioOptions;
	private EnumSet<Options> simulationOptions;
	private EnumMap<InitialConditions, Double> initialConditions;
	private EnumMap<IntegratorConfig, Double> integratorConfig;
	private EnumMap<FlightControlType, Double> initialControls; 
	
	// Aircraft
	private AircraftBuilder ab;
	private EnumMap<MassProperties, Double> massProperties;

	/**
	 * Initializes initial settings, configurations and conditions
	 * to be edited through the menu options in the view
	 */
	public SimulationConfiguration() {
		simulationOptions = FileUtilities.parseSimulationSetup();
		displayOptions = FileUtilities.parseDisplaySetup();
		audioOptions = FileUtilities.parseAudioSetup();
		
		initialConditions = IntegrationSetup.gatherInitialConditions(null);
		integratorConfig = IntegrationSetup.gatherIntegratorConfig(null);
		initialControls = IntegrationSetup.gatherInitialControls(null);
		
		String aircraftName = FileUtilities.parseSimulationSetupForAircraft();
		ab = new AircraftBuilder(aircraftName);
	}
	
	/**
	 * @return simulationOptions EnumSet
	 */
	public EnumSet<Options> getSimulationOptions() {return simulationOptions;}
	
	/**
	 * @return displayOptions EnumMap
	 */
	public EnumMap<DisplayOptions, Integer> getDisplayOptions() {return displayOptions;}
	
	/**
	 * @return audioOptions EnumMap
	 */
	public EnumMap<AudioOptions, Float> getAudioOptions() {return audioOptions;}
	
	/**
	 * Updates simulation and display options and then saves the configurations to text files using either
	 * <p>{@link FileUtilities#writeConfigFile(String, String, Set, String)}</p>
	 * <br/>or
	 * <p>{@link FileUtilities#writeConfigFile(String, String, Map, String)}</p>
	 * 
	 * @param newOptions
	 * @param newDisplayOptions
	 * @param newAudioOptions
	 */
	public void updateOptions(EnumSet<Options> newOptions, EnumMap<DisplayOptions, Integer> newDisplayOptions,
							  EnumMap<AudioOptions, Float> newAudioOptions) {
		logger.debug("Updating simulation options...");
		
		try {
			simulationOptions = EnumSet.copyOf(newOptions);
			displayOptions = newDisplayOptions;
			audioOptions = newAudioOptions;
			
			FileUtilities.writeConfigFile(SimDirectories.SIM_CONFIG.toString(), SimFiles.SIMULATION_SETUP.toString(), simulationOptions, ab.getAircraft().getName());
			FileUtilities.writeConfigFile(SimDirectories.SIM_CONFIG.toString(), SimFiles.DISPLAY_SETUP.toString(), newDisplayOptions);
			FileUtilities.writeConfigFile(SimDirectories.SIM_CONFIG.toString(), SimFiles.AUDIO_SETUP.toString(), newAudioOptions);			
		} catch (Exception e) {
			logger.error("Error updating simulation options!", e);
		}
	}
	
	/**
	 * Calls the {@link AircraftBuilder} constructor with using the aircraftName argument and updates the SimulationSetup.txt
	 * configuration file with the new selected aircraft
	 * 
	 * @param aircraftName
	 */
	public void setAircraftBuilder(String aircraftName) {
		ab = new AircraftBuilder(aircraftName);
		FileUtilities.writeConfigFile(SimDirectories.SIM_CONFIG.toString(), SimFiles.SIMULATION_SETUP.toString(), simulationOptions, aircraftName);
	}
	
	/**
	 * Updates the MassProperties config file for the selected aircraft using aircraftName
	 * 
	 * @param aircraftName
	 * @param fuelWeight
	 * @param payloadWeight
	 */
	public void setMassProperties(String aircraftName, double fuelWeight, double payloadWeight) {
		logger.debug("Updating weights for " + aircraftName + "...");
		
		try {	
			massProperties = FileUtilities.parseMassProperties(aircraftName);
			
			massProperties.put(MassProperties.WEIGHT_FUEL, fuelWeight);
			massProperties.put(MassProperties.WEIGHT_PAYLOAD, payloadWeight);
			
			FileUtilities.writeConfigFile(SimDirectories.AIRCRAFT.toString() + File.pathSeparator + aircraftName, SimFiles.MASS_PROPERTIES.toString(), massProperties);
		} catch (Exception e) {
			logger.error("Error updating mass properties!", e);
		}
	}
	
	/**
	 * @return integratorConfig EnumMap
	 */
	public EnumMap<IntegratorConfig, Double> getIntegratorConfig() {return integratorConfig;}

	/**
	 * Updates the IntegratorConfig file with stepSize inverted and converted to a double  
	 * 
	 * @param stepSize
	 */
	public void setIntegratorConfig(int stepSize) {
		logger.debug("Updating simulation rate to " + stepSize + " Hz...");
		
		try {	
			integratorConfig.put(IntegratorConfig.DT, (1/((double)stepSize)));
			
			FileUtilities.writeConfigFile(SimDirectories.SIM_CONFIG.toString(), SimFiles.INTEGRATOR_CONFIG.toString(), integratorConfig);
		} catch (Exception e) {
			logger.error("Error updating integrator configuration!", e);
		}
	}

	/**
	 * @return initialControls EnumMap
	 */
	public EnumMap<FlightControlType, Double> getInitialControls() {return initialControls;}
	
	/**
	 * @return initialConditions EnumMap
	 */
	public EnumMap<InitialConditions, Double> getInitialConditions() {return initialConditions;}
	
	/**
	 * Updates initialConditions file with the following arguments, converted to radians and ft/sec:
	 * 
	 * @param coordinates [latitude, longitude]
	 * @param heading 
	 * @param altitude 
	 * @param airspeed
	 */
	public void setInitialConditions(double[] coordinates, double heading, double altitude, double airspeed) {
		logger.debug("Updating simulation intitial conditions...");
		
		try {	
			initialConditions.put(InitialConditions.INITLAT, Math.toRadians(coordinates[0]));
			initialConditions.put(InitialConditions.INITLON, Math.toRadians(coordinates[1]));
			initialConditions.put(InitialConditions.INITPSI, Math.toRadians(heading));
			initialConditions.put(InitialConditions.INITU,   SixDOFUtilities.toFtPerSec(airspeed));
			initialConditions.put(InitialConditions.INITD,   altitude);
			
			// Temporary method to calcuate north/east position from lat/lon position 
			initialConditions.put(InitialConditions.INITN, (Math.sin(Math.toRadians(coordinates[0])) * 20903520));
			initialConditions.put(InitialConditions.INITE, (Math.sin(Math.toRadians(coordinates[1])) * 20903520));
			
			FileUtilities.writeConfigFile(SimDirectories.SIM_CONFIG.toString(), SimFiles.INITIAL_CONDITIONS.toString(), initialConditions);
		} catch (Exception e) {
			logger.error("Error updating simulation initial conditions!", e);
		}
	}

	/**
	 * Updates the InitialControls config file
	 */
	public void setInitialControls() {
		FileUtilities.writeConfigFile(SimDirectories.SIM_CONFIG.toString(), SimFiles.INITIAL_CONTROLS.toString(), initialControls);
	}
	
	/**
	 * @return {@link AircraftBuilder} object
	 */
	public AircraftBuilder getAircraftBuilder() {return ab;}
	
	/**
	 * Allows {@link AircraftBuilder} to be changed to a different aircraft outside of being parsed in
	 * the SimulationSetup.txt configuration file
	 * 
	 * @param ab
	 */
	public void setAircraftBuilder(AircraftBuilder ab) {this.ab = ab;}
}
