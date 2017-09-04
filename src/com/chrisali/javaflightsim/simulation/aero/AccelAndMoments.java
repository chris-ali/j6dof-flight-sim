/*******************************************************************************
 * Copyright (C) 2016-2017 Christopher Ali
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
package com.chrisali.javaflightsim.simulation.aero;

import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.chrisali.javaflightsim.simulation.aircraft.Aircraft;
import com.chrisali.javaflightsim.simulation.aircraft.MassProperties;
import com.chrisali.javaflightsim.simulation.enviroment.EnvironmentParameters;
import com.chrisali.javaflightsim.simulation.flightcontrols.FlightControlType;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.integration.IntegrateGroundReaction;
import com.chrisali.javaflightsim.simulation.propulsion.Engine;
import com.chrisali.javaflightsim.simulation.utilities.SaturationUtilities;

/**
 * Calculates total accelerations and moments experienced by the aircraft in the simulation. The init method creates an
 * {@link Aerodynamics} object to calculate aerodynamic forces and moments, which are then added to other various forces 
 * (ground reaction, wind, engine, etc) to yield accelerations and moments used by {@link Integrate6DOFEquations} in its 
 * numerical integration
 * @see Source: <i>Small Unmanned Aircraft: Theory and Practice by Beard, R.W. and McLain, T.W.</i>
 */
public class AccelAndMoments {
	
	private static Aerodynamics aero;
	
	private static double[] linearAccelerations = new double[3];
	private static double[] totalMoments 		= new double[3];
	
	/**
	 * Initializes {@link AccelAndMoments}. It uses the {@link Aircraft} argument to create an {@link Aerodynamics} object, 
	 * which calculates aerodynamic forces and moments associated with the Aircraft object passed in
	 *  
	 * @param aircraft
	 */
	public static void init(Aircraft aircraft) {aero = new Aerodynamics(aircraft);}
	
	
	/**
	 * Calculates the total linear acceleration experienced by the aircraft (ft/sec^2)
	 * 
	 * @param windParameters
	 * @param angularRates
	 * @param environmentParameters
	 * @param controls
	 * @param alphaDot
	 * @param engineList
	 * @param aircraft
	 * @param groundReaction
	 * @param heightAGL
	 * @return linearAccelerations
	 */
	public static double[] calculateLinearAccelerations(double[] windParameters,
									         		    double[] angularRates,
									         		    Map<EnvironmentParameters, Double> environmentParameters,
									         		    Map<FlightControlType, Double> controls,
									         		    double alphaDot,
									         		    Set<Engine> engineList,
									         		    Aircraft aircraft,
									         		    IntegrateGroundReaction groundReaction,
									         		    double heightAGL) {
		
		Vector3D aeroForceVector = new Vector3D(aero.calculateBodyForces(windParameters, 
																	     angularRates, 
																	     environmentParameters, 
																	     controls, 
																	     alphaDot,
																	     heightAGL));
		
		Vector3D groundForceVector = new Vector3D(groundReaction.getTotalGroundForces());
		
		// Create a vector of engine force, iterate through engineList and add the thrust of each engine in list
		Vector3D engineForceVector = Vector3D.ZERO;
		for (Engine engine : engineList)
			engineForceVector = engineForceVector.add(new Vector3D(engine.getEngineThrust()));

		linearAccelerations = aeroForceVector.add(engineForceVector).add(groundForceVector)
											 .scalarMultiply(1/aircraft.getMassProperty(MassProperties.TOTAL_MASS))
											 .toArray(); 
		
		return SaturationUtilities.limitLinearAccelerations(linearAccelerations);
	}
	
	/**
	 * Calculates the total moment experienced by the aircraft (lb ft)
	 * 
	 * @param windParameters
	 * @param angularRates
	 * @param environmentParameters
	 * @param controls
	 * @param alphaDot
	 * @param engineList
	 * @param aircraft
	 * @param groundReaction
	 * @param heightAGL
	 * @return totalMoments
	 */
	public static double[] calculateTotalMoments(double[] windParameters,
											     double[] angularRates,
											     Map<EnvironmentParameters, Double> environmentParameters,
											     Map<FlightControlType, Double> controls,
											     double alphaDot,
											     Set<Engine> engineList,
											     Aircraft aircraft,
											     IntegrateGroundReaction groundReaction,
											     double heightAGL) {

		Vector3D aeroForceVector = new Vector3D(aero.calculateBodyForces(windParameters, 
																	     angularRates, 
																	     environmentParameters, 
																	     controls, 
																	     alphaDot,
																	     heightAGL));
		
		// Apache Commons vector methods only accept primitive double[] arrays
		Vector3D acVector = new Vector3D(aircraft.getAerodynamicCenter());
		Vector3D cgVector = new Vector3D(aircraft.getCenterOfGravity());
		
		Vector3D aeroForceCrossProd = Vector3D.crossProduct(aeroForceVector, acVector.subtract(cgVector));
		
		Vector3D aeroMomentVector = new Vector3D(aero.calculateAeroMoments(windParameters, 
																		   angularRates, 
																		   environmentParameters, 
																		   controls, 
																		   alphaDot)); 
		
		Vector3D groundMomentVector = new Vector3D(groundReaction.getTotalGroundMoments());
		
		// Create a vector of engine moment, iterate through engineList and add the moment of each engine in list
		Vector3D engineMoment = Vector3D.ZERO;
		for (Engine engine : engineList)
			engineMoment = engineMoment.add(new Vector3D(engine.getEngineMoment()));
		
		totalMoments = aeroMomentVector.add(engineMoment).add(aeroForceCrossProd).add(groundMomentVector)
									   .toArray();
		
		return SaturationUtilities.limitTotalMoments(totalMoments); 
	}
}
