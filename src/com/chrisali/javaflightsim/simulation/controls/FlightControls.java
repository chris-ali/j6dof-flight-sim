package com.chrisali.javaflightsim.simulation.controls;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;

import com.chrisali.javaflightsim.controllers.SimulationController;
import com.chrisali.javaflightsim.datatransfer.FlightData;
import com.chrisali.javaflightsim.datatransfer.FlightDataListener;
import com.chrisali.javaflightsim.simulation.controls.hidcontrollers.AbstractController;
import com.chrisali.javaflightsim.simulation.controls.hidcontrollers.CHControls;
import com.chrisali.javaflightsim.simulation.controls.hidcontrollers.Joystick;
import com.chrisali.javaflightsim.simulation.controls.hidcontrollers.Keyboard;
import com.chrisali.javaflightsim.simulation.controls.hidcontrollers.Mouse;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.setup.IntegratorConfig;
import com.chrisali.javaflightsim.simulation.setup.Options;

/**
 * Contains the Flight Controls thread used to handle flight controls actuated by human interface devices, such as
 * {@link Joystick}, {@link Keyboard}, {@link Mouse} or {@link CHControls}, or by P/PD controllers such as autopilots
 * and stability augmentation sytems. Also contains method to inject doublets into controls when simulation is run
 * as analysis. Uses {@link FlightDataListener} to feed back {@link FlightData} to use in P/PD controllers
 * 
 * 
 * @author Christopher Ali
 *
 */
public class FlightControls implements Runnable, FlightDataListener {

	private static boolean running;
	
	private Map<FlightControlType, Double> controls;
	
	private Map<IntegratorConfig, Double> integratorConfig;
	private EnumSet<Options> options;
//	private Map<FlightDataType, Double> flightData;
	
	private AbstractController hidController;
	private Keyboard hidKeyboard;
	
	/**
	 * Constructor for {@link FlightControls}; {@link SimulationController} argument to initialize {@link IntegratorConfig} 
	 * EnumMap, the {@link Options} EnumSet, as well as to update simulation options and call simulation methods
	 * 
	 * @param options
	 */
	public FlightControls(SimulationController simController) {
		this.controls = simController.getInitialControls();
		this.integratorConfig = simController.getIntegratorConfig();
		this.options = simController.getSimulationOptions();
		
		this.hidKeyboard = new Keyboard(controls, simController);
		
		// initializes static EnumMap that contains trim values of controls for doublets 
		FlightControlsUtilities.init();
	}
	
	@Override
	public void run() {
		// Use controllers for pilot in loop simulation if ANALYSIS_MODE not enabled 
		if (!options.contains(Options.ANALYSIS_MODE)) {
			if (options.contains(Options.USE_JOYSTICK))
				hidController = new Joystick(controls);
			else if (options.contains(Options.USE_MOUSE))
				hidController = new Mouse(controls);
			else if (options.contains(Options.USE_CH_CONTROLS))
				hidController = new CHControls(controls);
		}
		
		running = true;
		
		while (running) {
			try {
				// if not running in analysis mode, controls and options should be updated using updateFlightControls()/updateOptions()
				if (!options.contains(Options.ANALYSIS_MODE)) {
					if (hidController != null) 
						controls = hidController.updateFlightControls(controls);
					
					controls = hidKeyboard.updateFlightControls(controls);
					
					hidKeyboard.hotKeys();
					
					Thread.sleep((long) (integratorConfig.get(IntegratorConfig.DT)*1000));
				// in analysis mode, controls updated using generated doublets instead of pilot input
				} else {
					controls = FlightControlsUtilities.doubletSeries(controls, Integrate6DOFEquations.getTime());
				}
				
			} catch (InterruptedException e) {}
		}
		
		running = false;
	}

	/**
	 * Receive fed back flight data to be used with P/PD controllers
	 */
	@Override
	public void onFlightDataReceived(FlightData flightData) {
//		if (flightData!= null)
//			this.flightData = flightData.getFlightData();
	}
	
	/**
	 * Returns thread-safe map containing flight controls data, with {@link FlightControlType} as the keys 
	 * 
	 * @return controls
	 */
	public synchronized Map<FlightControlType, Double> getFlightControls() {return Collections.unmodifiableMap(controls);}

	/**
	 * Lets other objects know if {@link FlightControls} thread is running
	 * 
	 * @return if {@link FlightControls} thread is running
	 */
	public static synchronized boolean isRunning() {return running;}

	/**
	 * Lets other objects request to stop the {@link FlightControls} thread by setting running to false
	 * 
	 * @param running
	 */
	public static synchronized void setRunning(boolean running) {FlightControls.running = running;}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		for (Map.Entry<FlightControlType, Double> entry : controls.entrySet()) {
			sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
		}
		sb.append("\n");
		
		return sb.toString();
	}
}
