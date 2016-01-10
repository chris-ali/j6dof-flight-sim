package com.chrisali.javaflightsim.utilities.integration;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;

import com.chrisali.javaflightsim.aero.AccelAndMoments;
import com.chrisali.javaflightsim.aircraft.Aircraft;
import com.chrisali.javaflightsim.controls.FlightControls;
import com.chrisali.javaflightsim.controls.FlightControlsUtilities;
import com.chrisali.javaflightsim.controls.Joystick;
import com.chrisali.javaflightsim.controls.SimulationController;
import com.chrisali.javaflightsim.enviroment.Environment;
import com.chrisali.javaflightsim.propulsion.FixedPitchPropEngine;
import com.chrisali.javaflightsim.setup.IntegrationSetup;
import com.chrisali.javaflightsim.setup.Options;

/*
 * This class integrates all 12 6DOF equations numerically to obtain the aircraft states.
 * The Apache Commons' Classical Runge-Kutta is used to integrate over a period of time defined in the integratorConfig double array. 
 *  
 * In order to formulate the first order ODE, the following (double arrays) must be passed in:
 * 		integratorConfig[]{0,dt,dt}    (sec)
 * 		initialConditions[]{initU,initV,initW,initN,initE,initD,initPhi,initTheta,initPsi,initP,initQ,initR}
 *		Aircraft aircraftIn
 *      FixedPitchPropEngine engineIn
 * 
 * The class outputs at each step using logData to generate a logsOut ArrayList of simOut EnumMaps containing simulation outputs.
 */
public class Integrate6DOFEquations implements Runnable {
	
	// Data Fields
	private double[] linearVelocities 		= new double[3];
	private double[] NEDPosition      		= new double[3];
	private double[] eulerAngles      		= new double[3];
	private double[] angularRates     		= new double[3];
	
	private double[] windParameters   		= new double[3];
	private double[] environmentParameters  = new double[4];
	private double[] gravity				= new double[3];
	
	private double[] linearAccelerations    = new double[3];
	private double[] totalMoments     		= new double[3];
	
	private double alphaDot 				= 0.0f;
	private double mach     				= 0.0f;
	
	// Simulation controls (Joystick, Keyboard, etc.)
	private EnumMap<FlightControls, Double> controls;
	private SimulationController joystick;
	
	// Integrator Fields
	private double[] sixDOFDerivatives		= new double[12];
	private double[] y					    = new double[12];
	private double[] initialConditions		= new double[12];
	private double[] integratorConfig		= new double[3];
	private ClassicalRungeKuttaIntegrator integrator;
	
	// Aircraft Properties
	private Aircraft aircraft  				= new Aircraft();
	private FixedPitchPropEngine engine 	= new FixedPitchPropEngine();
	private AccelAndMoments accelAndMoments = new AccelAndMoments();
	
	// Output Logging
	private ArrayList<EnumMap<SimOuts, Double>> logsOut = new ArrayList<>();
	private EnumMap<SimOuts, Double> simOut;
	
	// Threading and Options
	private CountDownLatch latch;
	private EnumMap<Options, Boolean> options;
	
	public Integrate6DOFEquations(Aircraft aircraft, 
								  FixedPitchPropEngine engine,
								  CountDownLatch latch,
								  EnumMap<Options, Boolean> options) {
		this.aircraft 		   = aircraft;
		this.engine   		   = engine;
		
		this.controls 		   = IntegrationSetup.gatherInitialControls("InitialControls");
		this.initialConditions = IntegrationSetup.gatherInitialConditions("InitialConditions"); //{initU,initV,initW,initN,initE,initD,initPhi,initTheta,initPsi,initP,initQ,initR}
		this.integratorConfig  = IntegrationSetup.gatherIntegratorConfig("IntegratorConfig");  // {startTime, dt, endTime}
		this.gravity  		   = Environment.getGravity();
		
		this.latch			   = latch;
		this.options		   = options;
		
		// If USE_JOYSTICK enabled, use joystick
		if (options.get(Options.USE_JOYSTICK))
			this.joystick = new Joystick(controls);
		else
			this.joystick = null;
		
		// Lets simulation run forever when UNLIMITED_FLIGHT and ANALYSIS_MODE are true/false, respectively
		if(options.get(Options.UNLIMITED_FLIGHT) & !options.get(Options.ANALYSIS_MODE))
			integratorConfig[2] = Double.POSITIVE_INFINITY;
		
		// Use fourth-order Runge-Kutta numerical integration with time step of dt
		this.integrator = new ClassicalRungeKuttaIntegrator(integratorConfig[2]);
		
		// Calculate initial data members' values
		updateDataMembers(initialConditions, integratorConfig[0]);
	}
	
	// Creates the 12 state derivatives integrator uses to numerically integrate
	private class SixDOFEquations implements FirstOrderDifferentialEquations {		
		private SixDOFEquations() {}

		public void computeDerivatives(double t, double[] y, double[] yDot) {
			for (int i = 0; i < yDot.length; i++) 
				yDot[i] = sixDOFDerivatives[i];
		}

