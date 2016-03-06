package com.chrisali.javaflightsim.simulation.integration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;

import com.chrisali.javaflightsim.simulation.aero.AccelAndMoments;
import com.chrisali.javaflightsim.simulation.aircraft.Aircraft;
import com.chrisali.javaflightsim.simulation.aircraft.AircraftBuilder;
import com.chrisali.javaflightsim.simulation.controls.FlightControls;
import com.chrisali.javaflightsim.simulation.controls.FlightControlsUtilities;
import com.chrisali.javaflightsim.simulation.controls.hidcontrollers.Joystick;
import com.chrisali.javaflightsim.simulation.controls.hidcontrollers.Keyboard;
import com.chrisali.javaflightsim.simulation.controls.hidcontrollers.Mouse;
import com.chrisali.javaflightsim.simulation.controls.hidcontrollers.AbstractController;
import com.chrisali.javaflightsim.simulation.enviroment.Environment;
import com.chrisali.javaflightsim.simulation.enviroment.EnvironmentParameters;
import com.chrisali.javaflightsim.simulation.propulsion.Engine;
import com.chrisali.javaflightsim.simulation.setup.IntegrationSetup;
import com.chrisali.javaflightsim.simulation.setup.Options;
import com.chrisali.javaflightsim.utilities.Utilities;
import com.chrisali.javaflightsim.utilities.plotting.MakePlots;
import com.chrisali.javaflightsim.utilities.plotting.SimulationPlots;

/**
 * This class integrates all 12 6DOF (plus 2 latitude/longitude) equations numerically to obtain the aircraft's states.
 * The {@link ClassicalRungeKuttaIntegrator} is used to integrate over a period of time defined in {@link Integrate6DOFEquations#integratorConfig}.
 * It uses threading to delay the integration to emulate running at a real-time rate. The class outputs at each step using {@link Integrate6DOFEquations#logData(double)} to 
 * generate a {@link Integrate6DOFEquations#logsOut} ArrayList of {@link Integrate6DOFEquations#simOut} EnumMaps containing simulation outputs.
 * These can be obtained using the proper getters for {@link Integrate6DOFEquations#logsOut} and {@link Integrate6DOFEquations#simOut}. Options are passed into the class to
 * allow the user to choose between various run-time options 
 * 
 * @param  AircraftBuilder builtAircraft
 * @param  EnumSet runOptions
 *      
 * @return EnumMap simOut
 * @return ArrayList logsOut
 *      
 * @see FirstOrderDifferentialEquations 
 * @see ClassicalRungeKuttaIntegrator
 * @see AircraftBuilder
 * @see Options
 */
public class Integrate6DOFEquations implements Runnable {
	// 6DOF Integration Results
	private double[] linearVelocities 		= new double[3];
	private double[] NEDPosition      		= new double[3];
	private double[] eulerAngles      		= new double[3];
	private double[] angularRates     		= new double[3];
	
	// Environment and Wind Parameters
	private EnumMap<EnvironmentParameters, Double> environmentParameters;
	private double   gravity			    = Environment.getGravity();
	private double[] windParameters   		= new double[3];	
	private double   alphaDot 				= 0.0f;
	private double   mach     				= 0.0f;
	
	// Forces and Moments
	private double[] linearAccelerations    = new double[3];
	private double[] totalMoments     		= new double[3];
	
	// Simulation controls (Joystick, Keyboard, etc.)
	private EnumMap<FlightControls, Double> controls;
	private AbstractController hidController;
	private Keyboard hidKeyboard;
	
	// Integrator Fields
	private ClassicalRungeKuttaIntegrator integrator;
	private double[] sixDOFDerivatives		= new double[14];
	private double[] y					    = new double[14];
	private double[] initialConditions      = new double[14]; 
	private double[] integratorConfig		= new double[3];
	
	// Aircraft Properties
	private Aircraft aircraft;
	private Set<Engine> engineList;
	private AccelAndMoments accelAndMoments;
	
