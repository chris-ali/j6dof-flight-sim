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
package com.chrisali.javaflightsim.lwjgl.renderengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.chrisali.javaflightsim.lwjgl.entities.Camera;
import com.chrisali.javaflightsim.lwjgl.entities.Entity;
import com.chrisali.javaflightsim.lwjgl.entities.EntityCollections;
import com.chrisali.javaflightsim.lwjgl.entities.Light;
import com.chrisali.javaflightsim.lwjgl.models.TexturedModel;
import com.chrisali.javaflightsim.lwjgl.shaders.StaticShader;
import com.chrisali.javaflightsim.lwjgl.shaders.TerrainShader;
import com.chrisali.javaflightsim.lwjgl.terrain.Terrain;

public class MasterRenderer {
	private static float fov = 85;
	private static float nearPlane = 0.1f;
	private static float farPlane = 6000;
	
	private static float skyRed = 0.5f;
	private static float skyGreen = 0.5f;
	private static float skyBlue = 0.5f;
	
	private static float fogDensity = 0.0015f;
	private static float fogGradient = 0.5f;
	
	private static float drawDistance = 4800;
	
	private StaticShader staticShader = new StaticShader();
	private TerrainShader terrainShader = new TerrainShader();
	
	private EntityRenderer entityRenderer;
	private Map<TexturedModel, List<Entity>> entityMap = new HashMap<>();
	
	private TerrainRenderer terrainRenderer;
	private TreeSet<Terrain> terrainTree = new TreeSet<>();
	
	private Matrix4f projectionMatrix;
	
	public MasterRenderer() {
		enableCulling();
		createProjectionMatrix();
		
		entityRenderer = new EntityRenderer(staticShader, projectionMatrix);
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
	}
	
	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	/**
	 * Takes all entities and terrains, and adds them (if necessary) to entity/terrain maps, and then renders the scene with
	 * the given lights, camera and clipping plane  
	 * 
	 * @param entityCollection
	 * @param terrainTreeMap
	 * @param lights
	 * @param camera
	 * @param clippingPlane
	 */
	public void renderWholeScene(EntityCollections entityCollection, TreeMap<String, Terrain> terrainTreeMap, List<Light> lights, Camera camera, Vector4f clippingPlane) {
		// Process miscellaneous entities from entityCollention
		for(Entity entity : entityCollection.getStaticEntities())
			processEntity(entity);
		
		for(Entity entity : entityCollection.getLitEntities())
			processEntity(entity);
		
		this.terrainTree = new TreeSet<>(terrainTreeMap.values());
		
		// Process entities tied to each terrain only if they are part of a terrain within the draw distance
		for (Terrain terrain : terrainTree) {
			if (terrain.getDistanceFromOwnship() < drawDistance) {
				for (Entity entity : terrain.getStaticEntities())
					processEntity(entity);
				
				for (Entity entity : terrain.getLitEntities())
					processEntity(entity);
			}
		}
		
		render(lights, camera, clippingPlane);
	}

	private void render(List<Light> lights, Camera camera, Vector4f clippingPlane) {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(skyRed, skyGreen, skyBlue, 1);

		staticShader.start();
		staticShader.loadClippingPlane(clippingPlane);
		staticShader.loadSkyColor(skyRed, skyGreen, skyBlue);
		staticShader.loadFog(fogDensity, fogGradient);
		staticShader.loadLights(lights);
		staticShader.loadViewMatrix(camera);
		entityRenderer.render(entityMap);
		staticShader.stop();
		
		terrainShader.start();
		terrainShader.loadClippingPlane(clippingPlane);
		terrainShader.loadSkyColor(skyRed, skyGreen, skyBlue);
		terrainShader.loadFog(fogDensity, fogGradient);
		terrainShader.loadLights(lights);
		terrainShader.loadViewMatrix(camera);
		terrainRenderer.render(terrainTree);
		terrainShader.stop();
		
		entityMap.clear();
		terrainTree.clear();
	}
	
	private void createProjectionMatrix() {
		float aspectRatio = DisplayManager.getAspectRatio();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(fov/2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = farPlane - nearPlane;
        
        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((farPlane + nearPlane) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * nearPlane * farPlane) / frustum_length);
        projectionMatrix.m33 = 0;
	}

	private void processEntity(Entity entity) {
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = entityMap.get(entityModel);
		
		if(batch!=null) {
			batch.add(entity);
		} else {
			List<Entity> newBatch = new ArrayList<>();
			newBatch.add(entity);
			entityMap.put(entityModel, newBatch);
		}
	}

	public void cleanUp() {
		staticShader.cleanUp();
		terrainShader.cleanUp();
	}
	
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public static Vector3f getSkyColor() {
		return new Vector3f(skyRed, skyGreen, skyBlue);
	}
	
	public static void setSkyColor(Vector3f skyColor) {
		skyRed = skyColor.x; 
		skyGreen = skyColor.y;
		skyBlue = skyColor.z;
	}

	public static float getFogDensity() {
		return fogDensity;
	}
	
	public static void setFogDensity(float fogDens) {
		fogDensity = fogDens;
	}

	public static float getFogGradient() {
		return fogGradient;
	}
	
	public static void setFogGradient(float fogGrad) {
		fogGradient = fogGrad;
	}
	
	public static void setFarPlane(float farPlane) {
		MasterRenderer.farPlane = farPlane;
	}
	
	public static void setFov(float fov) {
		MasterRenderer.fov = fov;
	}

	public static float getFov() {
		return fov;
	}

	public static float getDrawDistance() {
		return drawDistance;
	}

	public static void setDrawDistance(float drawDistance) {
		MasterRenderer.drawDistance = drawDistance;
	}
}
