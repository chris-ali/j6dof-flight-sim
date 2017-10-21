package com.chrisali.javaflightsim.simulation.utilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import com.chrisali.javaflightsim.simulation.aircraft.Aircraft;
import com.chrisali.javaflightsim.simulation.integration.SimOuts;
import com.chrisali.javaflightsim.simulation.propulsion.Engine;
import com.chrisali.javaflightsim.simulation.setup.SimulationConfiguration;
import com.chrisali.javaflightsim.swing.plotting.PlotConfiguration;
import com.chrisali.javaflightsim.swing.plotting.PlotConfiguration.SubPlotBundle;
import com.chrisali.javaflightsim.swing.plotting.PlotConfiguration.SubPlotOptions;

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
		
		aircraft.save();
				
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
	
	@Test
	public void WriteThenReadPlotConfigurationTest() {
		PlotConfiguration plots = FileUtilities.readPlotConfiguration();
		String assertion = "Deserialized property should not be null";
		
		assertNotNull(assertion, plots);
		assertNotNull(assertion, plots.getSubPlotBundles());
		assertTrue("There should be at least one bundle in configuration", plots.getSubPlotBundles().size() > 0);
		
		for(Map.Entry<String, SubPlotBundle> entry : plots.getSubPlotBundles().entrySet()) {
			SubPlotBundle bundle = entry.getValue();
			assertNotNull(assertion, bundle);
			
			assertNotNull(assertion, bundle.getSizeXPixels());
			assertNotNull(assertion, bundle.getSizeYPixels());
			assertNotNull(assertion, bundle.getTitle());
			assertNotNull(assertion, bundle.getSubPlots());
			
			assertTrue("There should be at least one subplot in this bundle", bundle.getSubPlots().size() > 0);
			
			for(SubPlotOptions subplot : bundle.getSubPlots()) {
				assertNotNull(assertion, subplot);
				assertNotNull(assertion, subplot.getTitle());
				assertNotNull(assertion, subplot.getxAxisName());
				assertNotNull(assertion, subplot.getyAxisName());
				assertNotNull(assertion, subplot.getxData());
				assertNotNull(assertion, subplot.getyData());
				
				assertTrue("There should be at least one y data in this bundle", subplot.getyData().size() > 0);
				
				for(SimOuts simout : subplot.getyData()) {
					assertNotNull(assertion, simout);
				}
			}
		}
		
		plots.save();
		
		PlotConfiguration readPlots = FileUtilities.readPlotConfiguration();
		assertion = "Deserialized property should not be null";
		
		assertNotNull(assertion, readPlots);
		assertNotNull(assertion, readPlots.getSubPlotBundles());
		assertTrue("There should be at least one bundle in configuration", readPlots.getSubPlotBundles().size() > 0);
		
		
		for(Map.Entry<String, SubPlotBundle> entry : readPlots.getSubPlotBundles().entrySet()) {
			SubPlotBundle bundle = entry.getValue();
			assertNotNull(assertion, bundle);
			
			assertNotNull(assertion, bundle.getSizeXPixels());
			assertNotNull(assertion, bundle.getSizeYPixels());
			assertNotNull(assertion, bundle.getTitle());
			assertNotNull(assertion, bundle.getSubPlots());
			
			assertTrue("There should be at least one subplot in this bundle", bundle.getSubPlots().size() > 0);
			
			for(SubPlotOptions subplot : bundle.getSubPlots()) {
				assertNotNull(assertion, subplot);
				assertNotNull(assertion, subplot.getTitle());
				assertNotNull(assertion, subplot.getxAxisName());
				assertNotNull(assertion, subplot.getyAxisName());
				assertNotNull(assertion, subplot.getxData());
				assertNotNull(assertion, subplot.getyData());
				
				assertTrue("There should be at least one y data in this bundle", subplot.getyData().size() > 0);
				
				for(SimOuts simout : subplot.getyData()) {
					assertNotNull(assertion, simout);
				}
			}
		}
	}
}
