package sixDOFFlightSim;

import org.apache.commons.math3.ode.*;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;

/*
 * This class integrates all 12 6DOF equations numerically to obtain the aircraft states.
 * The Apache Commons' Classical Runge-Kutta is used to integrate over a period of time defined in the simConfig double array. 
 *  
 * In order to formulate the first order ODE, the following (double arrays) must be passed in:
 * 		linearAccelerations[]{a_x,a_y,a_z}   (ft/sec^2)
 * 		aerodynamicMoments[] {L,M,N}
 * 		massProperties[]{mass,Ix,Iy,Iz,Ixz}  
 * 		gravity[]{g_x,g_y,g_z}				 (ft/sec^2)
 * 		integratorConfig[]{0,dt,dt}    (sec)
 * 		initialConditions[]{initU,initV,initW,initN,initE,initD,initPhi,initTheta,initPsi,initP,initQ,initR}
 * 
 * The class outputs the following (double arrays):
 *      linearVelocities[]{u,v,w}     (ft/sec)
 *      NEDPosition[]{N,E,D}		  (ft)		
 *      eulerAngles[]{phi,theta,psi}  (rad)
 * 		angularRates[]{p,q,r}		  (rad/sec)
 */
public class Integrate6DOFEquations  {
	
	public double[] linearVelocities = new double[3];
	public double[] NEDPosition      = new double[3];
	public double[] eulerAngles      = new double[3];
	public double[] angularRates     = new double[3];
	
	public Integrate6DOFEquations(double[] linearAccelerations,
			   					  double[] aerodynamicMoments,
								  double[] massProperties,
								  double[] gravity,
								  double[] integratorConfig,
								  double[] initialConditions) {
		
		// construct 12 6DOF state equations to integrate numerically 
		SixDOFEquations equations = new SixDOFEquations(linearAccelerations,
													  	aerodynamicMoments,
													  	massProperties,
													  	gravity);

		// using fourth-order Runge-Kutta numerical integration with time step of dt
		ClassicalRungeKuttaIntegrator integrator = new ClassicalRungeKuttaIntegrator(integratorConfig[2]);

		// run integrator for one step; assign y[] values to object member double arrays
		double[] yTemp = new double[12];
		yTemp = integrator.singleStep(equations,
									  integratorConfig[0],              // start time
									  new double[]{initialConditions[0],initialConditions[1],initialConditions[2],
									   		       initialConditions[3],initialConditions[4],initialConditions[5],
										 	       initialConditions[6],initialConditions[7],initialConditions[8],
											       initialConditions[9],initialConditions[10],initialConditions[11]},    // initial conditions
									  integratorConfig[2]);              // end time (equal to dt for single step)\
		
		// assign indices in yTemp array to 6DOF state arrays
		for (int i=0; i<linearVelocities.length; i++) {
			linearVelocities[i] = yTemp[i];
			NEDPosition[i] = yTemp[i+3];
			eulerAngles[i] = yTemp[i+6];
			angularRates[i] = yTemp[i+9];
		}
		
		// implement saturation and (2)pi bounding to keep states within realistic limits
		linearVelocities = SaturationLimits.limitLinearVelocities(linearVelocities);
		eulerAngles = SaturationLimits.piBounding(eulerAngles);
		angularRates = SaturationLimits.limitAngularRates(angularRates);
	}

	public class SixDOFEquations implements FirstOrderDifferentialEquations {
		
		private double a_x;
		private double a_y;
		private double a_z;
		
		private double L;
		private double M;
		private double N;
		
		private double mass;
		private double Ix;
		private double Iy;
		private double Iz;
		private double Ixz;
		
		private double g_z;

		public SixDOFEquations(double[] linearAccelerations,
							   double[] aerodynamicMoments,
							   double[] massProperties,
							   double[] gravity) {
			
			this.a_x  = linearAccelerations[0];
			this.a_y  = linearAccelerations[1];
			this.a_z  = linearAccelerations[2];
			
			this.L    = aerodynamicMoments[0];
			this.M    = aerodynamicMoments[1];
			this.N    = aerodynamicMoments[2];
			
			this.mass = massProperties[0];
			this.Ix   = massProperties[1];
			this.Iy   = massProperties[2];
			this.Iz   = massProperties[3];
			this.Ixz  = massProperties[4];
						
			this.g_z  = gravity[2];
		}

		public void computeDerivatives(double t, double[] y, double[] yDot) {
			double[][] dirCosMat = SixDOFUtilities.body2Ned(new double[]{y[6], y[7], y[8]});      // create DCM for NED equations
			double[] inertiaCoeffs = SixDOFUtilities.getInertiaCoeffs(new double[]{mass,Ix,Iy,Iz,Ixz});
			
			yDot[0]  = (y[11]*y[1])-(y[10]*y[2])+(g_z*Math.sin(y[7]))-a_x;    			    // u (ft/sec)
			yDot[1]  = (y[9]*y[2])-(y[11]*y[0])-(g_z*Math.sin(y[6])*Math.cos(y[7]))-a_y;    // v (ft/sec)
			yDot[2]  = (y[10]*y[0])-(y[9]*y[1])-(g_z*Math.cos(y[6])*Math.cos(y[7]))-a_z;    // w (ft/sec)
			
			yDot[3]  = y[0]*dirCosMat[0][0]+y[1]*dirCosMat[0][1]+y[2]*dirCosMat[0][2];      // N (ft)
			yDot[4]  = y[0]*dirCosMat[1][0]+y[1]*dirCosMat[1][1]+y[2]*dirCosMat[1][2];      // E (ft)
			yDot[5]  = y[0]*dirCosMat[2][0]-y[1]*dirCosMat[2][1]-y[2]*dirCosMat[2][2];      // D (ft)
			
			yDot[6]  = y[9]+(Math.tan(y[7])*((y[10]*Math.sin(y[6]))+(y[11]*Math.cos(y[6])))); // phi (rad)
			yDot[7]  = (y[10]*Math.cos(y[6]))-(y[11]*Math.sin(y[6]));     			          // theta (rad)
			yDot[8]  = ((y[10]*Math.sin(y[6]))+(y[11]*Math.cos(y[6])))/Math.cos(y[7]);        // psi (rad)
			
			yDot[9]  = ((-(inertiaCoeffs[0]*y[11])+(inertiaCoeffs[1]*y[9]))*y[10])+(inertiaCoeffs[2]*L)+(inertiaCoeffs[3]*N);     // p (rad/sec)
			yDot[10] = (inertiaCoeffs[4]*y[9]*y[11])-(inertiaCoeffs[5]*((y[9]*y[9])-(y[11]*y[11])))+(inertiaCoeffs[6]*M);        // q (rad/sec)
			yDot[11] = (((inertiaCoeffs[7]*y[9])-(inertiaCoeffs[1]*y[11]))*y[10])+(inertiaCoeffs[3]*L)+(inertiaCoeffs[8]*N);     // r (rad/sec)
		}

		public int getDimension() {
			return 12;
		}

	}
		
	// integrator init function; sets up any 
	public void init(double t0, double[] y0, double t) {

	}

}