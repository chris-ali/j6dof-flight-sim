package com.chrisali.javaflightsim.utilities.tests;

import org.apache.commons.math3.analysis.interpolation.PiecewiseBicubicSplineInterpolatingFunction;

import com.chrisali.javaflightsim.aircraft.AircraftBuilder;

public class LookupTableTest {
	public LookupTableTest(String aircraftName) {
		double[] alpha = new double[] {-2, 0, 2, 4, 6, 8, 10, 12, 14, 16};
		double[] dFlap = new double[] {0, 10, 20, 30, 40};
											 
		PiecewiseBicubicSplineInterpolatingFunction pbsi = AircraftBuilder.createLookupTable(aircraftName, "CL_alpha");									 
				
		for (int j=0; j<dFlap.length; j++) {
			for (int i=(int)alpha[0]; i<=(int)alpha[alpha.length-1]; i++) {
				System.out.printf("Flaps: %2.0f | Alpha: %2d | CL_Alpha: %4.3f%n", dFlap[j], i, pbsi.value(i, j));
				System.out.println("-----------------------------------------");
			}
			System.out.println();
		}
	}

	public static void main(String[] args) {new LookupTableTest("LookupNavion");
	}

}
