package com.chrisali.javaflightsim.simulation.utilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Test;

import com.chrisali.javaflightsim.simulation.aircraft.Aircraft;
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
		Aircraft aircraft = FileUtilities.readAircraftConfiguration(aircraftName);
		String assertion = "Property to serialize should not be null";
		
		assertNotNull(assertion, aircraft);
		assertEquals("Names should be equal", aircraftName, aircraft.getName());
		assertNotNull(assertion, aircraft.getMassProps());
		assertNotNull(assertion, aircraft.getWingGeometry());
		assertNotNull(assertion, aircraft.getStabDerivs());
		assertNotNull(assertion, aircraft.getGroundReaction());
		assertNotNull(assertion, aircraft.getEngines());
		
		for(Engine engine : aircraft.getEngines()) {
			assertNotNull(assertion, engine);
		}
		
		String filepath = FileUtilities.FILE_ROOT + SimDirectories.AIRCRAFT.toString() + File.separator + aircraftName;
		FileUtilities.serializeJson(filepath, aircraft.getClass().getSimpleName(), aircraft);
				
		Aircraft readAircraft = FileUtilities.readAircraftConfiguration(aircraftName);
		assertion = "Deserialized property should not be null";
		
		assertNotNull(assertion, readAircraft);
		assertEquals("Names should be equal", aircraftName, readAircraft.getName());
		assertNotNull(assertion, readAircraft.getMassProps());
		assertNotNull(assertion, readAircraft.getWingGeometry());
		assertNotNull(assertion, readAircraft.getStabDerivs());
		assertNotNull(assertion, readAircraft.getGroundReaction());
		assertNotNull(assertion, readAircraft.getEngines());
		
		for(Engine engine : readAircraft.getEngines()) {
			assertNotNull(assertion, engine);
		}
	}
}
