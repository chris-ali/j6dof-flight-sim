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
import java.util.EnumSet;
import java.util.Map;

import com.chrisali.javaflightsim.simulation.flightcontrols.FlightControlType;
import com.chrisali.javaflightsim.simulation.interfaces.SimulationController;
import com.chrisali.javaflightsim.simulation.setup.Options;
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
	// Keep track if button is pressed, so events occur only once if button held down 
	private boolean pausePressed = false;
	private boolean resetPressed = false;
	private boolean gearPressed = false;
	// Keep track of reset, so that it can only be run once per pause
	private boolean wasReset = false;
	
	private SimulationController simController;
	private EnumSet<Options> options;
	
	/**
	 *  Constructor for Keyboard class; creates list of controllers using searchForControllers() and
	 *  creates a reference to a {@link SimulationController} object 
	 * @param controls
	 * @param simController
	 */
	public Keyboard(Map<FlightControlType, Double> controls, SimulationController simController) {
		this.simController = simController;
		controllerList = new ArrayList<>();
		options = simController.getConfiguration().getSimulationOptions();
		
		// Get initial trim values from initial values in controls EnumMap (rad)
		trimElevator = controls.get(FlightControlType.ELEVATOR);
		trimAileron  = controls.get(FlightControlType.AILERON);
		trimRudder   = controls.get(FlightControlType.RUDDER);
		
		logger.debug("Setting up keyboard...");
		
		searchForControllers();
	}
	
	/**
	 * Search for and add controllers of type Controller.Type.KEYBOARD to controllerList
	 */
	@Override
	public void searchForControllers() {
		Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();

		for (Controller controller : controllers) {
			if (controller.getType() == Controller.Type.KEYBOARD)
				controllerList.add(controller);
		}

		// If no keyboards available, exit function
		if (controllerList.isEmpty()) {
			logger.debug("No keyboard found!");
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
		// Iterate through all controllers connected
		for (Controller keyboard : controllerList) {
			// Poll controller for data; if disconnected, break out of componentIdentification loop
			if(!keyboard.poll()) 
				break;
			
			// Iterate through all components of the controller.
			for (Component component : keyboard.getComponents()) {
				String componentName = component.getIdentifier().getName();
				
				// When simulation paused, can be reset once per pause with "R" key
				if (componentName.matches(Key.P.toString())) {
					if(component.getPollData() == 1.0f && !options.contains(Options.PAUSED) && !pausePressed) {
						options.add(Options.PAUSED);
						logger.debug("Simulation Paused!");
						pausePressed = true;
					} else if(component.getPollData() == 1.0f && options.contains(Options.PAUSED) && !pausePressed) {
						options.remove(Options.PAUSED);
						wasReset = false;
						pausePressed = true;
					} else if(component.getPollData() == 0.0f && pausePressed) {
						pausePressed = false;
					}

					continue;
				}
				
				// Reset simulation
				if (componentName.matches(Key.R.toString())) {
					if(component.getPollData() == 1.0f && options.contains(Options.PAUSED) 
					    && !options.contains(Options.RESET) && !resetPressed && !wasReset) {
						options.add(Options.RESET);
						logger.debug("Simulation Reset!");
						wasReset = true;
						resetPressed = true;
					} else if (component.getPollData() == 0.0f && resetPressed) {
						options.remove(Options.RESET);
						resetPressed = false;
					}
					
					continue;
				}
				
				// Quits simulation
				if (componentName.matches(Key.Q.toString())) {
					if(component.getPollData() == 1.0f) {
						simController.stopSimulation();
					}
					
					continue;
				}
				
				// Plots simulation
				if (componentName.matches(Key.L.toString())) {
					if(component.getPollData() == 1.0f && simController.getSimulation() != null 
						&& !simController.isPlotWindowVisible()) {
						simController.plotSimulation();
					}
					
					continue;
				}
			}
		}
	}
	
	/**
	 *  Get button  values from keyboard, and return a Map for updateFlightControls in {@link SimulationController)
	 *  
	 *  @return flightControls Map
	 */
	@Override
	protected Map<FlightControlType, Double> calculateControllerValues(Map<FlightControlType, Double> controls) {
		// Iterate through all controllers connected
		for (Controller controller : controllerList) {
			// Poll controller for data; if disconnected, break out of componentIdentification loop
			if(!controller.poll()) 
				break;
			
			// Iterate through all components (keys) of the controller.
			for (Component component : controller.getComponents()) {
				String componentName = component.getIdentifier().getName();
				
				// Elevator (Pitch) Down
				if (componentName.matches(Key.UP.toString()) && 
					controls.get(FlightControlType.ELEVATOR) <= FlightControlType.ELEVATOR.getMaximum()) {
					
					if(component.getPollData() == 1.0f)
						controls.put(FlightControlType.ELEVATOR, controls.get(FlightControlType.ELEVATOR) + getDeflectionRate(FlightControlType.ELEVATOR));
					
					continue;
				}
				
				// Elevator (Pitch) Up
				if (componentName.matches(Key.DOWN.toString()) &&
					controls.get(FlightControlType.ELEVATOR) >= FlightControlType.ELEVATOR.getMinimum()) {
					
					if(component.getPollData() == 1.0f)
						controls.put(FlightControlType.ELEVATOR, controls.get(FlightControlType.ELEVATOR) - getDeflectionRate(FlightControlType.ELEVATOR));
					
					continue;
				}
				
				// Left Aileron
				if (componentName.matches(Key.LEFT.toString()) && 
					controls.get(FlightControlType.AILERON) >= FlightControlType.AILERON.getMinimum()) {
					
					if(component.getPollData() == 1.0f)
						controls.put(FlightControlType.AILERON, controls.get(FlightControlType.AILERON) + getDeflectionRate(FlightControlType.AILERON));
					
					continue;
				}
				
				// Right Aileron
				if (componentName.matches(Key.RIGHT.toString()) && 
					controls.get(FlightControlType.AILERON) <= FlightControlType.AILERON.getMaximum()) {
					
					if(component.getPollData() == 1.0f)
						controls.put(FlightControlType.AILERON, controls.get(FlightControlType.AILERON) - getDeflectionRate(FlightControlType.AILERON));
					
					continue;
				}
				
				// Increase Throttle
				if (componentName.matches(Key.PAGEUP.toString()) && 
					controls.get(FlightControlType.THROTTLE_1) <= FlightControlType.THROTTLE_1.getMaximum() &&
					controls.get(FlightControlType.THROTTLE_2) <= FlightControlType.THROTTLE_2.getMaximum() &&
					controls.get(FlightControlType.THROTTLE_3) <= FlightControlType.THROTTLE_3.getMaximum() &&
					controls.get(FlightControlType.THROTTLE_4) <= FlightControlType.THROTTLE_4.getMaximum()) {
					
					if(component.getPollData() == 1.0f) {
						controls.put(FlightControlType.THROTTLE_1, controls.get(FlightControlType.THROTTLE_1) + getDeflectionRate(FlightControlType.THROTTLE_1));
						controls.put(FlightControlType.THROTTLE_2, controls.get(FlightControlType.THROTTLE_2) + getDeflectionRate(FlightControlType.THROTTLE_2));
						controls.put(FlightControlType.THROTTLE_3, controls.get(FlightControlType.THROTTLE_3) + getDeflectionRate(FlightControlType.THROTTLE_3));
						controls.put(FlightControlType.THROTTLE_4, controls.get(FlightControlType.THROTTLE_4) + getDeflectionRate(FlightControlType.THROTTLE_4));
					}
					
					continue;
				}
				
				// Decrease Throttle
				if (componentName.matches(Key.PAGEDOWN.toString()) && 
					controls.get(FlightControlType.THROTTLE_1) >= FlightControlType.THROTTLE_1.getMinimum() &&
					controls.get(FlightControlType.THROTTLE_2) >= FlightControlType.THROTTLE_2.getMinimum() &&
					controls.get(FlightControlType.THROTTLE_3) >= FlightControlType.THROTTLE_3.getMinimum() &&
					controls.get(FlightControlType.THROTTLE_4) >= FlightControlType.THROTTLE_4.getMinimum()) {
					
					if(component.getPollData() == 1.0f) {
						controls.put(FlightControlType.THROTTLE_1, controls.get(FlightControlType.THROTTLE_1) - getDeflectionRate(FlightControlType.THROTTLE_1));
						controls.put(FlightControlType.THROTTLE_2, controls.get(FlightControlType.THROTTLE_2) - getDeflectionRate(FlightControlType.THROTTLE_2));
						controls.put(FlightControlType.THROTTLE_3, controls.get(FlightControlType.THROTTLE_3) - getDeflectionRate(FlightControlType.THROTTLE_3));
						controls.put(FlightControlType.THROTTLE_4, controls.get(FlightControlType.THROTTLE_4) - getDeflectionRate(FlightControlType.THROTTLE_4));
					}
					
					continue;
				}
				
				// Flaps Down
				if (componentName.matches(Key.F7.toString()) && 
					controls.get(FlightControlType.FLAPS) <= FlightControlType.FLAPS.getMaximum()) {
					
					if(component.getPollData() == 1.0f)
						controls.put(FlightControlType.FLAPS, (flaps += getDeflectionRate(FlightControlType.FLAPS)));
					
					continue;
				}
				
				// Flaps Up
				if (componentName.matches(Key.F6.toString()) && 
						controls.get(FlightControlType.FLAPS) >= FlightControlType.FLAPS.getMinimum()) {
						
					if(component.getPollData() == 1.0f)
						controls.put(FlightControlType.FLAPS, (flaps -= getDeflectionRate(FlightControlType.FLAPS)));
					
					continue;
				}
				
				// Landing Gear Down/Up
				// use gPressed to prevent numerous cycles of gear up/down if key held down;
				// need to release key to extend or retract gear again
				if (componentName.matches(Key.G.toString()) && 
						!gearPressed &&
						controls.get(FlightControlType.GEAR) < 0.5) {
					
					if(component.getPollData() == 1.0f) {
						controls.put(FlightControlType.GEAR, 1.0);
						gearPressed = true;
					}
					
					continue;
				} else if (componentName.matches(Key.G.toString()) && 
						!gearPressed &&
						controls.get(FlightControlType.GEAR) > 0.5) {
					
					if(component.getPollData() == 1.0f) {
						controls.put(FlightControlType.GEAR, 0.0);
						gearPressed = true;
					}
					
					continue;
				} else if (componentName.matches(Key.G.toString()) && 
						component.getPollData() == 0.0f && 
						gearPressed) {
					
					gearPressed = false;
					
					continue;
				} 
			}
		}
		return controls;
	}

}
