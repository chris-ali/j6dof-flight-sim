package com.chrisali.javaflightsim.menus;

import java.util.EnumMap;
import java.util.EnumSet;

import com.chrisali.javaflightsim.instrumentpanel.flightdata.FlightData;
import com.chrisali.javaflightsim.simulation.aircraft.AircraftBuilder;
import com.chrisali.javaflightsim.simulation.aircraft.MassProperties;
import com.chrisali.javaflightsim.simulation.controls.FlightControls;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.setup.InitialConditions;
import com.chrisali.javaflightsim.simulation.setup.IntegrationSetup;
import com.chrisali.javaflightsim.simulation.setup.IntegratorConfig;
import com.chrisali.javaflightsim.simulation.setup.Options;
import com.chrisali.javaflightsim.utilities.Utilities;

public class Controller {
	
	private static final String SIM_CONFIG_PATH = ".\\SimConfig\\";
	private static final String AIRCRAFT_PATH = ".\\Aircraft\\";
	
	private Integrate6DOFEquations runSim;
	private FlightData flightData;
	private AircraftBuilder ab;
	
	private EnumMap<MassProperties, Double> massProperties;
	
	private EnumSet<Options> options;
	private EnumMap<InitialConditions, Double> initialConditions;
	private EnumMap<IntegratorConfig, Double> integratorConfig;
	private EnumMap<FlightControls, Double> initialControls; 
	
	public Controller() {
		options = EnumSet.noneOf(Options.class);
		
		initialConditions = IntegrationSetup.gatherInitialConditions("InitialConditions");
		integratorConfig = IntegrationSetup.gatherIntegratorConfig("IntegratorConfig");
		initialControls = IntegrationSetup.gatherInitialControls("InitialControls");
	}
	
	public void updateOptions(EnumSet<Options> newOptions) {options = EnumSet.copyOf(newOptions);}
	
	public void updateAircraft(String aircraftName) {ab = new AircraftBuilder(aircraftName);}
	
	public void updateMassProperties(String aircraftName, double fuelWeight, double payloadWeight) {
		massProperties.put(MassProperties.WEIGHT_FUEL, fuelWeight);
		massProperties.put(MassProperties.WEIGHT_PAYLOAD, payloadWeight);
		Utilities.writeConfigFile("MassProperties", AIRCRAFT_PATH + aircraftName + "\\", massProperties);
	}
	
	public void updateIntegratorConfig(int stepSize) {
		integratorConfig.put(IntegratorConfig.DT, (1/((double)stepSize)));
		Utilities.writeConfigFile("IntegratorConfig", SIM_CONFIG_PATH, integratorConfig);
	}
	
	public void updateInitialConditions() {
		Utilities.writeConfigFile("InitialConditions", SIM_CONFIG_PATH, initialConditions);
	}
	
	public void updateIninitialControls() {
		Utilities.writeConfigFile("InitialContols", SIM_CONFIG_PATH, initialControls);
	}	
	
	public void startSimulation() {
		runSim = new Integrate6DOFEquations(ab, options);
		new Thread(runSim).start();
		
		if (!options.contains(Options.ANALYSIS_MODE)) {
			flightData = new FlightData(runSim);
			new Thread(flightData).start();
		}
	}
}
