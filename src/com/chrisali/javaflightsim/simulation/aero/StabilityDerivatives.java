package com.chrisali.javaflightsim.simulation.aero;

import com.chrisali.javaflightsim.simulation.aircraft.Aircraft;

/**
 * Provides Enum values to define stability derivatives that make up the aerodynamics of an {@link Aircraft}. 
 * The String field is used to parse the Aero.txt file of an Aircraft object in the constructor. 
 * These values should be units of (1/rad)
 * 
 *   @see Aerodynamics 
 */
public enum StabilityDerivatives {
	// Lift Derivatives
	CL_ALPHA       	("CL_alpha"),
	CL_0 		   	("CL_0"),
	CL_Q 		   	("CL_q"),
	CL_ALPHA_DOT 	("CL_alpha_dot"),
	CL_D_ELEV 		("CL_d_elev"),
	CL_D_FLAP 		("CL_d_flap"),
	
	// Side Force Derivatives
	CY_BETA 		("CY_beta"),
	CY_D_RUD 		("CY_d_rud"),
	
	// Drag Derivatives
	CD_ALPHA 		("CD_alpha"),
	CD_0 			("CD_0"),
	CD_D_ELEV 		("CD_d_elev"),
	CD_D_FLAP 		("CD_d_flap"),
	CD_D_GEAR 		("CD_d_gear"),
	
	// Roll Moment Derivatives
	CROLL_BETA 		("Croll_beta"),
	CROLL_P 		("Croll_p"),
	CROLL_R 		("Croll_r"),
	CROLL_D_AIL 	("Croll_d_ail"),
	CROLL_D_RUD 	("Croll_d_rud"),
	
	// Pitch Moment Derivatives
	CM_ALPHA 		("CM_alpha"),
	CM_0 			("CM_0"),
	CM_Q 			("CM_q"),
	CM_ALPHA_DOT 	("CM_alpha_dot"),
	CM_D_ELEV 		("CM_d_elev"),
	CM_D_FLAP 		("CM_d_flap"),
	
	// Yaw Moment Derivatives
	CN_BETA 		("CN_beta"),
	CN_P 			("CN_p"),
	CN_R 			("CN_r"),
	CN_D_AIL 		("CN_d_ail"),
	CN_D_RUD 		("CN_d_rud");
	
	private final String stabDir;
	
	private StabilityDerivatives(String stabDir) {this.stabDir = stabDir;}
	
	public String toString() {return stabDir;}
}
