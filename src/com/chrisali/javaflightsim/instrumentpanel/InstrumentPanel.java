package com.chrisali.javaflightsim.instrumentpanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.border.Border;

import com.chrisali.javaflightsim.datatransfer.FlightData;
import com.chrisali.javaflightsim.datatransfer.FlightDataListener;
import com.chrisali.javaflightsim.datatransfer.FlightDataType;
import com.chrisali.javaflightsim.instrumentpanel.gauges.AirspeedIndicator;
import com.chrisali.javaflightsim.instrumentpanel.gauges.Altimeter;
import com.chrisali.javaflightsim.instrumentpanel.gauges.ArtificialHorizon;
import com.chrisali.javaflightsim.instrumentpanel.gauges.DirectionalGyro;
import com.chrisali.javaflightsim.instrumentpanel.gauges.Tachometer;
import com.chrisali.javaflightsim.instrumentpanel.gauges.TurnCoordinator;
import com.chrisali.javaflightsim.instrumentpanel.gauges.VerticalSpeed;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;

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
	private TurnCoordinator	  turnCoordinator;
	private Tachometer	  	  tachometer;
	
	private ClosePanelListener closePanelListener;
	
	/**
	 * Creates a simple instrument panel with a {@link FlightDataListener} to set the gauge values from
	 * flight data received by the simulation in {@link FlightData}
	 */
	public InstrumentPanel() {
		super("Panel");
		
		//----------------------------- Borders and Grid Bag Setup ------------------------------
		
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
		
		turnCoordinator = new TurnCoordinator();
		turnCoordinator.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
		
		add(turnCoordinator, gc);
		
		//----------------------------------- Tachometer -----------------------------------------

		gc.gridx = 7;
		gc.gridy = 6;
		
		tachometer = new Tachometer();
		tachometer.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
		
		add(tachometer, gc);
		
		//========================== Window Settings =============================================
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (closePanelListener != null)
					closePanelListener.panelWindowClosed();
			}
		});
		
		getContentPane().setBackground(Color.DARK_GRAY);
		setSize(900, 480);
		setMinimumSize(new Dimension(900, 480));
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}
	
	/**
	 * Sets a listener to monitor for a window closing event so that the simulation can stop
	 * 
	 * @param closePanelListener
	 */
	public void setClosePanelListener(ClosePanelListener closePanelListener) {
		this.closePanelListener = closePanelListener;
	}

	/**
	 * When the instrument panel receives the event that data was received, use the values stored
	 * in {@link FlightData}'s EnumMap flightData to set gauge values
	 */
	@Override
	public void onFlightDataReceived(FlightData flightData) {
		Map<FlightDataType, Double> receivedFlightData = flightData.getFlightData();
		
		if (!receivedFlightData.containsValue(null)) {
			artificalHorizon.setPitch(receivedFlightData.get(FlightDataType.PITCH));
			artificalHorizon.setRoll(receivedFlightData.get(FlightDataType.ROLL));
			
			altimeter.setValue(receivedFlightData.get(FlightDataType.ALTITUDE));
			
			airspeedIndicator.setValue(receivedFlightData.get(FlightDataType.IAS));
			
			directionalGyro.setValue(receivedFlightData.get(FlightDataType.HEADING));
			
			verticalSpeed.setValue(receivedFlightData.get(FlightDataType.VERT_SPEED));
			
			turnCoordinator.setInclinoValue(receivedFlightData.get(FlightDataType.TURN_RATE));
			turnCoordinator.setCoordValue(receivedFlightData.get(FlightDataType.TURN_COORD));
			
			tachometer.setLeftValue(receivedFlightData.get(FlightDataType.RPM_L));
			tachometer.setRightValue(receivedFlightData.get(FlightDataType.RPM_R));
		}
	}
}
