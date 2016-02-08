package com.chrisali.javaflightsim.setup;

import java.util.EnumSet;

import com.chrisali.javaflightsim.aircraft.AircraftBuilder;
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

	public static void trimSim(AircraftBuilder ab) {
		perturb = 0.001;
		error = 100.0;
		tolerance = 1E-10;
		count = 1;

		while (error > tolerance) {
			
			Integrate6DOFEquations runSim = new Integrate6DOFEquations(ab,EnumSet.of(Options.TRIM_MODE));
			
			double[] initialConditions = new double[]{runSim.getSimOut().get(SimOuts.U),
													  runSim.getSimOut().get(SimOuts.V),
													  runSim.getSimOut().get(SimOuts.W),
												  	  runSim.getSimOut().get(SimOuts.PHI),
												  	  runSim.getSimOut().get(SimOuts.THETA),
												  	  runSim.getSimOut().get(SimOuts.P),
												  	  runSim.getSimOut().get(SimOuts.Q),
												  	  runSim.getSimOut().get(SimOuts.R)};
			
			double[] initialControls = new double[]{runSim.getSimOut().get(SimOuts.ELEVATOR),
													runSim.getSimOut().get(SimOuts.AILERON),
													runSim.getSimOut().get(SimOuts.RUDDER),
													runSim.getSimOut().get(SimOuts.THROTTLE_1),
													runSim.getSimOut().get(SimOuts.THROTTLE_2),
													runSim.getSimOut().get(SimOuts.THROTTLE_3),
													runSim.getSimOut().get(SimOuts.THROTTLE_4)};
			
			writeInitialConditions(initialConditions);
			
			writeInitialControls(initialControls);
		}
	}

	public static void writeInitialConditions(double[] initialConditions) {
		IntegrationSetup.gatherInitialConditions("InitialConditions");
	}

	public static void writeInitialControls(double[] initialControls) {
		IntegrationSetup.gatherInitialControls("InitialControls");
	}
}
