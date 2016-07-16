package com.chrisali.javaflightsim.simulation.aircraft;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

import org.apache.commons.math3.analysis.interpolation.PiecewiseBicubicSplineInterpolatingFunction;

import com.chrisali.javaflightsim.simulation.aero.AccelAndMoments;
import com.chrisali.javaflightsim.simulation.aero.Aerodynamics;
import com.chrisali.javaflightsim.simulation.aero.StabilityDerivatives;
import com.chrisali.javaflightsim.simulation.aero.WingGeometry;
import com.chrisali.javaflightsim.simulation.enviroment.Environment;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.propulsion.Engine;
import com.chrisali.javaflightsim.utilities.SixDOFUtilities;
import com.chrisali.javaflightsim.utilities.FileUtilities;

/**
 * Aircraft object which consists of {@link StabilityDerivatives} and {@link WingGeometry} to define its aerodynamic properties,
 * and {@link MassProperties} to define its mass and inertia, and {@link GroundReaction} to define the landing gear geometry and properties. 
 * Uses {@link AircraftBuilder} to create a package with a set of {@link Engine}s to be used in {@link Integrate6DOFEquations} to 
 * create a flight simulation. Stability derivatives (1/rad) can be either Double values or {@link PiecewiseBicubicSplineInterpolatingFunction} 
 */
public class Aircraft {
	private String name;
	
	private Map<StabilityDerivatives, Object> stabDerivs;
	private Map<WingGeometry, Double> 		  wingGeometry;
	private Map<MassProperties, Double> 	  massProps;
	private Map<GroundReaction, Double>		  groundReaction;
	
