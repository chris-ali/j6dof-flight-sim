/*******************************************************************************
 * Copyright (C) 2016-2018 Christopher Ali
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.chrisali.javaflightsim.simulation.setup.Options;
import com.chrisali.javaflightsim.simulation.setup.SimulationConfiguration;
import com.chrisali.javaflightsim.swing.CancelButtonListener;

public class OptionsPanel extends JPanel {

	private static final long serialVersionUID = -8867495044721208756L;
	
	private SimulationOptionsTab simulationOptionsTab;
	private DisplayOptionsTab displayOptionsTab;
	private AudioOptionsTab audioOptionsTab;
	private CameraOptionsTab cameraOptionsTab;
	private JButton okButton;
	private JButton cancelButton;
	
	private OptionsConfigurationListener optionsConfigurationListener;
	private CancelButtonListener cancelButtonListener;
	
	private int stepSize;
	
	public OptionsPanel() {
		
		//-------------------- Panels ---------------------------
		
		JTabbedPane optionsTabs = new JTabbedPane();
		JPanel buttonPanel = new JPanel();
		
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		setLayout(new BorderLayout());
		add(optionsTabs, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		
		//------------- Simulation Options Tab -------------------
		
		simulationOptionsTab = new SimulationOptionsTab();
		simulationOptionsTab.setStepSizeValueChangedListener(new StepSizeValueChangedListener() {
			@Override
			public void valueChanged(int newStepValue) {
				stepSize = newStepValue;
			}
		});
		optionsTabs.add(simulationOptionsTab, "Simulation");
		
		//------------- Display Options Tab -------------------
		
		displayOptionsTab = new DisplayOptionsTab();
		optionsTabs.add(displayOptionsTab, "Display");
		
		//-------------- Audio Options Tab ---------------------
		
		audioOptionsTab = new AudioOptionsTab();
		optionsTabs.add(audioOptionsTab, "Audio");
		
		//-------------- Camera Options Tab ---------------------
		
		cameraOptionsTab = new CameraOptionsTab();
		optionsTabs.add(cameraOptionsTab, "Camera");
		
		//----------------- OK Button ----------------------------
		
		okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!simulationOptionsTab.getSimulationOptions().contains(Options.ANALYSIS_MODE))
					simulationOptionsTab.getSimulationOptions().add(Options.UNLIMITED_FLIGHT);
				
				if (optionsConfigurationListener != null) {
					optionsConfigurationListener.simulationOptionsConfigured(simulationOptionsTab.getSimulationOptions(), stepSize,
																			displayOptionsTab.getDisplayOptions(),
																			audioOptionsTab.getAudioOptions());
				}
			}
		});
		buttonPanel.add(okButton);
		
		//------------------- Cancel Button ------------------------
		
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (cancelButtonListener != null)
					cancelButtonListener.cancelButtonClicked();
			}
		});
		buttonPanel.add(cancelButton);
		okButton.setPreferredSize(cancelButton.getPreferredSize());
		
		//========================== Window Settings ===============================================
		
		Dimension dims = new Dimension(400, 400);
		setSize(dims);
		setPreferredSize(dims);
	}
	
	public void setAllOptions(SimulationConfiguration config) {
		stepSize = config.getSimulationRateHz();
		simulationOptionsTab.setOptionsTab(config.getSimulationOptions(), stepSize);
		displayOptionsTab.setOptionsTab(config.getDisplayOptions());
		audioOptionsTab.setOptionsTab(config.getAudioOptions());
		cameraOptionsTab.setOptionsTab(config.getCameraConfiguration());
	}
	
	public void setCancelButtonListener(CancelButtonListener cancelButtonListener) {
		this.cancelButtonListener = cancelButtonListener;
	}
	
	public void setOptionsConfigurationListener(OptionsConfigurationListener optionsConfigurationListener) {
		this.optionsConfigurationListener = optionsConfigurationListener;
	}
}
