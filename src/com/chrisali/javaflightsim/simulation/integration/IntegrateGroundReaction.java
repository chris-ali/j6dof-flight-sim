package com.chrisali.javaflightsim.simulation.integration;

import java.util.Map;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;

import com.chrisali.javaflightsim.simulation.aircraft.Aircraft;
import com.chrisali.javaflightsim.simulation.aircraft.GroundReaction;
import com.chrisali.javaflightsim.simulation.aircraft.MassProperties;
import com.chrisali.javaflightsim.simulation.controls.FlightControls;
import com.chrisali.javaflightsim.utilities.SixDOFUtilities;

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
	
	private double[] tirePosition			   = new double[3];
	private double[] tireVelocity			   = new double[3];
	
	// Forces and Moments
	private double[] noseGroundForces 		   = new double[3];
	private double[] leftGroundForces 		   = new double[3];
	private double[] rightGroundForces 		   = new double[3];
	
	private double[] totalGroundForces 		   = new double[3];
	private double[] totalGroundMoments		   = new double[3];
	
	// Integrator Fields
	private ClassicalRungeKuttaIntegrator integrator;
	private double t;
	private double[] integratorConfig		   = new double[3];
	private double[] groundReactionDerivatives = new double[6];
	private double[] y					       = new double[6];
	private double[] y0					       = new double[6];
	
	
	// 6DOF Integration Results
	private double[] linearVelocities 		  = new double[3];
	private double[] NEDPosition      		  = new double[3];
	private double[] eulerAngles      		  = new double[3];
	private double[] angularRates     		  = new double[3];
	
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
		
		// "Initial conditions"
		for (int i = 0; i < y0.length; i++)
			y0[i] = 0.0;
		
		integrator = new ClassicalRungeKuttaIntegrator(integratorConfig[2]);
		t = integratorConfig[0];
		
		updateDerivatives(y);
	}
	
	private class GroundReactionEquations implements FirstOrderDifferentialEquations {
		@Override
		public void computeDerivatives(double t, double[] y, double[] yDot) {
			for (int i = 0; i < groundReactionDerivatives.length; i++)
				yDot[i] = groundReactionDerivatives[i];			
		}

		@Override
		public int getDimension() {return 6;}
	}
	
	private double[] updateDerivatives(double[] y) {
		double[] yDot = new double[6];
		
		// If tire position > 0, tire is still airborne and no forces should be applied 
		for (int i = 0; i < tirePosition.length; i++) {
			if (tirePosition[i] > 0) 
				yDot[2*i+1] = y[2*i+1] = 0;
		}
		
		// Nose
		yDot[0] = y[1];
		yDot[1] =  (- groundReaction.get(GroundReaction.NOSE_DAMPING)/mass  * y[1]) 
				 	- (groundReaction.get(GroundReaction.NOSE_SPRING)/mass  * y[0])
				 	+ noseGroundForces[2]/mass;
		
		// Left Main
		yDot[2] = y[3];
		yDot[3] =  (- groundReaction.get(GroundReaction.LEFT_DAMPING)/mass  * y[3]) 
				 	- (groundReaction.get(GroundReaction.LEFT_SPRING)/mass  * y[2])
				 	+ leftGroundForces[2]/mass;
		
		// Right Main
		yDot[4] = y[5];
		yDot[5] =  (- groundReaction.get(GroundReaction.RIGHT_DAMPING)/mass * y[5]) 
				 	- (groundReaction.get(GroundReaction.RIGHT_SPRING)/mass * y[4])
				 	+ rightGroundForces[2]/mass;
		
		return yDot;
	}
	
	private void calculateTirePositionsAndVelocities() {
		double[][] dirCosMat = SixDOFUtilities.body2Ned(eulerAngles);
		double[] gearUvwPostion = new double[3];
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
			
			// Transpose of body2Ned matrix to convert NED back to body coordinates plus gear position relative to CG
			gearUvwPostion[0] = (NEDPosition[0]*dirCosMat[0][0]+NEDPosition[1]*dirCosMat[1][0]+NEDPosition[2]*dirCosMat[2][0]) + gearRelativeCG[0];    // U (ft)
			gearUvwPostion[1] = (NEDPosition[0]*dirCosMat[0][1]+NEDPosition[1]*dirCosMat[1][1]+NEDPosition[2]*dirCosMat[2][1]) + gearRelativeCG[1];    // V (ft)
			gearUvwPostion[2] = (NEDPosition[0]*dirCosMat[0][2]+NEDPosition[1]*dirCosMat[1][2]+NEDPosition[2]*dirCosMat[2][2]) + gearRelativeCG[2];    // W (ft)
			
			// Take the cross product of angularRates and gear position relative to CG and add them to linear velocities
			Vector3D angularRatesVector = new Vector3D(angularRates);
			Vector3D gearRelativeCGVector = new Vector3D(gearRelativeCG);
			double[] tangentialVelocity = Vector3D.crossProduct(angularRatesVector, gearRelativeCGVector).toArray();
			
			gearUvwVelocity[0] = linearVelocities[0] + tangentialVelocity[0];
			gearUvwVelocity[1] = linearVelocities[1] + tangentialVelocity[1];
			gearUvwVelocity[2] = linearVelocities[2] + tangentialVelocity[2];
			
			// 3rd row of body2Ned matrix (D) minus terrain height is the height of the landing gear above ground
			tirePosition[i]  = -(gearUvwPostion[0]*dirCosMat[2][0]+gearUvwPostion[1]*dirCosMat[2][1]+gearUvwPostion[2]*dirCosMat[2][2]) - terrainHeight;   // D (ft)
			tireVelocity[i]  =  (gearUvwVelocity[0]*dirCosMat[2][0]+gearUvwVelocity[1]*dirCosMat[2][1]+gearUvwVelocity[2]*dirCosMat[2][2]);                // D (ft/sec)
		}
	}
	
	private void calculateTotalGroundForces() {
		// X Forces
		// Use static coefficient of friction if forward velocity is near 0 
		if (linearVelocities[0] > 0.5) {
			leftGroundForces[0]  = - groundReactionDerivatives[3] * TIRE_STATIC_FRICTION * mass;
			rightGroundForces[0] = - groundReactionDerivatives[5] * TIRE_STATIC_FRICTION * mass;
		} else {
			leftGroundForces[0]  = - groundReactionDerivatives[3] * TIRE_ROLLING_FRICTION * mass;
			rightGroundForces[0] = - groundReactionDerivatives[5] * TIRE_ROLLING_FRICTION * mass;
		}
		
		if (controls.get(FlightControls.BRAKE_L) > 0)
			leftGroundForces[0]  += groundReaction.get(GroundReaction.BRAKING_FORCE) * controls.get(FlightControls.BRAKE_L);
		
		if (controls.get(FlightControls.BRAKE_R) > 0)
			rightGroundForces[0] += groundReaction.get(GroundReaction.BRAKING_FORCE) * controls.get(FlightControls.BRAKE_R);
		
		// Y Forces		
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
				forceVector = new Vector3D(noseGroundForces);
				break;
			case 2:
				gearRelativeCGVector = new Vector3D(new double[]{groundReaction.get(GroundReaction.RIGHT_X),
																 groundReaction.get(GroundReaction.RIGHT_Y),
																 groundReaction.get(GroundReaction.RIGHT_Z)});
				forceVector = new Vector3D(noseGroundForces);
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
	
	public void integrateStep() {
		calculateTirePositionsAndVelocities();
		
		groundReactionDerivatives = updateDerivatives(new double[] {tireVelocity[0],tirePosition[0],
																	tireVelocity[1],tirePosition[1],
																	tireVelocity[2],tirePosition[2]});
		// Run a single step of integration
		y = integrator.singleStep(new GroundReactionEquations(), // derivatives
								  t, 		  					 // start time
								  y0, 		  					 // initial conditions
								  t+integratorConfig[1]);   	 // end time (t+dt)
		
		calculateTotalGroundForces();
		calculateTotalGroundMoments();
		
		t += integratorConfig[1];
	}
	
	public double[] getTotalGroundForces() {return totalGroundForces;}

	public double[] getTotalGroundMoments() {return totalGroundMoments;}
}
