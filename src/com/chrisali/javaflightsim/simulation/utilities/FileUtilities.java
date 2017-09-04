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
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chrisali.javaflightsim.simulation.aircraft.AircraftBuilder;
import com.chrisali.javaflightsim.simulation.aircraft.MassProperties;
import com.chrisali.javaflightsim.simulation.integration.SimOuts;
import com.chrisali.javaflightsim.simulation.setup.SimulationConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Contains various static methods for reading and parsing text configuration files
 */
public class FileUtilities {
	
	private static final Logger logger = LogManager.getLogger(FileUtilities.class);

	public static final String CONFIG_EXT = ".txt";
	
	public static final String JSON_EXT = ".json";

	public static final String FILE_ROOT = ""; //"." + File.separator;
	
	//===================================================================================================
	//										File Reading
	//===================================================================================================
	
	/**
	 * Splits a config file called "fileContents"{@value #CONFIG_EXT} located in the folder 
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
		
		logger.debug("Reading file: " + sb.toString() + "...");
		
		try (BufferedReader br = new BufferedReader(new FileReader(sb.toString()))) {
			while ((readLine = br.readLine()) != null)
				readAndSplit.add(readLine.split(" = "));
		} catch (FileNotFoundException e) {logger.error("Could not find: " + fileContents + CONFIG_EXT + "!", e);}
		catch (IOException e) {logger.error("Could not read: " + fileContents + CONFIG_EXT + "!", e);}
		catch (NullPointerException e) {logger.error("Bad reference when reading: " + fileContents + CONFIG_EXT + "!", e);} 
		catch (NumberFormatException e) {logger.error("Error parsing data from " + fileContents + CONFIG_EXT + "!", e);}
		
		return readAndSplit;
	}
	
	/**
	 * Splits a config file called "fileName"{@value #CONFIG_EXT} located in the folder 
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
		
		logger.debug("Reading file: " + sb.toString() + "...");
		
		try (BufferedReader br = new BufferedReader(new FileReader(sb.toString()))) {
			while ((readLine = br.readLine()) != null)
				readAndSplit.add(readLine.split(" = "));
		} catch (FileNotFoundException e) {logger.error("Could not find: " + fileName + CONFIG_EXT + "!", e);}
		catch (IOException e) {logger.error("Could not read: " + fileName + CONFIG_EXT + "!", e);}
		catch (NullPointerException e) {logger.error("Bad reference when reading: " + fileName + CONFIG_EXT + "!", e);}
		catch (NumberFormatException e) {logger.error("Error parsing data from " + fileName + CONFIG_EXT + "!", e);}
		
		return readAndSplit;
	}
	
	/**
	 * Parses a config file called SimulationSetup{@value #CONFIG_EXT} located in SimConfig\
	 * where each line is written as  <br><code>"*parameter* = *value*\n"</code></br>
	 * and returns a String of the right hand side value contained on the line
	 * <br><code>"selectedAircraft = *value*\n"</code></br>
	 * 
	 * @return selectedAircraft
	 * @throws IllegalArgumentException
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
	 * Parses the MassProperties{@value #CONFIG_EXT} file in Aircraft\aircraftName and returns an EnumMap with {@link MassProperties}
	 * as the keys
	 * 
	 * @param aircraftName
	 * @return massProperties EnumMap
	 */
	public static EnumMap<MassProperties, Double> parseMassProperties(String aircraftName) {
		logger.debug("Parsing mass propertes for " + aircraftName + "...");
		
		EnumMap<MassProperties, Double> massProperties = new EnumMap<MassProperties, Double>(MassProperties.class);
		
		// Mass Properties
		ArrayList<String[]> readMassPropFile = readFileAndSplit(aircraftName, SimDirectories.AIRCRAFT.toString(), SimFiles.MASS_PROPERTIES.toString());
		
		try {
			for(MassProperties massPropKey : MassProperties.values()) {
				for (String[] readLine : readMassPropFile) {
					if (massPropKey.toString().equals(readLine[0]))
						massProperties.put(massPropKey, Double.parseDouble(readLine[1]));
				}
			}
		} catch (Exception e) {
			// TODO should try to return a default configuration
			logger.error("Error reading display configuration!", e);
		}		

		return massProperties;
	}
	
	public static AircraftBuilder readAircraftConfiguration(String aircraftName) {
		AircraftBuilder ab = deserializeJson(AircraftBuilder.class.getSimpleName(), 
											 SimDirectories.AIRCRAFT.toString() + File.separator + aircraftName, 
											 AircraftBuilder.class);
		
		return ab;
	}
	
	public static SimulationConfiguration readSimulationConfiguration() {
		SimulationConfiguration configuration = new SimulationConfiguration();
		configuration = deserializeJson(SimulationConfiguration.class.getSimpleName(), 
										SimDirectories.SIM_CONFIG.toString(), 
										SimulationConfiguration.class);
		
		return configuration;
	}
	
	private static <T> T deserializeJson(String filename, String filepath, Class<T> klasse) {
		StringBuilder sb = new StringBuilder();
		sb.append(FILE_ROOT).append(filepath).append(File.separator).append(filename).append(JSON_EXT);
				
		logger.debug("Reading file: " + sb.toString() + "...");
		
		T objToDeserialize = null;
		ObjectMapper mapper = new ObjectMapper();
		
		try (BufferedReader br = new BufferedReader(new FileReader(sb.toString()))) {
			objToDeserialize = mapper.readValue(br, klasse);
			
		} catch (FileNotFoundException e) {logger.error("Could not find: " + filename + JSON_EXT + "!", e);}
		catch (IOException e) {logger.error("Could not read: " + filename + JSON_EXT + "!", e);}
		catch (NullPointerException e) {logger.error("Bad reference when reading: " + filename + JSON_EXT + "!", e);}
		catch (NumberFormatException e) {logger.error("Error parsing data from " + filename + JSON_EXT + "!", e);}
		
		return objToDeserialize;
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
	 * Serializes an object as a JSON file called "fileName"{@value #JSON_EXT} located in the folder 
	 * specified by filePath
	 *  
	 * @param filepath
	 * @param filename
	 * @param objToSerialize
	 */
	public static void serializeJson(String filepath, String filename, Object objToSerialize) {
		StringBuilder sb = new StringBuilder();
		sb.append(FILE_ROOT).append(filepath).append(File.separator).append(filename).append(JSON_EXT);
		
		logger.debug("Saving configuration file to: " + sb.toString());
		
		ObjectMapper mapper = new ObjectMapper();	
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(sb.toString()))) {
			mapper.writeValue(bw, objToSerialize);
			
		} catch (FileNotFoundException e) {logger.error("Could not find: " + filename + JSON_EXT + "!", e);}
		catch (IOException e) {logger.error("Could not read: " + filename + JSON_EXT + "!", e);}
		catch (NullPointerException e) {logger.error("Bad reference to: " + filename + JSON_EXT + "!", e);}
		
		logger.debug(filename + JSON_EXT + " saved successfully!");
	}
			
	/**
	 * Writes a CSV file from data contained within the logsOut ArrayList 
	 * 
	 * @param file
	 * @param logsOut
	 * @throws IOException
	 */
	public static void saveToCSVFile(File file, List<Map<SimOuts, Double>> logsOut) throws IOException {
		
		logger.debug("Saving configuration file to: " + file.getAbsolutePath());
		
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
		
		logger.debug(file.getName() + " saved successfully!");
	}
}
