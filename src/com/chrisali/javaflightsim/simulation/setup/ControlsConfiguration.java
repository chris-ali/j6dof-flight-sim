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
package com.chrisali.javaflightsim.simulation.setup;

import java.util.HashMap;
import java.util.Map;

import com.chrisali.javaflightsim.simulation.flightcontrols.FlightControl;
import com.chrisali.javaflightsim.simulation.interfaces.Saveable;
import com.chrisali.javaflightsim.simulation.utilities.FileUtilities;
import com.chrisali.javaflightsim.simulation.utilities.SimDirectories;

import net.java.games.input.Component.POV;

/**
 * Contains user-defined key, button (and eventually axis) bindings for each controller for jinput 
 * to control the aircraft in the simulation. Used to (de)serialize the file ControlsConfiguration.json
 * 
 * @author Christopher
 *
 */
public class ControlsConfiguration implements Saveable {

	private Map<String, KeyCommand> keyboardAssignments;
	
	/**
	 * String key is the name of the joystick discovered by jinput
	 */
	private Map<String, Map<String, JoystickAxis>> joystickAxisAssignments;
	
	/**
	 * String key is the name of the joystick discovered by jinput
	 */
	private Map<String, Map<String, KeyCommand>> joystickButtonAssignments;
	
	/**
	 * String key is the name of the joystick discovered by jinput
	 */
	private Map<String, Map<Float, KeyCommand>> joystickHatAssignments;

