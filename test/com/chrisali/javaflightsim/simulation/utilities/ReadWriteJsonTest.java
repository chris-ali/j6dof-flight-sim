package com.chrisali.javaflightsim.simulation.utilities;

import org.junit.Test;
import static org.junit.Assert.*;

import com.chrisali.javaflightsim.simulation.setup.SimulationConfiguration;

public class ReadWriteJsonTest {

	@Test
	public void WriteThenReadJsonConfigTest() {
		SimulationConfiguration configuration = new SimulationConfiguration();
		String assertion = "Property to serialize should not be null"; 
		
		assertNotNull(assertion, configuration.getAudioOptions());
		assertNotNull(assertion, configuration.getDisplayOptions());
		assertNotNull(assertion, configuration.getInitialConditions());
		assertNotNull(assertion, configuration.getIntegratorConfig());
		assertNotNull(assertion, configuration.getInitialControls());
		
		FileUtilities.writeConfigFile(SimDirectories.SIM_CONFIG.toString(), configuration);
		
		SimulationConfiguration newConfiguration = FileUtilities.readSimulationConfiguration();
		assertion = "Deserialized property should not be null";
		
		assertNotNull(assertion, newConfiguration.getAudioOptions());
		assertNotNull(assertion, newConfiguration.getDisplayOptions());
		assertNotNull(assertion, newConfiguration.getInitialConditions());
		assertNotNull(assertion, newConfiguration.getIntegratorConfig());
		assertNotNull(assertion, newConfiguration.getInitialControls());
	}
}
