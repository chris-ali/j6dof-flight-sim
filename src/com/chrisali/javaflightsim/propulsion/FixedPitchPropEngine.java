package com.chrisali.javaflightsim.propulsion;

public class FixedPitchPropEngine extends EngineModel {
	
	public FixedPitchPropEngine(double maxBHP, double maxRPM, double propDiameter) {
		this.maxBHP = maxBHP;
		this.maxRPM = maxRPM;
		this.propArea = Math.PI*(propDiameter*propDiameter)/4;
		this.propEfficiency = 0.85;
	}
	
	public FixedPitchPropEngine() {
		this.maxBHP = 200;
		this.maxRPM = 2700;
		this.propArea = Math.PI*(6.5*6.5)/4;
		this.propEfficiency = 0.85;
	}
	
	//TODO consider engine orientation
	public double[] calculateThrust(double[] controls,				//{elevator,aileron,rudder,throttle,propeller,mixture,flaps,gear,leftBrake,rightBrake}
									double[] NEDPosition,			    //{N,E,D}
									double[] environmentParameters,	//{temp,rho,p,a}
									double[] windParameters) {		//{vTrue,beta,alpha})
		
		// Consider static thrust case at low speeds
		if (windParameters[0] <= 5) {
			double totalThrust = Math.pow((controls[3]*maxBHP*HP_2_FTLBS), 2/3)*Math.pow(2*environmentParameters[1]*propArea, 1/3);
			
			engineThrust[0] = totalThrust; 
			return engineThrust;
		}			
		else {
			double totalThrust = (controls[3]*maxBHP*HP_2_FTLBS)*((A_P*environmentParameters[1]/RHO_SSL)-B_P)*(propEfficiency/windParameters[0]);

			engineThrust[0] = totalThrust; 			
			return engineThrust;			
		}
	}
	
	public double getFuelFlow(double[] controls) {
		return (0.9+(controls[3]*14.8))*controls[5]; // TODO need better method of getting fuel flow
	}
	
	public double getRPM(double[] controls) {
		return 500+(controls[3]*(maxRPM-500)); 		 // TODO need better method of getting RPM
	}
}
