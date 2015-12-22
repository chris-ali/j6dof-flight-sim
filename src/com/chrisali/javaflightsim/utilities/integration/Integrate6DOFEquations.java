package com.chrisali.javaflightsim.utilities.integration;

import java.util.ArrayList;

import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;

import com.chrisali.javaflightsim.aero.AccelAndMoments;
import com.chrisali.javaflightsim.aircraft.Aircraft;
import com.chrisali.javaflightsim.enviroment.Environment;
import com.chrisali.javaflightsim.propulsion.FixedPitchPropEngine;

/*
 * This class integrates all 12 6DOF equations numerically to obtain the aircraft states.
 * The Apache Commons' Classical Runge-Kutta is used to integrate over a period of time defined in the integratorConfig double array. 
 *  
 * In order to formulate the first order ODE, the following (double arrays) must be passed in:
 * 		integratorConfig[]{0,dt,dt}    (sec)
 * 		initialConditions[]{initU,initV,initW,initN,initE,initD,initPhi,initTheta,initPsi,initP,initQ,initR}
 * 		controls[]{elevator,aileron,rudder,leftThrottle,rightThrottle,leftPropeller,rightPropeller,leftMixture,rightMixture,flaps,gear,leftBrake,rightBrake}   (rad,rad,rad,norm,norm,norm,norm,norm,norm,rad,norm,norm,norm)
 *		Aircraft aircraftIn
 *      FixedPitchPropEngine engineIn
 * 
 * The class outputs at each step using logData to generate the following double array:
 * 	    t,							(time [sec])
 *	    linearVelocities[0],        (u [ft/sec])
 *	    linearVelocities[1],		(v [ft/sec])
 *	    linearVelocities[2],		(w [ft/sec])
 *	    angularRates[0],			(p [rad/sec])
 *	    angularRates[1],			(q [rad/sec])
 *	    angularRates[2],			(r [rad/sec])
 *	    eulerAngles[0],				(phi [rad])
 *	    eulerAngles[1],				(theta [rad])
 *	    eulerAngles[2],				(psi [rad])
 *	    windParameters[0],			(vTrue [ft/sec])
 *	    windParameters[1],			(beta [rad])
 *	    windParameters[2],			(alpha [rad])
 *	    linearAccelerations[0],		(a_x [ft/sec^2])
 *	    linearAccelerations[1],		(a_y [ft/sec^2])
 *	    linearAccelerations[2],		(a_z [ft/sec^2])
 *	    totalMoments[0],			(L [ft*lbf])
 *	    totalMoments[1],			(M [ft*lbf])
 *	    totalMoments[2],			(N [ft*lbf])
 *	    NEDPosition[0],				(N [ft])
 *	    NEDPosition[1],				(E [ft])
 *	    NEDPosition[2]				(D [ft])
 */
public class Integrate6DOFEquations {
	
	private double[] linearVelocities 		= new double[3];
	private double[] NEDPosition      		= new double[3];
	private double[] eulerAngles      		= new double[3];
	private double[] angularRates     		= new double[3];
	
	private double[] windParameters   		= new double[3];
	private double[] environmentParameters  = new double[4];
	private double[] gravity				= new double[3];
	
	private double[] linearAccelerations    = new double[3];
	private double[] totalMoments     		= new double[3];
	
	private double[] controls				= new double[10];
	
	private double[] sixDOFDerivatives		= new double[12];
	
	//TODO need a way to calculate alphaDot
	private double alphaDot = 0.0f; // = u*w_dot-w*u_dot/(u^2+w^2)
	
	private Aircraft aircraft  				= new Aircraft();
	private FixedPitchPropEngine engine 	= new FixedPitchPropEngine();
	private AccelAndMoments accelAndMoments = new AccelAndMoments();
	
	private ArrayList<Double[]> logsOut     = new ArrayList<>();
	private Double[] simOut;
	
