package com.chrisali.javaflightsim.aero;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.chrisali.javaflightsim.propulsion.EngineModel;
import com.chrisali.javaflightsim.propulsion.FixedPitchPropEngine;
import com.chrisali.javaflightsim.utilities.integration.SaturationLimits;

public class AccelAndMoments extends Aerodynamics {
	
	public double[] getBodyAccelerations(double[] windParameters,
									     double[] angularRates,
									     double[] wingDimensions,
									     double[] environmentParameters,
									     double[] controls,
									     double alphaDot,
									     FixedPitchPropEngine engine) {
		
		Vector3D aeroForceVector = new Vector3D(getBodyForces(windParameters, 
															  angularRates, 
															  wingDimensions, 
															  environmentParameters, 
															  controls, 
															  alphaDot));
		
		Vector3D engineForce = new Vector3D(engine.getThrust());
		
		// Negative engine thrust propels aircraft forward according to coordinate system
		double[] tempLinearAccel = aeroForceVector.add(engineForce.negate()).scalarMultiply(1/massProperties[0]).toArray(); 
		
		return SaturationLimits.limitLinearAccelerations(tempLinearAccel);
	}
	
	public double[] getTotalMoments(double[] windParameters,
								    double[] angularRates,
								    double[] wingDimensions,
								    double[] environmentParameters,
								    double[] controls,
								    double alphaDot,
								    EngineModel engine) {

		Vector3D aeroForceVector = new Vector3D(getBodyForces(windParameters, 
															  angularRates, 
															  wingDimensions, 
															  environmentParameters, 
															  controls, 
															  alphaDot));
		
		Vector3D acVector = new Vector3D(aerodynamicCenter);
		Vector3D cgVector = new Vector3D(centerOfGravity);
		
		Vector3D aeroForceCrossProd = Vector3D.crossProduct(aeroForceVector, acVector.subtract(cgVector));
		
		Vector3D aeroMomentVector = new Vector3D(getAeroMoments(windParameters, 
																angularRates, 
																wingDimensions, 
																environmentParameters, 
																controls, 
																alphaDot)); 
		
		Vector3D engineMoment = new Vector3D(engine.getEngineMoment());
		
		double[] tempTotalMoments = aeroMomentVector.add(engineMoment).add(aeroForceCrossProd).toArray();
		
		return SaturationLimits.limitTotalMoments(tempTotalMoments); 
	}
}
