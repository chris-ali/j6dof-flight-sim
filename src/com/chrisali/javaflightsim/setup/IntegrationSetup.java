package com.chrisali.javaflightsim.setup;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

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
		ArrayList<String[]> intCongigFile = readFileAndSplit(fileName);
		
		if (intCongigFile.size() != 3) {
			System.err.println("Error in integration configuration file! Generating default integration configuration...");
			return new double[] {0.0,0.05,100.0};
		} else {
			double[] integratorConfig = new double[intCongigFile.size()];
			for (int i = 0; i < intCongigFile.size(); i++)
				integratorConfig[i] = Double.parseDouble(intCongigFile.get(i)[1]);
			return integratorConfig;
		}
	}
	
	public static double[] gatherInitialControls(String fileName) {
		ArrayList<String[]> initControlFile = readFileAndSplit(fileName);
		
		if (initControlFile.size() != 13) {
			System.err.println("Error in controls file! Generating default control deflections...");
			return new double[] {0.036,0,0,0.65,0.65,1.0,1.0,1.0,1.0,0,0,0,0};
		} else {
			double[] initControl = new double[initControlFile.size()];
			for (int i = 0; i < initControlFile.size(); i++)
				initControl[i] = Double.parseDouble(initControlFile.get(i)[1]);
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
}
