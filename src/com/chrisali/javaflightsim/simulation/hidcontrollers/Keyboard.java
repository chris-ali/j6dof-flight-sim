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
package com.chrisali.javaflightsim.simulation.hidcontrollers;

import java.util.ArrayList;
import java.util.Map;

import com.chrisali.javaflightsim.simulation.flightcontrols.FlightControl;
import com.chrisali.javaflightsim.simulation.interfaces.SimulationController;
import com.chrisali.javaflightsim.simulation.setup.ControlsConfiguration;
import com.chrisali.javaflightsim.simulation.setup.SimulationConfiguration;

import net.java.games.input.Component;
import net.java.games.input.Component.Identifier.Key;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

/**
 * The Keyboard object uses JInput to integrate keyboard functionality into the simulation.
 * It works by generating an ArrayList of keyboards connected to the computer, 
 * polling each one's active buttons, using the polled data to calculate control deflections, 
 * and assigning these to each respective key in the controls EnumMap and options EnumSet. 
 * Up/Down and Left/Right control the elevator and ailerons, respectively, and all throttles are 
 * controlled by Page Up/Down. The simulation can be toggled paused by pressing P, and while paused
 * the simulation can be reset to initial conditions by pressing R.
 * The simulation is quit by pressing Q and L plots the simulation.
 * @see AbstractController
 */
public class Keyboard extends AbstractController {
	
	/**
	 *  Constructor for Keyboard class; creates list of controllers using searchForControllers() and
	 *  creates a reference to a {@link SimulationController} object 
	 * @param flightControls
	 * @param simController
	 */
	public Keyboard(Map<FlightControl, Double> flightControls, SimulationController simController) {
		logger.debug("Setting up keyboard...");

		this.flightControls = flightControls;
		this.simController = simController;
		
		controlsConfig = new ControlsConfiguration();
		options = simController.getConfiguration().getSimulationOptions();
				
		searchForControllers();
	}
	
	/**
	 * Search for and add controllers of type Controller.Type.KEYBOARD to controllerList
	 */
	@Override
	public void searchForControllers() {
		Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
		controllerList = new ArrayList<>();
		
		for (Controller controller : controllers) {
			if (controller.getType() == Controller.Type.KEYBOARD) {
				controllerList.add(controller);
				logger.debug("Found a keyboard: " + controller.getName());
			}
		}

		if (controllerList.isEmpty()) {
			logger.error("No keyboard found!");
			return;
		}	
	}
	
	/**
	 * Contains hot keys used by the simulation for various tasks: <br>
	 * Pausing the simulation<br>
	 * Resetting it back to the initial conditions defined in {@link SimulationConfiguration}<br>
	 * Quitting the simulation<br>
	 * Plotting the simulation<br>
	 * 
	 * @param simController
	 */
	public void hotKeys() {
		for (Controller keyboard : controllerList) {
			// Poll controller for data
			if(!keyboard.poll()) 
				continue;
			
			// Iterate through all components of the controller.
			for (Component component : keyboard.getComponents()) {
				String componentName = component.getIdentifier().getName();
				boolean isPressed = component.getPollData() == 1.0f; 
				
				if (componentName.matches(Key.P.toString())) {
					Events.pauseSimulation(options, isPressed);
				}
				
				if (componentName.matches(Key.R.toString())) {
					Events.resetSimulation(options, isPressed);
				}
				
				if (componentName.matches(Key.Q.toString()) && isPressed) {
					Events.stopSimulation(simController);
				}
				
				if (componentName.matches(Key.L.toString()) && isPressed) {
					Events.plotSimulation(simController);
				}
			}
		}
	}
	
	/**
	 *  Get button values from keyboard, and return a Map for updateFlightControls in {@link SimulationController)
	 *  
	 *  @return flightControls Map
	 */
	@Override
	public Map<FlightControl, Double> calculateControllerValues() {
		for (Controller controller : controllerList) {
			// Poll controller for data
			if(!controller.poll()) 
				continue;
			
			for (Component component : controller.getComponents()) {
				String componentName = component.getIdentifier().getName();
				boolean isPressed = component.getPollData() == 1.0f;
								
				// Elevator (Pitch) Down
				if (componentName.matches(Key.UP.toString()) && isPressed) {
					Events.elevatorDown(flightControls);
					continue;
				}
				
				// Elevator (Pitch) Up
				if (componentName.matches(Key.DOWN.toString()) && isPressed) {
					Events.elevatorUp(flightControls);
					continue;
				}
				
				// Left Aileron
				if (componentName.matches(Key.LEFT.toString()) && isPressed) {
					Events.aileronLeft(flightControls);
					continue;
				}
				
				// Right Aileron
				if (componentName.matches(Key.RIGHT.toString()) && isPressed) {
					Events.aileronRight(flightControls);
					continue;
				}
				
				// Increase Throttle
				if (componentName.matches(Key.PAGEUP.toString()) && isPressed) {
					Events.increaseThrottle(flightControls);
					continue;
				}
				
				// Decrease Throttle
				if (componentName.matches(Key.PAGEDOWN.toString()) && isPressed) {
					Events.decreaseThrottle(flightControls);
					continue;
				}
				
				// Flaps Down
				if (componentName.matches(Key.F7.toString()) && isPressed) {
					Events.extendFlaps(flightControls);
					continue;
				}
				
				// Flaps Up
				if (componentName.matches(Key.F6.toString()) && isPressed) {
					Events.retractFlaps(flightControls);
					continue;
				}
				
				// Landing Gear Down/Up
				if (componentName.matches(Key.G.toString())) {
					Events.cycleGear(flightControls, isPressed);
					continue;
				} 
			}
		}
		
		return limitControls(flightControls);
	}
}
