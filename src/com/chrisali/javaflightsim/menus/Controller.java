package com.chrisali.javaflightsim.menus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.chrisali.javaflightsim.instrumentpanel.InstrumentPanel;
import com.chrisali.javaflightsim.instrumentpanel.flightdata.FlightData;
import com.chrisali.javaflightsim.plotting.PlotWindow;
import com.chrisali.javaflightsim.simulation.aircraft.AircraftBuilder;
import com.chrisali.javaflightsim.simulation.aircraft.MassProperties;
import com.chrisali.javaflightsim.simulation.controls.FlightControls;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.integration.SimOuts;
import com.chrisali.javaflightsim.simulation.setup.InitialConditions;
import com.chrisali.javaflightsim.simulation.setup.IntegrationSetup;
import com.chrisali.javaflightsim.simulation.setup.IntegratorConfig;
import com.chrisali.javaflightsim.simulation.setup.Options;
import com.chrisali.javaflightsim.utilities.Utilities;

public class Controller {
	
	private static final String SIM_CONFIG_PATH = ".\\SimConfig\\";
	private static final String AIRCRAFT_PATH = ".\\Aircraft\\";
	
	private EnumSet<Options> options;
	private EnumMap<InitialConditions, Double> initialConditions;
	private EnumMap<IntegratorConfig, Double> integratorConfig;
	private EnumMap<FlightControls, Double> initialControls; 
	
	private Integrate6DOFEquations runSim;
	private Thread simulationThread;
	private Thread flightDataThread;
	private FlightData flightData;
	
	private AircraftBuilder ab;
	private EnumMap<MassProperties, Double> massProperties;
	
	private PlotWindow plotWindow;
	private Set<String> plotCategories = new HashSet<>(Arrays.asList("Controls", "Instruments", "Position", "Rates", "Miscellaneous"));
	
	private ConsoleTablePanel consoleTablePanel;
	
	public Controller() {
		options = EnumSet.noneOf(Options.class);
		
		initialConditions = IntegrationSetup.gatherInitialConditions("InitialConditions");
		integratorConfig = IntegrationSetup.gatherIntegratorConfig("IntegratorConfig");
		initialControls = IntegrationSetup.gatherInitialControls("InitialControls");
	}
	
	//=============================== Configuration ===========================================================
	
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
	
	//=============================== Simulation ===========================================================
	
	public boolean simulationIsRunning() {return runSim.isRunning();}
	
	public AircraftBuilder getAircraftBuilder() {return ab;}
	
	public List<EnumMap<SimOuts, Double>> getLogsOut() {return runSim.getLogsOut();}
	
	public void startSimulation(InstrumentPanel panel) {
		runSim = new Integrate6DOFEquations(ab, options);
		
		simulationThread = new Thread(runSim);
		simulationThread.start();
		
		if (options.contains(Options.CONSOLE_DISPLAY))
			updateConsole();
		
		if (options.contains(Options.ANALYSIS_MODE)) {
			plotSimulation();
		} else {
			flightData = new FlightData(runSim);
			flightData.setFlightDataListener(panel);
			
			flightDataThread = new Thread(flightData);
			flightDataThread.start();
			
			consoleTablePanel.setVisible(true);
		}
	}
	
	public void stopSimulation() {
		if (runSim != null && runSim.isRunning() && simulationThread != null && simulationThread.isAlive())
			simulationThread.interrupt();
		if (flightDataThread != null && flightDataThread.isAlive())
			flightDataThread.interrupt();
		if (consoleTablePanel.isVisible())
			consoleTablePanel.setVisible(false);
	}
	
	//=============================== Plotting =============================================================
	
	public void plotSimulation() {
		if(plotWindow == null)
			plotWindow = new PlotWindow(runSim.getLogsOut(), plotCategories, ab.getAircraft());
		else
			plotWindow.refreshPlots(new ArrayList<EnumMap<SimOuts, Double>>(runSim.getLogsOut()));
	}
	
	public boolean isPlotWindowVisible() {
		if (plotWindow == null) return false;
		else return plotWindow.isVisible();
	}
	
	//=============================== Console =============================================================
	
	public void updateConsole() {
		if (consoleTablePanel == null)
			consoleTablePanel = new ConsoleTablePanel(runSim.getLogsOut(), this);
		else
			consoleTablePanel.refresh();
	}
	
	public void saveConsoleOutput(File file) throws IOException {
		Utilities.saveToCSVFile(file, runSim.getLogsOut());
	}
}
