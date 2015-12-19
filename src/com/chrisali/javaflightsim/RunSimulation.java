package com.chrisali.javaflightsim;

import com.chrisali.javaflightsim.aircraft.Aircraft;
import com.chrisali.javaflightsim.propulsion.FixedPitchPropEngine;
import com.chrisali.javaflightsim.utilities.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.utilities.plotting.SimulationPlots;

public class RunSimulation {

	public static void main(String[] args) {
		
		// Define aircraft, engine and forces/moments
		Aircraft aircraft = new Aircraft();
		FixedPitchPropEngine fixedPitchEngine = new FixedPitchPropEngine(200, 2700, 6.5);
		
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
		double initQ = 0.00;
		double initR = 0;
		
		double startTime = 0;
		double dt = 0.05;
		double endTime = 20;
		
		// Sim conditions
		double[] integratorConfig = {startTime,dt,endTime};
		double[] initialConditions = {initU,initV,initW,initN,initE,initD,initPhi,initTheta,initPsi,initP,initQ,initR};
		
		// TODO integrate joystick
		double[] controls = {0.02165,0,0,0.75,0,0,0,0,0,0}; //{elevator,aileron,rudder,throttle,propeller,mixture,flaps,gear,leftBrake,rightBrake};

		//---------------------
		// Start Simulation Here
		//---------------------
		Integrate6DOFEquations runSim = new Integrate6DOFEquations(integratorConfig,
																   initialConditions,
																   controls,
																   aircraft,
																   fixedPitchEngine);
		
		new SimulationPlots(runSim.getLogsOut(), "Simulation Plots");

	}
}
