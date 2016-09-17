package com.chrisali.javaflightsim.simulation.integration;

/**
 * This class provides methods to limit the results calculated in {@link Integrate6DOFEquations} to prevent large or non-physical values 
 * of the 6DOF states from affecting the simulation elsewhere  
 */
public class SaturationLimits {
	
	/**
	 * @return Angle bound between 0 and 2*Pi. 
	 */
	public static double twoPiBounding(double angle) {
		// Return only positive values of angle between 0 and 2*pi
		angle += 2*Math.PI;
		angle %= 2*Math.PI;
		
		return angle;
	}
	
	/**
	 * Binds pitch and bank angles between +/- 2*Pi, and heading between 0 and 2*Pi. In addition, pitch is prevented from reaching +/- Pi/2, 
	 * as this would cause a singularity in the calculation of heading in {@link Integrate6DOFEquations}
	 * @param eulerAngles
	 * @param angularRates
	 * @return eulerAngles
	 */
	public static double[] piBounding(double[] eulerAngles, double[] angularRates) {
		double phi   = eulerAngles[0];
		double theta = eulerAngles[1];
		double psi   = eulerAngles[2];
		
		// If banked past +/- pi, subtract/add 2*pi to return -/+ pi
//		if (eulerAngles[0] > Math.PI) {
//			phi -= 2*Math.PI;
//			phi %= Math.PI;
//		} else if (eulerAngles[0] < -Math.PI) {
//			phi += 2*Math.PI;
//			phi %= Math.PI;
//		}
		
		phi %= 2*Math.PI;
		
		if (eulerAngles[1] > Math.PI) {
			theta -= 2*Math.PI;
			theta %= Math.PI;
		} else if (eulerAngles[1] < -Math.PI) {
			theta += 2*Math.PI;
			theta %= Math.PI;
		// Prevent theta from reaching PI/2, which would cause singularity in 6DOF equations
		} else if (eulerAngles[1] ==  Math.PI/2) {
			theta = angularRates[1] > 0 ? Math.PI*0.52 : Math.PI*0.48;
		} else if (eulerAngles[1] == -Math.PI/2) {
			theta = angularRates[1] < 0 ? -Math.PI*0.52 : -Math.PI*0.48;
		}
		
		// Return only positive values of psi between 0 and 2*pi
		psi += 2*Math.PI;
		psi %= 2*Math.PI;
		
		eulerAngles[0] = phi;
		eulerAngles[1] = theta;
		eulerAngles[2] = psi;
		
		return eulerAngles;
	}
	
	/**
	 *  Limits u, v and w velocities; u is restricted to 0.5-1000 ft/sec, while v and w are restricted to +/-1000 ft/sec.
	 *  On ground v is restricted to -3-3 ft/sec
	 *  @param linearVelocities
	 *  @param isWeightOnWheels
	 *  @return linearVelocities
	 */
	public static double[] limitLinearVelocities(double[] linearVelocities) {
		double u = linearVelocities[0];
		double v = linearVelocities[1];
		double w = linearVelocities[2];
		
		// Airborne limits
		if (linearVelocities[0] < 0.5)
			u = 0.5;
		else if (linearVelocities[0] > 1000)
			u = 1000;
		
		if (linearVelocities[1] < -1000)
			v = -1000;
		else if (linearVelocities[1] > 1000)
			v = 1000;
		
		if (linearVelocities[2] < -1000)
			w = -1000;
		else if (linearVelocities[2] > 1000)
			w = 1000;
		
		linearVelocities[0] = u;
		linearVelocities[1] = v;
		linearVelocities[2] = w;
		
		return linearVelocities;
	}
	