	/**
	 * Default constructor that sets default controller assignments
	 */
	public ControlsConfiguration() { 
		
		// Keyboard
		keyboardAssignments = new HashMap<String, KeyCommand>(); 
		keyboardAssignments.put("P", KeyCommand.PAUSE_UNPAUSE_SIM);
		keyboardAssignments.put("Q", KeyCommand.EXIT_SIMULATION);
		keyboardAssignments.put("R", KeyCommand.RESET_SIM);
		keyboardAssignments.put("L", KeyCommand.GENERATE_PLOTS);
		keyboardAssignments.put("UP", KeyCommand.ELEVATOR_DOWN);
		keyboardAssignments.put("DOWN", KeyCommand.ELEVATOR_UP);
		keyboardAssignments.put("LEFT", KeyCommand.AILERON_LEFT);
		keyboardAssignments.put("RIGHT", KeyCommand.AILERON_RIGHT);
		keyboardAssignments.put("PAGEUP", KeyCommand.INCREASE_THROTTLE);
		keyboardAssignments.put("PAGEDOWN", KeyCommand.DECREASE_THROTTLE);
		keyboardAssignments.put("F7", KeyCommand.INCREASE_FLAPS);
		keyboardAssignments.put("F6", KeyCommand.DECREASE_FLAPS);
		keyboardAssignments.put("G", KeyCommand.GEAR_UP_DOWN);
		
		// Joystick Axes
		joystickAxisAssignments = new HashMap<String, Map<String, JoystickAxis>>();
		Map<String, JoystickAxis> chYokeAxes = new HashMap<String, JoystickAxis>();
		chYokeAxes.put("X", new JoystickAxis(FlightControl.ELEVATOR));
		chYokeAxes.put("Y", new JoystickAxis(FlightControl.AILERON));
		
		Map<String, JoystickAxis> chPedalsAxes = new HashMap<String, JoystickAxis>();
		chPedalsAxes.put("Y", new JoystickAxis(FlightControl.BRAKE_L));
		chPedalsAxes.put("X", new JoystickAxis(FlightControl.BRAKE_R));
		chPedalsAxes.put("Z", new JoystickAxis(FlightControl.RUDDER));
		
		Map<String, JoystickAxis> chThrottleAxes = new HashMap<String, JoystickAxis>();
		chThrottleAxes.put("X", new JoystickAxis(FlightControl.THROTTLE_1));
		chThrottleAxes.put("Y", new JoystickAxis(FlightControl.THROTTLE_2));
		chThrottleAxes.put("Z", new JoystickAxis(FlightControl.PROPELLER_1));
		chThrottleAxes.put("RX", new JoystickAxis(FlightControl.PROPELLER_2));
		chThrottleAxes.put("RY", new JoystickAxis(FlightControl.MIXTURE_1));
		chThrottleAxes.put("RZ", new JoystickAxis(FlightControl.MIXTURE_2));
		
		joystickAxisAssignments.put("ch flight sim yoke usb", chYokeAxes);
		joystickAxisAssignments.put("ch pro pedals usb", chPedalsAxes);
		joystickAxisAssignments.put("ch throttle quadrant usb", chThrottleAxes);
		
		// Joystick POV Hat
		joystickHatAssignments = new HashMap<String, Map<Float, KeyCommand>>();
		Map<Float, KeyCommand> chYokeHat = new HashMap<Float, KeyCommand>();
		chYokeHat.put(POV.UP, KeyCommand.ELEVATOR_TRIM_DOWN);
		chYokeHat.put(POV.DOWN, KeyCommand.ELEVATOR_TRIM_UP);
		chYokeHat.put(POV.LEFT, KeyCommand.AILERON_TRIM_LEFT);
		chYokeHat.put(POV.RIGHT, KeyCommand.AILERON_TRIM_RIGHT);
		
		joystickHatAssignments.put("ch flight sim yoke usb", chYokeHat);
		
		// Joystick Buttons
		joystickButtonAssignments = new HashMap<String, Map<String, KeyCommand>>();
		Map<String, KeyCommand> chYokeButtons = new HashMap<String, KeyCommand>();
		chYokeButtons.put("2", KeyCommand.AILERON_TRIM_LEFT);
		chYokeButtons.put("3", KeyCommand.AILERON_TRIM_RIGHT);
		chYokeButtons.put("4", KeyCommand.GEAR_UP);
		chYokeButtons.put("5", KeyCommand.GEAR_DOWN);
		chYokeButtons.put("6", KeyCommand.DECREASE_FLAPS);
		chYokeButtons.put("7", KeyCommand.INCREASE_FLAPS);
		chYokeButtons.put("10", KeyCommand.ELEVATOR_TRIM_DOWN);
		chYokeButtons.put("11", KeyCommand.ELEVATOR_TRIM_UP);
		
		Map<String, KeyCommand> chThrottleButtons = new HashMap<String, KeyCommand>();
		chThrottleButtons.put("0", KeyCommand.ELEVATOR_TRIM_DOWN);
		chThrottleButtons.put("1", KeyCommand.ELEVATOR_TRIM_UP);
		
		joystickButtonAssignments.put("ch flight sim yoke usb", chYokeButtons);
		joystickButtonAssignments.put("ch throttle quadrant usb", chThrottleButtons);
	}
	
	@Override
	public void save() {
		FileUtilities.serializeJson(SimDirectories.SIM_CONFIG.toString(), this.getClass().getSimpleName(), this.getClass());		
	}

	public Map<String, KeyCommand> getKeyboardAssignments() { return keyboardAssignments; }

	public void setKeyboardAssignments(Map<String, KeyCommand> keyboardAssignments) { this.keyboardAssignments = keyboardAssignments; }

	public Map<String, Map<String, JoystickAxis>> getJoystickAxisAssignments() { return joystickAxisAssignments; }

	public void setJoystickAxisAssignments(Map<String, Map<String, JoystickAxis>> joystickAxisAssignments) { this.joystickAxisAssignments = joystickAxisAssignments; }

	public Map<String, Map<String, KeyCommand>> getJoystickButtonAssignments() { return joystickButtonAssignments;	}

	public void setJoystickButtonAssignments(Map<String, Map<String, KeyCommand>> joystickButtonAssignments) { this.joystickButtonAssignments = joystickButtonAssignments;	}

	public Map<String, Map<Float, KeyCommand>> getJoystickHatAssignments() { return joystickHatAssignments;}

	public void setJoystickHatAssignments(Map<String, Map<Float, KeyCommand>> joystickHatAssignments) { this.joystickHatAssignments = joystickHatAssignments;	}
}
