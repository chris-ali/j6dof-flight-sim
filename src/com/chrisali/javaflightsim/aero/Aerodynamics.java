package com.chrisali.javaflightsim.aero;

import java.util.EnumMap;

import org.apache.commons.math3.analysis.interpolation.PiecewiseBicubicSplineInterpolatingFunction;

import com.chrisali.javaflightsim.aircraft.Aircraft;
import com.chrisali.javaflightsim.aircraft.AircraftBuilder;
import com.chrisali.javaflightsim.controls.FlightControls;
import com.chrisali.javaflightsim.enviroment.EnvironmentParameters;
import com.chrisali.javaflightsim.utilities.integration.SixDOFUtilities;

/**
 * This class calculates aerodynamic forces and moments in the stability coordinate frame. The aerodynamic forces are then converted to the 
 * body frame to calculate accelerations and moments in {@link AccelAndMoments}. Depending on the stability derivatives specified upon aircraft creation
 * in {@link AircraftBuilder} the stability derivatives can be either a constant double or interpolated linearly using a lookup table
 * 
 * @param EnumMap controls                            
 * @param windParameters 								
 * @param angularRates
 * @param double alphaDot
 * @param EnumMap environmentParams
 * 
 * @return aerodynamicMoments (ft*lbf)
 * @return aerodynamicForces  (lbf)
 * 
 * @see Aircraft
 * @see StabilityDerivatives
 * @see PiecewiseBicubicSplineInterpolatingFunction
 */
public class Aerodynamics extends Aircraft {
	
	// Calculate CL
	private double getCL(double[] angularRates,
						 double[] windParameters,
						 EnumMap<FlightControls, Double> controls,
						 double alphaDot) {
		double rotaryTerm = wingGeometry.get(WingGeometry.C_BAR)/(2*windParameters[0]);
		
		return (Double)stabDerivs.get(StabilityDerivatives.CL_ALPHA)*windParameters[2]+
			   (Double)stabDerivs.get(StabilityDerivatives.CL_0)+	
			   (Double)stabDerivs.get(StabilityDerivatives.CL_Q)*angularRates[1]*rotaryTerm+
			   (Double)stabDerivs.get(StabilityDerivatives.CL_ALPHA_DOT)*alphaDot*rotaryTerm+
			   (Double)stabDerivs.get(StabilityDerivatives.CL_D_ELEV)*controls.get(FlightControls.ELEVATOR)+	
			   (Double)stabDerivs.get(StabilityDerivatives.CL_D_FLAP)*controls.get(FlightControls.FLAPS);		
	}
	
	// Calculate CY
	private double getCY(double[] windParameters,
						 EnumMap<FlightControls, Double> controls) {
		return (Double)stabDerivs.get(StabilityDerivatives.CY_BETA)*windParameters[1]+
			   (Double)stabDerivs.get(StabilityDerivatives.CY_D_RUD)*controls.get(FlightControls.RUDDER);	
	}
	
	// Calculate CD
	private double getCD(double[] windParameters,
						 EnumMap<FlightControls, Double> controls) {
		return (Double)stabDerivs.get(StabilityDerivatives.CD_ALPHA)*Math.abs(windParameters[2])+ // Need absolute value to prevent negative drag at negative alpha
			   (Double)stabDerivs.get(StabilityDerivatives.CD_0)+
			   (Double)stabDerivs.get(StabilityDerivatives.CD_D_FLAP)*controls.get(FlightControls.FLAPS)+
			   (Double)stabDerivs.get(StabilityDerivatives.CD_D_ELEV)*controls.get(FlightControls.ELEVATOR)+
			   (Double)stabDerivs.get(StabilityDerivatives.CD_D_GEAR)*controls.get(FlightControls.GEAR);		
	}
	
	// Calculate CRoll
	private double getCRoll(double[] angularRates,
					  	    double[] windParameters,
					  	    EnumMap<FlightControls, Double> controls) {
		double helixAngle = wingGeometry.get(WingGeometry.B_WING)/(2*windParameters[0]);
		
		return (Double)stabDerivs.get(StabilityDerivatives.CROLL_BETA)*windParameters[1]+
			   (Double)stabDerivs.get(StabilityDerivatives.CROLL_P)*angularRates[0]*helixAngle+
			   (Double)stabDerivs.get(StabilityDerivatives.CROLL_R)*angularRates[2]*helixAngle+
			   (Double)stabDerivs.get(StabilityDerivatives.CROLL_D_AIL)*controls.get(FlightControls.AILERON)+
			   (Double)stabDerivs.get(StabilityDerivatives.CROLL_D_RUD)*controls.get(FlightControls.RUDDER);
	}
	
