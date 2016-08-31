package com.chrisali.javaflightsim.utilities;

import java.util.Map;

import com.chrisali.javaflightsim.simulation.aircraft.Aircraft;
import com.chrisali.javaflightsim.simulation.enviroment.EnvironmentParameters;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.integration.SaturationLimits;

/**
 * This class contains methods to do coordinate axes transformations from Body to NED and from Wind to Body. It also calculates Geodetic coordinates from NED position, Mach number, 
 * alphadot, wind parameters (true airspeed, beta and alpha), and Inertia parameters used in {@link Integrate6DOFEquations} to calculate derivatives
 * 
 * @see Source: <i>Small Unmanned Aircraft: Theory and Practice by Beard, R.W. and McLain, T.W.</i>
 */
public class SixDOFUtilities {
	
	/**
	 * Calculates the direction cosine matrix needed to convert from body to NED coordinate axes 
	 * @see <i>Source Small Unmanned Aircraft: Theory and Practice by Beard, R.W. and McLain, T.W.</i>
	 */
	public static double[][] body2Ned(double[] eulerAngles) {
		double body2NedDCM[][] = new double[3][3]; //[column][row]
		
		body2NedDCM[0][0] =  Math.cos(eulerAngles[1])*Math.cos(eulerAngles[2]);
		body2NedDCM[1][0] =  Math.cos(eulerAngles[1])*Math.sin(eulerAngles[2]);
		body2NedDCM[2][0] = -Math.sin(eulerAngles[1]);
		
		body2NedDCM[0][1] =  Math.sin(eulerAngles[0])*Math.sin(eulerAngles[1])*Math.cos(eulerAngles[2]) - Math.cos(eulerAngles[0])*Math.sin(eulerAngles[2]);
		body2NedDCM[1][1] =  Math.sin(eulerAngles[0])*Math.sin(eulerAngles[1])*Math.sin(eulerAngles[2]) + Math.cos(eulerAngles[0])*Math.cos(eulerAngles[2]);
		body2NedDCM[2][1] =  Math.sin(eulerAngles[0])*Math.cos(eulerAngles[1]);
		
		body2NedDCM[0][2] =  Math.cos(eulerAngles[0])*Math.sin(eulerAngles[1])*Math.cos(eulerAngles[2]) + Math.sin(eulerAngles[0])*Math.sin(eulerAngles[2]);
		body2NedDCM[1][2] =  Math.cos(eulerAngles[0])*Math.sin(eulerAngles[1])*Math.sin(eulerAngles[2]) - Math.sin(eulerAngles[0])*Math.cos(eulerAngles[2]);
		body2NedDCM[2][2] =  Math.cos(eulerAngles[0])*Math.cos(eulerAngles[1]);
				
		return body2NedDCM;
	}
	
	/**
	 *  Calculates the inertia coefficients used in the calculation of p, q and r dot in {@link Integrate6DOFEquations}
	 *  @see Aircraft
	 *  @see <i>Source Small Unmanned Aircraft: Theory and Practice by Beard, R.W. and McLain, T.W.</i>
	 */
	public static double[] calculateInertiaCoeffs(double[] inertiaVals) { //inertiaVals[]{Ix,Iy,Iz,Ixz}
		double[] inertiaCoeffs = new double[9];
		
		double gamma = (inertiaVals[0]*inertiaVals[2])-(Math.pow(inertiaVals[3], 2));
		
		inertiaCoeffs[0] = (((inertiaVals[1]-inertiaVals[2])*inertiaVals[2])-(Math.pow(inertiaVals[3], 2)))/gamma;
		inertiaCoeffs[1] = (inertiaVals[0]-inertiaVals[1]+inertiaVals[2])*inertiaVals[3]/gamma;
		inertiaCoeffs[2] = inertiaVals[2]/gamma;
		inertiaCoeffs[3] = inertiaVals[3]/gamma;
		inertiaCoeffs[4] = (inertiaVals[2]-inertiaVals[0])/inertiaVals[1];
		inertiaCoeffs[5] = inertiaVals[3]/inertiaVals[1];
		inertiaCoeffs[6] = 1/inertiaVals[1];
		inertiaCoeffs[7] = (inertiaVals[0]*(inertiaVals[0]-inertiaVals[1])+(Math.pow(inertiaVals[3], 2)))/gamma;
		inertiaCoeffs[8] = inertiaVals[0]/gamma;
		
		return inertiaCoeffs;
	}
	
