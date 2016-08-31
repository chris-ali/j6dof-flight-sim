package com.chrisali.javaflightsim.tests;

import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import com.chrisali.javaflightsim.controllers.SimulationController;
import com.chrisali.javaflightsim.otw.audio.AudioMaster;
import com.chrisali.javaflightsim.otw.audio.SoundCollection;
import com.chrisali.javaflightsim.otw.audio.SoundCollection.SoundCategory;
import com.chrisali.javaflightsim.otw.audio.SoundCollection.SoundEvent;
import com.chrisali.javaflightsim.otw.audio.SoundSource;

public class AudioTest {

	public static void main(String[] args) {
		AudioTest test = new AudioTest();
		
		test.AudioTestLoop();
		test.AudioTestMovement();
		test.AudioTestPitch();
		test.AudioTestRPM();
		test.AudioTestFlaps();
		test.AudioTestGear();
		test.AudioTestStall();
		test.AudioTestWind();
		
		test.finish();
	}
	
	private Vector3f playerPostion = new Vector3f(0, 0, 0);
	private Vector3f playerVelocity = new Vector3f(0, 0, 0);
	
	private Vector3f sourcePosition = new Vector3f(8, 0, 2);
	private Vector3f sourceVelocity = new Vector3f(-0.02f, 0, 0);
	
	private SimulationController controller = new SimulationController();
	
	private Map<String, SoundSource> soundSources = new HashMap<>();
	private Map<SoundCategory, Double> soundValues = new EnumMap<>(SoundCategory.class);
	
	private AudioTest() {
		AudioMaster.init();
		AudioMaster.setListenerData(playerPostion, playerVelocity);
		
		soundSources.put("wind", new SoundSource("Audio", "wind"));
		soundSources.get("wind").setLooping(true);
		
		soundSources.put("windHigh", new SoundSource("Audio", "wind"));
		soundSources.get("windHigh").setPitch(2);
		
		soundValues.put(SoundCategory.FLAPS, 0.0);
		soundValues.put(SoundCategory.PREV_STEP_FLAPS, 0.0);
		soundValues.put(SoundCategory.GEAR, 0.0);
		soundValues.put(SoundCategory.PREV_STEP_GEAR, 0.0);
	}
	
	private void AudioTestLoop() {
		System.out.println("Type \'p\' to pause/resume loop. \'o\' plays a higher pitch sound and \'q\' quits looping test");
		
		soundSources.get("wind").play();
		
		char in = ' ';
		
		while (in != 'q') {
			try {in = (char) System.in.read();} 
			catch (IOException e) {System.err.println("Invalid command!");}
			
			switch (in){
			case 'p':
				if (soundSources.get("wind").isPlaying())
					soundSources.get("wind").pause();
				else
					soundSources.get("wind").resume();
				break;
			case 'o':
				
				soundSources.get("windHigh").play();
				break;
			default:
				break;
			}
		}
	}
	
	private void AudioTestMovement() {
		System.out.println("Starting moving sound test");
		
		int counter = 0, dT = 10;
		
		while (counter < 10000) {
			sourcePosition.x += sourceVelocity.x;
			soundSources.get("wind").setPosition(sourcePosition);
			
			if (Math.abs(sourcePosition.x) >= 9)
				sourceVelocity.x *= -1;
			
			try {Thread.sleep(dT);} 
			catch (InterruptedException e) {}
			
			counter += dT;
		}
		
		soundSources.get("wind").stop();
		
		System.out.println("Done!");
	}
	
	private void AudioTestPitch() {
		System.out.println("Starting sound pitch test");
		
		soundSources.get("wind").play();
		
		int counter = 0, dT = 10;
		float pitch = 0.5f;
		while (counter < 5000) {
			pitch = (counter/5000f)+0.5f;
			soundSources.get("wind").setPitch(pitch);
			
			try {Thread.sleep(dT);} 
			catch (InterruptedException e) {}
			
			counter += dT;
		}
		
		soundSources.get("wind").stop();
		
		System.out.println("Done!");
	}
	
