package com.chrisali.javaflightsim.tests;

import java.util.EnumMap;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.chrisali.javaflightsim.controls.FlightControls;
import com.chrisali.javaflightsim.enviroment.Environment;
import com.chrisali.javaflightsim.enviroment.EnvironmentParameters;
import com.chrisali.javaflightsim.propulsion.Engine;
import com.chrisali.javaflightsim.propulsion.FixedPitchPropEngine;
import com.chrisali.javaflightsim.setup.IntegrationSetup;

public class EngineTest {
	public EngineTest() {
		EnumMap<FlightControls, Double> controls = IntegrationSetup.gatherInitialControls("InitialControls");
		EnumMap<EnvironmentParameters, Double> environmentParameters;

		double[] windParameters = new double[]{0, 0, 0};
		Engine defaultLycoming 	= new FixedPitchPropEngine();
		
		XYSeries thrustData     = new XYSeries("T");
		XYSeries momentData     = new XYSeries("P");
		XYSeries fuelFlowData   = new XYSeries("Rho");
		XYSeries rpmData   	    = new XYSeries("Speed of Sound");
		
		XYSeriesCollection thrustSeries   = new XYSeriesCollection();
		XYSeriesCollection momentSeries   = new XYSeriesCollection();
		XYSeriesCollection fuelFlowSeries = new XYSeriesCollection();
		XYSeriesCollection rpmSeries      = new XYSeriesCollection();
		
		for (double vTrue = 0; vTrue < 500; vTrue += 1) {
			environmentParameters = Environment.updateEnvironmentParams(new double[] {0,0,0});
			defaultLycoming.updateEngineState(controls, 
											  environmentParameters,
											  new double[] {0, 0, 0});
		}
	}
	
	public static void main(String[] args) {new EngineTest();}
}
