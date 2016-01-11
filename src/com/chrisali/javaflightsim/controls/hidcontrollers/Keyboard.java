package com.chrisali.javaflightsim.controls.hidcontrollers;

import java.util.ArrayList;
import java.util.EnumMap;

import com.chrisali.javaflightsim.controls.FlightControls;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Component.Identifier;

public class Keyboard extends SimulationController {
	
	// Constructor for Keyboard class creates list of controllers using
	// searchForControllers()
	public Keyboard(EnumMap<FlightControls, Double> controls) {
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
			if (controller.getType() == Controller.Type.KEYBOARD)
				controllerList.add(controller);
		}

		// If no keyboards available, exit function
		if (controllerList.isEmpty()) {
			System.err.println("No keyboard found!");
			return;
		}
		
	}

	@Override
	protected EnumMap<FlightControls, Double> calculateControllerValues(EnumMap<FlightControls, Double> controls) {
		// Iterate through all controllers connected
		//for (Controller controller : controllerList) {
			Controller controller = controllerList.get(1);
			controller.poll();
			// Poll controller for data; if disconnected, break out of componentIdentification loop
//			if(!controller.poll()) 
//				break;
			
			// Iterate through all components of the controller.
			//for (Component component : controller.getComponents()) {
			Component[] components = controller.getComponents();
            for(int i=0; i < components.length; i++) {
                Component component = components[i];
				Identifier componentIdentifier = component.getIdentifier();
				
				if (componentIdentifier.getName().matches(Component.Identifier.Key._4.toString())) {
					if(component.getPollData() == 1.0f)
						System.out.println("Pressed " + component.getName());
					
					continue;
				}
			}
			
		//}
		
		return controls;
	}

}
