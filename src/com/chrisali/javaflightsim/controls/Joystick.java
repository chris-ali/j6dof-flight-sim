package com.chrisali.javaflightsim.controls;

import java.util.ArrayList;
import java.util.EnumMap;
import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

public class Joystick {
	private ArrayList<Controller> controllerList;
	
	// Add these trim values to getControlDeflection method call to emulate trim deflections
	private double trimElevator = 0.0;
	private double trimAileron  = 0.0;
	private double trimRudder   = 0.0;
	
	// Constructor for Joystick class creates list of controllers using searchForControllers()
	public Joystick(EnumMap<FlightControls, Double> controls) {
		this.controllerList = new ArrayList<>();
		
		// Get initial trim values from initial values in controls EnumMap
		this.trimElevator = controls.get(FlightControls.ELEVATOR);
		this.trimAileron  = controls.get(FlightControls.AILERON);
		this.trimRudder   = controls.get(FlightControls.RUDDER);
		
		searchForControllers();
	}
	
	// Updates values for controls in 
	public EnumMap<FlightControls, Double> updateFlightControls(EnumMap<FlightControls, Double> controls) {		
		return FlightControlsUtilities.limitControls(getJoystickValues(controls));
	}

	// Search for and add controllers of type Controller.Type.STICK, Controller.Type.GAMEPAD
	// and Controller.Type.WHEEL to controllerList
	private void searchForControllers() {
		Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();

		for(Controller controller : controllers){
			if (controller.getType() == Controller.Type.STICK || 
				controller.getType() == Controller.Type.GAMEPAD || 
				controller.getType() == Controller.Type.WHEEL) {
				// Add new controller to the list of all controllers.
				controllerList.add(controller);
			}
		}
	}

	// Get button, POV and axis values from joystick(s), and return a Double array for updateControls
	private EnumMap<FlightControls, Double> getJoystickValues(EnumMap<FlightControls, Double> controls) {
		
		// Iterate through all controllers connected
		for (Controller controller : controllerList) {
			// Iterate through all components of the controller.
			Component[] componentList = controller.getComponents();
			for(Component component : componentList) {
				Identifier componentIdentifier = component.getIdentifier();

				// Buttons
				if(componentIdentifier.getName().matches("^[0-9]*$")) { // If the component identifier name contains only numbers, then this is a button.
					// Is button pressed?
					boolean isPressed = true;
					if(component.getPollData() == 0.0f)
						isPressed = false;

					// Button index
					String buttonIndex = component.getIdentifier().toString();

					continue; // Go to next component
				}

				// Hat switch
				if(componentIdentifier == Component.Identifier.Axis.POV) {
					float hatSwitchPosition = component.getPollData();
					continue; // Go to next component
				}

				// Axes
				if(component.isAnalog()){
					double axisValue = (double)component.getPollData();

					// X axis
					if(componentIdentifier == Component.Identifier.Axis.X) {
						controls.put(FlightControls.AILERON, 
									 getControlDeflection(FlightControls.AILERON, 
											 		   	  axisValue+trimAileron));
						continue; // Go to next component
					}
					// Y axis
					if(componentIdentifier == Component.Identifier.Axis.Y) {
						controls.put(FlightControls.ELEVATOR, 
								 	 getControlDeflection(FlightControls.ELEVATOR, 
								 			 		   	  axisValue+trimElevator));
						continue; // Go to next component
					}
					// Z axis
					if(componentIdentifier == Component.Identifier.Axis.Z) {
						controls.put(FlightControls.RUDDER, 
								 	 getControlDeflection(FlightControls.RUDDER, 
								 			 		   	  axisValue+trimRudder));
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
			return (controlType.getMinimum()*axisValue);
		else
			return (controlType.getMaximum()*axisValue);
	}
}
