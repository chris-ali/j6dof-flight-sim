package com.chrisali.javaflightsim.propulsion;

import java.util.EnumMap;

import com.chrisali.javaflightsim.controls.FlightControls;
import com.chrisali.javaflightsim.enviroment.EnvironmentParameters;

public class FixedPitchPropEngine extends EngineModel {
	
	private double throttle;
	private double mixture;
	
	public FixedPitchPropEngine(double maxBHP, double maxRPM, double propDiameter, double[] enginePosition, int engineNumber) {
		this.maxBHP 		= maxBHP;
		this.maxRPM 		= maxRPM;
		this.propArea 		= Math.PI*(Math.pow(propDiameter, 2))/4;
		this.propEfficiency = 0.85;
		this.enginePosition = enginePosition;
		this.engineNumber   = engineNumber;
	}
	
	public FixedPitchPropEngine() {
		this.maxBHP 		= 200;
		this.maxRPM 		= 2700;
		this.propArea 		= Math.PI*(Math.pow(6.5, 2))/4;
		this.propEfficiency = 0.85;
		this.enginePosition = new double[] {0, 0, 0};
		this.engineNumber   = 1;
	}
	
	// Update all states for one engine
	public void updateEngineState(EnumMap<FlightControls, Double> controls,				
								  double[] NEDPosition,				//{N,E,D}
								  EnumMap<EnvironmentParameters, Double> environmentParameters,
								  double[] windParameters) {		//{vTrue,beta,alpha}
		// Assign engine controls depending on engine number specified
		switch (engineNumber) {
			case 1:
				mixture  = controls.get(FlightControls.MIXTURE_1);
				throttle = controls.get(FlightControls.THROTTLE_1);
				break;
			case 2:
				mixture  = controls.get(FlightControls.MIXTURE_2);
				throttle = controls.get(FlightControls.THROTTLE_2);
				break;
			case 3:
				mixture  = controls.get(FlightControls.MIXTURE_3);
				throttle = controls.get(FlightControls.THROTTLE_3);
				break;
			case 4:
				mixture  = controls.get(FlightControls.MIXTURE_4);
				throttle = controls.get(FlightControls.THROTTLE_4);
				break;
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
								 EnumMap<EnvironmentParameters, Double> environmentParameters, 
								 double[] windParameters) {		 
		// Consider static thrust case at low speeds
		if (windParameters[0] <= 5)
			this.engineThrust[0] = Math.pow((throttle*maxBHP*HP_2_FTLBS), 2/3)*Math.pow(2*environmentParameters.get(EnvironmentParameters.RHO)*propArea, 1/3);			
		else
			this.engineThrust[0] = (throttle*maxBHP*HP_2_FTLBS)*((A_P*environmentParameters.get(EnvironmentParameters.RHO)/RHO_SSL)-B_P)*(propEfficiency/windParameters[0]);
	}
	
	private void calculateFuelFlow() {this.fuelFlow = (0.9+(throttle*14.8))*mixture;} // TODO need better method of getting fuel flow
	
	private void calculateRPM() {this.rpm = 500+(throttle*(maxRPM-500));} 		 // TODO need better method of getting RPM
}

