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
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chrisali.javaflightsim.interfaces.SimulationController;
import com.chrisali.javaflightsim.lwjgl.LWJGLWorld;
import com.chrisali.javaflightsim.lwjgl.renderengine.DisplayManager;
import com.chrisali.javaflightsim.simulation.SimulationRunner;
import com.chrisali.javaflightsim.simulation.datatransfer.EnvironmentData;
import com.chrisali.javaflightsim.simulation.datatransfer.FlightData;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.integration.SimOuts;
import com.chrisali.javaflightsim.simulation.setup.Options;
import com.chrisali.javaflightsim.simulation.setup.SimulationConfiguration;
import com.chrisali.javaflightsim.simulation.setup.Trimming;
import com.chrisali.javaflightsim.simulation.utilities.FileUtilities;
import com.chrisali.javaflightsim.swing.GuiFrame;
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
	
	//Logging
	private static final Logger logger = LogManager.getLogger(LWJGLSwingSimulationController.class);
	
	// Configuration
	private SimulationConfiguration configuration;
	
	// Simulation and Threads
	private SimulationRunner runner;
	private Thread runnerThread;
	
	// Menus and Integrated Simulation Window
	private GuiFrame guiFrame;
	
	// Plotting
	private PlotWindow plotWindow;
	
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
	@Override
	public SimulationConfiguration getConfiguration() { return configuration; }
	
	//=============================== Simulation ===========================================================

	/**
	 * Initializes, trims and starts the flight controls, simulation (and flight and environment data, if selected) threads.
	 * Depending on options specified, a console panel and/or plot window will also be initialized and opened 
	 */
	@Override
	public void startSimulation() {
		if (runner != null && runner.isRunning()) {
			logger.warn("Simulation is already running! Please wait until it has finished");
			return;
		}
		
		configuration = FileUtilities.readSimulationConfiguration();
			
		logger.debug("Starting simulation...");
		
		logger.debug("Trimming aircraft...");
		Trimming.trimSim(configuration, false);
				
		if (configuration.getSimulationOptions().contains(Options.ANALYSIS_MODE)) {
			logger.debug("Running simulation in Analysis Mode...");
			initializeAnalysisMode();
		} else {
			logger.debug("Running simulation in Normal Mode...");
			initializeNormalMode();
		}
				
		if (runner != null) {
			logger.debug("Initializaing and starting simulation runner thread...");
			runnerThread = new Thread(runner);
			runnerThread.start();			
		} else {
			logger.error("Simulation Runner has not been initialized yet! Please do so before starting the runner thread");
		}
	}
	
	/**
	 * Stops simulation and data transfer threads (if running), closes the raw data {@link ConsoleTablePanel},
	 * {@link SimulationWindow}, and opens the main menus window again
	 */
	@Override
	public void stopSimulation() {
		logger.debug("Stopping simulation...");

		runner.setRunning(false);	
		
		logger.debug("Returning to menus...");
		getGuiFrame().setVisible(true);
	}
	
	/**
	 * Initalize all processes needed for starting the simulation in analysis mode
	 */
	private void initializeAnalysisMode() {
		try {						
			logger.debug("Initializing simulation runner...");
			runner = new SimulationRunner(this);
		} catch (Exception ey) {
			logger.error("An error occurred while running in analysis mose!", ey);
		}
	}
	
	/**
	 * Initalize all processes needed for starting the simulation in normal (pilot in the loop) mode
	 */
	private void initializeNormalMode() {
		try {
			logger.debug("Initializing LWJGL world...");
			outTheWindow = new LWJGLWorld(this);
			
			logger.debug("Initializing OTW thread...");
			outTheWindowThread = new Thread(outTheWindow);
			outTheWindowThread.start(); 
			
			logger.debug("Initializing simulation runner...");
			runner = new SimulationRunner(this, outTheWindow);
			runner.addFlightDataListener(outTheWindow);
		} catch (Exception e) {
			logger.error("An error occurred while starting normal mode!", e);
		}
	}
	
	/**
	 * @return ArrayList of simulation output data 
	 * @see SimOuts
	 */
	public List<Map<SimOuts, Double>> getLogsOut() {
		return (runner != null && runner.isRunning()) ? runner.getSimulation().getLogsOut() : null;
	}
	
	/**
	 * @return if simulation was able to clear data kept in logsOut
	 */
	public boolean clearLogsOut() {
		return (runner != null && runner.isRunning()) ? runner.getSimulation().clearLogsOut() : false;
	}
		
	//=============================== Plotting =============================================================
	
	/**
	 * Initializes the plot window if not already initialized, otherwise refreshes the window and sets it visible again
	 */
	@Override
	public void plotSimulation() {
		logger.debug("Plotting simulation results...");
		
		try {
			if(plotWindow != null)
				plotWindow.setVisible(false);
				
			plotWindow = new PlotWindow(this);		
		} catch (Exception e) {
			logger.error("An error occurred while generating plots!", e);
		}
	}
	
	/**
	 * @return Instance of the plot window
	 */
	public PlotWindow getPlotWindow() { return plotWindow; }

	/**
	 * @return if the plot window is visible
	 */
	@Override
	public boolean isPlotWindowVisible() {
		return (plotWindow == null) ? false : plotWindow.isVisible();
	}
	
	//=============================== Console =============================================================
	
	/**
	 * Initializes the raw data console window and starts the auto-refresh of its contents
	 */
	@Override
	public void initializeConsole() {
		try {
			logger.debug("Starting flight data console...");
			
			if(consoleTablePanel != null)
				consoleTablePanel.setVisible(false);
			
			consoleTablePanel = new ConsoleTablePanel(this, runner);
			consoleTablePanel.startTableRefresh();			
		} catch (Exception e) {
			logger.error("An error occurred while starting the console panel!", e);
		}
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
		logger.debug("Saving console output to: " + file.getAbsolutePath());
		
		try {			
			FileUtilities.saveToCSVFile(file, runner.getSimulation().getLogsOut());
		} catch (Exception e) {
			logger.error("An error occurred while saving console output!", e);
		}
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
	 * @return reference to {@link GuiFrame} object in {@link NewLWJGLSwingSimulationController}
	 */
	public GuiFrame getGuiFrame() {
		return guiFrame;
	}
}