	// Output Logging
	private ArrayList<EnumMap<SimOuts, Double>> logsOut = new ArrayList<>();
	private EnumMap<SimOuts, Double> simOut;
	
	// Options
	private EnumSet<Options> options;
	private boolean running;
	
	/**
	 * Creates the {@link Integrate6DOFEquations} object with an {@link AircraftBuilder object} and a list of run-time options defined
	 * in the {@link Options} EnumSet
	 * 
	 * @param builtAircraft
	 * @param runOptions
	 */
	public Integrate6DOFEquations(AircraftBuilder builtAircraft,
								  EnumSet<Options> runOptions) {
		this.aircraft 		   = builtAircraft.getAircraft();
		this.engineList   	   = builtAircraft.getEngineList();
		this.accelAndMoments   = new AccelAndMoments(aircraft);
		this.options		   = runOptions;
		
		this.controls 		   = IntegrationSetup.gatherInitialControls("InitialControls");
		
		// If TRIM_MODE enabled, use the initial conditions/controls from the trim method
		if (options.contains(Options.TRIM_MODE)) {
			this.initialConditions = Utilities.unboxDoubleArray(IntegrationSetup.gatherInitialConditions("nextStepInitialConditions"));
			this.integratorConfig  = Utilities.unboxDoubleArray(IntegrationSetup.gatherIntegratorConfig("nextStepIntegratorConfig"));
		} else {
			this.initialConditions = Utilities.unboxDoubleArray(IntegrationSetup.gatherInitialConditions("InitialConditions"));
			this.integratorConfig  = Utilities.unboxDoubleArray(IntegrationSetup.gatherIntegratorConfig("IntegratorConfig"));
		}

		// If USE_JOYSTICK/USE_MOUSE enabled, use joystick/mouse if ANALYSIS_MODE not enabled
		if (options.contains(Options.USE_JOYSTICK) & !options.contains(Options.USE_MOUSE) & !options.contains(Options.ANALYSIS_MODE) & !options.contains(Options.TRIM_MODE))
			this.hidController = new Joystick(controls);
		else if (options.contains(Options.USE_MOUSE) & !options.contains(Options.ANALYSIS_MODE))
			this.hidController = new Mouse(controls);
		else
			this.hidController = null;
		
		// If ANALYSIS_MODE not enabled use keyboard
		if (!options.contains(Options.ANALYSIS_MODE))
			this.hidKeyboard = new Keyboard(controls);
		else
			this.hidKeyboard = null;
		
		// Lets simulation run forever when UNLIMITED_FLIGHT enabled, and ANALYSIS_MODE and TRIM_MODE are disabled
		if(options.contains(Options.UNLIMITED_FLIGHT) & !options.contains(Options.ANALYSIS_MODE) & !options.contains(Options.TRIM_MODE))
			this.integratorConfig[2] = Double.POSITIVE_INFINITY;
		
		// Use fourth-order Runge-Kutta numerical integration with time step of dt
		this.integrator = new ClassicalRungeKuttaIntegrator(integratorConfig[1]);
		
		// Calculate initial data members' values
		updateDataMembers(initialConditions, integratorConfig[0]);
	}
	
	/**
	 * Creates the 14 (12 6DOF + 2 lat/lon) state derivatives that {@link Integrate6DOFEquations#integrator} uses to numerically integrate. It loops through {@link Integrate6DOFEquations#sixDOFDerivatives} to
	 * assign values to yDot[], which is used in the single step {@link Integrate6DOFEquations#integrator} in {@link Integrate6DOFEquations#run()}
	 * @see FirstOrderDifferentialEquations 
	 * @see Integrate6DOFEquations
	 * @see ClassicalRungeKuttaIntegrator
	 */
	private class SixDOFEquations implements FirstOrderDifferentialEquations {		
		private SixDOFEquations() {}

		public void computeDerivatives(double t, double[] y, double[] yDot) {
			for (int i = 0; i < yDot.length; i++) 
				yDot[i] = sixDOFDerivatives[i];
		}

		public int getDimension() {return 14;}
	}
	
