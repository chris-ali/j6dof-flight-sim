/*******************************************************************************
 * Copyright (C) 2016-2017 Christopher Ali
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *  If you have any questions about this project, you can visit
 *  the project's GitHub repository at: http://github.com/chris-ali/j6dof-flight-sim/
 ******************************************************************************/
package com.chrisali.javaflightsim.initializer;

import java.awt.Canvas;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.chrisali.javaflightsim.lwjgl.LWJGLWorld;
import com.chrisali.javaflightsim.lwjgl.renderengine.DisplayManager;
import com.chrisali.javaflightsim.simulation.datatransfer.EnvironmentData;
import com.chrisali.javaflightsim.simulation.datatransfer.FlightData;
import com.chrisali.javaflightsim.simulation.flightcontrols.FlightControls;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.integration.SimOuts;
import com.chrisali.javaflightsim.simulation.interfaces.SimulationController;
import com.chrisali.javaflightsim.simulation.setup.Options;
import com.chrisali.javaflightsim.simulation.setup.Trimming;
import com.chrisali.javaflightsim.simulation.utilities.FileUtilities;
import com.chrisali.javaflightsim.swing.GuiFrame;
import com.chrisali.javaflightsim.swing.SimulationWindow;
import com.chrisali.javaflightsim.swing.consoletable.ConsoleTablePanel;
import com.chrisali.javaflightsim.swing.plotting.PlotWindow;

/**
 * Controls the configuration and running of processes supporting the simulation component of JavaFlightSim. This consists of: 
 * <p>The simulation engine that integrates the 6DOF equations ({@link Integrate6DOFEquations})</p>
 * <p>Initialization and control of the LWJGL out the window (OTW) world ({@link LWJGLWorld})</p>
 * <p>Initializing the Swing GUI menus</p>
 * <p>Plotting of the simulation states and data ({@link PlotWindow})</p>
 * <p>Raw data display of simulation states ({@link ConsoleTablePanel})</p>
 * <p>Transmission of flight data to the instrument panel and out the window display ({@link FlightData})</p>
 * <p>Transmission of environment data to the simulation ({@link EnvironmentData})</p>
 * 
 * @author Christopher Ali
 *
 */
public class LWJGLSwingSimulationController implements SimulationController {
	
	// Configuration
	private SimulationConfiguration configuration;
	
	// Simulation and Threads
	private FlightControls flightControls;
	private Thread flightControlsThread;
	
	private Integrate6DOFEquations runSim;
	private Thread simulationThread;
	
	private FlightData flightData;
	private Thread flightDataThread;

	private EnvironmentData environmentData;
	private Thread environmentDataThread;
	
	// Menus and Integrated Simulation Window
	private GuiFrame guiFrame;
	
	// Plotting
	private PlotWindow plotWindow;
	private Set<String> plotCategories = new HashSet<>(Arrays.asList("Controls", "Instruments", "Position", "Rates", "Miscellaneous"));
	
	// Raw Data Console
	private ConsoleTablePanel consoleTablePanel;
	
	// Out the Window
	private LWJGLWorld outTheWindow;
	private Thread outTheWindowThread;
	
	/**
	 * Initializes initial settings, configurations and conditions to be edited through menu options
	 */
	public LWJGLSwingSimulationController(SimulationConfiguration configuration) {
		this.configuration = configuration;
	}
	
	//============================== Configuration =========================================================
	
	/**
	 * @return instance of configuraion
	 */
	public SimulationConfiguration getConfiguration() {return configuration;}
	
	//=============================== Simulation ===========================================================

	/**
	 * @return instance of simulation
	 */
	public Integrate6DOFEquations getSimulation() {return runSim;}

	/**
	 * @return ArrayList of simulation output data 
	 * @see SimOuts
	 */
	public List<Map<SimOuts, Double>> getLogsOut() {return runSim.getLogsOut();}
	
	/**
	 * @return if runSim was able to clear simulation data kept in logsOut
	 */
	public boolean clearLogsOut() {
		return (runSim != null) ? runSim.clearLogsOut() : false;
	}
	
