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
package com.chrisali.javaflightsim.simulation.aircraft;

import java.io.File;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.analysis.interpolation.PiecewiseBicubicSplineInterpolatingFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chrisali.javaflightsim.simulation.aero.AccelAndMoments;
import com.chrisali.javaflightsim.simulation.aero.LookupTable;
import com.chrisali.javaflightsim.simulation.aero.StabilityDerivatives;
import com.chrisali.javaflightsim.simulation.aero.WingGeometry;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.interfaces.Saveable;
import com.chrisali.javaflightsim.simulation.propulsion.Engine;
import com.chrisali.javaflightsim.simulation.utilities.FileUtilities;
import com.chrisali.javaflightsim.simulation.utilities.SaturationUtilities;
import com.chrisali.javaflightsim.simulation.utilities.SimDirectories;
import com.chrisali.javaflightsim.simulation.utilities.SixDOFUtilities;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Aircraft object which consists of {@link StabilityDerivatives} and {@link WingGeometry} to define its aerodynamic properties,
 * {@link MassProperties} to define its mass and inertia, {@link GroundReaction} to define the landing gear geometry and properties, 
 * and a LinkedHashSet of {@link Engine}(s). This object is used in {@link Integrate6DOFEquations} to create a flight simulation. 
 * Stability derivatives (1/rad) can be either Double values or {@link PiecewiseBicubicSplineInterpolatingFunction} 
 */
public class Aircraft implements Saveable {
	
	@JsonIgnore
	private static final Logger logger = LogManager.getLogger(Aircraft.class);
	
	private String name;
	private Set<Engine> 						   engines;
	private Map<StabilityDerivatives, LookupTable> stabDerivs;
	private Map<WingGeometry, Double> 		  	   wingGeometry;
	private Map<MassProperties, Double> 	  	   massProps;
	private Map<GroundReaction, Double>		  	   groundReaction;

	/**
	 * Custom aircraft constructor. It uses files located in <p><br><code>Aircraft\</code></br></p>
	 * to define the stability derivatives, mass properties, wing geometry and ground reaction in: 
	 * <p><br><code>Aircraft.json</code></br> 
	 * 
	 * These files must be in a folder, whose name matches the aircraftName passed into this constructor.
	 * 
	 * @param aircraftName
	 */
	public Aircraft(String aircraftName) {
		name = aircraftName;
		stabDerivs			= new EnumMap<StabilityDerivatives, LookupTable>(StabilityDerivatives.class);
		wingGeometry		= new EnumMap<WingGeometry, Double>(WingGeometry.class);
		massProps			= new EnumMap<MassProperties, Double>(MassProperties.class);
		groundReaction      = new EnumMap<GroundReaction, Double>(GroundReaction.class);
		engines 			= new LinkedHashSet<>();
	}
	
	/**
	 *  Default constructor that uses the Ryan Navion as a baseline. 
	 *   
	 *   @see https://en.wikipedia.org/wiki/Ryan_Navion
	 */
	public Aircraft() { this("Navion");	}
	
	/**
	 * Saves all properties in this instance to a JSON file in "Aircraft/{aircraft.getName()}" 
	 * via {@link FileUtilities#serializeJson(String, String, Object)}
	 */
	@Override
	public void save() { 
		FileUtilities.serializeJson(SimDirectories.AIRCRAFT.toString() + File.separator + name, 
									this.getClass().getSimpleName(), 
									this); 
	}
	
	/**
	 * Creates a double array of {@link MassProperties#CG_X}, {@link MassProperties#CG_Y} and {@link MassProperties#CG_Z}
	 *  used in {@link AccelAndMoments}, which needs a vector of these values
	 * 
	 * @return centerOfGravity
	 */
	@JsonIgnore
	public double[] getCenterOfGravity() {return new double[] {massProps.get(MassProperties.CG_X),
															   massProps.get(MassProperties.CG_Y),
															   massProps.get(MassProperties.CG_Z)};}
	
	/**
	 * Creates a double array of {@link WingGeometry#AC_X}, {@link WingGeometry#AC_Y} and {@link WingGeometry#AC_Z}
	 *  used in {@link AccelAndMoments#calculateTotalMoments(double[], double[], EnumMap, EnumMap, double, java.util.Set, Aircraft)}, 
	 *  which needs a vector of these values
	 * 
	 * @return centerOfGravity
	 */
	@JsonIgnore
	public double[] getAerodynamicCenter() {return new double[] {wingGeometry.get(WingGeometry.AC_X),
																 wingGeometry.get(WingGeometry.AC_Y),
																 wingGeometry.get(WingGeometry.AC_Z)};}
	
