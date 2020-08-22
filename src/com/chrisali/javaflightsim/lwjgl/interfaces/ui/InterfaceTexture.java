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
package com.chrisali.javaflightsim.lwjgl.interfaces.ui;

import org.lwjgl.util.vector.Vector2f;

/**
 * Texture that is rendered directly on a quad flush with the display to show GUI information
 * 
 * @author Christopher
 *
 */
public class InterfaceTexture {
	
	private int texture;
	private Vector2f position;
	private Vector2f scale;
	private float rotation;
	
	public InterfaceTexture() {}
	
	public InterfaceTexture(int texture, Vector2f position, float rotation, Vector2f scale) {
		this.texture = texture;
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
	}

	public int getTexture() { return texture; }

	public void setTexture(int texture) { this.texture = texture; }
	
	public Vector2f getPosition() { return position; }
	
	public void setPosition(Vector2f position) { this.position = position; }
	
	public float getRotation() { return rotation; }

	public void setRotation(float rotation) { this.rotation = rotation;	}

	public void setScale(Vector2f scale) { this.scale = scale; }

	public Vector2f getScale() { return scale; }
}
