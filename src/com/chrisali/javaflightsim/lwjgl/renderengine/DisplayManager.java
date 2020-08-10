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
package com.chrisali.javaflightsim.lwjgl.renderengine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * Handles the creation, updating and closing of the LWJGL OTW display window 
 * 
 * @author Christopher Ali
 *
 */
public class DisplayManager {
	
	private static final Logger logger = LogManager.getLogger(DisplayManager.class);
	
	private static int frameRateLimit = 60;
	
	private static int height = 900;
	private static int width = 1440;
	
	private static int aaSamples = 0;
	private static int colorDepth = 24;
	private static boolean useFullScreen = false;

	private static long lastFrameTime;
	private static float delta;

	/**
     * The handle of the window.
     */
	private static long window;
	
	/**
     * A reference to the error callback so it doesn't get GCd.
     */
    private static GLFWErrorCallback errorCallback;
	
	/**
	 * Creates the OpenGL display in its own window
	 */
	public static void createDisplay() {
		logger.debug("Initializing GLFW display...");

		//Initialize GLFW.
		glfwInit();
		
        //Setup an error callback to print GLFW errors to the console.
		glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));

		//Request an OpenGL 3.3 Core context.
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE); 
		glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);
		glfwWindowHint(GLFW_DEPTH_BITS, colorDepth);
		glfwWindowHint(GLFW_SAMPLES, aaSamples);

		//TODO Add fullscreen support
        long monitor = 0;
        if(useFullScreen) {
            //Get the primary monitor.
			monitor = glfwGetPrimaryMonitor();
			
            //Retrieve the desktop resolution
            GLFWVidMode vidMode = glfwGetVideoMode(monitor);
            width = vidMode.width();
            height = vidMode.height();
        }
				
		window = glfwCreateWindow(width, height, "Java Flight Simulator", monitor, NULL);
		
		if (window == 0)
			throw new RuntimeException("Failed to create GLFW display!");

		glfwMakeContextCurrent(window);
		GL.createCapabilities();
		glViewport(0, 0, width, height);
		lastFrameTime = getCurrentTime();

		glfwShowWindow(window);

		logger.debug("...done!");
	}
	
	/**
	 * Updates the display by rendering one frame based on the frame rate defined in {@link DisplayManager}
	 */
	public static void updateDisplay() {
		//Display.sync(frameRateLimit);
		
		glfwPollEvents();
		glfwSwapBuffers(window);

		long currentFrameTime = getCurrentTime();
		delta = (currentFrameTime - lastFrameTime)/1000f;	
		lastFrameTime = currentFrameTime;
	}
	
	public static float getFrameTimeSeconds() {
		return delta;
	}
	
	public static void closeDisplay() {
		glfwDestroyWindow(window);
	}
	
	private static long getCurrentTime() {
		return (long)glfwGetTime() * 1000;
	}
	
	public static int getHeight() {
		return height;
	}

	public static void setHeight(int height) {
		DisplayManager.height = height;
	}

	public static int getWidth() {
		return width;
	}

	public static void setWidth(int width) {
		DisplayManager.width = width;
	}

	public static void setAaSamples(int aaSamples) {
		DisplayManager.aaSamples = aaSamples;
	}

	public static void setColorDepth(int colorDepth) {
		DisplayManager.colorDepth = colorDepth;
	}

	public static int getFrameRateLimit() {
		return frameRateLimit;
	}

	public static void setFrameRateLimit(int frameRateLimit) {
		DisplayManager.frameRateLimit = frameRateLimit;
	}
	
	public static float getAspectRatio() {
		return ((float)width)/((float)height);
	}

	public static boolean isUseFullScreen() {
		return useFullScreen;
	}

	public static void setUseFullScreen(boolean useFullScreen) {
		DisplayManager.useFullScreen = useFullScreen;
	}

	public static long getWindow() {
		return window;
	}

	public static void setWindow(long window) {
		DisplayManager.window = window;
	}
}
