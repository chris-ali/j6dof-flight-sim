package com.chrisali.javaflightsim.simulation.enviroment;

import java.util.EnumMap;

/**
 * This class calculates atmospheric parameters as a function of height, and the gravitational acceleration constant.
 * It uses the 1976 NASA Standard Atmosphere model, and assumes that gravity is constant in the Z direction.
 */
public class Environment {
	private static final double RADIUS_EARTH = 3959*5280;
	
	private static final double R = 1716.49;
	private static final double GAMMA = 1.4;
	private static final double RHO_SSL = 0.002377;
	private static final double P_SSL = 2116.22;
	private static final double T_SSL = 518.67;
	private static final double GRAVITY = 32.17;
	
	private static final double HT_TROP = 36089;
	private static final double P_TROP = 472.6758;
	private static final double RHO_TROP = 0.000706115;
	
	private static final double ENV_CONST_TROP = 0.0000068755;
	private static final double ENV_CONST_STRAT = -0.0000480637;
	
	/**
	 * Calculates the temperature (R), presssure (lb/ft^2), density (slug/ft^3), speed of sound (ft/sec) and gravity (ft/sec^2)
	 * for a given height above Earth and then places that data into an EnumMap with {@link EnvironmentParameters} as the keys 
	 * 
	 * @param NEDPosition
	 * @return EnumMap of environment parameters
	 */
	public static EnumMap<EnvironmentParameters, Double> updateEnvironmentParams(double[] NEDPosition) {
		EnumMap<EnvironmentParameters, Double> environmentParams = new EnumMap<EnvironmentParameters, Double>(EnvironmentParameters.class); 
		Double temp, rho, p, a, g;
		
		// Troposphere
		if (NEDPosition[2] < HT_TROP) {
			temp = T_SSL-(0.003566*NEDPosition[2]);                              // (deg R)
			p = P_SSL*Math.pow((1-(ENV_CONST_TROP*NEDPosition[2])),5.2559);      // (lbf/ft^2)
			rho = RHO_SSL*Math.pow((1-(ENV_CONST_TROP*NEDPosition[2])),4.2559);  // (slug/ft^3) 												
		}
		// Stratosphere
		else {
			temp = 389.97; 														 // (degR)
			p = P_TROP*Math.exp(ENV_CONST_STRAT*(NEDPosition[2]-HT_TROP)); 		 // (lbf/ft^2)
			rho = RHO_TROP*Math.exp(ENV_CONST_STRAT*(NEDPosition[2]-HT_TROP));   // (slug/ft^3)			
		}
		
		a = Math.sqrt(GAMMA*R*temp);     									 // (ft/sec)
		
		g = GRAVITY*(RADIUS_EARTH/(RADIUS_EARTH+NEDPosition[2]));
		
		environmentParams.put(EnvironmentParameters.T,       temp);
		environmentParams.put(EnvironmentParameters.P,       p);
		environmentParams.put(EnvironmentParameters.RHO,     rho);
		environmentParams.put(EnvironmentParameters.A,       a);
		environmentParams.put(EnvironmentParameters.GRAVITY, g);
		
		return environmentParams;
	}
	
	/**
	 * @return Gravity (ft/sec^2) as a double array vector
	 */
	public static double getGravity() {return GRAVITY;}
}
