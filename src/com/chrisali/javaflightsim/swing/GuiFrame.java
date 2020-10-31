/*******************************************************************************
 * Copyright (C) 2016-2020 Christopher Ali
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *  If you have any questions about this project, you can visit
 *  the project's GitHub repository at: http://github.com/chris-ali/j6dof-flight-sim/
 ******************************************************************************/
package com.chrisali.javaflightsim.swing;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.chrisali.javaflightsim.simulation.aircraft.Aircraft;
import com.chrisali.javaflightsim.simulation.datatransfer.SimulationEventListener;
import com.chrisali.javaflightsim.simulation.setup.Options;
import com.chrisali.javaflightsim.simulation.setup.SimulationConfiguration;
import com.chrisali.javaflightsim.simulation.utilities.FileUtilities;
import com.chrisali.javaflightsim.swing.aircraftpanel.AircraftPanel;
import com.chrisali.javaflightsim.swing.initialconditionspanel.InitialConditionsPanel;
import com.chrisali.javaflightsim.swing.optionspanel.OptionsPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Main Swing class that contains the main menus to configure the and start simulation. 
 * 
 * @author Christopher Ali
 *
 */
public class GuiFrame extends JFrame {

	private static final long serialVersionUID = -1803264930661591606L;
	
	private static final Logger logger = LogManager.getLogger(GuiFrame.class);
	
	private SimulationConfiguration configuration;
	private Aircraft aircraft;

	private List<SimulationEventListener> simulationEventListeners = new ArrayList<>();
	
	private ButtonPanel buttonPanel;
	private AircraftPanel aircraftPanel;
	private OptionsPanel optionsPanel;
	private InitialConditionsPanel initialConditionsPanel;
	private JPanel cardPanel; 
	private CardLayout cardLayout;
	
