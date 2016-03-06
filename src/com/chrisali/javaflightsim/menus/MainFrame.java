package com.chrisali.javaflightsim.menus;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.EnumSet;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.chrisali.javaflightsim.simulation.setup.Options;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = -1803264930661591606L;
	
	private Controller simController;
	private ButtonPanel buttonPanel;
	private AircraftPanel aircraftPanel;
	private OptionsPanel optionsPanel;

	public MainFrame(Controller controller) {
		super("Java Flight Sim");
		
		simController = controller;
		
		setLayout(new BorderLayout());
		
		//-------------------------- Aircraft Panel ------------------------------------------------
		
		aircraftPanel = new AircraftPanel(this);
		aircraftPanel.setAircraftConfigurationListener(new AircraftConfigurationListener() {
			@Override
			public void aircraftConfigured(String aircraftName) {
				buttonPanel.setAircraftLabel(aircraftName);
			}
		});
		
		//-------------------------- Aircraft Panel ------------------------------------------------
		
		optionsPanel = new OptionsPanel(this);
		optionsPanel.setOptionsConfigurationListener(new OptionsConfigurationListener() {
			@Override
			public void optionsConfigured(EnumSet<Options> options, int stepSize) {
				buttonPanel.setOptionsLabel(options);
				simController.updateIntegratorConfig(stepSize);
				simController.updateOptions(options);
			}
		});
		
		//-------------------------- Button Panel --------------------------------------------------
		
		buttonPanel = new ButtonPanel();
		buttonPanel.setAircraftButtonListener(new AircraftButtonListener() {
			@Override
			public void buttonEventOccurred() {
				aircraftPanel.setVisible(true);
			}
		});
		buttonPanel.setInitialConditionsButtonListener(new InitialConditionsButtonListener() {
			@Override
			public void buttonEventOccurred() {
				System.out.println("Open initial conditions window");
			}
		});
		buttonPanel.setOptionsButtonListener(new OptionsButtonListener() {
			@Override
			public void buttonEventOccurred() {
				optionsPanel.setVisible(true);
			}
		});
		buttonPanel.setStartSimulationButtonListener(new StartSimulationButtonListener() {
			@Override
			public void buttonEventOccurred() {
				System.out.println("Start simulation");
			}
		});
		add(buttonPanel, BorderLayout.CENTER);
		
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

		Dimension dims = new Dimension(200, 400);
		setSize(dims);
		setMinimumSize(dims);
		setMaximumSize(dims);
		
		setVisible(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}

}
