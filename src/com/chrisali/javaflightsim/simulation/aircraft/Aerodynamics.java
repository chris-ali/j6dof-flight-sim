/*******************************************************************************
 * Copyright (C) 2016-2020 Christopher Ali
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *  If you have any questions about this project, you can visit
 *  the project's GitHub repository at: http://github.com/chris-ali/j6dof-flight-sim/
 ******************************************************************************/
package com.chrisali.javaflightsim.simulation.aircraft;

import java.util.Map;

import org.apache.commons.math3.analysis.interpolation.PiecewiseBicubicSplineInterpolatingFunction;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chrisali.javaflightsim.simulation.enviroment.EnvironmentParameters;
import com.chrisali.javaflightsim.simulation.flightcontrols.FlightControl;
import com.chrisali.javaflightsim.simulation.utilities.SixDOFUtilities;

/**
 * This class calculates aerodynamic forces and moments in the stability coordinate frame. The aerodynamic forces are then converted to the 
 * body frame to calculate accelerations and moments in {@link AccelAndMoments}. The stability derivatives are {@link LookupTable} objects,
 * which are either constant or linerally interpolatable 
 * 
 * @param EnumMap controls                            
 * @param windParameters 								
 * @param angularRates
 * @param double alphaDot
 * @param EnumMap environmentParams
 *  
 * @see Aircraft
 * @see StabilityDerivatives
 * @see PiecewiseBicubicSplineInterpolatingFunction
 * @see Source: Source: <i>Small Unmanned Aircraft: Theory and Practice by Beard, R.W. and McLain, T.W.</i>
 */
public class Aerodynamics {
	
	private static final Logger logger = LogManager.getLogger(Aerodynamics.class);
	
	private Aircraft aircraft;
	
	/**
	 * Aerodynamics constructor. Takes the aerodynamic parameters of an {@link Aircraft} object to generate aerodynamic forces and moments
	 * 
	 * @param aircraft
	 */
	public Aerodynamics(Aircraft aircraft) {this.aircraft = aircraft;}
	
	/**
	 *  Calculates the aircraft's total lift coefficient (CL)
	 *   
	 * @param angularRates
	 * @param windParameters
	 * @param controls
	 * @param alphaDot
	 * @param heightAGL
	 * @return CL
	 */
	private double calculateCL(double[] angularRates,
						  	   double[] windParameters,
						  	   Map<FlightControl, Double> controls,
						  	   double alphaDot,
						  	   double heightAGL) {
		double rotaryTerm = aircraft.getWingGeometry(WingGeometry.C_BAR)/(2*windParameters[0]);
		
		return calculateInterpStabDer(windParameters, controls, StabilityDerivatives.CL_ALPHA)*windParameters[2]*groundEffect(heightAGL)+
			   aircraft.getStabilityDerivative(StabilityDerivatives.CL_0).getValue()+	
			   aircraft.getStabilityDerivative(StabilityDerivatives.CL_Q).getValue()*angularRates[1]*rotaryTerm+
			   aircraft.getStabilityDerivative(StabilityDerivatives.CL_ALPHA_DOT).getValue()*alphaDot*rotaryTerm+
			   aircraft.getStabilityDerivative(StabilityDerivatives.CL_D_ELEV).getValue()*controls.get(FlightControl.ELEVATOR)+	
			   aircraft.getStabilityDerivative(StabilityDerivatives.CL_D_FLAP).getValue()*controls.get(FlightControl.FLAPS);		
	}
	
	/**
	 * Calculates the aircraft's total side force coefficient (CY)
	 * 
	 * @param windParameters
	 * @param controls
	 * @return CY
	 */
	private double calculateCY(double[] windParameters,
						 	   Map<FlightControl, Double> controls) {
		return aircraft.getStabilityDerivative(StabilityDerivatives.CY_BETA).getValue()*windParameters[1]+
			   aircraft.getStabilityDerivative(StabilityDerivatives.CY_D_RUD).getValue()*controls.get(FlightControl.RUDDER);	
	}
	
