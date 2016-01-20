package com.chrisali.javaflightsim.setup;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;

import com.chrisali.javaflightsim.controls.FlightControls;

public class IntegrationSetup {
	private static final String FILE_PATH = ".\\src\\com\\chrisali\\javaflightsim\\setup\\simconfig\\";
	
	public static EnumMap<InitialConditions, Double> gatherInitialConditions(String fileName) {
		ArrayList<String[]> initConditionsFile = readFileAndSplit(fileName);
		EnumMap<InitialConditions,Double> initialConditions = new EnumMap<InitialConditions,Double>(InitialConditions.class); 
				
		if (!verifyICFileIntegrity(initConditionsFile)) {
			System.err.println("Error in initial conditions file! Generating default initial conditions...");
			Double[] defaultIC = new Double[] {210.0, 0.0, -3.99, 0.0, 0.0, 5000.0, 0.0, -0.025, 1.57, 0.0, 0.0, 0.0};
			for (int i = 0; i < defaultIC.length; i++)
				initialConditions.put(InitialConditions.values()[i], defaultIC[i]);
			return initialConditions;
		} else {
			for (int i = 0; i < initConditionsFile.size(); i++)
				initialConditions.put(InitialConditions.values()[i], Double.parseDouble(initConditionsFile.get(i)[1]));
			return initialConditions;
		}
	}
	
	public static EnumMap<IntegratorConfig, Double> gatherIntegratorConfig(String fileName) {
		ArrayList<String[]> intConfigFile = readFileAndSplit(fileName);
		EnumMap<IntegratorConfig,Double> integratorConfig = new EnumMap<IntegratorConfig,Double>(IntegratorConfig.class); 
				
		if (!verifyIntConfigFileIntegrity(intConfigFile)) {
			System.err.println("Error in integration configuration file! Generating default integration configuration...");
			double[] defaultIntConfig = new double[] {0.0, 0.05, 100.0};
			for (int i = 0; i < defaultIntConfig.length; i++)
				integratorConfig.put(IntegratorConfig.values()[i], defaultIntConfig[i]);
			return integratorConfig;
		} else {
			for (int i = 0; i < intConfigFile.size(); i++)
				integratorConfig.put(IntegratorConfig.values()[i], Double.parseDouble(intConfigFile.get(i)[1]));
			return integratorConfig;
		}
	}
	
	public static EnumMap<FlightControls, Double> gatherInitialControls(String fileName) {
		ArrayList<String[]> initControlFile = readFileAndSplit(fileName);
		EnumMap<FlightControls,Double> initControl = new EnumMap<FlightControls,Double>(FlightControls.class); 
		
		if (!verifyControlFileIntegrity(initControlFile)) {
			System.err.println("Error in controls file! Generating default control deflections...");
			double[] defaultControl = new double[] {0.036, 0, 0, 0.65, 0.65, 0.65, 0.65, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0, 0, 0, 0};
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
	
	// Check parsed file to ensure that length and content match enum map langth and key content
	
	private static boolean verifyControlFileIntegrity(ArrayList<String[]> initControlFile) {
		// If lengths are not equal, don't bother checking integrity; return false
		if (FlightControls.values().length == initControlFile.size()) {
			// Compare enum string value with read string from file
			for (int i = 0; i < FlightControls.values().length; i++) {
				if (!initControlFile.get(i)[0].equals(FlightControls.values()[i].toString()))
					return false;
			}
		}
		else {return false;}
		
		return true;
	}
	
	private static boolean verifyICFileIntegrity(ArrayList<String[]> initConditionsFile) {
		// If lengths are not equal, don't bother checking integrity; return false
		if (InitialConditions.values().length == initConditionsFile.size()) {
			// Compare enum string value with read string from file
			for (int i = 0; i < InitialConditions.values().length; i++) {
				if (!initConditionsFile.get(i)[0].equals(InitialConditions.values()[i].toString()))
					return false;
			}
		}
		else {return false;}
		
		return true;
	}
	
	private static boolean verifyIntConfigFileIntegrity(ArrayList<String[]> intConfigFile) {
		// If lengths are not equal, don't bother checking integrity; return false
		if (IntegratorConfig.values().length == intConfigFile.size()) {
			// Compare enum string value with read string from file
			for (int i = 0; i < IntegratorConfig.values().length; i++) {
				if (!intConfigFile.get(i)[0].equals(IntegratorConfig.values()[i].toString()))
					return false;
			}
		}
		else {return false;}
		
		return true;
	}
	
	// Unboxes Double[] array to double[]; integrator needs primitive arrays, necessitating this method
	public static double[] unboxDoubleArray(EnumMap<?, Double> map) {
		double[] unboxedArray = new double[map.values().size()]; 
		for (int i = 0; i < unboxedArray.length; i++)
			unboxedArray[i] = map.values().toArray(new Double[unboxedArray.length])[i];
		return unboxedArray;
	}
	public static double[] unboxDoubleArray(Double[] boxed) {
		double[] unboxedArray = new double[boxed.length]; 
		for (int i = 0; i < unboxedArray.length; i++)
			unboxedArray[i] = boxed[i];
		return unboxedArray;
	}
}
