package com.chrisali.javaflightsim.aircraft;

import java.util.EnumMap;
import java.util.Map;

import com.chrisali.javaflightsim.aero.StabilityDerivatives;

public class Aircraft {
	protected double[] centerOfGravity;    // {CG_x,CG_y,CG_z}
	protected double[] aerodynamicCenter;  // {ac_x,ac_y,ac_z}
	protected double[] enginePosition; 	   // {eng_x,eng_y,eng_z}  (ft)

	protected double[] massProperties;     // {weight,Ix,Iy,Iz,Ixz}
	
	protected double[] wingDimensions;	   // {wingSfcArea,b,c_bar}
	
	protected Map<StabilityDerivatives, Object> stabDerivs;
	
	// Default constructor to give default values for aircraft definition (Navion)
	public Aircraft() { 
		this.centerOfGravity    = new double[]{0,0,0};
		this.aerodynamicCenter  = new double[]{0,0,0};
		this.enginePosition		= new double[]{0,0,0};
		
		this.massProperties     = new double[]{2750/32.2,1048,3000,3050,0};
		
		this.wingDimensions		= new double[]{184,33.4,5.7};
		
		// Creates an EnumMap and populates it with stability derivative values (either Double or PiecewiseBicubicSplineInterpolatingFunction)
		this.stabDerivs			= new EnumMap<StabilityDerivatives, Object>(StabilityDerivatives.class);
		
		stabDerivs.put(StabilityDerivatives.CL_ALPHA,     new Double(4.44));
		stabDerivs.put(StabilityDerivatives.CL_0, 	      new Double(0.41));
		stabDerivs.put(StabilityDerivatives.CL_Q,         new Double(3.80));
		stabDerivs.put(StabilityDerivatives.CL_ALPHA_DOT, new Double(0.0));
		stabDerivs.put(StabilityDerivatives.CL_D_ELEV,    new Double(0.355));
		stabDerivs.put(StabilityDerivatives.CL_D_FLAP,    new Double(0.355));
		
		stabDerivs.put(StabilityDerivatives.CY_BETA,      new Double(-0.564));
		stabDerivs.put(StabilityDerivatives.CY_D_RUD,     new Double(0.157));
		
		stabDerivs.put(StabilityDerivatives.CD_ALPHA,     new Double(0.33));
		stabDerivs.put(StabilityDerivatives.CD_0,         new Double(0.025));
		stabDerivs.put(StabilityDerivatives.CD_D_FLAP,    new Double(0.02));
		stabDerivs.put(StabilityDerivatives.CD_D_ELEV,    new Double(0.001));
		stabDerivs.put(StabilityDerivatives.CD_D_GEAR,    new Double(0.09));
		
		stabDerivs.put(StabilityDerivatives.CROLL_BETA,   new Double(-0.074));
		stabDerivs.put(StabilityDerivatives.CROLL_P,      new Double(-0.410));
		stabDerivs.put(StabilityDerivatives.CROLL_R,      new Double(0.107));
		stabDerivs.put(StabilityDerivatives.CROLL_D_AIL,  new Double(-0.134));
		stabDerivs.put(StabilityDerivatives.CROLL_D_RUD,  new Double(0.107));
		
		stabDerivs.put(StabilityDerivatives.CM_ALPHA,     new Double(-0.683));
		stabDerivs.put(StabilityDerivatives.CM_0,         new Double(0.02));
		stabDerivs.put(StabilityDerivatives.CM_Q,         new Double(-9.96));
		stabDerivs.put(StabilityDerivatives.CM_ALPHA_DOT, new Double(-4.36));
		stabDerivs.put(StabilityDerivatives.CM_D_ELEV,    new Double(-0.923));
		stabDerivs.put(StabilityDerivatives.CM_D_FLAP,    new Double(-0.050));
		
		stabDerivs.put(StabilityDerivatives.CN_BETA,      new Double(0.071));
		stabDerivs.put(StabilityDerivatives.CN_P,      	  new Double(-0.0575));
		stabDerivs.put(StabilityDerivatives.CN_R,         new Double(-0.125));
		stabDerivs.put(StabilityDerivatives.CN_D_AIL,     new Double(-0.0035));
		stabDerivs.put(StabilityDerivatives.CN_D_RUD,     new Double(-0.072));
	}
	
	// TODO Read a text file with aircraft attributes, and assign them to arrays	
/*
	public Aircraft(String fileName){
		File file = new File(fileName);
		try (BufferedReader aircraftReader = new BufferedReader(new FileReader(file))){
			String tempLine;
			while ((tempLine = aircraftReader.readLine())!=null) {
				switch (tempLine.substring(0,1)){
					case "CL": {
						for(int i=0;i<6;i++)
					        liftDerivs[i]=tempLine.split(" = ")[1].parseDouble();
						break;
					}
					case "CY": {
						for(int i=0;i<6;i++)
							sideForceDerivs[i]=tempLine.split(" = ")[1].parseDouble();
						break;
					}
					case "CD": {
						for(int i=0;i<6;i++)
							dragDerivs[i]=tempLine.split(" = ")[1].parseDouble();
						break;
					}
					case "Cl": {
						for(int i=0;i<6;i++)
							rollMomentDerivs[i]=tempLine.split(" = ")[1].parseDouble();
						break;
					}
					case "CM": {
						for(int i=0;i<6;i++)
							pitchMomentDerivs[i]=tempLine.split(" = ")[1].parseDouble();
						break;
					}
					case "CN": {
						for(int i=0;i<5;i++)
							yawMomentDerivs[i]=tempLine.split(" = ")[1].parseDouble();
						break;
					}
					case "ac": {
						for(int i=0;i<3;i++)
							aerodynamicCenter[i]=tempLine.split(" = ")[1].parseDouble();
						break;
					}
					case "CG": {
						for(int i=0;i<3;i++)
							centerOfGravity[i]=tempLine.split(" = ")[1].parseDouble();
						break;
					}
					case "ac": {
						for(int i=0;i<3;i++)
							aerodynamicCenter[i]=tempLine.split(" = ")[1].parseDouble();
						break;
					}
				}
			}
		} catch (IOException e) {e.printStackTrace();}	
	}
*/
	public double[] getCenterOfGravity() {return centerOfGravity;}

	public double[] getAerodynamicCenter() {return aerodynamicCenter;}

	public double[] getMassProperties() {return massProperties;}

	public double[] getWingDimensions() {return wingDimensions;}
}
