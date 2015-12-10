package sixDOFFlightSim;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/*
 * This class is the base abstract class for the 6DOF simulation's engine model. 
 * It uses the 1976 NASA Standard Atmosphere model, and assumes that gravity is constant in the Z direction.
 *  
 * The following (double array) must be passed in:
 * 		NEDPosition[]{N,E,D}  (ft)
 * 
 * The class outputs the following (double array):
 *      engineMoment[]{M_eng_x,M_eng_y,M_eng_z}  (deg R, slug/ft^3, lbf/ft^2, ft/sec)
 */
public abstract class EngineModel extends Aircraft {
	
	final protected double A_P = 1.132; 
	final protected double B_P = 0.132;
	final protected double RHO_SSL = 0.002377;
	final protected double HP_2_FTLBS = 550;
	
	protected double maxBHP;            //BHP at standard sea level
	protected double maxRPM;			//rev/min
	protected double propDiameter;		//ft
	protected double propArea;			//ft^2
	protected double propEfficiency;    
	
	protected double[] engineThrust = {0, 0, 0};	//{T_x,T_y,T_z}	(lbf)			
	protected double[] engineMoment = {0, 0, 0};	//{M_x,M_y,M_z} (lbf)

	
	//TODO need engine model properties (etaP, advance ratio, bhp curves) for lookup tables
	//TODO etaP needs to vary
  	//TODO model depends on if prop/jet	

	public double[] getEngMoments() {
		Vector3D forceVector = new Vector3D(engineThrust);
		Vector3D armVector = new Vector3D(enginePosition);
		
		engineMoment = Vector3D.crossProduct(forceVector, armVector).toArray();
		return engineMoment;
	}


}
