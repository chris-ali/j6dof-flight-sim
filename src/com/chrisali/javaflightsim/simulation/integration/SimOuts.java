/*******************************************************************************
 * Copyright (C) 2016-2017 Christopher Ali
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *  If you have any questions about this project, you can visit
 *  the project's GitHub repository at: http://github.com/chris-ali/j6dof-flight-sim/
 ******************************************************************************/
package com.chrisali.javaflightsim.simulation.integration;

/**
 * This Enum is used with {@link Integrate6DOFEquations} to define the EnumMap returned in {@link Integrate6DOFEquations#getSimOut()}. 
 * The string field is used in the console output to show the name of each value for clarity 
 */
public enum SimOuts {
	TIME 		("time"),
	U 			("u"),
	U_DOT 		("u_dot"),
	V 			("v"),
	V_DOT 		("v_dot"),
	W			("w"),
	W_DOT		("w_dot"),
	NORTH		("N"),
	NORTH_DOT	("N_dot"),
	EAST		("E"),
	EAST_DOT	("E_dot"),
	ALT			("alt"),
	ALT_DOT		("alt_dot"),
	PHI			("phi"),
	PHI_DOT		("phi_dot"),
	THETA		("theta"),
	THETA_DOT	("theta_dot"),
	PSI			("psi"),
	PSI_DOT		("psi_dot"),
	P			("p"),
	P_DOT		("p_dot"),
	Q			("q"),
	Q_DOT		("q_dot"),
	R			("r"),
	R_DOT		("r_dot"),
	TAS			("tas"),
	BETA		("beta"),
	ALPHA		("alpha"),
	ALPHA_DOT	("alpha_dot"),
	MACH		("Mach"),
	LAT			("lat"),
	LAT_DOT		("lat_dot"),
	LON			("lon"),
	LON_DOT		("lon_dot"),
	A_X			("A_x"),
	AN_X		("An_x"),
	A_Y			("A_y"),
	AN_Y		("An_y"),
	A_Z			("A_z"),
	AN_Z		("An_z"),
	L			("L"),
	M			("M"),
	N			("N"),
	THRUST_1	("thrust_1"),
	RPM_1		("rpm_1"),
	FUEL_FLOW_1	("fuel_flow_1"),
	THRUST_2	("thrust_2"),
	RPM_2		("rpm_2"),
	FUEL_FLOW_2	("fuel_flow_2"),
	THRUST_3	("thrust_3"),
	RPM_3		("rpm_3"),
	FUEL_FLOW_3	("fuel_flow_3"),
	THRUST_4	("thrust_4"),
	RPM_4		("rpm_4"),
	FUEL_FLOW_4	("fuel_flow_4"),
	ELEVATOR	("elevator"),
	AILERON		("aileron"),
	RUDDER		("rudder"),
	THROTTLE_1	("throttle_1"),
	THROTTLE_2	("throttle_2"),
	THROTTLE_3	("throttle_3"),
	THROTTLE_4	("throttle_4"),
	PROPELLER_1	("propeller_1"),
	PROPELLER_2	("propeller_2"),
	PROPELLER_3	("propeller_3"),
	PROPELLER_4	("propeller_4"),
	MIXTURE_1	("mixture_1"),
	MIXTURE_2	("mixture_2"),
	MIXTURE_3	("mixture_3"),
	MIXTURE_4	("mixture_4"),
	GEAR		("gear"),
	FLAPS		("flaps");
	
	private final String simOut;
	
	private SimOuts(String simOut) {this.simOut = simOut;}
	
	public String toString() {return simOut;}
}