	/**
	 * Initializes, trims and starts the flight controls, simulation (and flight and environment data, if selected) threads.
	 * Depending on options specified, a console panel and/or plot window will also be initialized and opened 
	 */
	@Override
	public void startSimulation() {
		Trimming.trimSim(configuration, false);
		
		flightControls = new FlightControls(this);
		flightControlsThread = new Thread(flightControls);
		
		runSim = new Integrate6DOFEquations(flightControls, configuration);
		simulationThread = new Thread(runSim);

		flightControlsThread.start();
		simulationThread.start();
		
		if (configuration.getSimulationOptions().contains(Options.CONSOLE_DISPLAY))
			initializeConsole();
		
		if (configuration.getSimulationOptions().contains(Options.ANALYSIS_MODE)) {
			try {
				// Wait a bit to allow the simulation to finish running
				Thread.sleep(1000);
				plotSimulation();
				//Stop flight controls thread after analysis finished
				FlightControls.setRunning(false);
			} catch (InterruptedException e) {}
			
		} else {
			outTheWindow = new LWJGLWorld(this);
			//(Re)initalize simulation window to prevent scaling issues with instrument panel
			getGuiFrame().initSimulationWindow();
			
			environmentData = new EnvironmentData(outTheWindow);
			environmentData.addEnvironmentDataListener(runSim);
			
			environmentDataThread = new Thread(environmentData);
			environmentDataThread.start();
			
			flightData = new FlightData(runSim);
			flightData.addFlightDataListener(guiFrame.getInstrumentPanel());
			flightData.addFlightDataListener(outTheWindow);
			
			flightDataThread = new Thread(flightData);
			flightDataThread.start();
		}
	}
	
	/**
	 * Stops simulation, flight controls and data transfer threads (if running), closes the raw data {@link ConsoleTablePanel},
	 * {@link SimulationWindow}, and opens the main menus window again
	 */
	@Override
	public void stopSimulation() {
		if (runSim != null && Integrate6DOFEquations.isRunning() && simulationThread != null && simulationThread.isAlive()) {
			Integrate6DOFEquations.setRunning(false);
			FlightControls.setRunning(false);
		}
		
		if (flightDataThread != null && flightDataThread.isAlive())
			FlightData.setRunning(false);
		
		if (outTheWindowThread != null && outTheWindowThread.isAlive())
			EnvironmentData.setRunning(false);
		
		getGuiFrame().getSimulationWindow().dispose();
		getGuiFrame().setVisible(true);
	}
	
	//=============================== Plotting =============================================================
	
	/**
	 * Initializes the plot window if not already initialized, otherwise refreshes the window and sets it visible again
	 */
	public void plotSimulation() {
		if(plotWindow == null)
			plotWindow = new PlotWindow(plotCategories, this);
		else
			plotWindow.refreshPlots(runSim.getLogsOut());
		
		if (!isPlotWindowVisible())
			plotWindow.setVisible(true);
	}
	
	/**
	 * @return if the plot window is visible
	 */
	public boolean isPlotWindowVisible() {
		return (plotWindow == null) ? false : plotWindow.isVisible();
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
		return (consoleTablePanel == null) ? false : consoleTablePanel.isVisible();
	}
	
	/**
	 * Saves the raw data in the console window to a .csv file 
	 * 
	 * @param file
	 * @throws IOException
	 */
	public void saveConsoleOutput(File file) throws IOException {
		FileUtilities.saveToCSVFile(file, runSim.getLogsOut());
	}
	
	//========================== Main Frame Menus =========================================================
	
	/**
	 * Sets {@link GuiFrame} reference for {@link LWJGLWorld}, which needs it to 
	 * set the parent {@link Canvas} in {@link DisplayManager}
	 * 
	 * @param guiFrame
	 */
	public void setGuiFrame(GuiFrame guiFrame) {
		this.guiFrame = guiFrame;
	}
	
	/**
	 * @return reference to {@link GuiFrame} object in {@link LWJGLSwingSimulationController}
	 */
	public GuiFrame getGuiFrame() {
		return guiFrame;
	}

	//=========================== OTW Threading ==========================================================
	
	/**
	 * Initalizes and starts out the window thread; called from {@link SimulationWindow}'s addNotify() method
	 * to allow OTW thread to start gracefully; uses the Stack Overflow solution shown here:
	 * <p>http://stackoverflow.com/questions/26199534/how-to-attach-opengl-display-to-a-jframe-and-dispose-of-it-properly</p>
	 */
	public void startOTWThread() {
		outTheWindowThread = new Thread(outTheWindow);
		outTheWindowThread.start(); 
	}
	
	/**
	 * Stops out the window thread; called from {@link SimulationWindow}'s removeNotify() method
	 * to allow OTW thread to stop gracefully; uses the Stack Overflow solution shown here:
	 * <p>http://stackoverflow.com/questions/26199534/how-to-attach-opengl-display-to-a-jframe-and-dispose-of-it-properly</p>
	 */
	public void stopOTWThread() {
		LWJGLWorld.requestClose(); // sets running boolean in RunWorld to false to begin the clean up process
		
		try {outTheWindowThread.join();
		} catch (InterruptedException e) {}
	}
}
