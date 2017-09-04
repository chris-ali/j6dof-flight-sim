package com.chrisali.javaflightsim.simulation.utilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Test;

import com.chrisali.javaflightsim.simulation.aircraft.AircraftBuilder;
import com.chrisali.javaflightsim.simulation.propulsion.Engine;
import com.chrisali.javaflightsim.simulation.setup.SimulationConfiguration;

public class ReadWriteJsonTest {

	@Test
	public void WriteThenReadJsonConfigTest() {
		SimulationConfiguration configuration = FileUtilities.readSimulationConfiguration();
		String assertion = "Property to serialize should not be null"; 
		
		assertNotNull(assertion, configuration.getAudioOptions());
		assertNotNull(assertion, configuration.getDisplayOptions());
		assertNotNull(assertion, configuration.getInitialConditions());
		assertNotNull(assertion, configuration.getIntegratorConfig());
		assertNotNull(assertion, configuration.getInitialControls());
		
		configuration.save();
		
		SimulationConfiguration newConfiguration = FileUtilities.readSimulationConfiguration();
		assertion = "Deserialized property should not be null";
		
		assertNotNull(assertion, newConfiguration.getAudioOptions());
		assertNotNull(assertion, newConfiguration.getDisplayOptions());
		assertNotNull(assertion, newConfiguration.getInitialConditions());
		assertNotNull(assertion, newConfiguration.getIntegratorConfig());
		assertNotNull(assertion, newConfiguration.getInitialControls());
	}
	
	@Test
	public void WriteThenReadJsonAircraftTest() {
		String aircraftName = "TwinNavion";
		AircraftBuilder ab = FileUtilities.readAircraftConfiguration(aircraftName); //new AircraftBuilder(aircraftName); //
		String assertion = "Property to serialize should not be null";
		
		assertNotNull(assertion, ab);
		assertNotNull(assertion, ab.getAircraft());
		assertEquals("Names should be equal", aircraftName, ab.getAircraft().getName());
		assertNotNull(assertion, ab.getAircraft().getMassProps());
		assertNotNull(assertion, ab.getAircraft().getWingGeometry());
		assertNotNull(assertion, ab.getAircraft().getStabDerivs());
		assertNotNull(assertion, ab.getAircraft().getGroundReaction());
		assertNotNull(assertion, ab.getEngineList());
		
		for(Engine engine : ab.getEngineList()) {
			assertNotNull(assertion, engine);
		}
		
		String filepath = FileUtilities.FILE_ROOT + SimDirectories.AIRCRAFT.toString() + File.separator + aircraftName;
		FileUtilities.serializeJson(filepath, ab.getClass().getSimpleName(), ab);
				
		AircraftBuilder readAb = FileUtilities.readAircraftConfiguration(aircraftName);
		assertion = "Deserialized property should not be null";
		
		assertNotNull(assertion, readAb);
		assertNotNull(assertion, readAb.getAircraft());
		assertEquals("Names should be equal", aircraftName, readAb.getAircraft().getName());
		assertNotNull(assertion, readAb.getAircraft().getMassProps());
		assertNotNull(assertion, readAb.getAircraft().getWingGeometry());
		assertNotNull(assertion, readAb.getAircraft().getStabDerivs());
		assertNotNull(assertion, readAb.getAircraft().getGroundReaction());
		assertNotNull(assertion, readAb.getEngineList());
		
		for(Engine engine : readAb.getEngineList()) {
			assertNotNull(assertion, engine);
		}
	}
}
