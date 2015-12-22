package com.chrisali.javaflightsim.aero;

import com.chrisali.javaflightsim.aircraft.Aircraft;
import com.chrisali.javaflightsim.utilities.integration.SixDOFUtilities;

/*
 * This class calculates aerodynamic forces and moments in the stability frame. The aerodynamic forces are then converted to the 
 * body frame to calculate linear accelerations. All stability derivatives are assumed to be constant for now.
 * 
 * TODO Make lookup tables for alpha/beta derivatives 
 *  
 * The following must be passed in:
 * 		controls[]{elevator,aileron,rudder,leftThrottle,rightThrottle,leftPropeller,rightPropeller,leftMixture,rightMixture,flaps,gear,leftBrake,rightBrake}   (rad,rad,rad,norm,norm,norm,norm,norm,norm,rad,norm,norm,norm)
 *      windParameters[]{vTrue,alpha,beta}  								(ft/sec,rad,rad)
 * 		angularRates[]{p,q,r}		  										(rad/sec)
 * 		double alphaDot				 										(rad/sec)
 * 
 * The following (double arrays) are inherited from the Aircraft class:
 *  	wingDimensions[]{wingSfcArea,b,c_bar} 								(ft^2,ft,ft) 
 * 		liftDerivs[]{CL_alpha,CL_0,CL_q,CL_alphadot,CL_de,CL_df}
 * 		sideForceDerivs[]{CY_beta,CY_dr}
 * 		dragDerivs[]{CD_alpha,CD_0,CD_df,CD_de,CD_dg}	
 * 		rollMomentDerivs[]{Cl_beta,Cl_p,Cl_r,Cl_da,Cl_dr}
 * 		pitchMomentDerivs[]{CM_alpha,CM_0,CM_q,CM_alphadot,CM_de,CM_df}
 * 		yawMomentDerivs[]{CN_beta,CN_p,CN_r,CN_da,CN_dr}
 *      environmentParameters[]{temp,rho,p,a}  								(deg R, slug/ft^3, lbf/ft^2, ft/sec)		
 * 
 * The class outputs the following (double arrays):
 *      aerodynamicMoments[] {L,M,N} 										(ft*lbf)
 *      aerodynamicForces[] {L,Y,D}	 										(lbf)	
 */
public class Aerodynamics extends Aircraft {
	
	// Calculate CL
	private double getCL(double[] angularRates,
						 double[] windParameters,
						 double[] controls,
						 double alphaDot) {
		double rotaryTerm = wingDimensions[2]/(2*windParameters[0]);
		
		return liftDerivs[0]*windParameters[2]+
			   liftDerivs[1]+	
			   liftDerivs[2]*angularRates[1]*rotaryTerm+
			   liftDerivs[3]*alphaDot+
			   liftDerivs[4]*controls[0]+	
			   liftDerivs[5]*controls[9];		
	}
	
	// Calculate CY
	private double getCY(double[] windParameters,
						 double[] controls) {
		return sideForceDerivs[0]*windParameters[1]+
			   sideForceDerivs[1]*controls[2];	
	}
	
	// Calculate CD
	private double getCD(double[] windParameters,
						 double[] controls) {
		return dragDerivs[0]*Math.abs(windParameters[2])+ // Need absolute value to prevent negative drag at negative alpha
			   dragDerivs[1]+
			   dragDerivs[2]*controls[9]+
			   dragDerivs[3]*controls[0]+
			   dragDerivs[4]*controls[10];		
	}
	
	// Calculate CRoll
	private double getCRoll(double[] angularRates,
					  	    double[] windParameters,
							double[] controls) {
		double helixAngle = wingDimensions[1]/(2*windParameters[0]);
		
		return rollMomentDerivs[0]*windParameters[1]+
			   rollMomentDerivs[1]*angularRates[0]*helixAngle+
			   rollMomentDerivs[2]*angularRates[2]*helixAngle+
			   rollMomentDerivs[3]*controls[1]+
			   rollMomentDerivs[4]*controls[2];
	}
	
	// Calculate CM
	private double getCM(double[] angularRates,
						 double[] windParameters,
						 double[] controls,
						 double alphaDot) {
		double rotaryTerm = wingDimensions[2]/(2*windParameters[0]);
		
		return pitchMomentDerivs[0]*windParameters[2]+
			   pitchMomentDerivs[1]+
			   pitchMomentDerivs[2]*angularRates[1]*rotaryTerm+
			   pitchMomentDerivs[3]*alphaDot+
			   pitchMomentDerivs[4]*controls[0]+
			   pitchMomentDerivs[5]*controls[9];
	}
	
	// Calculate CN
	private double getCN(double[] angularRates,
						 double[] windParameters,
						 double[] controls) {
		double helixAngle = wingDimensions[1]/(2*windParameters[0]);
		
		return yawMomentDerivs[0]*windParameters[1]+
			   yawMomentDerivs[1]*angularRates[0]*helixAngle+
			   yawMomentDerivs[2]*angularRates[2]*helixAngle+
			   yawMomentDerivs[3]*controls[1]+
			   yawMomentDerivs[4]*controls[2];	
	}
	
	// Calculate Body Forces
	public double[] getBodyForces(double[] windParameters,
								  double[] angularRates,
								  double[] wingDimensions,
							      double[] environmentParameters,
								  double[] controls,
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
								   double[] controls,
								   double alphaDot) {
		double qBar = environmentParameters[1]*windParameters[0]*windParameters[0]/2;
		
		return new double[] {qBar*getCRoll(angularRates, windParameters, controls)*wingDimensions[0]*wingDimensions[1], 
							 qBar*getCM(angularRates, windParameters, controls, alphaDot)*wingDimensions[0]*wingDimensions[2], 
						 	 qBar*getCN(angularRates, windParameters, controls)*wingDimensions[0]*wingDimensions[1]};					
	}
}
