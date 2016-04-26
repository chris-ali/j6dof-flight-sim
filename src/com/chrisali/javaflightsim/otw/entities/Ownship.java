package com.chrisali.javaflightsim.otw.entities;

import org.lwjgl.util.vector.Vector3f;

import com.chrisali.javaflightsim.otw.models.TexturedModel;

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
