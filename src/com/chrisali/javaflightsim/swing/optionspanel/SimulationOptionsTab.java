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
package com.chrisali.javaflightsim.swing.optionspanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumSet;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
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
	private JSpinner stepSizeSpinner;
	private StepSizeValueChangedListener stepSizeValueChangedListener;
	private JCheckBox debugMode;

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
				} else {
					simulationOptions.remove(Options.ANALYSIS_MODE);
					simulationOptions.add(Options.UNLIMITED_FLIGHT);
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

		//---------- Debug Mode Checkbox ------------------- 
		gc.gridy++;
		
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.EAST;
		controlsPanel.add(new JLabel("Debug Mode:"), gc);
		
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.WEST;
		debugMode = new JCheckBox("Enabled");
		debugMode.setToolTipText("Generates extra logging and telemetry for debug purposes");
		debugMode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(((JCheckBox)e.getSource()).isSelected())
					simulationOptions.add(Options.DEBUG_MODE);
				else
					simulationOptions.remove(Options.DEBUG_MODE);
			}
		});
		controlsPanel.add(debugMode, gc);
		
		//========================== Window Settings ===============================================
		
		Dimension dims = new Dimension(400, 400);
		setSize(dims);
		setPreferredSize(dims);
	}
	
	/**
	 * Reads options EnumSet and step size integer value to determine how to set {@link SimulationOptionsTab} panel objects
	 * 
	 * @param options
	 * @param stepSize
	 */
	public void setOptionsTab(EnumSet<Options> options, int stepSize) {
		this.simulationOptions = options;
		
		analysisMode.setSelected(simulationOptions.contains(Options.ANALYSIS_MODE));
		consoleDisplay.setSelected(simulationOptions.contains(Options.CONSOLE_DISPLAY));
		stepSizeSpinner.setValue(stepSize);
		debugMode.setSelected(simulationOptions.contains(Options.DEBUG_MODE));
	}
	
	protected EnumSet<Options> getSimulationOptions() {
		return simulationOptions;
	}

	public void setStepSizeValueChangedListener(StepSizeValueChangedListener stepSizeValueChangedListener) {
		this.stepSizeValueChangedListener = stepSizeValueChangedListener;
	}
}