		public int getDimension() {return 12;}
	}
	
	// Recalculates derivatives based on newly calculated accelerations and moments
	private double[] updateDerivatives(double[] y) {
		double[] yDot = new double[12];
		
		double[][] dirCosMat = SixDOFUtilities.body2Ned(new double[]{y[6], y[7], y[8]});      // create DCM for NED equations ([row][column])
		double[] inertiaCoeffs = SixDOFUtilities.getInertiaCoeffs(aircraft.getMassProperties());
		
		yDot[0]  = (y[11]*y[1])-(y[10]*y[2])-(gravity[2]*Math.sin(y[7]))               +linearAccelerations[0];    // u (ft/sec)
		yDot[1]  = (y[9]* y[2])-(y[11]*y[0])+(gravity[2]*Math.sin(y[6])*Math.cos(y[7]))+linearAccelerations[1];    // v (ft/sec)
		yDot[2]  = (y[10]*y[0])-(y[9]* y[1])+(gravity[2]*Math.cos(y[6])*Math.cos(y[7]))+linearAccelerations[2];    // w (ft/sec)
		
		yDot[3]  =  y[0]*dirCosMat[0][0]+y[1]*dirCosMat[0][1]+y[2]*dirCosMat[0][2];         // N (ft)
		yDot[4]  =  y[0]*dirCosMat[1][0]+y[1]*dirCosMat[1][1]+y[2]*dirCosMat[1][2];         // E (ft)
		yDot[5]  = (y[0]*dirCosMat[2][0]+y[1]*dirCosMat[2][1]+y[2]*dirCosMat[2][2])*-1;     // D (ft) (Negative value to reverse altitude sensing)
		
		yDot[6]  =   y[9]+(Math.tan(y[7])*((y[10]*Math.sin(y[6]))+(y[11]*Math.cos(y[6])))); // phi (rad)
		yDot[7]  =  (y[10]*Math.cos(y[6]))-(y[11]*Math.sin(y[6]));     			            // theta (rad)
		yDot[8]  = ((y[10]*Math.sin(y[6]))+(y[11]*Math.cos(y[6])))/Math.cos(y[7]);          // psi (rad)
		
		yDot[9]  = ((inertiaCoeffs[1]*y[9]*y[10]) - (inertiaCoeffs[0]*y[10])*y[11]) + (inertiaCoeffs[2]*totalMoments[0])+(inertiaCoeffs[3]*totalMoments[2]);     // p (rad/sec)
		yDot[10] =  (inertiaCoeffs[4]*y[9]*y[11]) - (inertiaCoeffs[5]*((y[9]*y[9])-(y[11]*y[11])))                      +(inertiaCoeffs[6]*totalMoments[1]);     // q (rad/sec)
		yDot[11] = ((inertiaCoeffs[7]*y[9]*y[10]) - (inertiaCoeffs[1]*y[10]*y[11])) + (inertiaCoeffs[3]*totalMoments[0])+(inertiaCoeffs[8]*totalMoments[2]);     // r (rad/sec)
		
		return yDot;
	}
	
	// Runs helper methods to update data members in functions
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
		this.windParameters = SixDOFUtilities.getWindParameters(linearVelocities);
		
		// Update environment		
		this.environmentParameters = Environment.getEnvironmentParams(NEDPosition);
		
		// Update controls with joystick, doublets, or mouse (later)
		if (options.get(Options.USE_JOYSTICK) & ! options.get(Options.ANALYSIS_MODE))
			this.controls = ((Joystick) joystick).updateFlightControls(controls);
		else if (!options.get(Options.USE_JOYSTICK) & options.get(Options.ANALYSIS_MODE)) {	
			// Update controls with an aileron doublet
			this.controls = FlightControlsUtilities.makeDoublet(controls, 
																t, 
																10, 
																0.5, 
																0.035, 
																FlightControls.AILERON);
			// Update controls with a rudder doublet
			this.controls = FlightControlsUtilities.makeDoublet(controls, 
																t, 
																13, 
																0.5, 
																0.035, 
																FlightControls.RUDDER);
			
			// Update controls with an elevator doublet
			this.controls = FlightControlsUtilities.makeDoublet(controls, 
																t, 
																50, 
																0.5, 
																0.035, 
																FlightControls.ELEVATOR);
		}
		
		// Update engine
		this.engine.updateEngineState(controls, 
									  NEDPosition, 
									  environmentParameters, 
									  windParameters);
		
		// Update alphaDot
		this.alphaDot = SixDOFUtilities.getAlphaDot(linearVelocities, sixDOFDerivatives);
		
		// Update mach
		this.mach = SixDOFUtilities.getMach(windParameters, environmentParameters);
		
		// Update accelerations
		this.linearAccelerations = accelAndMoments.getBodyAccelerations(windParameters,
																	    angularRates,
																	    aircraft.getWingDimensions(),
																	    environmentParameters,
																	    controls,
																	    alphaDot,
																	    engine);
		// Update moments
		this.totalMoments = accelAndMoments.getTotalMoments(windParameters,
													 		angularRates,
															aircraft.getWingDimensions(),
															environmentParameters,
															controls,
															alphaDot,
															engine);
				
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
	
