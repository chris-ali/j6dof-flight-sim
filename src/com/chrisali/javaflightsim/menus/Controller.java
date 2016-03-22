package com.chrisali.javaflightsim.menus;

import java.io.File;
import java.io.IOException;
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
	
	/**
	 * @return options EnumSet
	 */
	public EnumSet<Options> getOptions() {return options;}
	
	/**
	 * Sets options to new options and then saves the configuration to a text file using 
	 * {@link Utilities#writeConfigFile(String, String, Set, String)}
	 * 
	 * @param newOptions
	 */
	public void updateOptions(EnumSet<Options> newOptions) {
		options = EnumSet.copyOf(newOptions);
		
		if (ab != null)
			Utilities.writeConfigFile(SIM_CONFIG_PATH, "SimulationSetup", options, ab.getAircraft().getName());
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
		
		Utilities.writeConfigFile("IntegratorConfig", SIM_CONFIG_PATH, integratorConfig);
	}
	
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
		
		Utilities.writeConfigFile("InitialConditions", SIM_CONFIG_PATH, initialConditions);
	}
	
	/**
	 * Updates the InitialControls config file
	 */
	public void updateIninitialControls() {
		Utilities.writeConfigFile("InitialControls", SIM_CONFIG_PATH, initialControls);
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
	public List<EnumMap<SimOuts, Double>> getLogsOut() {return runSim.getLogsOut();}
	
	/**
	 * Initializes and starts the simulation (and flight data, if selected) threads.
	 * Depending on options specified, a console panel, plot window and instrument panel
	 * will alse be initialized and opened 
	 * 
	 * @param panel
	 */
	public void startSimulation(InstrumentPanel panel) {
		runSim = new Integrate6DOFEquations(ab, options);
		
		simulationThread = new Thread(runSim);
		simulationThread.start();
		
		if (options.contains(Options.CONSOLE_DISPLAY))
			initializeConsole();
		if (options.contains(Options.ANALYSIS_MODE)) {
			plotSimulation();
		} else {
			flightData = new FlightData(runSim);
			flightData.setFlightDataListener(panel);
			
			flightDataThread = new Thread(flightData);
			flightDataThread.start();
		}
	}
	
	/**
	 * Interrupts simulation and flight data (if running) threads and closes the raw data console window
	 */
	public void stopSimulation() {
		if (runSim != null && runSim.isRunning() && simulationThread != null && simulationThread.isAlive())
			simulationThread.interrupt();
		if (flightDataThread != null && flightDataThread.isAlive())
			flightDataThread.interrupt();
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
		consoleTablePanel = new ConsoleTablePanel(runSim.getLogsOut(), this);
		consoleTablePanel.startTableRefresh();
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
