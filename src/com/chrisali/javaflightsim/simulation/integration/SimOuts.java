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
	TIME 		("Time [sec]"),
	U 			("u [ft/sec]"),
	U_DOT 		("u_dot [ft/sec^2]"),
	V 			("v [ft/sec]"),
	V_DOT 		("v_dot [ft/sec^2]"),
	W			("w [ft/sec]"),
	W_DOT		("w_dot [ft/sec^2]"),
	NORTH		("N [ft]"),
	NORTH_DOT	("N_dot [ft/sec]"),
	EAST		("E [ft]"),
	EAST_DOT	("E_dot [ft/sec]"),
	ALT			("Alt [ft]"),
	ALT_DOT		("Alt_dot [ft/sec]"),
	PHI			("Phi [rad]"),
	PHI_DOT		("Phi_dot [rad/sec]"),
	THETA		("theta [rad]"),
	THETA_DOT	("theta_dot [rad/sec]"),
	PSI			("psi [rad]"),
	PSI_DOT		("psi_dot [rad/sec]"),
	P			("p [rad/sec]"),
	P_DOT		("p_dot [rad/sec^2]"),
	Q			("q [rad/sec]"),
	Q_DOT		("q_dot [rad/sec^2]"),
	R			("r [rad/sec]"),
	R_DOT		("r_dot [rad/sec^2]"),
	TAS			("TAS [ft/sec]"),
	BETA		("Beta [rad]"),
	ALPHA		("Alpha [rad]"),
	ALPHA_DOT	("Alpha_dot [rad/sec]"),
	MACH		("Mach"),
	LAT			("Lat [rad]"),
	LAT_DOT		("Lat_dot [rad/sec]"),
	LON			("Lon [rad]"),
	LON_DOT		("Lon_dot [rad/sec]"),
	A_X			("A_x [ft/sec^2]"),
	AN_X		("An_x [g]"),
	A_Y			("A_y [ft/sec^2]"),
	AN_Y		("An_y [g]"),
	A_Z			("A_z [ft/sec^2]"),
	AN_Z		("An_z [g]"),
	L			("L [ft*lbf/sec^2]"),
	M			("M [ft*lbf/sec^2]"),
	N			("N [ft*lbf/sec^2]"),
	THRUST_1	("Thrust 1 [lbf]"),
	RPM_1		("RPM 1"),
	FUEL_FLOW_1	("Fuel Flow 1"),
	THRUST_2	("Thrust 2 [lbf]"),
	RPM_2		("RPM 2"),
	FUEL_FLOW_2	("Fuel Flow 2"),
	THRUST_3	("Thrust 3 [lbf]"),
	RPM_3		("RPM 3"),
	FUEL_FLOW_3	("Fuel Flow 3"),
	THRUST_4	("Thrust 4 [lbf]"),
	RPM_4		("RPM 4"),
	FUEL_FLOW_4	("Fuel Flow 4"),
	ELEVATOR	("Elevator [rad]"),
	AILERON		("Aileron [rad]"),
	RUDDER		("Rudder [rad"),
	THROTTLE_1	("Throttle 1"),
	THROTTLE_2	("Throttle 2"),
	THROTTLE_3	("Throttle 3"),
	THROTTLE_4	("Throttle 4"),
	PROPELLER_1	("Propeller 1"),
	PROPELLER_2	("Propeller 2"),
	PROPELLER_3	("Propeller 3"),
	PROPELLER_4	("Propeller 4"),
	MIXTURE_1	("Mixture 1"),
	MIXTURE_2	("Mixture 2"),
	MIXTURE_3	("Mixture 3"),
	MIXTURE_4	("Mixture 4"),
	GEAR		("Gear"),
	FLAPS		("Flaps [rad]");
	
	private final String simOut;
	
	private SimOuts(String simOut) {this.simOut = simOut;}
	
	public String toString() {return simOut;}
}
