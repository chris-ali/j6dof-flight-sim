package com.chrisali.javaflightsim.utilities;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;

import com.chrisali.javaflightsim.integration.Integrate6DOFEquations;

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
	 * Splits a text file of the name "fileContents".txt located in the folder 
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
		catch (NullPointerException e) {System.err.println("Bad reference to: " + fileContents + ".txt!");} 
		catch (NumberFormatException e) {System.err.println("Error parsing data from " + fileContents + ".txt!");}
		
		return readAndSplit;
	}
	
	/**
	 * Splits a text file of the name "fileName".txt located in the folder: 
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
		catch (NullPointerException e) {System.err.println("Bad reference to: " + fileName + ".txt!");} 
		
		return readAndSplit;
	}
}
