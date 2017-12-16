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
package com.chrisali.javaflightsim.lwjgl.renderengine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

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
	
	private static long lastFrameTime;
	private static float delta;
	
	/**
	 * Creates the OpenGL display in its own window
	 */
	public static void createDisplay() {
		try {
			ContextAttribs attribs = new ContextAttribs(3,3)
										.withForwardCompatible(true)
										.withProfileCore(true);
			Display.setDisplayMode(new DisplayMode(width, height));
			Display.setTitle("Java Flight Simulator");
			Display.create(new PixelFormat().withSamples(aaSamples).withDepthBits(colorDepth),attribs);
		} catch (LWJGLException e) {
			logger.error("An error was encountered while creating the LWJGL display!", e);
		}
		
		GL11.glViewport(0, 0, width, height);
		lastFrameTime = getCurrentTime();
	}
	
	/**
	 * Updates the display by rendering one frame based on the frame rate defined in {@link DisplayManager}
	 */
	public static void updateDisplay() {
		Display.sync(frameRateLimit);
		Display.update();
		long currentFrameTime = getCurrentTime();
		delta = (currentFrameTime - lastFrameTime)/1000f;	
		lastFrameTime = currentFrameTime;
	}
	
	public static float getFrameTimeSeconds() {
		return delta;
	}
	
	public static void closeDisplay() {
		Display.destroy();
	}
	
	private static long getCurrentTime() {
		return Sys.getTime()*1000/Sys.getTimerResolution();
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
}
