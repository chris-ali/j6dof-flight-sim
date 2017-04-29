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
package com.chrisali.javaflightsim.utilities;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.chrisali.javaflightsim.otw.entities.Camera;

/**
 * Contains static methods to create view/transformation matrices and calculate barycentric coordicates  
 * 
 * @author Christopher Ali
 *
 */
public class RenderingUtilities {
	
	/**
	 * Creates a transformation matrix for 2D particles using a 2D Vector for translation and scale
	 * 
	 * @param translation
	 * @param scale
	 * @return 4D transformation matrix
	 */
	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
		return matrix;
	}
	
	/**
	 * Creates a transformation matrix for 3D entities using a 3D vector for translation, and separate vales for rotation and scaling
	 * 
	 * @param translation
	 * @param rx
	 * @param ry
	 * @param rz
	 * @param scale
	 * @return 4D transformation matrix
	 */
	public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1,0,0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0,1,0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0,0,1), matrix, matrix);
		
		Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);
		
		return matrix;
	}
	
	/**
	 * Creates a 4D view matrix using the camera's current view angles 
	 * 
	 * @param camera
	 * @return 4D view matrix
	 */
	public static Matrix4f createViewMatrix(Camera camera){
		  Matrix4f viewMatrix = new Matrix4f();
		  viewMatrix.setIdentity();
		  
		  Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0), viewMatrix,
				  								 viewMatrix);
		  Matrix4f.rotate((float) Math.toRadians(camera.getYaw()),   new Vector3f(0, 1, 0), viewMatrix,
	  											 viewMatrix);
		  Matrix4f.rotate((float) Math.toRadians(camera.getRoll()),  new Vector3f((float) Math.sin(Math.toRadians(camera.getYaw())), 0, (float) -Math.cos(Math.toRadians(camera.getYaw()))), viewMatrix,
				  								 viewMatrix); // Rotation of unit vector to rotate around needed to ensure camera "rolls" regardless of yaw angle 
		  
		  Vector3f cameraPos = camera.getPosition();
		  Vector3f negativeCameraPos = new Vector3f(-cameraPos.x,-cameraPos.y,-cameraPos.z);
		  Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
		  
		  return viewMatrix;
	}
	
	/**
	 * Generates a barycentric coordinate using the coordinates of each point in a triangle
	 * 
	 * @param p1
	 * @param p2
	 * @param p3
	 * @param pos
	 * @return barycentric coordinate
	 */
	public static float barycentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x)  + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}
}
