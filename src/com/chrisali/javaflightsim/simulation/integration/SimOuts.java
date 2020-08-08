/*******************************************************************************
 * Copyright (C) 2016-2018 Christopher Ali
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

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * This Enum is used with {@link Integrate6DOFEquations} to define the simOut
 * EnumMap. The String and DecimalFormat fields are used in the console output to 
 * show the name of each value, formatted for clarity.
 */
public enum SimOuts {
	TIME 		(0,  "Time [sec]", 			new DecimalFormat("#.##")),
	U 			(1,  "u [ft/sec]", 			new DecimalFormat("#.####")),
	U_DOT 		(2,  "u_dot [ft/sec^2]", 	new DecimalFormat("#.####")),
	V 			(3,  "v [ft/sec]", 			new DecimalFormat("#.####")),
	V_DOT 		(4,  "v_dot [ft/sec^2]", 	new DecimalFormat("#.####")),
	W			(5,  "w [ft/sec]", 			new DecimalFormat("#.####")),
	W_DOT		(6,  "w_dot [ft/sec^2]", 	new DecimalFormat("#.####")),
	NORTH		(7,  "N [ft]", 				new DecimalFormat("#.#")),
	NORTH_DOT	(8,  "N_dot [ft/sec]", 		new DecimalFormat("#.####")),
	EAST		(9,  "E [ft]", 				new DecimalFormat("#.#")),
	EAST_DOT	(10, "E_dot [ft/sec]", 		new DecimalFormat("#.####")),
	ALT			(11, "Alt [ft]", 			new DecimalFormat("#.#")),
	ALT_DOT		(12, "Alt_dot [ft/sec]", 	new DecimalFormat("#.####")),
	PHI			(13, "Phi [rad]", 			new DecimalFormat("#.####")),
	PHI_DOT		(14, "Phi_dot [rad/sec]", 	new DecimalFormat("#.####")),
	THETA		(15, "theta [rad]", 		new DecimalFormat("#.####")),
	THETA_DOT	(16, "theta_dot [rad/sec]", new DecimalFormat("#.####")),
	PSI			(17, "psi [rad]", 			new DecimalFormat("#.####")),
	PSI_DOT		(18, "psi_dot [rad/sec]", 	new DecimalFormat("#.####")),
	P			(19, "p [rad/sec]", 		new DecimalFormat("#.####")),
	P_DOT		(20, "p_dot [rad/sec^2]", 	new DecimalFormat("#.####")),
	Q			(21, "q [rad/sec]", 		new DecimalFormat("#.####")),
	Q_DOT		(22, "q_dot [rad/sec^2]", 	new DecimalFormat("#.####")),
	R			(23, "r [rad/sec]", 		new DecimalFormat("#.####")),
	R_DOT		(24, "r_dot [rad/sec^2]", 	new DecimalFormat("#.####")),
	TAS			(25, "TAS [ft/sec]", 		new DecimalFormat("#.##")),
	BETA		(26, "Beta [rad]", 			new DecimalFormat("#.####")),
	ALPHA		(27, "Alpha [rad]", 		new DecimalFormat("#.####")),
	ALPHA_DOT	(28, "Alpha_dot [rad/sec]", new DecimalFormat("#.####")),
	MACH		(29, "Mach", 				new DecimalFormat("#.####")),
	LAT			(30, "Lat [rad]", 			new DecimalFormat("#.####")),
	LAT_DOT		(31, "Lat_dot [rad/sec]", 	new DecimalFormat("#.######")),
	LON			(32, "Lon [rad]", 			new DecimalFormat("#.####")),
	LON_DOT		(33, "Lon_dot [rad/sec]", 	new DecimalFormat("#.######")),
	A_X			(34, "A_x [ft/sec^2]", 		new DecimalFormat("#.####")),
	AN_X		(35, "An_x [g]", 			new DecimalFormat("#.####")),
	A_Y			(36, "A_y [ft/sec^2]", 		new DecimalFormat("#.####")),
	AN_Y		(37, "An_y [g]", 			new DecimalFormat("#.####")),
	A_Z			(38, "A_z [ft/sec^2]", 		new DecimalFormat("#.####")),
	AN_Z		(39, "An_z [g]", 			new DecimalFormat("#.####")),
	L			(40, "L [ft*lbf/sec^2]", 	new DecimalFormat("#.####")),
	M			(41, "M [ft*lbf/sec^2]", 	new DecimalFormat("#.####")),
	N			(42, "N [ft*lbf/sec^2]", 	new DecimalFormat("#.####")),
	THRUST_1	(43, "Thrust 1 [lbf]", 		new DecimalFormat("#.##")),
	RPM_1		(44, "RPM 1", 				new DecimalFormat("#.##")),
	FUEL_FLOW_1	(45, "Fuel Flow 1", 		new DecimalFormat("#.##")),
	THRUST_2	(46, "Thrust 2 [lbf]", 		new DecimalFormat("#.##")),
	RPM_2		(47, "RPM 2", 				new DecimalFormat("#.##")),
	FUEL_FLOW_2	(48, "Fuel Flow 2", 		new DecimalFormat("#.##")),
	THRUST_3	(49, "Thrust 3 [lbf]", 		new DecimalFormat("#.##")),
	RPM_3		(50, "RPM 3", 				new DecimalFormat("#.##")),
	FUEL_FLOW_3	(51, "Fuel Flow 3", 		new DecimalFormat("#.##")),
	THRUST_4	(52, "Thrust 4 [lbf]", 		new DecimalFormat("#.##")),
	RPM_4		(53, "RPM 4", 				new DecimalFormat("#.##")),
	FUEL_FLOW_4	(54, "Fuel Flow 4", 		new DecimalFormat("#.##")),
	ELEVATOR	(55, "Elevator [rad]", 		new DecimalFormat("#.##")),
	AILERON		(56, "Aileron [rad]", 		new DecimalFormat("#.##")),
	RUDDER		(57, "Rudder [rad", 		new DecimalFormat("#.##")),
	THROTTLE_1	(58, "Throttle 1", 			new DecimalFormat("#.#")),
	THROTTLE_2	(59, "Throttle 2", 			new DecimalFormat("#.#")),
	THROTTLE_3	(60, "Throttle 3", 			new DecimalFormat("#.#")),
	THROTTLE_4	(61, "Throttle 4", 			new DecimalFormat("#.#")),
	PROPELLER_1	(62, "Propeller 1", 		new DecimalFormat("#.#")),
	PROPELLER_2	(63, "Propeller 2", 		new DecimalFormat("#.#")),
	PROPELLER_3	(64, "Propeller 3", 		new DecimalFormat("#.#")),
	PROPELLER_4	(65, "Propeller 4", 		new DecimalFormat("#.#")),
	MIXTURE_1	(66, "Mixture 1", 			new DecimalFormat("#.#")),
	MIXTURE_2	(67, "Mixture 2", 			new DecimalFormat("#.#")),
	MIXTURE_3	(68, "Mixture 3", 			new DecimalFormat("#.#")),
	MIXTURE_4	(69, "Mixture 4", 			new DecimalFormat("#.#")),
	GEAR		(70, "Gear", 				new DecimalFormat("#.#")),
	FLAPS		(71, "Flaps [rad]", 		new DecimalFormat("#.#"));
	
	private final int index;

	private final String name;

	private final DecimalFormat format;
	
	private SimOuts(int index, String simOut, DecimalFormat format) {
		this.index  = index;
		this.name   = simOut;
		this.format = format;
	}
	
	/**
	 * @return Friendly name of this enum
	 */
	@Override
	public String toString() {return name;}

	/**
	 * @return Decimal place formatting for this enum
	 */
	public DecimalFormat getFormat() {return format;}

	/**
	 * @return Column index of this enum
	 */
	public int getIndex() {return index;}

	private static Map<Integer, SimOuts> simOutMap = new HashMap<Integer, SimOuts>();

	// Statically initialize this map for performant access with getByIndex()
	static {
		for (SimOuts simOut : SimOuts.values()) {
			simOutMap.put(simOut.index, simOut);
		}
	}

	/**
	 * Finds a SimOuts enum given a specified index
	 * @param index
	 * @return SimOuts enum or null if value not found in enum
	 */
	public static SimOuts getByIndex(int index) {
		return simOutMap.get(index);
	}
}
