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
package com.chrisali.javaflightsim.lwjgl.shaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

public abstract class ShaderProgram {
	
	private static final Logger logger = LogManager.getLogger(ShaderProgram.class);
	
	protected static final String SHADER_ROOT_PATH = "/com/chrisali/javaflightsim/lwjgl/shaders/";
	protected static final String SHADER_EXTENSION = ".txt";
	
	private int programID;
	private int vertexShaderID;
	private int fragmentStaderID;
	
	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
	
	protected static int maxLights = 8;
	
	public ShaderProgram(String vertexFile, String fragmentFile) {
		vertexShaderID = loadShader(vertexFile, GL_VERTEX_SHADER);
		fragmentStaderID = loadShader(fragmentFile, GL_FRAGMENT_SHADER);
		programID = glCreateProgram();
		glAttachShader(programID, vertexShaderID);
		glAttachShader(programID, fragmentStaderID);
		bindAttributes();
		glLinkProgram(programID);
		glValidateProgram(programID);
		getAllUniformLocations();
	}
	
	protected abstract void getAllUniformLocations();
	
	protected int getUniformLocation(String uniformName) {
		return glGetUniformLocation(programID, uniformName);
	}
	
	public void start() {
		glUseProgram(programID);
	}
	
	public void stop() {
		glUseProgram(0);
	}
	
	public void cleanUp() {
		stop();
		glDetachShader(programID, vertexShaderID);
		glDetachShader(programID, fragmentStaderID);
		glDeleteShader(vertexShaderID);
		glDeleteShader(fragmentStaderID);
		glDeleteProgram(programID);
	}
	
	protected abstract void bindAttributes();
	
	protected void bindAttribute(int attribute, String variableName) {
		glBindAttribLocation(programID, attribute, variableName);
	}
	
	protected void loadInt(int location, int value) {
		glUniform1i(location, value);
	}
	
	protected void loadFloat(int location, float value) {
		glUniform1f(location, value);
	}
	
	protected void loadVector(int location, Vector4f vector) {
		glUniform4f(location, vector.x, vector.y, vector.z, vector.w);
	}
	
	protected void loadVector(int location, Vector3f vector) {
		glUniform3f(location, vector.x, vector.y, vector.z);
	}
	
	protected void loadVector(int location, Vector2f vector) {
		glUniform2f(location, vector.x, vector.y);
	}
	
	protected void loadBoolean(int location, boolean value) {
		float toLoad = 0;
		if (value)
			toLoad = 1;
		
		glUniform1f(location, toLoad);
	}
	
	protected void loadMatrix(int location, Matrix4f matrix) {
		matrix.store(matrixBuffer);
		matrixBuffer.flip();
		glUniformMatrix4fv(location, false, matrixBuffer);
	}
	
	private static int loadShader(String file, int type) {
		StringBuilder shaderSource = new StringBuilder();
		
		try {
			InputStream in = Class.class.getResourceAsStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			while((line=reader.readLine())!=null) {
				shaderSource.append(line).append("//\n");
			}
			reader.close();
		} catch (IOException e) {
			logger.fatal("Could not read shader file: " + file);
			System.exit(-1);
		}
		
		int shaderID = glCreateShader(type);
		glShaderSource(shaderID, shaderSource);
		glCompileShader(shaderID);
		
		if(glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
			logger.fatal("Could not compile shader: " + file);
			logger.fatal(glGetShaderInfoLog(shaderID, 500));
			System.exit(-1);
		}
		
		return shaderID;
	}
}
