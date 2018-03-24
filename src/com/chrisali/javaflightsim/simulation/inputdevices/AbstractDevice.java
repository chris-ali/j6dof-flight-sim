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

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chrisali.javaflightsim.simulation.flightcontrols.ControlParameterActuator;

import net.java.games.input.Controller;

/**
 * Abstract representation of Human Interface Devices that are polled for raw data before being converted into
 * contol commands that a {@link ControlParameterActuator} must handle 
 */
public abstract class AbstractDevice {
	
	protected static final Logger logger = LogManager.getLogger(AbstractDevice.class);
	
	protected List<Controller> controlDeviceList;
	
	public abstract void searchForControlDevices();
	
	/**
	 * Polls a capable {@link InputDeviceVisitor} and then commands that device to
	 * handle the poll data results 
	 * 
	 * @param visitor
	 */
	public void collectControlDeviceValues(InputDeviceVisitor visitor) {
		for (Controller device : controlDeviceList) {
			if (visitor.canHandleDevice(device)) {
				if (!device.poll()) continue;
				
				visitor.handleDeviceInput(device);
			}
		}
	}
}
