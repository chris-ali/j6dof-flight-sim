package com.chrisali.javaflightsim.simulation.setup;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import com.chrisali.javaflightsim.controllers.SimulationController;
import com.chrisali.javaflightsim.simulation.aero.Aerodynamics;
import com.chrisali.javaflightsim.simulation.aero.StabilityDerivatives;
import com.chrisali.javaflightsim.simulation.aero.WingGeometry;
import com.chrisali.javaflightsim.simulation.aircraft.Aircraft;
import com.chrisali.javaflightsim.simulation.aircraft.MassProperties;
import com.chrisali.javaflightsim.simulation.controls.FlightControlType;
import com.chrisali.javaflightsim.simulation.enviroment.Environment;
import com.chrisali.javaflightsim.simulation.enviroment.EnvironmentParameters;
import com.chrisali.javaflightsim.simulation.propulsion.Engine;
import com.chrisali.javaflightsim.utilities.FileUtilities;

/**
 * Simple rudimentary method of longitudinally trimming an aircraft by statically equating forces and moments.
 * This calculates trim deflections of throttle and elevator needed for level flight. For trim pitch, flight path
 * angle and angle of attack are considered by using the following equation:
 * 
 *  <p>Angle of Attack = Pitch Angle + Flight Path Angle</p>
 * 
 * @author Christopher Ali
 * @see Principles of Flight Simulation - David Allerton (pp 170-1)
 */
public class Trimming {
	
