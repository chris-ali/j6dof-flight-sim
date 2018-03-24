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

import com.chrisali.javaflightsim.simulation.flightcontrols.ControlParameterActuator;
import com.chrisali.javaflightsim.simulation.flightcontrols.FlightControl;
import com.chrisali.javaflightsim.simulation.flightcontrols.FlightControlsState;
import com.chrisali.javaflightsim.simulation.flightcontrols.FlightControlsStateManager;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Component.Identifier.Axis;

/**
 * Visitor that {@link FlightControlsStateManager} calls to poll joystick data via jInput and send to a {@link ControlParameterActuator}
 * object, which then updates the {@link FlightControlsState} object as needed
 * 
 * @author Christopher
 *
 */
public class MouseVisitor implements InputDeviceVisitor {
	
	private ControlParameterActuator actuator;

	// Mouse axes are measured relative to the stopped position; temp fields store the control deflection, 
	// and the mouse axis value is added to these
	private float tempElev  = 0.0f;
	private float tempAil   = 0.0f;
	private float tempThrot = 0.0f;
	
	// Add these trim values to getControlDeflection method call to emulate trim deflections
	private float trimElevator = 0.0f;
	private float trimAileron  = 0.0f;
	
	public MouseVisitor(FlightControlsState controlsState, ControlParameterActuator actuator) {
		this.actuator = actuator;
		
		// Get initial trim values from initial values in controls EnumMap (rad)
		trimElevator = (float)controlsState.get(FlightControl.ELEVATOR);
		trimAileron = (float)controlsState.get(FlightControl.AILERON);
	}

	@Override
	public boolean canHandleDevice(Controller device) {
		return device.getType() == Controller.Type.MOUSE;
	}
	
	@Override
	public void handleDeviceInput(Controller device) {
		if(!device.poll()) 
			return;
		
		for(Component component : device.getComponents()) {
			Identifier componentIdentifier = component.getIdentifier();
			
			// Buttons
			if(componentIdentifier.getName().matches("^[0-9]*$")) { // If the component identifier contains only numbers, it is a button
				continue; // Button index (nothing implemented yet)
			}
			
			// Mouse Axes - Read raw mouse relative value, add relative value to temp* variable, and add trim value
			// to control deflection
			if(component.isRelative()) {
				float axisValue = component.getPollData()/10;
				
				// Y axis (Elevator)
				if(componentIdentifier == Axis.Y) {
					if(axisValue != 0) {
						tempElev += axisValue;
						actuator.handleParameterChange(FlightControl.ELEVATOR, -(tempElev+trimElevator));
					}
					continue;
				}
				// X axis (Aileron)
				if(componentIdentifier == Axis.X) {
					if(axisValue != 0) {
						tempAil += axisValue;
						actuator.handleParameterChange(FlightControl.AILERON, -(tempAil+trimAileron));
					}
					continue;
				}
				// Z axis (Throttle)
				if(componentIdentifier == Axis.Z) {
					if(axisValue != 0) {
						tempThrot += axisValue;
						actuator.handleParameterChange(FlightControl.THROTTLE_1, tempThrot*250);
						actuator.handleParameterChange(FlightControl.THROTTLE_2, tempThrot*250);
					}
					continue;
				}
			}
		}
	}
}