	/**
	 * Creates a double array of {@link MassProperties#J_X}, {@link MassProperties#J_Y}, {@link MassProperties#J_Z} and {@link MassProperties#J_XZ}
	 *  used in {@link SixDOFUtilities#calculateInertiaCoeffs(double[])}, which needs an array of these values
	 * 
	 * @return centerOfGravity
	 */
	@JsonIgnore
	public double[] getInertiaValues() {return new double[] {massProps.get(MassProperties.J_X),
														     massProps.get(MassProperties.J_Y),
														     massProps.get(MassProperties.J_Z),
														     massProps.get(MassProperties.J_XZ)};}
	
	/**
	 * Returns the double value held by the {@link LookupTable} value for the {@link StabilityDerivatives} key in the stabDerivs EnumMap. 
	 * 
	 * @param stabDer
	 * @return value of key in stabDerivs
	 */
	@JsonIgnore
	public LookupTable getStabilityDerivative(StabilityDerivatives stabDer) {return stabDerivs.get(stabDer);}
	
	/**
	 * Returns the value held by the {@link WingGeometry} key in the wingGeometry EnumMap
	 * 
	 * @param wingGeom
	 * @return value of key in wingGeometry
	 */
	@JsonIgnore
	public double getWingGeometry(WingGeometry wingGeom) {return wingGeometry.get(wingGeom);}
	
	/**
	 * Returns the value held by the {@link MassProperties} key in the massProps EnumMap
	 * 
	 * @param massProp
	 * @return value of key in massProps
	 */
	@JsonIgnore
	public double getMassProperty(MassProperties massProp) {return massProps.get(massProp);}
	
	/**
	 * Updates the value held by the {@link MassProperties} key in the massProps EnumMap
	 * 
	 * @param massProp
	 */
	@JsonIgnore
	public void setMassProperty(MassProperties massProp, Double value) {massProps.put(massProp, value);}
	
	/**
	 * Updates the MassProperties config file with weight percentages
	 * 
	 * @param fuelWeightPercent (0.0 - 1.0)
	 * @param payloadWeightPercent (0.0 - 1.0)
	 */
	@JsonIgnore
	public void updateWeightPercentages(double fuelWeightPercent, double payloadWeightPercent) {
		fuelWeightPercent = SaturationUtilities.saturatePercentage(fuelWeightPercent);
		payloadWeightPercent = SaturationUtilities.saturatePercentage(payloadWeightPercent);
		
		logger.debug("Updating weights for " + name + " to " + fuelWeightPercent 
				+ " percent fuel and " + payloadWeightPercent + " percent payload...");
		
		try {	
			massProps.put(MassProperties.WEIGHT_FUEL, fuelWeightPercent);
			massProps.put(MassProperties.WEIGHT_PAYLOAD, payloadWeightPercent);
		} catch (Exception e) {
			logger.error("Error updating mass properties!", e);
		}
	}
	
	public Map<MassProperties, Double> getMassProps() {return massProps;}

	public void setMassProps(Map<MassProperties, Double> massProps) { this.massProps = massProps; }
			
	public Map<StabilityDerivatives, LookupTable> getStabDerivs() { return stabDerivs; }

	public void setStabDerivs(Map<StabilityDerivatives, LookupTable> stabDerivs) { this.stabDerivs = stabDerivs;	}

	public Map<WingGeometry, Double> getWingGeometry() { return wingGeometry; }

	public void setWingGeometry(Map<WingGeometry, Double> wingGeometry) { this.wingGeometry = wingGeometry;	}
	
	public Map<GroundReaction, Double> getGroundReaction() {return groundReaction;}

	public void setGroundReaction(Map<GroundReaction, Double> groundReaction) { this.groundReaction = groundReaction; }
	
	public Set<Engine> getEngines() { return engines; }

	public void setEngines(Set<Engine> engines) { this.engines = engines; }

	public String getName() { return name; }
	
	public void setName(String name) { this.name = name; }

	/**
	 *  Outputs the stability derivatives, mass properties, and wing geometry of an aircraft
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("======================\n");
		sb.append(this.name).append(" Aircraft Parameters:\n");
		sb.append("======================\n\n");
		
		sb.append("Engines\n\n");
		
		for (Engine engine : engines)
			sb.append(engine.toString()).append("\n");
		
		sb.append("\\nStability Derivatives\n\n");
		
		for (StabilityDerivatives stabDer : stabDerivs.keySet())
			sb.append(stabDer.toString()).append(": ").append(stabDerivs.get(stabDer)).append("\n");
		
		sb.append("\nWing Geometry\n\n");
		
		for (WingGeometry wingGeo : wingGeometry.keySet())
			sb.append(wingGeo.toString()).append(": ").append(wingGeometry.get(wingGeo)).append("\n");
		
		sb.append("\nMass Properties\n\n");
		
		for (MassProperties massProp : massProps.keySet())
			sb.append(massProp.toString()).append(": ").append(massProps.get(massProp)).append("\n");
		
		sb.append("\nGround Reaction\n\n");
		
		for (GroundReaction gndReact : groundReaction.keySet())
			sb.append(gndReact.toString()).append(": ").append(groundReaction.get(gndReact)).append("\n");
		
		return sb.toString();
	}
}
