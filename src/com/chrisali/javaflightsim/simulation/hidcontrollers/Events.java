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
package com.chrisali.javaflightsim.simulation.hidcontrollers;

import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chrisali.javaflightsim.interfaces.SimulationController;
import com.chrisali.javaflightsim.simulation.flightcontrols.FlightControl;
import com.chrisali.javaflightsim.simulation.setup.IntegratorConfig;
import com.chrisali.javaflightsim.simulation.setup.Options;
import com.chrisali.javaflightsim.simulation.setup.SimulationConfiguration;

/**
 * Contains static methods that handle all flight control events (flaps, gear, aileron, trim, throttle, etc) and simulation
 * (pause, reset, quit) that can happen in JavaFlightSimulator. Used in tandem with HID Controller classes that 
 * extend {@link AbstractController}
 * 
 * @author Christopher
 *
 */
public class Events {
	
	private static final Logger logger = LogManager.getLogger(Events.class);
	
	/**
	 * Initializes class with trim control values and integration step size values 
	 * from {@link SimulationConfiguration}
	 * 
	 * @param configuration
	 */
	public static void init(SimulationConfiguration configuration) {
		logger.debug("Initializing flight control events...");
		
		dt = configuration.getIntegratorConfig().get(IntegratorConfig.DT);
		trimAileron = configuration.getInitialControls().get(FlightControl.AILERON);
		trimElevator= configuration.getInitialControls().get(FlightControl.ELEVATOR);
		trimRudder = configuration.getInitialControls().get(FlightControl.RUDDER);
	}
	
	private static double dt = 0.05;
	
	// Add trim values to getDeflection() to emulate trim deflections
	private static double trimElevator = 0.0;
	private static double trimAileron  = 0.0;
	private static double trimRudder   = 0.0;
	private static double flaps   	   = 0.0;
	
	// Keep track if button is pressed, so events occur only once if button held down 
	private static boolean gearPressed = false; 
	private static boolean pausePressed = false;
	private static boolean resetPressed = false;
	
	// Keep track of reset, so that it can only be run once per pause
	private static boolean wasReset = false;
	
	public static void retractGear(Map<FlightControl, Double> controls) {
		controls.put(FlightControl.GEAR, FlightControl.GEAR.getMinimum());	
	}

	public static void extendGear(Map<FlightControl, Double> controls) {
		controls.put(FlightControl.GEAR, FlightControl.GEAR.getMaximum());
	}
	
	/** 
	 * Cycles Landing Gear Down/Up. Use gearPressed to prevent numerous cycles of gear up/down if key held down;
	 * need to release key to extend or retract gear again
	 * 
	 * @param controls
	 * @param buttonPressed
	 */
	public static void cycleGear(Map<FlightControl, Double> controls, boolean buttonPressed) {
		if (!gearPressed && controls.get(FlightControl.GEAR) < 0.5) {
			if(buttonPressed) {
				controls.put(FlightControl.GEAR, 1.0);
				gearPressed = true;
			}
		} else if (!gearPressed && controls.get(FlightControl.GEAR) > 0.5) {
			if(buttonPressed) {
				controls.put(FlightControl.GEAR, 0.0);
				gearPressed = true;
			}
		} else if (gearPressed && !buttonPressed) {
			gearPressed = false;
		} 
	}
	
	public static void retractFlaps(Map<FlightControl, Double> controls) {
		if (flaps >= FlightControl.FLAPS.getMinimum())
			controls.put(FlightControl.FLAPS, (flaps -= getRate(FlightControl.FLAPS)));
	}
	
	public static void extendFlaps(Map<FlightControl, Double> controls) {
		if (flaps <= FlightControl.FLAPS.getMaximum()) 
			controls.put(FlightControl.FLAPS, (flaps += getRate(FlightControl.FLAPS)));
	}
	
	public static void aileronTrimLeft() {
		if(trimAileron >= FlightControl.AILERON.getMinimum()) 
			trimAileron += getRate(FlightControl.AILERON)/10;
	}
	
	public static void aileronTrimRight() {
		if(trimAileron <= FlightControl.AILERON.getMaximum()) 
			trimAileron -= getRate(FlightControl.AILERON)/10;
	}
	
	public static void rudderTrimRight() {
		if(trimRudder >= FlightControl.RUDDER.getMinimum()) 
			trimRudder -= getRate(FlightControl.RUDDER)/10;
	}
	
	public static void rudderTrimLeft() {
		if(trimRudder <= FlightControl.RUDDER.getMaximum()) 
			trimRudder += getRate(FlightControl.RUDDER)/10;
	}
	
	public static void elevatorTrimDown() {
		if (trimElevator <= FlightControl.ELEVATOR.getMaximum()) 
			trimElevator += getRate(FlightControl.ELEVATOR)/10;
	}
	
