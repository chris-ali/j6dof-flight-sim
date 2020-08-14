/*******************************************************************************
 * Copyright (C) 2016-2020 Christopher Ali
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

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chrisali.javaflightsim.simulation.flightcontrols.FlightControlsStateManager;
import com.chrisali.javaflightsim.simulation.flightcontrols.FlightControl;
import com.chrisali.javaflightsim.simulation.setup.KeyCommand;

import org.lwjgl.BufferUtils;

/**
 *	Container of input data from external keyboards, mice or controllers 
 *  into the simulation using {@link FlightControlsStateManager}. Relatively thread safe.
 */
public class InputData {

	private DoubleBuffer mouseXPos;
	private DoubleBuffer mouseYPos;
	private DoubleBuffer mouseScrollOffset;

	/**
	 * List of mouse buttons that have been pressed since last polled
	 */ 
	private List<Integer> mouseButtonsPressed = Collections.synchronizedList(new ArrayList<Integer>());  

	/**
	 * Map of Joystick axis commands and their respective values
	 */
	private Map<FlightControl, Float> joystickInputs = Collections.synchronizedMap(new HashMap<FlightControl, Float>());
	
	/**
	 * List of keys and/or buttons that have been pressed since last polled
	 */ 
	private List<KeyCommand> keyCommands = Collections.synchronizedList(new ArrayList<KeyCommand>());

	public InputData() {
		mouseXPos = BufferUtils.createDoubleBuffer(1);
		mouseYPos = BufferUtils.createDoubleBuffer(1);
		mouseScrollOffset = BufferUtils.createDoubleBuffer(1);
	}

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
	 * Thread safely adds new mouse button that has been pressed since last polled
	 * 
	 * @see GLFW_MOUSE_BUTTON_x
	 * @param command
	 */
	public void addMouseButtonPressed(int mouseButton) {
		synchronized (mouseButtonsPressed) {
			mouseButtonsPressed.add(mouseButton);
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
	 * Clears the collection of mouse buttons pressed for the next polling
	 */
	public void clearMouseButtonsPressed() {
		synchronized (mouseButtonsPressed) {
			mouseButtonsPressed.clear();
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

	/**
	 * @return if the provided key command enum has been presed since last poll
	 * @param command 
	 */
	public boolean isCommandPressed(KeyCommand command) {
		return keyCommands.contains(command);
	}

	public double getMouseXPos() {
		return mouseXPos.get(0);
	}

	public void setMouseXPos(double mouseXPos) {
		this.mouseXPos.put(0, mouseXPos);
	}

	public double getMouseYPos() {
		return mouseYPos.get(0);
	}

	public void setMouseYPos(double mouseYPos) {
		this.mouseYPos.put(0, mouseYPos);
	}

	public double getMouseScrollOffset() {
		return mouseScrollOffset.get(0);
	}

	public void setMouseScrollOffset(double scrollOffset) {
		mouseScrollOffset.put(0, scrollOffset);
	}

	public List<Integer> getMouseButtonsPressed() {
		return mouseButtonsPressed;
	}

	/**
	 * @return if the provided mouse button ID has been presed since last poll
	 * @param mouseButtonID GLFW mouse button ID 
	 */
	public boolean isMousePressed(int mouseButtonID) {
		return mouseButtonsPressed.contains(mouseButtonID);
	}
}
