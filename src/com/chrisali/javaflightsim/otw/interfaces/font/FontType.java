/*******************************************************************************
 * Copyright (C) 2016-2017 Christopher Ali
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
package com.chrisali.javaflightsim.otw.interfaces.font;

import java.io.File;

import com.chrisali.javaflightsim.otw.renderengine.Loader;
import com.chrisali.javaflightsim.utilities.FileUtilities;

/**
 * Represents a font. It holds the font's texture atlas as well as having the
 * ability to create the quad vertices for any text using this font.
 * 
 * @author Karl
 *
 */
public class FontType {

	private int textureAtlas;
	private TextMeshCreator loader;

	/**
	 * Creates a new font and loads up the data about each character from the
	 * font file.
	 * 
	 * @param loader
	 *            - Resource Loader that generates the ID of the font atlas texture.
	 * @param fontName
	 *            - the font file's name in the Resources/Fonts folder, which contains 
	 *            information about each character in the texture atlas.
	 */
	public FontType(Loader loader, String fontName) {
		this.textureAtlas = loader.loadTexture(fontName, FileUtilities.FONTS_DIR);
		File fontFile = new File(FileUtilities.RESOURCES_DIR + File.separator + FileUtilities.FONTS_DIR + File.separator + fontName + FileUtilities.FONT_EXT); 
		this.loader = new TextMeshCreator(fontFile);
	}

	/**
	 * @return The font texture atlas.
	 */
	public int getTextureAtlas() {
		return textureAtlas;
	}

	/**
	 * Takes in an unloaded text and calculate all of the vertices for the quads
	 * on which this text will be rendered. The vertex positions and texture
	 * coords and calculated based on the information from the font file.
	 * 
	 * @param text
	 *            - the unloaded text.
	 * @return Information about the vertices of all the quads.
	 */
	public TextMeshData loadText(GUIText text) {
		return loader.createTextMesh(text);
	}

}
