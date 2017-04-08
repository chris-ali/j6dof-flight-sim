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
