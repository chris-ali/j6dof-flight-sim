package com.chrisali.javaflightsim.setup;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import com.chrisali.javaflightsim.aircraft.AircraftBuilder;
import com.chrisali.javaflightsim.controls.FlightControls;
import com.chrisali.javaflightsim.utilities.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.utilities.integration.SimOuts;

/**
 * @see R. Hall and S. ANstee, Trim Calculation Methods for a Dynamical Model of
 *      the REMUS 100 Autonomous Underwater Vehicle (DSTO-TR-2576)
 */
public class Trimming {
	private static double perturb;
	private static double error;
	private static double tolerance;
	private static int count;
	private static RealMatrix jacobian;

	public static void trimSim(AircraftBuilder ab) {
		perturb = 0.001;
		error = 100.0;
		tolerance = 1E-10;
		count = 1;

		while ((error > tolerance) & (count < 15)) {
			// Create copies of initial control/condition EnumMaps as "nextStepInitial*" text files for Integrate6DOFEquations 
			writeNextInitialConditions(new EnumMap<InitialConditions, Double>(InitialConditions.class));
			writeNextInitialControls(new EnumMap<FlightControls, Double>(FlightControls.class));
			
			Integrate6DOFEquations runSim = new Integrate6DOFEquations(ab,EnumSet.of(Options.TRIM_MODE));
			
			EnumMap<InitialConditions, Double> nextInitialConditions = new EnumMap<InitialConditions, Double>(InitialConditions.class);
			nextInitialConditions.put(InitialConditions.INITU,     	runSim.getSimOut().get(SimOuts.U));
			nextInitialConditions.put(InitialConditions.INITV, 	  	runSim.getSimOut().get(SimOuts.V));
			nextInitialConditions.put(InitialConditions.INITW, 	  	runSim.getSimOut().get(SimOuts.W));
			nextInitialConditions.put(InitialConditions.INITPHI,   	runSim.getSimOut().get(SimOuts.PHI));
			nextInitialConditions.put(InitialConditions.INITTHETA, 	runSim.getSimOut().get(SimOuts.THETA));
			nextInitialConditions.put(InitialConditions.INITP, 	  	runSim.getSimOut().get(SimOuts.P));
			nextInitialConditions.put(InitialConditions.INITQ, 	  	runSim.getSimOut().get(SimOuts.Q));
			nextInitialConditions.put(InitialConditions.INITR, 	  	runSim.getSimOut().get(SimOuts.R));
			
			EnumMap<FlightControls, Double> nextInitialControls = new EnumMap<FlightControls, Double>(FlightControls.class);
			nextInitialControls.put(FlightControls.ELEVATOR,   runSim.getSimOut().get(SimOuts.ELEVATOR));
			nextInitialControls.put(FlightControls.AILERON,    runSim.getSimOut().get(SimOuts.AILERON));
			nextInitialControls.put(FlightControls.RUDDER, 	   runSim.getSimOut().get(SimOuts.RUDDER));
			nextInitialControls.put(FlightControls.THROTTLE_1, runSim.getSimOut().get(SimOuts.THROTTLE_1));
			nextInitialControls.put(FlightControls.THROTTLE_2, runSim.getSimOut().get(SimOuts.THROTTLE_1));
			nextInitialControls.put(FlightControls.THROTTLE_3, runSim.getSimOut().get(SimOuts.THROTTLE_1));
			nextInitialControls.put(FlightControls.THROTTLE_4, runSim.getSimOut().get(SimOuts.THROTTLE_1));
			
			jacobian = MatrixUtils.createRealMatrix(new double[nextInitialConditions.size()][nextInitialConditions.size()]); 
			
			// Create "nextStepInitial*" text files for next step of trimming
			writeNextInitialConditions(nextInitialConditions);
			writeNextInitialControls(nextInitialControls);
			
			count++;
		}
	}

	private static void writeNextInitialConditions(EnumMap<InitialConditions, Double> nextInitialConditions) {
		// Use InitialConditions EnumMap as a base; nextStepInitialConditions will update its dependent values
		EnumMap<InitialConditions, Double> initialConditions = IntegrationSetup.gatherInitialConditions("InitialConditions");
		
		// nextStepInitialConditions only contains part of the whole initialConditions EnumMap, because
		// the other states are position values that don't influence the trim results - loop through the
		// the states that do influence trimming, and assign them to initialConditions 
		for (Map.Entry<InitialConditions, Double> entry : nextInitialConditions.entrySet())
			initialConditions.put(entry.getKey(), entry.getValue());
			
		writeFile(initialConditions, "nextStepInitialConditions");
	}

	private static void writeNextInitialControls(EnumMap<FlightControls, Double> nextInitialControls) {
		EnumMap<FlightControls, Double> initialControls = IntegrationSetup.gatherInitialControls("InitialControls");
		
		for (Map.Entry<FlightControls, Double> entry : nextInitialControls.entrySet())
			initialControls.put(entry.getKey(), entry.getValue());
		
		writeFile(initialControls, "nextStepInitialControls");
	}
	
	private static void copyFile(String fileName) {
		
	}
	
	private static void writeFile(EnumMap<?, Double> initEnumMap, String fileName) {
		StringBuilder sb = new StringBuilder();
		sb.append(IntegrationSetup.FILE_PATH).append(fileName).append(".txt");
		
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(sb.toString()))) {
			for(Enum<?> entry : initEnumMap.keySet())
				bw.write(entry.toString() + " = " + initEnumMap.get(entry));
		} catch (FileNotFoundException e) {System.err.println("Could not find: " + fileName + ".txt!");}
		catch (IOException e) {System.err.println("Could not read: " + fileName + ".txt!");}
		catch (NullPointerException e) {System.err.println("Bad reference to: " + fileName + ".txt!");} 
	}
}
