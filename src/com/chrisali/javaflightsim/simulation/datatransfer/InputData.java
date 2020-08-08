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
package com.chrisali.javaflightsim.simulation.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chrisali.javaflightsim.simulation.flightcontrols.ExternalFlightControlsStateManager;
import com.chrisali.javaflightsim.simulation.flightcontrols.FlightControl;
import com.chrisali.javaflightsim.simulation.setup.KeyCommand;

/**
 *	Container of input data from external keyboards or controllers 
 *  into the simulation using {@link ExternalFlightControlsStateManager}. Relatively thread safe.
 */
public class InputData {

	/**
	 * Map of Joystick axis commands and their respective values
	 */
	private Map<FlightControl, Float> joystickInputs = Collections.synchronizedMap(new HashMap<FlightControl, Float>());
	
	/**
	 * List of keys and/or buttons that have been pressed since last polled
	 */ 
	private List<KeyCommand> keyCommands = Collections.synchronizedList(new ArrayList<KeyCommand>());

	public InputData() {}

	/**
	 * Thread safely adds new KeyCommand that has been pressed since last polled
	 * 
	 * @param command
	 */
	public void addKeyPressed(KeyCommand command) {
		synchronized (keyCommands) {
			keyCommands.add(command);
		}
	}

	/**
	 * Clears the collection of keys pressed for the next polling
	 */
	public void clearKeysPressed() {
		synchronized (keyCommands) {
			keyCommands.clear();
		}
	}

	/**
	 * Thread safely assigns values of map of joystick inputs
	 * 
	 * @param axis
	 * @param value
	 */
	public void updateJoystickInputs(FlightControl axis, float value) {
		synchronized (joystickInputs) {
			joystickInputs.put(axis, value);
		}
	}

	public Map<FlightControl, Float> getJoystickInputs() { 
		return joystickInputs; 
	}

	public List<KeyCommand> getKeyCommands() { 
		return keyCommands; 
	}
}
