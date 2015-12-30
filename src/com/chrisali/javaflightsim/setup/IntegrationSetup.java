package com.chrisali.javaflightsim.setup;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;

import com.chrisali.javaflightsim.controls.FlightControls;

public class IntegrationSetup {
	public static final String FILE_PATH = ".\\src\\com\\chrisali\\javaflightsim\\setup\\simconfig\\";
	
	public static double[] gatherInitialConditions(String fileName) {
		ArrayList<String[]> initConditionsList = readFileAndSplit(fileName);
		
		if (initConditionsList.size() != 12) {
			System.err.println("Error in initial conditions file! Generating default initial conditions...");
			return new double[] {210,0.0,-3.99,0.0,0.0,5000.0,0.0,-0.025,1.57,0.0,0.0,0.0};
		} else {
			double[] initialCondtions = new double[initConditionsList.size()];
			for (int i = 0; i < initConditionsList.size(); i++)
				initialCondtions[i] = Double.parseDouble(initConditionsList.get(i)[1]);
			return initialCondtions;
		}
	}
	
	public static double[] gatherIntegratorConfig(String fileName) {
		ArrayList<String[]> intConfigFile = readFileAndSplit(fileName);
		
		if (intConfigFile.size() != 3) {
			System.err.println("Error in integration configuration file! Generating default integration configuration...");
			return new double[] {0.0,0.05,100.0};
		} else {
			double[] integratorConfig = new double[intConfigFile.size()];
			for (int i = 0; i < intConfigFile.size(); i++)
				integratorConfig[i] = Double.parseDouble(intConfigFile.get(i)[1]);
			return integratorConfig;
		}
	}
	
	public static EnumMap<FlightControls,Double> gatherInitialControls(String fileName) {
		ArrayList<String[]> initControlFile = readFileAndSplit(fileName);
		EnumMap<FlightControls,Double> initControl = new EnumMap<FlightControls,Double>(FlightControls.class); 
		
		if (!verifyControlFileIntegrity(initControlFile)) {
			System.err.println("Error in controls file! Generating default control deflections...");
			double[] defaultControl = new double[] {0.036,0,0,0.65,0.65,1.0,1.0,1.0,1.0,0,0,0,0};
			for (int i = 0; i < defaultControl.length; i++)
				initControl.put(FlightControls.values()[i], defaultControl[i]);
			return initControl;
		} else {
			for (int i = 0; i < initControlFile.size(); i++)
				initControl.put(FlightControls.values()[i], Double.parseDouble(initControlFile.get(i)[1]));
			return initControl;
		}
	}
	
	private static ArrayList<String[]> readFileAndSplit(String fileName) {
		StringBuilder sb = new StringBuilder();
		sb.append(FILE_PATH).append(fileName).append(".txt");
		ArrayList<String[]> readAndSplit = new ArrayList<>();
		String readLine = null;
		
		try (BufferedReader br = new BufferedReader(new FileReader(sb.toString()))) {
			while ((readLine = br.readLine()) != null)
				readAndSplit.add(readLine.split(" = "));
		} catch (FileNotFoundException e) {System.err.println("Could not find: " + fileName + ".txt!");}
		catch (IOException e) {System.err.println("Could not read: " + fileName + ".txt!");}
		catch (NullPointerException e) {System.err.println("Bad reference to: " + fileName + ".txt!");} 
		
		return readAndSplit;
	}
	
	private static boolean verifyControlFileIntegrity(ArrayList<String[]> initControlFile) {
		// If lengths are not equal, don't bother checking integrity; return false
		if (FlightControls.values().length == initControlFile.size()) {
			// Compare enum string value with read string from file
			for (int i = 0; i < FlightControls.values().length; i++) {
				if (!initControlFile.get(i)[0].equals(FlightControls.values()[i].toString()))
					return false;
			}
		}
		else {
			return false;
		}
		return true;
	}
}
