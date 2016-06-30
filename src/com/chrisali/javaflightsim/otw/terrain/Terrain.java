package com.chrisali.javaflightsim.otw.terrain;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.chrisali.javaflightsim.otw.models.RawModel;
import com.chrisali.javaflightsim.otw.renderengine.Loader;
import com.chrisali.javaflightsim.otw.textures.TerrainTexture;
import com.chrisali.javaflightsim.otw.textures.TerrainTexturePack;
import com.chrisali.javaflightsim.utilities.RenderingUtilities;

public class Terrain {
	private static final float SIZE = 1600;
	private static final float MAX_HEIGHT = 20;
	private static final float MAX_PIXEL_COLOR = 256 * 256 * 256;
	
	private float x, z;
	private RawModel model;
	private TerrainTexturePack texturePack;
	private TerrainTexture blendMap;
	
	private float[][] heightArray;
	
	/**
	 * <p>Constructor for Terrain object; uses {@link TerrainTexturePack} and {@link TerrainTexture} to
	 * generate a terrain texture blend map </p>
	 * 
	 * <p>gridX and gridZ correspond to indices in the terrain array that this object resides</p>
	 * 
	 * <p>fileName and Directory point to a height map .png file to give the terrain vertical modeling</p>
	 * 
	 * @param gridX
	 * @param gridZ
	 * @param fileName
	 * @param directory
	 * @param loader
	 * @param texturePack
	 * @param blendMap
	 */
	public Terrain(int gridX, int gridZ, String fileName, String directory, 
					Loader loader, TerrainTexturePack texturePack, TerrainTexture blendMap) {
		this.texturePack = texturePack;
		this.blendMap = blendMap;
		this.x = gridX * SIZE;
		this.z = gridZ * SIZE;
		this.model = generateTerrain(fileName, directory, loader);
	}

	/**
	 * Gererates a terrain model using a BufferedImage height map
	 * 
	 * @param fileName
	 * @param directory
	 * @param loader
	 * @return terrain model
	 */
	private RawModel generateTerrain(String fileName, String directory, Loader loader){
		
		BufferedImage image = null;
		
		try {image = ImageIO.read(new File("Resources\\" + directory + "\\" + fileName + ".png"));} 
		catch (IOException e) {System.err.println("Could not load height map: " + fileName + ".png");}
		
		int VERTEX_COUNT = image.getHeight();
		int count = VERTEX_COUNT * VERTEX_COUNT;
		
		heightArray = new float[VERTEX_COUNT][VERTEX_COUNT];
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count*2];
		int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
		
		int vertexPointer = 0;
		for(int i=0;i<VERTEX_COUNT;i++){
			for(int j=0;j<VERTEX_COUNT;j++){
				vertices[vertexPointer*3] = (float)j/((float)VERTEX_COUNT - 1) * SIZE;
				
				float height = getHeightFromImage(j, i, image);
				heightArray[j][i] = height;
				
				vertices[vertexPointer*3+1] = getHeightFromImage(j, i, image);
				vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * SIZE;
				
				Vector3f normal = calculateNormal(j, i, image);
				normals[vertexPointer*3]   = normal.x;
				normals[vertexPointer*3+1] = normal.y;
				normals[vertexPointer*3+2] = normal.z;
				
				textureCoords[vertexPointer*2] = (float)j/((float)VERTEX_COUNT - 1);
				textureCoords[vertexPointer*2+1] = (float)i/((float)VERTEX_COUNT - 1);
				
				vertexPointer++;
			}
		}
		
