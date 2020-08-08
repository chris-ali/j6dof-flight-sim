/*******************************************************************************
 * Copyright (C) 2016-2020 Christopher Ali
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
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chrisali.javaflightsim.lwjgl.interfaces.gauges.InstrumentPanel;
import com.chrisali.javaflightsim.simulation.aircraft.Aircraft;
import com.chrisali.javaflightsim.simulation.flightcontrols.analysis.AnalysisControls;
import com.chrisali.javaflightsim.simulation.integration.SimOuts;
import com.chrisali.javaflightsim.simulation.setup.ControlsConfiguration;
import com.chrisali.javaflightsim.simulation.setup.SimulationConfiguration;
import com.chrisali.javaflightsim.swing.plotting.PlotConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Contains various static methods for reading and parsing text configuration files
 */
public class FileUtilities {
	
	private static final Logger logger = LogManager.getLogger(FileUtilities.class);

	public static final String CONFIG_EXT = ".json";

	public static final String FILE_ROOT = ""; //"." + File.separator;
	
	//===================================================================================================
	//										File Reading
	//===================================================================================================
	
	/**
	 * Creates an {@link Aircraft} object by deserializing a JSON file in the Aircraft/{aircraftName} directory 
	 * called Aircraft.json 
	 * 
	 * @param aircraftName
	 * @return desrialized {@link Aircraft}
	 */
	public static Aircraft readAircraftConfiguration(String aircraftName) {
		Aircraft aircraft = deserializeJson(Aircraft.class.getSimpleName(), 
											SimDirectories.AIRCRAFT.toString() + File.separator + aircraftName, 
											Aircraft.class);
		
		return aircraft;
	}
	
	/**
	 * Creates an {@link InstrumentPanel} object by deserializing a JSON file in the Aircraft/{aircraftName} directory 
	 * called InstrumentPanel.json 
	 * 
	 * @param aircraftName
	 * @return desrialized {@link InstrumentPanel}
	 */
	public static InstrumentPanel readInstrumentPanelConfiguration(String aircraftName) {
		InstrumentPanel panel = deserializeJson(InstrumentPanel.class.getSimpleName(), 
												SimDirectories.AIRCRAFT.toString() + File.separator + aircraftName, 
												InstrumentPanel.class);
		
		return panel;
	}
	
	/**
	 * Creates a {@link SimulationConfiguration} object by deserializing a JSON file in the SimConfig directory 
	 * called SimulationConfiguration.json
	 * 
	 * @return deserialized {@link SimulationConfiguration}
	 */
	public static SimulationConfiguration readSimulationConfiguration() {
		SimulationConfiguration configuration = new SimulationConfiguration();
		configuration = deserializeJson(SimulationConfiguration.class.getSimpleName(), 
										SimDirectories.SIM_CONFIG.toString(), 
										SimulationConfiguration.class);
		
		return configuration;
	}
	
	/**
	 * Creates a {@link ControlsConfiguration} object by deserializing a JSON file in the SimConfig directory 
	 * called ControlsConfiguration.json
	 * 
	 * @return deserialized {@link ControlsConfiguration}
	 */
	public static ControlsConfiguration readControlsConfiguration() {
		ControlsConfiguration configuration = new ControlsConfiguration();
		configuration = deserializeJson(ControlsConfiguration.class.getSimpleName(), 
										SimDirectories.SIM_CONFIG.toString(), 
										ControlsConfiguration.class);
		
		return configuration;
	}
	
	/**
	 * Creates a {@link AnalysisControls} object by deserializing a JSON file in the SimConfig directory 
	 * called AnalysisControls.json
	 * 
	 * @return deserialized {@link AnalysisControls}
	 */
	public static AnalysisControls readAnalysisControls() {
		AnalysisControls controls = new AnalysisControls();
		controls = deserializeJson(AnalysisControls.class.getSimpleName(), 
								   SimDirectories.SIM_CONFIG.toString(), 
								   AnalysisControls.class);
		
		return controls;
	}
	
	/**
	 * Creates a {@link PlotConfiguration} object by deserializing a JSON file in the SimConfig directory 
	 * called PlotConfiguration.json
	 * 
	 * @return deserialized {@link PlotConfiguration}
	 */
	public static PlotConfiguration readPlotConfiguration() {
		PlotConfiguration configuration = new PlotConfiguration();
		configuration = deserializeJson(PlotConfiguration.class.getSimpleName(), 
									    SimDirectories.SIM_CONFIG.toString(), 
									    PlotConfiguration.class);
		
		return configuration;
	}
	
	/**
	 * Deserializes an JSON file into a T object based on the file name, file path and class provided
	 * 
	 * @param filename
	 * @param filepath
	 * @param klasse
	 * @return deserialized POJO
	 */
	private static <T> T deserializeJson(String filename, String filepath, Class<T> klasse) {
		StringBuilder sb = new StringBuilder();
		sb.append(FILE_ROOT).append(filepath).append(File.separator).append(filename).append(CONFIG_EXT);
				
		logger.debug("Reading file: " + sb.toString() + "...");
		
		T objToDeserialize = null;
		ObjectMapper mapper = new ObjectMapper();
		
		try (BufferedReader br = new BufferedReader(new FileReader(sb.toString()))) {
			objToDeserialize = mapper.readValue(br, klasse);
			
		} catch (FileNotFoundException e) {logger.error("Could not find: " + filename + CONFIG_EXT + "!", e);}
		catch (IOException e) {logger.error("Could not read: " + filename + CONFIG_EXT + "!", e);}
		catch (NullPointerException e) {logger.error("Bad reference when reading: " + filename + CONFIG_EXT + "!", e);}
		catch (NumberFormatException e) {logger.error("Error parsing data from " + filename + CONFIG_EXT + "!", e);}
		
		logger.debug("...done!");
		
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
	 * Serializes an object as a JSON file called "fileName"{@value #CONFIG_EXT} located in the folder 
	 * specified by filePath
	 *  
	 * @param filepath
	 * @param filename
	 * @param objToSerialize
	 */
	public static void serializeJson(String filepath, String filename, Object objToSerialize) {
		StringBuilder sb = new StringBuilder();
		sb.append(FILE_ROOT).append(filepath).append(File.separator).append(filename).append(CONFIG_EXT);
		
		logger.debug("Saving configuration file to: " + sb.toString());
		
		ObjectMapper mapper = new ObjectMapper();	
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(sb.toString()))) {
			mapper.writeValue(bw, objToSerialize);
			
		} catch (FileNotFoundException e) {logger.error("Could not find: " + filename + CONFIG_EXT + "!", e);}
		catch (IOException e) {logger.error("Could not read: " + filename + CONFIG_EXT + "!", e);}
		catch (NullPointerException e) {logger.error("Bad reference to: " + filename + CONFIG_EXT + "!", e);}
		
		logger.debug(filename + CONFIG_EXT + " saved successfully!");
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
