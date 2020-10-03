package com.chrisali.javaflightsim.simulation.flightcontrols;

import static com.chrisali.javaflightsim.simulation.flightcontrols.FlightControl.*;

import java.util.ArrayList;
import java.util.List;

import com.chrisali.javaflightsim.simulation.datatransfer.SimulationEventListener;
import com.chrisali.javaflightsim.simulation.setup.IntegratorConfig;
import com.chrisali.javaflightsim.simulation.setup.KeyCommand;
import com.chrisali.javaflightsim.simulation.setup.SimulationConfiguration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Handles setting of values in the Maps in {@link FlightControlsState} for a given {@link ControlParameter} 
 * 
 * @author Christopher
 *
 */
public class FlightControlActuator implements ControlParameterActuator {
	
	private final Logger logger = LogManager.getLogger(FlightControlActuator.class);
	
	FlightControlsState controlsState;
	
	// Events
	private List<SimulationEventListener> simulationEventListeners = new ArrayList<>();
	
	// Scales the values in getRate() depending on the rate the simulation is running at
	private double dt = 0.05;
	
	// Add trim values to getDeflection() to emulate trim deflections
	private double trimElevator = 0.0;
	private double trimAileron  = 0.0;
	private double trimRudder   = 0.0;
	private double flaps   	    = 0.0;
	
	// Keep track if button is pressed, so events occur only once if button held down 
	private boolean gearPressed = false;
	private boolean gearLeverDown = false;
	
	// If true, don't directly calculate controls; use a transient value 
	private boolean useTransientLag = false;
	
	public FlightControlActuator(SimulationConfiguration configuration, FlightControlsState controlsState) {
		dt = configuration.getIntegratorConfig().get(IntegratorConfig.DT);
		
		this.controlsState = controlsState;
		
		gearLeverDown = controlsState.get(GEAR) == 1.0;
		
		resetTrimTabs();
	}

	public void resetTrimTabs() {
		trimAileron  = controlsState.getTrimValue(AILERON);
		trimElevator = controlsState.getTrimValue(ELEVATOR);
		trimRudder   = controlsState.getTrimValue(RUDDER);
	}
	
	@Override
	public void handleParameterChange(ControlParameter parameter, float value) {
		if(parameter.isRelative()) {
			handlePresses(parameter, value);
		}
		else {
			handleAxes(parameter, value);
		}
		
		continuous(GEAR, gearLeverDown ? GEAR.getMinimum() : GEAR.getMaximum());
	}

	/**
	 * Handles any control parameter that can be considered a button or key press
	 * 
	 * @param parameter
	 * @param value
	 */
	private void handlePresses(ControlParameter parameter, float value) {
		switch ((KeyCommand)parameter) {
			case AILERON_LEFT:
				aileronLeft();
				break;
			case AILERON_RIGHT:
				aileronRight();
				break;
			case AILERON_TRIM_LEFT:
				aileronTrimLeft();
				break;
			case AILERON_TRIM_RIGHT:
				aileronTrimRight();
				break;
			case BRAKES:
				pedal(BRAKE_L, BRAKE_L.getMaximum());
				pedal(BRAKE_R, BRAKE_R.getMaximum());
				break;
			case CENTER_CONTROLS:
				centerControls();
				break;
			case DECREASE_FLAPS:
				retractFlaps();
				break;
			case DECREASE_MIXTURE:
				break;
			case DECREASE_PROPELLER:
				break;
			case DECREASE_THROTTLE:
				decreaseThrottle();
				break;
			case ELEVATOR_DOWN:
				elevatorDown();
				break;
			case ELEVATOR_TRIM_DOWN:
				elevatorTrimDown();
				break;
			case ELEVATOR_TRIM_UP:
				elevatorTrimUp();
				break;
			case ELEVATOR_UP:
				elevatorUp();
				break;
			case EXIT_SIMULATION:
				simulationEventListeners.forEach(listener ->listener.onStopSimulation());					
				break;
			case GEAR_DOWN:
				extendGear();
				break;
			case GEAR_UP:
				retractGear();
				break;
			case GEAR_UP_DOWN:
				cycleGear();
				break;
			case GENERATE_PLOTS:
				simulationEventListeners.forEach(listener -> listener.onPlotSimulation());
				break;
			case INCREASE_FLAPS:
				extendFlaps();
				break;
			case INCREASE_MIXTURE:
				break;
			case INCREASE_PROPELLER:
				break;
			case INCREASE_THROTTLE:
				increaseThrottle();
				break;
			case PAUSE_UNPAUSE_SIM:
				simulationEventListeners.forEach(listener -> listener.onPauseUnpauseSimulation());
				break;
			case RESET_SIM:
				simulationEventListeners.forEach(listener -> listener.onResetSimulation());
				break;
			case RUDDER_LEFT:
				rudderLeft();
				break;
			case RUDDER_RIGHT:
				rudderRight();
				break;
			case RUDDER_TRIM_LEFT:
				rudderTrimLeft();
				break;
			case RUDDER_TRIM_RIGHT:
				rudderTrimRight();
				break;
			default:
				break;
		}
	}