	public static void elevatorTrimUp() {
		if (trimElevator >= FlightControl.ELEVATOR.getMinimum()) 
			trimElevator -= getRate(FlightControl.ELEVATOR)/10;
	}
	
	public static void elevator(Map<FlightControl, Double> controls, double value) {
		double deflection = calculateDeflection(FlightControl.ELEVATOR, negativeSquare(value));	
		controls.put(FlightControl.ELEVATOR, (deflection + trimElevator));
	}
	
	public static void aileron(Map<FlightControl, Double> controls, double value) {
		double deflection = calculateDeflection(FlightControl.AILERON, negativeSquare(value));
		controls.put(FlightControl.AILERON, (deflection + trimAileron));
	}
	
	public static void rudder(Map<FlightControl, Double> controls, double value) {
		double deflection = calculateDeflection(FlightControl.RUDDER, negativeSquare(value));
		controls.put(FlightControl.RUDDER, (deflection + trimRudder));
	}
	
	public static void elevatorDown(Map<FlightControl, Double> controls) {
		if (controls.get(FlightControl.ELEVATOR) <= FlightControl.ELEVATOR.getMaximum())
			controls.put(FlightControl.ELEVATOR, controls.get(FlightControl.ELEVATOR) + getRate(FlightControl.ELEVATOR));
	}
	
	public static void elevatorUp(Map<FlightControl, Double> controls) {
		if (controls.get(FlightControl.ELEVATOR) >= FlightControl.ELEVATOR.getMinimum())
			controls.put(FlightControl.ELEVATOR, controls.get(FlightControl.ELEVATOR) - getRate(FlightControl.ELEVATOR));
	}
	
	public static void aileronLeft(Map<FlightControl, Double> controls) {
		if (controls.get(FlightControl.AILERON) >= FlightControl.AILERON.getMinimum())
			controls.put(FlightControl.AILERON, controls.get(FlightControl.AILERON) + getRate(FlightControl.AILERON));
	}
	
	public static void aileronRight(Map<FlightControl, Double> controls) {
		if (controls.get(FlightControl.AILERON) <= FlightControl.AILERON.getMaximum())
			controls.put(FlightControl.AILERON, controls.get(FlightControl.AILERON) - getRate(FlightControl.AILERON));
	}
	
	public static void rudderLeft(Map<FlightControl, Double> controls) {
		if (controls.get(FlightControl.RUDDER) >= FlightControl.RUDDER.getMinimum())
			controls.put(FlightControl.RUDDER, controls.get(FlightControl.RUDDER) - getRate(FlightControl.RUDDER));
	}
	
	public static void rudderRight(Map<FlightControl, Double> controls) {
		if (controls.get(FlightControl.RUDDER) <= FlightControl.RUDDER.getMaximum())
			controls.put(FlightControl.RUDDER, controls.get(FlightControl.RUDDER) + getRate(FlightControl.RUDDER));
	}
	
	public static void centerControls(Map<FlightControl, Double> controls) {
		controls.put(FlightControl.ELEVATOR, trimElevator);
		controls.put(FlightControl.AILERON, trimAileron);
		controls.put(FlightControl.RUDDER, trimRudder);
	}
	
	public static void brakeRight(Map<FlightControl, Double> controls, double value) {
		controls.put(FlightControl.BRAKE_R, negativeSquare(value));
	}
	
	public static void brakeLeft(Map<FlightControl, Double> controls, double value) {
		controls.put(FlightControl.BRAKE_L, negativeSquare(value));
	}
	
	public static void throttle1(Map<FlightControl, Double> controls, double value) {
		controls.put(FlightControl.THROTTLE_1, -(value-1)/2);
	}
	
	public static void throttle2(Map<FlightControl, Double> controls, double value) {
		controls.put(FlightControl.THROTTLE_2, -(value-1)/2);
	}
	
	public static void propeller1(Map<FlightControl, Double> controls, double value) {
		controls.put(FlightControl.PROPELLER_1, -(value-1)/2);
	}
	
	public static void propeller2(Map<FlightControl, Double> controls, double value) {
		controls.put(FlightControl.PROPELLER_2, -(value-1)/2);
	}
	
	public static void mixture1(Map<FlightControl, Double> controls, double value) {
		controls.put(FlightControl.MIXTURE_1, -(value-1)/2);
	}
	
	public static void mixture2(Map<FlightControl, Double> controls, double value) {
		controls.put(FlightControl.MIXTURE_2, -(value-1)/2);
	}
	
