package com.chrisali.javaflightsim.propulsion;

import java.util.EnumMap;

import com.chrisali.javaflightsim.controls.FlightControls;

public class FixedPitchPropEngine extends EngineModel {
	
	private double throttle;
	private double mixture;
	
	public FixedPitchPropEngine(double maxBHP, double maxRPM, double propDiameter, double[] enginePosition) {
		this.maxBHP 		= maxBHP;
		this.maxRPM 		= maxRPM;
		this.propArea 		= Math.PI*(Math.pow(propDiameter, 2))/4;
		this.propEfficiency = 0.85;
		this.enginePosition = enginePosition;
		
		if (enginePosition[1] > 0) // Determines whether the engine is on the left/right
			this.isRightEngine = true;
		else
			this.isRightEngine = false;
	}
	
	public FixedPitchPropEngine() {
		this.maxBHP 		= 200;
		this.maxRPM 		= 2700;
		this.propArea 		= Math.PI*(Math.pow(6.5, 2))/4;
		this.propEfficiency = 0.85;
		this.enginePosition = new double[] {0, 0, 0};
		this.isRightEngine  = false;
	}
	
	// Update all states for one engine
	public void updateEngineState(EnumMap<FlightControls, Double> controls,				
								  double[] NEDPosition,				//{N,E,D}
								  double[] environmentParameters,	//{temp,rho,p,a}
								  double[] windParameters) {		//{vTrue,beta,alpha}
		// Get engine controls' position depending on if right/left engine
		if(isRightEngine) {
			mixture  = controls.get(FlightControls.MIXTURE_R);
			throttle = controls.get(FlightControls.THROTTLE_R);
		} else {
			mixture  = controls.get(FlightControls.MIXTURE_L);
			throttle = controls.get(FlightControls.THROTTLE_L);
		}
		
		calculateThrust(NEDPosition,
						environmentParameters,
						windParameters);
		
		calculateEngMoments();
		
		calculateFuelFlow();
		
		calculateRPM();
	}
	
	//TODO consider engine orientation
	private void calculateThrust(double[] NEDPosition,			 
			 					 double[] environmentParameters, 
								 double[] windParameters) {		 
		// Consider static thrust case at low speeds
		if (windParameters[0] <= 5)
			this.engineThrust[0] = Math.pow((throttle*maxBHP*HP_2_FTLBS), 2/3)*Math.pow(2*environmentParameters[1]*propArea, 1/3);			
		else
			this.engineThrust[0] = (throttle*maxBHP*HP_2_FTLBS)*((A_P*environmentParameters[1]/RHO_SSL)-B_P)*(propEfficiency/windParameters[0]);
	}
	
	private void calculateFuelFlow() {this.fuelFlow = (0.9+(throttle*14.8))*mixture;} // TODO need better method of getting fuel flow
	
	private void calculateRPM() {this.rpm = 500+(throttle*(maxRPM-500));} 		 // TODO need better method of getting RPM
}
