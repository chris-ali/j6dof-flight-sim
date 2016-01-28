package com.chrisali.javaflightsim.aircraft;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.chrisali.javaflightsim.propulsion.Engine;
import com.chrisali.javaflightsim.propulsion.FixedPitchPropEngine;

public class AircraftBuilder {
	private Set<Engine> engineList = new LinkedHashSet<>();
	private Aircraft aircraft;
	
	private static final String FILE_PATH = ".\\src\\com\\chrisali\\javaflightsim\\aircraft\\AircraftConfigurations\\";
		
	public AircraftBuilder() {
		this.aircraft = new Aircraft();
		this.engineList.add(new FixedPitchPropEngine());
	}
	
	public AircraftBuilder(String aircraftName) {
		this.aircraft = new Aircraft(aircraftName);
		
		List<String[]> readPropulsionFile = readFileAndSplit(aircraftName, "Propulsion");

		// Gets the number of engines on the aircraft from the first line of the
		// String[] ArrayList
		int numEngines = Integer.parseInt(readPropulsionFile.get(0)[1]);
		
		if (numEngines > 0 & numEngines < 5) {	
			for (int i = 1; i <= numEngines; i++) {
				String   engineName     = "Lycoming IO-360";
				double   maxBHP         = 200;
				double   maxRPM         = 2700;
				double   propDiameter   = 6.5; 
				double[] enginePosition = new double[]{0.0, 0.0, 0.0};
				
				// Iterate through propulsion file, assign engine parameters from 
				// lines that match the engine number (engX_1, maxBHP_2, etc) 
				for (String[] line : readPropulsionFile) {
					if (line[0].endsWith(String.valueOf(i)) & line[0].startsWith("engineName"))
						engineName = line[1];
					if (line[0].endsWith(String.valueOf(i)) & line[0].startsWith("maxBHP"))
						maxBHP = Double.parseDouble(line[1]);
					if (line[0].endsWith(String.valueOf(i)) & line[0].startsWith("maxRPM"))
						maxRPM = Double.parseDouble(line[1]);
					if (line[0].endsWith(String.valueOf(i)) & line[0].startsWith("propDiameter"))
						propDiameter = Double.parseDouble(line[1]);
					if (line[0].endsWith(String.valueOf(i)) & line[0].startsWith("engX"))
						enginePosition[0] = Double.parseDouble(line[1]);
					if (line[0].endsWith(String.valueOf(i)) & line[0].startsWith("engY"))
						enginePosition[1] = Double.parseDouble(line[1]);
					if (line[0].endsWith(String.valueOf(i)) & line[0].startsWith("engZ"))
						enginePosition[2] = Double.parseDouble(line[1]);
				}
								
				this.engineList.add(new FixedPitchPropEngine(engineName, 
															 maxBHP, 
															 maxRPM, 
															 propDiameter, 
															 enginePosition, 
															 i));
			}
		} else {
			System.err.println("Invalid number of engines! Defaulting to single engine...");
			this.engineList.add(new FixedPitchPropEngine());
		}
	}
	
	protected static ArrayList<String[]> readFileAndSplit(String aircraftName, String fileContents) {
		StringBuilder sb = new StringBuilder();
		sb.append(FILE_PATH).append(aircraftName).append("\\").append(fileContents).append(".txt");
		ArrayList<String[]> readAndSplit = new ArrayList<>();
		String readLine = null;
		
		try (BufferedReader br = new BufferedReader(new FileReader(sb.toString()))) {
			while ((readLine = br.readLine()) != null)
				readAndSplit.add(readLine.split(" = "));
		} catch (FileNotFoundException e) {System.err.println("Could not find: " + fileContents + ".txt!");}
		catch (IOException e) {System.err.println("Could not read: " + fileContents + ".txt!");}
		catch (NullPointerException e) {System.err.println("Bad reference to: " + fileContents + ".txt!");} 
		
		return readAndSplit;
	}
	
	public Aircraft getAircraft() {return this.aircraft;}
	
	public Set<Engine> getEngineList() {return this.engineList;}
	
	public static String getFilePath() {return FILE_PATH;}
}