	private void AudioTestRPM() {
		System.out.println("Starting RPM Test");
		
		SoundCollection.initializeSounds(controller);
		
		soundValues.put(SoundCategory.RPM_1, 500.0);
		
		float rpm = 500, dT = 1;
		while (rpm < 2700) {
			soundValues.put(SoundCategory.RPM_1, (double) rpm);
			
			SoundCollection.setRPM(controller.getAircraftBuilder(), soundValues);
			
			try {Thread.sleep((int)dT * 5);} 
			catch (InterruptedException e) {}
			
			rpm += dT;
		}
		
		try {Thread.sleep(5000);} 
		catch (InterruptedException e) {}
		
		SoundCollection.cleanUp();
		
		System.out.println("Done!");
	}
	
	private void AudioTestGear() {
		System.out.println("Starting Gear Test");
		
		SoundCollection.initializeSounds(controller);
		
		soundValues.put(SoundCategory.PREV_STEP_GEAR, 0.0);
		soundValues.put(SoundCategory.GEAR, 1.0);
		
		SoundCollection.setControl(SoundEvent.GEAR, soundValues);
		
		soundValues.put(SoundCategory.PREV_STEP_GEAR, 1.0);
		
		try {Thread.sleep(5000);} 
		catch (InterruptedException e) {}
		
		soundValues.put(SoundCategory.PREV_STEP_GEAR, 1.0);
		soundValues.put(SoundCategory.GEAR, 0.0);
		
		SoundCollection.setControl(SoundEvent.GEAR, soundValues);
		
		soundValues.put(SoundCategory.PREV_STEP_GEAR, 0.0);
		
		SoundCollection.cleanUp();
		
		System.out.println("Done!");
	}
	
	private void AudioTestFlaps() {
		System.out.println("Starting Flaps Test");
		
		SoundCollection.initializeSounds(controller);
		
		for (double flaps = 0; flaps < 30; flaps += 1.0) {
			
			soundValues.put(SoundCategory.FLAPS, flaps);
			
			SoundCollection.setControl(SoundEvent.FLAPS, soundValues);
			
			soundValues.put(SoundCategory.PREV_STEP_FLAPS, flaps-1.0);
			
			try {Thread.sleep(200);} 
			catch (InterruptedException e) {}
		}
		
		soundValues.put(SoundCategory.PREV_STEP_FLAPS, 30.0);
		
		try {Thread.sleep(1000);} 
		catch (InterruptedException e) {}
		
		for (double flaps = 30; flaps > 0; flaps -= 1.0) {

			soundValues.put(SoundCategory.FLAPS, flaps);
			
			SoundCollection.setControl(SoundEvent.FLAPS, soundValues);
			
			soundValues.put(SoundCategory.PREV_STEP_FLAPS, flaps+1.0);
			
			try {Thread.sleep(200);} 
			catch (InterruptedException e) {}
		}
		
		soundValues.put(SoundCategory.PREV_STEP_FLAPS, 0.0);
		
		SoundCollection.cleanUp();
		
		System.out.println("Done!");
	}
	
	private void AudioTestStall() {
		System.out.println("Starting Stall Test");
		
		SoundCollection.initializeSounds(controller);
		
		for (float alpha = 0; alpha < 0.5f; alpha += 0.02) {
			SoundCollection.setStallHorn(alpha, 0.25f);
			
			try {Thread.sleep(20);} 
			catch (InterruptedException e) {}
		}
		
		try {Thread.sleep(1000);} 
		catch (InterruptedException e) {}
		
		for (float alpha = 0.5f; alpha > 0.0f; alpha -= 0.02) {
			SoundCollection.setStallHorn(alpha, 0.25f);
			
			try {Thread.sleep(200);} 
			catch (InterruptedException e) {}
		}
		
		SoundCollection.cleanUp();
		
		System.out.println("Done!");
	}
	
	private void AudioTestWind() {
		System.out.println("Starting Wind Test");
		
		SoundCollection.initializeSounds(controller);
		
		for (float trueAirspeed = 30; trueAirspeed < 300; trueAirspeed += 1.0) {
			SoundCollection.setWind(trueAirspeed);
			
			try {Thread.sleep(50);} 
			catch (InterruptedException e) {}
		}
		
		try {Thread.sleep(1000);} 
		catch (InterruptedException e) {}
		
		SoundCollection.cleanUp();
		
		System.out.println("Done!");
	} 
	
	private void finish() {
		for (Map.Entry<String, SoundSource> entry : soundSources.entrySet())
			entry.getValue().delete();
		
		AudioMaster.cleanUp();
	}
}
