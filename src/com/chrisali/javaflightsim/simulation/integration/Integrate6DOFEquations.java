package com.chrisali.javaflightsim.simulation.integration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;

import com.chrisali.javaflightsim.controllers.SimulationController;
import com.chrisali.javaflightsim.datatransfer.EnvironmentData;
import com.chrisali.javaflightsim.datatransfer.EnvironmentDataListener;
import com.chrisali.javaflightsim.datatransfer.EnvironmentDataType;
import com.chrisali.javaflightsim.simulation.aero.AccelAndMoments;
import com.chrisali.javaflightsim.simulation.aircraft.Aircraft;
import com.chrisali.javaflightsim.simulation.aircraft.AircraftBuilder;
import com.chrisali.javaflightsim.simulation.controls.FlightControlType;
import com.chrisali.javaflightsim.simulation.controls.FlightControls;
import com.chrisali.javaflightsim.simulation.enviroment.Environment;
import com.chrisali.javaflightsim.simulation.enviroment.EnvironmentParameters;
import com.chrisali.javaflightsim.simulation.propulsion.Engine;
import com.chrisali.javaflightsim.simulation.setup.IntegrationSetup;
import com.chrisali.javaflightsim.simulation.setup.IntegratorConfig;
import com.chrisali.javaflightsim.simulation.setup.Options;
import com.chrisali.javaflightsim.utilities.SixDOFUtilities;

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
public class Integrate6DOFEquations implements Runnable, EnvironmentDataListener {
	// 6DOF Integration Results
	private double[] linearVelocities 		= new double[3];
	private double[] NEDPosition      		= new double[3];
	private double[] eulerAngles      		= new double[3];
	private double[] angularRates     		= new double[3];
	
	// Environment and Wind Parameters
	private Map<EnvironmentParameters, Double> environmentParameters;
	private double   gravity			    = Environment.getGravity();
	private double[] windParameters   		= new double[3];	
	private double   alphaDot 				= 0.0f;
	private double   mach     				= 0.0f;
	
	// Ground Reaction
	private IntegrateGroundReaction groundReaction;
	private double   terrainHeight			= 0.0f;
	
	// Forces and Moments
	private double[] linearAccelerations    = new double[3];
	private double[] totalMoments     		= new double[3];
	
	// Simulation Controls (Joystick, Keyboard, etc.)
	private Map<FlightControlType, Double> controls;
	
	// Integrator Fields
	private ClassicalRungeKuttaIntegrator integrator;
	private double[] sixDOFDerivatives		= new double[14];
	private double[] y					    = new double[14];
	private double[] initialConditions      = new double[14]; 
	
	// Static fields for concurrency
	private static double[] integratorConfig = new double[3];
	private static double   t;
	
	// Aircraft Properties
	private Aircraft aircraft;
	private Set<Engine> engineList;
	
	// Output Logging
	private List<Map<SimOuts, Double>> logsOut = Collections.synchronizedList(new ArrayList<Map<SimOuts, Double>>());
	private Map<SimOuts, Double> simOut;
	
	// Options
	private EnumSet<Options> options;
	private static boolean running;
	
