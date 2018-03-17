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
package com.chrisali.javaflightsim.simulation.inputdevices;

import java.util.Map;

import com.chrisali.javaflightsim.simulation.flightcontrols.ControlParameterActuator;
import com.chrisali.javaflightsim.simulation.flightcontrols.FlightControlsState;
import com.chrisali.javaflightsim.simulation.flightcontrols.FlightControlsStateManager;
import com.chrisali.javaflightsim.simulation.setup.JoystickAxis;
import com.chrisali.javaflightsim.simulation.setup.KeyCommand;
import com.chrisali.javaflightsim.simulation.setup.ControlsConfiguration.JoystickAssignments;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.Event;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Component.Identifier.Axis;

/**
 * "Visitor" object that {@link FlightControlsStateManager} calls to poll joystick data via jInput and send to a {@link ControlParameterActuator}
 * object, which then updates the {@link FlightControlsState} object as needed
 * 
 * @author Christopher
 *
 */
public class JoystickHandler implements InputDeviceHandler {
	
	private Map<String, JoystickAssignments> joystickAssignments;
	private ControlParameterActuator actuator;
	
	public JoystickHandler(Map<String, JoystickAssignments> joystickAssignments, ControlParameterActuator actuator) {
		this.joystickAssignments = joystickAssignments;
		this.actuator = actuator;
	}

	@Override
	public boolean canHandleDevice(Controller device) {
		return joystickAssignments.get(device.getName()) != null;
	}

	@Override
	public void handleDeviceInput(Controller device) {
		if (!canHandleDevice(device))
            return;
        
        JoystickAssignments assignments = joystickAssignments.get(device.getName());
        Map<String, JoystickAxis> axisAssignments 	= assignments.getAxisAssignments();
        Map<String, KeyCommand>   buttonAssignments = assignments.getButtonAssignments();
        Map<Float, KeyCommand>    hatAssignments  	= assignments.getHatAssignments();

		if(device.poll()) {	
			Event event = new Event();
			
			while (device.getEventQueue().getNextEvent(event)) {
				Component component = event.getComponent();
				
				Identifier componentIdentifier = component.getIdentifier();
				String componentName = componentIdentifier.getName();
				float pollValue = component.getPollData();
				
				// Buttons
				if(buttonAssignments != null && componentIdentifier.getName().matches("^[0-9]*$")) { // If the component name contains only numbers, it is a button
					KeyCommand command = buttonAssignments.get(componentName);
					
					if(command != null)					
						actuator.handleParameterChange(command, pollValue);					
					
					continue;
				}
				
				// Hat Switch
				if(hatAssignments != null && componentIdentifier == Axis.POV) {										
					KeyCommand command = hatAssignments.get(pollValue);
					
					if(command != null)					
						actuator.handleParameterChange(command, pollValue);					
					
					continue;
				}
				
				// Joystick Axes
				if(axisAssignments != null){
					JoystickAxis axis = axisAssignments.get(componentName);
					
					if(axis != null)					
						actuator.handleParameterChange(axis.getAxisAssignment(), pollValue);				
					
					continue;
				}
			}
		}	
	}
}
