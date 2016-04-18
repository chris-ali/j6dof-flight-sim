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

import com.chrisali.javaflightsim.instrumentpanel.ClosePanelListener;
import com.chrisali.javaflightsim.instrumentpanel.InstrumentPanel;
import com.chrisali.javaflightsim.menus.aircraftpanel.AircraftConfigurationListener;
import com.chrisali.javaflightsim.menus.aircraftpanel.AircraftPanel;
import com.chrisali.javaflightsim.menus.aircraftpanel.WeightConfiguredListener;
import com.chrisali.javaflightsim.menus.initialconditionspanel.InitialConditionsConfigurationListener;
import com.chrisali.javaflightsim.menus.initialconditionspanel.InitialConditionsPanel;
import com.chrisali.javaflightsim.menus.optionspanel.DisplayOptions;
import com.chrisali.javaflightsim.menus.optionspanel.OptionsConfigurationListener;
import com.chrisali.javaflightsim.menus.optionspanel.SimulationOptionsPanel;
import com.chrisali.javaflightsim.simulation.setup.IntegratorConfig;
import com.chrisali.javaflightsim.simulation.setup.Options;
import com.chrisali.javaflightsim.utilities.Utilities;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = -1803264930661591606L;
	
	private Controller simController;
	private ButtonPanel buttonPanel;
	private AircraftPanel aircraftPanel;
	private SimulationOptionsPanel optionsPanel;
	private InitialConditionsPanel initialConditionsPanel;
	private InstrumentPanel instrumentPanel;
	private JPanel cardPanel; 
	private CardLayout cardLayout;
	
	public MainFrame(Controller controller) {
		super("Java Flight Sim");
		
		simController = controller;
		
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
				simController.stopSimulation();
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
				simController.updateAircraft(aircraftName);
				
				setSize(dims);
				cardPanel.setVisible(false);
			}
		});
		aircraftPanel.setWeightConfiguredListener(new WeightConfiguredListener() {
			@Override
			public void weightConfigured(String aircraftName, double fuelWeight, double payloadWeight) {
				simController.updateMassProperties(aircraftName, fuelWeight, payloadWeight);
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
		
		optionsPanel = new SimulationOptionsPanel();
		optionsPanel.setOptionsConfigurationListener(new OptionsConfigurationListener() {
			@Override
			public void simulationOptionsConfigured(EnumSet<Options> options, int stepSize) {
				buttonPanel.setOptionsLabel(options, stepSize);
				simController.updateIntegratorConfig(stepSize);
				simController.updateOptions(options);
				
				setSize(dims);
				cardPanel.setVisible(false);
			}

			@Override
			public void displayOptionsConfigured(EnumMap<DisplayOptions, Integer> displayOptions) {
				
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
				simController.updateInitialConditions(coordinates, heading, altitude, airspeed);
				
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
				
				simController.startSimulation(instrumentPanel);
				MainFrame.this.setVisible(simController.getOptions().contains(Options.ANALYSIS_MODE) ? true : false);
				instrumentPanel.setVisible(simController.getOptions().contains(Options.ANALYSIS_MODE) ? false : true);
			}
		});
		add(buttonPanel, BorderLayout.CENTER);
		
		//============================== Hot Keys ==================================================
		
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
			@Override
			public boolean dispatchKeyEvent(KeyEvent ev) {
				switch (ev.getKeyCode()) {
				
				case KeyEvent.VK_L:
					if (simController.getSimulation() != null 
							&& simController.getSimulation().isRunning() 
							&& !simController.isPlotWindowVisible())
						simController.plotSimulation();
					break;
					
				case KeyEvent.VK_Q:
					if (simController.getSimulation().isRunning()) {
						simController.stopSimulation();
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
			simController.updateOptions(Utilities.parseSimSetupOptions());
			simController.updateAircraft(Utilities.parseSimSetupSelectedAircraft());
		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(this, "Unable to read SimulationSetup.txt!", 
					"Error Reading File", JOptionPane.ERROR_MESSAGE);
		}
		
		int stepSize = (int)(1/simController.getIntegratorConfig().get(IntegratorConfig.DT));
		String aircrafName = simController.getAircraftBuilder().getAircraft().getName();
		
		buttonPanel.setOptionsLabel(simController.getOptions(), stepSize);
		buttonPanel.setAircraftLabel(aircrafName);
		
		aircraftPanel.setAircraftPanel(aircrafName);
		optionsPanel.setOptionsPanel(simController.getOptions(), stepSize);
	}
}
