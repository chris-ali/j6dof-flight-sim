package com.chrisali.javaflightsim.propulsion;

import java.util.Arrays;
import java.util.EnumMap;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.chrisali.javaflightsim.controls.FlightControls;
import com.chrisali.javaflightsim.enviroment.EnvironmentParameters;

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
public abstract class Engine {
	
	// Propeller Engine Parameters
	final static protected double A_P        = 1.132; 
	final static protected double B_P        = 0.132;
	final static protected double RHO_SSL    = 0.002377;
	final static protected double HP_2_FTLBS = 550;
	
	protected double maxBHP;            //BHP at standard sea level
	protected double maxRPM;			//rev/min
	protected double propDiameter;		//ft
	protected double propArea;			//ft^2
	protected double propEfficiency;
	protected double rpm;
	protected double fuelFlow;
	
	// Jet Engine Parameters
	//TODO add jet/turboprop
	
	// Universal Parameters
	protected String   engineName;
	protected int      engineNumber;
	protected double[] enginePosition; 	   			// {eng_x,eng_y,eng_z}  (ft)
	
	protected double[] engineThrust   = {0, 0, 0};	// {T_x,T_y,T_z}	    (lbf)			
	protected double[] engineMoment;				// {M_x,M_y,M_z}        (lbf)
		
	//TODO need engine model properties (etaP, advance ratio, bhp curves) for lookup tables
	//TODO etaP needs to vary
  	
	public abstract void updateEngineState(EnumMap<FlightControls, Double> controls,				
										   EnumMap<EnvironmentParameters, Double> environmentParameters,
										   double[] windParameters);
	
	// Moment calculation same regardless of engine type
	protected void calculateEngMoments() {
		Vector3D forceVector = new Vector3D(engineThrust);
		Vector3D armVector   = new Vector3D(enginePosition);
		
		this.engineMoment = Vector3D.crossProduct(forceVector, armVector).toArray();
	}
	
	public double[] getThrust() {return engineThrust;}
	
	public double[] getEngineMoment() {return engineMoment;}
	
	public double getRPM() {return rpm;}
	
	public double getFuelFlow() {return fuelFlow;}
	
	public String getEngineName() {return engineName;}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((engineName == null) ? 0 : engineName.hashCode());
		result = prime * result + engineNumber;
		result = prime * result + Arrays.hashCode(enginePosition);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Engine other = (Engine) obj;
		if (engineName == null) {
			if (other.engineName != null)
				return false;
		} else if (!engineName.equals(other.engineName))
			return false;
		if (engineNumber != other.engineNumber)
			return false;
		if (!Arrays.equals(enginePosition, other.enginePosition))
			return false;
		return true;
	}
}
