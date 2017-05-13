/**
 * 
 */
package com.chrisali.javaflightsim.initializer;

import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chrisali.javaflightsim.simulation.setup.SimulationConfiguration;
import com.chrisali.javaflightsim.swing.GuiFrame;

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
	 */
	public static void selectRunConfigurationAndRun() {
		
		logger.debug("Configuring simulation options...");
		
		SimulationConfiguration configuration;
		
		try {
			logger.debug("Reading configuration files...");
			
			configuration = new SimulationConfiguration();
		} catch (Exception e) {
			logger.fatal("Error initializing configuration!");
			logger.fatal(e.getMessage());
			
			return;
		}
		
		// Temporarily set mode to LWJGL Swing mode until parse method is decided
		RunDisplayMode mode = RunDisplayMode.LWJGL_SWING;
		
		switch (mode) {
		case LWJGL_SWING:
			logger.debug(mode.toString() + " selected");
			runSwingLWJGLApp(configuration);
			break;
		case SWING_ONLY:
			logger.debug(mode.toString() + " selected");
			runSwingApp(configuration);
			break;
		case JMONKEYENGINE:
			logger.debug(mode.toString() + " selected");
			runJMonkeyEngineApp(configuration);
			break;
		case NETWORK:
			logger.debug(mode.toString() + " selected");
			runNetworkApp(configuration);
			break;
		default:
			logger.error("Invalid run mode selected, defaulting to Swing with LWJGL!");
			runSwingLWJGLApp(configuration);
		}
	}
	
	/**
	 * Initializes {@link LWJGLSwingSimulationController} and Swing {@link GuiFrame}; due to cross-referencing
	 * needed with both objects, {@link LWJGLSwingSimulationController#setGuiFrame(GuiFrame)} needs to be
	 * called
	 * 
	 * @param configuration
	 */
	private static void runSwingLWJGLApp(SimulationConfiguration configuration) {
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					LWJGLSwingSimulationController controller = new LWJGLSwingSimulationController(configuration); 
					GuiFrame guiFrame = new GuiFrame(controller);
					
					// Pass in mainFrame reference so that OTW display can get Canvas reference
					controller.setGuiFrame(guiFrame);								
				} catch (Exception e) {
					logger.fatal("Error setting up Swing GUI and controller: ");
					logger.fatal(e.getMessage());
					
					return;
				}
			}
		});
	}
	
	/**
	 * Initializes {@link SwingSimulationController} and brings up the Swing GUI menus
	 * 
	 * @param configuration
	 */
	private static void runSwingApp(SimulationConfiguration configuration) {
		/*
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					SwingSimulationController controller = new SwingSimulationController(configuration); 
					
					new GuiFrame(controller);
				} catch (Exception e) {
					logger.fatal("Error setting up Swing GUI and controller: ");
					logger.fatal(e.getMessage());
					
					return;
				}		
			}
		});
		*/
		logger.fatal("This mode is not implemented yet!");
		return;
	}
	
	/**
	 * To be implemented later; initializes JMonkeyEngine menus and world
	 * 
	 * @param configuration
	 */
	private static void runJMonkeyEngineApp(SimulationConfiguration configuration) {
		logger.fatal("This mode is not implemented yet!");
		return;
	}
	
	/**
	 * To be implemented later; initializes network adapter for UDP packet transmission
	 * 
	 * @param configuration
	 */
	private static void runNetworkApp(SimulationConfiguration configuration) {
		logger.fatal("This mode is not implemented yet!");
		return;
	}
}
