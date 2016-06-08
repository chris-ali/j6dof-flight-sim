package com.chrisali.javaflightsim.simulation.setup;

import java.util.EnumMap;
import java.util.Set;

import com.chrisali.javaflightsim.controllers.SimulationController;
import com.chrisali.javaflightsim.simulation.aero.Aerodynamics;
import com.chrisali.javaflightsim.simulation.aero.StabilityDerivatives;
import com.chrisali.javaflightsim.simulation.aero.WingGeometry;
import com.chrisali.javaflightsim.simulation.aircraft.Aircraft;
import com.chrisali.javaflightsim.simulation.aircraft.AircraftBuilder;
import com.chrisali.javaflightsim.simulation.aircraft.MassProperties;
import com.chrisali.javaflightsim.simulation.controls.FlightControls;
import com.chrisali.javaflightsim.simulation.enviroment.Environment;
import com.chrisali.javaflightsim.simulation.enviroment.EnvironmentParameters;
import com.chrisali.javaflightsim.simulation.propulsion.Engine;
import com.chrisali.javaflightsim.utilities.SixDOFUtilities;
import com.chrisali.javaflightsim.utilities.Utilities;

/**
 * Simple rudimentary method of longitudinally trimming an aircraft by statically equating forces and moments.
 * This calculates trim deflections of throttle and elevator needed for level flight. For trim pitch, flight path
 * angle and angle of attack are considered by using the following equation:
 * 
 *  <p>Angle of Attack = Pitch Angle + Flight Path Angle</p>
 * 
 * @author Christopher Ali
 * @see Introduction to Aircraft Stability and Control Course Notes for M&AE 5070 - David A. Caughey, Cornell University (pg 29)
 *  <p> https://courses.cit.cornell.edu/mae5070/Caughey_2011_04.pdf </p> 
 */
public class Trimming {
	
	private static EnumMap<InitialConditions, Double> initialConditions;
	private static EnumMap<FlightControls, Double> initialControls;
	private static EnumMap<EnvironmentParameters, Double> environmentParams;
	private static Aircraft aircraft;
	private static Aerodynamics aero;
	
