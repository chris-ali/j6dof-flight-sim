package com.chrisali.javaflightsim.simulation.flightcontrols;

import static com.chrisali.javaflightsim.simulation.flightcontrols.FlightControl.*;
import static com.chrisali.javaflightsim.simulation.setup.KeyCommand.*;

import com.chrisali.javaflightsim.simulation.setup.IntegratorConfig;
import com.chrisali.javaflightsim.simulation.setup.SimulationConfiguration;

/**
 * Handles setting of values in the Maps in {@link FlightControlsState} for a given {@link ControlParameter} 
 * 
 * @author Christopher
 *
 */
public class FlightControlActuator implements ControlParameterActuator {
	
	FlightControlsState controlsState;
	
	private double dt = 0.05;
	
	// Add trim values to getDeflection() to emulate trim deflections
	private double trimElevator = 0.0;
	private double trimAileron  = 0.0;
	private double trimRudder   = 0.0;
	private double flaps   	    = 0.0;
	
	// Keep track if button is pressed, so events occur only once if button held down 
	private boolean gearPressed = false;
	
	// If true, don't directly calculate controls; use a transient value 
	private boolean useTransientLag = true;
	
	public FlightControlActuator(SimulationConfiguration configuration, FlightControlsState controlsState) {
		dt = configuration.getIntegratorConfig().get(IntegratorConfig.DT);
		
		this.controlsState = controlsState;
		
		trimAileron  = controlsState.getTrimValue(AILERON);
		trimElevator = controlsState.getTrimValue(ELEVATOR);
		trimRudder   = controlsState.getTrimValue(RUDDER);
	}
	
