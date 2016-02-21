package com.chrisali.javaflightsim.instrumentpanel.panel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.border.Border;

import com.chrisali.javaflightsim.instrumentpanel.flightdata.FlightData;
import com.chrisali.javaflightsim.instrumentpanel.flightdata.FlightDataType;
import com.chrisali.javaflightsim.instrumentpanel.gauges.AirspeedIndicator;
import com.chrisali.javaflightsim.instrumentpanel.gauges.Altimeter;
import com.chrisali.javaflightsim.instrumentpanel.gauges.ArtificialHorizon;
import com.chrisali.javaflightsim.instrumentpanel.gauges.DirectionalGyro;
import com.chrisali.javaflightsim.instrumentpanel.gauges.Inclinometer;
import com.chrisali.javaflightsim.instrumentpanel.gauges.TurnCoordinator;
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
	private TurnCoordinator   turnCoordinator;

	private FlightData flightData;
	
	/**
	 * Creates a simple instrument panel with a {@link FlightDataListener} to set the gauge values from
	 * flight data received by the simulation in {@link FlightData}
	 * 
	 * @param flightData
	 */
	public InstrumentPanel(FlightData flightData) {
		super("Panel");
		
		this.flightData = flightData;
		
		int margin = 5;
		Border outerBorder = BorderFactory.createEmptyBorder(margin,margin,margin,margin);
		Border innerBorder = BorderFactory.createEtchedBorder();
		
		setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		
		gc.anchor = GridBagConstraints.EAST;
		gc.gridx = 6;
		gc.gridy = 5;
		gc.weightx = 100;
		gc.weighty = 100;
		gc.fill = GridBagConstraints.BOTH;
		
		altimeter = new Altimeter();
		altimeter.setUnitString("");
		altimeter.setTitle(altimeter.toString());
		altimeter.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
		
		add(altimeter, gc);
		
		gc.anchor = GridBagConstraints.CENTER;
		gc.gridx = 5;
		gc.gridy = 5;
		gc.weightx = 100;
		gc.weighty = 100;
		gc.fill = GridBagConstraints.BOTH;
		
		artificalHorizon = new ArtificialHorizon();
		artificalHorizon.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
		
		add(artificalHorizon, gc);
		
		gc.anchor = GridBagConstraints.SOUTH;
		gc.gridx = 5;
		gc.gridy = 6;
		gc.weightx = 100;
		gc.weighty = 100;
		gc.fill = GridBagConstraints.BOTH;
		
		directionalGyro = new DirectionalGyro();
		directionalGyro.setUnitString("");
		directionalGyro.setTitle(directionalGyro.toString());
		directionalGyro.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
		
		add(directionalGyro, gc);
		
		gc.anchor = GridBagConstraints.NORTHWEST;
		gc.gridx = 4;
		gc.gridy = 5;
		gc.weightx = 100;
		gc.weighty = 100;
		gc.fill = GridBagConstraints.BOTH;
		
		airspeedIndicator = new AirspeedIndicator();
		airspeedIndicator.setUnitString("KNOTS");
		airspeedIndicator.setTitle(airspeedIndicator.toString());
		airspeedIndicator.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
		
		add(airspeedIndicator, gc);
		
		gc.anchor = GridBagConstraints.SOUTHEAST;
		gc.gridx = 6;
		gc.gridy = 6;
		gc.weightx = 100;
		gc.weighty = 100;
		gc.fill = GridBagConstraints.BOTH;
		
		verticalSpeed = new VerticalSpeed();
		verticalSpeed.setUnitString("x1000 FT/MIN");
		verticalSpeed.setTitle(verticalSpeed.toString());
		verticalSpeed.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
		
		add(verticalSpeed, gc);
		
		gc.anchor = GridBagConstraints.SOUTHWEST;
		gc.gridx = 4;
		gc.gridy = 6;
		gc.weightx = 100;
		gc.weighty = 100;
		gc.fill = GridBagConstraints.BOTH;
		
		turnCoordinator = new TurnCoordinator();
				
		//add(turnCoordinator, gc);
		
		gc.anchor = GridBagConstraints.SOUTHWEST;
		gc.gridx = 4;
		gc.gridy = 6;
		gc.weightx = 100;
		gc.weighty = 100;
		gc.fill = GridBagConstraints.BOTH;
		
		inlinometer = new Inclinometer();
		inlinometer.setUnitString("");
		inlinometer.setTitle(inlinometer.toString());
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
	public void onFlightDataReceived() {
		artificalHorizon.setPitch(flightData.getFlightData().get(FlightDataType.PITCH));
		artificalHorizon.setRoll(flightData.getFlightData().get(FlightDataType.ROLL));
		
		altimeter.setValue(flightData.getFlightData().get(FlightDataType.ALTITUDE));
		
		airspeedIndicator.setValue(flightData.getFlightData().get(FlightDataType.IAS));
		
		directionalGyro.setValue(flightData.getFlightData().get(FlightDataType.HEADING));
		
		verticalSpeed.setValue(flightData.getFlightData().get(FlightDataType.VERT_SPEED));
		
		inlinometer.setValueAnimated(flightData.getFlightData().get(FlightDataType.TURN_RATE));
		turnCoordinator.setValueAnimated(flightData.getFlightData().get(FlightDataType.TURN_COORD));
		
		System.out.println(flightData);
	}
}