	/**
	 * Calculates the aircraft's total drag coefficient (CD)
	 * 
	 * @param windParameters
	 * @param controls
	 * @param heightAGL
	 * @return CD
	 */
	private double calculateCD(double[] windParameters,
					 	  	   Map<FlightControl, Double> controls,
					 	  	   double heightAGL) {
		return calculateInterpStabDer(windParameters, controls, StabilityDerivatives.CD_ALPHA)*Math.abs(windParameters[2])/groundEffect(heightAGL)+ // Need absolute value to prevent negative drag at negative alpha
			   aircraft.getStabilityDerivative(StabilityDerivatives.CD_0).getValue()+
			   aircraft.getStabilityDerivative(StabilityDerivatives.CD_D_FLAP).getValue()*controls.get(FlightControl.FLAPS)+
			   aircraft.getStabilityDerivative(StabilityDerivatives.CD_D_ELEV).getValue()*controls.get(FlightControl.ELEVATOR)+
			   aircraft.getStabilityDerivative(StabilityDerivatives.CD_D_GEAR).getValue()*controls.get(FlightControl.GEAR);		
	}
	
	/**
	 * Calculates the aircraft's total roll moment coefficient (Cl)
	 * 
	 * @param angularRates
	 * @param windParameters
	 * @param controls
	 * @return Croll
	 */
	private double calculateCRoll(double[] angularRates,
					  	    	  double[] windParameters,
					  	    	  Map<FlightControl, Double> controls) {
		double helixAngle = aircraft.getWingGeometry(WingGeometry.B_WING)/(2*windParameters[0]);
		
		return aircraft.getStabilityDerivative(StabilityDerivatives.CROLL_BETA).getValue()*windParameters[1]+
			   aircraft.getStabilityDerivative(StabilityDerivatives.CROLL_P).getValue()*angularRates[0]*helixAngle+
			   aircraft.getStabilityDerivative(StabilityDerivatives.CROLL_R).getValue()*angularRates[2]*helixAngle+
			   aircraft.getStabilityDerivative(StabilityDerivatives.CROLL_D_AIL).getValue()*controls.get(FlightControl.AILERON)+
			   aircraft.getStabilityDerivative(StabilityDerivatives.CROLL_D_RUD).getValue()*controls.get(FlightControl.RUDDER);
	}
	
	/**
	 * Calculates the aircraft's total pitch moment coefficient (CM)
	 * 
	 * @param angularRates
	 * @param windParameters
	 * @param controls
	 * @param alphaDot
	 * @return
	 */
	private double calculateCM(double[] angularRates,
						 	   double[] windParameters,
						 	   Map<FlightControl, Double> controls,
						 	   double alphaDot) {
		double rotaryTerm = aircraft.getWingGeometry(WingGeometry.C_BAR)/(2*windParameters[0]);
		
		return calculateInterpStabDer(windParameters, controls, StabilityDerivatives.CM_ALPHA)*windParameters[2]+
			   aircraft.getStabilityDerivative(StabilityDerivatives.CM_0).getValue()+
			   aircraft.getStabilityDerivative(StabilityDerivatives.CM_Q).getValue()*angularRates[1]*rotaryTerm+
			   aircraft.getStabilityDerivative(StabilityDerivatives.CM_ALPHA_DOT).getValue()*alphaDot*rotaryTerm+
			   aircraft.getStabilityDerivative(StabilityDerivatives.CM_D_ELEV).getValue()*controls.get(FlightControl.ELEVATOR)+
			   aircraft.getStabilityDerivative(StabilityDerivatives.CM_D_FLAP).getValue()*controls.get(FlightControl.FLAPS);
	}
	
	/**
	 * Calculates the aircraft's total yaw moment coefficient (CN) 
	 * 
	 * @param angularRates
	 * @param windParameters
	 * @param controls
	 * @return CN
	 */
	private double calculateCN(double[] angularRates,
						 	   double[] windParameters,
						 	   Map<FlightControl, Double> controls) {
		double helixAngle = aircraft.getWingGeometry(WingGeometry.B_WING)/(2*windParameters[0]);
		
		return aircraft.getStabilityDerivative(StabilityDerivatives.CN_BETA).getValue()*windParameters[1]+
			   aircraft.getStabilityDerivative(StabilityDerivatives.CN_P).getValue()*angularRates[0]*helixAngle+
			   aircraft.getStabilityDerivative(StabilityDerivatives.CN_R).getValue()*angularRates[2]*helixAngle+
			   aircraft.getStabilityDerivative(StabilityDerivatives.CN_D_AIL).getValue()*controls.get(FlightControl.AILERON)+
			   aircraft.getStabilityDerivative(StabilityDerivatives.CN_D_RUD).getValue()*controls.get(FlightControl.RUDDER);	
	}
	
