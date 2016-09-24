package com.chrisali.javaflightsim.simulation.aero;

import java.util.Map;

import org.apache.commons.math3.analysis.interpolation.PiecewiseBicubicSplineInterpolatingFunction;
import org.apache.commons.math3.exception.OutOfRangeException;

import com.chrisali.javaflightsim.simulation.aircraft.Aircraft;
import com.chrisali.javaflightsim.simulation.aircraft.AircraftBuilder;
import com.chrisali.javaflightsim.simulation.controls.FlightControlType;
import com.chrisali.javaflightsim.simulation.enviroment.EnvironmentParameters;
import com.chrisali.javaflightsim.utilities.SixDOFUtilities;

/**
 * This class calculates aerodynamic forces and moments in the stability coordinate frame. The aerodynamic forces are then converted to the 
 * body frame to calculate accelerations and moments in {@link AccelAndMoments}. Depending on the stability derivatives specified upon aircraft creation
 * in {@link AircraftBuilder} the stability derivatives can be either a constant double or interpolated linearly using a lookup table
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
						  	   Map<FlightControlType, Double> controls,
						  	   double alphaDot,
						  	   double heightAGL) {
		double rotaryTerm = aircraft.getWingGeometry(WingGeometry.C_BAR)/(2*windParameters[0]);
		
		return calculateInterpStabDer(windParameters, controls, StabilityDerivatives.CL_ALPHA)*windParameters[2]*groundEffect(heightAGL)+
			   (Double)aircraft.getStabilityDerivative(StabilityDerivatives.CL_0)+	
			   (Double)aircraft.getStabilityDerivative(StabilityDerivatives.CL_Q)*angularRates[1]*rotaryTerm+
			   (Double)aircraft.getStabilityDerivative(StabilityDerivatives.CL_ALPHA_DOT)*alphaDot*rotaryTerm+
			   (Double)aircraft.getStabilityDerivative(StabilityDerivatives.CL_D_ELEV)*controls.get(FlightControlType.ELEVATOR)+	
			   (Double)aircraft.getStabilityDerivative(StabilityDerivatives.CL_D_FLAP)*controls.get(FlightControlType.FLAPS);		
	}
	
	/**
	 * Calculates the aircraft's total side force coefficient (CY)
	 * 
	 * @param windParameters
	 * @param controls
	 * @return CY
	 */
	private double calculateCY(double[] windParameters,
						 	   Map<FlightControlType, Double> controls) {
		return (Double)aircraft.getStabilityDerivative(StabilityDerivatives.CY_BETA)*windParameters[1]+
			   (Double)aircraft.getStabilityDerivative(StabilityDerivatives.CY_D_RUD)*controls.get(FlightControlType.RUDDER);	
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
					 	  	   Map<FlightControlType, Double> controls,
					 	  	   double heightAGL) {
		return calculateInterpStabDer(windParameters, controls, StabilityDerivatives.CD_ALPHA)*Math.abs(windParameters[2])/groundEffect(heightAGL)+ // Need absolute value to prevent negative drag at negative alpha
			   (Double)aircraft.getStabilityDerivative(StabilityDerivatives.CD_0)+
			   (Double)aircraft.getStabilityDerivative(StabilityDerivatives.CD_D_FLAP)*controls.get(FlightControlType.FLAPS)+
			   (Double)aircraft.getStabilityDerivative(StabilityDerivatives.CD_D_ELEV)*controls.get(FlightControlType.ELEVATOR)+
			   (Double)aircraft.getStabilityDerivative(StabilityDerivatives.CD_D_GEAR)*controls.get(FlightControlType.GEAR);		
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
					  	    	  Map<FlightControlType, Double> controls) {
		double helixAngle = aircraft.getWingGeometry(WingGeometry.B_WING)/(2*windParameters[0]);
		
		return (Double)aircraft.getStabilityDerivative(StabilityDerivatives.CROLL_BETA)*windParameters[1]+
			   (Double)aircraft.getStabilityDerivative(StabilityDerivatives.CROLL_P)*angularRates[0]*helixAngle+
			   (Double)aircraft.getStabilityDerivative(StabilityDerivatives.CROLL_R)*angularRates[2]*helixAngle+
			   (Double)aircraft.getStabilityDerivative(StabilityDerivatives.CROLL_D_AIL)*controls.get(FlightControlType.AILERON)+
			   (Double)aircraft.getStabilityDerivative(StabilityDerivatives.CROLL_D_RUD)*controls.get(FlightControlType.RUDDER);
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
						 	   Map<FlightControlType, Double> controls,
						 	   double alphaDot) {
		double rotaryTerm = aircraft.getWingGeometry(WingGeometry.C_BAR)/(2*windParameters[0]);
		
		return calculateInterpStabDer(windParameters, controls, StabilityDerivatives.CM_ALPHA)*windParameters[2]+
			   (Double)aircraft.getStabilityDerivative(StabilityDerivatives.CM_0)+
			   (Double)aircraft.getStabilityDerivative(StabilityDerivatives.CM_Q)*angularRates[1]*rotaryTerm+
			   (Double)aircraft.getStabilityDerivative(StabilityDerivatives.CM_ALPHA_DOT)*alphaDot*rotaryTerm+
			   (Double)aircraft.getStabilityDerivative(StabilityDerivatives.CM_D_ELEV)*controls.get(FlightControlType.ELEVATOR)+
			   (Double)aircraft.getStabilityDerivative(StabilityDerivatives.CM_D_FLAP)*controls.get(FlightControlType.FLAPS);
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
						 	   Map<FlightControlType, Double> controls) {
		double helixAngle = aircraft.getWingGeometry(WingGeometry.B_WING)/(2*windParameters[0]);
		
		return (Double)aircraft.getStabilityDerivative(StabilityDerivatives.CN_BETA)*windParameters[1]+
			   (Double)aircraft.getStabilityDerivative(StabilityDerivatives.CN_P)*angularRates[0]*helixAngle+
			   (Double)aircraft.getStabilityDerivative(StabilityDerivatives.CN_R)*angularRates[2]*helixAngle+
			   (Double)aircraft.getStabilityDerivative(StabilityDerivatives.CN_D_AIL)*controls.get(FlightControlType.AILERON)+
			   (Double)aircraft.getStabilityDerivative(StabilityDerivatives.CN_D_RUD)*controls.get(FlightControlType.RUDDER);	
	}
	
	/**
	 * Gets the type of value contained in the specified key of the {@link StabilityDerivatives} EnumMap, 
	 * interpolate it if its type is {@link PiecewiseBicubicSplineInterpolatingFunction}, or simply return it
	 * if its type is Double 
	 *  
	 * @param windParameters
	 * @param controls
	 * @param stabDer
	 * @return interpStabDer
	 */
	public Double calculateInterpStabDer(double[] windParameters,
			 							 Map<FlightControlType, Double> controls,
			 							 StabilityDerivatives stabDer) {
		Double interpStabDer;
		PiecewiseBicubicSplineInterpolatingFunction pbsif;
		
		// If object is of type Double, return that value, otherwise create an interpolating function and
		// get the interpolated Double value 
		if (aircraft.getStabilityDerivative(stabDer).getClass().getName().equals("java.lang.Double"))
			interpStabDer = (Double)aircraft.getStabilityDerivative(stabDer);
		else {
			pbsif = (PiecewiseBicubicSplineInterpolatingFunction)aircraft.getStabilityDerivative(stabDer);
			try {
				interpStabDer = pbsif.value(windParameters[2], controls.get(FlightControlType.FLAPS));
			} catch (OutOfRangeException e) {
				System.err.println("Number out of range for interpolation! Returning 0 for value.");
				return 0.0;
			}
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
									    Map<FlightControlType, Double> controls,
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
									     Map<FlightControlType, Double> controls,
									     double alphaDot) {
		double qBar = environmentParameters.get(EnvironmentParameters.RHO)*Math.pow(windParameters[0], 2)/2;
		
		return new double[] {qBar*calculateCRoll(angularRates, windParameters, controls)*aircraft.getWingGeometry(WingGeometry.S_WING)*aircraft.getWingGeometry(WingGeometry.B_WING), 
							 qBar*calculateCM(angularRates, windParameters, controls, alphaDot)*aircraft.getWingGeometry(WingGeometry.S_WING)*aircraft.getWingGeometry(WingGeometry.C_BAR), 
						 	 qBar*calculateCN(angularRates, windParameters, controls)*aircraft.getWingGeometry(WingGeometry.S_WING)*aircraft.getWingGeometry(WingGeometry.B_WING)};					
	}
}
