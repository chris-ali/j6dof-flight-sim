/*******************************************************************************
 * Copyright (C) 2016-2020 Christopher Ali
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

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Map;

import com.chrisali.javaflightsim.lwjgl.renderengine.DisplayManager;
import com.chrisali.javaflightsim.simulation.datatransfer.FlightData;
import com.chrisali.javaflightsim.simulation.datatransfer.InputData;
import com.chrisali.javaflightsim.simulation.setup.ControlsConfiguration;
import com.chrisali.javaflightsim.simulation.setup.JoystickAxis;
import com.chrisali.javaflightsim.simulation.setup.KeyCommand;
import com.chrisali.javaflightsim.simulation.setup.ControlsConfiguration.JoystickAssignments;
import com.chrisali.javaflightsim.simulation.utilities.FileUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Handles polling and reading input values from controllers and keyboards into collections that can easily 
 * be used with the simulation
 * 
 * All inputs' axis, button and hat IDs used here refer to the GLFW input specification 
 * 
 * @author Christopher
 * @see https://www.glfw.org/docs/latest/group__input.html
 */
public class InputMaster {
    private static InputData inputData;
    private static ControlsConfiguration controlsConfig;
    
    private static final Logger logger = LogManager.getLogger(InputMaster.class);
    
    /**
     * Reads controls configuration and initializes all event callbacks for mouse and keyboard
     */
    public static void init() {
        inputData = new InputData();
        controlsConfig = FileUtilities.readControlsConfiguration();

        logger.debug("Setting up GLFW Mouse and Keyboard callbacks...");

        glfwSetKeyCallback(DisplayManager.getWindow(), (window, key, scancode, action, mods) -> {
            inputData.clearKeysPressed();

            // Keyboard keys are integers that change depending on the engine implementation for maximum confusion
            for (Map.Entry<Integer, KeyCommand> entry : controlsConfig.getKeyboardAssignments().entrySet()) {
                if (key == entry.getKey() && (action == GLFW_PRESS || action == GLFW_REPEAT))
                    inputData.addKeyPressed(entry.getValue());
            }
        });

        glfwSetScrollCallback(DisplayManager.getWindow(), (window, xoffset, yoffset) -> {
            inputData.setMouseScrollOffset(yoffset);
        });

        glfwSetMouseButtonCallback(DisplayManager.getWindow(), (windowHnd, button, action, mods) -> {
            inputData.addMouseButtonPressed(button);
            
            if (action == GLFW_RELEASE)
                inputData.clearMouseButtonsPressed();
        });

        glfwSetCursorPosCallback(DisplayManager.getWindow(), (windowHnd, xpos, ypos) -> {
            inputData.setMouseXPos(xpos);
            inputData.setMouseYPos(ypos);
        });

        logger.debug("Connected joysticks:");

        // Loop through all conntected controllers
        for (int GLFW_JOYSTICK = 0; GLFW_JOYSTICK < 15; GLFW_JOYSTICK++) {
            String controllerName = glfwGetJoystickName(GLFW_JOYSTICK);
            
            if (controllerName == null)
                break;
            
            logger.debug("Index " + GLFW_JOYSTICK + ": " + controllerName);
        }
    }

    /**
     * Polls controllers for new data, and then translates these values into commands and axis 
     * deflections for {@link FlightData}
     */
    public static void update() {
        updateJoysticks();
    }

    private static void updateJoysticks() {
        Map<String, JoystickAssignments> allJoystickAssignments = controlsConfig.getJoystickAssignments();

        // Loop through all conntected controllers
        for (int GLFW_JOYSTICK = 0; GLFW_JOYSTICK < 15; GLFW_JOYSTICK++) {
            String controllerName = glfwGetJoystickName(GLFW_JOYSTICK);
            
            // Anything past the first instance of a null will not have any joystick data 
            if (controllerName == null)
                break;
            
            FloatBuffer glfwAxes = glfwGetJoystickAxes(GLFW_JOYSTICK);
            ByteBuffer glfwButtons = glfwGetJoystickButtons(GLFW_JOYSTICK);
            ByteBuffer glfwHats = glfwGetJoystickHats(GLFW_JOYSTICK);

            // Get assignments for a single joystick
            JoystickAssignments joystickAssignments = allJoystickAssignments.get(controllerName);
            
            if (joystickAssignments == null)
                continue;

            // Loop through joystick axes on connected controller
            Map<Integer, JoystickAxis> axisAssignments = joystickAssignments.getAxisAssignments();
            for (int axisIndex = 0; axisIndex < glfwAxes.capacity(); axisIndex++) {
                JoystickAxis axis = axisAssignments.get(axisIndex);

                // If controls configuration has an assignment for this axis, use it
                if (axis != null)
                    inputData.updateJoystickInputs(axis.getAxisAssignment(), glfwAxes.get(axisIndex));
            }

            // Loop through buttons on connected controller
            Map<Integer, KeyCommand> buttonAssignments = joystickAssignments.getButtonAssignments();
            for (int buttonIndex = 0; buttonIndex < glfwButtons.capacity(); buttonIndex++) {
                
                // If controls configuration has an assignment for this pressed button, use it
                if (glfwButtons.get(buttonIndex) == GLFW_TRUE) {
                    KeyCommand command = buttonAssignments.get(buttonIndex);

                    if (command != null)
                        inputData.addKeyPressed(command);
                }
            }

            // Determine hat direction on connected controller, if supported
            //TODO figure out hat assignments
            Map<Integer, KeyCommand> hatAssignments = joystickAssignments.getHatAssignments();
            if (hatAssignments != null) {
                KeyCommand command = hatAssignments.get((int)glfwHats.get(0));

                if (command != null)
                    inputData.addKeyPressed(command);
            }
        }
    }

	public static InputData getInputData() { 
        return inputData; 
    }
}