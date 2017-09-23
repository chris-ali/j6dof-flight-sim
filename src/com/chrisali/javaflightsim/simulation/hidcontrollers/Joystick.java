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

import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Component.Identifier.Axis;
import net.java.games.input.Component.POV;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

/**
 * The Joystick object uses JInput to integrate joystick functionality into the simulation.
 * It works by generating an ArrayList of joysticks, gamepads and steering wheels connected
 * to the computer, polling each one's active components (buttons, axes, POV hat), using 
 * the polled data to calculate control deflections, and assigning these to each respective key 
 * in the controls EnumMap. These deflections are limited by the constants defined in the 
 * {@link FlightControl}. Aileron and Elevator trim are handled by the POV hat switch, and all
 * throttles are controlled by the throttle slider.
 * @see AbstractController
 */
public class Joystick extends AbstractController {
	
	/**
	 *  Constructor for Joystick class creates list of controllers using searchForControllers()
	 * @param controls
	 */
	public Joystick(Map<FlightControl, Double> controls) {
		this.controllerList = new ArrayList<>();
		
		// Get initial trim values from initial values in controls EnumMap (rad)
		trimElevator = controls.get(FlightControl.ELEVATOR);
		trimAileron  = controls.get(FlightControl.AILERON);
		trimRudder   = controls.get(FlightControl.RUDDER);
		
		logger.debug("Setting up joystick...");
		
		searchForControllers();
	}
	
	/**
	 * Search for and add controllers of type Controller.Type.STICK or Controller.Type.GAMEPAD
	 * to controllerList
	 */ 
	@Override
	public void searchForControllers() {
		Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
		
		for(Controller controller : controllers){
			if (controller.getType() == Controller.Type.STICK || controller.getType() == Controller.Type.GAMEPAD)
				controllerList.add(controller);
		}
		
		// If no joysticks available, exit function
		if (controllerList.isEmpty()) {
			logger.error("No joysticks found!");
			return;
		}
	}

	/**
	 *  Get button, POV and axis values from joystick(s), and return a Map for updateFlightControls() 
	 *  in {@link AbstractController}
	 *  @return controls Map
	 */
	@Override
	protected Map<FlightControl, Double> calculateControllerValues(Map<FlightControl, Double> controls) {
		// Iterate through all controllers connected
		for (Controller controller : controllerList) {
			
			// Poll controller for data; if disconnected, break out of componentIdentification loop
			if(!controller.poll()) 
				break;
			
			// Iterate through all components of the controller.
			for(Component component : controller.getComponents()) {
				Identifier componentIdentifier = component.getIdentifier();

				// Buttons
				if(componentIdentifier.getName().matches("^[0-9]*$")) { // If the component identifier contains only numbers, it is a button
					if(component.getPollData() == 1.0f) {
						switch(componentIdentifier.toString()) {
						case "0":
							controls.put(FlightControl.BRAKE_L, negativeSquare(FlightControl.BRAKE_L.getMaximum()));
							controls.put(FlightControl.BRAKE_R, negativeSquare(FlightControl.BRAKE_R.getMaximum()));
							break;
						case "4":
							controls.put(FlightControl.GEAR, FlightControl.GEAR.getMaximum());
							break;
						case "5":
							controls.put(FlightControl.GEAR, FlightControl.GEAR.getMinimum());
							break;
						case "6":
							if (flaps >= FlightControl.FLAPS.getMinimum())	controls.put(FlightControl.FLAPS, (flaps -= getDeflectionRate(FlightControl.FLAPS)));
							break;
						case "7":
							if (flaps <= FlightControl.FLAPS.getMaximum()) controls.put(FlightControl.FLAPS, (flaps += getDeflectionRate(FlightControl.FLAPS)));
							break;
						}
					}
					continue; // Go to next component
				}

				// POV Hat Switch - Control elevator and aileron trim 
				if(componentIdentifier == Axis.POV) {
					float povValue = component.getPollData();
					
					if      (Float.compare(povValue, POV.UP)    == 0 & trimElevator <= FlightControl.ELEVATOR.getMaximum())
						trimElevator += getDeflectionRate(FlightControl.ELEVATOR)/10; 
					else if (Float.compare(povValue, POV.DOWN)  == 0 & trimElevator >= FlightControl.ELEVATOR.getMinimum()) 
						trimElevator -= getDeflectionRate(FlightControl.ELEVATOR)/10;
					else if (Float.compare(povValue, POV.LEFT)  == 0 & trimAileron  >= FlightControl.AILERON.getMinimum()) 
						trimAileron  += getDeflectionRate(FlightControl.AILERON)/20;
					else if (Float.compare(povValue, POV.RIGHT) == 0 & trimAileron  <= FlightControl.AILERON.getMaximum())
						trimAileron  -= getDeflectionRate(FlightControl.AILERON)/20;
					
					continue; // Go to next component
				}

				// Joystick Axes - Read raw joystick value, square to reduce its sensitivity, convert to control deflection, and add trim value
				if(component.isAnalog()){
					double axisValue = (double)component.getPollData();

					// Y axis (Elevator)
					if(componentIdentifier == Axis.Y) {
						controls.put(FlightControl.ELEVATOR, 
								 	 calculateDeflection(FlightControl.ELEVATOR, 
								 			 		   	  		negativeSquare(axisValue))+trimElevator);
						continue; // Go to next component
					}
					// X axis (Aileron)
					if(componentIdentifier == Axis.X) {
						controls.put(FlightControl.AILERON, 
									 calculateDeflection(FlightControl.AILERON, 
											 					negativeSquare(axisValue))+trimAileron);
						continue; // Go to next component
					}
					// Z axis (Rudder)
					if(componentIdentifier == Axis.RZ) {
						controls.put(FlightControl.RUDDER, 
								 	 calculateDeflection(FlightControl.RUDDER, 
								 			 					negativeSquare(axisValue))+trimRudder);
						continue; // Go to next component
					}
					// Slider axis (Throttle)
					if(componentIdentifier == Axis.SLIDER) {
						controls.put(FlightControl.THROTTLE_1,-(axisValue-1)/2);
						controls.put(FlightControl.THROTTLE_2,-(axisValue-1)/2);
						continue; // Go to next component
					}
				}
			}
		}
		return controls;
	}
}
