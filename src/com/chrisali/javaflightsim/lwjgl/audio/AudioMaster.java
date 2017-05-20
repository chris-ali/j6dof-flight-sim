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
package com.chrisali.javaflightsim.lwjgl.audio;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;
import org.lwjgl.util.vector.Vector3f;

import com.chrisali.javaflightsim.lwjgl.utilities.OTWDirectories;
import com.chrisali.javaflightsim.lwjgl.utilities.OTWFiles;

public class AudioMaster {
	
	private static final Logger logger = LogManager.getLogger(AudioMaster.class);
	
	private static List<Integer> buffers = new ArrayList<Integer>();
	
	public static void init() {
		try {AL.create();} 
		catch (LWJGLException e) {logger.error("Unable to initialize OpenAL!"); logger.error(e.getMessage());}
	}
	
	public static void setListenerData(Vector3f position, Vector3f  velocity) {
		AL10.alListener3f(AL10.AL_POSITION, position.x, position.y, position.z);
		AL10.alListener3f(AL10.AL_VELOCITY, velocity.x, velocity.y, velocity.z);
	}
	
	public static int loadSound(String directory, String fileName) {
		int buffer = AL10.alGenBuffers();
		buffers.add(buffer);
				
		try {
			String path = OTWDirectories.RESOURCES.toString() + File.separator + directory + File.separator + fileName + OTWFiles.SOUND_EXT.toString();
			WaveData waveFile = WaveData.create(new BufferedInputStream(new FileInputStream(path)));
			AL10.alBufferData(buffer, waveFile.format, waveFile.data, waveFile.samplerate);
			waveFile.dispose();
		} catch (IOException | NullPointerException e) {
			logger.error("Could not load sound: " + fileName + OTWFiles.SOUND_EXT.toString() + "!");
			logger.error(e.getMessage());
		}
		
		return buffer;
	}

	public static void cleanUp() {
		for (int buffer : buffers) {AL10.alDeleteBuffers(buffer);}
		
		AL.destroy();
	}
}
