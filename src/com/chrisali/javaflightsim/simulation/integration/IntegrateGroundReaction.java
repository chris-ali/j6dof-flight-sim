package com.chrisali.javaflightsim.simulation.integration;

import java.util.Map;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.ode.FirstOrderConverter;
import org.apache.commons.math3.ode.SecondOrderDifferentialEquations;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;

import com.chrisali.javaflightsim.simulation.aircraft.GroundReaction;
import com.chrisali.javaflightsim.simulation.controls.FlightControls;
import com.chrisali.javaflightsim.utilities.SixDOFUtilities;

public class IntegrateGroundReaction {
	
	private static final double TIRE_STATIC_FRICTION  = 0.8;
	private static final double TIRE_ROLLING_FRICTION = 0.01;
	
	private double brakingForce;
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
	private double[] integratorConfig		   = new double[3];
	private double[] groundReactionDerivatives = new double[3]; //"Gear strut force"
	private double[] y					       = new double[3];
	private double[] y0					       = new double[3];
	private double[] yDot				       = new double[3];
	
	// 6DOF Integration Results
	private double[] linearVelocities 		  = new double[3];
	private double[] NEDPosition      		  = new double[3];
	private double[] eulerAngles      		  = new double[3];
	private double[] angularRates     		  = new double[3];
	
	private double t;
	
	public IntegrateGroundReaction(double[] linearVelocities, 
								   double[] NEDPosition,
								   double[] eulerAngles, 
								   double[] angularRates,
								   double[] integratorConfig,
								   Map<FlightControls, Double> controls,
								   double terrainHeight) {

		this.linearVelocities = linearVelocities;
		this.NEDPosition = NEDPosition;
		this.eulerAngles = eulerAngles;
		this.angularRates = angularRates;
		
		this.terrainHeight = terrainHeight;
		this.controls = controls;
		
		this.integratorConfig = integratorConfig;
		integrator = new ClassicalRungeKuttaIntegrator(integratorConfig[2]);
		t = integratorConfig[0];
		
		updateDerivatives(y, yDot);
	}
	
	private class GroundReactionEquations implements SecondOrderDifferentialEquations {
		@Override
		public void computeSecondDerivatives(double t, double[] y, double[] yDot, double[] yDDot) {
			for (int i = 0; i < groundReactionDerivatives.length; i++)
				yDDot[i] = groundReactionDerivatives[i];			
		}

		@Override
		public int getDimension() {return 3;}
	}
	
	private void updateDerivatives(double[] y, double[] yDot) {
		double[] yDDot = new double[3];
		
		// If tire position > 0, tire is still airborne and no forces should be applied 
		for (int i = 0; i < y.length; i++)
			if (tirePosition[i] > 0) yDot[i] = y[i] = 0;
		
		yDDot[0] = (- groundReaction.get(GroundReaction.NOSE_DAMPING)  * yDot[0]) 
				 	- (groundReaction.get(GroundReaction.NOSE_SPRING)  * y[0])
				 	+ noseGroundForces[2];
		yDDot[1] = (- groundReaction.get(GroundReaction.LEFT_DAMPING)  * yDot[1]) 
				 	- (groundReaction.get(GroundReaction.LEFT_SPRING)  * y[1])
				 	+ leftGroundForces[2];
		yDDot[2] = (- groundReaction.get(GroundReaction.RIGHT_DAMPING) * yDot[2]) 
				 	- (groundReaction.get(GroundReaction.RIGHT_SPRING) * y[2])
				 	+ rightGroundForces[2];
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
			tirePosition[i]  =  (gearUvwPostion[0]*dirCosMat[2][0]+gearUvwPostion[1]*dirCosMat[2][1]+gearUvwPostion[2]*dirCosMat[2][2]) - terrainHeight;   // D (ft)
			tireVelocity[i]  = -(gearUvwVelocity[0]*dirCosMat[2][0]+gearUvwVelocity[1]*dirCosMat[2][1]+gearUvwVelocity[2]*dirCosMat[2][2]);                // D (ft/sec)
		}
	}
	
	private void calculateTotalGroundForces() {
		
		// X Forces
		// Use static coefficient of friction if forward velocity is near 0 
		if (linearVelocities[0] > 0.5) {
			leftGroundForces[0]  = - groundReactionDerivatives[1] * TIRE_STATIC_FRICTION;
			rightGroundForces[0] = - groundReactionDerivatives[2] * TIRE_STATIC_FRICTION;
		} else {
			leftGroundForces[0]  = - groundReactionDerivatives[1] * TIRE_ROLLING_FRICTION;
			rightGroundForces[0] = - groundReactionDerivatives[2] * TIRE_ROLLING_FRICTION;
		}
		
		if (controls.get(FlightControls.BRAKE_L) > 0)
			leftGroundForces[0] += brakingForce * controls.get(FlightControls.BRAKE_L);
		
		if (controls.get(FlightControls.BRAKE_R) > 0)
			rightGroundForces[0] += brakingForce * controls.get(FlightControls.BRAKE_R);
		
		// Y Forces		
		leftGroundForces[1]  = - groundReactionDerivatives[1] * TIRE_STATIC_FRICTION;
		rightGroundForces[1] = - groundReactionDerivatives[2] * TIRE_STATIC_FRICTION;
		
		// Z Forces
		noseGroundForces[2] = groundReactionDerivatives[0];
		leftGroundForces[2] = groundReactionDerivatives[1];
		rightGroundForces[2] = groundReactionDerivatives[2];
		
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
	
	public double[] getTotalGroundForces() {
		return totalGroundForces;
	}

	public double[] getTotalGroundMoments() {
		return totalGroundMoments;
	}

	public void integrateStep() {
		calculateTirePositionsAndVelocities();
		
		updateDerivatives(y, yDot);
		
		// Run a single step of integration
		y = integrator.singleStep(new FirstOrderConverter(new GroundReactionEquations()), 	  // derivatives
								  t, 		  				// start time
								  y0, 		  				// initial conditions
								  t+integratorConfig[1]);   // end time (t+dt)
		
		calculateTotalGroundForces();
		calculateTotalGroundMoments();
		
		t += integratorConfig[1];
	}
}
