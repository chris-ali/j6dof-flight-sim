/*******************************************************************************
 * Copyright (C) 2016-2017 Christopher Ali
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
import java.util.EnumMap;
import java.util.EnumSet;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.chrisali.javaflightsim.initializer.LWJGLSwingSimulationController;
import com.chrisali.javaflightsim.initializer.SimulationConfiguration;
import com.chrisali.javaflightsim.lwjgl.LWJGLWorld;
import com.chrisali.javaflightsim.lwjgl.renderengine.DisplayManager;
import com.chrisali.javaflightsim.simulation.datatransfer.FlightDataListener;
import com.chrisali.javaflightsim.simulation.setup.IntegratorConfig;
import com.chrisali.javaflightsim.simulation.setup.Options;
import com.chrisali.javaflightsim.simulation.utilities.FileUtilities;
import com.chrisali.javaflightsim.swing.aircraftpanel.AircraftConfigurationListener;
import com.chrisali.javaflightsim.swing.aircraftpanel.AircraftPanel;
import com.chrisali.javaflightsim.swing.aircraftpanel.WeightConfiguredListener;
import com.chrisali.javaflightsim.swing.initialconditionspanel.InitialConditionsConfigurationListener;
import com.chrisali.javaflightsim.swing.initialconditionspanel.InitialConditionsPanel;
import com.chrisali.javaflightsim.swing.instrumentpanel.ClosePanelListener;
import com.chrisali.javaflightsim.swing.instrumentpanel.InstrumentPanel;
import com.chrisali.javaflightsim.swing.optionspanel.AudioOptions;
import com.chrisali.javaflightsim.swing.optionspanel.DisplayOptions;
import com.chrisali.javaflightsim.swing.optionspanel.OptionsConfigurationListener;
import com.chrisali.javaflightsim.swing.optionspanel.OptionsPanel;

/**
 * Main Swing class that contains the main menus to configure the simulation and simulation window,
 * which renders the Out The Window display and instrument panel. 
 * 
 * @author Christopher Ali
 *
 */
public class GuiFrame extends JFrame {

	private static final long serialVersionUID = -1803264930661591606L;
	
	private LWJGLSwingSimulationController simulationController;
	private SimulationConfiguration configuration;
	
	private ButtonPanel buttonPanel;
	private AircraftPanel aircraftPanel;
	private OptionsPanel optionsPanel;
	private InitialConditionsPanel initialConditionsPanel;
	private SimulationWindow simulationWindow;
	private JPanel cardPanel; 
	private CardLayout cardLayout;
	
