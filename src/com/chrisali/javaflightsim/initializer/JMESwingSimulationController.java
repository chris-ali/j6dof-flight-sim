/*******************************************************************************
 * Copyright (C) 2016-2018 Christopher Ali
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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.chrisali.javaflightsim.interfaces.SimulationController;
import com.chrisali.javaflightsim.simulation.SimulationStepper;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.integration.SimOuts;
import com.chrisali.javaflightsim.simulation.setup.SimulationConfiguration;
import com.chrisali.javaflightsim.simulation.setup.Trimming;
import com.chrisali.javaflightsim.simulation.utilities.FileUtilities;
import com.chrisali.javaflightsim.swing.consoletable.ConsoleTablePanel;
import com.chrisali.javaflightsim.swing.plotting.PlotWindow;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Controls the configuration and running of processes supporting the simulation component of JavaFlightSim. This consists of: 
 * <p>The simulation engine that integrates the 6DOF equations ({@link Integrate6DOFEquations})</p>
 * <p>Plotting of the simulation states and data ({@link PlotWindow})</p>
 * <p>Raw data display of simulation states ({@link ConsoleTablePanel})</p>
 * 
 * @author Christopher Ali
 *
 */
public class JMESwingSimulationController implements SimulationController {
	
	//Logging
	private static final Logger logger = LogManager.getLogger(JMESwingSimulationController.class);
	
	// Configuration
	private SimulationConfiguration configuration;
	
	// Simulation
	private SimulationStepper stepper;
		
	// Plotting
	private PlotWindow plotWindow;
	
	// Raw Data Console
	private ConsoleTablePanel consoleTablePanel;
		
	/**
	 * Initializes initial settings, configurations and conditions to be edited through menu options
	 */
	public JMESwingSimulationController(SimulationConfiguration configuration) {
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
	 * Initializes, trims and starts the flight controls, simulation, and flight/environment data steppers.
	 * Depending on options specified, a console panel and/or plot window will also be initialized and opened 
	 */
	@Override
	public void startSimulation() {
		if (stepper != null && stepper.isRunning()) {
			logger.warn("Simulation is already running! Please wait until it has finished");
			return;
		}
		
		configuration = FileUtilities.readSimulationConfiguration();
			
		logger.debug("Starting simulation...");
		
		logger.debug("Trimming aircraft...");
		Trimming.trimSim(configuration, false);
		
		logger.debug("Initializing simulation stepper...");
		stepper = new SimulationStepper(this);
	}
	
	/**
	 * Stops simulation stepping
	 */
	@Override
	public void stopSimulation() {
		logger.debug("Stopping simulation...");

		stepper.setRunning(false);
	}

	/**
	 * @return if simulation is running
	 */
	@Override
	public boolean isSimulationRunning() {
		return (stepper != null && stepper.isRunning());
	}
	
	/**
	 * @return ArrayList of simulation output data 
	 * @see SimOuts
	 */
	public List<Map<SimOuts, Double>> getLogsOut() {
		return (stepper != null && stepper.isRunning()) ? stepper.getSimulation().getLogsOut() : null;
	}
	
	/**
	 * @return if simulation was able to clear data kept in logsOut
	 */
	public boolean clearLogsOut() {
		return (stepper != null && stepper.isRunning()) ? stepper.getSimulation().clearLogsOut() : false;
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
			
			consoleTablePanel = new ConsoleTablePanel(this);
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
	@Override
	public void saveConsoleOutput(File file) throws IOException {
		logger.debug("Saving console output to: " + file.getAbsolutePath());
		
		FileUtilities.saveToCSVFile(file, stepper.getSimulation().getLogsOut());
	}
}
