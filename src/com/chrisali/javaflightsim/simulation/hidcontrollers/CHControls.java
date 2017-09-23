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

import com.chrisali.javaflightsim.simulation.flightcontrols.Events;
import com.chrisali.javaflightsim.simulation.flightcontrols.FlightControl;

import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Component.Identifier.Axis;
import net.java.games.input.Component.POV;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

/**
 * The CHControls object uses JInput to integrate functionality for CH controls into the simulation.
 * It works by generating an ArrayList of CH controls (yoke, pedals and throttle quadrant) connected
 * to the computer, polling each one's active components (buttons, axes, POV hat), using 
 * the polled data to calculate control deflections, and assigning these to each respective key 
 * in the controls EnumMap. These deflections are limited by the constants defined in the 
 * {@link FlightControl}. Aileron and Elevator trim are handled by the POV hat switch, and all
 * throttles are controlled by the throttle quadrant.
 * @see AbstractController
 */
public class CHControls extends AbstractController {
	
	/**
	 *  Constructor for CHControls class creates list of controllers using searchForControllers()
	 * @param controls
	 */
	public CHControls(Map<FlightControl, Double> controls) {
		controllerList = new ArrayList<>();

		logger.debug("Setting up CH Flight Controls...");
		
		searchForControllers();
	}
	
	/**
	 * Search for and add controllers of type Controller.Type.STICK
	 * to controllerList
	 */ 
	@Override
	public void searchForControllers() {
		Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
		
		for(Controller controller : controllers){
			if (controller.getType() == Controller.Type.STICK) {
				controllerList.add(controller);
				logger.debug("Found a joystick: " + controller.getName());
			}
		}
		
		// If no joysticks available, exit function
		if (controllerList.isEmpty()) {
			logger.error("No joysticks found!");
			return;
		}
	}

	/**
	 *  Get button, POV and axis values from each joystick controller, and return a map for updateFlightControls()
	 *  in {@link AbstractController}
	 *  
	 *  @return controls Map
	 */
	@Override
	public Map<FlightControl, Double> calculateControllerValues(Map<FlightControl, Double> controls) {
		// Iterate through all controllers connected
		for (Controller controller : controllerList) {
			
			// Poll controller for data; if disconnected, break out of componentIdentification loop
			if(!controller.poll()) 
				break;
			
			String controllerName = controller.getName().toLowerCase();
			
			// Iterate through all components of the controller.
			for(Component component : controller.getComponents()) {
				Identifier componentIdentifier = component.getIdentifier();
				boolean isPressed = component.getPollData() == 1.0f;

				// Buttons
				if(componentIdentifier.getName().matches("^[0-9]*$")) { // If the component identifier contains only numbers, it is a button
					if(isPressed && controllerName.compareTo("ch flight sim yoke usb") == 0) {
						// Button index
						switch(componentIdentifier.toString()) {
						case "2":
							Events.aileronTrimLeft();
							break;
						case "3":
							Events.aileronTrimRight();
							break;
						case "4":
							Events.retractGear(controls);
							break;
						case "5":
							Events.extendGear(controls);
							break;
						case "6":
							Events.retractFlaps(controls);
							break;
						case "7":
							Events.extendFlaps(controls);
							break;
						case "10":
							Events.elevatorTrimDown();
							break;	
						case "11":
							Events.elevatorTrimUp();
							break;
						}
					} else if(isPressed && controllerName.compareTo("ch throttle quadrant usb") == 0) {
						// Button index
						switch(componentIdentifier.toString()) {
						case "0":
							Events.elevatorTrimDown();
							break;
						case "1":
							Events.elevatorTrimUp();
							break;
						}
					}
					continue;
				}

				// POV Hat Switch - Control elevator and aileron trim 
				if(componentIdentifier == Axis.POV) {
					float povValue = component.getPollData();
					
					if      (Float.compare(povValue, POV.UP)    == 0)
						Events.elevatorTrimDown(); 
					else if (Float.compare(povValue, POV.DOWN)  == 0) 
						Events.elevatorTrimUp();
					else if (Float.compare(povValue, POV.LEFT)  == 0) 
						Events.aileronTrimLeft();
					else if (Float.compare(povValue, POV.RIGHT) == 0)
						Events.aileronTrimRight();
					
					continue;
				}

				// Controller Axes - Read raw controller value, square to reduce its sensitivity, convert to control deflection, and add trim value
				if(component.isAnalog() && controllerName.compareTo("ch flight sim yoke usb") == 0){
					double axisValue = (double)component.getPollData();

					// Y axis (Elevator)
					if(componentIdentifier == Axis.Y) {
						Events.elevator(controls, axisValue);
						continue;
					}
					// X axis (Aileron)
					if(componentIdentifier == Axis.X) {
						Events.aileron(controls, axisValue);
						continue;
					}
				} else if(component.isAnalog() && controllerName.compareTo("ch pro pedals usb") == 0){
					double axisValue = (double)component.getPollData();

					// Y axis (Elevator)
					if(componentIdentifier == Axis.Y) {
						Events.brakeLeft(controls, axisValue);
						continue;
					}
					// X axis (Aileron)
					if(componentIdentifier == Axis.X) {
						Events.brakeRight(controls, axisValue);
						continue;
					}
					// Z axis (Rudder)
					if(componentIdentifier == Axis.Z) {
						Events.rudder(controls, axisValue);
						continue;
					}
				} else if(component.isAnalog() && controllerName.compareTo("ch throttle quadrant usb") == 0){
					double axisValue = (double)component.getPollData();

					// X axis (Throttle 1)
					if(componentIdentifier == Axis.X) {
						Events.throttle1(controls, axisValue);
						continue;
					}
					// Y axis (Throttle 2)
					if(componentIdentifier == Axis.Y) {
						Events.throttle2(controls, axisValue);
						continue;
					}
					// Z axis (Propeller 1)
					if(componentIdentifier == Axis.Z) {
						Events.propeller1(controls, axisValue);
						continue;
					}
					// RZ axis (Propeller 2)
					if(componentIdentifier == Axis.RZ) {
						Events.propeller2(controls, axisValue);
						continue;
					}
					// RY axis (Mixture 1)
					if(componentIdentifier == Axis.RY) {
						Events.mixture1(controls, axisValue);
						continue;
					}
					// RX axis (Mixture 2)
					if(componentIdentifier == Axis.RX) {
						Events.mixture2(controls, axisValue);
						continue;
					}
				}
			}
		}
		
		return limitControls(controls);
	}
}
