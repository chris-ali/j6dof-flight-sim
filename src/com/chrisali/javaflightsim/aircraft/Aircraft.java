package com.chrisali.javaflightsim.aircraft;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

import com.chrisali.javaflightsim.aero.StabilityDerivatives;
import com.chrisali.javaflightsim.aero.WingGeometry;

public class Aircraft {
	protected Double[] centerOfGravity;    // {CG_x,CG_y,CG_z}
	protected Double[] aerodynamicCenter;  // {ac_x,ac_y,ac_z}
	protected Double[] massProperties;     // {weight,Ix,Iy,Iz,Ixz}
	protected Double[] wingDimensions;	   // {wingSfcArea,b,c_bar}
	
	protected Map<StabilityDerivatives, Object> stabDerivs;
	protected Map<WingGeometry, Double> 		wingGeom;
	protected Map<MassProperties, Object> 		massProps;
	
	public static final String FILE_PATH = ".\\src\\com\\chrisali\\javaflightsim\\aircraft\\";
	
	// Default constructor to give default values for aircraft definition (Navion)
	public Aircraft() { 
		this.centerOfGravity    = new Double[]{0.0,0.0,0.0};
		this.aerodynamicCenter  = new Double[]{0.0,0.0,0.0};
		
		this.massProperties     = new Double[]{2750/32.2,1048.0,3000.0,3050.0,0.0};
		
		this.wingDimensions		= new Double[]{184.0,33.4,5.7};
		
		// Creates an EnumMap and populates it with stability derivative values (either Double or PiecewiseBicubicSplineInterpolatingFunction)
		this.stabDerivs			= new EnumMap<StabilityDerivatives, Object>(StabilityDerivatives.class);
		
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
	}
	
	// TODO Read a text file with aircraft attributes, and assign them to EnumMap	

	public Aircraft(String aircraftName){
		ArrayList<String[]> readAeroFile = readFileAndSplit(aircraftName, "Aero");
		
		for(int i = 0; i < readAeroFile.size(); i++) {
			for (String[] readLine : readAeroFile) {
				if (StabilityDerivatives.values()[i].equals(readLine[0]))
					stabDerivs.put(StabilityDerivatives.values()[i], readLine[1]);
			}
		}
	}
	
	public Double[] getCenterOfGravity() {return centerOfGravity;}

	public Double[] getAerodynamicCenter() {return aerodynamicCenter;}

	public Double[] getMassProperties() {return massProperties;}

	public Double[] getWingDimensions() {return wingDimensions;}

	private static ArrayList<String[]> readFileAndSplit(String aircraftName, String fileContents) {
		StringBuilder sb = new StringBuilder();
		sb.append(FILE_PATH).append(aircraftName).append("\\").append(fileContents).append(".txt");
		ArrayList<String[]> readAndSplit = new ArrayList<>();
		String readLine = null;
		
		try (BufferedReader br = new BufferedReader(new FileReader(sb.toString()))) {
			while ((readLine = br.readLine()) != null)
				readAndSplit.add(readLine.split(" = "));
		} catch (FileNotFoundException e) {System.err.println("Could not find: " + aircraftName + ".txt!");}
		catch (IOException e) {System.err.println("Could not read: " + aircraftName + ".txt!");}
		catch (NullPointerException e) {System.err.println("Bad reference to: " + aircraftName + ".txt!");} 
		
		return readAndSplit;
	}
}
