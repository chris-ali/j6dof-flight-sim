package com.chrisali.javaflightsim.tests;

import java.util.Map;

import com.chrisali.javaflightsim.simulation.controls.FlightControls;
import com.chrisali.javaflightsim.simulation.integration.IntegrateGroundReaction;
import com.chrisali.javaflightsim.simulation.setup.IntegrationSetup;
import com.chrisali.javaflightsim.utilities.Utilities;

public class GroundReactionTest {

	private IntegrateGroundReaction groundReaction;
	private double terrainHeight;
	private double[] integratorConfig = Utilities.unboxDoubleArray(IntegrationSetup.gatherIntegratorConfig("IntegratorConfig"));
	private Map<FlightControls, Double> controls = IntegrationSetup.gatherInitialControls("InitialControls");
	
	private double[] linearVelocities 		  = new double[]{0, 0, 0};
	private double[] NEDPosition      		  = new double[]{0, 0, 0};
	private double[] eulerAngles      		  = new double[]{0, 0, 0};
	private double[] angularRates     		  = new double[]{0, 0, 0};
	
	public static void main(String[] args) {
		GroundReactionTest test = new GroundReactionTest();
		test.setup();
		test.run();

	}
	
	private void setup() {
		
		terrainHeight = 0;
		
		groundReaction = new IntegrateGroundReaction(linearVelocities, 
													NEDPosition, 
													eulerAngles, 
													angularRates,
													integratorConfig,
													controls, 
													terrainHeight);
	}
	
	private void run() {
		groundReaction.integrateStep();
		
		System.out.println(groundReaction.getTotalGroundForces());
		System.out.println(groundReaction.getTotalGroundMoments());
	}

}
