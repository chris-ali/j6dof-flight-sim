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

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import com.chrisali.javaflightsim.lwjgl.interfaces.gauges.InstrumentPanel;
import com.chrisali.javaflightsim.lwjgl.interfaces.ui.InterfaceTexture;
import com.chrisali.javaflightsim.lwjgl.loader.Loader;
import com.chrisali.javaflightsim.lwjgl.models.RawModel;
import com.chrisali.javaflightsim.lwjgl.shaders.InterfaceShader;
import com.chrisali.javaflightsim.lwjgl.utilities.RenderingUtilities;
import com.chrisali.javaflightsim.simulation.setup.SimulationConfiguration;

public class InterfaceRenderer {
	
	private final RawModel quad;
	private InterfaceShader shader;
	
	public InterfaceRenderer(Loader loader) {
		float[] positions = {-1, 1, -1, -1, 1, 1, 1, -1};
		quad = loader.loadToVAO(positions);
		shader = new InterfaceShader();
	}
	
	public void render(SimulationConfiguration configuration, Map<String, List<InterfaceTexture>> interfaceTextures) {
		shader.start();
		
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		for (Map.Entry<String, List<InterfaceTexture>> entry : interfaceTextures.entrySet()) {
			if (!configuration.getCameraConfiguration().isShowPanel() && entry.getKey().matches(InstrumentPanel.class.getSimpleName()))
				continue;
			
			for (InterfaceTexture interfaceTexture : entry.getValue()) {
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, interfaceTexture.getTexture());
				
				Matrix4f matrix = RenderingUtilities.createTransformationMatrix(interfaceTexture.getPosition(),
						interfaceTexture.getRotation(),
						interfaceTexture.getScale());
				shader.loadTransformation(matrix);
				
				GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
			}		
		}
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		
		shader.stop();
	}
	
	public void cleanUp() {
		shader.cleanUp();
	}
}
