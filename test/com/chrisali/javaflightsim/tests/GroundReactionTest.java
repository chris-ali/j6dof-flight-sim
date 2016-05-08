package com.chrisali.javaflightsim.tests;

import java.util.Map;

import com.chrisali.javaflightsim.simulation.aircraft.AircraftBuilder;
import com.chrisali.javaflightsim.simulation.controls.FlightControls;
import com.chrisali.javaflightsim.simulation.integration.IntegrateGroundReaction;
import com.chrisali.javaflightsim.simulation.setup.IntegrationSetup;
import com.chrisali.javaflightsim.utilities.Utilities;

public class GroundReactionTest {

	private IntegrateGroundReaction groundReaction;
	private AircraftBuilder ab = new AircraftBuilder("Navion");
	private double terrainHeight;
	private double[] integratorConfig = Utilities.unboxDoubleArray(IntegrationSetup.gatherIntegratorConfig("IntegratorConfig"));
	private double t;
	private Map<FlightControls, Double> controls = IntegrationSetup.gatherInitialControls("InitialControls");
	
	// 6DOF Integration Results
	private double[] linearVelocities 		  = new double[]{5,0,0};
	private double[] NEDPosition      		  = new double[]{0,0,0};
	private double[] eulerAngles      		  = new double[]{0,0,0};
	private double[] angularRates     		  = new double[]{0,0,0};
	
	public static void main(String[] args) {
		GroundReactionTest test = new GroundReactionTest();
		test.setup();
		test.run();

	}
	
	private void setup() {
		
		terrainHeight = 0;
		t = 0;
		
		groundReaction = new IntegrateGroundReaction(linearVelocities,
													 NEDPosition,
													 eulerAngles,
													 angularRates,
													 integratorConfig,
													 ab.getAircraft(),
													 controls, 
													 terrainHeight);
	}
	
	private void run() {
		
		while (t <= integratorConfig[2]) {
			
			NEDPosition[2] = -t/100;
		
			groundReaction.integrateStep();
			
			StringBuilder sb = new StringBuilder();
			sb.append("Ground Forces: [");
			for (int i = 0; i < 3; i++) {
				sb.append((int) groundReaction.getTotalGroundForces()[i]);
				if (i < 2)
					sb.append(", ");
			}
			sb.append("]\n");
			
			sb.append("Ground Moments: [");
			for (int i = 0; i < 3; i++) {
				sb.append((int) groundReaction.getTotalGroundMoments()[i]);
				if (i < 2)
					sb.append(", ");
			}
			sb.append("]");
			
			System.out.println(sb.toString());
			
			t += integratorConfig[1];
		}
	}

}
