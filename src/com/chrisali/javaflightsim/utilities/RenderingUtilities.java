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
	
	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
		return matrix;
	}
	
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
	
	public static float barycentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x)  + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}
}
