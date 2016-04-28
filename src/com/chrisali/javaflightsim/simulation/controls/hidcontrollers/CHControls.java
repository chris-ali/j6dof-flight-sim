package com.chrisali.javaflightsim.simulation.controls.hidcontrollers;

import java.util.ArrayList;
import java.util.EnumMap;

import com.chrisali.javaflightsim.simulation.controls.FlightControls;

import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Component.POV;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

/**
 * The CHControls object uses JInput to integrate functionality for CH controls into the simulation.
 * It works by generating an ArrayList of CH controls (yoke, pedals and throttle quadrant) connected
 * to the computer, polling each one's active components (buttons, axes, POV hat), using 
 * the polled data to calculate control deflections, and assigning these to each respective key 
 * in the controls EnumMap. These deflections are limited by the constants defined in the 
 * {@link FlightControls}. Aileron and Elevator trim are handled by the POV hat switch, and all
 * throttles are controlled by the throttle quadrant.
 * @see AbstractController
 */
public class CHControls extends AbstractController {
	
	/**
	 *  Constructor for CHControls class creates list of controllers using searchForControllers()
	 * @param controls
	 */
	public CHControls(EnumMap<FlightControls, Double> controls) {
		this.controllerList = new ArrayList<>();
		
		// Get initial trim values from initial values in controls EnumMap (rad)
		trimElevator = controls.get(FlightControls.ELEVATOR);
		trimAileron  = controls.get(FlightControls.AILERON);
		trimRudder   = controls.get(FlightControls.RUDDER);
		
		flaps = controls.get(FlightControls.FLAPS);
		
		searchForControllers();
	}
	
	/**
	 * Search for and add controllers of type Controller.Type.STICK
	 * to controllerList
	 */ 
	@Override
	protected void searchForControllers() {
		Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
		
		for(Controller controller : controllers){
			if (controller.getType() == Controller.Type.STICK)
				controllerList.add(controller);
		}
		
		// If no joysticks available, exit function
		if (controllerList.isEmpty()) {
			System.err.println("No joysticks found!");
			return;
		}
	}

	/**
	 *  Get button, POV and axis values from each joystick controller, and return an EnumMap for updateFlightControls 
	 *  in SimulationController class
	 */
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
					if(component.getPollData() == 1.0f && controller.getName().toLowerCase().compareTo("ch flight sim yoke usb") == 0) {
						// Button index
						switch(component.getIdentifier().toString()) {
						case "2":
							if(trimAileron >= FlightControls.AILERON.getMinimum()) trimAileron  += 0.000125;
							break;
						case "3":
							if(trimAileron <= FlightControls.AILERON.getMaximum()) trimAileron  -= 0.000125;
							break;
						case "4":
							controls.put(FlightControls.GEAR, FlightControls.GEAR.getMinimum()); // Retract landing gear
							break;
						case "5":
							controls.put(FlightControls.GEAR, FlightControls.GEAR.getMaximum()); // Extend landing gear
							break;
						case "6":
							if (flaps >= FlightControls.FLAPS.getMinimum())	controls.put(FlightControls.FLAPS, (flaps -= 0.004));
							break;
						case "7":
							if (flaps <= FlightControls.FLAPS.getMaximum()) controls.put(FlightControls.FLAPS, (flaps += 0.004));
							break;
						case "10":
							if (trimElevator <= FlightControls.ELEVATOR.getMaximum()) trimElevator += 0.00025;
							break;	
						case "11":
							if (trimElevator >= FlightControls.ELEVATOR.getMinimum()) trimElevator -= 0.00025;
							break;
						}
					} else if(component.getPollData() == 1.0f && controller.getName().toLowerCase().compareTo("ch throttle quadrant usb") == 0) {
						// Button index
						switch(component.getIdentifier().toString()) {
						case "0":
							if (trimElevator <= FlightControls.ELEVATOR.getMaximum()) trimElevator += 0.000125;
							break;
						case "1":
							if (trimElevator >= FlightControls.ELEVATOR.getMinimum()) trimElevator -= 0.000125;
							break;
						}
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

				// Controller Axes - Read raw controller value, square to reduce its sensitivity, convert to control deflection, and add trim value
				if(component.isAnalog() && controller.getName().toLowerCase().compareTo("ch flight sim yoke usb") == 0){
					double axisValue = (double)component.getPollData();

					// Y axis (Elevator)
					if(componentIdentifier == Component.Identifier.Axis.Y) {
						controls.put(FlightControls.ELEVATOR, 
								 	 calculateControlDeflection(FlightControls.ELEVATOR, 
								 			 		   	  		negativeSquare(axisValue))+trimElevator);
						continue; // Go to next component
					}
					// X axis (Aileron)
					if(componentIdentifier == Component.Identifier.Axis.X) {
						controls.put(FlightControls.AILERON, 
									 calculateControlDeflection(FlightControls.AILERON, 
											 					negativeSquare(axisValue))+trimAileron);
						continue; // Go to next component
					}
				} else if(component.isAnalog() && controller.getName().toLowerCase().compareTo("ch pro pedals usb") == 0){
					double axisValue = (double)component.getPollData();

					// Y axis (Elevator)
					if(componentIdentifier == Component.Identifier.Axis.Y) {
						controls.put(FlightControls.BRAKE_R, negativeSquare(axisValue));
						continue; // Go to next component
					}
					// X axis (Aileron)
					if(componentIdentifier == Component.Identifier.Axis.X) {
						controls.put(FlightControls.BRAKE_L, negativeSquare(axisValue));
						continue; // Go to next component
					}
					// Z axis (Rudder)
					if(componentIdentifier == Component.Identifier.Axis.Z) {
						controls.put(FlightControls.RUDDER, 
								 	 calculateControlDeflection(FlightControls.RUDDER, 
								 			 					negativeSquare(axisValue))+trimRudder);
						continue; // Go to next component
					}
				} else if(component.isAnalog() && controller.getName().toLowerCase().compareTo("ch throttle quadrant usb") == 0){
					double axisValue = (double)component.getPollData();

					// X axis (Throttle 1)
					if(componentIdentifier == Component.Identifier.Axis.X) {
						controls.put(FlightControls.THROTTLE_1,-(axisValue-1)/2);
						continue; // Go to next component
					}
					// Y axis (Throttle 2)
					if(componentIdentifier == Component.Identifier.Axis.Y) {
						controls.put(FlightControls.THROTTLE_2,-(axisValue-1)/2);
						continue; // Go to next component
					}
					// Z axis (Propeller 1)
					if(componentIdentifier == Component.Identifier.Axis.Z) {
						controls.put(FlightControls.PROPELLER_1,-(axisValue-1)/2);
						continue; // Go to next component
					}
					// RZ axis (Propeller 2)
					if(componentIdentifier == Component.Identifier.Axis.RZ) {
						controls.put(FlightControls.PROPELLER_2,-(axisValue-1)/2);
						continue; // Go to next component
					}
					// RY axis (Mixture 1)
					if(componentIdentifier == Component.Identifier.Axis.RY) {
						controls.put(FlightControls.MIXTURE_1,-(axisValue-1)/2);
						continue; // Go to next component
					}
					// RX axis (Mixture 2)
					if(componentIdentifier == Component.Identifier.Axis.RX) {
						controls.put(FlightControls.MIXTURE_2,-(axisValue-1)/2);
						continue; // Go to next component
					}
				}
			}
		}
		return controls;
	}
}