	/**
	 * Recalculates the 14 (12 6DOF + 2 lat/lon) state derivatives based on the newly calculated accelerations and moments accomplished in {@link Integrate6DOFEquations#updateDataMembers(double[], double)}.
	 * The equations are calculated with the help of methods in {@link SixDOFUtilities} to convert coordinate frames and calculate inertia parameters
	 * @return ydot[]
	 * @see Source: <i>Small Unmanned Aircraft: Theory and Practice by Beard, R.W. and McLain, T.W.</i>
	 */
	private double[] updateDerivatives(double[] y) {
		double[]   yDot          = new double[14];
		double[][] dirCosMat     = SixDOFUtilities.body2Ned(new double[]{y[6], y[7], y[8]});      // create DCM for NED equations ([row][column])
		double[]   inertiaCoeffs = SixDOFUtilities.calculateInertiaCoeffs(Utilities.unboxDoubleArray(aircraft.getInertiaValues()));
		double[]   ned2LLA       = SixDOFUtilities.ned2LLA(y);
		
		yDot[0]  = (y[11]*y[1])-(y[10]*y[2])-(gravity*Math.sin(y[7]))               +linearAccelerations[0];    // u (ft/sec)
		yDot[1]  = (y[9]* y[2])-(y[11]*y[0])+(gravity*Math.sin(y[6])*Math.cos(y[7]))+linearAccelerations[1];    // v (ft/sec)
		yDot[2]  = (y[10]*y[0])-(y[9]* y[1])+(gravity*Math.cos(y[6])*Math.cos(y[7]))+linearAccelerations[2];    // w (ft/sec)
		
		yDot[3]  = (y[0]*dirCosMat[0][0]+y[1]*dirCosMat[0][1]+y[2]*dirCosMat[0][2]);    // N (ft)
		yDot[4]  = (y[0]*dirCosMat[1][0]+y[1]*dirCosMat[1][1]+y[2]*dirCosMat[1][2]);    // E (ft)
		yDot[5]  = (y[0]*dirCosMat[2][0]+y[1]*dirCosMat[2][1]+y[2]*dirCosMat[2][2])*-1; // D (ft)
		
		yDot[6]  =   y[9]+(Math.tan(y[7])*((y[10]*Math.sin(y[6]))+(y[11]*Math.cos(y[6])))); // phi (rad)
		yDot[7]  =  (y[10]*Math.cos(y[6]))-(y[11]*Math.sin(y[6]));     			            // theta (rad)
		yDot[8]  = ((y[10]*Math.sin(y[6]))+(y[11]*Math.cos(y[6])))/Math.cos(y[7]);          // psi (rad)
		
		yDot[9]  = ((inertiaCoeffs[1]*y[9]*y[10]) - (inertiaCoeffs[0]*y[10])*y[11]) + (inertiaCoeffs[2]*totalMoments[0])+(inertiaCoeffs[3]*totalMoments[2]);     // p (rad/sec)
		yDot[10] =  (inertiaCoeffs[4]*y[9]*y[11]) - (inertiaCoeffs[5]*((y[9]*y[9])-(y[11]*y[11])))                      +(inertiaCoeffs[6]*totalMoments[1]);     // q (rad/sec)
		yDot[11] = ((inertiaCoeffs[7]*y[9]*y[10]) - (inertiaCoeffs[1]*y[10]*y[11])) + (inertiaCoeffs[3]*totalMoments[0])+(inertiaCoeffs[8]*totalMoments[2]);     // r (rad/sec)
		
		yDot[12] = yDot[3]*ned2LLA[0]; // Latitude  (rad)
		yDot[13] = yDot[4]*ned2LLA[1]; // Longitude (rad)
		
		return yDot;
	}
	
