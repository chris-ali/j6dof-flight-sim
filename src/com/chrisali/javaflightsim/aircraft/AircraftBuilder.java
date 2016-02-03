package com.chrisali.javaflightsim.aircraft;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.analysis.interpolation.PiecewiseBicubicSplineInterpolatingFunction;

import com.chrisali.javaflightsim.aero.Aerodynamics;
import com.chrisali.javaflightsim.propulsion.Engine;
import com.chrisali.javaflightsim.propulsion.FixedPitchPropEngine;
import com.chrisali.javaflightsim.utilities.integration.Integrate6DOFEquations;

/**
 * Wrapper class to build a complete aircraft with a "body" ({@link Aircraft}) and a LinkedHashSet of {@link Engine}(s). This object is used by {@link Integrate6DOFEquations}
 * in the simulation of the aircraft. 
 */
public class AircraftBuilder {
	private Set<Engine> engineList = new LinkedHashSet<>();
	private Aircraft aircraft;
	
	private static final String FILE_PATH = ".\\src\\com\\chrisali\\javaflightsim\\aircraft\\AircraftConfigurations\\";
	
	/**
	 *  Default AircraftBuilder constructor, using the default constructors of {@link Aircraft} and {@link FixedPitchPropEngine}
	 *  to create a generic Navion aircraft with one Lycoming IO-360 engine
	 */
	public AircraftBuilder() {
		this.aircraft = new Aircraft();
		this.engineList.add(new FixedPitchPropEngine());
	}
	
	/**
	 * Custom AircraftBuilder constructor that uses a set of text files in a folder with a name matching the aircraftName argument. This folder is located in
	 * <br><code>.\src\com\chrisali\javaflightsim\aircraft\AircraftConfigurations\</code></br>
	 * An example of the proper file structure and syntax can be seen in the sample aircraft (LookupNavion, Navion and TwinNavion) within this folder.  
	 * @param aircraftName
	 */
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
	
	/**
	 * Splits a text file of the name "fileContents".txt located in the folder: 
	 *  <br><code>.\src\com\chrisali\javaflightsim\aircraft\AircraftConfigurations\"aircraftName"</code></br>
	 *  whose general syntax on each line is:
	 *  <br><code>*parameter name* = *double value*</code></br>
	 *  into an ArrayList of string arrays resembling:
	 *  <br><code>{*parameter name*,*double value*}</code></br>
	 *  
	 * @param aircraftName
	 * @param fileContents
	 * @return An ArrayList of String arrays of length 2  
	 */
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
	
	/**
	 * Parses a text file located in:
	 * <br><code>.\src\com\chrisali\javaflightsim\aircraft\AircraftConfigurations\"aircraftName"\LookupTables</code></br>
	 * for text files with the title "fileName".txt. This text file contains a table of values separated by the regular expression ",\t", 
	 * where the first row contains control position breakpoints, the first column contains angle (of attack/sideslip) breakpoints, and 
	 * the rest contains lookup values. It generates a {@link PiecewiseBicubicSplineInterpolatingFunction} from this data, which is used
	 * in @{@link Aircraft} and later in {@link Aerodynamics} to calculate aerodynamic forces and moments for the simulation 
	 * @param aircraftName
	 * @param fileName
	 * @return Lookup table of the type PiecewiseBicubicSplineInterpolatingFunction
	 */
	protected static PiecewiseBicubicSplineInterpolatingFunction createLookupTable(String aircraftName, String fileName) {
		StringBuilder sb = new StringBuilder();
		sb.append(FILE_PATH).append(aircraftName).append("\\LookupTables\\").append(fileName).append(".txt");
		
		List<Double[]> readAndSplit = new LinkedList<>();
		PiecewiseBicubicSplineInterpolatingFunction pbsi = null;
		String readLine = null;
		
		try (BufferedReader br = new BufferedReader(new FileReader(sb.toString()))) {
			// Read each line of file, split it into String array, convert it to a double array and add to double list
			while ((readLine = br.readLine()) != null) {
				String[] tempLineString = readLine.split(",\t");
				Double[] tempLineDouble = new Double[tempLineString.length];
				
				for (int i=0; i<tempLineString.length; i++)
					tempLineDouble[i] = Double.parseDouble(tempLineString[i]);
				
				readAndSplit.add(tempLineDouble);
			}
			
			// Take readAndSplit list and convert it to a 2D double array
			Double[][]splitArray = readAndSplit.toArray(new Double[readAndSplit.size()][readAndSplit.get(1).length]);
			
			// Break up 2D array into 2x arrays for lookup breakpoints, and 1 smaller 2D array of lookup values
			double[]   breakPointFlap  = new double[splitArray[0].length];
			double[]   breakPointAngle = new double[splitArray.length-1];
			double[][] lookUpValues    = new double[splitArray.length-1][splitArray[1].length-1];
			
			// Convert the flap and alpha breakpoints to radians (for now; assuming lookup data will come to program in degrees)
			for (int i=0; i<breakPointFlap.length; i++)
				breakPointFlap[i] = Math.toRadians(splitArray[0][i]);
				
			for (int i=1; i<=breakPointAngle.length; i++)
				breakPointAngle[i-1] = Math.toRadians(splitArray[i][0]);
					
			for (int i=1; i<=lookUpValues.length; i++) {
				for (int j=1; j<=lookUpValues[0].length; j++)
					lookUpValues[i-1][j-1] = splitArray[i][j];
			}
									
			pbsi = new PiecewiseBicubicSplineInterpolatingFunction(breakPointAngle, breakPointFlap, lookUpValues);									 
			
		} catch (FileNotFoundException e) {System.err.println("Could not find: " + fileName + ".txt!");}
		catch (IOException e) {System.err.println("Could not read: " + fileName + ".txt!");}
		catch (NullPointerException e) {System.err.println("Bad reference to: " + fileName + ".txt!");} 
		
		return pbsi;
	}
	
	public Aircraft getAircraft() {return this.aircraft;}
	
	public Set<Engine> getEngineList() {return this.engineList;}
	
	/**
	 * @return File path where all aircraft template files and folders are located
	 */
	public static String getFilePath() {return FILE_PATH;}
}