	/**
	 * Constructor, which takes a {@link LWJGLSwingSimulationController} reference to gain access to methods to
	 * configure the simulation
	 * 
	 * @param controller
	 */
	public GuiFrame(LWJGLSwingSimulationController controller) {
		super("Java Flight Sim");
		
		simulationController = controller;
		configuration = controller.getConfiguration();
		
		setLayout(new BorderLayout());
		Dimension dims = new Dimension(200, 400);
		
		//---------------------------- Card Panel --------------------------------------------------
		
		cardLayout = new ResizingCardLayout();
		cardPanel = new JPanel();
		cardPanel.setLayout(cardLayout);
		cardPanel.setVisible(false);
		add(cardPanel, BorderLayout.EAST);
		
		//------------------------- Simulation Window ----------------------------------------------
		
		initSimulationWindow();
		
		//-------------------------- Aircraft Panel ------------------------------------------------
		
		aircraftPanel = new AircraftPanel(this);
		aircraftPanel.setAircraftConfigurationListener(new AircraftConfigurationListener() {
			@Override
			public void aircraftConfigured(String aircraftName) {
				buttonPanel.setAircraftLabel(aircraftName);
				configuration.setAircraftBuilder(aircraftName);
				
				setSize(dims);
				cardPanel.setVisible(false);
			}
		});
		aircraftPanel.setWeightConfiguredListener(new WeightConfiguredListener() {
			@Override
			public void weightConfigured(String aircraftName, double fuelWeight, double payloadWeight) {
				configuration.setMassProperties(aircraftName, fuelWeight, payloadWeight);
			}
		});
		aircraftPanel.setCancelButtonListener(new CancelButtonListener() {
			@Override
			public void cancelButtonClicked() {
				setSize(dims);
				cardPanel.setVisible(false);
			}
		});
		cardPanel.add(aircraftPanel, "aircraft");
		
		//--------------------------- Options Panel ------------------------------------------------
		
		optionsPanel = new OptionsPanel();
		optionsPanel.setOptionsConfigurationListener(new OptionsConfigurationListener() {
			@Override
			public void simulationOptionsConfigured(EnumSet<Options> options, int stepSize, 
													EnumMap<DisplayOptions, Integer> displayOptions,
													EnumMap<AudioOptions, Float> audioOptions) {
				buttonPanel.setOptionsLabel(options, stepSize);
				configuration.setIntegratorConfig(stepSize);
				configuration.updateOptions(options, displayOptions, audioOptions);
				
				setSize(dims);
				cardPanel.setVisible(false);
			}
		});
		optionsPanel.setCancelButtonListener(new CancelButtonListener() {
			@Override
			public void cancelButtonClicked() {
				setSize(dims);
				cardPanel.setVisible(false);
			}
		});
		cardPanel.add(optionsPanel, "options");
		
		//-------------------- Initial Conditions Panel --------------------------------------------
		
		initialConditionsPanel = new InitialConditionsPanel();
		initialConditionsPanel.setInitialConditionsPanel(configuration.getInitialConditions());
		initialConditionsPanel.setInitialConditionsConfigurationListener(new InitialConditionsConfigurationListener() {
			@Override
			public void initialConditonsConfigured(double[] coordinates, double heading, double altitude, double airspeed) {
				buttonPanel.setInitialConditionsLabel(coordinates, heading, altitude, airspeed);
				configuration.setInitialConditions(coordinates, heading, altitude, airspeed);
				
				setSize(dims);
				cardPanel.setVisible(false);
			}
		});
		initialConditionsPanel.setCancelButtonListener(new CancelButtonListener() {
			@Override
			public void cancelButtonClicked() {
				setSize(dims);
				cardPanel.setVisible(false);
			}
		});
		cardPanel.add(initialConditionsPanel, "initialConditions");
		
		//-------------------------- Button Panel --------------------------------------------------
		
		buttonPanel = new ButtonPanel();
		buttonPanel.setAircraftButtonListener(new AircraftButtonListener() {
			@Override
			public void buttonEventOccurred() {
				setSize((dims.width+aircraftPanel.getPreferredSize().width), dims.height);
				cardPanel.setVisible(true);
				cardLayout.show(cardPanel, "aircraft");
			}
		});
		buttonPanel.setInitialConditionsButtonListener(new InitialConditionsButtonListener() {
			@Override
			public void buttonEventOccurred() {
				setSize((dims.width+initialConditionsPanel.getPreferredSize().width), dims.height);
				cardPanel.setVisible(true);
				cardLayout.show(cardPanel, "initialConditions");
			}
		});
		buttonPanel.setOptionsButtonListener(new OptionsButtonListener() {
			@Override
			public void buttonEventOccurred() {
				setSize((dims.width+optionsPanel.getPreferredSize().width), dims.height);
				cardPanel.setVisible(true);
				cardLayout.show(cardPanel, "options");
			}
		});
		buttonPanel.setStartSimulationButtonListener(new StartSimulationButtonListener() {
			@Override
			public void buttonEventOccurred() {
				setSize(dims);
				cardPanel.setVisible(false);
				
				simulationController.startSimulation();
				GuiFrame.this.setVisible(configuration.getSimulationOptions().contains(Options.ANALYSIS_MODE) ? true : false);
				simulationWindow.setVisible(configuration.getSimulationOptions().contains(Options.ANALYSIS_MODE) ? false : true);
			}
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
	
	/**
	 * 	Sets all options and text on panels by calling methods in {@link LWJGLSwingSimulationController} to
	 *  parse setup files and get EnumMap values
	 */
	private void setOptionsAndText() {
		try {
			configuration.updateOptions(FileUtilities.parseSimulationSetup(), 
									    FileUtilities.parseDisplaySetup(),
									    FileUtilities.parseAudioSetup());
			configuration.setAircraftBuilder(FileUtilities.parseSimulationSetupForAircraft());
		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(this, "Unable to read SimulationSetup.txt!", 
					"Error Reading File", JOptionPane.ERROR_MESSAGE);
		}
		
		int stepSize = (int)(1/configuration.getIntegratorConfig().get(IntegratorConfig.DT));
		String aircraftName = configuration.getAircraftBuilder().getAircraft().getName();
		
		buttonPanel.setOptionsLabel(configuration.getSimulationOptions(), stepSize);
		buttonPanel.setAircraftLabel(aircraftName);
		
		aircraftPanel.setAircraftPanel(aircraftName);
		optionsPanel.setAllOptions(configuration.getSimulationOptions(), stepSize, 
				configuration.getDisplayOptions(),
				configuration.getAudioOptions());
	}
	
	//=============================== Simulation Window ==============================================
	
	/**
	 * (Re)initializes simulationWindow object so that instrument panel and OTW view are scaled correctly depending
	 * on if the instrument panel is shown or not
	 */
	public void initSimulationWindow() {
		simulationWindow = new SimulationWindow(simulationController);
		simulationWindow.setClosePanelListener(new ClosePanelListener() {
			@Override
			public void panelWindowClosed() {
				simulationController.stopSimulation();
				simulationWindow.setVisible(false);
				GuiFrame.this.setVisible(true);
			}
		});
	}
	
	/**
	 * @return {@link SimulationWindow} object for {@link LWJGLWorld} to set its display parent
	 * within {@link DisplayManager}
	 */
	public SimulationWindow getSimulationWindow() {
		return simulationWindow;
	}
	
	/**
	 * @return {@link InstrumentPanel} object for {@link LWJGLSwingSimulationController} to set a
	 * {@link FlightDataListener} to when {@link LWJGLSwingSimulationController#startSimulation()} is called
	 */
	public InstrumentPanel getInstrumentPanel() {
		return simulationWindow.getInstrumentPanel();
	}
}
