package com.chrisali.javaflightsim.simulation.aircraft;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.analysis.interpolation.PiecewiseBicubicSplineInterpolatingFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.NullArgumentException;

import com.chrisali.javaflightsim.simulation.aero.Aerodynamics;
import com.chrisali.javaflightsim.simulation.aero.StabilityDerivatives;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.propulsion.Engine;
import com.chrisali.javaflightsim.simulation.propulsion.EngineParameters;
import com.chrisali.javaflightsim.simulation.propulsion.FixedPitchPropEngine;
import com.chrisali.javaflightsim.utilities.FileUtilities;

/**
 * Wrapper class to build a complete aircraft with a "body" ({@link Aircraft}) and a LinkedHashSet of {@link Engine}(s). This object is used by {@link Integrate6DOFEquations}
 * in the simulation of the aircraft. 
 */
public class AircraftBuilder {
	private Set<Engine> engineList = new LinkedHashSet<>();
	private Aircraft aircraft;
	
	protected static final String FILE_PATH = ".\\Aircraft\\";
	
	/**
	 *  Default AircraftBuilder constructor, using the default constructors of {@link Aircraft} and {@link FixedPitchPropEngine}
	 *  to create a generic Navion aircraft with one Lycoming IO-360 engine
	 */
	public AircraftBuilder() {
		this.aircraft = new Aircraft();
		this.engineList.add(new FixedPitchPropEngine());
	}
	
	/**
	 * Custom AircraftBuilder constructor that uses a set of text files in a folder with a name matching the aircraftName argument. This folder is located in:
	 * <br><code>.\Aircraft\</code></br>
	 * An example of the proper file structure and syntax can be seen in the sample aircraft (LookupNavion, Navion and TwinNavion) within this folder.  
	 * @param aircraftName
	 */
	public AircraftBuilder(String aircraftName) {
		this.aircraft = new Aircraft(aircraftName);
		
		List<String[]> readPropulsionFile = FileUtilities.readFileAndSplit(aircraftName, FILE_PATH, "Propulsion");

		// Gets the number of engines on the aircraft from the first line of the
		// String[] ArrayList
		int numEngines = Integer.parseInt(readPropulsionFile.get(0)[1]);
		
		if (numEngines > 0 & numEngines < 5) {	
			for (int i = 1; i <= numEngines; i++) {
				Map<EngineParameters, String> engineParams = new EnumMap<EngineParameters, String>(EngineParameters.class);
				
				// Iterate through propulsion file, assign engine parameters to EnumMap from 
				// lines of readPropulsionFile that match the engine number (engX_1, maxBHP_2, etc)
				// using .startsWith to get the parameter (engX) and .endsWith to get the strapping (1)
				for (String[] line : readPropulsionFile) {
					for (EngineParameters engineParam : EngineParameters.values()) {
						if (line[0].endsWith(String.valueOf(i)) & line[0].startsWith(engineParam.toString()))
							engineParams.put(engineParam, line[1]);
					}
				}
				
				double[] enginePosition = new double[]{Double.parseDouble(engineParams.get(EngineParameters.POS_X)), 
													   Double.parseDouble(engineParams.get(EngineParameters.POS_Y)), 
													   Double.parseDouble(engineParams.get(EngineParameters.POS_Z))};
				
				// Currently only one type of engine, so just default to creating a fixedPitchPropEngine 
				switch (engineParams.get(EngineParameters.TYPE)) {
					case "fixedPitchPropEngine":
					default:
					this.engineList.add(new FixedPitchPropEngine(					engineParams.get(EngineParameters.NAME), 
																 Double.parseDouble(engineParams.get(EngineParameters.MAX_BHP)), 
																 Double.parseDouble(engineParams.get(EngineParameters.MAX_RPM)), 
																 Double.parseDouble(engineParams.get(EngineParameters.PROP_DIAMETER)), 
																 enginePosition, 
																 i));	
					break;
				}
				
			}
		} else {
			System.err.println("Invalid number of engines! Defaulting to single engine...");
			this.engineList.add(new FixedPitchPropEngine());
		}
	}
	
