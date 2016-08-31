package com.chrisali.javaflightsim.otw.renderengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.chrisali.javaflightsim.otw.entities.Camera;
import com.chrisali.javaflightsim.otw.entities.Entity;
import com.chrisali.javaflightsim.otw.entities.EntityCollections;
import com.chrisali.javaflightsim.otw.entities.Light;
import com.chrisali.javaflightsim.otw.models.TexturedModel;
import com.chrisali.javaflightsim.otw.shaders.StaticShader;
import com.chrisali.javaflightsim.otw.shaders.TerrainShader;
import com.chrisali.javaflightsim.otw.terrain.Terrain;

public class MasterRenderer {
	private static float fov = 85;
	private static float nearPlane = 0.1f;
	private static float farPlane = 6000;
	
	private static float skyRed = 0.5f;
	private static float skyGreen = 0.5f;
	private static float skyBlue = 0.5f;
	
	private static float fogDensity = 0.0015f;
	private static float fogGradient = 0.5f;
	
	private StaticShader staticShader = new StaticShader();
	private TerrainShader terrainShader = new TerrainShader();
	
	private EntityRenderer entityRenderer;
	private Map<TexturedModel, List<Entity>> entityMap = new HashMap<>();
	
	private TerrainRenderer terrainRenderer;
	private List<Terrain> terrains = new ArrayList<>();
	
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
	
	public void renderWholeScene(EntityCollections entityCollection, Map<String, Terrain> terrainMap, List<Light> lights, Camera camera, Vector4f clippingPlane) {
		for(Entity entity : entityCollection.getStaticEntities())
			processEntity(entity);
		
		for(Entity entity : entityCollection.getLitEntities())
			processEntity(entity);
		
		terrains = new ArrayList<>(terrainMap.values());
		
		render(lights, camera, clippingPlane);
	}

	private void render(List<Light> lights, Camera camera, Vector4f clippingPlane) {
		prepare();

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
		terrainRenderer.render(terrains);
		terrainShader.stop();
		
		entityMap.clear();
		terrains.clear();
	}
	
	private void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(skyRed, skyGreen, skyBlue, 1);
	}
	
	private void createProjectionMatrix() {
		float aspectRatio = (float) DisplayManager.getWidth() / (float) DisplayManager.getHeight();
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
	
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public void cleanUp() {
		staticShader.cleanUp();
		terrainShader.cleanUp();
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
}
