package com.chrisali.javaflightsim.otw.interfaces.font;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chrisali.javaflightsim.otw.renderengine.FontRenderer;
import com.chrisali.javaflightsim.otw.renderengine.Loader;

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