	// Need to find a better way of selecting the right method to call
	@Override
	public void handleParameterChange(ControlParameter parameter, float value) {
		if(parameter.isRelative()) {
			if (parameter.equals(AILERON_LEFT)) {
				if (isPressed(value)) aileronLeft();
			} else if (parameter.equals(AILERON_RIGHT)) {
				if (isPressed(value)) aileronRight();
			} else if (parameter.equals(AILERON_TRIM_LEFT)) {
				if (isPressed(value)) aileronTrimLeft();
			} else if (parameter.equals(AILERON_TRIM_RIGHT)) {
				if (isPressed(value)) aileronTrimRight();
			} else if (parameter.equals(BRAKES)) {
				pedal(BRAKE_L, BRAKE_L.getMaximum());
				pedal(BRAKE_R, BRAKE_R.getMaximum());
			} else if (parameter.equals(CENTER_CONTROLS)) {
				if (isPressed(value)) centerControls();
			} else if (parameter.equals(DECREASE_FLAPS)) {
				if (isPressed(value)) retractFlaps();
			} else if (parameter.equals(DECREASE_MIXTURE)) {
			} else if (parameter.equals(DECREASE_PROPELLER)) {
			} else if (parameter.equals(DECREASE_THROTTLE)) {
				if (isPressed(value)) decreaseThrottle();
			} else if (parameter.equals(ELEVATOR_DOWN)) {
				if (isPressed(value)) elevatorDown();
			} else if (parameter.equals(ELEVATOR_UP)) {
				if (isPressed(value)) elevatorUp();
			} else if (parameter.equals(ELEVATOR_TRIM_DOWN)) {
				if (isPressed(value)) elevatorTrimDown();
			} else if (parameter.equals(ELEVATOR_TRIM_UP)) {
				if (isPressed(value)) elevatorTrimUp();
			} else if (parameter.equals(GEAR_UP_DOWN)) {
				cycleGear(isPressed(value));
			} else if (parameter.equals(GEAR_DOWN)) {
				if (isPressed(value)) extendGear();
			} else if (parameter.equals(GEAR_UP)) {
				if (isPressed(value)) retractGear();
			} else if (parameter.equals(INCREASE_FLAPS)) {
				if (isPressed(value)) extendFlaps();
			} else if (parameter.equals(INCREASE_MIXTURE)) {
			} else if (parameter.equals(INCREASE_PROPELLER)) {
			} else if (parameter.equals(INCREASE_THROTTLE)) {
				if (isPressed(value)) increaseThrottle();
			} else if (parameter.equals(RUDDER_LEFT)) {
				if (isPressed(value)) rudderLeft();
			} else if (parameter.equals(RUDDER_RIGHT)) {
				if (isPressed(value)) rudderRight();
			} else if (parameter.equals(RUDDER_TRIM_LEFT)) {
				if (isPressed(value)) rudderTrimLeft();
			} else if (parameter.equals(RUDDER_TRIM_RIGHT)) {
				if (isPressed(value)) rudderTrimRight();
			} else if (parameter.equals(PAUSE_UNPAUSE_SIM)) {
				SimEvents.pauseUnpauseSimulation(isPressed(value));
			} else if (parameter.equals(RESET_SIM)) {
				SimEvents.resetSimulation(isPressed(value));
			} else if (parameter.equals(GENERATE_PLOTS)) {
				if (isPressed(value)) SimEvents.plotSimulation();
			} 
		}
		else {
			if (parameter.equals(AILERON)) {
				trimmableControl(AILERON, value, trimAileron);
			} else if (parameter.equals(BRAKE_L)) {
				pedal(BRAKE_L, value);
			} else if (parameter.equals(BRAKE_R)) {
				pedal(BRAKE_R, value);
			} else if (parameter.equals(ELEVATOR)) {
				trimmableControl(ELEVATOR, value, trimElevator);
			} else if (parameter.equals(FLAPS)) {
			} else if (parameter.equals(GEAR)) {
			} else if (parameter.equals(MIXTURE_1)) {
				lever(MIXTURE_1, value);
			} else if (parameter.equals(MIXTURE_2)) {
				lever(MIXTURE_2, value);
			} else if (parameter.equals(MIXTURE_3)) {
				lever(MIXTURE_3, value);
			} else if (parameter.equals(MIXTURE_4)) {
				lever(MIXTURE_4, value);
			} else if (parameter.equals(PROPELLER_1)) {
				lever(PROPELLER_1, value);
			} else if (parameter.equals(PROPELLER_2)) {
				lever(PROPELLER_2, value);
			} else if (parameter.equals(PROPELLER_3)) {
				lever(PROPELLER_3, value);
			} else if (parameter.equals(PROPELLER_4)) {
				lever(PROPELLER_4, value);
			} else if (parameter.equals(RUDDER)) {
				trimmableControl(RUDDER, value, trimRudder);
			} else if (parameter.equals(THROTTLE_1)) {
				lever(THROTTLE_1, value);
			} else if (parameter.equals(THROTTLE_2)) {
				lever(THROTTLE_2, value);
			} else if (parameter.equals(THROTTLE_3)) {
				lever(THROTTLE_3, value);
			} else if (parameter.equals(THROTTLE_4)) {
				lever(THROTTLE_4, value);
			}
		}
	}
		
	/**
	 *  Using a transient control value saved in {@link FlightControlsState}, calculates a control value based on a linear
	 *  rate defined by getRate() and the desired "direct" control value. If useTransientLag is set to false, the direct
	 *  value from the input device will be used instead
	 *  
	 * @param controlType
	 * @param value
	 * @return Actual control value
	 */
	private double calculateDeflection(FlightControl controlType, double value) {
		// In-between value updated each simulation step
		double transientValue = controlsState.getTransientValue(controlType);
		
		// Final value that the transient value seeks out
		double desiredValue = (value <= 0) ? (controlType.getMaximum()*Math.abs(value)) : (controlType.getMinimum()*value);   
						
		// Scale the rate as desired and transient values near each other
		double rateScale = 0.125; //Math.abs((desiredValue - transientValue) / (value <= 0 ? controlType.getMaximum() : controlType.getMinimum())); //
		
		transientValue += (desiredValue - transientValue) * rateScale;
			
		controlsState.setTransientValue(controlType, transientValue);
		
		return useTransientLag ? transientValue : desiredValue;
	}
	
