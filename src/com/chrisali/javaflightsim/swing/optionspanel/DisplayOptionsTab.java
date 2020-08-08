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

import com.chrisali.javaflightsim.simulation.setup.DisplayConfiguration;

public class DisplayOptionsTab extends JPanel {

	private static final long serialVersionUID = -2865224216075732617L;
	
	private JLabel headerLabel;
	private JCheckBox antiAliasingCheckbox;
	private JSpinner anisotropicFilteringSpinner;
	private JSpinner displayWidthSpinner;
	private JSpinner displayHeightSpinner;
	
	private DisplayConfiguration displayConfiguration;
	
	public DisplayOptionsTab() {
		
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
		
		//----------- Anti Aliasing Checkbox --------------------- 
		gc.gridy++;
		
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.EAST;
		controlsPanel.add(new JLabel("Anti Aliasing:"), gc);
		
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.WEST;
		antiAliasingCheckbox = new JCheckBox("Use Anti Aliasing");
		antiAliasingCheckbox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				displayConfiguration.setUseAntiAliasing(((JCheckBox)e.getSource()).isSelected());
			}
		});
		controlsPanel.add(antiAliasingCheckbox, gc);
		
		//------------- Anisotropic Filtering Spinner -------------------- 
		gc.gridy++;
		
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.EAST;
		controlsPanel.add(new JLabel("Anisotropic Filtering:"), gc);
		
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.WEST;
		anisotropicFilteringSpinner = new JSpinner(new SpinnerNumberModel(8,0,16,2));
		anisotropicFilteringSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				displayConfiguration.setAnisotropicFiltering((int) anisotropicFilteringSpinner.getValue());
			}
		});
		controlsPanel.add(anisotropicFilteringSpinner, gc);
		
		//------------- Window Width Spinner -------------------- 
		gc.gridy++;
		
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.EAST;
		controlsPanel.add(new JLabel("Window Width (pixels):"), gc);
		
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.WEST;
		displayWidthSpinner = new JSpinner(new SpinnerNumberModel(1440,320,1920,10));
		displayWidthSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				displayConfiguration.setDisplayWidth((int) displayWidthSpinner.getValue());
			}
		});
		controlsPanel.add(displayWidthSpinner, gc);
		
		//------------- Window Height Spinner -------------------- 
		gc.gridy++;
		
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.EAST;
		controlsPanel.add(new JLabel("Window Height (pixels):"), gc);
		
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.WEST;
		displayHeightSpinner = new JSpinner(new SpinnerNumberModel(900,240,1080,10));
		displayHeightSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				displayConfiguration.setDisplayHeight((int) displayHeightSpinner.getValue());
			}
		});
		controlsPanel.add(displayHeightSpinner, gc);
		
		//========================== Window Settings ===============================================
		
		Dimension dims = new Dimension(400, 400);
		setSize(dims);
		setPreferredSize(dims);
	}

	/**
	 * Reads displayConfiguration to set {@link DisplayOptionsTab} panel objects
	 * 
	 * @param displayConfiguration
	 */
	public void setOptionsTab(DisplayConfiguration displayConfiguration) {
		this.displayConfiguration = displayConfiguration;
		
		antiAliasingCheckbox.setSelected(displayConfiguration.isUseAntiAliasing());
		anisotropicFilteringSpinner.setValue(displayConfiguration.getAnisotropicFiltering());
		displayHeightSpinner.setValue(displayConfiguration.getDisplayHeight());		
		displayWidthSpinner.setValue(displayConfiguration.getDisplayWidth());
	}
}
