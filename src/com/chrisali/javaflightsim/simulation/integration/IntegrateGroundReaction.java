package com.chrisali.javaflightsim.simulation.integration;

import java.util.Map;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;

import com.chrisali.javaflightsim.otw.RunWorld;
import com.chrisali.javaflightsim.simulation.aircraft.Aircraft;
import com.chrisali.javaflightsim.simulation.aircraft.GroundReaction;
import com.chrisali.javaflightsim.simulation.aircraft.MassProperties;
import com.chrisali.javaflightsim.simulation.controls.FlightControls;
import com.chrisali.javaflightsim.utilities.SixDOFUtilities;

/**
 * This class contains calculations needed to model the force and moment reactions between the aircraft
 * and its landing gear. It uses a spring-mass-damper system which is modeled as three second order 
 * differential equations integrated simultaneously, converted to first order equations by use of 
 * state-space methods so that {@link ClassicalRungeKuttaIntegrator} can be used. The integration runs
 * as single steps inside of {@link Integrate6DOFEquations} own integration, and the calculated forces 
 * and moments are fed back into the 6DOF integrator to calculate the total accelerations and moments for
 * the aircraft. 
 * 
 * @author Christopher Ali
 *
 */
public class IntegrateGroundReaction {
	// Tire Properties
	private static final double TIRE_STATIC_FRICTION  = 0.8;
	private static final double TIRE_ROLLING_FRICTION = 0.01;
	
	// Aircraft Properties
	private double mass;
	private Map<FlightControls, Double> controls;
	private Map<GroundReaction, Double> groundReaction;
	
	// Positions
	private double   terrainHeight;
	
	private double[] tirePosition			   = new double[3]; //{nose, left, right} [ft]
	private double[] tireVelocity			   = new double[3]; //{nose, left, right} [ft/sec]
	
	// Forces and Moments
	private double[] noseGroundForces 		   = new double[3]; //{Fx, Fy, Fz} [lbf]
	private double[] leftGroundForces 		   = new double[3]; //{Fx, Fy, Fz} [lbf]
	private double[] rightGroundForces 		   = new double[3]; //{Fx, Fy, Fz} [lbf]
	
	private double[] totalGroundForces 		   = new double[3]; //{Fx, Fy, Fz} [lbf]
	private double[] totalGroundMoments		   = new double[3]; //{Fx, Fy, Fz} [lbf]
	
	// Integrator Fields
	private ClassicalRungeKuttaIntegrator integrator;
	private double   t;
	private double[] integratorConfig		   = new double[3];
	private double[] groundReactionDerivatives = new double[6];
	private double[] y					       = new double[6];
	private double[] y0					       = new double[6];
	
	// 6DOF Integration Results
	private double[] linearVelocities 		  = new double[3];
	private double[] NEDPosition      		  = new double[3];
	private double[] eulerAngles      		  = new double[3];
	private double[] angularRates     		  = new double[3];
	
	/**
	 * Constructor for ground reaction integrator; uses references to integrated states from 
	 * {@link Integrate6DOFEquations} as well as terrain height received from the 
	 * out-the-window display view ({@link RunWorld})
	 * 
	 * @param linearVelocities
	 * @param NEDPosition
	 * @param eulerAngles
	 * @param angularRates
	 * @param integratorConfig
	 * @param aircraft
	 * @param controls
	 * @param terrainHeight
	 */
	public IntegrateGroundReaction(double[] linearVelocities,
								   double[] NEDPosition,
								   double[] eulerAngles,
								   double[] angularRates,
								   double[] integratorConfig,
								   Aircraft aircraft,
								   Map<FlightControls, Double> controls,
								   double terrainHeight) {

		this.linearVelocities = linearVelocities;
		this.NEDPosition = NEDPosition;
		this.eulerAngles = eulerAngles;
		this.angularRates = angularRates;
		
		this.terrainHeight = terrainHeight;
		
		this.controls = controls;
		this.groundReaction = aircraft.getGroundReaction();
		this.mass = aircraft.getMassProps().get(MassProperties.TOTAL_MASS);
		
		this.integratorConfig = integratorConfig;
		
		// "Initial conditions" are zeroed for now; on ground trimming needs pre-loading of gear
		for (int i = 0; i < y0.length; i++)
			y0[i] = 0.0;
		
		integrator = new ClassicalRungeKuttaIntegrator(integratorConfig[2]);
		t = integratorConfig[0];
		
		updateDerivatives(y);
	}
	
