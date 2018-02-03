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
package com.chrisali.javaflightsim.lwjgl.entities;

import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import com.chrisali.javaflightsim.lwjgl.models.TexturedModel;
import com.chrisali.javaflightsim.simulation.datatransfer.FlightDataType;
import com.chrisali.javaflightsim.simulation.setup.InitialConditions;

/**
 * An {@link Entity} with no physics attached to it that relies on an outside source to set its position/angles
 * 
 * @author Christopher Ali
 *
 */
public class Ownship extends Entity {
	
	/**
	 * Constructor that takes a Map of inital conditions to set initial position and rotation  
	 * 
	 * @param model
	 * @param initialConditions
	 * @param scale
	 */
	public Ownship(TexturedModel model, Map<InitialConditions, Double> initialConditions, float scale) {
		super(model, new Vector3f(0,0,0), 0, 0, 0, scale);
		
		//(800, 150, 800)
		super.setPosition(new Vector3f(
			(float)initialConditions.get(InitialConditions.INITN).doubleValue() / 15,
		    (float)initialConditions.get(InitialConditions.INITD).doubleValue() / 15, 
		    (float)initialConditions.get(InitialConditions.INITE).doubleValue() / 15)
		); 
		
		// (0, 0, 135)
		super.setRotX(-(float)Math.toDegrees(initialConditions.get(InitialConditions.INITPHI)));
		super.setRotY(-(float)Math.toDegrees(initialConditions.get(InitialConditions.INITTHETA))); 
		super.setRotZ((float)Math.toDegrees(initialConditions.get(InitialConditions.INITPSI)) + 90); 
	}

	public Ownship(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
	}
	
	public void move(Vector3f position, float phi, float theta, float psi) {
		super.setPosition(position);
		
		super.setRotX(phi);
		super.setRotZ(theta);
		super.setRotY(psi);
	}
	
	/**
	 * Translates and rotates ownship using FlightData provided by simulation  
	 * 
	 * @param flightData
	 */
	public void move(Map<FlightDataType, Double> flightData) {
		// Scale distances from simulation to OTW
		setPosition(new Vector3f(
			(float) (flightData.get(FlightDataType.NORTH)    / 15),
			(float) (flightData.get(FlightDataType.ALTITUDE) / 15),
			(float) (flightData.get(FlightDataType.EAST)     / 15)
		));
		
		// Convert right-handed coordinates from simulation to left-handed coordinates of OTW
		setRotX((float) -(flightData.get(FlightDataType.ROLL)));
		setRotY((float) -(flightData.get(FlightDataType.PITCH)));
		setRotZ((float)  (flightData.get(FlightDataType.HEADING) + 90));
	}
}
