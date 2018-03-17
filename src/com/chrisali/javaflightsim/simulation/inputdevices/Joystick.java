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

import java.util.ArrayList;

import com.chrisali.javaflightsim.simulation.flightcontrols.ControlParameterActuator;

import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

/**
 * Joystick HID that is polled for raw data before being converted into contol commands 
 * that a {@link ControlParameterActuator} must handle 
 */
public class Joystick extends AbstractDevice {
	
	/**
	 *  Constructor for Joystick class creates list of controllers using searchForControllers()
	 */
	public Joystick() {
		logger.debug("Setting up joystick...");
		
		searchForControlDevices();
	}
	
	/**
	 * Search for and add controllers of type Controller.Type.STICK or Controller.Type.GAMEPAD
	 * to controlDeviceList
	 */ 
	@Override
	public void searchForControlDevices() {
		Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
		controlDeviceList = new ArrayList<>();
		
		for(Controller controller : controllers){
			if (controller.getType() == Controller.Type.STICK || controller.getType() == Controller.Type.GAMEPAD) {
				logger.debug("Found a joystick: " + controller.getName());
				controlDeviceList.add(controller);
			}
		}
		
		if (controlDeviceList.isEmpty()) {
			logger.error("No joysticks found!");
			return;
		}
	}
}
