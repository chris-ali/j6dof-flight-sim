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
package com.chrisali.javaflightsim.lwjgl.entities;

import org.lwjgl.util.vector.Vector3f;

import com.chrisali.javaflightsim.lwjgl.models.TexturedModel;

/**
 * An {@link Entity} with no physics attached to it that relies on an outside source to set its position/angles
 * 
 * @author Christopher Ali
 *
 */
public class Ownship extends Entity {

	public Ownship(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
	}
	
	public void move(Vector3f position, float phi, float theta, float psi) {
		super.setPosition(position);
		
		super.setRotX(phi);
		super.setRotZ(theta);
		super.setRotY(psi);
	}
	
}
