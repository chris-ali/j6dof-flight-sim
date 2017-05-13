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
package com.chrisali.javaflightsim;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chrisali.javaflightsim.initializer.Initializer;

/**
 * Runner class to start Java Flight Simulator
 * 
 * @author Christopher Ali
 *
 */
public class RunJavaFlightSimulator {
	private static final Logger logger = LogManager.getLogger(RunJavaFlightSimulator.class);
	
	public static void main(String[] args) {
		logger.debug("===============================================");
		logger.debug("Java Flight Simulator v0.4a by Christopher Ali");
		logger.debug("===============================================");
		
		Initializer.selectRunConfigurationAndRun();
		
		logger.debug("Closing Java Flight Simulator");
	}
}
