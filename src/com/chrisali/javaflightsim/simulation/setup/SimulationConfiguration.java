/**
 * 
 */
package com.chrisali.javaflightsim.simulation.setup;

import java.io.File;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import com.chrisali.javaflightsim.menus.optionspanel.AudioOptions;
import com.chrisali.javaflightsim.menus.optionspanel.DisplayOptions;
import com.chrisali.javaflightsim.simulation.aircraft.AircraftBuilder;
import com.chrisali.javaflightsim.simulation.aircraft.MassProperties;
import com.chrisali.javaflightsim.simulation.controls.FlightControlType;
import com.chrisali.javaflightsim.utilities.FileUtilities;
import com.chrisali.javaflightsim.utilities.SixDOFUtilities;

/**
 * Contains collections and methods used to configure the simulation and save/load 
 * configuration to/from external files
 */
public class SimulationConfiguration {
	
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
		simulationOptions = EnumSet.copyOf(newOptions);
		displayOptions = newDisplayOptions;
		audioOptions = newAudioOptions;
		
		FileUtilities.writeConfigFile(FileUtilities.SIM_CONFIG_DIR, FileUtilities.SIMULATION_SETUP_FILE, simulationOptions, ab.getAircraft().getName());
		FileUtilities.writeConfigFile(FileUtilities.SIM_CONFIG_DIR, FileUtilities.DISPLAY_SETUP_FILE, newDisplayOptions);
		FileUtilities.writeConfigFile(FileUtilities.SIM_CONFIG_DIR, FileUtilities.AUDIO_SETUP_FILE, newAudioOptions);
	}
	
	/**
	 * Calls the {@link AircraftBuilder} constructor with using the aircraftName argument and updates the SimulationSetup.txt
	 * configuration file with the new selected aircraft
	 * 
	 * @param aircraftName
	 */
	public void setAircraftBuilder(String aircraftName) {
		ab = new AircraftBuilder(aircraftName);
		FileUtilities.writeConfigFile(FileUtilities.SIM_CONFIG_DIR, FileUtilities.SIMULATION_SETUP_FILE, simulationOptions, aircraftName);
	}
	
	/**
	 * Updates the MassProperties config file for the selected aircraft using aircraftName
	 * 
	 * @param aircraftName
	 * @param fuelWeight
	 * @param payloadWeight
	 */
	public void setMassProperties(String aircraftName, double fuelWeight, double payloadWeight) {
		massProperties = FileUtilities.parseMassProperties(aircraftName);
		
		massProperties.put(MassProperties.WEIGHT_FUEL, fuelWeight);
		massProperties.put(MassProperties.WEIGHT_PAYLOAD, payloadWeight);
		
		FileUtilities.writeConfigFile(FileUtilities.AIRCRAFT_DIR + File.pathSeparator + aircraftName, FileUtilities.MASS_PROPERTIES_FILE, massProperties);
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
		integratorConfig.put(IntegratorConfig.DT, (1/((double)stepSize)));
		
		FileUtilities.writeConfigFile(FileUtilities.SIM_CONFIG_DIR, FileUtilities.INTEGRATOR_CONFIG_FILE, integratorConfig);
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
		initialConditions.put(InitialConditions.INITLAT, Math.toRadians(coordinates[0]));
		initialConditions.put(InitialConditions.INITLON, Math.toRadians(coordinates[1]));
		initialConditions.put(InitialConditions.INITPSI, Math.toRadians(heading));
		initialConditions.put(InitialConditions.INITU,   SixDOFUtilities.toFtPerSec(airspeed));
		initialConditions.put(InitialConditions.INITD,   altitude);
		
		// Temporary method to calcuate north/east position from lat/lon position 
		initialConditions.put(InitialConditions.INITN, (Math.sin(Math.toRadians(coordinates[0])) * 20903520));
		initialConditions.put(InitialConditions.INITE, (Math.sin(Math.toRadians(coordinates[1])) * 20903520));
		
		FileUtilities.writeConfigFile(FileUtilities.SIM_CONFIG_DIR, FileUtilities.INITIAL_CONDITIONS_FILE, initialConditions);
	}

	/**
	 * Updates the InitialControls config file
	 */
	public void setIninitialControls() {
		FileUtilities.writeConfigFile(FileUtilities.SIM_CONFIG_DIR, FileUtilities.INITIAL_CONTROLS_FILE, initialControls);
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
