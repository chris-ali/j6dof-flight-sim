package com.chrisali.javaflightsim.aero;

import java.util.EnumMap;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.chrisali.javaflightsim.controls.FlightControls;
import com.chrisali.javaflightsim.propulsion.EngineModel;
import com.chrisali.javaflightsim.setup.IntegrationSetup;
import com.chrisali.javaflightsim.utilities.integration.SaturationLimits;

public class AccelAndMoments extends Aerodynamics {
	
	public double[] getBodyAccelerations(double[] windParameters,
									     double[] angularRates,
									     Double[] wingDimensions,
									     double[] environmentParameters,
									     EnumMap<FlightControls, Double> controls,
									     double alphaDot,
									     EngineModel engine) {
		
		Vector3D aeroForceVector = new Vector3D(getBodyForces(windParameters, 
															  angularRates, 
															  wingDimensions, 
															  environmentParameters, 
															  controls, 
															  alphaDot));
		
		Vector3D engineForce = new Vector3D(engine.getThrust());
		
		double[] tempLinearAccel = aeroForceVector.add(engineForce).scalarMultiply(1/massProperties[0]).toArray(); 
		
		return SaturationLimits.limitLinearAccelerations(tempLinearAccel);
	}
	
	public double[] getTotalMoments(double[] windParameters,
								    double[] angularRates,
								    Double[] wingDimensions,
								    double[] environmentParameters,
								    EnumMap<FlightControls, Double> controls,
								    double alphaDot,
								    EngineModel engine) {

		Vector3D aeroForceVector = new Vector3D(getBodyForces(windParameters, 
															  angularRates, 
															  wingDimensions, 
															  environmentParameters, 
															  controls, 
															  alphaDot));
		
		Vector3D acVector = new Vector3D(IntegrationSetup.unboxDoubleArray(aerodynamicCenter));
		Vector3D cgVector = new Vector3D(IntegrationSetup.unboxDoubleArray(centerOfGravity));
		
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