	// After each step adds data to a logging arrayList for plotting and outputs to the console (if desired)
	private void logData(double t) {
		// Make a new EnumMap
		simOut = new EnumMap<SimOuts, Double>(SimOuts.class);
		
		// Assign EnumMap with data members from integration
		simOut.put(SimOuts.TIME, 		t);
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
		simOut.put(SimOuts.TAS, 		windParameters[0]);
		simOut.put(SimOuts.BETA, 		windParameters[1]);
		simOut.put(SimOuts.ALPHA, 	 	windParameters[2]);
		simOut.put(SimOuts.A_X, 		linearAccelerations[0]);
		simOut.put(SimOuts.A_Y, 		linearAccelerations[1]);
		simOut.put(SimOuts.A_Z, 		linearAccelerations[2]);
		simOut.put(SimOuts.L, 		 	totalMoments[0]);
		simOut.put(SimOuts.M, 		 	totalMoments[1]);
		simOut.put(SimOuts.N, 		 	totalMoments[2]);
		simOut.put(SimOuts.AN_X, 	   (sixDOFDerivatives[0]/gravity[2]));
		simOut.put(SimOuts.AN_Y, 	   (sixDOFDerivatives[1]/gravity[2]));
		simOut.put(SimOuts.AN_Z, 	  ((sixDOFDerivatives[2]/gravity[2])+1.0));
		simOut.put(SimOuts.NORTH_DOT,   sixDOFDerivatives[3]);
		simOut.put(SimOuts.EAST_DOT, 	sixDOFDerivatives[4]);
		simOut.put(SimOuts.ALT_DOT,    (sixDOFDerivatives[5]*60));
		simOut.put(SimOuts.PHI_DOT, 	sixDOFDerivatives[6]);
		simOut.put(SimOuts.THETA_DOT,   sixDOFDerivatives[7]);
		simOut.put(SimOuts.PSI_DOT, 	sixDOFDerivatives[8]);
		simOut.put(SimOuts.P_DOT, 	 	sixDOFDerivatives[9]);
		simOut.put(SimOuts.Q_DOT, 	 	sixDOFDerivatives[10]);
		simOut.put(SimOuts.R_DOT, 	 	sixDOFDerivatives[11]);
		simOut.put(SimOuts.THRUST_L, 	engine.getThrust()[0]);
		simOut.put(SimOuts.RPM_L, 	 	engine.getRPM());
		simOut.put(SimOuts.FUEL_FLOW_L, engine.getFuelFlow());
		simOut.put(SimOuts.ELEVATOR,    controls.get(FlightControls.ELEVATOR));
		simOut.put(SimOuts.AILERON, 	controls.get(FlightControls.AILERON));
		simOut.put(SimOuts.RUDDER, 	 	controls.get(FlightControls.RUDDER));
		simOut.put(SimOuts.THROTTLE_L, 	controls.get(FlightControls.THROTTLE_L));
		simOut.put(SimOuts.FLAPS, 	 	controls.get(FlightControls.FLAPS));
		simOut.put(SimOuts.ALPHA_DOT,   alphaDot);
		simOut.put(SimOuts.MACH, 		mach);
		
		// Removes the first entry in logsOut to keep a maximum of 100 sec of flight data in UNLIMITED_FLIGHT
		if (options.get(Options.UNLIMITED_FLIGHT) & t >= 100)
			logsOut.remove(0);
			
		// Add output step to logging arrayList
		logsOut.add(simOut);
		
		// Prints to console (if desired)
		if (options.get(Options.CONSOLE_DISPLAY)) {
			for (Map.Entry<SimOuts, Double> out : simOut.entrySet())
				System.out.printf("%9.2f ", out.getValue());
			System.out.println("\n");
		}
	}
	
	public ArrayList<EnumMap<SimOuts, Double>> getLogsOut() {return logsOut;}
	
	public EnumMap<SimOuts, Double> getSimOut() {return simOut;}
	
	// Runs integration loop
	@Override
	public void run() {
		// Integration loop
		try {
			for (double t = integratorConfig[0]; t < integratorConfig[2]; t += integratorConfig[1]) {
				// If paused, skip the integration and update process
				if (!options.get(Options.PAUSED)) {
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
				
				// If paused and resed selected, reset initialConditions using IntegrationSetup's method 
				if (options.get(Options.PAUSED) & options.get(Options.RESET))
					initialConditions = IntegrationSetup.gatherInitialConditions("InitialConditions");
				
				// Pause the integration for dt*1000 milliseconds to emulate real time operation
				// if ANALYSIS_MODE is false
				if (!options.get(Options.ANALYSIS_MODE))
					Thread.sleep((long)(integratorConfig[1]*1000));
				
			}
			latch.countDown();
		} catch (InterruptedException e) {System.err.println("Warning! Simulation interrupted!");
		}
	}
}