		int pointer = 0;
		for(int gz=0;gz<VERTEX_COUNT-1;gz++){
			for(int gx=0;gx<VERTEX_COUNT-1;gx++){
				int topLeft = (gz*VERTEX_COUNT)+gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
				int bottomRight = bottomLeft + 1;
				
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		
		return loader.loadToVAO(vertices, textureCoords, normals, indices);
	}
	
	/**
	 * Calculates normal of a terrain vertex for use with lighting or specular calculations
	 * 
	 * @param x
	 * @param z
	 * @param image
	 * @return normal vector
	 */
	private Vector3f calculateNormal(int x, int z, BufferedImage image) {
		float heightL = getHeightFromImage(x-1, z  , image);
		float heightR = getHeightFromImage(x+1, z  , image);
		float heightD = getHeightFromImage(x  , z-1, image);
		float heightU = getHeightFromImage(x  , z+1, image);
		
		Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
		normal.normalise();
		
		return normal;
	}
	
	/**
	 * Calculates the height of a terrain vertex by reading the RGB value pixel of a 
	 * buffered image and converting it to a height value  
	 * 
	 * @param x
	 * @param z
	 * @param image
	 * @return height of terrain vertex
	 */
	private float getHeightFromImage(int x, int z, BufferedImage image) {
		// If out of terrain bounds, return 0
		if (x < 0 || x >= image.getHeight() || z < 0 || z >= image.getWidth())
			return 0;
		
		// Get RGB value and convert from white/black to +/-MAX_HEIGHT
		float height = image.getRGB(x, z);
		height += MAX_PIXEL_COLOR/2f;
		height /= MAX_PIXEL_COLOR/2f;
		height *= MAX_HEIGHT;
		
		return height;
	}
	
	/**
	 * Uses Barycentric interpolation to calculate the height of terrain for a given X and Z position
	 * 
	 * @param worldX
	 * @param worldZ
	 * @return terrain height
	 */
	public float getTerrainHeight(float worldX, float worldZ) {
		// Convert absolute world position to position relative to terrain square
		float terrainX = worldX - this.x;
		float terrainZ = worldZ - this.z;
		
		// Size of each grid square
		float gridSquareSize = SIZE / ((float)heightArray.length - 1);
		
		// Grid square that the player is located in
		int gridX = (int) Math.floor(terrainX/gridSquareSize);
		int gridZ = (int) Math.floor(terrainZ/gridSquareSize);
		
		// If outside terrain bounds return zero
		if (gridX >= (heightArray.length - 1) || gridZ >= (heightArray.length - 1) || gridX < 0 || gridZ < 0)
			return 0;
		
		// Location of player on a grid square
		float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
		float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;
		
		// Get terrain height by using barycentric coordinates
		float terrainHeight;
		if (xCoord <= (1-zCoord)) {
			terrainHeight = RenderingUtilities.barycentric(new Vector3f(0, heightArray[gridX][gridZ], 0), 
														   new Vector3f(1, heightArray[gridX + 1][gridZ], 0), 
														   new Vector3f(0, heightArray[gridX][gridZ + 1], 1), 
														   new Vector2f(xCoord, zCoord));
		} else {
			terrainHeight = RenderingUtilities.barycentric(new Vector3f(1, heightArray[gridX + 1][gridZ], 0), 
														   new Vector3f(1, heightArray[gridX + 1][gridZ + 1], 1), 
														   new Vector3f(0, heightArray[gridX][gridZ + 1], 1), 
														   new Vector2f(xCoord, zCoord));
		}
		
		return terrainHeight;			
	}
	
	/**
	 * Returns the Terrain object from a Terrain map that the player is currently standing on 
	 * 
	 * @param terrainMap
	 * @param worldX
	 * @param worldZ
	 * @return terrain object that the player is standing on
	 */
	public static Terrain getCurrentTerrain(Map<String, Terrain> terrainMap, float worldX, float worldZ) {
		// Floor divide player's absolute (world) x and z coordinates to get the grid indices that this terrain object lies in  
		int xGrid = Math.floorDiv((int)worldX, (int)Terrain.SIZE);
		int zGrid = Math.floorDiv((int)worldZ, (int)Terrain.SIZE);
		
		// "xGrid-zGrid" comprises each key to the map of terrains
		return terrainMap.get(xGrid + "-" + zGrid);
	}
	
	public float getX() {
		return x;
	}

	public float getZ() {
		return z;
	}

	public RawModel getModel() {
		return model;
	}

	public TerrainTexturePack getTexturePack() {
		return texturePack;
	}

	public TerrainTexture getBlendMap() {
		return blendMap;
	}

	public static float getSize() {
		return SIZE;
	}

	public static float getMaxHeight() {
		return MAX_HEIGHT;
	}
}
