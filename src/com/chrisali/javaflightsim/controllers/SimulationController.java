package com.chrisali.javaflightsim.controllers;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.chrisali.javaflightsim.consoletable.ConsoleTablePanel;
import com.chrisali.javaflightsim.datatransfer.EnvironmentData;
import com.chrisali.javaflightsim.datatransfer.FlightData;
import com.chrisali.javaflightsim.instrumentpanel.InstrumentPanel;
import com.chrisali.javaflightsim.menus.optionspanel.DisplayOptions;
import com.chrisali.javaflightsim.otw.RunWorld;
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

/**
 * Controls the configuration and running of processes supporting the simulation component of JavaFlightSim. This consists of: 
 * <p>The simulation engine that integrates the 6DOF equations ({@link Integrate6DOFEquations})</p>
 * <p>Plotting of the simulation states and data ({@link PlotWindow})</p>
 * <p>Raw data display of simulation states ({@link ConsoleTablePanel})</p>
 * <p>Transmission of flight data to the instrument panel and out the window display ({@link FlightData})</p>
 * <p>Transmission of environment data to the simulation ({@link EnvironmentData})</p>
 * 
 * @author Christopher Ali
 *
 */
public class SimulationController {
	
	// Paths
	private static final String SIM_CONFIG_PATH = ".\\SimConfig\\";
	private static final String AIRCRAFT_PATH = ".\\Aircraft\\";
	
	// Configuration
	private EnumMap<DisplayOptions, Integer> displayOptions;
	private EnumSet<Options> simulationOptions;
	private EnumMap<InitialConditions, Double> initialConditions;
	private EnumMap<IntegratorConfig, Double> integratorConfig;
	private EnumMap<FlightControls, Double> initialControls; 
	
	// Simulation
	private Integrate6DOFEquations runSim;
	private Thread simulationThread;
	private Thread flightDataThread;
	private FlightData flightData;
	
	// Aircraft
	private AircraftBuilder ab;
	private EnumMap<MassProperties, Double> massProperties;
	
	// Plotting
	private PlotWindow plotWindow;
	private Set<String> plotCategories = new HashSet<>(Arrays.asList("Controls", "Instruments", "Position", "Rates", "Miscellaneous"));
	
	// Raw Data Console
	private ConsoleTablePanel consoleTablePanel;
	
	// Out the Window
	private RunWorld outTheWindow;
	private Thread outTheWindowThread;
	private Thread environmentDataThread;
	private EnvironmentData environmentData;
	
	/**
	 * Constructor for the controller that initializes initial settings, configurations and conditions
	 * to be edited through the menu options in the view
	 */
	public SimulationController() {
		simulationOptions = EnumSet.noneOf(Options.class);
		displayOptions = new EnumMap<DisplayOptions, Integer>(DisplayOptions.class);
		
		initialConditions = IntegrationSetup.gatherInitialConditions("InitialConditions");
		integratorConfig = IntegrationSetup.gatherIntegratorConfig("IntegratorConfig");
		initialControls = IntegrationSetup.gatherInitialControls("InitialControls");
	}
	
	//=============================== Configuration ===========================================================
	
	/**
	 * @return simulationOptions EnumSet
	 */
	public EnumSet<Options> getSimulationOptions() {return simulationOptions;}
	
	/**
	 * @return displayOptions EnumMap
	 */
	public EnumMap<DisplayOptions, Integer> getDisplayOptions() {return displayOptions;}
	
	/**
	 * Updates simulation and display options and then saves the configurations to text files using either
	 * <p>{@link Utilities#writeConfigFile(String, String, Set, String)}</p>
	 * <br/>or
	 * <p>{@link Utilities#writeConfigFile(String, String, Set, String)}</p>
	 * 
	 * @param newOptions
	 * @param newDisplayOptions
	 */
	public void updateOptions(EnumSet<Options> newOptions, EnumMap<DisplayOptions, Integer> newDisplayOptions) {
		simulationOptions = EnumSet.copyOf(newOptions);
		displayOptions = newDisplayOptions;
		
		if (ab != null)
			Utilities.writeConfigFile(SIM_CONFIG_PATH, "SimulationSetup", simulationOptions, ab.getAircraft().getName());
		Utilities.writeConfigFile(SIM_CONFIG_PATH, "DisplaySetup", newDisplayOptions);
	}
	
	/**
	 * Calls the {@link AircraftBuilder} constructor with using the aircraftName argument
	 * 
	 * @param aircraftName
	 */
	public void updateAircraft(String aircraftName) {ab = new AircraftBuilder(aircraftName);}
	