	/**
	 *  Runs various helper methods to update data members in {@link Integrate6DOFEquations}. It updates the 6DOF states, environment parameters, controls, engine state, and finally 
	 *  calculates accelerations and moments to be used in {@link Integrate6DOFEquations#updateDerivatives(double[])} 
	 */
	private void updateDataMembers(double[] y, double t) {
		// Assign indices in yTemp array to 6DOF state arrays
		for (int i=0; i<linearVelocities.length; i++) {
			this.linearVelocities[i] = y[i];
			this.NEDPosition[i]      = y[i+3];
			this.eulerAngles[i]      = y[i+6];
			this.angularRates[i]     = y[i+9];
		}

		// Implement saturation and (2)pi bounding to keep states within realistic limits
		this.linearVelocities = SaturationLimits.limitLinearVelocities(linearVelocities);
		this.eulerAngles      = SaturationLimits.piBounding(eulerAngles);
		this.angularRates     = SaturationLimits.limitAngularRates(angularRates);
		
		// Update wind parameters
		this.windParameters = SixDOFUtilities.calculateWindParameters(linearVelocities);
		
		// Update environment		
		this.environmentParameters = Environment.updateEnvironmentParams(NEDPosition);
		
		// Update controls with joystick, keyboard or mouse; if in analysis mode, create a series of doublets (aileron, rudder and then elevator)
		if (!options.contains(Options.ANALYSIS_MODE) & !options.contains(Options.TRIM_MODE)) {
			this.controls = hidController.updateFlightControls(controls);
			this.controls = hidKeyboard.updateFlightControls(controls);
		} else if (options.contains(Options.ANALYSIS_MODE) & !options.contains(Options.TRIM_MODE)) {	
			this.controls = FlightControlsUtilities.doubletSeries(controls, t);
		}
		
		// Update all engines in engine list
		for(Engine engine : this.engineList)
			 engine.updateEngineState(controls, environmentParameters, windParameters);
		
		// Update alphaDot
		this.alphaDot = SixDOFUtilities.calculateAlphaDot(linearVelocities, sixDOFDerivatives);
		
		// Update mach
		this.mach = SixDOFUtilities.calculateMach(windParameters, environmentParameters);
		
		// Update accelerations
		this.linearAccelerations = accelAndMoments.calculateLinearAccelerations(windParameters,
																			    angularRates,
																			    environmentParameters,
																			    controls,
																			    alphaDot,
																			    engineList,
																			    aircraft);
		// Update moments
		this.totalMoments = accelAndMoments.calculateTotalMoments(windParameters,
															 	  angularRates,
																  environmentParameters,
																  controls,
																  alphaDot,
																  engineList,
																  aircraft);
				
		// Recalculates derivatives for next step
		this.sixDOFDerivatives = updateDerivatives(new double[] {linearVelocities[0],
																 linearVelocities[1],
																 linearVelocities[2],
																 NEDPosition[0],
																 NEDPosition[1],
																 NEDPosition[2],
																 eulerAngles[0],
																 eulerAngles[1],
																 eulerAngles[2],
																 angularRates[0],
																 angularRates[1],
																 angularRates[2]});
	}
	
