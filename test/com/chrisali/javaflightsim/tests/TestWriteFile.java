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
package com.chrisali.javaflightsim.tests;

import java.io.File;
import java.util.Map;

import com.chrisali.javaflightsim.simulation.setup.IntegrationSetup;
import com.chrisali.javaflightsim.simulation.setup.IntegratorConfig;
import com.chrisali.javaflightsim.simulation.utilities.FileUtilities;

public class TestWriteFile {
	
	private static final String FILE_PATH = ".\\SimConfig\\";
	private static final String OLD_CONFIG_FILE_NAME = "IntegratorConfig";
	private static final String NEW_CONFIG_FILE_NAME = "NewIntegratorConfig";
	
	public static void main(String[] args) {
		Map<IntegratorConfig, Double> simConfig = IntegrationSetup.gatherIntegratorConfig(OLD_CONFIG_FILE_NAME);
		
		FileUtilities.writeConfigFile(NEW_CONFIG_FILE_NAME, FILE_PATH, simConfig);
		
		Map<IntegratorConfig, Double> newSimConfig = IntegrationSetup.gatherIntegratorConfig(NEW_CONFIG_FILE_NAME);
		
		if (simConfig.equals(newSimConfig))
			System.out.println("Parsed Maps are identical");
		else
			System.err.println("Parsed Maps are different!");
		
		new File(FILE_PATH + NEW_CONFIG_FILE_NAME + ".txt").deleteOnExit();
	}
}