	/**
	 *  Limits angular rates for p, q and r to +/-10 rad/sec
	 *  @return angularRates
	 *  @return angularRates
	 */
	public static double[] limitAngularRates(double[] angularRates) {
		double p = angularRates[0];
		double q = angularRates[1];
		double r = angularRates[2];
		
		if (angularRates[0] < -10)
			p = -10;
		else if (angularRates[0] > 10)
			p = 10;
		
		if (angularRates[1] < -10)
			q = -10;
		else if (angularRates[1] > 10)
			q = -10;
		
		if (angularRates[2] < -10)
			r = -10;
		else if (angularRates[2] > 10)
			r = 10;
		
		angularRates[0] = p;
		angularRates[1] = q;
		angularRates[2] = r;
		
		return angularRates;
	}
	
	/**
	 *  Limits accelerations in all directions to +/-1000 ft/sec^2
	 *  @param linearAccelerations
	 *  @return linearAccelerations
	 */
	public static double[] limitLinearAccelerations(double[] linearAccelerations) {
		double a_x = linearAccelerations[0];
		double a_y = linearAccelerations[1];
		double a_z = linearAccelerations[2];
		
		if (linearAccelerations[0] < -1000)
			a_x = -1000;
		else if (linearAccelerations[0] > 1000)
			a_x = 1000;
		
		if (linearAccelerations[1] < -1000)
			a_y = -1000;
		else if (linearAccelerations[1] > 1000)
			a_y = -1000;
		
		if (linearAccelerations[2] < -1000)
			a_z = -1000;
		else if (linearAccelerations[2] > 1000)
			a_z = 1000;
		
		linearAccelerations[0] = a_x;
		linearAccelerations[1] = a_y;
		linearAccelerations[2] = a_z;
		
		return linearAccelerations;
	}
	
	/**
	 *  Limits moments in all directions to +/-100000 lb*ft
	 *  @return linearMoments
	 *  @return linearMoments
	 */
	public static double[] limitTotalMoments(double[] totalMoments) {
		double m_x = totalMoments[0];
		double m_y = totalMoments[1];
		double m_z = totalMoments[2];
		
		if (totalMoments[0] < -100000)
			m_x = -100000;
		else if (totalMoments[0] > 100000)
			m_x = 100000;
		
		if (totalMoments[1] < -100000)
			m_y = -100000;
		else if (totalMoments[1] > 100000)
			m_y = -100000;
		
		if (totalMoments[2] < -100000)
			m_z = -100000;
		else if (totalMoments[2] > 100000)
			m_z = 100000;
		
		totalMoments[0] = m_x;
		totalMoments[1] = m_y;
		totalMoments[2] = m_z;
		
		return totalMoments;
	}
	
	/**
	 *  Limits true airspeed to 0.5-1000 ft/sec, beta to +/-Pi/4 and alpha to +/-Pi/12 
	 *  @param windParameters
	 *  @return windParameters
	 */
	public static double[] limitWindParameters(double[] windParameters) {
		double vTrue = windParameters[0];
		double beta  = windParameters[1];
		double alpha = windParameters[2];
		
		if (windParameters[0] < 0.5)
			vTrue = 0.5;
		else if (windParameters[0] > 1000)
			vTrue = 1000;
		
		if (windParameters[1] < -Math.PI/4)
			beta = -Math.PI/4;
		else if (windParameters[1] > Math.PI/4)
			beta = Math.PI/4;
		
		if (windParameters[2] < -Math.PI/12)
			alpha = -Math.PI/12;
		else if (windParameters[2] > Math.PI/12)
			alpha = Math.PI/12;
		
		// At slow speeds zero alpha and beta to prevent incorrectly large values of alpha and beta
		if (windParameters[0] < 30) {
			beta = 0.0;
			alpha = 0.0;
		}
		
		windParameters[0] = vTrue;
		windParameters[1] = beta;
		windParameters[2] = alpha;
		
		return windParameters;
	}
	
	/**
	 * Prevents from sinking too far below ground
	 * 
	 * @param NEDPosition
	 * @param terrainHeight
	 * @return NEDPosition
	 */
	public static double[] limitNEDPosition(double[] NEDPosition, 
											double terrainHeight) {
		if (NEDPosition[2] < terrainHeight-10)
			NEDPosition[2] = terrainHeight-10;
		
		return NEDPosition;
	}
	
}