	// Calculate CM
	private double getCM(double[] angularRates,
						 double[] windParameters,
						 EnumMap<FlightControls, Double> controls,
						 double alphaDot) {
		double rotaryTerm = wingGeometry.get(WingGeometry.C_BAR)/(2*windParameters[0]);
		
		return (Double)stabDerivs.get(StabilityDerivatives.CM_ALPHA)*windParameters[2]+
			   (Double)stabDerivs.get(StabilityDerivatives.CM_0)+
			   (Double)stabDerivs.get(StabilityDerivatives.CM_Q)*angularRates[1]*rotaryTerm+
			   (Double)stabDerivs.get(StabilityDerivatives.CM_ALPHA_DOT)*alphaDot*rotaryTerm+
			   (Double)stabDerivs.get(StabilityDerivatives.CM_D_ELEV)*controls.get(FlightControls.ELEVATOR)+
			   (Double)stabDerivs.get(StabilityDerivatives.CM_D_FLAP)*controls.get(FlightControls.FLAPS);
	}
	
	// Calculate CN
	private double getCN(double[] angularRates,
						 double[] windParameters,
						 EnumMap<FlightControls, Double> controls) {
		double helixAngle = wingGeometry.get(WingGeometry.B_WING)/(2*windParameters[0]);
		
		return (Double)stabDerivs.get(StabilityDerivatives.CN_BETA)*windParameters[1]+
			   (Double)stabDerivs.get(StabilityDerivatives.CN_P)*angularRates[0]*helixAngle+
			   (Double)stabDerivs.get(StabilityDerivatives.CN_R)*angularRates[2]*helixAngle+
			   (Double)stabDerivs.get(StabilityDerivatives.CN_D_AIL)*controls.get(FlightControls.AILERON)+
			   (Double)stabDerivs.get(StabilityDerivatives.CN_D_RUD)*controls.get(FlightControls.RUDDER);	
	}
	
	// Gets the type of value contained in the specified key of the stability derivatives EnumMap, interpolate it if its type is PiecewiseBicubicSplineInterpolatingFunction  
	public Double calculateInterpStabDer(double[] windParameters,
			 							  EnumMap<FlightControls, Double> controls,
			 							  StabilityDerivatives stabDer) {
		Double interpStabDer;
		PiecewiseBicubicSplineInterpolatingFunction pbsif;
		
		// If object is of type Double, return that value, otherwise create an interpolating function and
		// get the interpolated Double value 
		if (stabDerivs.get(stabDer).getClass().getName().equals("java.lang.Double"))
			interpStabDer = (Double)stabDerivs.get(stabDer);
		else {
			pbsif = (PiecewiseBicubicSplineInterpolatingFunction)stabDerivs.get(stabDer);
			interpStabDer = pbsif.value(windParameters[2], controls.get(FlightControls.FLAPS));
		}
		
		return interpStabDer;
	}
	
	// Calculate Body Forces
	public double[] getBodyForces(double[] windParameters,
								  double[] angularRates,
								  EnumMap<EnvironmentParameters, Double> environmentParameters,
							      EnumMap<FlightControls, Double> controls,
								  double alphaDot) {
		double qBar = environmentParameters.get(EnvironmentParameters.RHO)*Math.pow(windParameters[0], 2)/2;
		
		double[][] w2bDCM = SixDOFUtilities.wind2Body(windParameters);
		
		// Negative L and D to switch body directions and position in array swapped
		double[] aeroForces = {-qBar*getCD(windParameters, controls)*wingGeometry.get(WingGeometry.S_WING),
				   			    qBar*getCY(windParameters, controls)*wingGeometry.get(WingGeometry.S_WING),
							   -qBar*getCL(angularRates, windParameters, controls, alphaDot)*wingGeometry.get(WingGeometry.S_WING)};
		
		return new double[] {aeroForces[0]*w2bDCM[0][0]+aeroForces[1]*w2bDCM[0][1]+aeroForces[2]*w2bDCM[0][2],
							 aeroForces[0]*w2bDCM[1][0]+aeroForces[1]*w2bDCM[1][1]+aeroForces[2]*w2bDCM[1][2],
							 aeroForces[0]*w2bDCM[2][0]+aeroForces[1]*w2bDCM[2][1]+aeroForces[2]*w2bDCM[2][2]};
	}
	
	// Calculate Aerodynamic Moments
	public double[] getAeroMoments(double[] windParameters,
								   double[] angularRates,
								   EnumMap<EnvironmentParameters, Double> environmentParameters,
								   EnumMap<FlightControls, Double> controls,
								   double alphaDot) {
		double qBar = environmentParameters.get(EnvironmentParameters.RHO)*Math.pow(windParameters[0], 2)/2;
		
		return new double[] {qBar*getCRoll(angularRates, windParameters, controls)*wingGeometry.get(WingGeometry.S_WING)*wingGeometry.get(WingGeometry.B_WING), 
							 qBar*getCM(angularRates, windParameters, controls, alphaDot)*wingGeometry.get(WingGeometry.S_WING)*wingGeometry.get(WingGeometry.C_BAR), 
						 	 qBar*getCN(angularRates, windParameters, controls)*wingGeometry.get(WingGeometry.S_WING)*wingGeometry.get(WingGeometry.B_WING)};					
	}
}