	/**
	 * Trims an aircraft longitudinally for a forward velocity and altitude specified in 
	 * 
	 * <p> ./SimConfig/InitialConditions.txt </p>
	 * 
	 * by setting the elevator, throttle and pitch attitude. These values are calculated by statically equating forces and moments.
	 * If unable to reach a given trim condition, the method will return the maximum values for each control and pitch attitude.
	 * These values are then saved to 
	 * 
	 * <p> ./SimConfig/InitialConditions.txt </p>
	 * and 
	 * <p> ./SimConfig/InitialControls.txt </p>
	 * 
	 * as long as the test mode boolean flag is false; otherwise the results will be displayed in the console
	 * 
	 * @param ab
	 * @param testMode
	 */
	public static void trimSim(AircraftBuilder ab, boolean testMode) {
		aircraft = ab.getAircraft();
		aero = new Aerodynamics(aircraft);
		initialConditions = IntegrationSetup.gatherInitialConditions("InitialConditions");
		initialControls = IntegrationSetup.gatherInitialControls("InitialControls");
		environmentParams = Environment.updateEnvironmentParams(new double[]{0,0,initialConditions.get(InitialConditions.INITD)});
		
		//=============================================== Elevator and Pitch ===========================================================
		
		double[] windParameters = SixDOFUtilities.calculateWindParameters(new double[] {initialConditions.get(InitialConditions.INITU),
																						initialConditions.get(InitialConditions.INITV),
																						initialConditions.get(InitialConditions.INITW)});
		
		// Aerodynamic parameters 
		double drag = aero.calculateBodyForces(windParameters, new double[] {0, 0, 0}, environmentParams, initialControls, 0)[0]*0.75;
		
		double q = environmentParams.get(EnvironmentParameters.RHO)*Math.pow(windParameters[0], 2)/2;
		double CL_trim = (aircraft.getMassProperty(MassProperties.TOTAL_MASS) * Environment.getGravity())/
						 (q * aircraft.getWingGeometry(WingGeometry.S_WING));
		
		double CL_alpha = aero.calculateInterpStabDer(windParameters, initialControls, StabilityDerivatives.CL_ALPHA)*5.0;
		double CL_d_elev = (double) aircraft.getStabilityDerivative(StabilityDerivatives.CL_D_ELEV);
		
		double CM_alpha = aero.calculateInterpStabDer(windParameters, initialControls, StabilityDerivatives.CM_ALPHA)*5.0;
		double CM_d_elev = (double) aircraft.getStabilityDerivative(StabilityDerivatives.CM_D_ELEV) * 3; //0.875; //3;
		double CM_0 = (double) aircraft.getStabilityDerivative(StabilityDerivatives.CM_0);
		
		double delta = ((CM_alpha * CL_d_elev) - (CL_alpha * CM_d_elev));
		
		// Calculate trim deflections, limiting if necessary
		double elevTrim = ((CL_alpha * CM_0) + (CM_alpha * CL_trim)) / delta;
		elevTrim = elevTrim > FlightControls.ELEVATOR.getMaximum() ? FlightControls.ELEVATOR.getMaximum() : 
				   elevTrim < FlightControls.ELEVATOR.getMinimum() ? FlightControls.ELEVATOR.getMinimum() : elevTrim;
		
		// Calculate trim pitch by using relation between flight path angle and angle of attack (theta = alpha + FPA)
		double alphaTrim = ((-CL_d_elev * CM_0) - (CM_d_elev * CL_trim) ) / delta;
		alphaTrim = alphaTrim > 0.18 ? 0.18 :
					alphaTrim < 0.0 ? 0.0 : alphaTrim;
		 
		double thetaTrim = alphaTrim + 0; // Zero for level flight
		
		// Recalculate w velocity for new angle of attack
		double wVelocityTrim = windParameters[0] * Math.sin(-alphaTrim);
		
		//==================================================== Throttle ==============================================================
		
		// Get max thrust from each engine, equate it with drag of aircraft to find trim throttle
		double maxThrust = 0.0;
		Set<Engine> engines = ab.getEngineList();
		for (Engine engine : engines) {
			engine.updateEngineState(initialControls, environmentParams, windParameters);
			maxThrust += engine.getThrust()[0]/initialControls.get(FlightControls.THROTTLE_1);
		}
		
		// Calculate trim throttle, limiting if necessary
		double throttleTrim = (Math.abs(drag))/(maxThrust);
		throttleTrim = throttleTrim > FlightControls.THROTTLE_1.getMaximum() ? FlightControls.THROTTLE_1.getMaximum() : 
			           throttleTrim < FlightControls.THROTTLE_1.getMinimum() ? FlightControls.THROTTLE_1.getMinimum() : throttleTrim;
		
		// Update initialControls and initialConditions
		initialConditions.put(InitialConditions.INITTHETA, -thetaTrim);
		initialConditions.put(InitialConditions.INITW, wVelocityTrim);
		
		initialControls.put(FlightControls.ELEVATOR, -elevTrim);
		
		initialControls.put(FlightControls.THROTTLE_1, throttleTrim);
		initialControls.put(FlightControls.THROTTLE_2, throttleTrim);
		initialControls.put(FlightControls.THROTTLE_3, throttleTrim);
		initialControls.put(FlightControls.THROTTLE_4, throttleTrim);
		
		if (!testMode) {
			Utilities.writeConfigFile(SimulationController.getSimConfigPath(), "InitialConditions", initialConditions);
			Utilities.writeConfigFile(SimulationController.getSimConfigPath(), "InitialControls", initialControls);
		} else {
			System.out.println(Trimming.outputTrimValues());
		}
	}
	
	public static String outputTrimValues() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("======================\n");
		sb.append(aircraft.getName()).append(" Trim Values:\n");
		sb.append("======================\n\n");
		
		sb.append(InitialConditions.INITTHETA.toString()).append(": ").append(initialConditions.get(InitialConditions.INITTHETA)).append("\n\n");
		
		sb.append(InitialConditions.INITW.toString()).append(": ").append(initialConditions.get(InitialConditions.INITW)).append("\n\n");
	
		sb.append(FlightControls.ELEVATOR.toString()).append(": ").append(initialControls.get(FlightControls.ELEVATOR)).append("\n\n");
		sb.append(FlightControls.THROTTLE_1.toString()).append(": ").append(initialControls.get(FlightControls.THROTTLE_1)).append("\n");
		sb.append(FlightControls.THROTTLE_2.toString()).append(": ").append(initialControls.get(FlightControls.THROTTLE_2)).append("\n");
		sb.append(FlightControls.THROTTLE_3.toString()).append(": ").append(initialControls.get(FlightControls.THROTTLE_3)).append("\n");
		sb.append(FlightControls.THROTTLE_4.toString()).append(": ").append(initialControls.get(FlightControls.THROTTLE_4)).append("\n");
		
		return sb.toString();
	}
}