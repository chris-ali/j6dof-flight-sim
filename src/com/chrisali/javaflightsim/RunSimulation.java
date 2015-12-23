package com.chrisali.javaflightsim;

import com.chrisali.javaflightsim.aircraft.Aircraft;
import com.chrisali.javaflightsim.propulsion.FixedPitchPropEngine;
import com.chrisali.javaflightsim.setup.IntegrationSetup;
import com.chrisali.javaflightsim.utilities.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.utilities.plotting.SimulationPlots;

public class RunSimulation {

	public static void main(String[] args) {

		// TODO gather all initial conditions/controls from trim routine
		// TODO integrate joystick

		//---------------------
		// Start Simulation Here
		//---------------------
		Integrate6DOFEquations runSim = new Integrate6DOFEquations(IntegrationSetup.gatherIntegratorConfig("IntegratorConfig"),  // {startTime, dt, endTime}
																   IntegrationSetup.gatherInitialConditions("InitialConditions"), //{initU,initV,initW,initN,initE,initD,initPhi,initTheta,initPsi,initP,initQ,initR}
																   IntegrationSetup.gatherInitialControls("InitialControls"), // {elevator,aileron,rudder,leftThrottle,rightThrottle,leftPropeller,rightPropeller,leftMixture,rightMixture,flaps,gear,leftBrake,rightBrake}
																   new Aircraft(), // Default to Navion
																   new FixedPitchPropEngine()); // Default to Lycoming IO-360
		//TODO enable/disable debug mode
		new SimulationPlots(runSim.getLogsOut(), "Controls");
		new SimulationPlots(runSim.getLogsOut(), "Instruments");
		new SimulationPlots(runSim.getLogsOut(), "Position");
		new SimulationPlots(runSim.getLogsOut(), "Rates");
	}
}