	/**
	 * Calculates the direction cosine matrix needed to convert from wind to body coordinate axes 
	 * @see <i>Source Small Unmanned Aircraft: Theory and Practice by Beard, R.W. and McLain, T.W.</i>
	 */
	public static double[][] wind2Body(double[] windParameters) {
		double wind2BodyDCM[][] = new double[3][3]; //[row][column]
		
		wind2BodyDCM[0][0] =  Math.cos(windParameters[1])*Math.cos(windParameters[2]);
		wind2BodyDCM[1][0] =  Math.sin(windParameters[1]);  
		wind2BodyDCM[2][0] =  Math.cos(windParameters[1])*Math.sin(windParameters[2]);
		
		wind2BodyDCM[0][1] = -Math.sin(windParameters[1])*Math.cos(windParameters[2]);										
		wind2BodyDCM[1][1] =  Math.cos(windParameters[1]);
		wind2BodyDCM[2][1] = -Math.sin(windParameters[1])*Math.sin(windParameters[2]);
		
		wind2BodyDCM[0][2] = -Math.sin(windParameters[2]);
		wind2BodyDCM[1][2] =  0; 
		wind2BodyDCM[2][2] =  Math.cos(windParameters[2]);
				
		return wind2BodyDCM;
	}
	
	/**
	 * Calculates the conversion factors needed to convert between lat/lon dot and N/E dot 
	 * @see Source: <i>G. Cai et al., Unmanned Rotorcraft Systems</i>
	 */
	public static double[] ned2LLA(double[] y) {
		double[] ned2LLA = new double[2]; // Conversion factors for latitude (lambda), longitude (phi) and altitude (h)
		
		// WGS84 Parameters
		double rEarth = 6378137; // Earth's radius [m]
		double e = 0.08181919; // Earth's eccentricity (e)
		
		double eSqSinSq = Math.pow(e, 2.0)*Math.pow(Math.sin(y[4]),2.0); //(e^2)sin^2(phi)
		
		double meridianRadiusCurvature = (rEarth*(1-Math.pow(e, 2.0)))/(Math.pow((1-eSqSinSq), 1.50));
		double verticalRadiusCurvature = rEarth/Math.sqrt(1-eSqSinSq);
		
		ned2LLA[0] = 1/(meridianRadiusCurvature+y[5]); 				    // phi_dot/N_dot
		ned2LLA[1] = 1/((verticalRadiusCurvature+y[5]*Math.cos(y[4]))); // lambda_dot/E_dot
		
		return ned2LLA;		
	}
	
	/**
	 * Calculates true airspeed, angle of sideslip and angle of attack 
	 * @see <i>Source Small Unmanned Aircraft: Theory and Practice by Beard, R.W. and McLain, T.W.</i>
	 */
	public static double[] calculateWindParameters(double[] linearVelocities) {
		double vTrue = Math.sqrt(Math.pow(linearVelocities[0],2) + Math.pow(linearVelocities[1],2) + Math.pow(linearVelocities[2],2));
		double beta = Math.asin(linearVelocities[1]/vTrue);
		double alpha = Math.atan(linearVelocities[2]/linearVelocities[0]);
		
		return SaturationLimits.limitWindParameters(new double[] {vTrue,beta,alpha});
	}
	
	/**
	 * Calculates rate of change of angle of attack with respect to time 
	 * @see <i>Source: R. Hall and S. Anstee, Trim Calculation Methods for a Dynamical Model of the REMUS 100 Autonomous Underwater Vehicle </i>
	 */
	public static double calculateAlphaDot(double[] linearVelocities, double[] sixDOFDerivatives) {
		return ((linearVelocities[0]*sixDOFDerivatives[2])-(linearVelocities[2]*sixDOFDerivatives[0]))
				/((Math.pow(linearVelocities[0], 2)+(Math.pow(linearVelocities[2], 2))));// = u*w_dot-w*u_dot/(u^2+w^2)
	}
	
	/**
	 * Calculates Mach number
	 * @see <i>Source Small Unmanned Aircraft: Theory and Practice by Beard, R.W. and McLain, T.W.</i>
	 */
	public static double calculateMach(double[] windParameters, Map<EnvironmentParameters, Double> environmentParameters) {
		return windParameters[0]/environmentParameters.get(EnvironmentParameters.A);
	}

}