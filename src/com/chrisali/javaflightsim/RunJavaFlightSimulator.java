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

import javax.swing.SwingUtilities;

import com.chrisali.javaflightsim.controllers.LWJGLSimulationController;
import com.chrisali.javaflightsim.menus.GuiFrame;

/**
 * Runner class to start Java Flight Simulator
 * 
 * @author Christopher Ali
 *
 */
public class RunJavaFlightSimulator {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {runApp();}
		});
	}
	
	/**
	 * Initializes {@link LWJGLSimulationController} and {@link GuiFrame}; due to cross-referencing
	 * needed with both objects, {@link LWJGLSimulationController#setGuiFrame(GuiFrame)} needs to be
	 * called
	 */
	private static void runApp() {
		LWJGLSimulationController controller = new LWJGLSimulationController(); 
		GuiFrame guiFrame = new GuiFrame(controller);
		
		// Pass in mainFrame reference so that OTW display can get Canvas
		// reference
		controller.setGuiFrame(guiFrame);
	}
}
