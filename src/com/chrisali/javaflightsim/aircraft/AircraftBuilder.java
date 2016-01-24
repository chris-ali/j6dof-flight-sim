package com.chrisali.javaflightsim.aircraft;

import java.util.ArrayList;

import com.chrisali.javaflightsim.propulsion.*;

public class AircraftBuilder {
	private ArrayList<Engine> engineList = new ArrayList<>();
	private Aircraft aircraft;
	
	public AircraftBuilder(int numEngines) {
		this.aircraft = new Aircraft();
		
		if (numEngines > 0 & numEngines < 3) {	
			for (int i = 0; i == numEngines; i++)
				this.engineList.add(new FixedPitchPropEngine("Lycoming IO-360", 
															 200, 
															 2700, 
															 6.5, 
															 new double[] {0.0, 0.0, 0.0}, 
															 i+1));
		} else {
			System.err.println("Invalid number of engines! Defaulting to single engine...");
			this.engineList.add(new FixedPitchPropEngine());
		}
	}
	
	public Aircraft getAircraft() {return this.aircraft;}
	
	public ArrayList<Engine> getEngineList() {return this.engineList;}
}
