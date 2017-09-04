/**
 * 
 */
package com.chrisali.javaflightsim.simulation.aero;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.analysis.interpolation.PiecewiseBicubicSplineInterpolatingFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chrisali.javaflightsim.simulation.aircraft.Aircraft;
import com.chrisali.javaflightsim.simulation.aircraft.AircraftBuilder;
import com.chrisali.javaflightsim.simulation.utilities.FileUtilities;
import com.chrisali.javaflightsim.simulation.utilities.SimDirectories;

/**
 * Contains a builder to generate a {@link PiecewiseBicubicSplineInterpolatingFunction}, which simulates a 2D lookup table in JavaFlightSimulator
 * 
 * @author Chris Ali
 *
 */
public class LookupTableBuilder {
	
	private static final Logger logger = LogManager.getLogger(LookupTableBuilder.class);

	private static final String AIRCRAFT_PATH = SimDirectories.AIRCRAFT.toString(); //FileUtilities.FILE_ROOT + File.separator + (For Windows [?])
	
	private static final String LOOKUP_PATH = File.separator + SimDirectories.LOOKUP_TABLE.toString() + File.separator;
	
	/**
	 * Parses a text file located in:
	 * <br><code>Aircraft\"aircraftName"\LookupTables</code></br>
	 * for text files with the title "fileName".txt. This text file contains a table of values separated by the regular expression ",\t", 
	 * where the first row contains control position breakpoints, the first column contains angle (of attack/sideslip) breakpoints, and 
	 * the rest contains lookup values. It generates a {@link PiecewiseBicubicSplineInterpolatingFunction} from this data, which is used
	 * in @{@link Aircraft} and later in {@link Aerodynamics} to calculate aerodynamic forces and moments for the simulation 
	 * @param aircraftName
	 * @param fileName
	 * @return Lookup table of the type PiecewiseBicubicSplineInterpolatingFunction
	 */
	public static LookupTable buildLookupTable(Aircraft aircraft, String fileName) {
		StringBuilder sb = new StringBuilder();
		sb.append(AIRCRAFT_PATH).append(File.separator).append(aircraft.getName())
		  .append(LOOKUP_PATH).append(fileName).append(FileUtilities.CONFIG_EXT);
		
		logger.debug("Creating a lookup table for the stability derivative: " + fileName);
		logger.debug("Opening the configuration file: " + sb.toString());
		
		List<Double[]> readAndSplit = new LinkedList<>();
		LookupTable table = null;
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
			Double[][] splitArray = readAndSplit.toArray(new Double[readAndSplit.size()][readAndSplit.get(1).length]);
			
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
									
			table = new LookupTable(breakPointAngle, breakPointFlap, lookUpValues, 0.0, fileName);									 
			
		} catch (FileNotFoundException e) {
			logger.error("Could not find: " + fileName + FileUtilities.CONFIG_EXT + "!");
			table = buildDefaultLookup(fileName);
		}
		catch (IOException e) {
			logger.error("Could not read: " + fileName + FileUtilities.CONFIG_EXT + "!");
			table = buildDefaultLookup(fileName);
		}
		catch (NullPointerException e) {
			logger.error("Bad reference to: " + fileName + FileUtilities.CONFIG_EXT + "!");
			table = buildDefaultLookup(fileName);
		}
		catch (NumberFormatException e) {
			logger.error("Error parsing number data from " + fileName + FileUtilities.CONFIG_EXT + "!");
			table = buildDefaultLookup(fileName);
		}
		catch (DimensionMismatchException e) {
			logger.error("Lookup table dimensions do not match in " + fileName + FileUtilities.CONFIG_EXT + "!");
			table = buildDefaultLookup(fileName);
		}
		catch (NonMonotonicSequenceException e) {
			logger.error("Lookup table breakpoints do not increase monotonically in " + fileName + FileUtilities.CONFIG_EXT + "!");
			table = buildDefaultLookup(fileName);
		}
		catch (NullArgumentException e) {
			logger.error("Error parsing data from " + fileName + FileUtilities.CONFIG_EXT + "!");
			table = buildDefaultLookup(fileName);
		}
		catch (NoDataException e) {
			logger.error("No lookup table data found in " + fileName + FileUtilities.CONFIG_EXT + "!");
			table = buildDefaultLookup(fileName);
		}
		
		return table;
	}
	
	/**
	 * If {@link AircraftBuilder#buildLookupTable(Aircraft, String)} throws an exception, this method creates a
	 * lookup table of constant values equal to the stability derivative in question. 
	 * 
	 * @param fileName
	 * @return LookupTable
	 */
	private static LookupTable buildDefaultLookup(String fileName) {
		// Create default aircraft to get constant value for stability derivative in question
		Aircraft aircraft = new Aircraft(); 
		double constStabDerVal = 0.0;
		for (StabilityDerivatives stabDer : StabilityDerivatives.values()) {
			if (stabDer.toString().equals(fileName))
				constStabDerVal = aircraft.getStabilityDerivative(stabDer).getValue();
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
		
		logger.warn("\t- Creating default lookup table for " + fileName + "...");
		logger.warn("\t- Beware! Aircraft may not handle as expected!");
		
		return new LookupTable(breakPointAngle, breakPointFlap, lookUpValues, 0.0, fileName);
	}
}