	/**
	 * Updates the MassProperties config file for the selected aircraft using aircraftName
	 * 
	 * @param aircraftName
	 * @param fuelWeight
	 * @param payloadWeight
	 */
	public void updateMassProperties(String aircraftName, double fuelWeight, double payloadWeight) {
		massProperties = Utilities.parseMassProperties(aircraftName);
		
		massProperties.put(MassProperties.WEIGHT_FUEL, fuelWeight);
		massProperties.put(MassProperties.WEIGHT_PAYLOAD, payloadWeight);
		
		Utilities.writeConfigFile("MassProperties", AIRCRAFT_PATH + aircraftName + "\\", massProperties);
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
	public void updateIntegratorConfig(int stepSize) {
		integratorConfig.put(IntegratorConfig.DT, (1/((double)stepSize)));
		
		Utilities.writeConfigFile(SIM_CONFIG_PATH, "IntegratorConfig", integratorConfig);
	}
	
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
	public void updateInitialConditions(double[] coordinates, double heading, double altitude, double airspeed) {
		initialConditions.put(InitialConditions.INITLAT, Math.toRadians(coordinates[0]));
		initialConditions.put(InitialConditions.INITLON, Math.toRadians(coordinates[1]));
		initialConditions.put(InitialConditions.INITPSI, Math.toRadians(heading));
		initialConditions.put(InitialConditions.INITU,   Utilities.toFtPerSec(airspeed));
		initialConditions.put(InitialConditions.INITD,   altitude);
		
		Utilities.writeConfigFile(SIM_CONFIG_PATH, "InitialConditions", initialConditions);
	}
	
	/**
	 * Updates the InitialControls config file
	 */
	public void updateIninitialControls() {
		Utilities.writeConfigFile(SIM_CONFIG_PATH, "InitialControls", initialControls);
	}	
	
	//=============================== Simulation ===========================================================
	
	/**
	 * @return instance of simulation
	 */
	public Integrate6DOFEquations getSimulation() {return runSim;}
	
	/**
	 * @return {@link AircraftBuilder} object
	 */
	public AircraftBuilder getAircraftBuilder() {return ab;}
	
	/**
	 * @return ArrayList of simulation output data 
	 * @see SimOuts
	 */
	public List<Map<SimOuts, Double>> getLogsOut() {return runSim.getLogsOut();}
	
	/**
	 * Initializes and starts the simulation (and flight and environment data, if selected) threads.
	 * Depending on options specified, a console panel, plot window, instrument panel
	 * and out the window display window will also be initialized and opened 
	 * 
	 * @param panel
	 */
	public void startSimulation(InstrumentPanel panel) {
		runSim = new Integrate6DOFEquations(ab, simulationOptions);
		
		simulationThread = new Thread(runSim);
		simulationThread.start();
		
		if (simulationOptions.contains(Options.CONSOLE_DISPLAY))
			initializeConsole();
		if (simulationOptions.contains(Options.ANALYSIS_MODE)) {
			plotSimulation();
		} else {
			outTheWindow = new RunWorld(displayOptions);
			
			environmentData = new EnvironmentData(outTheWindow);
			environmentData.addEnvironmentDataListener(runSim);
			
			environmentDataThread = new Thread(environmentData);
			environmentDataThread.start();
			
			flightData = new FlightData(runSim);
			flightData.addFlightDataListener(panel);
			flightData.addFlightDataListener(outTheWindow);
			
			flightDataThread = new Thread(flightData);
			flightDataThread.start();
			
			outTheWindowThread = new Thread(outTheWindow);
			outTheWindowThread.start();
		}
	}
	
	/**
	 * Stops simulation and flight data (if running) threads, and closes the raw data console window
	 */
	public void stopSimulation() {
		if (runSim != null && Integrate6DOFEquations.isRunning() && simulationThread != null && simulationThread.isAlive())
			Integrate6DOFEquations.setRunning(false);
		if (flightDataThread != null && flightDataThread.isAlive())
			FlightData.setRunning(false);
		if (consoleTablePanel != null && consoleTablePanel.isVisible())
			consoleTablePanel.setVisible(false);
	}
	
	//=============================== Plotting =============================================================
	
	/**
	 * Initializes the plot window if not already initialized, otherwise refreshes the window and sets it visible again
	 */
	public void plotSimulation() {
		if(plotWindow == null)
			plotWindow = new PlotWindow(runSim.getLogsOut(), plotCategories, ab.getAircraft());
		else
			plotWindow.refreshPlots(runSim.getLogsOut());
	}
	
	/**
	 * @return if the plot window is visible
	 */
	public boolean isPlotWindowVisible() {
		if (plotWindow == null) return false;
		else return plotWindow.isVisible();
	}
	
	//=============================== Console =============================================================
	
	/**
	 * Initializes the raw data console window and starts the auto-refresh of its contents
	 */
	public void initializeConsole() {
		consoleTablePanel = new ConsoleTablePanel(this);
		consoleTablePanel.startTableRefresh();
	}
	
	/**
	 * @return if the raw data console window is visible
	 */
	public boolean isConsoleWindowVisible() {
		if (consoleTablePanel == null) return false;
		else return consoleTablePanel.isVisible();
	}
	
	/**
	 * Saves the raw data in the console window to a .csv file 
	 * 
	 * @param file
	 * @throws IOException
	 */
	public void saveConsoleOutput(File file) throws IOException {
		Utilities.saveToCSVFile(file, runSim.getLogsOut());
	}
}
