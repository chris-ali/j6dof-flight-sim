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
package com.chrisali.javaflightsim.lwjgl.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GLContext;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import com.chrisali.javaflightsim.lwjgl.models.RawModel;
import com.chrisali.javaflightsim.lwjgl.utilities.OTWDirectories;
import com.chrisali.javaflightsim.lwjgl.utilities.OTWFiles;

/**
 * Class that contains various methods to load resources (textures, models, etc) into memory
 * 
 * @author Christopher Ali
 *
 */
public class Loader {
	
	private static final Logger logger = LogManager.getLogger(Loader.class);
	
	private static boolean useAnisotropicFiltering;

	private List<Integer> vaoList = new LinkedList<>();
	private List<Integer> vboList = new LinkedList<>();
	private List<Integer> textureList = new LinkedList<>();

	//=============================== VAO Loaders for Various Entity Types =====================================
	
	public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, int[] indices) {
		int vaoID = createVAO();
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0, 3, positions);
		storeDataInAttributeList(1, 2, textureCoords);
		storeDataInAttributeList(2, 3, normals);
		unbindVAO();

		return new RawModel(vaoID, indices.length);
	}
	
	public RawModel loadToVAO(float[] positions, int dimensions) {
		int vaoID = createVAO();
		this.storeDataInAttributeList(0, dimensions, positions);
		unbindVAO();
		
		return new RawModel(vaoID, positions.length / dimensions);
	}
	
	public RawModel loadToVAO(float[] positions) {
		int vaoID = createVAO();
		this.storeDataInAttributeList(0, 2, positions);
		unbindVAO();
		
		return new RawModel(vaoID, positions.length / 2);
	}

	public int loadToVAO(float[] positions, float[] textureCoords) {
		int vaoID = createVAO();
		storeDataInAttributeList(0, 2, positions);
		storeDataInAttributeList(1, 2, textureCoords);
		unbindVAO();

		return vaoID;
	}
	
	private int createVAO() {
		int vaoID = GL30.glGenVertexArrays();
		vaoList.add(vaoID);
		GL30.glBindVertexArray(vaoID);
		return vaoID;
	}
	
	private void unbindVAO() {
		GL30.glBindVertexArray(0);
	}

	//================================== VBO Methods =============================================

	public int createEmptyVBO(int floatCount) {
		int vbo = GL15.glGenBuffers();
		vboList.add(vbo);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, floatCount * 4, GL15.GL_STREAM_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		return vbo;
	}
	
	public void updateVBO(int vbo, float[] data, FloatBuffer buffer) {
		buffer.clear();
		buffer.put(data);
		buffer.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer.capacity() * 4, GL15.GL_STREAM_DRAW);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	public void addInstancedAttribute(int vao, int vbo, int attribute, int dataSize, int instancedDataLength,
			int offset) {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL30.glBindVertexArray(vao);
		GL20.glVertexAttribPointer(attribute, dataSize, GL11.GL_FLOAT, false, instancedDataLength * 4, offset * 4);
		GL33.glVertexAttribDivisor(attribute, 1);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}
	
	//========================================== Textures ===========================================

	/**
	 * Loads a texture into memory using SlikUtils png loader using a specific directory stemming from the ./Resources
	 * directory. Sets anisotropic filtering for textures as well in this method
	 * 
	 * @param fileName
	 * @param directory
	 * @return texture ID
	 */
	public int loadTexture(String fileName, String directory) {
		return loadTexture(OTWDirectories.RESOURCES.toString(), fileName, directory);
	}
	
	/**
	 * Loads a texture into memory using SlikUtils png loader using a specific directory stemming from the ./Resources
	 * directory. Sets anisotropic filtering for textures as well in this method
	 * 
	 * @param rootDirectory
	 * @param fileName
	 * @param directory
	 * @return texture ID
	 */
	public int loadTexture(String rootDirectory,String fileName, String directory) {
		int textureID = loadAndGetTexture(rootDirectory, fileName, directory).getTextureID();
		textureList.add(textureID);

		return textureID;
	}
	
	/**
	 * Loads a texture into memory using SlikUtils png loader using a specific directory stemming from the ./Resources
	 * directory. Sets anisotropic filtering for textures as well in this method. Returns the Texture obect directly so that
	 * the file's properties can be used elsewhere
	 * 
	 * @param fileName
	 * @param directory
	 * @return Texture object
	 */
	public Texture loadAndGetTexture(String fileName, String directory) {
		return loadAndGetTexture(OTWDirectories.RESOURCES.toString(), fileName, directory);
	}
	
	/**
	 * Loads a texture into memory using SlikUtils png loader using a specific directory stemming from the rootDirectory
	 * argument. Sets anisotropic filtering for textures as well in this method. Returns the Texture obect directly so that
	 * the file's properties can be used elsewhere
	 * 
	 * @param rootDirectory
	 * @param fileName
	 * @param directory
	 * @return Texture object
	 */
	public Texture loadAndGetTexture(String rootDirectory, String fileName, String directory) {
		Texture texture = null;
		
		try {
			texture = TextureLoader.getTexture("PNG",
					new FileInputStream(rootDirectory + File.separator + directory + File.separator + fileName + OTWFiles.TEXTURE_EXT.toString()));
		
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
			
			// Set Anisotropic Filtering
			if (GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic && useAnisotropicFiltering) {
				float value = Math.min(4f, GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, value);
			}
			
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.4f);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
				
			return texture;
		} catch (IOException e) {
			logger.error("Could not load texture: " + fileName + OTWFiles.TEXTURE_EXT.toString(), e);
		}
		
		return texture;
	}
		
	public static void setUseAnisotropicFiltering(boolean useAnisotropicFiltering) {
		Loader.useAnisotropicFiltering = useAnisotropicFiltering;
	}
	
	//=================================== Indices and Buffers ========================================
	
	private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data) {
		int vboID = GL15.glGenBuffers();
		vboList.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	private void bindIndicesBuffer(int[] indices) {
		int vboId = GL15.glGenBuffers();
		vboList.add(vboId);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboId);
		IntBuffer buffer = storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}

	private IntBuffer storeDataInIntBuffer(int[] data) {
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	private FloatBuffer storeDataInFloatBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	public void cleanUp() {
		for (int vao : vaoList)
			GL30.glDeleteVertexArrays(vao);
		for (int vbo : vboList)
			GL15.glDeleteBuffers(vbo);
		for (int texture : textureList)
			GL11.glDeleteTextures(texture);
		
		vaoList.clear();
		vboList.clear();
		textureList.clear();
	}
}
