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
package com.chrisali.javaflightsim.lwjgl.audio;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.openal.EXTThreadLocalContext;
import org.lwjgl.util.WaveData;
import org.lwjgl.util.vector.Vector3f;

import static org.lwjgl.openal.ALC11.*;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.system.MemoryUtil.*;

import com.chrisali.javaflightsim.lwjgl.utilities.OTWDirectories;
import com.chrisali.javaflightsim.lwjgl.utilities.OTWFiles;

public class AudioMaster {
	
	private static final Logger logger = LogManager.getLogger(AudioMaster.class);

	/**
	 * Reference to the OpenAL Capabilities of this thread
	 */
	private static ALCapabilities alCapabilities;
	
	private static List<Integer> buffers = new ArrayList<Integer>();

	private static long device;
	private static long context;
	
	public static void init() {
		try {
			device = alcOpenDevice((ByteBuffer) null);

			if (device == NULL)
				throw new IllegalStateException("Failed to open the default device.");

			ALCCapabilities capabilities = ALC.createCapabilities(device);
			
			if (!capabilities.OpenALC10)
				throw new IllegalStateException("Failed to open the default device.");
			
			context = alcCreateContext(device, (IntBuffer) null);
				
			EXTThreadLocalContext.alcSetThreadContext(context);
			
			alCapabilities = AL.createCapabilities(capabilities);
		
			if(alcMakeContextCurrent(context))
				logger.info("Successfully started OpenAL context for device:" + device);

		} 
		catch (Exception e) {
			logger.error("Unable to initialize OpenAL!", e);
		}
	}
	
	public static void setListenerData(Vector3f position, Vector3f  velocity) {
		alListener3f(AL_POSITION, position.x, position.y, position.z);
		alListener3f(AL_VELOCITY, velocity.x, velocity.y, velocity.z);
	}
	
	public static int loadSound(String directory, String fileName) {
		int buffer = alGenBuffers();
		buffers.add(buffer);
				
		try {
			String path = OTWDirectories.RESOURCES.toString() + File.separator + directory + File.separator + fileName + OTWFiles.SOUND_EXT.toString();
			WaveData waveFile = WaveData.create(new BufferedInputStream(new FileInputStream(path)));
			alBufferData(buffer, waveFile.format, waveFile.data, waveFile.samplerate);
			waveFile.dispose();
		} catch (IOException | NullPointerException e) {
			logger.error("Could not load sound: " + fileName + OTWFiles.SOUND_EXT.toString() + "!", e);
		}
		
		return buffer;
	}

	public static void cleanUp() {
		for (int buffer : buffers) 
			alDeleteBuffers(buffer);

		alcMakeContextCurrent(NULL);
		AL.setCurrentThread(null);
		alcDestroyContext(context);
		alcCloseDevice(device);
	}

	public static ALCapabilities getAlCapabilities() {
		return alCapabilities;
	}
}
