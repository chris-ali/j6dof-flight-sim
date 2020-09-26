/*******************************************************************************
 * Copyright (C) 2016-2020 Christopher Ali
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

import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chrisali.javaflightsim.javafx.MainMenu;
import com.chrisali.javaflightsim.simulation.utilities.FileUtilities;

/**
 * Contains methods used to properly initialize all components of Java Flight Simulator based on 
 * configuration settings specified 
 * 
 * @author Christopher Ali
 *
 */
public class Initializer {
	
	public static final Logger logger = LogManager.getLogger(Initializer.class);
	
	/**
	 * Selects an appropriate initialization process based on the the RunDisplayMode enum read/parsed in ______
	 * and then run the application
	 * 
	 * @param args Java VM args
	 */
	public static void selectRunConfigurationAndRun(String[] args) {
		// Temporarily set mode to LWJGL Swing mode until parse method is decided
		RunDisplayMode mode = RunDisplayMode.LWJGL_JAVAFX;
		
		switch (mode) {
		case LWJGL_SWING:
			logger.info(mode.toString() + " selected");
			runLWJGLSwingApp();
			break;
		case LWJGL_JAVAFX:
			logger.info(mode.toString() + " selected");
			runLWJGLJavaFXApp(args);
			break;
		case SWING_ONLY:
			logger.info(mode.toString() + " selected");
			runSwingApp();
			break;
		case JMONKEYENGINE:
			logger.info(mode.toString() + " selected");
			runJMonkeyEngineApp();
			break;
		case NETWORK:
			logger.info(mode.toString() + " selected");
			runNetworkApp();
			break;
		default:
			logger.error("Invalid run mode selected, defaulting to Swing with LWJGL!");
			runLWJGLSwingApp();
		}
	}
	
	/**
	 * Initializes {@link LWJGLSwingSimulationController}
	 */
	private static void runLWJGLSwingApp() {
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					new LWJGLSwingSimulationController(FileUtilities.readSimulationConfiguration()); 							
				} catch (Exception e) {
					logger.fatal("Error setting up Swing GUI and controller: ", e);
					
					return;
				}
			}
		});
	}

	/**
	 * Initializes {@link MainMenu} which will initialize {@link LWJGLSwingSimulationController}
	 * 
	 * @param args
	 */
	private static void runLWJGLJavaFXApp(String[] args) {
		try {
			MainMenu mainMenu = new MainMenu();
			mainMenu.launchMenus(args);		
		} catch (Exception e) {
			logger.fatal("Error setting up JavaFX GUI and controller: ", e);
			
			return;
		}
	}
	
	/**
	 * Initializes {@link SwingSimulationController} and brings up the Swing GUI menus
	 */
	private static void runSwingApp() {
		logger.fatal("This mode is not implemented yet!");
		return;
	}
	
	/**
	 * To be implemented later; initializes JMonkeyEngine menus and world
	 */
	private static void runJMonkeyEngineApp() {
		logger.fatal("This mode is not implemented yet!");
		return;
	}
	
	/**
	 * To be implemented later; initializes network adapter for UDP packet transmission
	 */
	private static void runNetworkApp() {
		logger.fatal("This mode is not implemented yet!");
		return;
	}
}
