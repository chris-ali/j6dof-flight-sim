package com.chrisali.javaflightsim.aero;

import java.util.EnumMap;

import com.chrisali.javaflightsim.aircraft.Aircraft;
import com.chrisali.javaflightsim.controls.FlightControls;
import com.chrisali.javaflightsim.utilities.integration.SixDOFUtilities;

/*
 * This class calculates aerodynamic forces and moments in the stability frame. The aerodynamic forces are then converted to the 
 * body frame to calculate linear accelerations. All stability derivatives are assumed to be constant for now.
 * 
 * TODO Make lookup tables for alpha/beta derivatives 
 *  
 * The following must be passed in:
 * 		controls EnumMap<FlightControls, Double>                            (rad,rad,rad,norm,norm,norm,norm,norm,norm,rad,norm,norm,norm)
 *      windParameters[]{vTrue,alpha,beta}  								(ft/sec,rad,rad)
 * 		angularRates[]{p,q,r}		  										(rad/sec)
 * 		double alphaDot				 										(rad/sec)
 * 		environmentParameters[]{temp,rho,p,a}  								(deg R, slug/ft^3, lbf/ft^2, ft/sec)
 * 
 * The following are inherited from the Aircraft class:
 *  	wingDimensions[]{wingSfcArea,b,c_bar} 								(ft^2,ft,ft) 
 * 		stabDerivs EnumMap<StabilityDerivatives, (Double)Object>		
 * 
 * The class outputs the following (double arrays):
 *      aerodynamicMoments[] {L,M,N} 										(ft*lbf)
 *      aerodynamicForces[] {L,Y,D}	 										(lbf)	
 */
public class Aerodynamics extends Aircraft {
	
	// Calculate CL
	private double getCL(double[] angularRates,
						 double[] windParameters,
						 EnumMap<FlightControls, Double> controls,
						 double alphaDot) {
		double rotaryTerm = wingDimensions[2]/(2*windParameters[0]);
		
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
		double helixAngle = wingDimensions[1]/(2*windParameters[0]);
		
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
		double rotaryTerm = wingDimensions[2]/(2*windParameters[0]);
		
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
		double helixAngle = wingDimensions[1]/(2*windParameters[0]);
		
		return (Double)stabDerivs.get(StabilityDerivatives.CN_BETA)*windParameters[1]+
			   (Double)stabDerivs.get(StabilityDerivatives.CN_P)*angularRates[0]*helixAngle+
			   (Double)stabDerivs.get(StabilityDerivatives.CN_R)*angularRates[2]*helixAngle+
			   (Double)stabDerivs.get(StabilityDerivatives.CN_D_AIL)*controls.get(FlightControls.AILERON)+
			   (Double)stabDerivs.get(StabilityDerivatives.CN_D_RUD)*controls.get(FlightControls.RUDDER);	
	}
	
	// Calculate Body Forces
	public double[] getBodyForces(double[] windParameters,
								  double[] angularRates,
								  double[] wingDimensions,
							      double[] environmentParameters,
							      EnumMap<FlightControls, Double> controls,
								  double alphaDot) {
		double qBar = environmentParameters[1]*windParameters[0]*windParameters[0]/2;
		
		double[][] w2bDCM = SixDOFUtilities.wind2Body(windParameters);
		
		// Negative L and D to switch body directions and position in array swapped
		double[] aeroForces = {-qBar*getCD(windParameters, controls)*wingDimensions[0],
				   			    qBar*getCY(windParameters, controls)*wingDimensions[0],
							   -qBar*getCL(angularRates, windParameters, controls, alphaDot)*wingDimensions[0]};
		
		return new double[] {aeroForces[0]*w2bDCM[0][0]+aeroForces[1]*w2bDCM[0][1]+aeroForces[2]*w2bDCM[0][2],
							 aeroForces[0]*w2bDCM[1][0]+aeroForces[1]*w2bDCM[1][1]+aeroForces[2]*w2bDCM[1][2],
							 aeroForces[0]*w2bDCM[2][0]+aeroForces[1]*w2bDCM[2][1]+aeroForces[2]*w2bDCM[2][2]};
	}
	
	// Calculate Aerodynamic Moments
	public double[] getAeroMoments(double[] windParameters,
								   double[] angularRates,
								   double[] wingDimensions,
								   double[] environmentParameters,
								   EnumMap<FlightControls, Double> controls,
								   double alphaDot) {
		double qBar = environmentParameters[1]*windParameters[0]*windParameters[0]/2;
		
		return new double[] {qBar*getCRoll(angularRates, windParameters, controls)*wingDimensions[0]*wingDimensions[1], 
							 qBar*getCM(angularRates, windParameters, controls, alphaDot)*wingDimensions[0]*wingDimensions[2], 
						 	 qBar*getCN(angularRates, windParameters, controls)*wingDimensions[0]*wingDimensions[1]};					
	}
}
