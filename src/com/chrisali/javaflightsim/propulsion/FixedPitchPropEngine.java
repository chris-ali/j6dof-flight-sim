package com.chrisali.javaflightsim.propulsion;

public class FixedPitchPropEngine extends EngineModel {
	
	public FixedPitchPropEngine(double maxBHP, double maxRPM, double propDiameter, double[] enginePosition) {
		this.maxBHP 		= maxBHP;
		this.maxRPM 		= maxRPM;
		this.propArea 		= Math.PI*(Math.pow(propDiameter, 2))/4;
		this.propEfficiency = 0.85;
		this.enginePosition = enginePosition;
		
		if (enginePosition[1] > 0) // Determines whether the engine is on the left/right
			this.isRightSide = 1;
		else
			this.isRightSide = 0;
	}
	
	public FixedPitchPropEngine() {
		this.maxBHP 		= 200;
		this.maxRPM 		= 2700;
		this.propArea 		= Math.PI*(Math.pow(6.5, 2))/4;
		this.propEfficiency = 0.85;
		this.enginePosition = new double[] {0, 0, 0};
		this.isRightSide    = 0;
	}
	
	// Update all states for one engine
	public void updateEngineState(double[] controls,				//{elevator,aileron,rudder,throttle,propeller,mixture,flaps,gear,leftBrake,rightBrake}
								  double[] NEDPosition,				//{N,E,D}
								  double[] environmentParameters,	//{temp,rho,p,a}
								  double[] windParameters) {		//{vTrue,beta,alpha}
		
		calculateThrust(controls,
						NEDPosition,
						environmentParameters,
						windParameters);
		
		calculateEngMoments();
		
		calculateFuelFlow(controls);
		
		calculateRPM(controls);
	}
	
	//TODO consider engine orientation
	private void calculateThrust(double[] controls,				 
			 					 double[] NEDPosition,			 
			 					 double[] environmentParameters, 
								 double[] windParameters) {		 
		
		// Consider static thrust case at low speeds
		if (windParameters[0] <= 5)
			this.engineThrust[0] = Math.pow((controls[3+isRightSide]*maxBHP*HP_2_FTLBS), 2/3)*Math.pow(2*environmentParameters[1]*propArea, 1/3);			
		else
			this.engineThrust[0] = (controls[3+isRightSide]*maxBHP*HP_2_FTLBS)*((A_P*environmentParameters[1]/RHO_SSL)-B_P)*(propEfficiency/windParameters[0]);
	}
	
	private void calculateFuelFlow(double[] controls) {
		this.fuelFlow = (0.9+(controls[3+isRightSide]*14.8))*controls[7+isRightSide]; // TODO need better method of getting fuel flow
	}
	
	private void calculateRPM(double[] controls) {
		this.rpm = 500+(controls[3+isRightSide]*(maxRPM-500)); 		 // TODO need better method of getting RPM
	}
}