	/**
	 * Gets the type of value contained in the specified key of the {@link StabilityDerivatives} EnumMap and 
	 * interpolates it using {@link LookupTable#interpolate(double, double)} 
	 *  
	 * @param windParameters
	 * @param controls
	 * @param stabDer
	 * @return interpStabDer
	 */
	public Double calculateInterpStabDer(double[] windParameters,
			 							 Map<FlightControl, Double> controls,
			 							 StabilityDerivatives stabDer) {
		Double interpStabDer;		
		LookupTable lookup = aircraft.getStabilityDerivative(stabDer);
		
		try {
			interpStabDer = lookup.interpolate(windParameters[2], controls.get(FlightControl.FLAPS));
		} catch (OutOfRangeException e) {
			logger.error("Number out of range for interpolation! Returning 0 for value.");
			return 0.0;
		}
		
		return interpStabDer;
	}
	
	/**
	 * If aircraft is within 1 wing span length of the ground, return a slight multiple adjustment to CL_alpha
	 * and CD_alpha to simulate aerodynamic benefits of ground effect
	 * 
	 * @param heightAGL
	 * @return adjustment to CL_alpha and CD_alpha
	 */
	private double groundEffect(double heightAGL) {
		double normalizedHeightAGL = heightAGL/aircraft.getWingGeometry(WingGeometry.B_WING);
		if (normalizedHeightAGL < 1.0)
			return 1 - (Math.atan(15*(normalizedHeightAGL-1)) / 10);
		else
			return 1.0;
	}
	
	/**
	 * Calculates aerodynamic forces experienced by the aircraft, converted from the wind frame to the body
	 * frame by using {@link SixDOFUtilities#wind2Body(double[])}
	 * 
	 * @param windParameters
	 * @param angularRates
	 * @param environmentParameters
	 * @param controls
	 * @param alphaDot
	 * @param heightAGL
	 * @return bodyForces
	 */
	public double[] calculateBodyForces(double[] windParameters,
									  	double[] angularRates,
										Map<EnvironmentParameters, Double> environmentParameters,
									    Map<FlightControl, Double> controls,
										double alphaDot,
										double heightAGL) {
		double qBar = environmentParameters.get(EnvironmentParameters.RHO)*Math.pow(windParameters[0], 2)/2;
		
		double[][] w2bDCM = SixDOFUtilities.wind2Body(windParameters);
		
		// Negative L and D to switch body directions and position in array swapped
		double[] aeroForces = {-qBar*calculateCD(windParameters, controls, heightAGL)*aircraft.getWingGeometry(WingGeometry.S_WING),
				   			    qBar*calculateCY(windParameters, controls)*aircraft.getWingGeometry(WingGeometry.S_WING),
							   -qBar*calculateCL(angularRates, windParameters, controls, alphaDot, heightAGL)*aircraft.getWingGeometry(WingGeometry.S_WING)};
		
		return new double[] {aeroForces[0]*w2bDCM[0][0]+aeroForces[1]*w2bDCM[0][1]+aeroForces[2]*w2bDCM[0][2],
							 aeroForces[0]*w2bDCM[1][0]+aeroForces[1]*w2bDCM[1][1]+aeroForces[2]*w2bDCM[1][2],
							 aeroForces[0]*w2bDCM[2][0]+aeroForces[1]*w2bDCM[2][1]+aeroForces[2]*w2bDCM[2][2]};
	}
	
	/**
	 * Calculates aerodynamic moments experienced by the aircraft
	 * 
	 * @param windParameters
	 * @param angularRates
	 * @param environmentParameters
	 * @param controls
	 * @param alphaDot
	 * @return aerodynamicMoments
	 */
	public double[] calculateAeroMoments(double[] windParameters,
									     double[] angularRates,
									     Map<EnvironmentParameters, Double> environmentParameters,
									     Map<FlightControl, Double> controls,
									     double alphaDot) {
		double qBar = environmentParameters.get(EnvironmentParameters.RHO)*Math.pow(windParameters[0], 2)/2;
		
		return new double[] {qBar*calculateCRoll(angularRates, windParameters, controls)*aircraft.getWingGeometry(WingGeometry.S_WING)*aircraft.getWingGeometry(WingGeometry.B_WING), 
							 qBar*calculateCM(angularRates, windParameters, controls, alphaDot)*aircraft.getWingGeometry(WingGeometry.S_WING)*aircraft.getWingGeometry(WingGeometry.C_BAR), 
						 	 qBar*calculateCN(angularRates, windParameters, controls)*aircraft.getWingGeometry(WingGeometry.S_WING)*aircraft.getWingGeometry(WingGeometry.B_WING)};					
	}
}
