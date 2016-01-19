package com.chrisali.javaflightsim.aircraft;

import java.util.ArrayList;

import com.chrisali.javaflightsim.propulsion.*;

public class AircraftBuilder {
	private ArrayList<EngineModel> engineList = new ArrayList<>();
	private Aircraft aircraft;
	
	public AircraftBuilder(int numEngines) {
		this.aircraft = new Aircraft();
		
		if (numEngines == 0)
			this.engineList.add(new FixedPitchPropEngine(0, 0, 0, aircraft.enginePosition, 1));
		else if (numEngines > 0 & numEngines < 3) {	
			for (int i = 0; i == numEngines; i++)
				this.engineList.add(new FixedPitchPropEngine());
			}
		else {
			System.err.println("Invalid number of engines! Defaulting to single engine...");
			this.engineList.add(new FixedPitchPropEngine());
		}
	}
	
	public Aircraft getAircraft() {return this.aircraft;}
	
	public ArrayList<EngineModel> getEngineList() {return this.engineList;}
}
