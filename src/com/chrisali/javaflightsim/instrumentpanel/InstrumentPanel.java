package com.chrisali.javaflightsim.instrumentpanel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.DecimalFormat;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import com.chrisali.javaflightsim.datatransfer.FlightData;
import com.chrisali.javaflightsim.datatransfer.FlightDataListener;
import com.chrisali.javaflightsim.datatransfer.FlightDataType;
import com.chrisali.javaflightsim.instrumentpanel.gauges.AirspeedIndicator;
import com.chrisali.javaflightsim.instrumentpanel.gauges.Altimeter;
import com.chrisali.javaflightsim.instrumentpanel.gauges.ArtificialHorizon;
import com.chrisali.javaflightsim.instrumentpanel.gauges.DirectionalGyro;
import com.chrisali.javaflightsim.instrumentpanel.gauges.Led;
import com.chrisali.javaflightsim.instrumentpanel.gauges.Tachometer;
import com.chrisali.javaflightsim.instrumentpanel.gauges.TurnCoordinator;
import com.chrisali.javaflightsim.instrumentpanel.gauges.VerticalSpeed;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;

import eu.hansolo.steelseries.tools.LedColor;

/**
 *	Simple Swing GUI of an aircraft instrument panel with custom gauges implementing Gerrit Grunwald's SteelSeries
 *  gauge framework. Instruments are arranged in a typical "six-pack" layout common in general aviation aircraft, and
 *  make use of flight data gathered by {@link FlightData} from {@link Integrate6DOFEquations}
 */
public class InstrumentPanel extends JPanel implements FlightDataListener {

	private static final long serialVersionUID = -3900476226233156470L;
	
	private Altimeter 		   altimeter; 
	private	ArtificialHorizon  artificalHorizon; 
	private	DirectionalGyro	   directionalGyro; 
	private AirspeedIndicator  airspeedIndicator;
	private VerticalSpeed 	   verticalSpeed;
	private TurnCoordinator	   turnCoordinator;
	private Tachometer	  	   tachometer;
	private JLabel			   flapsIndicator;
	private Led				   gearIndicator;
	