	/**
	 * Parses a text file located in:
	 * <br><code>.\Aircraft\"aircraftName"\LookupTables</code></br>
	 * for text files with the title "fileName".txt. This text file contains a table of values separated by the regular expression ",\t", 
	 * where the first row contains control position breakpoints, the first column contains angle (of attack/sideslip) breakpoints, and 
	 * the rest contains lookup values. It generates a {@link PiecewiseBicubicSplineInterpolatingFunction} from this data, which is used
	 * in @{@link Aircraft} and later in {@link Aerodynamics} to calculate aerodynamic forces and moments for the simulation 
	 * @param aircraftName
	 * @param fileName
	 * @return Lookup table of the type PiecewiseBicubicSplineInterpolatingFunction
	 */
	protected static PiecewiseBicubicSplineInterpolatingFunction createLookupTable(Aircraft aircraft, String fileName) {
		StringBuilder sb = new StringBuilder();
		sb.append(FILE_PATH).append(aircraft.getName()).append("\\LookupTables\\").append(fileName).append(".txt");
		
		List<Double[]> readAndSplit = new LinkedList<>();
		PiecewiseBicubicSplineInterpolatingFunction pbsif = null;
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
									
			pbsif = new PiecewiseBicubicSplineInterpolatingFunction(breakPointAngle, breakPointFlap, lookUpValues);									 
			
		} catch (FileNotFoundException e) {
			System.err.println("Could not find: " + fileName + ".txt!");
			createDefaultLookup(fileName);
		}
		catch (IOException e) {
			System.err.println("Could not read: " + fileName + ".txt!");
			createDefaultLookup(fileName);
		}
		catch (NullPointerException e) {
			System.err.println("Bad reference to: " + fileName + ".txt!");
			createDefaultLookup(fileName);
		}
		catch (NumberFormatException e) {
			System.err.println("Error parsing number data from " + fileName + ".txt!");
			createDefaultLookup(fileName);
		}
		catch (DimensionMismatchException e) {
			System.err.println("Lookup table dimensions do not match in " + fileName + ".txt!");
			createDefaultLookup(fileName);
		}
		catch (NonMonotonicSequenceException e) {
			System.err.println("Lookup table breakpoints do not increase monotonically in " + fileName + ".txt!");
			createDefaultLookup(fileName);
		}
		catch (NullArgumentException e) {
			System.err.println("Error parsing data from " + fileName + ".txt!");
			createDefaultLookup(fileName);
		}
		catch (NoDataException e) {
			System.err.println("No lookup table data found in " + fileName + ".txt!");
			createDefaultLookup(fileName);
		}
		
		return pbsif;
	}
	
	/**
	 * If {@link AircraftBuilder#createLookupTable(Aircraft, String)} throws an exception, this method creates a
	 * lookup table of constant values equal to the stability derivative in question. This prevents any errors
	 * down the line with attempting to access an erroneous {@link PiecewiseBicubicSplineInterpolatingFunction}
	 * 
	 * @param fileName
	 * @return Lookup table of the type PiecewiseBicubicSplineInterpolatingFunction
	 */
	private static PiecewiseBicubicSplineInterpolatingFunction createDefaultLookup(String fileName) {
		// Create default aircraft to get constant value for stability derivative in question
		Aircraft aircraft = new Aircraft(); 
		double constStabDerVal = 0.0;
		for (StabilityDerivatives stabDer : StabilityDerivatives.values()) {
			if (stabDer.toString().equals(fileName))
				constStabDerVal = (Double)aircraft.getStabilityDerivative(stabDer);
		}
		
		double[]    breakPointFlap = new double[5];
		double[]    breakPointAngle = new double[15];
		double[][]  lookUpValues = new double[15][5];
		
		// Convert the flap and alpha breakpoints to radians (for now; assuming lookup data will come to program in degrees)
		for (int i=0; i<breakPointFlap.length; i++)
			breakPointFlap[i] = Math.toRadians(10*i);
			
		for (int i=0; i<breakPointAngle.length; i++)
			breakPointAngle[i] = Math.toRadians(-14+2*i);
				
		for (int i=0; i<lookUpValues.length; i++) {
			for (int j=0; j<lookUpValues[0].length; j++)
				lookUpValues[i][j] = constStabDerVal;
		}
		
		System.err.println("\t- Creating default lookup table for " + fileName + "...");
		System.err.println("\t- Beware! Aircraft may not handle as expected!");
		
		return new PiecewiseBicubicSplineInterpolatingFunction(breakPointAngle, breakPointFlap, lookUpValues);
	}
	
	public Aircraft getAircraft() {return this.aircraft;}
	
	public Set<Engine> getEngineList() {return this.engineList;}
	
	/**
	 * @return File path where all aircraft template files and folders are located
	 */
	public static String getFilePath() {return FILE_PATH;}
}