	/**
	 * Creates the {@link Integrate6DOFEquations} object with references to {@link FlightControls} and {@link SimulationController}
	 * objects
	 * 
	 * @param flightControls
	 * @param simController
	 */
	public Integrate6DOFEquations(FlightControls flightControls, SimulationController simController) {
		
		controls 		   = flightControls.getFlightControls();
		aircraft 		   = simController.getAircraftBuilder().getAircraft();
		engineList   	   = simController.getAircraftBuilder().getEngineList();
		options		       = simController.getSimulationOptions();
		
		// Use Apache Commons Lang to convert EnumMap values into primitive double[] 
		initialConditions = ArrayUtils.toPrimitive(simController.getInitialConditions().values()
				   												.toArray(new Double[initialConditions.length]));
		integratorConfig  = ArrayUtils.toPrimitive(simController.getIntegratorConfig().values()
				   												.toArray(new Double[integratorConfig.length]));

		// Allows simulation to run forever in pilot in the loop simulation if ANALYSIS_MODE not enabled 
		if (!options.contains(Options.ANALYSIS_MODE) && options.contains(Options.UNLIMITED_FLIGHT))
			integratorConfig[2] = Double.POSITIVE_INFINITY;
		
		// Set up running parameters for integration
		t = integratorConfig[0];
		
		// Use fourth-order Runge-Kutta numerical integration with time step of dt
		integrator = new ClassicalRungeKuttaIntegrator(integratorConfig[1]);
		
		// Set up ground reaction integration
		groundReaction = new IntegrateGroundReaction(linearVelocities, 
													 NEDPosition, 
													 eulerAngles, 
													 angularRates,
													 windParameters,
													 sixDOFDerivatives,
													 integratorConfig, 
													 aircraft, 
													 controls);
		
		// Initialize accelerations and moments, and calculate initial data members' values
		AccelAndMoments.init(aircraft);
		updateDataMembers();
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
	 * @see Source: <i>Small Unmanned Aircraft: Theory and Practice by Beard, R.W. and McLain, T.W.</i>
	 */
	private void updateDerivatives(double[] y) {
		double[][] dirCosMat     = SixDOFUtilities.body2Ned(new double[]{y[6], y[7], y[8]});      // create DCM for NED equations ([column][row])
		double[]   inertiaCoeffs = SixDOFUtilities.calculateInertiaCoeffs(aircraft.getInertiaValues());
		double[]   ned2LLA       = SixDOFUtilities.ned2LLA(y);
		double[]   windSpdNED    = new double[]{environmentParameters.get(EnvironmentParameters.WIND_SPEED_N),
												environmentParameters.get(EnvironmentParameters.WIND_SPEED_E),
												environmentParameters.get(EnvironmentParameters.WIND_SPEED_D)};
		
		sixDOFDerivatives[0]  = (y[11]*y[1])-(y[10]*y[2])-(gravity*Math.sin(y[7]))               +linearAccelerations[0];    // u (ft/sec)
		sixDOFDerivatives[1]  = (y[9]* y[2])-(y[11]*y[0])+(gravity*Math.sin(y[6])*Math.cos(y[7]))+linearAccelerations[1];    // v (ft/sec)
		sixDOFDerivatives[2]  = (y[10]*y[0])-(y[9]* y[1])+(gravity*Math.cos(y[6])*Math.cos(y[7]))+linearAccelerations[2];    // w (ft/sec)
		
		sixDOFDerivatives[3]  =    (y[0]*dirCosMat[0][0]+y[1]*dirCosMat[0][1]+y[2]*dirCosMat[0][2])+windSpdNED[0];    // N (ft)
		sixDOFDerivatives[4]  =    (y[0]*dirCosMat[1][0]+y[1]*dirCosMat[1][1]+y[2]*dirCosMat[1][2])+windSpdNED[1];    // E (ft)
		sixDOFDerivatives[5]  = -1*(y[0]*dirCosMat[2][0]+y[1]*dirCosMat[2][1]+y[2]*dirCosMat[2][2])+windSpdNED[2];    // D (ft)
		
		sixDOFDerivatives[6]  =   y[9]+(Math.tan(y[7])*((y[10]*Math.sin(y[6]))+(y[11]*Math.cos(y[6])))); // phi (rad)
		sixDOFDerivatives[7]  =  (y[10]*Math.cos(y[6]))-(y[11]*Math.sin(y[6]));     			            // theta (rad)
		sixDOFDerivatives[8]  = ((y[10]*Math.sin(y[6]))+(y[11]*Math.cos(y[6])))/Math.cos(y[7]);          // psi (rad)
		
		sixDOFDerivatives[9]  = ((inertiaCoeffs[1]*y[9]*y[10]) - (inertiaCoeffs[0]*y[10])*y[11]) + (inertiaCoeffs[2]*totalMoments[0])+(inertiaCoeffs[3]*totalMoments[2]);     // p (rad/sec)
		sixDOFDerivatives[10] =  (inertiaCoeffs[4]*y[9]*y[11]) - (inertiaCoeffs[5]*((y[9]*y[9])-(y[11]*y[11])))                      +(inertiaCoeffs[6]*totalMoments[1]);     // q (rad/sec)
		sixDOFDerivatives[11] = ((inertiaCoeffs[7]*y[9]*y[10]) - (inertiaCoeffs[1]*y[10]*y[11])) + (inertiaCoeffs[3]*totalMoments[0])+(inertiaCoeffs[8]*totalMoments[2]);     // r (rad/sec)
		
		sixDOFDerivatives[12] = sixDOFDerivatives[3]*ned2LLA[0]; // Latitude  (rad)
		sixDOFDerivatives[13] = sixDOFDerivatives[4]*ned2LLA[1]; // Longitude (rad)
	}
	
	/**
	 *  Runs various helper methods to update data members in {@link Integrate6DOFEquations}. It updates the 6DOF states, environment parameters, controls, engine state, and finally 
	 *  calculates accelerations and moments to be used in {@link Integrate6DOFEquations#updateDerivatives(double[])} 
	 */
	private void updateDataMembers() {
		// Assign indices in yTemp array to 6DOF state arrays
		for (int i=0; i<linearVelocities.length; i++) {
			linearVelocities[i] = y[i];
			NEDPosition[i]      = y[i+3];
			eulerAngles[i]      = y[i+6];
			angularRates[i]     = y[i+9];
		}

		// Implement saturation and (2)pi bounding to keep states within realistic limits
		linearVelocities = SaturationLimits.limitLinearVelocities(linearVelocities);
		NEDPosition      = SaturationLimits.limitNEDPosition(NEDPosition, terrainHeight);
		eulerAngles      = SaturationLimits.piBounding(eulerAngles);
		angularRates     = SaturationLimits.limitAngularRates(angularRates);
		
		// Update wind parameters
		windParameters = SixDOFUtilities.calculateWindParameters(linearVelocities);
		
		// Update environment		
		environmentParameters = Environment.updateEnvironmentParams(NEDPosition);
		
		// Update all engines in engine list
		for(Engine engine : engineList)
			 engine.updateEngineState(controls, environmentParameters, windParameters);
		
		// Update alphaDot
		alphaDot = SixDOFUtilities.calculateAlphaDot(linearVelocities, sixDOFDerivatives);
		
		// Update mach
		mach = SixDOFUtilities.calculateMach(windParameters, environmentParameters);
		
		// Integrate another step of ground reaction only if within 100 ft of ground
		if ((NEDPosition[2] - terrainHeight) < 100)
			groundReaction.integrateStep(terrainHeight);
		
		//System.out.println(groundReaction);
		
		// Update accelerations
		linearAccelerations = AccelAndMoments.calculateLinearAccelerations(windParameters,
																		   angularRates,
																		   environmentParameters,
																		   controls,
																		   alphaDot,
																		   engineList,
																		   aircraft,
																		   groundReaction);
		// Update moments
		totalMoments = AccelAndMoments.calculateTotalMoments(windParameters,
														 	 angularRates,
															 environmentParameters,
															 controls,
															 alphaDot,
															 engineList,
															 aircraft,
															 groundReaction);
				
		// Recalculates derivatives for next step
		updateDerivatives(y);
	}
	
	/**
	 *  Adds simulation data to the ArrayList {@link Integrate6DOFEquations#getLogsOut()} after each successful step of integration 
	 *  for plotting and outputs to the console, if set in {@link Integrate6DOFEquations#options}. 
	 *  The data calculated in each step of integration is available in the EnumMap {@link Integrate6DOFEquations#getSimOut()}. All
	 *  collections are synchronized to mitigate data access problems from threading
	 */
	private void logData() {
		// Need to initialize within logData(), else plots won't display correctly
		simOut = Collections.synchronizedMap(new EnumMap<SimOuts, Double>(SimOuts.class));
		
		synchronized (simOut) {
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
			simOut.put(SimOuts.U_DOT, 	    sixDOFDerivatives[0]);
			simOut.put(SimOuts.V_DOT, 	    sixDOFDerivatives[1]);
			simOut.put(SimOuts.W_DOT, 	    sixDOFDerivatives[2]);
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
			simOut.put(SimOuts.THRUST_1, 	0.0);
			simOut.put(SimOuts.RPM_1, 	 	0.0);
			simOut.put(SimOuts.FUEL_FLOW_1, 0.0);
			simOut.put(SimOuts.THRUST_2, 	0.0);
			simOut.put(SimOuts.RPM_2, 	 	0.0);
			simOut.put(SimOuts.FUEL_FLOW_2, 0.0);
			simOut.put(SimOuts.THRUST_3, 	0.0);
			simOut.put(SimOuts.RPM_3, 	 	0.0);
			simOut.put(SimOuts.FUEL_FLOW_3, 0.0);
			simOut.put(SimOuts.THRUST_4, 	0.0);
			simOut.put(SimOuts.RPM_4, 	 	0.0);
			simOut.put(SimOuts.FUEL_FLOW_4, 0.0);
	
			for (Engine engine : engineList) {
				int engineNumber = engine.getEngineNumber();
				
				simOut.put(Enum.valueOf(SimOuts.class, "THRUST_" + engineNumber), 	 engine.getThrust()[0]);
				simOut.put(Enum.valueOf(SimOuts.class, "RPM_" + engineNumber), 	 	 engine.getRPM());
				simOut.put(Enum.valueOf(SimOuts.class, "FUEL_FLOW_" + engineNumber), engine.getFuelFlow());
			}
			
			// Controls
			simOut.put(SimOuts.ELEVATOR,    controls.get(FlightControlType.ELEVATOR));
			simOut.put(SimOuts.AILERON, 	controls.get(FlightControlType.AILERON));
			simOut.put(SimOuts.RUDDER, 	 	controls.get(FlightControlType.RUDDER));
			simOut.put(SimOuts.THROTTLE_1, 	controls.get(FlightControlType.THROTTLE_1));
			simOut.put(SimOuts.THROTTLE_2, 	controls.get(FlightControlType.THROTTLE_2));
			simOut.put(SimOuts.THROTTLE_3, 	controls.get(FlightControlType.THROTTLE_3));
			simOut.put(SimOuts.THROTTLE_4, 	controls.get(FlightControlType.THROTTLE_4));
			simOut.put(SimOuts.PROPELLER_1, controls.get(FlightControlType.PROPELLER_1));
			simOut.put(SimOuts.PROPELLER_2, controls.get(FlightControlType.PROPELLER_2));
			simOut.put(SimOuts.PROPELLER_3, controls.get(FlightControlType.PROPELLER_3));
			simOut.put(SimOuts.PROPELLER_4, controls.get(FlightControlType.PROPELLER_4));
			simOut.put(SimOuts.MIXTURE_1, 	controls.get(FlightControlType.MIXTURE_1));
			simOut.put(SimOuts.MIXTURE_2, 	controls.get(FlightControlType.MIXTURE_2));
			simOut.put(SimOuts.MIXTURE_3, 	controls.get(FlightControlType.MIXTURE_3));
			simOut.put(SimOuts.MIXTURE_4, 	controls.get(FlightControlType.MIXTURE_4));
			simOut.put(SimOuts.FLAPS, 	 	controls.get(FlightControlType.FLAPS));
			simOut.put(SimOuts.GEAR, 	 	controls.get(FlightControlType.GEAR));
		}
		
		synchronized (logsOut) {
			// Removes the first entry in logsOut to keep a maximum of 100 sec of flight data in UNLIMITED_FLIGHT
			if (options.contains(Options.UNLIMITED_FLIGHT) & t >= 100 & logsOut.size() > 0)
				logsOut.remove(0);
				
			// Add output step to logging arrayList
			logsOut.add(simOut);	
		}
	}
	
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
			
			while (t < integratorConfig[2] && running) {
				// If paused and reset selected, reset initialConditions using IntegrationSetup's method 
				if (options.contains(Options.PAUSED) & options.contains(Options.RESET))				
 					initialConditions = ArrayUtils.toPrimitive(IntegrationSetup.gatherInitialConditions("InitialConditions").values()
 																			   .toArray(new Double[initialConditions.length]));
				
				// If paused, skip the integration and update process
				if (!options.contains(Options.PAUSED)) {
					// Run a single step of integration each step of the loop
					y = integrator.singleStep(new SixDOFEquations(), 	  // derivatives
											  t, 		  				  // start time
											  initialConditions, 		  // initial conditions
											  t+integratorConfig[1]);     // end time (t+dt)
					
					// Update data members' values
					updateDataMembers();
					
					// Update initial conditions for next step of integration
					initialConditions = y;
					
					// Update output log
					logData();
				}
				
				// Pause the integration for dt*1000 milliseconds to emulate real time operation
				// if ANALYSIS_MODE is false
				if (!options.contains(Options.ANALYSIS_MODE))
					Thread.sleep((long)(integratorConfig[1]*1000));
				
				// Increments time using an intrinsic lock
				incrementTime();
			}
			
		} catch (InterruptedException e) {
		} finally {running = false;} 
		
	}
	
	//================================= Simulation Logging =====================================================
	
	/**
	 * Returns an ArrayList of {@link Integrate6DOFEquations#getSimOut()} objects; acts as a logging method, which can be used to plot simulation data
	 * or output it to a file
	 * 
	 * @return logsOut
	 */
	public synchronized List<Map<SimOuts, Double>> getLogsOut() {return Collections.unmodifiableList(logsOut);}
	
	/**
	 * Clears logsOut list of past data in preparation for recording a new maneuver 
	 * 
	 * @return If logsOut list was successfully deleted
	 */
	public synchronized boolean clearLogsOut() {return logsOut.removeAll(logsOut);}
	
	/**
	 * Returns an EnumMap of data for a single step of integration accomplished in {@link Integrate6DOFEquations#accelAndMoments#logData(double)}	
	 * 
	 * @return simOut
	 */
	public synchronized Map<SimOuts, Double> getSimOut() {return Collections.unmodifiableMap(simOut);}
	
	//========================================= Time ============================================================
	
	/**
	 * @return current time of simulation (sec)
	 */
	public static synchronized double getTime() {return Integrate6DOFEquations.t;}
	
	/**
	 * Gets the intrinsic lock on {@link Integrate6DOFEquations#t} and increments it by {@link IntegratorConfig#DT} seconds 
	 */
	private static synchronized void incrementTime() {Integrate6DOFEquations.t += integratorConfig[1];}
	
	//==================================== Running Status =======================================================
	
	/**
	 * Lets other objects know if {@link Integrate6DOFEquations#run()} is currently running
	 * 
	 * @return Running status of integration
	 */
	public static synchronized boolean isRunning() {return Integrate6DOFEquations.running;}
	
	/**
	 * Lets other objects request to stop the simulation by setting running to false
	 * 
	 * @param running
	 */
	public static synchronized void setRunning(boolean running) {Integrate6DOFEquations.running = running;}
	
	//==================================== Environment ==========================================================
	
	/**
	 * Sets the wind speed (kts), wind direction (deg) and temperature (deg C)  
	 * 
	 * @param windSpeed
	 * @param windDir
	 * @param temperature
	 */
	public void setEnvironment(double windSpeed, double windDir, double temperature) {
		Environment.setWindDir(windDir);
		Environment.setWindSpeed(windSpeed);
		// Subtract standard temperature from argument to get deviation from standard, then convert C deg to F deg 
		Environment.setDeltaIsa((temperature-15)*9/5);
	}
	
	@Override
	public void onEnvironmentDataReceived(EnvironmentData environmentData) {
		Map<EnvironmentDataType, Double> receivedEnvironmentData = environmentData.getEnvironmentData();
		
		if (environmentData != null)
			terrainHeight = (receivedEnvironmentData.get(EnvironmentDataType.TERRAIN_HEIGHT)*15)+5;
	}
}