	public Integrate6DOFEquations(double[] integratorConfig,
								  double[] initialConditions,
								  double[] controlsIn,
								  Aircraft aircraftIn,
								  FixedPitchPropEngine engineIn) {

		this.aircraft = aircraftIn;
		this.engine   = engineIn;
		this.controls = controlsIn;
		this.gravity  = Environment.getGravity();
		
		// Calculate initial data members' values
		updateDataMembers(initialConditions);

		// Use fourth-order Runge-Kutta numerical integration with time step of dt
		ClassicalRungeKuttaIntegrator integrator = new ClassicalRungeKuttaIntegrator(integratorConfig[2]);
		
		// Allocate states array for integrated states
		double[] y = new double[12];
		
		// Integration loop
		for (double t = integratorConfig[0]; t < integratorConfig[2]; t += integratorConfig[1]) {
			// Run a single step of integration each step of the loop
			y = integrator.singleStep(new SixDOFEquations(), 	  // derivatives
									  t, 		  				  // start time
									  initialConditions, 		  // initial conditions
									  t+integratorConfig[1]);     // end time
			
			// Update data members' values
			updateDataMembers(y);
			
			// Update initial conditions for next step of integration
			initialConditions = y;
			
			// Update output log (don't output states to console)
			logData(t, false);
		}
		
	}

	private class SixDOFEquations implements FirstOrderDifferentialEquations {		
		private SixDOFEquations() {}

		public void computeDerivatives(double t, double[] y, double[] yDot) {
			for (int i = 0; i < yDot.length; i++) 
				yDot[i] = sixDOFDerivatives[i];
		}

		public int getDimension() {return 12;}
	}
	
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
	
	public ArrayList<Double[]> getLogsOut() {return logsOut;}
	
	// Runs helper methods to update data members in functions
	private void updateDataMembers(double[] y) {
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
		
		// Update engine
		this.engine.updateEngineState(controls, 
									  NEDPosition, 
									  environmentParameters, 
									  windParameters);
		
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
	private void logData(double t, boolean useConsole) {
		// Create an output array of all state arrays
		simOut = new Double[] {t, 
							   linearVelocities[0],						// u (ft/sec)
							   linearVelocities[1],						// v (ft/sec)
							   linearVelocities[2],						// w (ft/sec)
							   NEDPosition[0],							// N (ft)
							   NEDPosition[1],							// E (ft)
							   NEDPosition[2],							// D (ft)
							   eulerAngles[0],							// phi (rad)
							   eulerAngles[1],							// theta (rad)
							   eulerAngles[2],							// psi (rad)
							   angularRates[0],							// p (rad/sec)
							   angularRates[1],							// q (rad/sec)
							   angularRates[2],							// e (rad/sec)
							   windParameters[0],						// TAS (ft/sec)
							   windParameters[1],						// beta (rad)
							   windParameters[2],						// alpha (rad)
							   linearAccelerations[0],					// a_x (ft/sec^2) (engine and aero)
							   linearAccelerations[1],					// a_y (ft/sec^2) (engine and aero)
							   linearAccelerations[2],					// a_z (ft/sec^2) (engine and aero)
							   totalMoments[0],							// L (ft*lb)
							   totalMoments[1],							// M (ft*lb)
							   totalMoments[2],							// N (ft*lb)
							  (sixDOFDerivatives[0]/gravity[2]),      	// an_x (g) 
							  (sixDOFDerivatives[1]/gravity[2]),	  	// an_y (g)
							 ((sixDOFDerivatives[2]/gravity[2])+1.0), 	// an_z (g) (with 1.0 bias of gravity)
							   sixDOFDerivatives[3],					// N_dot (ft/sec)
							   sixDOFDerivatives[4],					// E_dot (ft/sec)
							   sixDOFDerivatives[5],					// D_dot (ft/sec)
							   sixDOFDerivatives[6],					// phi_dot (rad/sec)
							   sixDOFDerivatives[7],					// theta_dot (rad/sec)
							   sixDOFDerivatives[8],					// psi_dot (rad/sec)
							   sixDOFDerivatives[9],					// p_dot (rad/sec^2)
							   sixDOFDerivatives[10],					// q_dot (rad/sec^2)
							   sixDOFDerivatives[11],					// r_dot (rad/sec^2)
							   engine.getThrust()[0],					// thrust (lb)
							   engine.getRPM(),							// rpm (rev/min)
							   engine.getFuelFlow()};					// fuelFlow (gal/hr)
		
		// Add output step to logging arrayList
		logsOut.add(simOut);
		
		if (useConsole) {
			for (Double out : simOut)
				System.out.printf("%9.2f ", out);
			System.out.println("\n");
		}
	}
	
	public Double[] getOutputStep() {return simOut;}
}