	/**
	 *  Default constructor that gives default values to stability derivatives, wing geometry, mass properties and ground reaction.
	 *  It uses the Ryan Navion as a baseline. 
	 *   
	 *   @see https://en.wikipedia.org/wiki/Ryan_Navion
	 */
	public Aircraft() {
		this.name               = "Navion"; 
		// Creates EnumMaps and populates them with: 
		// Stability derivative values (either Double or PiecewiseBicubicSplineInterpolatingFunction)
		// Wing geometry values (Double)
		// Mass properties		(Double)
		// Ground reaction      (Double)
		this.stabDerivs			= new EnumMap<StabilityDerivatives, Object>(StabilityDerivatives.class);
		this.wingGeometry		= new EnumMap<WingGeometry, Double>(WingGeometry.class);
		this.massProps			= new EnumMap<MassProperties, Double>(MassProperties.class);
		this.groundReaction     = new EnumMap<GroundReaction, Double>(GroundReaction.class);
		
		// =======================================
		// Default stability derivatives (Navion)
		// =======================================
		
		// Lift
		stabDerivs.put(StabilityDerivatives.CL_ALPHA,     new Double(4.44));
		stabDerivs.put(StabilityDerivatives.CL_0, 	      new Double(0.41));
		stabDerivs.put(StabilityDerivatives.CL_Q,         new Double(3.80));
		stabDerivs.put(StabilityDerivatives.CL_ALPHA_DOT, new Double(0.0));
		stabDerivs.put(StabilityDerivatives.CL_D_ELEV,    new Double(0.355));
		stabDerivs.put(StabilityDerivatives.CL_D_FLAP,    new Double(0.355));
		
		// Side Force
		stabDerivs.put(StabilityDerivatives.CY_BETA,      new Double(-0.564));
		stabDerivs.put(StabilityDerivatives.CY_D_RUD,     new Double(0.157));
		
		// Drag
		stabDerivs.put(StabilityDerivatives.CD_ALPHA,     new Double(0.33));
		stabDerivs.put(StabilityDerivatives.CD_0,         new Double(0.025));
		stabDerivs.put(StabilityDerivatives.CD_D_ELEV,    new Double(0.001));
		stabDerivs.put(StabilityDerivatives.CD_D_FLAP,    new Double(0.02));
		stabDerivs.put(StabilityDerivatives.CD_D_GEAR,    new Double(0.09));
		
		// Roll Moment
		stabDerivs.put(StabilityDerivatives.CROLL_BETA,   new Double(-0.074));
		stabDerivs.put(StabilityDerivatives.CROLL_P,      new Double(-0.410));
		stabDerivs.put(StabilityDerivatives.CROLL_R,      new Double(0.107));
		stabDerivs.put(StabilityDerivatives.CROLL_D_AIL,  new Double(-0.134));
		stabDerivs.put(StabilityDerivatives.CROLL_D_RUD,  new Double(-0.107));
		
		// Pitch Moment
		stabDerivs.put(StabilityDerivatives.CM_ALPHA,     new Double(-0.683));
		stabDerivs.put(StabilityDerivatives.CM_0,         new Double(0.02));
		stabDerivs.put(StabilityDerivatives.CM_Q,         new Double(-9.96));
		stabDerivs.put(StabilityDerivatives.CM_ALPHA_DOT, new Double(-4.36));
		stabDerivs.put(StabilityDerivatives.CM_D_ELEV,    new Double(-0.923));
		stabDerivs.put(StabilityDerivatives.CM_D_FLAP,    new Double(-0.050));
		
		// Yaw Moment
		stabDerivs.put(StabilityDerivatives.CN_BETA,      new Double(0.071));
		stabDerivs.put(StabilityDerivatives.CN_P,      	  new Double(-0.0575));
		stabDerivs.put(StabilityDerivatives.CN_R,         new Double(-0.125));
		stabDerivs.put(StabilityDerivatives.CN_D_AIL,     new Double(-0.0035));
		stabDerivs.put(StabilityDerivatives.CN_D_RUD,     new Double(-0.072));
		
		// =======================================
		// Default wing geometry (Navion)
		// =======================================		
		
		// Aerodynamic center
		wingGeometry.put(WingGeometry.AC_X,   0.0);
		wingGeometry.put(WingGeometry.AC_Y,   0.0);
		wingGeometry.put(WingGeometry.AC_Z,   0.0);
		
		// Wing dimensions
		wingGeometry.put(WingGeometry.S_WING, 184.0);
		wingGeometry.put(WingGeometry.B_WING, 33.4);
		wingGeometry.put(WingGeometry.C_BAR,  5.7);
		
		// =======================================
		// Default mass properties (Navion)
		// =======================================
		
		// Center of Gravity
		massProps.put(MassProperties.CG_X, 			 	 0.0);
		massProps.put(MassProperties.CG_Y, 			 	 0.0);
		massProps.put(MassProperties.CG_Z, 			 	 0.0);
		
		// Moments of Inertia
		massProps.put(MassProperties.J_X,  				 1048.0);
		massProps.put(MassProperties.J_Y,    		 	 3000.0);
		massProps.put(MassProperties.J_Z,  			 	 3050.0);
		massProps.put(MassProperties.J_XZ, 				 0.0);
		
		// Weights and Mass (lbf/slug)
		massProps.put(MassProperties.WEIGHT_EMPTY,   	 1780.0);
		massProps.put(MassProperties.MAX_WEIGHT_FUEL,    360.0);
		massProps.put(MassProperties.WEIGHT_FUEL,    	 1.0);
		massProps.put(MassProperties.MAX_WEIGHT_PAYLOAD, 610.0);
		massProps.put(MassProperties.WEIGHT_PAYLOAD,     1.0);
		massProps.put(MassProperties.TOTAL_MASS, ((massProps.get(MassProperties.MAX_WEIGHT_PAYLOAD) * massProps.get(MassProperties.WEIGHT_PAYLOAD)) +
												  (massProps.get(MassProperties.MAX_WEIGHT_FUEL)    * massProps.get(MassProperties.WEIGHT_FUEL)) +
												   massProps.get(MassProperties.WEIGHT_EMPTY))      / Environment.getGravity());
		
		// =======================================
		// Default ground reaction (Navion)
		// =======================================
		
		// Landing Gear Geometry (Nose) [ft]
		groundReaction.put(GroundReaction.NOSE_X,  		 2.0);
		groundReaction.put(GroundReaction.NOSE_Y,  		 0.0);
		groundReaction.put(GroundReaction.NOSE_Z,  		 2.0);
		
		// Landing Gear Geometry (Left Main) [ft]
		groundReaction.put(GroundReaction.LEFT_X, 		-2.0);
		groundReaction.put(GroundReaction.LEFT_Y, 		-3.0);
		groundReaction.put(GroundReaction.LEFT_Z,  		 2.0);
		
		// Landing Gear Geometry (Right Main) [ft]
		groundReaction.put(GroundReaction.RIGHT_X, 		-2.0);
		groundReaction.put(GroundReaction.RIGHT_Y,  	 3.0);
		groundReaction.put(GroundReaction.RIGHT_Z,  	 2.0);
		
		// Landing Gear Strut Damping [lbf/sec] //TODO verify units 
		groundReaction.put(GroundReaction.NOSE_DAMPING,  300.0);
		groundReaction.put(GroundReaction.LEFT_DAMPING,  300.0);
		groundReaction.put(GroundReaction.RIGHT_DAMPING, 300.0);
		
		// Landing Gear Strut Damping [lbf/ft]
		groundReaction.put(GroundReaction.NOSE_SPRING,   3600.0);
		groundReaction.put(GroundReaction.LEFT_SPRING,   3600.0);
		groundReaction.put(GroundReaction.RIGHT_SPRING,  3600.0);
		
		// Braking Force [lbf]
		groundReaction.put(GroundReaction.BRAKING_FORCE, 80000.0);
	}
	