	/**
	 * Initializes the main GUI window of Java Flight Simulator
	 * 
	 * @param controller
	 */
	public GuiFrame(SimulationConfiguration configuration) {
		super("Java Flight Simulator");
		this.configuration = configuration;
		
		setLayout(new BorderLayout());
		Dimension dims = new Dimension(200, 400);
		
		//---------------------------- Card Panel --------------------------------------------------
		
		cardLayout = new ResizingCardLayout();
		cardPanel = new JPanel();
		cardPanel.setLayout(cardLayout);
		cardPanel.setVisible(false);
		add(cardPanel, BorderLayout.EAST);
		
		//-------------------------- Aircraft Panel ------------------------------------------------
		
		aircraftPanel = new AircraftPanel(this);
		aircraftPanel.setAircraftConfigurationListener((String aircraftName) -> {
			buttonPanel.setAircraftLabel(aircraftName);
			
			configuration.setSelectedAircraft(aircraftName);
			configuration.save();

			setSize(dims);
			cardPanel.setVisible(false);
		});
		aircraftPanel.setWeightConfiguredListener((double fuelWeight, double payloadWeight) -> {
			if (aircraft != null) {
				aircraft.updateWeightPercentages(fuelWeight, payloadWeight);
				aircraft.save();					
			}
		});
		aircraftPanel.setCancelButtonListener(() -> {
			setSize(dims);
			cardPanel.setVisible(false);
		});
		aircraftPanel.setAircraftSelectedListener((String aircraftName) -> {
			aircraft = FileUtilities.readAircraftConfiguration(aircraftName);
			
			if (aircraftPanel != null && aircraftPanel.getWeightDialog() != null)
				aircraftPanel.getWeightDialog().refreshWeightOptions(aircraft);
		});
		cardPanel.add(aircraftPanel, "aircraft");
		
		//--------------------------- Options Panel ------------------------------------------------
		
		optionsPanel = new OptionsPanel();
		optionsPanel.setOptionsConfigurationListener((EnumSet<Options> options, int stepSize) -> {
			buttonPanel.setOptionsLabel(options, stepSize);
			configuration.setSimulationRateHz(stepSize);
			configuration.updateOptions(options);
			configuration.save();
			
			setSize(dims);
			cardPanel.setVisible(false);
		});
		optionsPanel.setCancelButtonListener(() -> {
			setSize(dims);
			cardPanel.setVisible(false);
		});
		cardPanel.add(optionsPanel, "options");
		
		//-------------------- Initial Conditions Panel --------------------------------------------
		
		initialConditionsPanel = new InitialConditionsPanel();
		initialConditionsPanel.setInitialConditionsPanel(configuration.getInitialConditions());
		initialConditionsPanel.setInitialConditionsConfigurationListener(
		(double[] coordinates, double heading, double altitude, double airspeed) -> {
			buttonPanel.setInitialConditionsLabel(coordinates, heading, altitude, airspeed);
			configuration.setInitialConditions(coordinates, heading, altitude, airspeed);
			configuration.save();
			
			setSize(dims);
			cardPanel.setVisible(false);
		});
		initialConditionsPanel.setCancelButtonListener(() -> {
			setSize(dims);
			cardPanel.setVisible(false);
		});
		cardPanel.add(initialConditionsPanel, "initialConditions");
		
		//-------------------------- Button Panel --------------------------------------------------
		
		buttonPanel = new ButtonPanel(configuration);
		buttonPanel.setAircraftButtonListener(() -> {
			setSize((dims.width+aircraftPanel.getPreferredSize().width), dims.height);
			cardPanel.setVisible(true);
			cardLayout.show(cardPanel, "aircraft");
		});
		buttonPanel.setInitialConditionsButtonListener(() -> {
			setSize((dims.width+initialConditionsPanel.getPreferredSize().width), dims.height);
			cardPanel.setVisible(true);
			cardLayout.show(cardPanel, "initialConditions");
		});
		buttonPanel.setOptionsButtonListener(() -> {
			setSize((dims.width+optionsPanel.getPreferredSize().width), dims.height);
			cardPanel.setVisible(true);
			cardLayout.show(cardPanel, "options");
		});
		buttonPanel.setStartSimulationButtonListener(() -> {
			setSize(dims);
			cardPanel.setVisible(false);
			GuiFrame.this.setVisible(configuration.getSimulationOptions().contains(Options.ANALYSIS_MODE));

			simulationEventListeners.forEach(listener -> listener.onStartSimulation());
		});
		add(buttonPanel, BorderLayout.CENTER);
		
		//============================ Miscellaneous ===============================================
		
		setOptionsAndText();
		
		//========================== Window Settings ===============================================
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int closeDialog = JOptionPane.showConfirmDialog(GuiFrame.this, "Are you sure you wish to quit?",
																"Confirm Exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (closeDialog == JOptionPane.YES_OPTION) {
					logger.info("Closing Java Flight Simulator");
					
					System.gc();
					System.exit(0);
				}
			}
		});

		setSize(dims);
		setResizable(false);
		
		setVisible(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}

	public void addSimulationEventListener(SimulationEventListener listener) {
		if (simulationEventListeners != null) {
			logger.info("Adding simulation event listener: " + listener.getClass());
			simulationEventListeners.add(listener);
		}
	}
	
	/**
	 * 	Sets all options and text on panels by rereading values saved in {@link SimulationConfiguration} as a json file 
	 */
	private void setOptionsAndText() {
		try {
			configuration = FileUtilities.readSimulationConfiguration();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Unable to read SimulationConfiguration.json!", 
					"Error Reading File", JOptionPane.ERROR_MESSAGE);
		}
		
		String aircraftName = configuration.getSelectedAircraft();
		
		buttonPanel.setOptionsLabel(configuration.getSimulationOptions(), configuration.getSimulationRateHz());
		buttonPanel.setAircraftLabel(aircraftName);
		
		aircraftPanel.setAircraftPanel(aircraftName);
		optionsPanel.setAllOptions(configuration);
	}
}
