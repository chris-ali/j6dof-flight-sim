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

import java.util.TreeMap;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import com.chrisali.javaflightsim.lwjgl.models.TexturedModel;
import com.chrisali.javaflightsim.lwjgl.renderengine.DisplayManager;
import com.chrisali.javaflightsim.lwjgl.terrain.Terrain;

/**
 * An {@link Entity} that the user can move around the world with the keyboard
 * 
 * @author Christopher Ali
 *
 */
public class Player extends Entity {
	
	private static final float RUN_SPEED = 20;
	private static final float TURN_SPEED = 160;
	private static final float GRAVITY = -50;
	private static final float JUMP_POWER = 20;
	
	private float currentSpeed = 0;
	private float currentVerticalSpeed = 0;
	private float currentTurnSpeed = 0;
	
	private boolean isAirborne = false;

	public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
	}
	
	/**
	 * Simple physics to move the player around the world while being tied to the ground
	 * 
	 * @param terrainMap
	 */
	public void move(TreeMap<String, Terrain> terrainMap) {
		checkInputs();
		
		super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		
		float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
		float dx = distance * (float)Math.sin(Math.toRadians(super.getRotY()));
		float dz = distance * (float)Math.cos(Math.toRadians(super.getRotY()));
		
		super.increasePosition(dx, 0, dz);
		currentVerticalSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
		super.increasePosition(0, currentVerticalSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		
		Terrain terrain = Terrain.getCurrentTerrain(terrainMap, super.getPosition().x, super.getPosition().z);
		float terrainHeight = terrain.getTerrainHeight(super.getPosition().x, super.getPosition().z);
		
		if (super.getPosition().y < terrainHeight) {
			currentVerticalSpeed = 0;
			isAirborne = false;
			super.getPosition().y = terrainHeight;
		}
		
	}
	
	/**
	 * Scans for key presses and sets the run/turn/jump speed as appropriate
	 */
	private void checkInputs() {
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
			this.currentSpeed = RUN_SPEED;
		} else if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
			this.currentSpeed = -RUN_SPEED;
		} else {
			this.currentSpeed = 0;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
			this.currentTurnSpeed = TURN_SPEED;
		} else if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
			this.currentTurnSpeed = -TURN_SPEED;
		} else {
			this.currentTurnSpeed = 0;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
			if(!isAirborne) {
				this.currentVerticalSpeed = JUMP_POWER;
				isAirborne = true;
			}
		}
	}
	
}