	/**
	 * Ground reaction derivatives integrated to determine ground forces and moments; 
	 * {@link IntegrateGroundReaction#updateDerivatives(double[])} is called in each step of
	 * integration to recalculate derivatives, which are then passed into the yDot[] array 
	 * programmatically
	 * 
	 * @author Christopher Ali
	 *
	 */
	private class GroundReactionEquations implements FirstOrderDifferentialEquations {
		@Override
		public void computeDerivatives(double t, double[] y, double[] yDot) {
			for (int i = 0; i < groundReactionDerivatives.length; i++)
				yDot[i] = groundReactionDerivatives[i];			
		}

		@Override
		public int getDimension() {return 6;}
	}
	
	/**
	 * Calculates the derivatives for each landing gear using second order simple spring-mass-damper
	 * ODEs converted to first order ODEs in state space form 
	 * 
	 * @param y
	 * @return yDot[] First order converted spring mass equations represented as state space equations
	 */
	private double[] updateDerivatives(double[] y) {
		double[] yDot = new double[6];
		
		// If tire position > 0, tire is still airborne and no forces should be applied 
		for (int i = 0; i < tirePosition.length; i++) {
			if (tirePosition[i] > 0.01) 
				yDot[2*i+1] = y[2*i+1] = 0;
		}
		
		// Nose
		yDot[0] = y[1];
		yDot[1] =  (- groundReaction.get(GroundReaction.NOSE_DAMPING)/mass * y[1]) 
				 	- (groundReaction.get(GroundReaction.NOSE_SPRING)/mass * y[0])
				 	+ noseGroundForces[2]/(mass*1.25); // 25% increase in mass to prevent runaway force
		
		// Left Main
		yDot[2] = y[3];
		yDot[3] =  (- groundReaction.get(GroundReaction.LEFT_DAMPING)/mass * y[3]) 
				 	- (groundReaction.get(GroundReaction.LEFT_SPRING)/mass * y[2])
				 	+ leftGroundForces[2]/(mass*1.25); // 25% increase in mass to prevent runaway force
		
		// Right Main
		yDot[4] = y[5];
		yDot[5] =  (- groundReaction.get(GroundReaction.RIGHT_DAMPING)/mass * y[5]) 
				 	- (groundReaction.get(GroundReaction.RIGHT_SPRING)/mass * y[4])
				 	+ rightGroundForces[2]/(mass*1.25); // 25% increase in mass to prevent runaway force
		
		return yDot;
	}
	
