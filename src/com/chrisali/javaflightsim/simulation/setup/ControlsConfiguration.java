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
package com.chrisali.javaflightsim.simulation.setup;

import java.util.Map;

import com.chrisali.javaflightsim.interfaces.Saveable;
import com.chrisali.javaflightsim.simulation.utilities.FileUtilities;
import com.chrisali.javaflightsim.simulation.utilities.SimDirectories;

/**
 * Contains user-defined key, button (and eventually axis) bindings for each controller for jinput 
 * to control the aircraft in the simulation. Used to (de)serialize the file ControlsConfiguration.json
 * 
 * Uses GLFW integer IDs to handle key/axis/button/hat assignments
 * 
 * @author Christopher
 * @see https://www.glfw.org/docs/latest/group__input.html
 */
public class ControlsConfiguration implements Saveable {

	/**
	 * Key is the int value representing the keyboard key pressed
	 */
	private Map<Integer, KeyCommand> keyboardAssignments;
	
	/**
	 * Key is the lowecase name of the joystick
	 */
	private Map<String, JoystickAssignments> joystickAssignments;

	public ControlsConfiguration() {}
	
	@Override
	public void save() {
		FileUtilities.serializeJson(SimDirectories.SIM_CONFIG.toString(), this.getClass().getSimpleName(), this);		
	}

	public Map<Integer, KeyCommand> getKeyboardAssignments() { return keyboardAssignments; }

	public void setKeyboardAssignments(Map<Integer, KeyCommand> keyboardAssignments) { this.keyboardAssignments = keyboardAssignments; }
	
	public Map<String, JoystickAssignments> getJoystickAssignments() { return joystickAssignments; }

	public void setJoystickAssignments(Map<String, JoystickAssignments> joystickAssignments) { this.joystickAssignments = joystickAssignments; }

	/**
	 * Wrapper class to house axis, hat and button assignments for each controller connected to the computer
	 * 
	 * @author Christopher
	 *
	 */
	public static class JoystickAssignments {

		/**
		 * Integer key is the name of the joystick axis
		 */
		private Map<Integer, JoystickAxis> axisAssignments;
		
		/**
		 * Integer key is the name of the joystick button
		 */
		private Map<Integer, KeyCommand> buttonAssignments;
		
		/**
		 * Integer key is the direction of the joystick hat
		 */
		private Map<Integer, KeyCommand> hatAssignments;
		
		public JoystickAssignments() {}
		
		public Map<Integer, JoystickAxis> getAxisAssignments() { return axisAssignments; }
		
		public void setAxisAssignments(Map<Integer, JoystickAxis> joystickAxisAssignments) { this.axisAssignments = joystickAxisAssignments; }
		
		public Map<Integer, KeyCommand> getButtonAssignments() { return buttonAssignments; }
		
		public void setButtonAssignments(Map<Integer, KeyCommand> joystickButtonAssignments) { this.buttonAssignments = joystickButtonAssignments; }
		
		public Map<Integer, KeyCommand> getHatAssignments() { return hatAssignments; }
		
		public void setHatAssignments(Map<Integer, KeyCommand> joystickHatAssignments) { this.hatAssignments = joystickHatAssignments; }
	}
}
