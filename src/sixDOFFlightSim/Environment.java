package sixDOFFlightSim;

/*
 * This class calculates atmospheric parameters as a function of height, and the gravitational acceleration constant.
 * It uses the 1976 NASA Standard Atmosphere model, and assumes that gravity is constant in the Z direction.
 *  
 * The following (double array) must be passed in:
 * 		NEDPosition[]{N,E,D}  (ft)
 * 
 * The class outputs the following (double arrays):
 *      environmentParameters[]{temp,rho,p,a}  (deg R, slug/ft^3, lbf/ft^2, ft/sec)
 *      gravity[]{G_x,G_y,G_z} (ft/sec^2)
 */
public class Environment {
	
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
	
	public static double[] getEnvironmentParams(double[] NEDPosition) {
		double temp, rho, p, a;
		
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
		
		a = Math.pow(GAMMA*R*temp,1/2);     									 // (ft/sec)

		return new double[] {temp,rho,p,a};
	}
	
	public static double[] getGravity() {
		return new double[] {0, 0, GRAVITY};
	}
	
}