	/**
	 * Creates a simple instrument panel with a {@link FlightDataListener} to set the gauge values from
	 * flight data received by the simulation in {@link FlightData}
	 */
	public InstrumentPanel() {
		super();
		
		//====================== <- Glareshield
		//| [  ] [  ] [  ] [  ]| 
		//| [  ] [  ] [  ]	   | <- Main Instruments
		//|--------------------|
		//|____________________| <- Aux Instruments
		
		//============================ Borders and Grid Bag Setup ==============================
		
		int margin = 5;
		Border outerBorder = BorderFactory.createEmptyBorder(margin,margin,margin,margin);
		Border innerBorder = BorderFactory.createEtchedBorder();

		JPanel glareshield = new JPanel();
		JPanel mainInstruments = new JPanel();
		JPanel auxInstruments  = new JPanel();
		
		glareshield.setLayout(new GridBagLayout());
		glareshield.setBackground(Color.BLACK);
		
		mainInstruments.setLayout(new GridBagLayout());
		mainInstruments.setBackground(Color.DARK_GRAY);
		
		auxInstruments.setLayout(new GridBagLayout());
		auxInstruments.setBackground(Color.GRAY);
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints gc = new GridBagConstraints();
		
		gc.fill = GridBagConstraints.BOTH;
		gc.gridy = 0;
		gc.weightx = 100;
		gc.weighty = 5;
		
		add(glareshield, gc);
		
		gc.gridy = 1;
		gc.weighty = 100;
		
		add(mainInstruments,gc);
		
		gc.gridy = 2;
		gc.weighty = 10;
		
		add(auxInstruments, gc);
		
		gc.weighty    = 100;
		gc.gridwidth  = 2;
		gc.gridheight = 2;
		
		//==================================== Glareshield ======================================
		
		gc.gridx      = 2;
		gc.gridy      = 2;
		
		glareshield.add(new JLabel(" "), gc);
		
		//================================= Main Instruments ====================================
		//------------------------------------ Altimeter ----------------------------------------
		
		gc.gridx = 12;
		gc.gridy = 10;
		
		altimeter = new Altimeter();
		altimeter.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
		
		mainInstruments.add(altimeter, gc);
		
		//-------------------------------- Artificial Horizon ------------------------------------
		
		gc.gridx = 10;
		gc.gridy = 10;
		
		artificalHorizon = new ArtificialHorizon();
		artificalHorizon.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
		
		mainInstruments.add(artificalHorizon, gc);
		
		//-------------------------------- Directional Gyro -------------------------------------
		
		gc.gridx = 10;
		gc.gridy = 12;
		
		directionalGyro = new DirectionalGyro();
		directionalGyro.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
		
		mainInstruments.add(directionalGyro, gc);
		
		//-------------------------------- Airspeed Indicator ------------------------------------
		
		gc.gridx = 8;
		gc.gridy = 10;
		
		airspeedIndicator = new AirspeedIndicator();
		airspeedIndicator.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
		
		mainInstruments.add(airspeedIndicator, gc);
		
		//---------------------------------- Vertical Speed --------------------------------------
		
		gc.gridx = 12;
		gc.gridy = 12;
		
		verticalSpeed = new VerticalSpeed();
		verticalSpeed.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
		
		mainInstruments.add(verticalSpeed, gc);
		
		//----------------------------------- Inclinometer ---------------------------------------

		gc.gridx = 8;
		gc.gridy = 12;
		
		turnCoordinator = new TurnCoordinator();
		turnCoordinator.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
		
		mainInstruments.add(turnCoordinator, gc);
		
		//----------------------------------- Tachometer -----------------------------------------
		
		gc.gridx = 14;
		gc.gridy = 10;
		
		tachometer = new Tachometer();
		tachometer.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
		
		mainInstruments.add(tachometer, gc);
		
		//============================= Auxiliary Instruments ====================================
		//--------------------------------- Reserved Space ---------------------------------------
		
		gc.fill = GridBagConstraints.BOTH;
		gc.weightx    = 100;
		gc.weighty    = 100;
		
		gc.gridx      = 0;
		gc.gridy      = 0;
		
		auxInstruments.add(new JLabel(""), gc);
		
		gc.gridx      = 0;
		gc.gridy      = 2;
		
		auxInstruments.add(new JLabel(""), gc);
		
		//--------------------------------- Gear Indicator ---------------------------------------
		
		gc.anchor = GridBagConstraints.CENTER;
		gc.weightx    = 1;
		
		gc.gridx      = 2;
		gc.gridy      = 0;
		
		auxInstruments.add(new JLabel("Gear"), gc);
		
		gc.gridx      = 2;
		gc.gridy      = 2;
		
		gearIndicator = new Led();
		gearIndicator.setLedColor(LedColor.GREEN_LED);
		
		auxInstruments.add(gearIndicator, gc);
		
		//--------------------------------- Flaps Indicator ---------------------------------------
		
		gc.weightx    = 1;
		gc.weighty    = 1;
		
		gc.gridx      = 4;
		gc.gridy      = 0;
		
		auxInstruments.add(new JLabel("Flaps"), gc);
		
		gc.gridx      = 4;
		gc.gridy      = 2;
		
		flapsIndicator = new JLabel("");
		
		auxInstruments.add(flapsIndicator, gc);
		
		//========================== Window Settings =============================================
		
		setSize(648, 500);
	}

	/**
	 * When the instrument panel receives the event that data was received, use the values stored
	 * in {@link FlightData}'s EnumMap flightData to set gauge values
	 */
	@Override
	public void onFlightDataReceived(FlightData flightData) {
		Map<FlightDataType, Double> receivedFlightData = flightData.getFlightData();
		DecimalFormat df = new DecimalFormat("#");
		
		if (!receivedFlightData.containsValue(null)) {
			artificalHorizon.setPitch(receivedFlightData.get(FlightDataType.PITCH));
			artificalHorizon.setRoll(receivedFlightData.get(FlightDataType.ROLL));
			
			altimeter.setValue(receivedFlightData.get(FlightDataType.ALTITUDE));
			
			airspeedIndicator.setValue(receivedFlightData.get(FlightDataType.IAS));
			
			directionalGyro.setValue(receivedFlightData.get(FlightDataType.HEADING));
			
			verticalSpeed.setValue(receivedFlightData.get(FlightDataType.VERT_SPEED));
			
			turnCoordinator.setInclinoValue(receivedFlightData.get(FlightDataType.TURN_RATE));
			turnCoordinator.setCoordValue(receivedFlightData.get(FlightDataType.TURN_COORD));
			
			tachometer.setLeftValue(receivedFlightData.get(FlightDataType.RPM_1));
			tachometer.setRightValue(receivedFlightData.get(FlightDataType.RPM_2));
			
			flapsIndicator.setText(String.valueOf(df.format(receivedFlightData.get(FlightDataType.FLAPS))));
			gearIndicator.setLedOn(receivedFlightData.get(FlightDataType.GEAR) == 1.0);
		}
	}
}
