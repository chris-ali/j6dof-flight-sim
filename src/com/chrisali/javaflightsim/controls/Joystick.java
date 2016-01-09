package com.chrisali.javaflightsim.controls;

import java.util.ArrayList;
import java.util.EnumMap;
import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Component.POV;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

/*
 * The Joystick object uses JInput to integrate joystick functionality into the simulation.
 * It works by generating an ArrayList of joysticks, gamepads and steering wheels connected
 * to the computer, polling each one's active components (buttons, axes, POV hat), using 
 * the polled data to calculate control deflections, and assigning these to each respective key 
 * in the controls EnumMap. These deflections are limited by the constants defined in the 
 * FlightControls enum. Aileron and Elevator trim are handled by the POV hat switch, and left
 * and right throttle are handled by the throttle slider.
 * 
 * The following must be passed in upon creation:
 * EnumMap<FlightControls, Double> controls
 * 
 * The following is returned from the object's updateFlightControls method:
 * EnumMap<FlightControls, Double> controls
 */
public class Joystick {
	private ArrayList<Controller> controllerList;
	
	// Add these trim values to getControlDeflection method call to emulate trim deflections
	private double trimElevator = 0.0;
	private double trimAileron  = 0.0;
	private double trimRudder   = 0.0;
	
	// Constructor for Joystick class creates list of controllers using searchForControllers()
	public Joystick(EnumMap<FlightControls, Double> controls) {
		this.controllerList = new ArrayList<>();
		
		// Get initial trim values from initial values in controls EnumMap (rad)
		this.trimElevator = controls.get(FlightControls.ELEVATOR);
		this.trimAileron  = controls.get(FlightControls.AILERON);
		this.trimRudder   = controls.get(FlightControls.RUDDER);
		
		searchForControllers();
	}
	
	// Updates values for controls in controls EnumMap
	public EnumMap<FlightControls, Double> updateFlightControls(EnumMap<FlightControls, Double> controls) {		
		return FlightControlsUtilities.limitControls(getJoystickValues(controls));
	}

	// Search for and add controllers of type Controller.Type.STICK, Controller.Type.GAMEPAD
	// and Controller.Type.WHEEL to controllerList
	private void searchForControllers() {
		Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
		
		// If any suitable joysticks found, set boolean to true
		boolean foundStick = false;
		
		for(Controller controller : controllers){
			if (controller.getType() == Controller.Type.STICK || 
				controller.getType() == Controller.Type.GAMEPAD || 
				controller.getType() == Controller.Type.WHEEL) {
				// Add new controller to the list of all controllers.
				controllerList.add(controller);
				// Found a usable joystick
				foundStick = true;
			}
		}
		
		// No joysticks available, exit function
		if (!foundStick) {
			System.err.println("No controllers found!");
			return;
		}
	}

	// Get button, POV and axis values from joystick(s), and return a Double array for updateControls
	private EnumMap<FlightControls, Double> getJoystickValues(EnumMap<FlightControls, Double> controls) {
		
		// Iterate through all controllers connected
		for (Controller controller : controllerList) {
			
			// Poll controller for data; if disconnected, break out of componentIdentification loop
			if(!controller.poll()) 
				break;
			
			// Iterate through all components of the controller.
			Component[] componentList = controller.getComponents();
			for(Component component : componentList) {
				Identifier componentIdentifier = component.getIdentifier();

				// Buttons
				if(componentIdentifier.getName().matches("^[0-9]*$")) { // If the component identifier contains only numbers, it is a button
					if(component.getPollData() == 1.0f) {
						// Button index
						switch(component.getIdentifier().toString()) {}
					}
					continue; // Go to next component
				}

				// POV Hat Switch - Control elevator and aileron trim 
				if(componentIdentifier == Component.Identifier.Axis.POV) {
					float povValue = component.getPollData();
					
					if      (Float.compare(povValue, POV.UP)    == 0 & trimElevator <= FlightControls.ELEVATOR.getMaximum())
						trimElevator += 0.001; 
					else if (Float.compare(povValue, POV.DOWN)  == 0 & trimElevator >= FlightControls.ELEVATOR.getMinimum()) 
						trimElevator -= 0.001;
					else if (Float.compare(povValue, POV.LEFT)  == 0 & trimAileron  >= FlightControls.AILERON.getMinimum()) 
						trimAileron  += 0.001;
					else if (Float.compare(povValue, POV.RIGHT) == 0 & trimAileron  <= FlightControls.AILERON.getMaximum())
						trimAileron  -= 0.001;
					
					continue; // Go to next component
				}

				// Joystick Axes - Read raw joystick value, convert to control deflection, and add trim value
				if(component.isAnalog()){
					double axisValue = (double)component.getPollData();

					// Y axis (Elevator)
					if(componentIdentifier == Component.Identifier.Axis.Y) {
						controls.put(FlightControls.ELEVATOR, 
								 	 getControlDeflection(FlightControls.ELEVATOR, 
								 			 		   	  axisValue+trimElevator));
						continue; // Go to next component
					}
					// X axis (Aileron)
					if(componentIdentifier == Component.Identifier.Axis.X) {
						controls.put(FlightControls.AILERON, 
									 getControlDeflection(FlightControls.AILERON, 
											 		   	  axisValue+trimAileron));
						continue; // Go to next component
					}
					// Z axis (Rudder)
					if(componentIdentifier == Component.Identifier.Axis.RZ) {
						controls.put(FlightControls.RUDDER, 
								 	 getControlDeflection(FlightControls.RUDDER, 
								 			 		   	  axisValue+trimRudder));
						continue; // Go to next component
					}
					// Slider axis (Throttle)
					if(componentIdentifier == Component.Identifier.Axis.SLIDER) {
						controls.put(FlightControls.THROTTLE_L,-(axisValue-1)/2);
						controls.put(FlightControls.THROTTLE_R,-(axisValue-1)/2);
						continue; // Go to next component
					}
				}
			}
		}

		return controls;
	}
	
	// Use maximum and minimum values defined in FlightControls enum to convert normalized joystick
	// axis value to actual control deflection 
	private double getControlDeflection(FlightControls controlType, double axisValue) {
		// Calculate positive and negative slope
		// (elevator has different values for positive/negative max)
		if (axisValue <= 0) 
			return (controlType.getMaximum()*Math.abs(axisValue));
		else
			return (controlType.getMinimum()*axisValue);
	}
}
