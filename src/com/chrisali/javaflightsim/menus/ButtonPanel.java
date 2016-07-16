package com.chrisali.javaflightsim.menus;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumSet;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import com.chrisali.javaflightsim.simulation.setup.InitialConditions;
import com.chrisali.javaflightsim.simulation.setup.IntegrationSetup;
import com.chrisali.javaflightsim.simulation.setup.Options;
import com.chrisali.javaflightsim.utilities.FileUtilities;

public class ButtonPanel extends JPanel {

	private static final long serialVersionUID = 6491285782356385338L;
	
	private JButton aircraftButton;
	private JLabel aircraftLabel;
	private JButton initialConditionsButton;
	private JLabel initialConditonsLabel;
	private JButton optionsButton;
	private JLabel optionsLabel;
	private JButton runButton;
	
	private String htmlBodyOpen = "<html><body>";
	private String parOpen = "<p style='width: 150px;'>";
	private String parClose = "</p>";
	private String htmlBodyClose = "</body></html>";
	
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
		
		Font labelFont = new Font(Font.SANS_SERIF, Font.PLAIN, 9);
		
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
		
		aircraftLabel = new JLabel("<b>Select an aircraft</b>");
		aircraftLabel.setFont(labelFont);
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
		
		initialConditonsLabel = new JLabel("<b>Select starting conditions</b>");
		initialConditonsLabel.setFont(labelFont);
		setInitialConditionsLabel();
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
		
		//--------------------- Options Label -----------------------------------------
		gc.gridy++;
		
		optionsLabel = new JLabel("<b>Choose options</b>");
		optionsLabel.setFont(labelFont);
		add(optionsLabel, gc);
	
		// -------------------- Start Simulation Button ---------------------------------------
		gc.gridy++;
		gc.weighty = 1;
		gc.anchor = GridBagConstraints.SOUTH;
	
		runButton = new JButton("Start Simulation");
		runButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
		runButton.setToolTipText("Runs the simulation with the selected options");
		runButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (startSimulationButtonListener != null)
					startSimulationButtonListener.buttonEventOccurred();
			}
		});
		add(runButton, gc);
		
		//================================ Window Settings =========================================
		
		setSize(new Dimension(200, 400));
	}
	
	public void setAircraftLabel(String text) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(parOpen).append("<b>Selected Aircraft:</b> ").append(parClose)
		  .append(parOpen).append(text).append(parClose);
		
		aircraftLabel.setText(htmlBodyOpen + sb.toString() + htmlBodyClose);
	}
	
	public void setInitialConditionsLabel() {
		Map<InitialConditions,Double> initialConditions = IntegrationSetup.gatherInitialConditions("InitialConditions");
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(parOpen).append("Latitude: ").append(Math.toDegrees(initialConditions.get(InitialConditions.INITLAT))).append(" deg").append(parClose)
		  .append(parOpen).append("Longitude: ").append(Math.toDegrees(initialConditions.get(InitialConditions.INITLON))).append(" deg").append(parClose)
		  .append(parOpen).append("Heading: ").append(Math.toDegrees(initialConditions.get(InitialConditions.INITPSI))).append(" deg").append(parClose)
		  .append(parOpen).append("Altitude: ").append(initialConditions.get(InitialConditions.INITD)).append(" ft").append(parClose)
		  .append(parOpen).append("Airspeed: ").append(FileUtilities.toKnots(initialConditions.get(InitialConditions.INITU))).append(" kts").append(parClose);
		
		initialConditonsLabel.setText(htmlBodyOpen + "<b>Starting Condtions:</b> " + sb.toString() + htmlBodyClose);
	}
	
	public void setInitialConditionsLabel(double[] coordinates, double heading, double altitude, double airspeed) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(parOpen).append("Latitude: ").append(coordinates[0]).append(" deg").append(parClose)
		  .append(parOpen).append("Longitude: ").append(coordinates[1]).append(" deg").append(parClose)
		  .append(parOpen).append("Heading: ").append(heading).append(" deg").append(parClose)
		  .append(parOpen).append("Altitude: ").append(altitude).append(" ft").append(parClose)
		  .append(parOpen).append("Airspeed: ").append(airspeed).append(" kts").append(parClose);
		
		initialConditonsLabel.setText(htmlBodyOpen + "<b>Starting Condtions:</b> " + sb.toString() + htmlBodyClose);
	}
	
	public void setOptionsLabel(EnumSet<Options> options, int stepSize) {
		StringBuilder sb = new StringBuilder();
		
		for (Options option : options)
			sb.append(parOpen).append(option.toString()).append(parClose);
		sb.append(parOpen).append("Update Rate: ").append(stepSize).append(" Hz").append(parClose);
		
		optionsLabel.setText(htmlBodyOpen + "<b>Selected Options:</b>" + sb.toString() + htmlBodyClose);
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