	/**
	 * Custom aircraft constructor. It uses files located in <p><br><code>.\Aircraft\</code></br></p>
	 * to define the stability derivatives, mass properties, wing geometry and ground reaction. These files are: 
	 * <p><br><code>Aero.txt</code></br> 
	 * <br><code>StabilityDerivaticves.txt</code></br>
	 * <br><code>WingGeometry.txt</code></br>
	 * <br><code>MassProperties.txt</code></br></p>
	 * <br><code>GroundReaction.txt</code></br></p>
	 * 
	 * These files must be in a folder, whose name matches the aircraftName passed into this constructor.
	 * 
	 * <p>The constructor also allows for custom look up tables ({@link PiecewiseBicubicSplineInterpolatingFunction}) to be used to better define
	 * the aerodynamics of the aircraft by using {@link AircraftBuilder#createLookupTable(String, String)}. </p>
	 * 
	 * Look up tables are defined as text files, and must be located in a subfolder of the desired aircraft's folder, with the folder name "LookupTables." 
	 * The title of a lookup table text file must match the string value of the {@link StabilityDerivatives} Enum that the user wishes to represent as a lookup table 
	 * 
	 * @param aircraftName
	 */
	public Aircraft(String aircraftName) {
		this.name = aircraftName;
		// Creates EnumMaps and populates them with: 
		// Stability derivative values (either Double or PiecewiseBicubicSplineInterpolatingFunction)
		// Wing geometry values (Double)
		// Mass properties		(Double)
		// Ground reaction	    (Double)
		this.stabDerivs			= new EnumMap<StabilityDerivatives, Object>(StabilityDerivatives.class);
		this.wingGeometry		= new EnumMap<WingGeometry, Double>(WingGeometry.class);
		this.massProps			= new EnumMap<MassProperties, Double>(MassProperties.class);
		this.groundReaction     = new EnumMap<GroundReaction, Double>(GroundReaction.class);
		
		// Aerodynamics
		ArrayList<String[]> readAeroFile = FileUtilities.readFileAndSplit(aircraftName, AircraftBuilder.FILE_PATH, "Aero");
		
		// Override constant stability derivative values with the keyword "lookup" in Aero.txt; need to then 
		// supply text file with lookup table and break points
		for(StabilityDerivatives stabDerKey : StabilityDerivatives.values()) {
			for (String[] readLine : readAeroFile) {
				if (stabDerKey.toString().equals(readLine[0]))
					if (readLine[1].toLowerCase().equals("lookup"))
						this.stabDerivs.put(stabDerKey, AircraftBuilder.createLookupTable(this, readLine[0]));
					else
						this.stabDerivs.put(stabDerKey, Double.parseDouble(readLine[1]));
			}
		}
		
		// Mass Properties
		ArrayList<String[]> readMassPropFile = FileUtilities.readFileAndSplit(aircraftName, AircraftBuilder.FILE_PATH, "MassProperties");
		
		for(MassProperties massPropKey : MassProperties.values()) {
			for (String[] readLine : readMassPropFile) {
				if (massPropKey.toString().equals(readLine[0]))
					this.massProps.put(massPropKey, Double.parseDouble(readLine[1]));
			}
		}
		// Sum up empty, fuel and payload weights divided by gravity to get total mass
		massProps.put(MassProperties.TOTAL_MASS, ((massProps.get(MassProperties.MAX_WEIGHT_PAYLOAD) * massProps.get(MassProperties.WEIGHT_PAYLOAD)) +
												  (massProps.get(MassProperties.MAX_WEIGHT_FUEL)    * massProps.get(MassProperties.WEIGHT_FUEL)) +
												   massProps.get(MassProperties.WEIGHT_EMPTY))      / Environment.getGravity());
		
		// Wing Geometry
		ArrayList<String[]> readWingGeomFile = FileUtilities.readFileAndSplit(aircraftName, AircraftBuilder.FILE_PATH, "WingGeometry");
		
		for(WingGeometry wingGeoKey : WingGeometry.values()) {
			for (String[] readLine : readWingGeomFile) {
				if (wingGeoKey.toString().equals(readLine[0]))
					this.wingGeometry.put(wingGeoKey, Double.parseDouble(readLine[1]));
			}
		}
		
		// Ground Reaction
		ArrayList<String[]> readGndReactFile = FileUtilities.readFileAndSplit(aircraftName, AircraftBuilder.FILE_PATH, "GroundReaction");
		
		for(GroundReaction gndReactKey : GroundReaction.values()) {
			for (String[] readLine : readGndReactFile) {
				if (gndReactKey.toString().equals(readLine[0]))
					this.groundReaction.put(gndReactKey, Double.parseDouble(readLine[1]));
			}
		}
	}
	
