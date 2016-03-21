package com.chrisali.javaflightsim.utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.chrisali.javaflightsim.simulation.aircraft.MassProperties;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.integration.SimOuts;
import com.chrisali.javaflightsim.simulation.setup.Options;

/**
 * Contains various static methods for unboxing arrays into primitive types and reading and parsing text files into lists
 */
public class Utilities {

	/**
	 * Unboxes Double[] array into a double[] array; {@link Integrate6DOFEquations} needs primitive arrays, 
	 * necessitating this method
	 * @param map
	 * @return Unboxed double[] array
	 */
	public static double[] unboxDoubleArray(EnumMap<?, Double> map) {
		double[] unboxedArray = new double[map.values().size()]; 
		for (int i = 0; i < unboxedArray.length; i++)
			unboxedArray[i] = map.values().toArray(new Double[unboxedArray.length])[i];
		return unboxedArray;
	}
	
	/**
	 * Unboxes Double[] array into a double[] array; {@link Integrate6DOFEquations} needs primitive arrays, 
	 * necessitating this method
	 * @param boxed
	 * @return Unboxed double[] array
	 */
	public static double[] unboxDoubleArray(Double[] boxed) {
		double[] unboxedArray = new double[boxed.length]; 
		for (int i = 0; i < unboxedArray.length; i++)
			unboxedArray[i] = boxed[i];
		return unboxedArray;
	}
	
	/**
	 * Splits a config file called "fileContents".txt located in the folder 
	 * specified by filePath whose general syntax on each line is:
	 *  <br><code>*parameter name* = *double value*</code></br>
	 *  into an ArrayList of string arrays resembling:
	 *  <br><code>{*parameter name*,*double value*}</code></br>
	 *  
	 * @param aircraftName
	 * @param filePath
	 * @param fileContents
	 * @return An ArrayList of String arrays of length 2  
	 */
	public static ArrayList<String[]> readFileAndSplit(String aircraftName, String filePath, String fileContents) {
		StringBuilder sb = new StringBuilder();
		sb.append(filePath).append(aircraftName).append("\\").append(fileContents).append(".txt");
		ArrayList<String[]> readAndSplit = new ArrayList<>();
		String readLine = null;
		
		try (BufferedReader br = new BufferedReader(new FileReader(sb.toString()))) {
			while ((readLine = br.readLine()) != null)
				readAndSplit.add(readLine.split(" = "));
		} catch (FileNotFoundException e) {System.err.println("Could not find: " + fileContents + ".txt!");}
		catch (IOException e) {System.err.println("Could not read: " + fileContents + ".txt!");}
		catch (NullPointerException e) {System.err.println("Bad reference when reading: " + fileContents + ".txt!");} 
		catch (NumberFormatException e) {System.err.println("Error parsing data from " + fileContents + ".txt!");}
		
		return readAndSplit;
	}
	
	/**
	 * Splits a config file called "fileName".txt located in the folder 
	 * specified by filePath whose general syntax on each line is:
	 *  <br><code>*parameter name* = *double value*</code></br>
	 *  into an ArrayList of string arrays resembling:
	 *  <br><code>{*parameter name*,*double value*}</code></br>
	 *  
	 * @param fileName
	 * @param filePath
	 * @return An ArrayList of String arrays of length 2  
	 */
	public static ArrayList<String[]> readFileAndSplit(String fileName, String filePath) {
		StringBuilder sb = new StringBuilder();
		sb.append(filePath).append(fileName).append(".txt");
		ArrayList<String[]> readAndSplit = new ArrayList<>();
		String readLine = null;
		
		try (BufferedReader br = new BufferedReader(new FileReader(sb.toString()))) {
			while ((readLine = br.readLine()) != null)
				readAndSplit.add(readLine.split(" = "));
		} catch (FileNotFoundException e) {System.err.println("Could not find: " + fileName + ".txt!");}
		catch (IOException e) {System.err.println("Could not read: " + fileName + ".txt!");}
		catch (NullPointerException e) {System.err.println("Bad reference when reading: " + fileName + ".txt!");}
		catch (NumberFormatException e) {System.err.println("Error parsing data from " + fileName + ".txt!");}
		
		return readAndSplit;
	}
	
