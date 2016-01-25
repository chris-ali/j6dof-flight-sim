package com.chrisali.javaflightsim.controls.hidcontrollers;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;

import com.chrisali.javaflightsim.controls.FlightControls;
import com.chrisali.javaflightsim.setup.Options;

import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

public class Keyboard extends SimulationController {
	// Keep track if button is pressed, so events occur only once if button held down 
	private boolean pPressed = false;
	private boolean rPressed = false;
	// Keep track of reset, so that it can only be run once per pause
	private boolean wasReset = false;
	
	// Constructor for Keyboard class creates list of controllers using
	// searchForControllers()
	public Keyboard(EnumMap<FlightControls, Double> controls) {
		this.controllerList = new ArrayList<>();

		// Get initial trim values from initial values in controls EnumMap (rad)
		trimElevator = controls.get(FlightControls.ELEVATOR);
		trimAileron  = controls.get(FlightControls.AILERON);
		trimRudder   = controls.get(FlightControls.RUDDER);
		
		searchForControllers();
	}
	
	@Override
	protected void searchForControllers() {
		Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();

		for (Controller controller : controllers) {
			if (controller.getType() == Controller.Type.KEYBOARD)
				controllerList.add(controller);
		}

		// If no keyboards available, exit function
		if (controllerList.isEmpty()) {
			System.err.println("No keyboard found!");
			return;
		}
		
	}
	
	public EnumSet<Options> updateOptions(EnumSet<Options> options) {
		// Iterate through all controllers connected
		for (Controller controller : controllerList) {
			// Poll controller for data; if disconnected, break out of componentIdentification loop
			if(!controller.poll()) 
				break;
			
			// Iterate through all components of the controller.
			for (Component component : controller.getComponents()) {
				Identifier componentIdentifier = component.getIdentifier();
				
				// When simulation paused, can be reset once per pause with "R" key
				if (componentIdentifier.getName().matches(Component.Identifier.Key.P.toString())) {
					if(component.getPollData() == 1.0f & !options.contains(Options.PAUSED) & !pPressed) {
						options.add(Options.PAUSED);
						System.err.println("Simulation Paused!");
						this.pPressed = true;
					} else if(component.getPollData() == 1.0f & options.contains(Options.PAUSED) & !pPressed) {
						options.remove(Options.PAUSED);
						this.wasReset = false;
						this.pPressed = true;
					} else if(component.getPollData() == 0.0f & pPressed) {
						this.pPressed = false;
					}

					continue;
				}
				
				if (componentIdentifier.getName().matches(Component.Identifier.Key.R.toString())) {
					if(component.getPollData() == 1.0f & options.contains(Options.PAUSED) & !options.contains(Options.RESET) &
					   !rPressed & !wasReset) {
						options.add(Options.RESET);
						System.err.println("Simulation Reset!");
						this.wasReset = true;
						this.rPressed = true;
					} else if (component.getPollData() == 0.0f & rPressed) {
						options.remove(Options.RESET);
						this.rPressed = false;
					}
					
					continue;
				}
			}
		}
		
		return options;
	}

	@Override
	protected EnumMap<FlightControls, Double> calculateControllerValues(EnumMap<FlightControls, Double> controls) {
		// Iterate through all controllers connected
		for (Controller controller : controllerList) {
			// Poll controller for data; if disconnected, break out of componentIdentification loop
			if(!controller.poll()) 
				break;
			
			// Iterate through all components (keys) of the controller.
			for (Component component : controller.getComponents()) {
				Identifier componentIdentifier = component.getIdentifier();
				
				if (componentIdentifier.getName().matches(Component.Identifier.Key.UP.toString()) & 
					controls.get(FlightControls.ELEVATOR) <= FlightControls.ELEVATOR.getMaximum()) {
					
					if(component.getPollData() == 1.0f)
						controls.put(FlightControls.ELEVATOR,controls.get(FlightControls.ELEVATOR)+0.001);
					
					continue;
				}
				
				if (componentIdentifier.getName().matches(Component.Identifier.Key.DOWN.toString()) & 
					controls.get(FlightControls.ELEVATOR) >= FlightControls.ELEVATOR.getMinimum()) {
					
					if(component.getPollData() == 1.0f)
						controls.put(FlightControls.ELEVATOR,controls.get(FlightControls.ELEVATOR)-0.001);
					
					continue;
				}
				
				if (componentIdentifier.getName().matches(Component.Identifier.Key.LEFT.toString()) & 
					controls.get(FlightControls.AILERON) >= FlightControls.AILERON.getMinimum()) {
					
					if(component.getPollData() == 1.0f)
						controls.put(FlightControls.AILERON,controls.get(FlightControls.AILERON)+0.001);
					
					continue;
				}
				
				if (componentIdentifier.getName().matches(Component.Identifier.Key.RIGHT.toString()) & 
					controls.get(FlightControls.AILERON) <= FlightControls.AILERON.getMaximum()) {
					
					if(component.getPollData() == 1.0f)
						controls.put(FlightControls.AILERON,controls.get(FlightControls.AILERON)-0.001);
					
					continue;
				}
				
				if (componentIdentifier.getName().matches(Component.Identifier.Key.PAGEUP.toString()) & 
					controls.get(FlightControls.THROTTLE_1) <= FlightControls.THROTTLE_1.getMaximum() &
					controls.get(FlightControls.THROTTLE_2) <= FlightControls.THROTTLE_2.getMaximum()) {
					
					if(component.getPollData() == 1.0f) {
						controls.put(FlightControls.THROTTLE_1,controls.get(FlightControls.THROTTLE_1)+0.01);
						controls.put(FlightControls.THROTTLE_2,controls.get(FlightControls.THROTTLE_2)+0.01);
					}
					
					continue;
				}
					
				if (componentIdentifier.getName().matches(Component.Identifier.Key.PAGEDOWN.toString()) & 
					controls.get(FlightControls.THROTTLE_1) >= FlightControls.THROTTLE_1.getMinimum() &
					controls.get(FlightControls.THROTTLE_2) >= FlightControls.THROTTLE_2.getMinimum()) {
					
					if(component.getPollData() == 1.0f) {
						controls.put(FlightControls.THROTTLE_1,controls.get(FlightControls.THROTTLE_1)-0.01);
						controls.put(FlightControls.THROTTLE_2,controls.get(FlightControls.THROTTLE_2)-0.01);
					}
					
					continue;
				}	
			}	
		}
		return controls;
	}

}
