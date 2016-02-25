package com.chrisali.javaflightsim.instrumentpanel;

import java.util.EnumSet;

import javax.swing.SwingUtilities;

import com.chrisali.javaflightsim.aircraft.AircraftBuilder;
import com.chrisali.javaflightsim.instrumentpanel.flightdata.FlightData;
import com.chrisali.javaflightsim.instrumentpanel.panel.InstrumentPanel;
import com.chrisali.javaflightsim.setup.Options;
import com.chrisali.javaflightsim.utilities.integration.Integrate6DOFEquations;

public class RunInstrumentPanel {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {runApp();}
		});
	}

	private static void runApp() {
		Integrate6DOFEquations runSim = new Integrate6DOFEquations(new AircraftBuilder("LookupNavion"),
																   EnumSet.of(Options.UNLIMITED_FLIGHT, Options.USE_JOYSTICK));
		FlightData flightData = new FlightData(runSim);

		new Thread(runSim).start();
		new Thread(flightData).start();
		
		InstrumentPanel panel = new InstrumentPanel();
		flightData.setFlightDataListener(panel);
	}
}