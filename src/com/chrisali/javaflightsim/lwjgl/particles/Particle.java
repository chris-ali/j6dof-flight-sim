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
package com.chrisali.javaflightsim.lwjgl.particles;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.chrisali.javaflightsim.lwjgl.entities.Camera;
import com.chrisali.javaflightsim.lwjgl.renderengine.DisplayManager;

public class Particle {
	
	private static final float GRAVITY = -50;
	
	private Vector3f position;
	private Vector3f velocity;
	private float gravityEffect;
	private float lifeLength;
	private float rotation;
	private float scale;

	private ParticleTexture texture;
	
	private Vector2f textureOffset1 = new Vector2f();
	private Vector2f textureOffset2 = new Vector2f();
	protected float textureBlend;
	
	private float elapsedTime = 0;
	private float distaceFromCamera;
	
	Vector3f change = new Vector3f();

	public Particle(ParticleTexture texture, Vector3f position, Vector3f velocity, float gravityEffect, float lifeLength, float rotation, float scale) {
		this.texture = texture;
		this.position = position;
		this.velocity = velocity;
		this.lifeLength = lifeLength;
		this.gravityEffect = gravityEffect;
		this.rotation = rotation;
		this.scale = scale;
		ParticleMaster.addParticle(this);
	}
	
	protected boolean update(Camera camera) {
		velocity.y += GRAVITY * gravityEffect * DisplayManager.getFrameTimeSeconds();
		
		change.set(velocity);
		change.scale(DisplayManager.getFrameTimeSeconds());
		Vector3f.add(change, position, position);
		
		distaceFromCamera = Vector3f.sub(camera.getPosition(), position, null).lengthSquared();
		
		updateTextureCoordinateInfo();
		elapsedTime += DisplayManager.getFrameTimeSeconds();
		
		return elapsedTime < lifeLength;
	}
	
	protected void updateTextureCoordinateInfo() {
		float lifeFactor = elapsedTime / lifeLength;
		int stageCount = texture.getNumberOfAtlasRows() * texture.getNumberOfAtlasRows();
		float atlasProgression = lifeFactor * stageCount;
		
		int index1 = (int)Math.floor(atlasProgression);
		int index2 = index1 < stageCount - 1 ? index1 + 1 : index1;
		
		this.textureBlend = atlasProgression % 1;
		
		setTextureOffset(textureOffset1, index1);
		setTextureOffset(textureOffset2, index2);
	}
	
	protected void setTextureOffset(Vector2f offset, int index) {
		int column = index % texture.getNumberOfAtlasRows();
		int row = index / texture.getNumberOfAtlasRows();
		offset.x = (float)column / texture.getNumberOfAtlasRows();
		offset.y = (float)row / texture.getNumberOfAtlasRows();
	}

	public float getDistaceFromCamera() {
		return distaceFromCamera;
	}

	public Vector2f getTextureOffset1() {
		return textureOffset1;
	}

	public Vector2f getTextureOffset2() {
		return textureOffset2;
	}

	public float getTextureBlend() {
		return textureBlend;
	}

	public Vector3f getPosition() {
		return position;
	}
	
	public float getLifeLength() {
		return lifeLength;
	}

	public ParticleTexture getTexture() {
		return texture;
	}

	public Vector3f getVelocity() {
		return velocity;
	}

	public float getGravityEffect() {
		return gravityEffect;
	}

	public float getRotation() {
		return rotation;
	}

	public float getScale() {
		return scale;
	}
	
}