	/**
	 * Creates a config file called "fileName".txt located in the folder 
	 * specified by filePath using an EnumMap where each line is written as:
	 *  <br><code>"*parameter name* = *double value*\n"</code></br>
	 *  
	 * @param fileName
	 * @param filePath
	 * @param enumMap
	 */
	public static void writeConfigFile(String fileName, String filePath, Map<?, Double> enumMap) {
		StringBuilder sb = new StringBuilder();
		sb.append(filePath).append(fileName).append(".txt");
		
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(sb.toString()))) {
			for (Map.Entry<?,Double> entry : enumMap.entrySet()) {
				bw.write(entry.getKey().toString() + " = " + entry.getValue());
				bw.newLine();
			}
		} catch (FileNotFoundException e) {System.err.println("Could not find: " + fileName + ".txt!");}
		catch (IOException e) {System.err.println("Could not read: " + fileName + ".txt!");}
		catch (NullPointerException e) {System.err.println("Bad reference when reading: " + fileName + ".txt!");}
		catch (NumberFormatException e) {System.err.println("Error parsing data from " + fileName + ".txt!");}
	}
	
	/**
	 * Creates a config file called "fileName".txt located in the folder specified by filePath 
	 * using the opstionsSet EnumSet of selected options and the selected aircraft's name,
	 * where each line is written as  <br><code>"*parameter* = *value*\n"</code></br>
	 *  
	 * @param optionsSet
	 * @param selectedAircraft
	 * @param enumMap
	 */
	public static void writeConfigFile(String fileName, String filePath, Set<Options> optionsSet, String selectedAircraft) {
		StringBuilder sb = new StringBuilder();
		sb.append(filePath).append(fileName).append(".txt");
		
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(sb.toString()))) {
			for (Options option : Options.values()) {
				bw.write(option.name() + " = " + optionsSet.contains(option));
				bw.newLine();
			}
			bw.write("selectedAircraft = " + selectedAircraft);
			bw.newLine();
		} catch (FileNotFoundException e) {System.err.println("Could not find: SimulationSetup.txt!");}
		catch (IOException e) {System.err.println("Could not read: SimulationSetup.txt!");}
		catch (NullPointerException e) {System.err.println("Bad reference to: SimulationSetup.txt!");}
	}
	
	/**
	 * Parses a config file called SimulationSetup.txt located in .\\SimConfig\\ 
	 * where each line is written as  <br><code>"*parameter* = *value*\n"</code></br>
	 * and returns an EnumSet containing enums from Options for each line in the
	 * file where *value* contains true  
	 * 
	 * @return EnumSet of selected options
	 */
	public static EnumSet<Options> parseSimSetupFile() throws IllegalArgumentException {
		ArrayList<String[]> readSimSetupFile = readFileAndSplit("SimulationSetup", ".\\SimConfig\\");
		EnumSet<Options> options = EnumSet.noneOf(Options.class);
		
		for (String[] readLine : readSimSetupFile) {
			if (readLine[1].compareTo("true") == 0)
				options.add(Options.valueOf(readLine[0]));
		}
		
		return options;
	}
	
	/**
	 * Parses the MassProperties.txt file in .\Aircraft\aircraftName and returns an EnumMap with {@link MassProperties}
	 * as the keys
	 * 
	 * @param aircraftName
	 * @return massProperties EnumMap
	 */
	public static EnumMap<MassProperties, Double> parseMassProperties(String aircraftName) {
		EnumMap<MassProperties, Double> massProperties = new EnumMap<MassProperties, Double>(MassProperties.class);
		
		// Mass Properties
		ArrayList<String[]> readMassPropFile = readFileAndSplit(aircraftName, ".//Aircraft//", "MassProperties");
		
		for(MassProperties massPropKey : MassProperties.values()) {
			for (String[] readLine : readMassPropFile) {
				if (massPropKey.toString().equals(readLine[0]))
					massProperties.put(massPropKey, Double.parseDouble(readLine[1]));
			}
		}

		return massProperties;
	}
	
	/**
	 * @param fileName
	 * @return string containing the file's extension 
	 */
	public static String getFileExtension(String fileName) {
		int periodLocation = fileName.lastIndexOf(".");
		
		if (periodLocation == -1)
			return "";
		else if (periodLocation == fileName.length()-1)
			return "";
		else 
			return fileName.substring(periodLocation+1, fileName.length());
	}
	
	/**
	 * Writes a CSV file from data contained within the logsOut ArrayList 
	 * 
	 * @param file
	 * @param logsOut
	 * @throws IOException
	 */
	public static void saveToCSVFile(File file, List<EnumMap<SimOuts, Double>> logsOut) throws IOException {
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(file.getPath()));
		
		// First line of CSV file should have the names of each parameter
		StringBuilder sb_line1 = new StringBuilder();
		for (SimOuts simOut : SimOuts.values()) {
			sb_line1.append(simOut.toString()).append(",");
		}
		bw.write(sb_line1.append("\n").toString());
		
		// Subsequent lines contain data
		for (EnumMap<SimOuts, Double> simOut : logsOut) {
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<?,Double> entry : simOut.entrySet()) {
				sb.append(entry.getValue().toString()).append(",");
			}
			bw.write(sb.append("\n").toString());
		}
		
		bw.close();
	}
	
	/**
	 * @param knots
	 * @return Airspeed converted from knots to ft/sec
	 */
	public static double toFtPerSec(double knots) {return knots*1.687810;}
	
	/**
	 * @param knots
	 * @return Airspeed converted from ft/sec to knots
	 */
	public static double toKnots(double ftPerSec) {return ftPerSec/1.687810;}
}
