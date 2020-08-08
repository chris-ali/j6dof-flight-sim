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
package com.chrisali.javaflightsim.simulation.setup;

import java.util.Map;

import com.chrisali.javaflightsim.interfaces.Saveable;
import com.chrisali.javaflightsim.simulation.utilities.FileUtilities;
import com.chrisali.javaflightsim.simulation.utilities.SimDirectories;

/**
 * Contains user-defined key, button (and eventually axis) bindings for each controller for jinput 
 * to control the aircraft in the simulation. Used to (de)serialize the file ControlsConfiguration.json
 * 
 * @author Christopher
 *
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
		 * String key is the name of the joystick axis
		 */
		private Map<String, JoystickAxis> axisAssignments;
		
		/**
		 * String key is the name of the joystick button
		 */
		private Map<String, KeyCommand> buttonAssignments;
		
		/**
		 * Float key is the direction of the joystick hat
		 */
		private Map<Float, KeyCommand> hatAssignments;
		
		public JoystickAssignments() {}
		
		public Map<String, JoystickAxis> getAxisAssignments() { return axisAssignments; }
		
		public void setAxisAssignments(Map<String, JoystickAxis> joystickAxisAssignments) { this.axisAssignments = joystickAxisAssignments; }
		
		public Map<String, KeyCommand> getButtonAssignments() { return buttonAssignments; }
		
		public void setButtonAssignments(Map<String, KeyCommand> joystickButtonAssignments) { this.buttonAssignments = joystickButtonAssignments; }
		
		public Map<Float, KeyCommand> getHatAssignments() { return hatAssignments; }
		
		public void setHatAssignments(Map<Float, KeyCommand> joystickHatAssignments) { this.hatAssignments = joystickHatAssignments; }
	}
}
