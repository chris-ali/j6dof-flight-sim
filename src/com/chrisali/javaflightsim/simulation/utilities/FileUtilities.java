/*******************************************************************************
 * Copyright (C) 2016-2017 Christopher Ali
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *  If you have any questions about this project, you can visit
 *  the project's GitHub repository at: http://github.com/chris-ali/j6dof-flight-sim/
 ******************************************************************************/
package com.chrisali.javaflightsim.simulation.utilities;

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
import com.chrisali.javaflightsim.simulation.integration.SimOuts;
import com.chrisali.javaflightsim.simulation.setup.Options;
import com.chrisali.javaflightsim.swing.optionspanel.AudioOptions;
import com.chrisali.javaflightsim.swing.optionspanel.DisplayOptions;

/**
 * Contains various static methods for reading and parsing text configuration files
 */
public class FileUtilities {

	public static final String CONFIG_EXT = ".txt";

	public static final String FILE_ROOT = ""; //"." + File.separator;
	
	//===================================================================================================
	//										File Reading
	//===================================================================================================
	
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
		sb.append(FILE_ROOT).append(filePath).append(File.separator).append(aircraftName).append(File.separator).append(fileContents).append(CONFIG_EXT);
		ArrayList<String[]> readAndSplit = new ArrayList<>();
		String readLine = null;
		
		try (BufferedReader br = new BufferedReader(new FileReader(sb.toString()))) {
			while ((readLine = br.readLine()) != null)
				readAndSplit.add(readLine.split(" = "));
		} catch (FileNotFoundException e) {System.err.println("Could not find: " + fileContents + CONFIG_EXT + "!");}
		catch (IOException e) {System.err.println("Could not read: " + fileContents + CONFIG_EXT + "!");}
		catch (NullPointerException e) {System.err.println("Bad reference when reading: " + fileContents + CONFIG_EXT + "!");} 
		catch (NumberFormatException e) {System.err.println("Error parsing data from " + fileContents + CONFIG_EXT + "!");}
		
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
		sb.append(FILE_ROOT).append(filePath).append(File.separator).append(fileName).append(CONFIG_EXT);
		ArrayList<String[]> readAndSplit = new ArrayList<>();
		String readLine = null;
		
		try (BufferedReader br = new BufferedReader(new FileReader(sb.toString()))) {
			while ((readLine = br.readLine()) != null)
				readAndSplit.add(readLine.split(" = "));
		} catch (FileNotFoundException e) {System.err.println("Could not find: " + fileName + CONFIG_EXT + "!");}
		catch (IOException e) {System.err.println("Could not read: " + fileName + CONFIG_EXT + "!");}
		catch (NullPointerException e) {System.err.println("Bad reference when reading: " + fileName + CONFIG_EXT + "!");}
		catch (NumberFormatException e) {System.err.println("Error parsing data from " + fileName + CONFIG_EXT + "!");}
		
