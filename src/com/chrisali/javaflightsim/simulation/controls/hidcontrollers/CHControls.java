package com.chrisali.javaflightsim.simulation.controls.hidcontrollers;

import java.util.ArrayList;
import java.util.Map;

import com.chrisali.javaflightsim.simulation.controls.FlightControlType;

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
 * {@link FlightControlType}. Aileron and Elevator trim are handled by the POV hat switch, and all
 * throttles are controlled by the throttle quadrant.
 * @see AbstractController
 */
public class CHControls extends AbstractController {
	
	/**
	 *  Constructor for CHControls class creates list of controllers using searchForControllers()
	 * @param controls
	 */
	public CHControls(Map<FlightControlType, Double> controls) {
		this.controllerList = new ArrayList<>();
		
		// Get initial trim values from initial values in controls EnumMap (rad)
		trimElevator = controls.get(FlightControlType.ELEVATOR);
		trimAileron  = controls.get(FlightControlType.AILERON);
		trimRudder   = controls.get(FlightControlType.RUDDER);
		
		flaps = controls.get(FlightControlType.FLAPS);
		
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
	 *  Get button, POV and axis values from each joystick controller, and return a map for updateFlightControls()
	 *  in {@link AbstractController}
	 *  
	 *  @return controls Map
	 */
	@Override
	protected Map<FlightControlType, Double> calculateControllerValues(Map<FlightControlType, Double> controls) {
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
							if(trimAileron >= FlightControlType.AILERON.getMinimum()) trimAileron  += getDeflectionRate(FlightControlType.ELEVATOR)/10;
							break;
						case "3":
							if(trimAileron <= FlightControlType.AILERON.getMaximum()) trimAileron  -= getDeflectionRate(FlightControlType.ELEVATOR)/10;
							break;
						case "4":
							controls.put(FlightControlType.GEAR, FlightControlType.GEAR.getMinimum()); // Retract landing gear
							break;
						case "5":
							controls.put(FlightControlType.GEAR, FlightControlType.GEAR.getMaximum()); // Extend landing gear
							break;
						case "6":
							if (flaps >= FlightControlType.FLAPS.getMinimum())	controls.put(FlightControlType.FLAPS, (flaps -= getDeflectionRate(FlightControlType.FLAPS)));
							break;
						case "7":
							if (flaps <= FlightControlType.FLAPS.getMaximum()) controls.put(FlightControlType.FLAPS, (flaps += getDeflectionRate(FlightControlType.FLAPS)));
							break;
						case "10":
							if (trimElevator <= FlightControlType.ELEVATOR.getMaximum()) trimElevator += getDeflectionRate(FlightControlType.AILERON)/20;
							break;	
						case "11":
							if (trimElevator >= FlightControlType.ELEVATOR.getMinimum()) trimElevator -= getDeflectionRate(FlightControlType.AILERON)/20;
							break;
						}
					} else if(component.getPollData() == 1.0f && controller.getName().toLowerCase().compareTo("ch throttle quadrant usb") == 0) {
						// Button index
						switch(component.getIdentifier().toString()) {
						case "0":
							if (trimElevator <= FlightControlType.ELEVATOR.getMaximum()) trimElevator += getDeflectionRate(FlightControlType.ELEVATOR)/10;
							break;
						case "1":
							if (trimElevator >= FlightControlType.ELEVATOR.getMinimum()) trimElevator -= getDeflectionRate(FlightControlType.ELEVATOR)/10;
							break;
						}
					}
					continue; // Go to next component
				}

				// POV Hat Switch - Control elevator and aileron trim 
				if(componentIdentifier == Component.Identifier.Axis.POV) {
					float povValue = component.getPollData();
					
					if      (Float.compare(povValue, POV.UP)    == 0 & trimElevator <= FlightControlType.ELEVATOR.getMaximum())
						trimElevator += 0.001; 
					else if (Float.compare(povValue, POV.DOWN)  == 0 & trimElevator >= FlightControlType.ELEVATOR.getMinimum()) 
						trimElevator -= 0.001;
					else if (Float.compare(povValue, POV.LEFT)  == 0 & trimAileron  >= FlightControlType.AILERON.getMinimum()) 
						trimAileron  += 0.001;
					else if (Float.compare(povValue, POV.RIGHT) == 0 & trimAileron  <= FlightControlType.AILERON.getMaximum())
						trimAileron  -= 0.001;
					
					continue; // Go to next component
				}

				// Controller Axes - Read raw controller value, square to reduce its sensitivity, convert to control deflection, and add trim value
				if(component.isAnalog() && controller.getName().toLowerCase().compareTo("ch flight sim yoke usb") == 0){
					double axisValue = (double)component.getPollData();

					// Y axis (Elevator)
					if(componentIdentifier == Component.Identifier.Axis.Y) {
						controls.put(FlightControlType.ELEVATOR, 
								 	 calculateControlDeflection(FlightControlType.ELEVATOR, 
								 			 		   	  		negativeSquare(axisValue))+trimElevator);
						continue; // Go to next component
					}
					// X axis (Aileron)
					if(componentIdentifier == Component.Identifier.Axis.X) {
						controls.put(FlightControlType.AILERON, 
									 calculateControlDeflection(FlightControlType.AILERON, 
											 					negativeSquare(axisValue))+trimAileron);
						continue; // Go to next component
					}
				} else if(component.isAnalog() && controller.getName().toLowerCase().compareTo("ch pro pedals usb") == 0){
					double axisValue = (double)component.getPollData();

					// Y axis (Elevator)
					if(componentIdentifier == Component.Identifier.Axis.Y) {
						controls.put(FlightControlType.BRAKE_R, negativeSquare(axisValue));
						continue; // Go to next component
					}
					// X axis (Aileron)
					if(componentIdentifier == Component.Identifier.Axis.X) {
						controls.put(FlightControlType.BRAKE_L, negativeSquare(axisValue));
						continue; // Go to next component
					}
					// Z axis (Rudder)
					if(componentIdentifier == Component.Identifier.Axis.Z) {
						controls.put(FlightControlType.RUDDER, 
								 	 calculateControlDeflection(FlightControlType.RUDDER, 
								 			 					negativeSquare(axisValue))+trimRudder);
						continue; // Go to next component
					}
				} else if(component.isAnalog() && controller.getName().toLowerCase().compareTo("ch throttle quadrant usb") == 0){
					double axisValue = (double)component.getPollData();

					// X axis (Throttle 1)
					if(componentIdentifier == Component.Identifier.Axis.X) {
						controls.put(FlightControlType.THROTTLE_1,-(axisValue-1)/2);
						continue; // Go to next component
					}
					// Y axis (Throttle 2)
					if(componentIdentifier == Component.Identifier.Axis.Y) {
						controls.put(FlightControlType.THROTTLE_2,-(axisValue-1)/2);
						continue; // Go to next component
					}
					// Z axis (Propeller 1)
					if(componentIdentifier == Component.Identifier.Axis.Z) {
						controls.put(FlightControlType.PROPELLER_1,-(axisValue-1)/2);
						continue; // Go to next component
					}
					// RZ axis (Propeller 2)
					if(componentIdentifier == Component.Identifier.Axis.RZ) {
						controls.put(FlightControlType.PROPELLER_2,-(axisValue-1)/2);
						continue; // Go to next component
					}
					// RY axis (Mixture 1)
					if(componentIdentifier == Component.Identifier.Axis.RY) {
						controls.put(FlightControlType.MIXTURE_1,-(axisValue-1)/2);
						continue; // Go to next component
					}
					// RX axis (Mixture 2)
					if(componentIdentifier == Component.Identifier.Axis.RX) {
						controls.put(FlightControlType.MIXTURE_2,-(axisValue-1)/2);
						continue; // Go to next component
					}
				}
			}
		}
		return controls;
	}
}
