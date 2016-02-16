package com.chrisali.javaflightsim.instrumentpanel.panel;

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
		altimeter.init(300, 300);
		altimeter.setMaxValue(1E6);
		altimeter.setUnitString("Calib. to 20000 ft");
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
		artificalHorizon.init(300, 300);
		artificalHorizon.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
		
		add(artificalHorizon, gc);
		
		gc.anchor = GridBagConstraints.SOUTH;
		gc.gridx = 5;
		gc.gridy = 6;
		gc.weightx = 100;
		gc.weighty = 100;
		gc.fill = GridBagConstraints.BOTH;
		
		directionalGyro = new DirectionalGyro();
		directionalGyro.init(300, 300);
		directionalGyro.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
		
		add(directionalGyro, gc);
		
		gc.anchor = GridBagConstraints.NORTHWEST;
		gc.gridx = 4;
		gc.gridy = 5;
		gc.weightx = 100;
		gc.weighty = 100;
		gc.fill = GridBagConstraints.BOTH;
		
		airspeedIndicator = new AirspeedIndicator();
		airspeedIndicator.init(300, 300);
		airspeedIndicator.setMaxValue(200);
		airspeedIndicator.setUnitString("Knots");
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
		verticalSpeed.init(300, 300);
		verticalSpeed.setUnitString("x100 ft/min");
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
		turnCoordinator.init(300, 300);
		
		add(turnCoordinator, gc);
		
		gc.anchor = GridBagConstraints.SOUTHWEST;
		gc.gridx = 4;
		gc.gridy = 6;
		gc.weightx = 100;
		gc.weighty = 100;
		gc.fill = GridBagConstraints.BOTH;
		
		inlinometer = new Inclinometer();
		inlinometer.init(300, 300);
		inlinometer.setUnitString("No Pitch Information");
		inlinometer.setTitle(inlinometer.toString());
		inlinometer.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
		
		add(inlinometer, gc);
		
		setSize(900, 660);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

	@Override
	public void onFlightDataReceived() {
		artificalHorizon.setPitchAnimated(flightData.getFlightData().get(FlightDataType.PITCH));
		artificalHorizon.setRollAnimated(flightData.getFlightData().get(FlightDataType.ROLL));
		
		altimeter.setValueAnimated(flightData.getFlightData().get(FlightDataType.ALTITUDE));
		
		directionalGyro.setValueAnimated(flightData.getFlightData().get(FlightDataType.HEADING));
		
		verticalSpeed.setValueAnimated(flightData.getFlightData().get(FlightDataType.VERT_SPEED));
		
		inlinometer.setValueAnimated(flightData.getFlightData().get(FlightDataType.TURN_RATE));
		turnCoordinator.setValueAnimated(flightData.getFlightData().get(FlightDataType.TURN_COORD));
		
		System.out.println(flightData);
	}
}
