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
package com.chrisali.javaflightsim.lwjgl.interfaces.text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

/**
 * Provides functionality for getting the values from a font file.
 * 
 * @author Karl
 *
 */
public class MetaFile {
	
	//Logging
	private static final Logger logger = LogManager.getLogger(MetaFile.class);

	private static final int PAD_TOP = 0;
	private static final int PAD_LEFT = 1;
	private static final int PAD_BOTTOM = 2;
	private static final int PAD_RIGHT = 3;

	private static final int DESIRED_PADDING = 3;

	private static final String SPLITTER = " ";
	private static final String NUMBER_SEPARATOR = ",";

	private double aspectRatio;

	private double verticalPerPixelSize;
	private double horizontalPerPixelSize;
	private double spaceWidth;
	private int[] padding;
	private int paddingWidth;
	private int paddingHeight;

	private Map<Integer, Character> metaData = new HashMap<Integer, Character>();

	private BufferedReader reader;
	private Map<String, String> values = new HashMap<String, String>();

	/**
	 * Opens a font file in preparation for reading.
	 * 
	 * @param file
	 *            - the font file.
	 */
	protected MetaFile(File file) {
		this.aspectRatio = (double) Display.getWidth() / (double) Display.getHeight();
		openFile(file);
		loadPaddingData();
		loadLineSizes();
		int imageWidth = getValueOfVariable("scaleW");
		loadCharacterData(imageWidth);
		close();
	}

	protected double getSpaceWidth() {
		return spaceWidth;
	}

	protected Character getCharacter(int ascii) {
		return metaData.get(ascii);
	}

	/**
	 * Read in the next line and store the variable values.
	 * 
	 * @return {@code true} if the end of the file hasn't been reached.
	 */
	private boolean processNextLine() {
		values.clear();
		String line = null;
		
		try {
			line = reader.readLine();
		} catch (IOException e) {
			logger.error("Error reading line in file!", e);
		}
		
		if (line == null) {
			return false;
		}
		
		for (String part : line.split(SPLITTER)) {
			String[] valuePairs = part.split("=");
			if (valuePairs.length == 2) {
				values.put(valuePairs[0], valuePairs[1]);
			}
		}
		
		return true;
	}

	/**
	 * Gets the {@code int} value of the variable with a certain name on the
	 * current line.
	 * 
	 * @param variable
	 *            - the name of the variable.
	 * @return The value of the variable.
	 */
	private int getValueOfVariable(String variable) {
		return Integer.parseInt(values.get(variable));
	}

	/**
	 * Gets the array of ints associated with a variable on the current line.
	 * 
	 * @param variable
	 *            - the name of the variable.
	 * @return The int array of values associated with the variable.
	 */
	private int[] getValuesOfVariable(String variable) {
		String[] numbers = values.get(variable).split(NUMBER_SEPARATOR);
		int[] actualValues = new int[numbers.length];
		for (int i = 0; i < actualValues.length; i++) {
			actualValues[i] = Integer.parseInt(numbers[i]);
		}
		return actualValues;
	}

	/**
	 * Closes the font file after finishing reading.
	 */
	private void close() {
		try {
			reader.close();
		} catch (IOException e) {
			logger.error("Error closing font file reader!", e);
		}
	}

	/**
	 * Opens the font file, ready for reading.
	 * 
	 * @param file
	 *            - the font file.
	 */
	private void openFile(File file) {
		try {
			reader = new BufferedReader(new FileReader(file));
		} catch (Exception e) {
			logger.error("Couldn't read font metafile: " + file.getAbsolutePath() + file.getName(), e);
		}
	}

	/**
	 * Loads the data about how much padding is used around each character in
	 * the texture atlas.
	 */
	private void loadPaddingData() {
		processNextLine();
		this.padding = getValuesOfVariable("padding");
		this.paddingWidth = padding[PAD_LEFT] + padding[PAD_RIGHT];
		this.paddingHeight = padding[PAD_TOP] + padding[PAD_BOTTOM];
	}

	/**
	 * Loads information about the line height for this font in pixels, and uses
	 * this as a way to find the conversion rate between pixels in the texture
	 * atlas and screen-space.
	 */
	private void loadLineSizes() {
		processNextLine();
		int lineHeightPixels = getValueOfVariable("lineHeight") - paddingHeight;
		verticalPerPixelSize = TextMeshCreator.LINE_HEIGHT / (double) lineHeightPixels;
		horizontalPerPixelSize = verticalPerPixelSize / aspectRatio;
	}

	/**
	 * Loads in data about each character and stores the data in the
	 * {@link Character} class.
	 * 
	 * @param imageWidth
	 *            - the width of the texture atlas in pixels.
	 */
	private void loadCharacterData(int imageWidth) {
		processNextLine();
		processNextLine();
		while (processNextLine()) {
			Character c = loadCharacter(imageWidth);
			if (c != null) {
				metaData.put(c.getId(), c);
			}
		}
	}

	/**
	 * Loads all the data about one character in the texture atlas and converts
	 * it all from 'pixels' to 'screen-space' before storing. The effects of
	 * padding are also removed from the data.
	 * 
	 * @param imageSize
	 *            - the size of the texture atlas in pixels.
	 * @return The data about the character.
	 */
	private Character loadCharacter(int imageSize) {
		int id = getValueOfVariable("id");
		if (id == TextMeshCreator.SPACE_ASCII) {
			this.spaceWidth = (getValueOfVariable("xadvance") - paddingWidth) * horizontalPerPixelSize;
			return null;
		}
		double xTex = ((double) getValueOfVariable("x") + (padding[PAD_LEFT] - DESIRED_PADDING)) / imageSize;
		double yTex = ((double) getValueOfVariable("y") + (padding[PAD_TOP] - DESIRED_PADDING)) / imageSize;
		int width = getValueOfVariable("width") - (paddingWidth - (2 * DESIRED_PADDING));
		int height = getValueOfVariable("height") - ((paddingHeight) - (2 * DESIRED_PADDING));
		double quadWidth = width * horizontalPerPixelSize;
		double quadHeight = height * verticalPerPixelSize;
		double xTexSize = (double) width / imageSize;
		double yTexSize = (double) height / imageSize;
		double xOff = (getValueOfVariable("xoffset") + padding[PAD_LEFT] - DESIRED_PADDING) * horizontalPerPixelSize;
		double yOff = (getValueOfVariable("yoffset") + (padding[PAD_TOP] - DESIRED_PADDING)) * verticalPerPixelSize;
		double xAdvance = (getValueOfVariable("xadvance") - paddingWidth) * horizontalPerPixelSize;
		return new Character(id, xTex, yTex, xTexSize, yTexSize, xOff, yOff, quadWidth, quadHeight, xAdvance);
	}
}
