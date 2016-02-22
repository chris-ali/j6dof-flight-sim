package com.chrisali.javaflightsim.instrumentpanel.panel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.border.Border;

import com.chrisali.javaflightsim.instrumentpanel.flightdata.FlightData;
import com.chrisali.javaflightsim.instrumentpanel.flightdata.FlightDataListener;
import com.chrisali.javaflightsim.instrumentpanel.flightdata.FlightDataType;
import com.chrisali.javaflightsim.instrumentpanel.gauges.AirspeedIndicator;
import com.chrisali.javaflightsim.instrumentpanel.gauges.Altimeter;
import com.chrisali.javaflightsim.instrumentpanel.gauges.ArtificialHorizon;
import com.chrisali.javaflightsim.instrumentpanel.gauges.DirectionalGyro;
import com.chrisali.javaflightsim.instrumentpanel.gauges.Inclinometer;
import com.chrisali.javaflightsim.instrumentpanel.gauges.VerticalSpeed;
import com.chrisali.javaflightsim.utilities.integration.Integrate6DOFEquations;

/**
 *	Simple Swing GUI of an aircraft instrument panel with custom gauges implementing Gerrit Grunwald's SteelSeries
 *  gauge framework. Instruments are arranged in a typical "six-pack" layout common in general aviation aircraft, and
 *  make use of flight data gathered by {@link FlightData} from {@link Integrate6DOFEquations}
 */
public class InstrumentPanel extends JFrame implements FlightDataListener {

	private static final long serialVersionUID = -3900476226233156470L;
	
	private Altimeter 		  altimeter; 
	private	ArtificialHorizon artificalHorizon; 
	private	DirectionalGyro	  directionalGyro; 
	private AirspeedIndicator airspeedIndicator;
	private VerticalSpeed 	  verticalSpeed;
	private Inclinometer	  inlinometer;
	
	/**
	 * Creates a simple instrument panel with a {@link FlightDataListener} to set the gauge values from
	 * flight data received by the simulation in {@link FlightData}
	 */
	public InstrumentPanel() {
		super("Panel");
		
		int margin = 5;
		Border outerBorder = BorderFactory.createEmptyBorder(margin,margin,margin,margin);
		Border innerBorder = BorderFactory.createEtchedBorder();
		
		setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		
		gc.fill = GridBagConstraints.BOTH;
		gc.weightx = 100;
		gc.weighty = 100;
		
		//------------------------------------ Altimeter ----------------------------------------
		
		gc.gridx = 6;
		gc.gridy = 5;
		
		altimeter = new Altimeter();
		altimeter.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
		
		add(altimeter, gc);
		
		//-------------------------------- Artificial Horizon ------------------------------------
		
		gc.gridx = 5;
		gc.gridy = 5;
		
		artificalHorizon = new ArtificialHorizon();
		artificalHorizon.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
		
		add(artificalHorizon, gc);
		
		//-------------------------------- Directional Gyro -------------------------------------
		
		gc.gridx = 5;
		gc.gridy = 6;
		
		directionalGyro = new DirectionalGyro();
		directionalGyro.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
		
		add(directionalGyro, gc);
		
		//-------------------------------- Airspeed Indicator ------------------------------------
		
		gc.gridx = 4;
		gc.gridy = 5;
		
		airspeedIndicator = new AirspeedIndicator();
		airspeedIndicator.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
		
		add(airspeedIndicator, gc);
		
		//---------------------------------- Vertical Speed --------------------------------------
		
		gc.gridx = 6;
		gc.gridy = 6;
		
		verticalSpeed = new VerticalSpeed();
		verticalSpeed.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
		
		add(verticalSpeed, gc);
		
		//----------------------------------- Inclinometer ---------------------------------------

		gc.gridx = 4;
		gc.gridy = 6;
		
		inlinometer = new Inclinometer();
		inlinometer.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
		
		add(inlinometer, gc);
		
		getContentPane().setBackground(Color.DARK_GRAY);
		setSize(900, 620);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

	/**
	 * When the instrument panel receives the event that data was received, use the values stored
	 * in {@link FlightData}'s EnumMap flightData to set gauge values
	 */
	@Override
	public void onFlightDataReceived(FlightData flightData) {
		artificalHorizon.setPitch(flightData.getFlightData().get(FlightDataType.PITCH));
		artificalHorizon.setRoll(flightData.getFlightData().get(FlightDataType.ROLL));
		
		altimeter.setValue(flightData.getFlightData().get(FlightDataType.ALTITUDE));
		
		airspeedIndicator.setValue(flightData.getFlightData().get(FlightDataType.IAS));
		
		directionalGyro.setValue(flightData.getFlightData().get(FlightDataType.HEADING));
		
		verticalSpeed.setValue(flightData.getFlightData().get(FlightDataType.VERT_SPEED));
		
		inlinometer.setValueAnimated(flightData.getFlightData().get(FlightDataType.TURN_RATE));
		
		//System.out.println(flightData);
	}
}
