package com.chrisali.javaflightsim.tests;

import com.chrisali.javaflightsim.otw.RunWorld;

public class WorldTest {
	
	public static void main(String[] args) {
		RunWorld world = new RunWorld();
		Thread worldThread = new Thread(world);
		worldThread.start();
	}
	
}
