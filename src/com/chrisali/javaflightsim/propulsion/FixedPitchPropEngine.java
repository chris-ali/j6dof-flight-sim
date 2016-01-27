package com.chrisali.javaflightsim.propulsion;

import java.util.Arrays;
import java.util.EnumMap;

import com.chrisali.javaflightsim.controls.FlightControls;
import com.chrisali.javaflightsim.enviroment.EnvironmentParameters;

public class FixedPitchPropEngine extends Engine {
	private double throttle;
	private double mixture;
	
	public FixedPitchPropEngine() {
		this.engineName		= "Lycoming IO-360";
		this.maxBHP 		= 200;
		this.maxRPM 		= 2700;
		this.propArea 		= Math.PI*(Math.pow(6.5, 2))/4;
		this.propEfficiency = 0.85;
		this.enginePosition = new double[] {0, 0, 0};
		this.engineNumber   = 1;
	}
	
	public FixedPitchPropEngine(String engineName, double maxBHP, double maxRPM, double propDiameter, double[] enginePosition, int engineNumber) {
		this.engineName		= engineName;
		this.maxBHP 		= maxBHP;
		this.maxRPM 		= maxRPM;
		this.propArea 		= Math.PI*(Math.pow(propDiameter, 2))/4;
		this.propEfficiency = 0.85;
		this.enginePosition = enginePosition;
		this.engineNumber   = engineNumber;
	}
		
	// Update all states for one engine
	public void updateEngineState(EnumMap<FlightControls, Double> controls,				
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
		
		calculateThrust(environmentParameters, windParameters);
		
		calculateEngMoments();
		
		calculateFuelFlow();
		
		calculateRPM();
	}

	//TODO consider engine orientation
	private void calculateThrust(EnumMap<EnvironmentParameters, Double> environmentParameters, 
								 double[] windParameters) {		 
		// Consider static thrust case at low speeds
		if (windParameters[0] <= 65)
			this.engineThrust[0] = Math.pow((throttle*maxBHP*HP_2_FTLBS), 0.6667)*Math.pow((2*environmentParameters.get(EnvironmentParameters.RHO)*propArea), 0.3333);			
		else
			this.engineThrust[0] = (throttle*maxBHP*HP_2_FTLBS)*((A_P*environmentParameters.get(EnvironmentParameters.RHO)/RHO_SSL)-B_P)*(propEfficiency/windParameters[0]);
	}
	
	private void calculateFuelFlow() {this.fuelFlow = (0.9+(throttle*14.8))*mixture;} // TODO need better method of getting fuel flow
	
	private void calculateRPM() {this.rpm = 500+(throttle*(maxRPM-500));} 		 // TODO need better method of getting RPM
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("Engine: ").append(engineName).append(" (# ").append(Integer.toString(engineNumber)).append(")")
		  .append("\nMax BHP: ").append(maxBHP)
		  .append("\nMax RPM: ").append(maxRPM)
		  .append("\nProp Diameter [ft]: ").append(propDiameter)
		  .append("\nEngine Position [ft]: ").append(Arrays.toString(enginePosition));
		
		return sb.toString();
	}
}

