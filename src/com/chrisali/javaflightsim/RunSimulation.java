package com.chrisali.javaflightsim;

import com.chrisali.javaflightsim.aircraft.Aircraft;
import com.chrisali.javaflightsim.propulsion.FixedPitchPropEngine;
import com.chrisali.javaflightsim.utilities.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.utilities.plotting.SimulationPlots;

public class RunSimulation {

	public static void main(String[] args) {

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
		
		// Sim conditions
		double[] integratorConfig  = {0,0.05,100};  // startTime, dt, endTime
		double[] initialConditions = {initU,initV,initW,initN,initE,initD,initPhi,initTheta,initPsi,initP,initQ,initR};
		
		// TODO integrate joystick
		double[] controls = {0.02185,0,0,0.55,0,0,0,0,0,0}; //{elevator,aileron,rudder,throttle,propeller,mixture,flaps,gear,leftBrake,rightBrake};

		//---------------------
		// Start Simulation Here
		//---------------------
		Integrate6DOFEquations runSim = new Integrate6DOFEquations(integratorConfig,
																   initialConditions,
																   controls,
																   new Aircraft(),
																   new FixedPitchPropEngine(200, 2700, 6.5));
		//TODO enable/disable debug mode
		new SimulationPlots(runSim.getLogsOut(), "Simulation Plots");

	}
}
