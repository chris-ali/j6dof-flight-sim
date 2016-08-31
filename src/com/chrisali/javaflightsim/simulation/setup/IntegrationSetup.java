package com.chrisali.javaflightsim.simulation.setup;

import java.util.ArrayList;
import java.util.EnumMap;

import com.chrisali.javaflightsim.controllers.SimulationController;
import com.chrisali.javaflightsim.simulation.controls.FlightControlType;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.utilities.FileUtilities;

/**
 * Class containing methods to parse setup files to generate EnumMaps used as initial conditions,
 * initial controls, and configuration for {@link Integrate6DOFEquations}
 */
public class IntegrationSetup {
	
	/**
	 * Parses a text file formatted as described in {@link IntegrationSetup#readFileAndSplit(String)} to generate an 
	 * EnumMap of initial conditions used by {@link Integrate6DOFEquations} to start the integration for the simulation. 
	 * 
	 * @param fileName
	 * @return EnumMap of initial conditions for the integration
	 */
	public static EnumMap<InitialConditions, Double> gatherInitialConditions(String fileName) {
		ArrayList<String[]> initConditionsFile = FileUtilities.readFileAndSplit(fileName, SimulationController.getSimConfigPath());
		EnumMap<InitialConditions,Double> initialConditions = new EnumMap<InitialConditions,Double>(InitialConditions.class); 
				
		if (!verifyICFileIntegrity(initConditionsFile)) {
			System.err.println("Error in initial conditions file! Generating default initial conditions...");
			Double[] defaultIC = new Double[] {210.0, 0.0, -3.99, 0.0, 0.0, 5000.0, 0.0, -0.025, 1.57, 0.0, 0.0, 0.0};
			for (int i = 0; i < defaultIC.length; i++)
				initialConditions.put(InitialConditions.values()[i], defaultIC[i]);
			return initialConditions;
		} else {
			for (int i = 0; i < initConditionsFile.size(); i++)
				initialConditions.put(InitialConditions.values()[i], Double.parseDouble(initConditionsFile.get(i)[1]));
			return initialConditions;
		}
	}
	
	/**
	 * Parses a text file formatted as described in {@link IntegrationSetup#readFileAndSplit(String)} to generate an 
	 * EnumMap of settings used by {@link Integrate6DOFEquations} to control the start, step and end times for the simulation. 
	 * 
	 * @param fileName
	 * @return EnumMap of integration configuration options
	 */
	public static EnumMap<IntegratorConfig, Double> gatherIntegratorConfig(String fileName) {
		ArrayList<String[]> intConfigFile = FileUtilities.readFileAndSplit(fileName, SimulationController.getSimConfigPath());
		EnumMap<IntegratorConfig,Double> integratorConfig = new EnumMap<IntegratorConfig,Double>(IntegratorConfig.class); 
				
		if (!verifyIntConfigFileIntegrity(intConfigFile)) {
			System.err.println("Error in integration configuration file! Generating default integration configuration...");
			double[] defaultIntConfig = new double[] {0.0, 0.05, 100.0};
			for (int i = 0; i < defaultIntConfig.length; i++)
				integratorConfig.put(IntegratorConfig.values()[i], defaultIntConfig[i]);
			return integratorConfig;
		} else {
			for (int i = 0; i < intConfigFile.size(); i++)
				integratorConfig.put(IntegratorConfig.values()[i], Double.parseDouble(intConfigFile.get(i)[1]));
			return integratorConfig;
		}
	}
	
	/**
	 * Parses a text file formatted as described in {@link IntegrationSetup#readFileAndSplit(String)} to generate an 
	 * EnumMap of initial controls used by {@link Integrate6DOFEquations} to start the integration for the simulation. 
	 * 
	 * @param fileName
	 * @return EnumMap of initial controls for the integration
	 */
	public static EnumMap<FlightControlType, Double> gatherInitialControls(String fileName) {
		ArrayList<String[]> initControlFile = FileUtilities.readFileAndSplit(fileName, SimulationController.getSimConfigPath());
		EnumMap<FlightControlType,Double> initControl = new EnumMap<FlightControlType,Double>(FlightControlType.class); 
		
		if (!verifyControlFileIntegrity(initControlFile)) {
			System.err.println("Error in controls file! Generating default control deflections...");
			double[] defaultControl = new double[] {0.036, 0, 0, 0.65, 0.65, 0.65, 0.65, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0, 0, 0, 0};
			for (int i = 0; i < defaultControl.length; i++)
				initControl.put(FlightControlType.values()[i], defaultControl[i]);
			return initControl;
		} else {
			for (int i = 0; i < initControlFile.size(); i++)
				initControl.put(FlightControlType.values()[i], Double.parseDouble(initControlFile.get(i)[1]));
			return initControl;
		}
	}
	
	/**
	 * Checks parsed InitialControls text file to ensure that read file length and content 
	 * match {@link FlightControlType} enum length and key content
	 * @param initControlFile
	 * @return
	 */
	private static boolean verifyControlFileIntegrity(ArrayList<String[]> initControlFile) {
		// If lengths are not equal, don't bother checking integrity; return false
		if (FlightControlType.values().length == initControlFile.size()) {
			// Compare enum string value with read string from file
			for (int i = 0; i < FlightControlType.values().length; i++) {
				if (!initControlFile.get(i)[0].equals(FlightControlType.values()[i].toString()))
					return false;
			}
		}
		else {return false;}
		
		return true;
	}
	
	/**
	 * Checks parsed InitialConditions text file to ensure that read file length and content 
	 * match {@link InitialConditions} enum length and key content
	 * @param initConditionsFile
	 * @return
	 */
	private static boolean verifyICFileIntegrity(ArrayList<String[]> initConditionsFile) {
		// If lengths are not equal, don't bother checking integrity; return false
		if (InitialConditions.values().length == initConditionsFile.size()) {
			// Compare enum string value with read string from file
			for (int i = 0; i < InitialConditions.values().length; i++) {
				if (!initConditionsFile.get(i)[0].equals(InitialConditions.values()[i].toString()))
					return false;
			}
		}
		else {return false;}
		
		return true;
	}
	
	/**
	 * Checks parsed IntegratorConfig text file to ensure that read file length and content 
	 * match {@link IntegratorConfig} enum length and key content
	 * @param intConfigFile
	 * @return
	 */
	private static boolean verifyIntConfigFileIntegrity(ArrayList<String[]> intConfigFile) {
		// If lengths are not equal, don't bother checking integrity; return false
		if (IntegratorConfig.values().length == intConfigFile.size()) {
			// Compare enum string value with read string from file
			for (int i = 0; i < IntegratorConfig.values().length; i++) {
				if (!intConfigFile.get(i)[0].equals(IntegratorConfig.values()[i].toString()))
					return false;
			}
		}
		else {return false;}
		
		return true;
	}
}
