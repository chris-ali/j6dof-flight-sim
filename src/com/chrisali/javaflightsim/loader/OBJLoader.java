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
package com.chrisali.javaflightsim.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.chrisali.javaflightsim.lwjgl.models.RawModel;
import com.chrisali.javaflightsim.lwjgl.utilities.OTWDirectories;
import com.chrisali.javaflightsim.lwjgl.utilities.OTWFiles;

/**
 * Contains static methods to load a {@link RawModel} into memory by reading and processing all vertices, textures and
 * normals associated with the model 
 * 
 * @author Christopher Ali
 *
 */
public class OBJLoader {
	
	private static final Logger logger = LogManager.getLogger(OBJLoader.class);
	
	/**
	 * Loads OBJ model into memory by specifying a URI to the model's location 
	 * 
	 * @param fileName
	 * @param directory
	 * @param loader
	 * @return RawModwl object
	 */
	public static RawModel loadObjModel(String fileName, String directory, Loader loader) {
 		FileReader fr = null;
		
		try {fr = new FileReader(new File(OTWDirectories.RESOURCES.toString() + File.separator + directory + File.separator + fileName + OTWFiles.MODEL_EXT.toString()));} 
		catch (FileNotFoundException e) {logger.error("Could not load model: " + fileName + OTWFiles.MODEL_EXT.toString() + "!", e);}
		
		BufferedReader reader = new BufferedReader(fr);
		String line;
		
		List<Vector3f> vertices = new ArrayList<>();
		List<Vector2f> textures = new ArrayList<>();
		List<Vector3f> normals = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();
		
		float[] verticesArray = null;
		float[] texturesArray = null;
		float[] normalsArray = null;
		int[] indicesArray = null;
		
		try {
			while(true) {
				line = reader.readLine();
				String[] currentLine = line.split(" ");
				if(line.startsWith("v ")) {
					Vector3f vertex = new Vector3f(Float.parseFloat(currentLine[1]),
												   Float.parseFloat(currentLine[2]),
												   Float.parseFloat(currentLine[3]));
					vertices.add(vertex);
				} else if (line.startsWith("vt ")) {
					Vector2f texture = new Vector2f(Float.parseFloat(currentLine[1]),
							   						Float.parseFloat(currentLine[2]));
					textures.add(texture);
				} else if (line.startsWith("vn ")) {
					Vector3f normal = new Vector3f(Float.parseFloat(currentLine[1]),
												   Float.parseFloat(currentLine[2]),
												   Float.parseFloat(currentLine[3]));
					normals.add(normal);
				} else if (line.startsWith("f ")) {
					texturesArray = new float[vertices.size()*2];
					normalsArray  = new float[vertices.size()*3];
					break;
				}
			}
			
			while(line!=null) {
				if (!line.startsWith("f ")) {
					line = reader.readLine();
					continue;
				}
				String[] currentLine = line.split(" ");
				String[] vertex1 = currentLine[1].split("/");
				String[] vertex2 = currentLine[2].split("/");
				String[] vertex3 = currentLine[3].split("/");
				
				processVertex(vertex1, indices, textures, vertices, normals, texturesArray, normalsArray);
				processVertex(vertex2, indices, textures, vertices, normals, texturesArray, normalsArray);
				processVertex(vertex3, indices, textures, vertices, normals, texturesArray, normalsArray);
				
				line = reader.readLine();
			}
			
			reader.close();
			
		} catch (Exception e) {
			logger.error("An error occurred while reading the model: " + fileName, e);
		}
		
		verticesArray= new float[vertices.size()*3];
		indicesArray= new int[indices.size()];
		
		int vertexPointer = 0;
		for(Vector3f vertex : vertices) {
			verticesArray[vertexPointer++] = vertex.x;
			verticesArray[vertexPointer++] = vertex.y;
			verticesArray[vertexPointer++] = vertex.z;
		}
		
		for(int i=0; i<indices.size(); i++)
			indicesArray[i] = indices.get(i);
		
		return loader.loadToVAO(verticesArray, texturesArray, normalsArray, indicesArray);
	}
	
	private static void processVertex(String[] vertexData, List<Integer> indices, List<Vector2f> textures, 
								List<Vector3f> vertices, List<Vector3f> normals, float[] texturesArray, float[] normalsArray) {
		int currentVertexPointer = Integer.parseInt(vertexData[0])-1;
		indices.add(currentVertexPointer);
		
		Vector2f currentTex = textures.get(Integer.parseInt(vertexData[1])-1);
		texturesArray[currentVertexPointer*2]   =     currentTex.x;
		texturesArray[currentVertexPointer*2+1] = 1 - currentTex.y;
		
		Vector3f currentNorm = normals.get(Integer.parseInt(vertexData[2])-1);
		normalsArray[currentVertexPointer*3]   = currentNorm.x;
		normalsArray[currentVertexPointer*3+1] = currentNorm.y;
		normalsArray[currentVertexPointer*3+2] = currentNorm.z;
	}
}