	public static void increaseThrottle(Map<FlightControl, Double> controls) {
		if (controls.get(FlightControl.THROTTLE_1) <= FlightControl.THROTTLE_1.getMaximum() &&
			controls.get(FlightControl.THROTTLE_2) <= FlightControl.THROTTLE_2.getMaximum() &&
			controls.get(FlightControl.THROTTLE_3) <= FlightControl.THROTTLE_3.getMaximum() &&
			controls.get(FlightControl.THROTTLE_4) <= FlightControl.THROTTLE_4.getMaximum()) {
			
			controls.put(FlightControl.THROTTLE_1, controls.get(FlightControl.THROTTLE_1) + getRate(FlightControl.THROTTLE_1));
			controls.put(FlightControl.THROTTLE_2, controls.get(FlightControl.THROTTLE_2) + getRate(FlightControl.THROTTLE_2));
			controls.put(FlightControl.THROTTLE_3, controls.get(FlightControl.THROTTLE_3) + getRate(FlightControl.THROTTLE_3));
			controls.put(FlightControl.THROTTLE_4, controls.get(FlightControl.THROTTLE_4) + getRate(FlightControl.THROTTLE_4));
		}
	}
	
	public static void decreaseThrottle(Map<FlightControl, Double> controls) {
		if (controls.get(FlightControl.THROTTLE_1) >= FlightControl.THROTTLE_1.getMinimum() &&
			controls.get(FlightControl.THROTTLE_2) >= FlightControl.THROTTLE_2.getMinimum() &&
			controls.get(FlightControl.THROTTLE_3) >= FlightControl.THROTTLE_3.getMinimum() &&
			controls.get(FlightControl.THROTTLE_4) >= FlightControl.THROTTLE_4.getMinimum()) {
			
			controls.put(FlightControl.THROTTLE_1, controls.get(FlightControl.THROTTLE_1) - getRate(FlightControl.THROTTLE_1));
			controls.put(FlightControl.THROTTLE_2, controls.get(FlightControl.THROTTLE_2) - getRate(FlightControl.THROTTLE_2));
			controls.put(FlightControl.THROTTLE_3, controls.get(FlightControl.THROTTLE_3) - getRate(FlightControl.THROTTLE_3));
			controls.put(FlightControl.THROTTLE_4, controls.get(FlightControl.THROTTLE_4) - getRate(FlightControl.THROTTLE_4));
		}
	}

	public static void pauseSimulation(Set<Options> options, boolean isPressed) {
		if(isPressed && !options.contains(Options.PAUSED) && !pausePressed) {
			options.add(Options.PAUSED);
			logger.debug("Simulation Paused!");
			pausePressed = true;
		} else if(isPressed && options.contains(Options.PAUSED) && !pausePressed) {
			options.remove(Options.PAUSED);
			wasReset = false;
			pausePressed = true;
		} else if(!isPressed && pausePressed) {
			pausePressed = false;
		}
	}
	
	// When simulation paused, can be reset once per pause with "R" key
	public static void resetSimulation(Set<Options> options, boolean isPressed) {
		if(isPressed && options.contains(Options.PAUSED) && !options.contains(Options.RESET) && !resetPressed && !wasReset) {
			options.add(Options.RESET);
			logger.debug("Resetting simulation...");
			wasReset = true;
			resetPressed = true;
		} else if (!isPressed && resetPressed) {
			logger.debug("...done!");
			resetPressed = false;
		}
	}
	
	public static void stopSimulation(SimulationController simController) {
		//simController.stopSimulation();
	}
	
	public static void plotSimulation(SimulationController simController) {
		if(!simController.isPlotWindowVisible()) {
			simController.plotSimulation();
		}
	}
	
	/**
	 * Standardizes rate of control deflection of keyboard and joystick button inputs regardless of the 
	 * simulation update rate based on the {@link FlightControl} argument provided and the 
	 * 
	 * @param type
	 */
	private static double getRate(FlightControl type) {
		switch (type) {
		case AILERON:
		case ELEVATOR:
		case RUDDER:
			return 0.12 * dt;
		case THROTTLE_1:
		case THROTTLE_2:
		case THROTTLE_3:
		case THROTTLE_4:
		case PROPELLER_1:
		case PROPELLER_2:
		case PROPELLER_3:
		case PROPELLER_4:
		case MIXTURE_1:
		case MIXTURE_2:
		case MIXTURE_3:
		case MIXTURE_4:
			return 0.5 * dt;
		case FLAPS:
			return 0.15 * dt;
		default:
			return 0;
		}
	}
		
	/**
	 *  Uses maximum and minimum values defined in {@link FlightControl} to convert normalized 
	 *  joystick axis value to actual control deflection 
	 *  
	 * @param controlType
	 * @param value
	 * @return Actual control deflection
	 */
	private static double calculateDeflection(FlightControl controlType, double value) {
		// Calculate positive and negative slope
		// (elevator has different values for positive/negative max)
		if (value <= 0) 
			return (controlType.getMaximum()*Math.abs(value));
		else
			return (controlType.getMinimum()*value);
	}
	
	/**
	 * Squares a value without removing its sign if negative
	 * 
	 * @param value
	 * @return value squared that retains its original sign
	 */
	private static double negativeSquare(double value) {
		if (value < 0)
			return -(Math.pow(value, 2));
		else
			return Math.pow(value, 2);
	}
}
