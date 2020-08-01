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
import com.chrisali.javaflightsim.simulation.setup.JoystickAxis;
import com.chrisali.javaflightsim.simulation.setup.KeyCommand;
import com.chrisali.javaflightsim.simulation.setup.ControlsConfiguration.JoystickAssignments;
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
        for (Map.Entry<String, KeyCommand> entry : controlsConfig.getKeyboardAssignments().entrySet()) {
            if (Keyboard.isKeyDown(key))
                inputData.addKeyPressed(entry.getValue());
        }

        Controllers.poll();

        Map<String, JoystickAssignments> allJoystickAssignments = controlsConfig.getJoystickAssignments();

        // Loop through all conntected controllers
        for (int controllerIndex = 0; controllerIndex < controllerCount; controllerIndex++) {
            Controller controller = Controllers.getController(controllerIndex);

            // Get assignments for a single joystick
            JoystickAssignments joystickAssignments = allJoystickAssignments.get(controller.getName());
            
            // Loop through joystick axes on connected controller
            for (int axisIndex; axisIndex < controller.getAxisCount(); axisIndex++) {
                JoystickAxis axis = joystickAssignments.getAxisAssignments().get(controller.getAxisName(axisIndex));

                // If controls configuration has an assignment for this axis, use it
                if (axis != null)
                    inputData.updateJoystickInputs(axis.getAxisAssignment(), controller.getAxisValue(axisIndex));
            }

            // Loop through buttons on connected controller
            for (int buttonIndex= 0; buttonIndex< controller.getButtonCount(); buttonIndex++) {
                Map<String, KeyCommand> buttonAssignments = joystickAssignments.getButtonAssignments();
                
                // If controls configuration has an assignment for this pressed button, use it
                if (controller.isButtonPressed(buttonIndex)) {
                    KeyCommand command = buttonAssignments.get(controller.getButtonName(buttonIndex));

                    if (command != null)
                        inputData.addKeyPressed(command);
                }
            }

            // May need to reassign hat to use POV X and Y in controls config
            Map<Float, KeyCommand> hatAssignments = joystickAssignments.getHatAssignments();
            if (hatAssignments != null) {
                float hatVal = controller.getPovX();
                KeyCommand command = hatAssignments.get(hatVal);

                if (hatVal > 0  && command != null)
                    inputData.addKeyPressed(command);
            }
        }
    }

	public static InputData getInputData() { 
        return inputData; 
    }
}