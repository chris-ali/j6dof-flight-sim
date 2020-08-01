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
package com.chrisali.javaflightsim.lwjgl.input;

import java.util.Map;
import java.util.Set;

import com.chrisali.javaflightsim.simulation.datatransfer.InputData;
import com.chrisali.javaflightsim.simulation.flightcontrols.FlightControl;
import com.chrisali.javaflightsim.simulation.setup.ControlsConfiguration;
import com.chrisali.javaflightsim.simulation.setup.KeyCommand;
import com.chrisali.javaflightsim.simulation.utilities.FileUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;

public class InputMaster {
    private static InputData inputData;
    private static ControlsConfiguration controlsConfig;
    private static int controllerCount;

    private static final Logger logger = LogManager.getLogger(InputMaster.class);
        
    public static void init() {
        inputData = new InputData();
        controlsConfig = FileUtilities.readControlsConfiguration();

        try {
            Controllers.create();
            controllerCount = Controllers.getControllerCount();

            logger.debug("Found " + controllerCount + " joystick controllers:");
            for (int i = 0; i < controllerCount; i++) {
                logger.debug(Controllers.getController(i).getName());
            }
        } catch (LWJGLException e) {
            logger.error("Encountered an error when loading joystick controllers!", e);
        }
    }

    public static void cleanUp() {
        Controllers.destroy();
    }

    public static void update() {
        //TODO Revise (de)serialization of controls to use int values for keys/axes that are "legible" for LWJGL Keyboard and Controller classes
        /*
        for (Map.Entry<String, KeyCommand> entry : controlsConfig.getKeyboardAssignments().entrySet()) {
            if (Keyboard.isKeyDown(key))
                inputData.addKeyPressed(entry.getValue());
        }

        Controllers.poll();

        for (int i = 0; i < controllerCount; i++) {

        }
        */
    }

	public static InputData getInputData() { 
        return inputData; 
    }
}