	/**
	 *  Adds simulation data to the ArrayList {@link Integrate6DOFEquations#getLogsOut()} after each successful step of integration for plotting and outputs to the console, if set in {@link Integrate6DOFEquations#options}. 
	 *  The data calculated in each step of integration is available in the EnumMap {@link Integrate6DOFEquations#getSimOut()} 
	 */
	private void logData(double t) {
		// Make a new EnumMap
		simOut = new EnumMap<SimOuts, Double>(SimOuts.class);
		
		// Assign EnumMap with data members from integration
		simOut.put(SimOuts.TIME, 		t);
		
		//6DOF States
		simOut.put(SimOuts.U, 		 	linearVelocities[0]);
		simOut.put(SimOuts.V, 		 	linearVelocities[1]);
		simOut.put(SimOuts.W, 		 	linearVelocities[2]);
		simOut.put(SimOuts.NORTH, 	 	NEDPosition[0]);
		simOut.put(SimOuts.EAST, 		NEDPosition[1]);
		simOut.put(SimOuts.ALT, 		NEDPosition[2]);
		simOut.put(SimOuts.PHI, 		eulerAngles[0]);
		simOut.put(SimOuts.THETA, 	 	eulerAngles[1]);
		simOut.put(SimOuts.PSI, 		eulerAngles[2]);
		simOut.put(SimOuts.P, 		 	angularRates[0]);
		simOut.put(SimOuts.Q, 		 	angularRates[1]);
		simOut.put(SimOuts.R, 		 	angularRates[2]);
		
		// Earth Position/Velocity
		simOut.put(SimOuts.LAT, 		y[12]);
		simOut.put(SimOuts.LAT_DOT, 	sixDOFDerivatives[12]);
		simOut.put(SimOuts.LON, 		y[13]);
		simOut.put(SimOuts.LON_DOT,		sixDOFDerivatives[13]);
		
		// Wind Parameters
		simOut.put(SimOuts.TAS, 		windParameters[0]);
		simOut.put(SimOuts.BETA, 		windParameters[1]);
		simOut.put(SimOuts.ALPHA, 	 	windParameters[2]*-1);
		
		simOut.put(SimOuts.ALPHA_DOT,   alphaDot);
		simOut.put(SimOuts.MACH, 		mach);
		
		// Accelerations
		simOut.put(SimOuts.A_X, 		linearAccelerations[0]);
		simOut.put(SimOuts.A_Y, 		linearAccelerations[1]);
		simOut.put(SimOuts.A_Z, 		linearAccelerations[2]);
		
		simOut.put(SimOuts.AN_X, 	   (sixDOFDerivatives[0]/gravity));
		simOut.put(SimOuts.AN_Y, 	   (sixDOFDerivatives[1]/gravity));
		simOut.put(SimOuts.AN_Z, 	  ((sixDOFDerivatives[2]/gravity)+1.0));
		
		// Moments
		simOut.put(SimOuts.L, 		 	totalMoments[0]);
		simOut.put(SimOuts.M, 		 	totalMoments[1]);
		simOut.put(SimOuts.N, 		 	totalMoments[2]);
		
		// 6DOF Derivatives
		simOut.put(SimOuts.NORTH_DOT,   sixDOFDerivatives[3]);
		simOut.put(SimOuts.EAST_DOT, 	sixDOFDerivatives[4]);
		simOut.put(SimOuts.ALT_DOT,    (sixDOFDerivatives[5]*60));
		simOut.put(SimOuts.PHI_DOT, 	sixDOFDerivatives[6]);
		simOut.put(SimOuts.THETA_DOT,   sixDOFDerivatives[7]);
		simOut.put(SimOuts.PSI_DOT, 	sixDOFDerivatives[8]);
		simOut.put(SimOuts.P_DOT, 	 	sixDOFDerivatives[9]);
		simOut.put(SimOuts.Q_DOT, 	 	sixDOFDerivatives[10]);
		simOut.put(SimOuts.R_DOT, 	 	sixDOFDerivatives[11]);
		
		// Engine(s)
		for (Engine engine : engineList) {
			if (engine.getEngineNumber() == 1) {
				simOut.put(SimOuts.THRUST_1, 	engine.getThrust()[0]);
				simOut.put(SimOuts.RPM_1, 	 	engine.getRPM());
				simOut.put(SimOuts.FUEL_FLOW_1, engine.getFuelFlow());
			}
		}
		
		// Controls
		simOut.put(SimOuts.ELEVATOR,    controls.get(FlightControls.ELEVATOR));
		simOut.put(SimOuts.AILERON, 	controls.get(FlightControls.AILERON));
		simOut.put(SimOuts.RUDDER, 	 	controls.get(FlightControls.RUDDER));
		simOut.put(SimOuts.THROTTLE_1, 	controls.get(FlightControls.THROTTLE_1));
		simOut.put(SimOuts.THROTTLE_2, 	controls.get(FlightControls.THROTTLE_2));
		simOut.put(SimOuts.THROTTLE_3, 	controls.get(FlightControls.THROTTLE_3));
		simOut.put(SimOuts.THROTTLE_4, 	controls.get(FlightControls.THROTTLE_4));
		simOut.put(SimOuts.FLAPS, 	 	controls.get(FlightControls.FLAPS));
		
		// Removes the first entry in logsOut to keep a maximum of 100 sec of flight data in UNLIMITED_FLIGHT
		if (options.contains(Options.UNLIMITED_FLIGHT) & t >= 100)
			logsOut.remove(0);
			
		// Add output step to logging arrayList
		logsOut.add(simOut);
		
		// Prints to console (if desired)
		if (options.contains(Options.CONSOLE_DISPLAY)) {
			for (Map.Entry<SimOuts, Double> out : simOut.entrySet())
				System.out.printf("%9.2f ", out.getValue());
			
			System.out.println();
			
			for (Map.Entry<SimOuts, Double> out : simOut.entrySet())
				System.out.printf("%9s ", out.getKey().toString());
				
			System.out.println("");
		}
	}
	
