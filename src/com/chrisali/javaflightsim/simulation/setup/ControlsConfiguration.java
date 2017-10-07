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

import net.java.games.input.Component.Identifier.Axis;
import net.java.games.input.Component.Identifier.Button;
import net.java.games.input.Component.Identifier.Key;
import net.java.games.input.Component.POV;

/**
 * Contains user-defined key, button (and eventually axis) bindings for each controller for jinput 
 * to control the aircraft in the simulation. Used to (de)serialize the file ControlsConfiguration.json
 * 
 * @author Christopher
 *
 */
public class ControlsConfiguration implements Saveable {

	private Map<Key, KeyCommand> keyboardAssignments;
	
	/**
	 * String key is the name of the joystick discovered by jinput
	 */
	private Map<String, Map<FlightControl, JoystickAxis>> joystickAxisAssignments;
	
	/**
	 * String key is the name of the joystick discovered by jinput
	 */
	private Map<String, Map<Button, KeyCommand>> joystickButtonAssignments;
	
	/**
	 * String key is the name of the joystick discovered by jinput
	 */
	private Map<String, Map<Float, KeyCommand>> joystickHatAssignments;

	/**
	 * Default constructor that sets default controller assignments
	 */
	public ControlsConfiguration() { 
		
		// Keyboard
		keyboardAssignments = new HashMap<Key, KeyCommand>(); 
		keyboardAssignments.put(Key.P, KeyCommand.PAUSE_UNPAUSE_SIM);
		keyboardAssignments.put(Key.Q, KeyCommand.EXIT_SIMULATION);
		keyboardAssignments.put(Key.R, KeyCommand.RESET_SIM);
		keyboardAssignments.put(Key.L, KeyCommand.GENERATE_PLOTS);
		keyboardAssignments.put(Key.UP, KeyCommand.ELEVATOR_DOWN);
		keyboardAssignments.put(Key.DOWN, KeyCommand.ELEVATOR_UP);
		keyboardAssignments.put(Key.LEFT, KeyCommand.AILERON_LEFT);
		keyboardAssignments.put(Key.RIGHT, KeyCommand.AILERON_RIGHT);
		keyboardAssignments.put(Key.PAGEUP, KeyCommand.INCREASE_THROTTLE);
		keyboardAssignments.put(Key.PAGEDOWN, KeyCommand.DECREASE_THROTTLE);
		keyboardAssignments.put(Key.F7, KeyCommand.INCREASE_FLAPS);
		keyboardAssignments.put(Key.F6, KeyCommand.DECREASE_FLAPS);
		keyboardAssignments.put(Key.G, KeyCommand.GEAR_UP_DOWN);
		
		// Joystick Axes
		joystickAxisAssignments = new HashMap<String, Map<FlightControl, JoystickAxis>>();
		Map<FlightControl, JoystickAxis> chYokeAxes = new HashMap<FlightControl, JoystickAxis>();
		chYokeAxes.put(FlightControl.ELEVATOR, new JoystickAxis(Axis.Y));
		chYokeAxes.put(FlightControl.AILERON, new JoystickAxis(Axis.X));
		
		Map<FlightControl, JoystickAxis> chPedalsAxes = new HashMap<FlightControl, JoystickAxis>();
		chPedalsAxes.put(FlightControl.BRAKE_L, new JoystickAxis(Axis.Y));
		chPedalsAxes.put(FlightControl.BRAKE_R, new JoystickAxis(Axis.X));
		chPedalsAxes.put(FlightControl.RUDDER, new JoystickAxis(Axis.Z));
		
		Map<FlightControl, JoystickAxis> chThrottleAxes = new HashMap<FlightControl, JoystickAxis>();
		chThrottleAxes.put(FlightControl.THROTTLE_1, new JoystickAxis(Axis.X));
		chThrottleAxes.put(FlightControl.THROTTLE_2, new JoystickAxis(Axis.Y));
		chThrottleAxes.put(FlightControl.PROPELLER_1, new JoystickAxis(Axis.Z));
		chThrottleAxes.put(FlightControl.PROPELLER_2, new JoystickAxis(Axis.RZ));
		chThrottleAxes.put(FlightControl.MIXTURE_1, new JoystickAxis(Axis.RY));
		chThrottleAxes.put(FlightControl.MIXTURE_2, new JoystickAxis(Axis.RX));
		
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
		joystickButtonAssignments = new HashMap<String, Map<Button, KeyCommand>>();
		Map<Button, KeyCommand> chYokeButtons = new HashMap<Button, KeyCommand>();
		chYokeButtons.put(Button._2, KeyCommand.AILERON_TRIM_LEFT);
		chYokeButtons.put(Button._3, KeyCommand.AILERON_TRIM_RIGHT);
		chYokeButtons.put(Button._4, KeyCommand.GEAR_UP);
		chYokeButtons.put(Button._5, KeyCommand.GEAR_DOWN);
		chYokeButtons.put(Button._6, KeyCommand.DECREASE_FLAPS);
		chYokeButtons.put(Button._7, KeyCommand.INCREASE_FLAPS);
		chYokeButtons.put(Button._10, KeyCommand.ELEVATOR_TRIM_DOWN);
		chYokeButtons.put(Button._11, KeyCommand.ELEVATOR_TRIM_UP);
		
		Map<Button, KeyCommand> chThrottleButtons = new HashMap<Button, KeyCommand>();
		chThrottleButtons.put(Button._0, KeyCommand.ELEVATOR_TRIM_DOWN);
		chThrottleButtons.put(Button._1, KeyCommand.ELEVATOR_TRIM_UP);
		
		joystickButtonAssignments.put("ch flight sim yoke usb", chYokeButtons);
		joystickButtonAssignments.put("ch throttle quadrant usb", chThrottleButtons);
	}
	
	@Override
	public void save() {
		FileUtilities.serializeJson(SimDirectories.SIM_CONFIG.toString(), this.getClass().getSimpleName(), this.getClass());		
	}

	public Map<Key, KeyCommand> getKeyboardAssignments() { return keyboardAssignments; }

	public void setKeyboardAssignments(Map<Key, KeyCommand> keyboardAssignments) { this.keyboardAssignments = keyboardAssignments; }

	public Map<String, Map<FlightControl, JoystickAxis>> getJoystickAxisAssignments() { return joystickAxisAssignments; }

	public void setJoystickAxisAssignments(Map<String, Map<FlightControl, JoystickAxis>> joystickAxisAssignments) { this.joystickAxisAssignments = joystickAxisAssignments; }

	public Map<String, Map<Button, KeyCommand>> getJoystickButtonAssignments() { return joystickButtonAssignments;	}

	public void setJoystickButtonAssignments(Map<String, Map<Button, KeyCommand>> joystickButtonAssignments) { this.joystickButtonAssignments = joystickButtonAssignments;	}

	public Map<String, Map<Float, KeyCommand>> getJoystickHatAssignments() { return joystickHatAssignments;}

	public void setJoystickHatAssignments(Map<String, Map<Float, KeyCommand>> joystickHatAssignments) { this.joystickHatAssignments = joystickHatAssignments;	}
}