	/**
	 * Squares a value without removing its sign if negative
	 * 
	 * @param value
	 * @return value squared that retains its original sign
	 */
	private double negativeSquare(double value) {
		if (value < 0)
			return -(Math.pow(value, 2));
		else
			return Math.pow(value, 2);
	}
	
	/**
	 * @param value
	 * @return if a relative {@link FlightControl} parameter is pressed
	 */
	private boolean isPressed(float value) { return value == 1.0; }
	
	/**
	 * Standardizes rate of control deflection of keyboard and joystick button inputs regardless of the 
	 * simulation update rate based on the {@link FlightControl} argument provided
	 * 
	 * @param type
	 */
	private double getRate(FlightControl type) {
		switch (type) {
		case AILERON:
		case ELEVATOR:
		case RUDDER:
			return 0.00012 / dt;
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
			return 0.0005 / dt;
		case FLAPS:
			return 0.00015 / dt;
		default:
			return 0;
		}
	}

	/**
	 * Configures the actuator to consider transient lagn when calculating the control deflection 
	 * 
	 * @param useTransientLag
	 */
	public void setUseTransientLag(boolean useTransientLag) { this.useTransientLag = useTransientLag; }
	
	/**
	 * Flight controls that have a trim control associated with them (elevator, rudder, aileron) 
	 * should use this method to calculate their actual value; includes a call to negativeSquare()
	 * to reduce the linearity of the polled value, giving a "gentler" response
	 * 
	 * @param type flight control in question
	 * @param value polled value from an input device
	 * @param trimValue saved trim value 
	 */
	private void trimmableControl(FlightControl type, double value, double trimValue) {
		double deflection = calculateDeflection(type, negativeSquare(value));	
		controlsState.set(type, (deflection + trimValue));
	}
	
	/**
	 * Flight controls without trim (brakes) should use this; includes a call to negativeSquare()
	 * to reduce the linearity of the polled value, giving a "gentler" response
	 * 
	 * @param type
	 * @param value
	 */
	private void pedal(FlightControl type, double value) {
		controlsState.set(type, negativeSquare(value));
	}
	
	/**
	 * Certain lever-based flight controls (throttle, propeller, mixture) require an shift of their polled 
	 * value to provide a value between 0 and 1; no smoothing is added via negativeSquare()
	 * 
	 * @param type
	 * @param value
	 */
	private void lever(FlightControl type, double value) {
		controlsState.set(type, -(value-1)/2);
	}
	
	/** 
	 * Cycles Landing Gear Down/Up. Use gearPressed to prevent numerous cycles of gear up/down if key held down;
	 * need to release key to extend or retract gear again
	 * 
	 * @param controls
	 * @param buttonPressed
	 */
	private void cycleGear(boolean buttonPressed) {
		if (!gearPressed && controlsState.get(GEAR) < 0.5) {
			if(buttonPressed) {
				controlsState.set(GEAR, 1.0);
				gearPressed = true;
			}
		} else if (!gearPressed && controlsState.get(GEAR) > 0.5) {
			if(buttonPressed) {
				controlsState.set(GEAR, 0.0);
				gearPressed = true;
			}
		} else if (gearPressed && !buttonPressed) {
			gearPressed = false;
		}
	}
	
	private void retractGear() {
		controlsState.set(GEAR, GEAR.getMinimum());	
	}

	private void extendGear() {
		controlsState.set(GEAR, GEAR.getMaximum());
	}

	private void retractFlaps() {
		if (flaps >= FLAPS.getMinimum())
			controlsState.set(FLAPS, (flaps -= getRate(FLAPS)));
	}
	
	private void extendFlaps() {
		if (flaps <= FLAPS.getMaximum()) 
			controlsState.set(FLAPS, (flaps += getRate(FLAPS)));
	}
	
	private void aileronTrimLeft() {
		if(trimAileron >= AILERON.getMinimum()) 
			trimAileron += getRate(AILERON)/10;
	}
	
	private void aileronTrimRight() {
		if(trimAileron <= AILERON.getMaximum()) 
			trimAileron -= getRate(AILERON)/10;
	}
	
