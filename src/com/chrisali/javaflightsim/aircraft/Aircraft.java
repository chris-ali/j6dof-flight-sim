package com.chrisali.javaflightsim.aircraft;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

import com.chrisali.javaflightsim.aero.StabilityDerivatives;
import com.chrisali.javaflightsim.aero.WingGeometry;
import com.chrisali.javaflightsim.enviroment.Environment;

public class Aircraft {
	private String name;
	
	protected Map<StabilityDerivatives, Object> stabDerivs;
	protected Map<WingGeometry, Double> 		wingGeometry;
	protected Map<MassProperties, Double> 		massProps;
	
	// Default constructor to give default values for aircraft definition (Navion)
	public Aircraft() {
		this.name               = "Navion"; 
		// Creates EnumMaps and populates them with: 
		// Stability derivative values (either Double or PiecewiseBicubicSplineInterpolatingFunction)
		// Wing geometry values (Double)
		// Mass properties		(Double)
		this.stabDerivs			= new EnumMap<StabilityDerivatives, Object>(StabilityDerivatives.class);
		this.wingGeometry		= new EnumMap<WingGeometry, Double>(WingGeometry.class);
		this.massProps			= new EnumMap<MassProperties, Double>(MassProperties.class);
		
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
		stabDerivs.put(StabilityDerivatives.CROLL_D_RUD,  new Double(0.107));
		
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
		massProps.put(MassProperties.CG_X, 			 0.0);
		massProps.put(MassProperties.CG_Y, 			 0.0);
		massProps.put(MassProperties.CG_Z, 			 0.0);
		
		// Moments of Inertia
		massProps.put(MassProperties.J_X,  			 1048.0);
		massProps.put(MassProperties.J_Y,    		 3000.0);
		massProps.put(MassProperties.J_Z,  			 3050.0);
		massProps.put(MassProperties.J_XZ, 			 0.0);
		
		// Weights and Mass (lbf/slug)
		massProps.put(MassProperties.WEIGHT_EMPTY,   1780.0);
		massProps.put(MassProperties.WEIGHT_FUEL,    360.0);
		massProps.put(MassProperties.WEIGHT_PAYLOAD, 610.0);
		massProps.put(MassProperties.TOTAL_MASS, (massProps.get(MassProperties.WEIGHT_EMPTY) + 
												  massProps.get(MassProperties.WEIGHT_FUEL)  +
												  massProps.get(MassProperties.WEIGHT_PAYLOAD))/Environment.getGravity());
	}
	
	public Aircraft(String aircraftName) {
		this.name = aircraftName;
		// Creates EnumMaps and populates them with: 
		// Stability derivative values (either Double or PiecewiseBicubicSplineInterpolatingFunction)
		// Wing geometry values (Double)
		// Mass properties		(Double)
		this.stabDerivs			= new EnumMap<StabilityDerivatives, Object>(StabilityDerivatives.class);
		this.wingGeometry		= new EnumMap<WingGeometry, Double>(WingGeometry.class);
		this.massProps			= new EnumMap<MassProperties, Double>(MassProperties.class);
		
		// Aerodynamics
		ArrayList<String[]> readAeroFile = AircraftBuilder.readFileAndSplit(aircraftName, "Aero");
		
		// Override constant stability derivative values with the keyword "lookup" in Aero.txt; need to then 
		// supply text file with lookup table and break points
		for(StabilityDerivatives stabDerKey : StabilityDerivatives.values()) {
			for (String[] readLine : readAeroFile) {
				if (stabDerKey.toString().equals(readLine[0]))
					if (readLine[1].toLowerCase().equals("lookup"))
						this.stabDerivs.put(stabDerKey, AircraftBuilder.createLookupTable(this.name, readLine[0]));
					else
						this.stabDerivs.put(stabDerKey, Double.parseDouble(readLine[1]));
			}
		}
		
		// Mass Properties
		ArrayList<String[]> readMassPropFile = AircraftBuilder.readFileAndSplit(aircraftName, "MassProperties");
		
		for(MassProperties massPropKey : MassProperties.values()) {
			for (String[] readLine : readMassPropFile) {
				if (massPropKey.toString().equals(readLine[0]))
					this.massProps.put(massPropKey, Double.parseDouble(readLine[1]));
			}
		}
		
		// Wing Geometry
		ArrayList<String[]> readWingGeomFile = AircraftBuilder.readFileAndSplit(aircraftName, "WingGeometry");
		
		for(WingGeometry wingGeoKey : WingGeometry.values()) {
			for (String[] readLine : readWingGeomFile) {
				if (wingGeoKey.toString().equals(readLine[0]))
					this.wingGeometry.put(wingGeoKey, Double.parseDouble(readLine[1]));
			}
		}
	}
	
	public Double[] getCenterOfGravity() {return new Double[] {massProps.get(MassProperties.CG_X),
															   massProps.get(MassProperties.CG_Y),
															   massProps.get(MassProperties.CG_Z)};}

	public Double[] getAerodynamicCenter() {return new Double[] {wingGeometry.get(WingGeometry.AC_X),
																 wingGeometry.get(WingGeometry.AC_Y),
																 wingGeometry.get(WingGeometry.AC_Z)};}
	
	public Double[] getInertiaValues() {return new Double[] {massProps.get(MassProperties.J_X),
														     massProps.get(MassProperties.J_Y),
														     massProps.get(MassProperties.J_Z),
														     massProps.get(MassProperties.J_XZ)};}
	
	public String getName() {return name;}

	// Outputs the stability derivatives, mass properties, and wing geometry of an aircraft
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
		
		return sb.toString();
	}
}
