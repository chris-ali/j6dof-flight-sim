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
package com.chrisali.javaflightsim.lwjgl.renderengine;

import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import com.chrisali.javaflightsim.lwjgl.interfaces.text.FontType;
import com.chrisali.javaflightsim.lwjgl.interfaces.text.GUIText;
import com.chrisali.javaflightsim.lwjgl.shaders.FontShader;

public class FontRenderer {

	private FontShader shader;

	public FontRenderer() {
		shader = new FontShader();
	}
	
	public void render(Map<FontType, List<GUIText>> texts) {
		prepare();
		
		for(FontType font : texts.keySet()) {
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, font.getTextureAtlas());
			
			for(GUIText text : texts.get(font))
				renderText(text);
		}
		
		endRendering();
	}

	public void cleanUp(){
		shader.cleanUp();
	}
	
	private void prepare(){
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glDisable(GL_DEPTH_TEST);
		shader.start();
	}
	
	private void renderText(GUIText text){
		glBindVertexArray(text.getMesh());
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		
		shader.loadColor(text.getColor());
		shader.loadTranslation(text.getPosition());
		glDrawArrays(GL_TRIANGLES, 0, text.getVertexCount());
		
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindVertexArray(0);
	}
	
	private void endRendering(){
		shader.stop();
		glDisable(GL_BLEND);
		glEnable(GL_DEPTH_TEST);
	}

}
