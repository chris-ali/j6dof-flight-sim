package com.chrisali.javaflightsim.tests;

import java.io.File;
import java.util.Map;

import com.chrisali.javaflightsim.simulation.setup.IntegrationSetup;
import com.chrisali.javaflightsim.simulation.setup.IntegratorConfig;
import com.chrisali.javaflightsim.utilities.Utilities;

public class WriteFileTest {
	
	private static final String FILE_PATH = ".\\SimConfig\\";
	private static final String OLD_CONFIG_FILE_NAME = "IntegratorConfig";
	private static final String NEW_CONFIG_FILE_NAME = "NewIntegratorConfig";
	
	public static void main(String[] args) {
		Map<IntegratorConfig, Double> simConfig = IntegrationSetup.gatherIntegratorConfig(OLD_CONFIG_FILE_NAME);
		
		Utilities.writeConfigFile(NEW_CONFIG_FILE_NAME, FILE_PATH, simConfig);
		
		Map<IntegratorConfig, Double> newSimConfig = IntegrationSetup.gatherIntegratorConfig(NEW_CONFIG_FILE_NAME);
		
		if (simConfig.equals(newSimConfig))
			System.out.println("Parsed Maps are identical");
		else
			System.err.println("Parsed Maps are different!");
		
		new File(FILE_PATH + NEW_CONFIG_FILE_NAME + ".txt").deleteOnExit();
	}

}
