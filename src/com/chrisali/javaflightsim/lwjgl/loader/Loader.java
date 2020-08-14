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
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.*;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

import com.chrisali.javaflightsim.lwjgl.models.RawModel;
import com.chrisali.javaflightsim.lwjgl.renderengine.DisplayManager;
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
		int vaoID = glGenVertexArrays();
		vaoList.add(vaoID);
		glBindVertexArray(vaoID);
		return vaoID;
	}
	
	private void unbindVAO() {
		glBindVertexArray(0);
	}

	//================================== VBO Methods =============================================

	public int createEmptyVBO(int floatCount) {
		int vbo = glGenBuffers();
		vboList.add(vbo);
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, floatCount * 4, GL_STREAM_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		return vbo;
	}
	
	public void updateVBO(int vbo, float[] data, FloatBuffer buffer) {
		buffer.clear();
		buffer.put(data);
		buffer.flip();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, buffer.capacity() * 4, GL_STREAM_DRAW);
		glBufferSubData(GL_ARRAY_BUFFER, 0, buffer);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	public void addInstancedAttribute(int vao, int vbo, int attribute, int dataSize, int instancedDataLength,
			int offset) {
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBindVertexArray(vao);
		glVertexAttribPointer(attribute, dataSize, GL_FLOAT, false, instancedDataLength * 4, offset * 4);
		glVertexAttribDivisor(attribute, 1);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}
	
	//========================================== Textures ===========================================

	/**
	 * Loads a texture into memory with PNGDecoder using a specific directory stemming from the ./Resources
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
	 * Loads a texture into memory with PNGDecoder using a specific directory stemming from the ./Resources
	 * directory. Sets anisotropic filtering for textures as well in this method
	 * 
	 * @param rootDirectory
	 * @param fileName
	 * @param directory
	 * @return texture ID
	 */
	public int loadTexture(String rootDirectory,String fileName, String directory) {
		int textureID = loadAndGetTextureID(rootDirectory, fileName, directory);
		textureList.add(textureID);

		return textureID;
	}
	
	/**
	 * Loads a texture into memory with PNGDecoder using a specific directory stemming from the ./Resources
	 * directory. Sets anisotropic filtering for textures as well in this method. Returns the Texture obect directly so that
	 * the file's properties can be used elsewhere
	 * 
	 * @param fileName
	 * @param directory
	 * @return OpenGL texture ID
	 */
	public int loadAndGetTexture(String fileName, String directory) {
		return loadAndGetTextureID(OTWDirectories.RESOURCES.toString(), fileName, directory);
	}
	
	/**
	 * Loads a texture into memory with PNGDecoder using a specific directory stemming from the rootDirectory
	 * argument. Sets anisotropic filtering for textures as well in this method. Returns the Texture obect directly so that
	 * the file's properties can be used elsewhere
	 * 
	 * @param rootDirectory
	 * @param fileName
	 * @param directory
	 * @return OpenGL texture ID
	 */
	public int loadAndGetTextureID(String rootDirectory, String fileName, String directory) {
		try {
			PNGDecoder decoder = new PNGDecoder(new FileInputStream(rootDirectory + File.separator + directory 
												+ File.separator + fileName + OTWFiles.TEXTURE_EXT.toString()));

			ByteBuffer buf = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
			decoder.decode(buf, decoder.getWidth() * 4, Format.RGBA);
			buf.flip();

			// Create a new OpenGL texture 
			int textureId = glGenTextures();

			// Bind the texture
			glBindTexture(GL_TEXTURE_2D, textureId);
			glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 
						0, GL_RGBA, GL_UNSIGNED_BYTE, buf);

			glGenerateMipmap(GL_TEXTURE_2D);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
			
			// Set Anisotropic Filtering
			if (DisplayManager.getGlCapabilities().GL_EXT_texture_filter_anisotropic && useAnisotropicFiltering) {
				float value = Math.min(4f, glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
				glTexParameterf(GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, value);
			}
			
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, -0.4f);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
				
			return textureId;
		} catch (IOException e) {
			logger.error("Could not load texture: " + fileName + OTWFiles.TEXTURE_EXT.toString(), e);
		}
		
		return 0;
	}
		
	public static void setUseAnisotropicFiltering(boolean useAnisotropicFiltering) {
		Loader.useAnisotropicFiltering = useAnisotropicFiltering;
	}
	
	//=================================== Indices and Buffers ========================================
	
	private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data) {
		int vboID = glGenBuffers();
		vboList.add(vboID);
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		glVertexAttribPointer(attributeNumber, coordinateSize, GL_FLOAT, false, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	private void bindIndicesBuffer(int[] indices) {
		int vboId = glGenBuffers();
		vboList.add(vboId);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
		IntBuffer buffer = storeDataInIntBuffer(indices);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
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
			glDeleteVertexArrays(vao);
		for (int vbo : vboList)
			glDeleteBuffers(vbo);
		for (int texture : textureList)
			glDeleteTextures(texture);
		
		vaoList.clear();
		vboList.clear();
		textureList.clear();
	}
}