		return readAndSplit;
	}
	
	/**
	 * Parses a config file called SimulationSetup.txt located in SimConfig\
	 * where each line is written as  <br><code>"*parameter* = *value*\n"</code></br>
	 * and returns an EnumSet containing enums from Options for each line in the
	 * file where *value* contains true  
	 * 
	 * @return EnumSet of selected options
	 */
	public static EnumSet<Options> parseSimulationSetup() throws IllegalArgumentException {
		ArrayList<String[]> readSimSetupFile = readFileAndSplit(SimFiles.SIMULATION_SETUP.toString(), SimDirectories.SIM_CONFIG.toString());
		EnumSet<Options> options = EnumSet.noneOf(Options.class);
		
		for (String[] readLine : readSimSetupFile) {
			if (readLine[1].compareTo("true") == 0)
				options.add(Options.valueOf(readLine[0]));
		}
		
		return options;
	}
	
	/**
	 * Parses a config file called SimulationSetup.txt located in SimConfig\
	 * where each line is written as  <br><code>"*parameter* = *value*\n"</code></br>
	 * and returns a String of the right hand side value contained on the line
	 * <br><code>"selectedAircraft = *value*\n"</code></br>
	 * 
	 * @return selectedAircraft
	 */
	public static String parseSimulationSetupForAircraft() throws IllegalArgumentException {
		ArrayList<String[]> readSimSetupFile = readFileAndSplit(SimFiles.SIMULATION_SETUP.toString(), SimDirectories.SIM_CONFIG.toString());
		String selectedAircraft = "";
		
		for (String[] readLine : readSimSetupFile) {
			if (readLine[0].compareTo("selectedAircraft") == 0)
				selectedAircraft = readLine[1];
		}
		
		return selectedAircraft;
	}
	
	/**
	 * Parses the DisplaySetup.txt file in SimConfig\ and returns an EnumMap with {@link DisplayOptions}
	 * as the keys
	 * 
	 * @return displayOptions EnumMap
	 */
	public static EnumMap<DisplayOptions, Integer> parseDisplaySetup() {
		EnumMap<DisplayOptions, Integer> displayOptions = new EnumMap<DisplayOptions, Integer>(DisplayOptions.class);
		
		// Display options
		ArrayList<String[]> readDisplaySetupFile = readFileAndSplit(SimFiles.DISPLAY_SETUP.toString(), SimDirectories.SIM_CONFIG.toString());
		
		for(DisplayOptions displayOptionsKey : DisplayOptions.values()) {
			for (String[] readLine : readDisplaySetupFile) {
				if (displayOptionsKey.toString().equals(readLine[0]))
					displayOptions.put(displayOptionsKey, Integer.decode(readLine[1]));
			}
		}

		return displayOptions;
	}
	
	/**
	 * Parses the AudioSetup.txt file in SimConfig\ and returns an EnumMap with {@link DisplayOptions}
	 * as the keys
	 * 
	 * @return audioOptions EnumMap
	 */
	public static EnumMap<AudioOptions, Float> parseAudioSetup() {
		EnumMap<AudioOptions, Float> audioOptions = new EnumMap<AudioOptions, Float>(AudioOptions.class);
		
		// Display options
		ArrayList<String[]> readAudioSetupFile = readFileAndSplit(SimFiles.AUDIO_SETUP.toString(), SimDirectories.SIM_CONFIG.toString());
		
		for(AudioOptions audioOptionsKey : AudioOptions.values()) {
			for (String[] readLine : readAudioSetupFile) {
				if (audioOptionsKey.toString().equals(readLine[0]))
					audioOptions.put(audioOptionsKey, Float.valueOf(readLine[1]));
			}
		}

		return audioOptions;
	}
	
	/**
	 * Parses the MassProperties.txt file in Aircraft\aircraftName and returns an EnumMap with {@link MassProperties}
	 * as the keys
	 * 
	 * @param aircraftName
	 * @return massProperties EnumMap
	 */
	public static EnumMap<MassProperties, Double> parseMassProperties(String aircraftName) {
		EnumMap<MassProperties, Double> massProperties = new EnumMap<MassProperties, Double>(MassProperties.class);
		
		// Mass Properties
		ArrayList<String[]> readMassPropFile = readFileAndSplit(aircraftName, SimDirectories.AIRCRAFT.toString(), SimFiles.MASS_PROPERTIES.toString());
		
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
	
	//===================================================================================================
	//										File Writing
	//===================================================================================================
	
	/**
	 * Creates a config file called "fileName".txt located in the folder 
	 * specified by filePath using an EnumMap where each line is written as:
	 *  <br><code>"*parameter name* = *double value*\n"</code></br>
	 *  
	 * @param fileName
	 * @param filePath
	 * @param enumMap
	 */
	public static void writeConfigFile(String filePath, String fileName, Map<?, ?> enumMap) {
		StringBuilder sb = new StringBuilder();
		sb.append(FILE_ROOT).append(filePath).append(File.separator).append(fileName).append(CONFIG_EXT);
		
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(sb.toString()))) {
			for (Map.Entry<?,?> entry : enumMap.entrySet()) {
				bw.write(entry.getKey().toString() + " = " + entry.getValue());
				bw.newLine();
			}
		} catch (FileNotFoundException e) {System.err.println("Could not find: " + fileName + CONFIG_EXT + "!");}
		catch (IOException e) {System.err.println("Could not read: " + fileName + CONFIG_EXT + "!");}
		catch (NullPointerException e) {System.err.println("Bad reference when reading: " + fileName + CONFIG_EXT + "!");}
		catch (NumberFormatException e) {System.err.println("Error parsing data from " + fileName + CONFIG_EXT + "!");}
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
	public static void writeConfigFile(String filePath, String fileName, Set<Options> optionsSet, String selectedAircraft) {
		StringBuilder sb = new StringBuilder();
		sb.append(FILE_ROOT).append(filePath).append(File.separator).append(fileName).append(CONFIG_EXT);
		
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(sb.toString()))) {
			for (Options option : Options.values()) {
				bw.write(option.name() + " = " + optionsSet.contains(option));
				bw.newLine();
			}
			bw.write("selectedAircraft = " + selectedAircraft);
			bw.newLine();
		} catch (FileNotFoundException e) {System.err.println("Could not find: SimulationSetup" + CONFIG_EXT + "!");}
		catch (IOException e) {System.err.println("Could not read: SimulationSetup" + CONFIG_EXT + "!");}
		catch (NullPointerException e) {System.err.println("Bad reference to: SimulationSetup" + CONFIG_EXT + "!");}
	}
	
	/**
	 * Writes a CSV file from data contained within the logsOut ArrayList 
	 * 
	 * @param file
	 * @param logsOut
	 * @throws IOException
	 */
	public static void saveToCSVFile(File file, List<Map<SimOuts, Double>> logsOut) throws IOException {
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(file.getPath()));
		
		// First line of CSV file should have the names of each parameter
		StringBuilder sb_line1 = new StringBuilder();
		for (SimOuts simOut : SimOuts.values()) {
			sb_line1.append(simOut.toString()).append(",");
		}
		bw.write(sb_line1.append("\n").toString());
		
		// Subsequent lines contain data
		for (Map<SimOuts, Double> simOut : logsOut) {
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<?,Double> entry : simOut.entrySet()) {
				sb.append(entry.getValue().toString()).append(",");
			}
			bw.write(sb.append("\n").toString());
		}
		
		bw.close();
	}
}