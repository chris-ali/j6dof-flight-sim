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
		
		new Thread(new Integrate6DOFEquations(new AircraftBuilder(), EnumSet.of(Options.CONSOLE_DISPLAY))).start();
	}
	
	private static void runApp() {
		FlightData flightData = new FlightData();
		
		InstrumentPanel panel = new InstrumentPanel(flightData);
		
		
	}
}
