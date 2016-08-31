package com.chrisali.javaflightsim.menus.optionspanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EnumSet;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.chrisali.javaflightsim.simulation.setup.Options;

public class SimulationOptionsTab extends JPanel {

	private static final long serialVersionUID = -2865224216075732617L;
	
	private JLabel headerLabel;
	private JCheckBox analysisMode;
	private JCheckBox consoleDisplay;
	private JCheckBox showInstrumentPanel;
	private JList<String> controllers;
	private JSpinner stepSizeSpinner;
	private StepSizeValueChangedListener stepSizeValueChangedListener;

	private EnumSet<Options> simulationOptions;
	
	public SimulationOptionsTab() {
		
		//-------------------- Panels ---------------------------
		
		JPanel headerPanel = new JPanel();
		JPanel controlsPanel = new JPanel();
		
		headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
		controlsPanel.setLayout(new GridBagLayout());
		
		setLayout(new BorderLayout());
		add(headerPanel, BorderLayout.NORTH);
		add(controlsPanel, BorderLayout.CENTER);
		
		//------------------ Borders and Insets -----------------
		
		int margins = 5;
		Border emptyBorder = BorderFactory.createEmptyBorder(margins ,margins, margins, margins);
		Border etchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
		
		controlsPanel.setBorder(BorderFactory.createCompoundBorder(emptyBorder, etchedBorder));
		
		Insets spacer = new Insets(margins, margins, margins, margins);
		
		//------------------- Header ----------------------------
		
		headerLabel = new JLabel("Options");
		headerLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
		headerPanel.add(headerLabel);
		
		//-------------- GridBag Items -------------------------- 
		
		GridBagConstraints gc = new GridBagConstraints();
		
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.CENTER;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.gridy = 0;
		gc.insets = spacer;
		
		//----------- Analysis Mode Checkbox --------------------- 
		gc.gridy++;
		
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.EAST;
		controlsPanel.add(new JLabel("Analysis Mode:"), gc);
		
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.WEST;
		analysisMode = new JCheckBox("Run Simulation Analysis");
		analysisMode.setToolTipText("Disables control of the simulation and runs a test analysis of the aircraft's\n " +
									"dynamic stability. At the end of the analysis, the results will be plotted in pop-up windows.");
		analysisMode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(((JCheckBox)e.getSource()).isSelected()) {
					simulationOptions.removeIf(p -> (p != Options.CONSOLE_DISPLAY));
					simulationOptions.add(Options.ANALYSIS_MODE);
					controllers.setEnabled(false);
				} else {
					simulationOptions.remove(Options.ANALYSIS_MODE);
					simulationOptions.add(Options.UNLIMITED_FLIGHT);
					controllers.setEnabled(true);
					setDesiredController(controllers.getSelectedValue()); // adds previously removed value back to options map 
				}
			}
		});
		controlsPanel.add(analysisMode, gc);
		
		//---------- Console Display Checkbox ------------------- 
		gc.gridy++;
		
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.EAST;
		controlsPanel.add(new JLabel("Console Display:"), gc);
		
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.WEST;
		consoleDisplay = new JCheckBox("Show Raw Data Output");
		consoleDisplay.setToolTipText("Displays a table showing raw data output of the simulation");
		consoleDisplay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(((JCheckBox)e.getSource()).isSelected())
					simulationOptions.add(Options.CONSOLE_DISPLAY);
				else
					simulationOptions.remove(Options.CONSOLE_DISPLAY);
			}
		});
		controlsPanel.add(consoleDisplay, gc);
		
		//-------------- Controllers List  ------------------------ 
		gc.gridy++;
		
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.EAST;
		controlsPanel.add(new JLabel("Selected Controller:"), gc);
		
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.WEST;
		DefaultListModel<String> controllerList = new DefaultListModel<>();
		controllerList.addElement("Joystick");
		controllerList.addElement("Mouse");
		controllerList.addElement("CH Controls");
		controllerList.addElement("Keyboard Only");
		controllers = new JList<String>(controllerList);
		controllers.setToolTipText("Chooses which HID controller will control the simulation");
		controllers.setSelectedIndex(0);
		controllers.setEnabled(!analysisMode.isSelected());
		controllers.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		controllers.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				setDesiredController(controllers.getSelectedValue());
			}
		});
		controlsPanel.add(controllers, gc);
		
		//------------ Use Instrument Panel ----------------------
		gc.gridy++;
		
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.EAST;
		controlsPanel.add(new JLabel("Instrument Panel:"), gc);
		
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.WEST;
		showInstrumentPanel = new JCheckBox("Show Panel");
		showInstrumentPanel.setToolTipText("Chooses whether a Swing instrument panel with gauges will display when the simulation runs");
		showInstrumentPanel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (((JCheckBox) e.getSource()).isSelected())
					simulationOptions.add(Options.INSTRUMENT_PANEL);
				else 
					simulationOptions.remove(Options.INSTRUMENT_PANEL);
			}
		});
		
		controlsPanel.add(showInstrumentPanel, gc);
		
		//--------- Simulation Step Size Spinner ----------------- 
		gc.gridy++;
		
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.EAST;
		controlsPanel.add(new JLabel("Simulation Refresh Rate (Hz):"), gc);
		
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.WEST;
		stepSizeSpinner = new JSpinner(new SpinnerNumberModel(20,10,500,10));
		stepSizeSpinner.setToolTipText("Sets the simulation resolution, dictating how many times per second the simulation updates");
		stepSizeSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (stepSizeValueChangedListener != null)
					stepSizeValueChangedListener.valueChanged((int) stepSizeSpinner.getValue());
			}
		});
		controlsPanel.add(stepSizeSpinner, gc);

		
		//========================== Window Settings ===============================================
		
		Dimension dims = new Dimension(400, 400);
		setSize(dims);
		setPreferredSize(dims);
	}
	
	/**
	 * Adds desired HID controller to simulationOptions EnumMap depending on string passed in; removes all other
	 * HID controller values before adding new value, unless "Keyboard Only" is selected, in which case no option
	 * is added
	 * 
	 * @param selectedValue
	 */
	private void setDesiredController(String selectedValue) {
		switch (selectedValue) {
		case ("Joystick"):
			simulationOptions.removeIf(p -> (p == Options.USE_MOUSE || p == Options.USE_CH_CONTROLS || p == Options.USE_KEYBOARD_ONLY));
			simulationOptions.add(Options.USE_JOYSTICK);
			break;
		case ("Mouse"):
			simulationOptions.removeIf(p -> (p == Options.USE_JOYSTICK || p == Options.USE_CH_CONTROLS || p == Options.USE_KEYBOARD_ONLY));
			simulationOptions.add(Options.USE_MOUSE);
			break;
		case ("CH Controls"):
			simulationOptions.removeIf(p -> (p == Options.USE_MOUSE || p == Options.USE_JOYSTICK || p == Options.USE_KEYBOARD_ONLY));
			simulationOptions.add(Options.USE_CH_CONTROLS);
			break;
		case ("Keyboard Only"):
			simulationOptions.removeIf(p -> (p == Options.USE_MOUSE || p == Options.USE_JOYSTICK || p == Options.USE_CH_CONTROLS));
			simulationOptions.add(Options.USE_KEYBOARD_ONLY);
			break;
		default:
			break;
		}
	}
	
	/**
	 * Reads options EnumSet and step size integer value to determine how to set {@link SimulationOptionsTab} panel objects
	 * 
	 * @param options
	 * @param stepSize
	 */
	public void setOptionsTab(EnumSet<Options> options, int stepSize) {
		this.simulationOptions = options;
		
		analysisMode.setSelected(simulationOptions.contains(Options.ANALYSIS_MODE) ? true : false);
		consoleDisplay.setSelected(simulationOptions.contains(Options.CONSOLE_DISPLAY) ? true : false);
		showInstrumentPanel.setSelected(simulationOptions.contains(Options.INSTRUMENT_PANEL) ? true : false);
		
		if (simulationOptions.contains(Options.USE_KEYBOARD_ONLY))
			controllers.setSelectedIndex(3);
		else if (simulationOptions.contains(Options.USE_CH_CONTROLS))
			controllers.setSelectedIndex(2);
		else if (simulationOptions.contains(Options.USE_MOUSE))
			controllers.setSelectedIndex(1);
		else
			controllers.setSelectedIndex(0);
		
		stepSizeSpinner.setValue(stepSize);
	}
	
	protected EnumSet<Options> getSimulationOptions() {
		return simulationOptions;
	}

	public void setStepSizeValueChangedListener(StepSizeValueChangedListener stepSizeValueChangedListener) {
		this.stepSizeValueChangedListener = stepSizeValueChangedListener;
	}
}
