package com.chrisali.javaflightsim;

import java.util.ArrayList;

import com.chrisali.javaflightsim.aero.AccelAndMoments;
import com.chrisali.javaflightsim.aircraft.Aircraft;
import com.chrisali.javaflightsim.enviroment.Environment;
import com.chrisali.javaflightsim.propulsion.FixedPitchPropEngine;
import com.chrisali.javaflightsim.utilities.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.utilities.integration.SixDOFUtilities;

public class RunSimulation {

	public static void main(String[] args) {
		
		// Define aircraft, engine and forces/moments
		Aircraft navion = new Aircraft();
		AccelAndMoments aircraftForcesAndMoments = new AccelAndMoments();
		FixedPitchPropEngine lycomingIO360 = new FixedPitchPropEngine(200, 2700, 6.5);
		
		// TODO gather all initial conditions from trim routine/text file
		double initU = 200;
		double initV = 0;
		double initW = 0;
		double initN = 0;
		double initE = 0;
		double initD = 5000;
		double initPhi = 0;
		double initTheta = 0;
		double initPsi = 1.57;
		double initP = 0;
		double initQ = 0;
		double initR = 0;
		
		double dt = 0.1;
		double startTime = 0;
		double endTime = 5;
		
		// Sim conditions
		double[] integratorConfig = {0,dt,dt};
		double[] initialConditions = {initU,initV,initW,initN,initE,initD,initPhi,initTheta,initPsi,initP,initQ,initR};
		ArrayList<double[]> logsOut = new ArrayList<>();
		
		// TODO integrate joystick
		double[] controls = {0.0,0,0,0.75,0,0,0,0,0,0}; //{elevator,aileron,rudder,throttle,propeller,mixture,flaps,gear,leftBrake,rightBrake};
		
		// Assign state arrays based on initial conditions
		double[] linearVelocities = {initU,initV,initW};
		double[] NEDPosition = {initN,initE,initD};		
		double[] eulerAngles = {initPhi,initTheta,initPsi};
		double[] angularRates = {initP,initQ,initR};
		
		// Calculate TAS,alpha and beta
		double[] windParameters = SixDOFUtilities.getWindParameters(linearVelocities);
		
		// Calculate initial environment parameters
		double[] environmentParameters = Environment.getEnvironmentParams(NEDPosition);
		
		//TODO need a way to calculate alphaDot
		double alphaDot = 0;
		
		// Calculate initial thrust
		lycomingIO360.calculateThrust(controls, NEDPosition, environmentParameters, windParameters);
		
		// Calculate initial accelerations
		double[] linearAccelerations = aircraftForcesAndMoments.getBodyAccelerations(windParameters,
																				     angularRates,
																				     navion.wingDimensions,
																				     environmentParameters,
																				     controls,
																				     alphaDot, 
																				     lycomingIO360);
		
		// Calculate initial moments
		double[] totalMoments = aircraftForcesAndMoments.getTotalMoments(windParameters,
																 		 angularRates,
																		 navion.wingDimensions,
																		 environmentParameters,
																		 controls,
																		 alphaDot,
																		 lycomingIO360);
		//---------------------
		// Start Sim Loop Here
		//---------------------
		for (double time = startTime; time < endTime; time+=dt) {
			// Run integrators for one step
			Integrate6DOFEquations simIntegrators = new Integrate6DOFEquations(linearAccelerations, 
																			   totalMoments, 
																			   navion.massProperties, 
																			   Environment.getGravity(), 
																			   integratorConfig, 
																			   initialConditions);
			
			// Assign 6DOF states
			linearVelocities = simIntegrators.linearVelocities;
			NEDPosition = simIntegrators.NEDPosition;
			angularRates = simIntegrators.angularRates;
			eulerAngles = simIntegrators.eulerAngles;
			
			// Update wind parameters
			windParameters = SixDOFUtilities.getWindParameters(linearVelocities);
			
			// Update environment		
			environmentParameters = Environment.getEnvironmentParams(NEDPosition);
			
			// Update engine
			lycomingIO360.calculateThrust(controls, NEDPosition, environmentParameters, windParameters);
			
			// Update accelerations
			linearAccelerations = aircraftForcesAndMoments.getBodyAccelerations(windParameters,
																			    angularRates,
																			    navion.wingDimensions,
																			    environmentParameters,
																			    controls,
																			    alphaDot,
																			    lycomingIO360);
			// Update moments
			totalMoments = aircraftForcesAndMoments.getTotalMoments(windParameters,
															 		angularRates,
																	navion.wingDimensions,
																	environmentParameters,
																	controls,
																	alphaDot,
																	lycomingIO360);
			
			// Create an output array of all state arrays
			double outputStep[] = {integratorConfig[1], 
								   linearVelocities[0],
								   linearVelocities[1],
								   linearVelocities[2],
								   angularRates[0],
								   angularRates[1],
								   angularRates[2],						
								   eulerAngles[0],
								   eulerAngles[1],
								   eulerAngles[2],
								   windParameters[0],
								   windParameters[1],
								   windParameters[2],
								   linearAccelerations[0],
								   linearAccelerations[1],
								   linearAccelerations[2],
								   totalMoments[0],
								   totalMoments[1],
								   totalMoments[2],
								   NEDPosition[0],
								   NEDPosition[1],
								   NEDPosition[2]};
			
			logsOut.add(outputStep);
			
			for (double out : outputStep)
				System.out.printf("%10.2f ", out);
			System.out.println("\n");
			
			// Increment the start and end times
			integratorConfig[0] += dt;
			integratorConfig[1] += dt;
		}
	}
}
