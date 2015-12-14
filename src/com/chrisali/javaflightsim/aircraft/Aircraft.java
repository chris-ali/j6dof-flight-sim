package com.chrisali.javaflightsim.aircraft;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Aircraft {
	public double[] centerOfGravity;    // {CG_x,CG_y,CG_z}
	public double[] aerodynamicCenter;  // {ac_x,ac_y,ac_z}
	public double[] enginePosition;		// {eng_x,eng_y,eng_z}
	
	public double[] massProperties;     // {weight,Ix,Iy,Iz,Ixz}
	
	public double[] wingDimensions;		// {wingSfcArea,b,c_bar}
	
	public double[] liftDerivs; 		// {CL_alpha,CL_0,CL_q,CL_alphadot,CL_de,CL_df}
	public double[] sideForceDerivs;	// {CY_beta,CY_dr}
	public double[] dragDerivs; 		// {CD_alpha,CD_0,CD_df,CD_de,CD_dg}

	public double[] rollMomentDerivs;   // {Cl_beta,Cl_p,Cl_r,Cl_da,Cl_dr}
	public double[] pitchMomentDerivs;  // {CM_alpha,CM_0,CM_q,CM_alphadot,CM_de,CM_df}
	public double[] yawMomentDerivs;    // {CN_beta,CN_p,CN_r,CN_da,CN_dr}
	
	// Default constructor to give default values for aircraft definition (Navion)
	public Aircraft() { 
		centerOfGravity     = new double[]{0,0,0};
		aerodynamicCenter   = new double[]{0,0,0};
		enginePosition		= new double[]{0,0,0};
		
		massProperties      = new double[]{2750/32.2,1048,3000,3050,0};
		
		wingDimensions		= new double[]{184,33.4,5.7};
		
		liftDerivs			= new double[]{4.44,0.41,3.8,0,0.355,0.355};
		sideForceDerivs		= new double[]{-0.564,0.157};
		dragDerivs			= new double[]{0.33,0.025,0.02,0.001,0.09};
		
		rollMomentDerivs	= new double[]{-0.074,-0.410,0.107,-0.134,0.107};
		pitchMomentDerivs	= new double[]{-0.683,0.02,-9.96,-4.36,-0.923,-0.050};
		yawMomentDerivs		= new double[]{0.071,-0.0575,-0.125,-0.0035,-0.072};
	}
	
	// TODO Read a text file with aircraft attributes, and assign them to arrays	
	public Aircraft(String fileName){

		FileReader aircraftFile;
		try {
			aircraftFile = new FileReader(fileName);
			BufferedReader aircraftReader = new BufferedReader(aircraftFile);
			
			while (aircraftReader.readLine()!=null) {
				//string tempLine =aircraftReader.readLine();
				//if (tempLine.substring(0,3).equals("CL")) {
					//for(int i=0;i<6;i++)
				    //    liftDerivs[i]=tempLine.split("/=",).toDouble();
				
			}
			
			aircraftReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
