package com.chrisali.javaflightsim.menus;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

public class ButtonPanel extends JPanel {

	private static final long serialVersionUID = 6491285782356385338L;
	
	private JButton aircraftButton;
	private JLabel aircraftLabel;
	private JButton initialConditionsButton;
	private JLabel initialConditonsLabel;
	private JButton optionsButton;
	private JLabel optionsLabel;
	private JButton runButton;
	
	private String htmlWidthOpen = "<html><body><p style='width: 150px;'>";
	private String htmlWidthClose = "</p></body></html>";
	
	private AircraftButtonListener aircraftButtonListener;
	private InitialConditionsButtonListener initialConditionsButtonListener;
	private OptionsButtonListener optionsButtonListener;
	private StartSimulationButtonListener startSimulationButtonListener;
	
	public ButtonPanel() {
		
		//-------------------------- Borders and Insets  --------------------------------------
		
		int margins = 7;
		
		Border outerBorder = BorderFactory.createEmptyBorder(margins, margins, margins, margins);
		Border innerBorder = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
		
		setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
		
		Insets insets = new Insets(margins, margins, margins, margins);
		
		//------------------------- Layout Setup -----------------------------------------------
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints gc = new GridBagConstraints();
		
		gc.weightx = 1;
		gc.weighty = 1;
		gc.gridx = 0;
		gc.gridy = 0;
		gc.anchor = GridBagConstraints.CENTER;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.insets = insets;
		
		//------------------------- Aircraft Button --------------------------------------------
		gc.gridy++;
		gc.weighty = 0.125;
		
		aircraftButton = new JButton("Aircraft");
		aircraftButton.setToolTipText("Select and configure an aircraft");
		aircraftButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (aircraftButtonListener != null)
					aircraftButtonListener.buttonEventOccurred();
			}
		});
		add(aircraftButton, gc);
		
		//------------------------- Aircraft Label ---------------------------------------------
		gc.gridy++;
		
		aircraftLabel = new JLabel("Select an aircraft");
		add(aircraftLabel, gc);
		
		//-------------------- Initial Conditions Button ---------------------------------------
		gc.gridy++;
		
		initialConditionsButton = new JButton("Initial Conditions");
		initialConditionsButton.setToolTipText("Set up starting conditions for flight");
		initialConditionsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (initialConditionsButtonListener != null)
					initialConditionsButtonListener.buttonEventOccurred();
			}
		});
		add(initialConditionsButton, gc);
		
		//--------------------- Initial Conditions Label -----------------------------------------
		gc.gridy++;
		
		initialConditonsLabel = new JLabel("Select starting conditions");
		add(initialConditonsLabel, gc);
		
		//-------------------------- Options Button ----------------------------------------------
		gc.gridy++;
	
		optionsButton = new JButton("Options");
		optionsButton.setToolTipText("Configure simulation options");
		optionsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (optionsButtonListener != null)
					optionsButtonListener.buttonEventOccurred();
			}
		});
		add(optionsButton, gc);
		
		//--------------------- Initial Conditions Label -----------------------------------------
		gc.gridy++;
		
		optionsLabel = new JLabel("Choose options");
		add(optionsLabel, gc);
	
		// -------------------- Initial Conditions Button ---------------------------------------
		gc.gridy++;
		gc.weighty = 1;
		gc.anchor = GridBagConstraints.SOUTH;
	
		runButton = new JButton("Start Simulation");
		runButton.setToolTipText("Runs the simulation with the selected options");
		runButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (startSimulationButtonListener != null)
					startSimulationButtonListener.buttonEventOccurred();
			}
		});
		add(runButton, gc);
	}
	
	public void setAircraftLabel(String text) {
		aircraftLabel.setText("Selected Aircraft: " + text);
	}
	
	public void setInitialConditionsLabel(String[] text) {
		StringBuilder sb = new StringBuilder();
		
		for (String string : text)
			sb.append(string).append("\n");
		
		initialConditonsLabel.setText(htmlWidthOpen + "Starting Condtions: " + sb.toString() + htmlWidthClose);
	}
	
	public void setOptionsLabel(String[] text) {
		StringBuilder sb = new StringBuilder();
		
		for (String string : text)
			sb.append(string).append("\n");
		
		optionsLabel.setText("Selected Options:\n" + sb.toString());
	}
	
	public void setAircraftButtonListener(AircraftButtonListener aircraftButtonListener) {
		this.aircraftButtonListener = aircraftButtonListener;
	}
	
	public void setInitialConditionsButtonListener(InitialConditionsButtonListener initialConditionsButtonListener) {
		this.initialConditionsButtonListener = initialConditionsButtonListener;	
	}
	
	public void setOptionsButtonListener(OptionsButtonListener optionsButtonListener) {
		this.optionsButtonListener = optionsButtonListener;
	}
	public void setStartSimulationButtonListener(StartSimulationButtonListener startSimulationButtonListener) {
		this.startSimulationButtonListener = startSimulationButtonListener;
	}
}
