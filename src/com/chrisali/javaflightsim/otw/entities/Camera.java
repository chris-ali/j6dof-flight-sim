package com.chrisali.javaflightsim.otw.entities;

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
		cameraToEntityTheta = isChaseView ? 20f : 0f;
		cameraDistanceToEntity = isChaseView ? 25.0f : 0.0f;
	}
	
	/**
	 * Sets the position of the camera based on trigometric calculations performed in the XZ plane of
	 * the entity
	 * 
	 * @param horizontalDistance
	 * @param verticalDistance
	 */
	private void calculateCameraPosition(float horizontalDistance, float verticalDistance) {
		float camAngle = entityToFollow.getRotY() + cameraToEntityPsi;
		float offsetX = horizontalDistance * (float) Math.sin(Math.toRadians(camAngle));
		float offsetZ = horizontalDistance * (float) Math.cos(Math.toRadians(camAngle));
		
		if (isChaseView) {	
			position.x = entityToFollow.getPosition().x - offsetX;
			position.y = entityToFollow.getPosition().y + verticalDistance;
			position.z = entityToFollow.getPosition().z - offsetZ;
		} else {
			position.x = entityToFollow.getPosition().x - offsetX;
			position.y = entityToFollow.getPosition().y + verticalDistance + pilotPosition.y;
			position.z = entityToFollow.getPosition().z - offsetZ + pilotPosition.z;
		}
	}
	
	/**
	 * Calculates horizontal distance based on the camera's pitch angle and the 
	 * magnitude of the distance vector from the camera to player; if chase view
	 * is enabled, distance vector is pilot's "eye" distance vector from the origin
	 * of the entity to the camera 
	 * 
	 * @return HorizontalDistanceToPlayer
	 */
	private float calculateHorizontalDistanceToPlayer() {
		if (isChaseView)
			return  (cameraDistanceToEntity * (float) Math.cos(Math.toRadians(theta)));
		else
			return  (pilotPosition.x * (float) Math.cos(Math.toRadians(theta)));
	}
	
	/**
	 * Calculates vertical distance based on the camera's pitch angle and the 
	 * magnitude of the distance vector from the camera to player; if chase view
	 * is enabled, distance vector is pilot's "eye" distance vector from the origin
	 * of the entity to the camera 
	 * 
	 * @return VerticalDistanceToPlayer
	 */
	private float calculateVerticalDistanceToPlayer() {
		if (isChaseView)
			return  (cameraDistanceToEntity * (float) Math.sin(Math.toRadians(theta)));
		else
			return  (pilotPosition.x * (float) Math.sin(Math.toRadians(theta)));
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
				cameraToEntityPsi += Mouse.getDX() * mouseSensitivity;
			}
		}
		
		calculateCameraPosition(calculateHorizontalDistanceToPlayer(), 
								calculateVerticalDistanceToPlayer());
		
		phi   =   0 - (entityToFollow.getRotZ() + cameraToEntityPhi);
		theta =   0 + (entityToFollow.getRotX() + cameraToEntityTheta);
		psi   = 180 - (entityToFollow.getRotY() + cameraToEntityPsi);
	}
	
	/**
	 * Translates and rotates the directly camera based on the position and angles supplied as arguments;
	 * these values are then sent to the shader classes where OpenGL can draw the scene.
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
		
		this.phi = phi; 
		this.theta = theta;
		this.psi = psi;
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public float getPitch() {
		return theta;
	}
	
	public float getRoll() {
		return phi;
	}
	
	public float getYaw() {
		return psi;
	}
	
	public float getCameraSpeed() {
		return cameraPanSpeed;
	}

	public void setCameraSpeed(float cameraSpeed) {
		this.cameraPanSpeed = cameraSpeed;
	}
	
	public float getMouseSensitivity() {
		return mouseSensitivity;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public void setPitch(float pitch) {
		this.theta = pitch;
	}

	public void setRoll(float roll) {
		this.phi = roll;
	}

	public void setYaw(float yaw) {
		this.psi = yaw;
	}

	public void setMouseSensitivity(float mouseSensitivity) {
		this.mouseSensitivity = mouseSensitivity;
	}

	public boolean isChaseView() {
		return isChaseView;
	}
	
	/**
	 * Sets the camera to use the mouse to pan around if true, 
	 * otherwise a fixed first person view is used
	 * 
	 * @param isChaseView
	 */
	public void setChaseView(boolean isChaseView) {
		cameraToEntityTheta = isChaseView ? 20f : 0f;
		cameraDistanceToEntity = isChaseView ? 25.0f : 0.0f;
		this.isChaseView = isChaseView;
	}

	public Vector3f getPilotPosition() {
		return pilotPosition;
	}

	public void setPilotPosition(Vector3f pilotPosition) {
		this.pilotPosition = pilotPosition;
	}
}
