package sixDOFFlightSim;

public class SixDOFUtilities {
	public static double[][] body2Ned(double[] eulerAngles) {
		double body2NedDCM[][] = new double[3][3];
		
		body2NedDCM[0][0] = Math.cos(eulerAngles[1])*Math.cos(eulerAngles[2]);
		body2NedDCM[1][0] = Math.cos(eulerAngles[1])*Math.sin(eulerAngles[2]);
		body2NedDCM[2][0] = -Math.sin(eulerAngles[1]);
		
		body2NedDCM[0][1] = -Math.cos(eulerAngles[0])*Math.sin(eulerAngles[2])+Math.sin(eulerAngles[0])*Math.sin(eulerAngles[1])*Math.cos(eulerAngles[2]);
		body2NedDCM[1][1] = Math.cos(eulerAngles[0])*Math.cos(eulerAngles[2])+Math.sin(eulerAngles[0])*Math.sin(eulerAngles[1])*Math.sin(eulerAngles[2]);
		body2NedDCM[2][1] = Math.sin(eulerAngles[0])*Math.cos(eulerAngles[1]);
		
		body2NedDCM[0][2] = Math.sin(eulerAngles[0])*Math.sin(eulerAngles[2])+Math.cos(eulerAngles[0])*Math.sin(eulerAngles[1])*Math.cos(eulerAngles[2]);
		body2NedDCM[1][2] = -Math.sin(eulerAngles[0])*Math.cos(eulerAngles[2])+Math.cos(eulerAngles[0])*Math.sin(eulerAngles[1])*Math.sin(eulerAngles[2]);
		body2NedDCM[2][2] = Math.cos(eulerAngles[0])*Math.cos(eulerAngles[1]);
				
		return body2NedDCM;
	}
	
	public static double[] getInertiaCoeffs(double[] massProperties) { //massProperties[]{mass,Ix,Iy,Iz,Ixz}
		double[] inertiaCoeffs = new double[9];
		
		double gamma = (massProperties[1]*massProperties[3])-(massProperties[4]*massProperties[4]);
		
		inertiaCoeffs[0] = (((massProperties[2]-massProperties[3])*massProperties[3])-(massProperties[4]*massProperties[4]))/gamma;
		inertiaCoeffs[1] = (massProperties[1]-massProperties[2]+massProperties[3])*massProperties[4]/gamma;
		inertiaCoeffs[2] = massProperties[3]/gamma;
		inertiaCoeffs[3] = massProperties[4]/gamma;
		inertiaCoeffs[4] = (massProperties[3]-massProperties[1])/massProperties[2];
		inertiaCoeffs[5] = massProperties[4]/massProperties[2];
		inertiaCoeffs[6] = 1/massProperties[2];
		inertiaCoeffs[7] = (massProperties[1]*(massProperties[1]-massProperties[2])+(massProperties[4]*massProperties[4]))/gamma;
		inertiaCoeffs[8] = massProperties[1]/gamma;
		
		return inertiaCoeffs;
	}
	
	public static double[][] wind2Body(double[] windParameters) {
		double wind2BodyDCM[][] = new double[3][3];
		
		wind2BodyDCM[0][0] = Math.cos(windParameters[2])*Math.cos(windParameters[1]);
		wind2BodyDCM[1][0] = -Math.sin(windParameters[1])*Math.cos(windParameters[2]); 
		wind2BodyDCM[2][0] = -Math.sin(windParameters[2]);
		
		wind2BodyDCM[0][1] = Math.sin(windParameters[1]); 										
		wind2BodyDCM[1][1] = Math.cos(windParameters[1]);
		wind2BodyDCM[2][1] = 0; 
		
		wind2BodyDCM[0][2] = Math.cos(windParameters[1])*Math.sin(windParameters[2]);
		wind2BodyDCM[1][2] = -Math.sin(windParameters[2])*Math.sin(windParameters[1]);
		wind2BodyDCM[2][2] = Math.cos(windParameters[2]);
				
		return wind2BodyDCM;
	}
	
	public static double[] getWindParameters(double[] linearVelocities) {
		double vTrue = Math.sqrt(Math.pow(linearVelocities[0],2) + Math.pow(linearVelocities[1],2) + Math.pow(linearVelocities[2],2));
		double beta = Math.asin(linearVelocities[1]/vTrue);
		double alpha = Math.atan(linearVelocities[2]/linearVelocities[0]);
		
		return SaturationLimits.limitWindParameters(new double[] {vTrue,beta,alpha});
	}

}