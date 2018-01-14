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
package com.chrisali.javaflightsim.lwjgl.interfaces.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chrisali.javaflightsim.lwjgl.renderengine.FontRenderer;
import com.chrisali.javaflightsim.lwjgl.renderengine.Loader;

public class TextMaster {
	private static Loader loader;
	private static Map<FontType, List<GUIText>> texts = new HashMap<>();
	private static FontRenderer renderer;
	
	public static void init(Loader loaderObj) {
		renderer = new FontRenderer();
		loader = loaderObj;
		
		// Need to clear out previous text from map when reinitializing otherwise will receive fatal error
		if(!texts.isEmpty()) 
			texts.clear();
	}
	
	public static void render(Map<String, GUIText> textMap) {
		for (Map.Entry<String, GUIText> entry : textMap.entrySet()) {
			TextMaster.loadText(entry.getValue());
			
			renderer.render(texts);
			
			TextMaster.removeText(entry.getValue());
		}
	}
	
	/**
	 * Loads {@link GUIText} object into VAO and texts HashMap to be rendered
	 * 
	 * @param text
	 */
	public static void loadText(GUIText text) {
		FontType font = text.getFont();
		TextMeshData data = font.loadText(text);
		int vao = loader.loadToVAO(data.getVertexPositions(), data.getTextureCoords());
		
		text.setMeshInfo(vao, data.getVertexCount());
		List<GUIText> textBatch = texts.get(font);
		
		if(textBatch == null) {
			textBatch = new ArrayList<>();
			texts.put(font, textBatch);
		}
		
		textBatch.add(text);
	}
	
	/**
	 * Removes {@link GUIText} object from texts HashMap after it has been rendered
	 * 
	 * @param text
	 */
	public static void removeText(GUIText text) {
		List<GUIText> textBatch = texts.get(text.getFont());
		textBatch.remove(text);

		if(textBatch.isEmpty())
			texts.remove(text.getFont());
	}
	
	public static void cleanUp() {
		renderer.cleanUp();
	}
}
