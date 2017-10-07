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
package com.chrisali.javaflightsim.simulation.interfaces;

import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;

/**
 * To replace threading, this interface allows objects run at various rates slower than {@link Integrate6DOFEquations} 
 * master rate by step updating in a master loop only when the current time meets a certain window 
 * 
 * @author Christopher
 *
 */
public interface Steppable {

	/**
	 * Updates all items needed by the object during one step; all code within the loop of the run() method should go here
	 */
	public void step();
	
	/**
	 * To emulate running synchronously at a different rate, define a modulus value here so that this object only updates every x 
	 * times the simulation steps
	 * 
	 * @param simTimeMS simulation time in milliseconds
	 * @return if the implementing object can step update at this point in time
	 */
	public boolean canStepNow(int simTimeMS);
}
