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
package com.chrisali.javaflightsim.lwjgl.particles;

import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

public class Cloud extends Particle {
	
	private int textureIndex;
	private int textureIndices;

	public Cloud(ParticleTexture texture, Vector3f position, Vector3f velocity, float rotation, float scale) {
		super(texture, position, velocity, 0, Float.POSITIVE_INFINITY, rotation, scale);
		
		Random random = new Random();
		textureIndices = getTexture().getNumberOfAtlasRows() * getTexture().getNumberOfAtlasRows();
		textureIndex = random.nextInt(textureIndices - 1);
	}

	@Override
	protected void updateTextureCoordinateInfo() {
		int index1 = textureIndex;
		int index2 = index1 < textureIndices - 1 ? index1 + 1 : index1;
		
		this.textureBlend = 1.0f;
		
		setTextureOffset(getTextureOffset1(), index1);
		setTextureOffset(getTextureOffset2(), index2);
	}
}
