package com.chrisali.javaflightsim.otw.audio;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;
import org.lwjgl.util.vector.Vector3f;

public class AudioMaster {
	
	private static List<Integer> buffers = new ArrayList<Integer>();
	
	public static void init() {
		try {AL.create();} 
		catch (LWJGLException e) {System.err.println("Unable to initialize OpenAL!\n" + e.getMessage());}
	}
	
	public static void setListenerData(Vector3f position, Vector3f  velocity) {
		AL10.alListener3f(AL10.AL_POSITION, position.x, position.y, position.z);
		AL10.alListener3f(AL10.AL_VELOCITY, velocity.x, velocity.y, velocity.z);
	}
	
	public static int loadSound(String directory, String fileName) {
		int buffer = AL10.alGenBuffers();
		buffers.add(buffer);
		
		try {
			WaveData waveFile = WaveData.create(new BufferedInputStream(new FileInputStream("Resources\\" + directory + "\\" + fileName + ".wav")));
			AL10.alBufferData(buffer, waveFile.format, waveFile.data, waveFile.samplerate);
			waveFile.dispose();
		} catch (IOException | NullPointerException e) {
			System.err.println("Could not load sound: " + fileName + ".wav");
			System.err.println(e.getMessage());
		}
		
		return buffer;
	}

	public static void cleanUp() {
		for (int buffer : buffers) {AL10.alDeleteBuffers(buffer);}
		
		AL.destroy();
	}
}
