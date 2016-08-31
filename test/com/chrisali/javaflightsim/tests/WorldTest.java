package com.chrisali.javaflightsim.tests;

import com.chrisali.javaflightsim.controllers.SimulationController;
import com.chrisali.javaflightsim.otw.RunWorld;

public class WorldTest {
	
	public static void main(String[] args) {
		Thread worldThread = new Thread(new RunWorld(new SimulationController()));
		worldThread.start();
	}
	
}
