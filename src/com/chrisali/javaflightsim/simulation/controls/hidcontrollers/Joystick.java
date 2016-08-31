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
 * The Joystick object uses JInput to integrate joystick functionality into the simulation.
 * It works by generating an ArrayList of joysticks, gamepads and steering wheels connected
 * to the computer, polling each one's active components (buttons, axes, POV hat), using 
 * the polled data to calculate control deflections, and assigning these to each respective key 
 * in the controls EnumMap. These deflections are limited by the constants defined in the 
 * {@link FlightControlType}. Aileron and Elevator trim are handled by the POV hat switch, and all
 * throttles are controlled by the throttle slider.
 * @see AbstractController
 */
public class Joystick extends AbstractController {
	
	/**
	 *  Constructor for Joystick class creates list of controllers using searchForControllers()
	 * @param controls
	 */
	public Joystick(Map<FlightControlType, Double> controls) {
		this.controllerList = new ArrayList<>();
		
		// Get initial trim values from initial values in controls EnumMap (rad)
		trimElevator = controls.get(FlightControlType.ELEVATOR);
		trimAileron  = controls.get(FlightControlType.AILERON);
		trimRudder   = controls.get(FlightControlType.RUDDER);
		
		searchForControllers();
	}
	
	/**
	 * Search for and add controllers of type Controller.Type.STICK or Controller.Type.GAMEPAD
	 * to controllerList
	 */ 
	@Override
	protected void searchForControllers() {
		Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
		
		for(Controller controller : controllers){
			if (controller.getType() == Controller.Type.STICK || controller.getType() == Controller.Type.GAMEPAD)
				controllerList.add(controller);
		}
		
		// If no joysticks available, exit function
		if (controllerList.isEmpty()) {
			System.err.println("No joysticks found!");
			return;
		}
	}

	/**
	 *  Get button, POV and axis values from joystick(s), and return a Map for updateFlightControls() 
	 *  in {@link AbstractController}
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
					if(component.getPollData() == 1.0f) {
						// Button index (nothing implemented yet)
						switch(component.getIdentifier().toString()) {
						case "0":
							controls.put(FlightControlType.BRAKE_L, negativeSquare(FlightControlType.BRAKE_L.getMaximum()));
							controls.put(FlightControlType.BRAKE_R, negativeSquare(FlightControlType.BRAKE_R.getMaximum()));
							break;
						case "4":
							controls.put(FlightControlType.GEAR, FlightControlType.GEAR.getMaximum());
							break;
						case "5":
							controls.put(FlightControlType.GEAR, FlightControlType.GEAR.getMinimum());
							break;
						case "6":
							if (flaps >= FlightControlType.FLAPS.getMinimum())	controls.put(FlightControlType.FLAPS, (flaps -= getDeflectionRate(FlightControlType.FLAPS)));
							break;
						case "7":
							if (flaps <= FlightControlType.FLAPS.getMaximum()) controls.put(FlightControlType.FLAPS, (flaps += getDeflectionRate(FlightControlType.FLAPS)));
							break;
						}
					}
					continue; // Go to next component
				}

				// POV Hat Switch - Control elevator and aileron trim 
				if(componentIdentifier == Component.Identifier.Axis.POV) {
					float povValue = component.getPollData();
					
					if      (Float.compare(povValue, POV.UP)    == 0 & trimElevator <= FlightControlType.ELEVATOR.getMaximum())
						trimElevator += getDeflectionRate(FlightControlType.ELEVATOR)/10; 
					else if (Float.compare(povValue, POV.DOWN)  == 0 & trimElevator >= FlightControlType.ELEVATOR.getMinimum()) 
						trimElevator -= getDeflectionRate(FlightControlType.ELEVATOR)/10;
					else if (Float.compare(povValue, POV.LEFT)  == 0 & trimAileron  >= FlightControlType.AILERON.getMinimum()) 
						trimAileron  += getDeflectionRate(FlightControlType.AILERON)/20;
					else if (Float.compare(povValue, POV.RIGHT) == 0 & trimAileron  <= FlightControlType.AILERON.getMaximum())
						trimAileron  -= getDeflectionRate(FlightControlType.AILERON)/20;
					
					continue; // Go to next component
				}

				// Joystick Axes - Read raw joystick value, square to reduce its sensitivity, convert to control deflection, and add trim value
				if(component.isAnalog()){
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
					// Z axis (Rudder)
					if(componentIdentifier == Component.Identifier.Axis.RZ) {
						controls.put(FlightControlType.RUDDER, 
								 	 calculateControlDeflection(FlightControlType.RUDDER, 
								 			 					negativeSquare(axisValue))+trimRudder);
						continue; // Go to next component
					}
					// Slider axis (Throttle)
					if(componentIdentifier == Component.Identifier.Axis.SLIDER) {
						controls.put(FlightControlType.THROTTLE_1,-(axisValue-1)/2);
						controls.put(FlightControlType.THROTTLE_2,-(axisValue-1)/2);
						continue; // Go to next component
					}
				}
			}
		}
		return controls;
	}
}
