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
public class Joystick extends SimulationController {
	
	// Constructor for Joystick class creates list of controllers using searchForControllers()
	public Joystick(EnumMap<FlightControls, Double> controls) {
		this.controllerList = new ArrayList<>();
		
		// Get initial trim values from initial values in controls EnumMap (rad)
		trimElevator = controls.get(FlightControls.ELEVATOR);
		trimAileron  = controls.get(FlightControls.AILERON);
		trimRudder   = controls.get(FlightControls.RUDDER);
		
		searchForControllers();
	}

	// Search for and add controllers of type Controller.Type.STICK or Controller.Type.GAMEPAD
	// to controllerList
	@Override
	protected void searchForControllers() {
		Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
		
		for(Controller controller : controllers){
			if (controller.getType() == Controller.Type.STICK || 
				controller.getType() == Controller.Type.GAMEPAD) {
				
				// Add new controller to the list of joystick controllers.
				controllerList.add(controller);
			}	
		}
		
		// If no joysticks available, exit function
		if (controllerList.isEmpty()) {
			System.err.println("No joysticks found!");
			return;
		}
	}

	// Get button, POV and axis values from joystick(s), and return an EnumMap for updateFlightControls
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
					// Z axis (Rudder)
					if(componentIdentifier == Component.Identifier.Axis.RZ) {
						controls.put(FlightControls.RUDDER, 
								 	 calculateControlDeflection(FlightControls.RUDDER, 
								 			 		   	  		axisValue)+trimRudder);
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
	
	
}