	/**
	 * Returns an ArrayList of {@link Integrate6DOFEquations#getSimOut()} objects; acts as a logging method, which can be used to plot simulation data
	 * or output it to a file
	 * 
	 * @return logsOut
	 * 
	 * @see SimulationPlots
	 */
	public List<EnumMap<SimOuts, Double>> getLogsOut() {return Collections.unmodifiableList(logsOut);}
	
	/**
	 * Returns an EnumMap of data for a single step of integration accomplished in {@link Integrate6DOFEquations#accelAndMoments#logData(double)}	
	 * 
	 * @return simOut
	 */
	public Map<SimOuts, Double> getSimOut() {return Collections.unmodifiableMap(simOut);}
	
	/**
	 * Lets other objects know if {@link Integrate6DOFEquations#run()} is currently running
	 * 
	 * @return Running status of integration
	 */
	public boolean isRunning() {return running;}
	
	/**
	 * Runs {@link Integrate6DOFEquations} integration loop by calling the {@link ClassicalRungeKuttaIntegrator#singleStep(FirstOrderDifferentialEquations, double, double[], double)}
	 * method on each iteration of the loop as long as {@link Options#PAUSED} isn't enabled 
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// Integration loop
		try {
			running = true;
			
			for (double t = integratorConfig[0]; t < integratorConfig[2]; t += integratorConfig[1]) {
				// Set pause/reset from within keyboard's updateOptions method if not in analysis mode
				if (!options.contains(Options.ANALYSIS_MODE))
					this.options  = hidKeyboard.updateOptions(options);
				
				// If paused and resed selected, reset initialConditions using IntegrationSetup's method 
				if (options.contains(Options.PAUSED) & options.contains(Options.RESET))				
 					initialConditions = Utilities.unboxDoubleArray(IntegrationSetup.gatherInitialConditions("InitialConditions"));
				
				// If paused, skip the integration and update process
				if (!options.contains(Options.PAUSED)) {
					// Run a single step of integration each step of the loop
					y = integrator.singleStep(new SixDOFEquations(), 	  // derivatives
											  t, 		  				  // start time
											  initialConditions, 		  // initial conditions
											  t+integratorConfig[1]);     // end time (t+dt)
					
					// Update data members' values
					updateDataMembers(y, t);
					
					// Update initial conditions for next step of integration
					initialConditions = y;
					
					// Update output log
					logData(t);
				}
				
				// Pause the integration for dt*1000 milliseconds to emulate real time operation
				// if ANALYSIS_MODE is false
				if (!options.contains(Options.ANALYSIS_MODE))
					Thread.sleep((long)(integratorConfig[1]*1000));
			}
			
			running = false;
			
			// If in analysis mode and not in unlimited flight, generate simulation plots
			if (options.contains(Options.ANALYSIS_MODE) & !options.contains(Options.UNLIMITED_FLIGHT) & !options.contains(Options.TRIM_MODE)) {
				new Thread(new MakePlots(this, 
						 				 new String[] {"Controls", "Instruments", "Position", "Rates", "Miscellaneous"},
						 				 options,
						 				 aircraft)).start();
			}
		} catch (InterruptedException e) {System.err.println("Warning! Simulation interrupted!");}
	}
}