	/**
	 * Creates a double array of {@link MassProperties#CG_X}, {@link MassProperties#CG_Y} and {@link MassProperties#CG_Z}
	 *  used in {@link AccelAndMoments}, which needs a vector of these values
	 * 
	 * @return centerOfGravity
	 */
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
	public double[] getAerodynamicCenter() {return new double[] {wingGeometry.get(WingGeometry.AC_X),
																 wingGeometry.get(WingGeometry.AC_Y),
																 wingGeometry.get(WingGeometry.AC_Z)};}
	
	/**
	 * Creates a double array of {@link MassProperties#J_X}, {@link MassProperties#J_Y}, {@link MassProperties#J_Z} and {@link MassProperties#J_XZ}
	 *  used in {@link SixDOFUtilities#calculateInertiaCoeffs(double[])}, which needs an array of these values
	 * 
	 * @return centerOfGravity
	 */
	public double[] getInertiaValues() {return new double[] {massProps.get(MassProperties.J_X),
														     massProps.get(MassProperties.J_Y),
														     massProps.get(MassProperties.J_Z),
														     massProps.get(MassProperties.J_XZ)};}
	
	/**
	 * Returns the value held by the {@link StabilityDerivatives} key in the stabDerivs EnumMap. {@link Aerodynamics} uses this in conjunction with 
	 * {@link Aerodynamics#calculateInterpStabDer(double[], EnumMap, StabilityDerivatives)} to interpolate the stability derivative value if a
	 * {@link PiecewiseBicubicSplineInterpolatingFunction} object is detected, or simply return a double value
	 * 
	 * @param stabDer
	 * @return value of key in stabDerivs
	 */
	public Object getStabilityDerivative(StabilityDerivatives stabDer) {return stabDerivs.get(stabDer);}
	
	/**
	 * Returns the value held by the {@link WingGeometry} key in the wingGeometry EnumMap
	 * 
	 * @param wingGeom
	 * @return value of key in wingGeometry
	 */
	public double getWingGeometry(WingGeometry wingGeom) {return wingGeometry.get(wingGeom);}
	
	/**
	 * Returns the value held by the {@link MassProperties} key in the massProps EnumMap
	 * 
	 * @param massProp
	 * @return value of key in massProps
	 */
	public double getMassProperty(MassProperties massProp) {return massProps.get(massProp);}
	
	/**
	 * Updates the value held by the {@link MassProperties} key in the massProps EnumMap
	 * 
	 * @param massProp
	 * @return wingGeometry
	 */
	public void setMassProperty(MassProperties massProp, Double value) {massProps.put(massProp, value);}
		
	/**
	 * Returns the EnumMap associated with the aircraft's {@link MassProperties}
	 * 
	 * @return massProps
	 */
	public Map<MassProperties, Double> getMassProps() {return massProps;}
	
	/**
	 * Returns the EnumMap associated with the aircraft's {@link GroundReaction}
	 * 
	 * @return groundReaction
	 */
	public Map<GroundReaction, Double> getGroundReaction() {return groundReaction;}

	/**
	 * Gets the name of the aircraft
	 * 
	 * @return name
	 */
	public String getName() {return name;}

	/**
	 *  Outputs the stability derivatives, mass properties, and wing geometry of an aircraft
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("======================\n");
		sb.append(this.name).append(" Aircraft Parameters:\n");
		sb.append("======================\n\n");
		
		sb.append("Stability Derivatives\n\n");
		
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
