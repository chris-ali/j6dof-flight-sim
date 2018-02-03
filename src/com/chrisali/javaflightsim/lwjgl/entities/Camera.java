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

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

/**
 * Camera for the game engine, which needs an {@link Entity} as a reference to follow.
 * A chase view is available for panning around in 3rd person
 * 
 * @author Christopher Ali
 *
 */
public class Camera {
	private Vector3f position = new Vector3f(0,1,0);
	
	// Camera's own angles
	private float phi;
	private float theta;
	private float psi;
	
	private float cameraPanSpeed = 0.1f;
	private float mouseSensitivity = 0.1f;
	
	// Camera in relation to player/ownship
	private float cameraDistanceToEntity;
	private float cameraToEntityPhi = 0.0f;
	private float cameraToEntityTheta = 0.0f;
	private float cameraToEntityPsi = 0.0f;
	
	/**
	 * If an instrument panel is displayed on screen, this pitches the camera view down to compensate
	 */
	private float pitchOffset = 0.0f;
	
	private boolean isChaseView = false;
	private Vector3f pilotPosition;
	
	private Entity entityToFollow;
	
	/**
	 * Constructor that takes an {@link Entity} to follow as it moves about the world;
	 * if the isChaseView boolean is set the distance and angle of the camera are zoomed out and pitched down
	 * 
	 * @param entityToFollow
	 */
	public Camera(Entity entityToFollow) {
		this.entityToFollow = entityToFollow;
		pilotPosition = new Vector3f(0, 0, 0);
		
		phi   = entityToFollow.getRotX();
		theta = entityToFollow.getRotY();
		psi   = entityToFollow.getRotZ();
		
		cameraToEntityTheta = 20f;
		cameraDistanceToEntity = 25.0f;
	}
	
	/**
	 * Sets the position of the camera based on trigometric calculations performed in the XZ plane of
	 * the entity
	 */
	private void calculateCameraPosition() {
		float horizontalDistance = cameraDistanceToEntity * (float) Math.cos(Math.toRadians(cameraToEntityTheta)), 
		      verticalDistance   = cameraDistanceToEntity * (float) Math.sin(Math.toRadians(cameraToEntityTheta));
				
		float offsetX = horizontalDistance * (float) Math.sin(Math.toRadians(cameraToEntityPsi)),
			  offsetZ = horizontalDistance * (float) Math.cos(Math.toRadians(cameraToEntityPsi));

		position.x = entityToFollow.getPosition().x + offsetX;
		position.y = entityToFollow.getPosition().y + verticalDistance;
		position.z = entityToFollow.getPosition().z + offsetZ;
	}
	
	/**
	 * Translates and rotates the camera based on the entity's position and angles; these
	 * values are then sent to the shader classes where OpenGL can draw the scene. <p />
	 * 
	 * If chase view is enabled, mouse inputs are tracked here to move/rotate/zoom the 
	 * camera as needed
	 */
	public void move() {
		if(isChaseView)  {
			cameraDistanceToEntity -= Mouse.getDWheel() * 0.05f;
			
			if(Mouse.isButtonDown(1)) {
				cameraToEntityTheta += Mouse.getDY() * mouseSensitivity;
				cameraToEntityPsi   += Mouse.getDX() * mouseSensitivity;
			}
			
			calculateCameraPosition();
			
			phi   =  cameraToEntityPhi   % 180;
			theta =  cameraToEntityTheta % 180;
			psi   = -cameraToEntityPsi   % 360;			
		} else {
			position.x = entityToFollow.getPosition().x + pilotPosition.x;
			position.y = entityToFollow.getPosition().y + pilotPosition.y;
			position.z = entityToFollow.getPosition().z + pilotPosition.z;
					
			phi   = entityToFollow.getRotX(); 
			theta = entityToFollow.getRotY() + pitchOffset;
			psi   = entityToFollow.getRotZ();
		}
	}
	
	/**
	 * Translates and rotates the directly camera based on the position and angles supplied as arguments.
	 * 
	 * @param position
	 * @param phi
	 * @param theta
	 * @param psi
	 */
	public void move(Vector3f position, float phi, float theta, float psi) {
		this.position.x = position.x;
		this.position.y = position.y;
		this.position.z = position.z;
				
		this.phi   = phi; 
		this.theta = theta;
		this.psi   = psi;
	}
	
	public Vector3f getPosition() { return position; }
	
	public float getPitch() { return theta;	}
	
	public float getRoll() { return phi; }
	
	public float getYaw() { return psi; }
	
	public float getCameraSpeed() { return cameraPanSpeed; }

	public void setCameraSpeed(float cameraSpeed) { this.cameraPanSpeed = cameraSpeed; }
	
	public float getMouseSensitivity() { return mouseSensitivity; }

	public void setPosition(Vector3f position) { this.position = position; }

	public void setPitch(float pitch) { this.theta = pitch;	}

	public void setRoll(float roll) { this.phi = roll; }

	public void setYaw(float yaw) { this.psi = yaw;	}

	public void setMouseSensitivity(float mouseSensitivity) { this.mouseSensitivity = mouseSensitivity; }

	public boolean isChaseView() { return isChaseView; }
	
	/**
	 * Sets the camera to use the mouse to pan around if true, 
	 * otherwise a fixed first person view is used
	 * 
	 * @param isChaseView
	 */
	public void setChaseView(boolean isChaseView) {
		this.isChaseView = isChaseView;
		cameraToEntityTheta = isChaseView ? 20f : 0f;
		cameraDistanceToEntity = isChaseView ? 25.0f : 0.0f;
	}

	public Vector3f getPilotPosition() { return pilotPosition; }

	public void setPilotPosition(Vector3f pilotPosition) { this.pilotPosition = pilotPosition; }

	public float getPitchOffset() { return pitchOffset;	}

	public void setPitchOffset(float pitchOffset) { this.pitchOffset = pitchOffset;	}
}