	private static EnumMap<InitialConditions, Double> initialConditions;
	private static EnumMap<FlightControlType, Double> initialControls;
	private static Map<EnvironmentParameters, Double> environmentParams;
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
	 * @param controller
	 * @param testMode
	 */
	public static void trimSim(SimulationController controller, boolean testMode) {
		aircraft = controller.getAircraftBuilder().getAircraft();
		aero = new Aerodynamics(aircraft);
		
		initialConditions = controller.getInitialConditions();
		initialControls = controller.getInitialControls();
		
		environmentParams = Environment.getAndUpdateEnvironmentParams(new double[]{0,0,initialConditions.get(InitialConditions.INITD)});
		
		double alphaMin = -0.18, alphaMax = 0.18, throttleMin = 0.0, throttleMax = 1.0,
			   alphaTrim = 0.0, thetaTrim = 0.0, elevTrim = 0.0, throttleTrim = 0.0, wVelocityTrim = 0.0, 
			   lift = 0.0, drag = 0.0, totalThrust = 0.0, zForce = 100.0;
		
		double trueAirspeed = Math.sqrt(Math.pow(initialConditions.get(InitialConditions.INITU), 2) +
				  			  			Math.pow(initialConditions.get(InitialConditions.INITW), 2));
		
		double weight = aircraft.getMassProperty(MassProperties.TOTAL_MASS) * Environment.getGravity();
		double q = environmentParams.get(EnvironmentParameters.RHO)*Math.pow(trueAirspeed, 2)/2;
		double s = aircraft.getWingGeometry(WingGeometry.S_WING);
		
		int counter = 0;

		do {
			alphaTrim = (alphaMin + alphaMax) / 2;
			
			// Break out of loop if trim condition not satisfied after 100 attempts
			if(counter == 100) {
				System.err.println("Unable to trim elevator and pitch for given conditions!");
				break;
			}
		
			//=============================================== Pitch ================================================================
			
			// Calculate trim pitch by using (theta = alpha + FPA)
			thetaTrim = alphaTrim + 0; // Zero for level flight
			
			double[] windParameters = new double[] {trueAirspeed, 0, alphaTrim};
			
			double CL_alpha = aero.calculateInterpStabDer(windParameters, initialControls, StabilityDerivatives.CL_ALPHA);
			double CL_0 = aero.calculateInterpStabDer(windParameters, initialControls, StabilityDerivatives.CL_0);
			
			lift = q * s * ((CL_alpha * alphaTrim) + CL_0);
			
			double CD_alpha = aero.calculateInterpStabDer(windParameters, initialControls, StabilityDerivatives.CD_ALPHA);
			double CD_0 = aero.calculateInterpStabDer(windParameters, initialControls, StabilityDerivatives.CD_0);
			
			drag = q * s * ((CD_alpha * alphaTrim) + CD_0);
			
			// Calculate zForce to find trim alpha to zero forces
			zForce = (-lift * Math.cos(alphaTrim)) - (drag * Math.sin(alphaTrim)) + (weight * Math.cos(thetaTrim));
			
			if (zForce  > 0)
				alphaMin = alphaTrim;
			else
				alphaMax = alphaTrim;
					
			// Recalculate w velocity for new angle of attack
			wVelocityTrim = windParameters[0] * Math.sin(alphaTrim);
			
			//==================================================== Elevator ========================================================
			
			// Calculate trim elevator, limiting if necessary
			double CM_alpha = aero.calculateInterpStabDer(windParameters, initialControls, StabilityDerivatives.CM_ALPHA);
			double CM_d_elev = (double) aircraft.getStabilityDerivative(StabilityDerivatives.CM_D_ELEV);
			double CM_0 = (double) aircraft.getStabilityDerivative(StabilityDerivatives.CM_0);
			
			elevTrim = (CM_0 + (CM_alpha * alphaTrim)) / CM_d_elev;
			elevTrim = elevTrim > FlightControlType.ELEVATOR.getMaximum() ? FlightControlType.ELEVATOR.getMaximum() : 
					   elevTrim < FlightControlType.ELEVATOR.getMinimum() ? FlightControlType.ELEVATOR.getMinimum() : elevTrim;
					   
			counter++;
					   
		} while (Math.abs(zForce) > 1);

		//==================================================== Throttle ============================================================
		
		Set<Engine> engines = controller.getAircraftBuilder().getEngineList();
		
		drag = (drag * Math.cos(alphaTrim)) - (lift * Math.sin(alphaTrim)) + (weight * Math.sin(thetaTrim));
		
		counter = 0;
		
		do {
			throttleTrim = (throttleMin + throttleMax) / 2;
			
			// Break out of loop if trim condition not satisfied after 100 attempts
			if(counter == 100) {
				System.err.println("Unable to trim throttle for given conditions!");
				break;
			}
			
			initialControls.put(FlightControlType.THROTTLE_1, throttleTrim);
			initialControls.put(FlightControlType.THROTTLE_2, throttleTrim);
			initialControls.put(FlightControlType.THROTTLE_3, throttleTrim);
			initialControls.put(FlightControlType.THROTTLE_4, throttleTrim);
			
			// Get total thrust, equate it with drag of aircraft to find trim throttle
			totalThrust = 0.0;
			for (Engine engine : engines) {
				engine.updateEngineState(initialControls, environmentParams, new double[]{trueAirspeed,0,0});
				totalThrust += engine.getThrust()[0];
			}
			
			if (totalThrust < drag)
				throttleMin = throttleTrim;
			else
				throttleMax = throttleTrim;
			
			counter++;
			
		} while (Math.abs(totalThrust - drag) > 1);
		
		// Update initialControls and initialConditions
		initialConditions.put(InitialConditions.INITTHETA, thetaTrim);
		initialConditions.put(InitialConditions.INITW, wVelocityTrim);
		
		initialControls.put(FlightControlType.ELEVATOR, -elevTrim);
		initialControls.put(FlightControlType.AILERON, 0.0);
		initialControls.put(FlightControlType.RUDDER, 0.0);		
		
		// In test mode do not write any config settings to files
		if (!testMode) {
			FileUtilities.writeConfigFile(SimulationController.getSimConfigPath(), "InitialConditions", initialConditions);
			FileUtilities.writeConfigFile(SimulationController.getSimConfigPath(), "InitialControls", initialControls);
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
	
		sb.append(FlightControlType.ELEVATOR.toString()).append(": ").append(initialControls.get(FlightControlType.ELEVATOR)).append("\n\n");
		sb.append(FlightControlType.THROTTLE_1.toString()).append(": ").append(initialControls.get(FlightControlType.THROTTLE_1)).append("\n");
		sb.append(FlightControlType.THROTTLE_2.toString()).append(": ").append(initialControls.get(FlightControlType.THROTTLE_2)).append("\n");
		sb.append(FlightControlType.THROTTLE_3.toString()).append(": ").append(initialControls.get(FlightControlType.THROTTLE_3)).append("\n");
		sb.append(FlightControlType.THROTTLE_4.toString()).append(": ").append(initialControls.get(FlightControlType.THROTTLE_4)).append("\n");
		
		return sb.toString();
	}
}