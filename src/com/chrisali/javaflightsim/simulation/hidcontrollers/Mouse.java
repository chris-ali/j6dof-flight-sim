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

import com.chrisali.javaflightsim.interfaces.SimulationController;
import com.chrisali.javaflightsim.simulation.flightcontrols.FlightControl;

import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Component.Identifier.Axis;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

/**
 * The Mouse object uses JInput to integrate mouse functionality into the simulation as a joystick substitute.
 * It works by generating an ArrayList of mice connected to the computer, polling each one's active components 
 * (buttons, axes), using the polled data to calculate control deflections, and assigning these to each respective key 
 * in the controls EnumMap. These deflections are limited by the constants defined in {@link FlightControl}. Ailerons 
 * and Elevator are controlled by horizontal and vertical mouse movement, respectively, and all throttles are controlled 
 * by the mouse wheel.
 * @see AbstractController
 */
public class Mouse extends AbstractController {
	
	// Since mouse axes are measured relative to the stopped position, these fields store the control deflection, 
	// and the mouse axis value is added to these
	private double tempElev  = 0.0;
	private double tempAil   = 0.0;
	private double tempThrot = 0.0;
	
	// Add these trim values to getControlDeflection method call to emulate trim deflections
	private double trimElevator = 0.0;
	private double trimAileron  = 0.0;

	/**
	 * Creates list of controllers using searchForControllers() and generates trim values for elevator and aileron
	 * 
	 * @param flightControls
	 */
	public Mouse(Map<FlightControl, Double> flightControls, SimulationController simController) {
		logger.debug("Setting up mouse...");

		this.flightControls = flightControls;
		this.simController = simController;
		
		options = simController.getConfiguration().getSimulationOptions();
		
		// Get initial trim values from initial values in controls EnumMap (rad)
		trimElevator = flightControls.get(FlightControl.ELEVATOR);
		trimAileron = flightControls.get(FlightControl.AILERON);
				
		searchForControllers();
	}
	
	/**
	 * Search for and add controllers of type Controller.Type.MOUSE to controllerList
	 */
	@Override
	public void searchForControllers() {
		Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
		controllerList = new ArrayList<>();
		
		for (Controller controller : controllers) {
			if (controller.getType() == Controller.Type.MOUSE)
				controllerList.add(controller);
		}

		if (controllerList.isEmpty()) {
			logger.error("No mice found!");
			return;
		}
	}
	
	/**
	 *  Get button, mouse wheel and axis values from mouse, and update flightControls Map with controls from jinput
	 *  in {@link AbstractController}
	 *  
	 *  @param flightControls
	 */
	@Override
	public void calculateControllerValues(Map<FlightControl, Double> flightControls) {
		for (Controller controller : controllerList) {
			
			// Poll controller for data
			if(!controller.poll()) 
				continue;
			
			for(Component component : controller.getComponents()) {
				Identifier componentIdentifier = component.getIdentifier();

				// Buttons
				if(componentIdentifier.getName().matches("^[0-9]*$")) { // If the component identifier contains only numbers, it is a button
					if(component.getPollData() == 1.0f) {
						// Button index (nothing implemented yet)
					}
					continue;
				}

				// Mouse Axes - Read raw mouse relative value, add relative value to temp* variable, and add trim value
				// to control deflection
				if(component.isRelative()){
					double axisValue = (double)component.getPollData()/10000;
					
					// Y axis (Elevator)
					if(componentIdentifier == Axis.Y) {
						if(axisValue != 0) {
							tempElev += axisValue;
							flightControls.put(FlightControl.ELEVATOR, -(tempElev+trimElevator));
						}
						continue;
					}
					// X axis (Aileron)
					if(componentIdentifier == Axis.X) {
						if(axisValue != 0) {
							tempAil += axisValue;
							flightControls.put(FlightControl.AILERON, -(tempAil+trimAileron));
						}
						continue;
					}
					// Z axis (Throttle)
					if(componentIdentifier == Axis.Z) {
						if(axisValue != 0) {
							tempThrot += axisValue;
							flightControls.put(FlightControl.THROTTLE_1, tempThrot*250);
							flightControls.put(FlightControl.THROTTLE_2, tempThrot*250);
						}
						continue;
					}
				}
			}
		}
	}
}
