package com.chrisali.javaflightsim.otw.entities;

import java.util.TreeMap;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import com.chrisali.javaflightsim.otw.models.TexturedModel;
import com.chrisali.javaflightsim.otw.renderengine.DisplayManager;
import com.chrisali.javaflightsim.otw.terrain.Terrain;

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
