package com.chrisali.javaflightsim.controls;

import java.util.ArrayList;
import java.util.EnumMap;

import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

public class Mouse extends SimulationController {

	// Constructor for Joystick class creates list of controllers using
	// searchForControllers()
	public Mouse(EnumMap<FlightControls, Double> controls) {
		this.controllerList = new ArrayList<>();

		// Get initial trim values from initial values in controls EnumMap (rad)
		trimElevator = controls.get(FlightControls.ELEVATOR);
		trimAileron = controls.get(FlightControls.AILERON);
		trimRudder = controls.get(FlightControls.RUDDER);

		searchForControllers();
	}

	@Override
	protected void searchForControllers() {
		Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();

		for (Controller controller : controllers) {
			if (controller.getType() == Controller.Type.MOUSE) {
				
				// Add new controller to the list of mouse controllers.
				controllerList.add(controller);
			}
		}

		// If no mice available, exit function
		if (controllerList.isEmpty()) {
			System.err.println("No mice found!");
			return;
		}

	}
	
	// Get button, mouse wheel and axis values from mouse, and return a Double array for updateFlightControls
	// in SimulationController class
	@Override
	protected EnumMap<FlightControls, Double> calculateControllerValues(EnumMap<FlightControls, Double> controls) {
		// Iterate through all controllers connected
		for (Controller controller : controllerList) {
			
			// Poll controller for data; if disconnected, break out of componentIdentification loop
			if(!controller.poll()) 
				break;
			
			// Iterate through all components of the controller.
			for(Component component : controller.getComponents()) {
				Identifier componentIdentifier = component.getIdentifier();

				// Buttons
				if(componentIdentifier.getName().matches("^[0-9]*$")) { // If the component identifier contains only numbers, it is a button
					if(component.getPollData() == 1.0f) {
						// Button index (nothing implemented yet)
					}
					continue; // Go to next component
				}

				// Mouse Axes - Read raw mouse value, convert to control deflection, and add trim value
				if(component.isRelative()){
					double axisValue = (double)component.getPollData();

					// Y axis (Elevator)
					if(componentIdentifier == Component.Identifier.Axis.Y) {
						controls.put(FlightControls.ELEVATOR, 
								 	 calculateControlDeflection(FlightControls.ELEVATOR, 
								 			 		   	  		axisValue)+trimElevator);
						continue; // Go to next component
					}
					// X axis (Aileron)
					if(componentIdentifier == Component.Identifier.Axis.X) {
						controls.put(FlightControls.AILERON, 
									 calculateControlDeflection(FlightControls.AILERON, 
											 		   	  		axisValue)+trimAileron);
						continue; // Go to next component
					}
				}
			}
		}
		
		return controls;
	}

}