	/**
	 * Handles any control parameter that can be considered an axis movement
	 * 
	 * @param parameter
	 * @param value
	 */
	private void handleAxes(ControlParameter parameter, float value) {
		switch ((FlightControl)parameter) {
			case AILERON:
				trimmableControl(AILERON, value, trimAileron);
				break;
			case BRAKE_L:
				pedal(BRAKE_L, value);
				break;
			case BRAKE_R:
				pedal(BRAKE_R, value);
				break;
			case ELEVATOR:
				trimmableControl(ELEVATOR, value, trimElevator);
				break;
			case FLAPS:
				break;
			case GEAR:
				break;
			case MIXTURE_1:
				lever(MIXTURE_1, value);
				break;
			case MIXTURE_2:
				lever(MIXTURE_2, value);
				break;
			case MIXTURE_3:
				lever(MIXTURE_3, value);
				break;
			case MIXTURE_4:
				lever(MIXTURE_4, value);
				break;
			case PROPELLER_1:
				lever(PROPELLER_1, value);
				break;
			case PROPELLER_2:
				lever(PROPELLER_2, value);
				break;
			case PROPELLER_3:
				lever(PROPELLER_3, value);
				break;
			case PROPELLER_4:
				lever(PROPELLER_4, value);
				break;
			case RUDDER:
				trimmableControl(RUDDER, value, trimRudder);
				break;
			case THROTTLE_1:
				lever(THROTTLE_1, value);
				break;
			case THROTTLE_2:
				lever(THROTTLE_2, value);
				break;
			case THROTTLE_3:
				lever(THROTTLE_3, value);
				break;
			case THROTTLE_4:
				lever(THROTTLE_4, value);
				break;
			default:
				break;
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
		double rateScale = 0.5;
		
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
		case GEAR:
			return 0.000015 / dt;
		default:
			return 0;
		}
	}

	/**
	 * Configures the actuator to consider transient lag when calculating the control deflection 
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
	 * Certain controls (flaps, gear, etc) continously, gradually actuate until they reach their desired position
	 * 
	 * @param type
	 * @param desiredValue
	 */
	private void continuous(FlightControl type, double desiredValue) {
		double currentValue = controlsState.get(type);
		
		if (currentValue != desiredValue && desiredValue > type.getMinimum())
			controlsState.set(type, (currentValue -= getRate(type)));
		else if (currentValue != desiredValue && desiredValue < type.getMaximum()) 
			controlsState.set(type, (currentValue += getRate(type)));
	}
	
	/** 
	 * Cycles Landing Gear Down/Up. Uses gearPressed so that the key needs to be released to extend or retract gear again
	 * 
	 * @param controls
	 */
	private void cycleGear() {
		if (!gearPressed) {
			gearLeverDown = gearLeverDown ? false : true;
			gearPressed = true;
		} else if (gearPressed) {
			gearPressed = false;
		}
	}
	
	private void retractGear() {
		gearLeverDown = false;
	}

	private void extendGear() {
		gearLeverDown = true;
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
	
	public void addSimulationEventListener(SimulationEventListener listener) {
		if (simulationEventListeners != null) {
			logger.info("Adding simulation event listener: " + listener.getClass());
			simulationEventListeners.add(listener);
		}
	}
}