	private void rudderTrimRight() {
		if(trimRudder >= RUDDER.getMinimum()) 
			trimRudder -= getRate(RUDDER)/10;
	}
	
	private void rudderTrimLeft() {
		if(trimRudder <= RUDDER.getMaximum()) 
			trimRudder += getRate(RUDDER)/10;
	}
	
	private void elevatorTrimDown() {
		if (trimElevator <= ELEVATOR.getMaximum()) 
			trimElevator += getRate(ELEVATOR)/10;
	}
	
	private void elevatorTrimUp() {
		if (trimElevator >= ELEVATOR.getMinimum()) 
			trimElevator -= getRate(ELEVATOR)/10;
	}

	private void elevatorDown() {
		if (controlsState.get(ELEVATOR) <= ELEVATOR.getMaximum())
			controlsState.set(ELEVATOR, controlsState.get(ELEVATOR) + getRate(ELEVATOR));
	}
	
	private void elevatorUp() {
		if (controlsState.get(ELEVATOR) >= ELEVATOR.getMinimum())
			controlsState.set(ELEVATOR, controlsState.get(ELEVATOR) - getRate(ELEVATOR));
	}
	
	private void aileronLeft() {
		if (controlsState.get(AILERON) >= AILERON.getMinimum())
			controlsState.set(AILERON, controlsState.get(AILERON) + getRate(AILERON));
	}
	
	private void aileronRight() {
		if (controlsState.get(AILERON) <= AILERON.getMaximum())
			controlsState.set(AILERON, controlsState.get(AILERON) - getRate(AILERON));
	}
	
	private void rudderLeft() {
		if (controlsState.get(RUDDER) >= RUDDER.getMinimum())
			controlsState.set(RUDDER, controlsState.get(RUDDER) - getRate(RUDDER));
	}
	
	private void rudderRight() {
		if (controlsState.get(RUDDER) <= RUDDER.getMaximum())
			controlsState.set(RUDDER, controlsState.get(RUDDER) + getRate(RUDDER));
	}
	
	private void centerControls() {
		controlsState.set(ELEVATOR, trimElevator);
		controlsState.set(AILERON, trimAileron);
		controlsState.set(RUDDER, trimRudder);
	}
		
	private void increaseThrottle() {
		if (controlsState.get(THROTTLE_1) <= THROTTLE_1.getMaximum() &&
			controlsState.get(THROTTLE_2) <= THROTTLE_2.getMaximum() &&
			controlsState.get(THROTTLE_3) <= THROTTLE_3.getMaximum() &&
			controlsState.get(THROTTLE_4) <= THROTTLE_4.getMaximum()) {
			
			controlsState.set(THROTTLE_1, controlsState.get(THROTTLE_1) + getRate(THROTTLE_1));
			controlsState.set(THROTTLE_2, controlsState.get(THROTTLE_2) + getRate(THROTTLE_2));
			controlsState.set(THROTTLE_3, controlsState.get(THROTTLE_3) + getRate(THROTTLE_3));
			controlsState.set(THROTTLE_4, controlsState.get(THROTTLE_4) + getRate(THROTTLE_4));
		}
	}
	
	private void decreaseThrottle() {
		if (controlsState.get(THROTTLE_1) >= THROTTLE_1.getMinimum() &&
			controlsState.get(THROTTLE_2) >= THROTTLE_2.getMinimum() &&
			controlsState.get(THROTTLE_3) >= THROTTLE_3.getMinimum() &&
			controlsState.get(THROTTLE_4) >= THROTTLE_4.getMinimum()) {
			
			controlsState.set(THROTTLE_1, controlsState.get(THROTTLE_1) - getRate(THROTTLE_1));
			controlsState.set(THROTTLE_2, controlsState.get(THROTTLE_2) - getRate(THROTTLE_2));
			controlsState.set(THROTTLE_3, controlsState.get(THROTTLE_3) - getRate(THROTTLE_3));
			controlsState.set(THROTTLE_4, controlsState.get(THROTTLE_4) - getRate(THROTTLE_4));
		}
	}
}