	/**
	 * Calculates the height and vertical velocity of each tire relative to the NED frame, used to 
	 * calculate ground reaction derivatives and forces 
	 */
	private void calculateTirePositionsAndVelocities() {
		double[][] dirCosMat = SixDOFUtilities.body2Ned(eulerAngles);
		double[] gearUvwVelocity = new double[3];
		double[] gearRelativeCG; // Position of {nose, left, right} gear relative to CG position
		
		for (int i = 0; i < 3; i++) {
			// Assign body gear positions depending on stage of loop
			switch(i) {
			case 0:
				gearRelativeCG = new double[]{groundReaction.get(GroundReaction.NOSE_X),
											  groundReaction.get(GroundReaction.NOSE_Y),
											  groundReaction.get(GroundReaction.NOSE_Z)};
				break;
			case 1:
				gearRelativeCG = new double[]{groundReaction.get(GroundReaction.LEFT_X),
											  groundReaction.get(GroundReaction.LEFT_Y),
											  groundReaction.get(GroundReaction.LEFT_Z)};
				break;
			case 2:
				gearRelativeCG = new double[]{groundReaction.get(GroundReaction.RIGHT_X),
											  groundReaction.get(GroundReaction.RIGHT_Y),
											  groundReaction.get(GroundReaction.RIGHT_Z)};
				break;
			default:
				gearRelativeCG = new double[]{0, 0, 0};
				break;
			}
			
			// Take the cross product of angularRates and gear position relative to CG and add them to linear velocities
			Vector3D angularRatesVector = new Vector3D(angularRates);
			Vector3D gearRelativeCGVector = new Vector3D(gearRelativeCG);
			double[] tangentialVelocity = Vector3D.crossProduct(angularRatesVector, gearRelativeCGVector).toArray();
			
			gearUvwVelocity[0] = linearVelocities[0] + tangentialVelocity[0];
			gearUvwVelocity[1] = linearVelocities[1] + tangentialVelocity[1];
			gearUvwVelocity[2] = linearVelocities[2] + tangentialVelocity[2];
			
			// 3rd row of body2Ned matrix (D) plus (altitude minus terrain height) is the height of the landing gear above ground
			tirePosition[i]  = -(gearRelativeCG[0]*dirCosMat[2][0]+gearRelativeCG[1]*dirCosMat[2][1]+gearRelativeCG[2]*dirCosMat[2][2]) + (NEDPosition[2]-terrainHeight);   // D (ft)
			tireVelocity[i]  = -(gearUvwVelocity[0]*dirCosMat[2][0]+gearUvwVelocity[1]*dirCosMat[2][1]+gearUvwVelocity[2]*dirCosMat[2][2]);                // D (ft/sec)
		}
	}
	
	/**
	 * Calculates each component of force for each landing gear on the aircraft, which is then used to calculate
	 * ground reaction derivatives and moments
	 */
	private void calculateTotalGroundForces() {
		// X Forces
		// Use static coefficient of friction if forward velocity is near 0 
		if (linearVelocities[0] < 0.5) {
			leftGroundForces[0]  = - groundReactionDerivatives[3] * TIRE_STATIC_FRICTION * mass;
			rightGroundForces[0] = - groundReactionDerivatives[5] * TIRE_STATIC_FRICTION * mass;
		} else {
			leftGroundForces[0]  = - groundReactionDerivatives[3] * TIRE_ROLLING_FRICTION * mass;
			rightGroundForces[0] = - groundReactionDerivatives[5] * TIRE_ROLLING_FRICTION * mass;
		}
		
		// Braking
		if (controls.get(FlightControls.BRAKE_L) > 0)
			leftGroundForces[0]  -= groundReaction.get(GroundReaction.BRAKING_FORCE) * controls.get(FlightControls.BRAKE_L);

		if (controls.get(FlightControls.BRAKE_R) > 0)
			rightGroundForces[0] -= groundReaction.get(GroundReaction.BRAKING_FORCE) * controls.get(FlightControls.BRAKE_R);
		
		// Y Forces				// Nosewheel steering friction force based on a fraction of the rudder deflection to the maximum deflection
		noseGroundForces[1]  =   groundReactionDerivatives[1] * TIRE_STATIC_FRICTION * mass 
							  * (controls.get(FlightControls.RUDDER)/FlightControls.RUDDER.getMaximum());  
		leftGroundForces[1]  = - groundReactionDerivatives[3] * TIRE_STATIC_FRICTION * mass;
		rightGroundForces[1] =   groundReactionDerivatives[5] * TIRE_STATIC_FRICTION * mass;
		
		// Z Forces
		noseGroundForces[2]  = groundReactionDerivatives[1] * mass;
		leftGroundForces[2]  = groundReactionDerivatives[3] * mass;
		rightGroundForces[2] = groundReactionDerivatives[5] * mass;
		
		// Summation of Forces
		for (int i = 0; i < 3; i ++)
			totalGroundForces[i] = leftGroundForces[i] + rightGroundForces[i] + noseGroundForces[i];
	}
	
