package com.chrisali.javaflightsim.tests;

import java.util.EnumMap;

import com.chrisali.javaflightsim.controls.FlightControls;
import com.chrisali.javaflightsim.enviroment.EnvironmentParameters;
import com.chrisali.javaflightsim.propulsion.EngineModel;
import com.chrisali.javaflightsim.propulsion.FixedPitchPropEngine;
import com.chrisali.javaflightsim.setup.IntegrationSetup;

public class EngineTest {
	public EngineTest() {
		EnumMap<FlightControls, Double> controls = IntegrationSetup.gatherInitialControls("InitialControls");
		EnumMap<EnvironmentParameters, Double> environment;
 		double[] integratorConfig 				 = IntegrationSetup.unboxDoubleArray(IntegrationSetup.gatherIntegratorConfig("IntegratorConfig"));
		double[] windParameters 				 = new double[3];
		EngineModel defaultLycoming 			 = new FixedPitchPropEngine();
		
		for (double t = integratorConfig[0]; t < integratorConfig[2]; t += integratorConfig[1]) {

		}
	}
	
	public static void main(String[] args) {new EngineTest();}
}
