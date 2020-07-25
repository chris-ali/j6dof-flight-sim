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

import com.chrisali.javaflightsim.interfaces.Steppable;
import com.chrisali.javaflightsim.simulation.flightcontrols.FlightControl;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.setup.KeyCommand;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *	Interacts with any registered listeners to pass input data from keyboards or controllers 
 *  into the simulation {@link Integrate6DOFEquations}. Relatively thread safe.
 */
public class InputData implements Steppable {
	
	private static final Logger logger = LogManager.getLogger(InputData.class);
	
	/**
	 * Map of Joystick axis commands and their respective values
	 */
	private Map<FlightControl, Float> joystickInputs = Collections.synchronizedMap(new HashMap<FlightControl, Float>());
	
	/**
	 * List of keys and/or buttons that have been pressed since last polled
	 */ 
	private List<KeyCommand> keyCommands = Collections.synchronizedList(new ArrayList<KeyCommand>());

	private List<InputDataListener> dataListenerList;

	public InputData() {
		this.dataListenerList = new ArrayList<>();
	}

	/**
	 * Thread safely assigns values needed for list of KeyCommands   
	 * 
	 * @param command
	 */
	public void updateKeyCommands(KeyCommand command) {
		synchronized (keyCommands) {
			keyCommands.add(command);
		}
	}

	/**
	 * Thread safely assigns values needed for map of Joystick Inputs
	 * 
	 * @param axis
	 * @param value
	 */
	public void updateJoystickInputs(FlightControl axis, float value) {
		synchronized (joystickInputs) {
			joystickInputs.put(axis, value);
		}
	}
		
	@Override
	public boolean canStepNow(int simTimeMS) {
		return simTimeMS % 1 == 0;
	}

	@Override
	public void step() {
		try {
			fireDataArrived();

			synchronized (keyCommands) {
				keyCommands.clear();
			}

			synchronized (joystickInputs) {
				joystickInputs.clear();
			}
		} catch (Exception e) {
			logger.error("Exception encountered while stepping input data!", e);
		}
	}
	
	/**
	 * Adds a listener that implements {@link InputDataListener} to a list of listeners that can listen
	 * to {@link InputData} 
	 * 
	 * @param dataListener
	 */
	public void addListener(InputDataListener dataListener) {
		logger.debug("Adding input data listener: " + dataListener.getClass());
		dataListenerList.add(dataListener);
	}
	
	/**
	 * Lets registered listeners know that input data has arrived from so that they can use it as needed
	 */
	private void fireDataArrived() {
		for (InputDataListener listener : dataListenerList) {
			if(listener != null) 
				listener.onInputDataReceived(this);
		}
	}

	public Map<FlightControl, Float> getJoystickInputs() { return joystickInputs; }

	public List<KeyCommand> getKeyCommands() { return keyCommands; }
}