	/**
	 * Calculates each landing gear object's moments about the center of gravity using gear positions relative
	 * to the center of gravity and ground reaction forces
	 */
	private void calculateTotalGroundMoments() {
		double[] tempTotalGroundMoments = new double[]{0, 0, 0};
		Vector3D forceVector;
		Vector3D gearRelativeCGVector;
		
		for (int i = 0; i < 3; i++) {
			// Assign body gear force and arm vectors depending on stage of loop
			switch(i) {
			case 0:
				gearRelativeCGVector = new Vector3D(new double[]{groundReaction.get(GroundReaction.NOSE_X),
																 groundReaction.get(GroundReaction.NOSE_Y),
																 groundReaction.get(GroundReaction.NOSE_Z)});
				forceVector = new Vector3D(noseGroundForces);
				break;
			case 1:
				gearRelativeCGVector = new Vector3D(new double[]{groundReaction.get(GroundReaction.LEFT_X),
																 groundReaction.get(GroundReaction.LEFT_Y),
																 groundReaction.get(GroundReaction.LEFT_Z)});
				forceVector = new Vector3D(leftGroundForces);
				break;
			case 2:
				gearRelativeCGVector = new Vector3D(new double[]{groundReaction.get(GroundReaction.RIGHT_X),
																 groundReaction.get(GroundReaction.RIGHT_Y),
																 groundReaction.get(GroundReaction.RIGHT_Z)});
				forceVector = new Vector3D(rightGroundForces);
				break;
			default:
				gearRelativeCGVector = new Vector3D(new double[]{0, 0, 0});
				forceVector = new Vector3D(new double[]{0, 0, 0});
				break;
			}
		
			// Take the cross product of force and arm vectors and add them to total moments 
			for (int j = 0; j < tempTotalGroundMoments.length; j ++)
				tempTotalGroundMoments[j] += Vector3D.crossProduct(forceVector, gearRelativeCGVector).toArray()[j];
		}
		
		totalGroundMoments = tempTotalGroundMoments;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Z Position: ").append(NEDPosition[2]).append("\n");
		
		sb.append("Tire Positions {n, l, r}: [");
		for (int i = 0; i < 3; i++) {
			sb.append(tirePosition[i]);
			if (i < 2)
				sb.append(", ");
		}
		sb.append("]\n");
		
		sb.append("Tire Velocities {n, l, r}: [");
		for (int i = 0; i < 3; i++) {
			sb.append(tireVelocity[i]);
			if (i < 2)
				sb.append(", ");
		}
		sb.append("]\n");
		
		sb.append("Ground Forces {Fx, Fy, Fz}: [");
		for (int i = 0; i < 3; i++) {
			sb.append((int) getTotalGroundForces()[i]);
			if (i < 2)
				sb.append(", ");
		}
		sb.append("]\n");
		
		sb.append("Ground Moments {L, M, N}: [");
		for (int i = 0; i < 3; i++) {
			sb.append((int) getTotalGroundMoments()[i]);
			if (i < 2)
				sb.append(", ");
		}
		sb.append("]");
		
		return sb.toString();
	}
	
	/**
	 * Calculates the positions and velocities of each landing gear on the aircraft, calculates derivatives for
	 * the next step of integration, runs the next step of integration and then calculates ground forces and moments
	 * based on the results 
	 */
	public void integrateStep() {
		calculateTirePositionsAndVelocities();
		
		groundReactionDerivatives = updateDerivatives(new double[] {tirePosition[0],tireVelocity[0],
																	tirePosition[1],tireVelocity[1],
																	tirePosition[2],tireVelocity[2]});
		// Run a single step of integration
		y = integrator.singleStep(new GroundReactionEquations(), // derivatives
								  t, 		  					 // start time
								  y0, 		  					 // initial conditions
								  t+integratorConfig[1]);   	 // end time (t+dt)
		
		calculateTotalGroundForces();
		calculateTotalGroundMoments();
		
		t += integratorConfig[1];
	}
	
	/**
	 * @return Array of total forces due to ground reaction  
	 */
	public double[] getTotalGroundForces() {return totalGroundForces;}

	/**
	 * @return Array of total moments due to ground reaction  
	 */
	public double[] getTotalGroundMoments() {return totalGroundMoments;}
}
