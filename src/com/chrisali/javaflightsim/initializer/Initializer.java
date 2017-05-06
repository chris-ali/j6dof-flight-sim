/**
 * 
 */
package com.chrisali.javaflightsim.initializer;

import javax.swing.SwingUtilities;

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
	
	/**
	 * Selects an appropriate initialization process based on the the RunDisplayMode enum read/parsed in ______
	 * and then run the application
	 */
	public static void selectRunConfigurationAndRun() {
		SimulationConfiguration configuration = new SimulationConfiguration();
		
		// Temporarily set mode to LWJGL Swing mode until parse method is decided
		RunDisplayMode mode = RunDisplayMode.LWJGL_SWING;
		
		switch (mode) {
		case LWJGL_SWING:
			runSwingLWJGLApp(configuration);
			break;
		case SWING_ONLY:
			runSwingApp(configuration);
			break;
		case JMONKEYENGINE:
			runJMonkeyEngineApp(configuration);
			break;
		case NETWORK:
			runNetworkApp(configuration);
			break;
		default:
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
				LWJGLSwingSimulationController controller = new LWJGLSwingSimulationController(configuration); 
				GuiFrame guiFrame = new GuiFrame(controller);
				
				// Pass in mainFrame reference so that OTW display can get Canvas
				// reference
				controller.setGuiFrame(guiFrame);			
			}
		});
	}
	
	/**
	 * Initializes {@link SwingSimulationController} and brings up the Swing GUI menus
	 * 
	 * @param configuration
	 */
	private static void runSwingApp(SimulationConfiguration configuration) {
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				LWJGLSwingSimulationController controller = new LWJGLSwingSimulationController(configuration); 
				
				new GuiFrame(controller);		
			}
		});
	}
	
	/**
	 * To be implemented later; initializes JMonkeyEngine menus and world
	 * 
	 * @param configuration
	 */
	private static void runJMonkeyEngineApp(SimulationConfiguration configuration) {
		
	}
	
	/**
	 * To be implemented later; initializes network adapter for UDP packet transmission
	 * 
	 * @param configuration
	 */
	private static void runNetworkApp(SimulationConfiguration configuration) {
		
	}
}
