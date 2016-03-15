package com.chrisali.javaflightsim.menus;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;

import com.chrisali.javaflightsim.instrumentpanel.InstrumentPanel;
import com.chrisali.javaflightsim.instrumentpanel.flightdata.FlightData;
import com.chrisali.javaflightsim.plotting.PlotWindow;
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
	private Thread simulationThread;
	private Thread flightDataThread;
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
	
	public EnumSet<Options> getOptions() {return options;}
	
	public void updateOptions(EnumSet<Options> newOptions) {options = EnumSet.copyOf(newOptions);}
	
	public void updateAircraft(String aircraftName) {ab = new AircraftBuilder(aircraftName);}
	
	public void updateMassProperties(String aircraftName, double fuelWeight, double payloadWeight) {
		massProperties = Utilities.parseMassProperties(aircraftName);
		
		massProperties.put(MassProperties.WEIGHT_FUEL, fuelWeight);
		massProperties.put(MassProperties.WEIGHT_PAYLOAD, payloadWeight);
		
		Utilities.writeConfigFile("MassProperties", AIRCRAFT_PATH + aircraftName + "\\", massProperties);
	}
	
	public void updateIntegratorConfig(int stepSize) {
		integratorConfig.put(IntegratorConfig.DT, (1/((double)stepSize)));
		
		Utilities.writeConfigFile("IntegratorConfig", SIM_CONFIG_PATH, integratorConfig);
	}
	
	public void updateInitialConditions(double[] coordinates, double heading, double altitude, double airspeed) {
		initialConditions.put(InitialConditions.INITLAT, Math.toRadians(coordinates[0]));
		initialConditions.put(InitialConditions.INITLON, Math.toRadians(coordinates[1]));
		initialConditions.put(InitialConditions.INITPSI, Math.toRadians(heading));
		initialConditions.put(InitialConditions.INITU,   Utilities.toFtPerSec(airspeed));
		initialConditions.put(InitialConditions.INITD,   altitude);
		
		Utilities.writeConfigFile("InitialConditions", SIM_CONFIG_PATH, initialConditions);
	}
	
	public void updateIninitialControls() {
		Utilities.writeConfigFile("InitialContols", SIM_CONFIG_PATH, initialControls);
	}	
	
	public void startSimulation(InstrumentPanel panel) {
		runSim = new Integrate6DOFEquations(ab, options);
		
		simulationThread = new Thread(runSim);
		simulationThread.start();
		
		if (options.contains(Options.ANALYSIS_MODE)) {
			plotSimulation();
		} else {
			flightData = new FlightData(runSim);
			flightData.setFlightDataListener(panel);
			
			flightDataThread = new Thread(flightData);
			flightDataThread.start();
		}
		
	}
	
	public void stopSimulation() {
		if (runSim != null && runSim.isRunning() && simulationThread != null && simulationThread.isAlive())
			simulationThread.interrupt();
		if (flightDataThread != null && flightDataThread.isAlive())
			flightDataThread.interrupt();
	}
	
	public void plotSimulation() {
		new PlotWindow(runSim.getLogsOut(), 
	 				   new HashSet<String>(Arrays.asList("Controls", "Instruments", "Position", "Rates", "Miscellaneous")),
	 				   ab.getAircraft());
	}
}
