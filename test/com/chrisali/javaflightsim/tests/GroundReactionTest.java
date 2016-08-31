package com.chrisali.javaflightsim.tests;

import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import com.chrisali.javaflightsim.simulation.aircraft.AircraftBuilder;
import com.chrisali.javaflightsim.simulation.controls.FlightControlType;
import com.chrisali.javaflightsim.simulation.integration.IntegrateGroundReaction;
import com.chrisali.javaflightsim.simulation.setup.IntegrationSetup;

public class GroundReactionTest {

	private IntegrateGroundReaction groundReaction;
	private AircraftBuilder ab = new AircraftBuilder("Navion");
	private double terrainHeight;
	double[] integratorConfig 				 = ArrayUtils.toPrimitive(IntegrationSetup.gatherInitialConditions("IntegratorConfig").values()
				  																	  .toArray(new Double[3]));
	private double t;
	private Map<FlightControlType, Double> controls = IntegrationSetup.gatherInitialControls("InitialControls");
	
	// 6DOF Integration Results
	private double[] linearVelocities 		  = new double[]{5,0,0};
	private double[] NEDPosition      		  = new double[]{0,0,0};
	private double[] eulerAngles      		  = new double[]{0,0,0};
	private double[] angularRates     		  = new double[]{0,0,0};
	private double[] windParameters			  = new double[]{0,0,0};
	
	private double[] sixDOFDerivatives		  = new double[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	
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
													 windParameters,
													 integratorConfig,
													 sixDOFDerivatives,
													 ab.getAircraft(),
													 controls);
	}
	
	private void run() {
		while (t <= (integratorConfig[2]-95)) {
			
			NEDPosition[2] = 1.75;
			//controls.put(FlightControls.BRAKE_L, 0.8);
			controls.put(FlightControlType.RUDDER, -0.0);
			
			groundReaction.integrateStep(terrainHeight);
			
			System.out.println(groundReaction.toString());
			
			t += integratorConfig[1];
		}
	}

}
