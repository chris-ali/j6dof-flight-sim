package com.chrisali.javaflightsim.menus;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.EnumMap;
import java.util.EnumSet;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.chrisali.javaflightsim.controllers.SimulationController;
import com.chrisali.javaflightsim.instrumentpanel.ClosePanelListener;
import com.chrisali.javaflightsim.instrumentpanel.InstrumentPanel;
import com.chrisali.javaflightsim.menus.aircraftpanel.AircraftConfigurationListener;
import com.chrisali.javaflightsim.menus.aircraftpanel.AircraftPanel;
import com.chrisali.javaflightsim.menus.aircraftpanel.WeightConfiguredListener;
import com.chrisali.javaflightsim.menus.initialconditionspanel.InitialConditionsConfigurationListener;
import com.chrisali.javaflightsim.menus.initialconditionspanel.InitialConditionsPanel;
import com.chrisali.javaflightsim.menus.optionspanel.DisplayOptions;
import com.chrisali.javaflightsim.menus.optionspanel.OptionsConfigurationListener;
import com.chrisali.javaflightsim.menus.optionspanel.OptionsPanel;
import com.chrisali.javaflightsim.simulation.setup.IntegratorConfig;
import com.chrisali.javaflightsim.simulation.setup.Options;
import com.chrisali.javaflightsim.utilities.Utilities;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = -1803264930661591606L;
	
	private SimulationController simulationController;
	
	private ButtonPanel buttonPanel;
	private AircraftPanel aircraftPanel;
	private OptionsPanel optionsPanel;
	private InitialConditionsPanel initialConditionsPanel;
	private InstrumentPanel instrumentPanel;
	private JPanel cardPanel; 
	private CardLayout cardLayout;
	
	public MainFrame(SimulationController simController) {
		super("Java Flight Sim");
		
		simulationController = simController;
		
		setLayout(new BorderLayout());
		Dimension dims = new Dimension(200, 400);
		
		//---------------------------- Card Panel --------------------------------------------------
		
		cardLayout = new ResizingCardLayout();
		cardPanel = new JPanel();
		cardPanel.setLayout(cardLayout);
		cardPanel.setVisible(false);
		add(cardPanel, BorderLayout.EAST);
		
		//------------------------- Instrument Panel -----------------------------------------------
		
		instrumentPanel = new InstrumentPanel();
		instrumentPanel.setClosePanelListener(new ClosePanelListener() {
			@Override
			public void panelWindowClosed() {
				simulationController.stopSimulation();
				instrumentPanel.setVisible(false);
				MainFrame.this.setVisible(true);
			}
		});
		
		//-------------------------- Aircraft Panel ------------------------------------------------
		
		aircraftPanel = new AircraftPanel(this);
		aircraftPanel.setAircraftConfigurationListener(new AircraftConfigurationListener() {
			@Override
			public void aircraftConfigured(String aircraftName) {
				buttonPanel.setAircraftLabel(aircraftName);
				simulationController.updateAircraft(aircraftName);
				
				setSize(dims);
				cardPanel.setVisible(false);
			}
		});
		aircraftPanel.setWeightConfiguredListener(new WeightConfiguredListener() {
			@Override
			public void weightConfigured(String aircraftName, double fuelWeight, double payloadWeight) {
				simulationController.updateMassProperties(aircraftName, fuelWeight, payloadWeight);
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
													EnumMap<DisplayOptions, Integer> displayOptions) {
				buttonPanel.setOptionsLabel(options, stepSize);
				simulationController.updateIntegratorConfig(stepSize);
				simulationController.updateOptions(options, displayOptions);
				
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
		initialConditionsPanel.setInitialConditionsConfigurationListener(new InitialConditionsConfigurationListener() {
			@Override
			public void initialConditonsConfigured(double[] coordinates, double heading, double altitude, double airspeed) {
				buttonPanel.setInitialConditionsLabel(coordinates, heading, altitude, airspeed);
				simulationController.updateInitialConditions(coordinates, heading, altitude, airspeed);
				
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
				
				simulationController.startSimulation(instrumentPanel);
				MainFrame.this.setVisible(simulationController.getSimulationOptions().contains(Options.ANALYSIS_MODE) ? true : false);
				instrumentPanel.setVisible(simulationController.getSimulationOptions().contains(Options.ANALYSIS_MODE) ? false : true);
			}
		});
		add(buttonPanel, BorderLayout.CENTER);
		
		//============================== Hot Keys ==================================================
		
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
			@Override
			public boolean dispatchKeyEvent(KeyEvent ev) {
				switch (ev.getKeyCode()) {
				
				case KeyEvent.VK_L:
					if (simulationController.getSimulation() != null 
							&& simulationController.getSimulation().isRunning() 
							&& !simulationController.isPlotWindowVisible())
						simulationController.plotSimulation();
					break;
					
				case KeyEvent.VK_Q:
					if (simulationController.getSimulation().isRunning()) {
						simulationController.stopSimulation();
						instrumentPanel.setVisible(false);
						MainFrame.this.setVisible(true);
					}
					break;
					
				default:
					break;
				}
				
				return false;
			}
		});
		
		//============================ Miscellaneous ===============================================
		
		setOptionsAndText();
		
		//========================== Window Settings ===============================================
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int closeDialog = JOptionPane.showConfirmDialog(MainFrame.this, "Are you sure you wish to quit?",
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
	
	private void setOptionsAndText() {
		try {
			simulationController.updateOptions(Utilities.parseSimulationSetup(), Utilities.parseDisplaySetup());
			simulationController.updateAircraft(Utilities.parseSimulationSetupForAircraft());
		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(this, "Unable to read SimulationSetup.txt!", 
					"Error Reading File", JOptionPane.ERROR_MESSAGE);
		}
		
		int stepSize = (int)(1/simulationController.getIntegratorConfig().get(IntegratorConfig.DT));
		String aircrafName = simulationController.getAircraftBuilder().getAircraft().getName();
		
		buttonPanel.setOptionsLabel(simulationController.getSimulationOptions(), stepSize);
		buttonPanel.setAircraftLabel(aircrafName);
		
		aircraftPanel.setAircraftPanel(aircrafName);
		optionsPanel.setAllOptions(simulationController.getSimulationOptions(), stepSize, 
									simulationController.getDisplayOptions());
	}
}
