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

import com.chrisali.javaflightsim.simulation.setup.CameraConfiguration;
import com.chrisali.javaflightsim.simulation.setup.CameraMode;

public class CameraOptionsTab extends JPanel {

	private static final long serialVersionUID = -2865224216075732617L;
	
	private JLabel headerLabel;
	private JCheckBox showInstrumentPanel;
	private JList<String> cameraMode;
	private JSpinner fieldOfView;
	
	private CameraConfiguration cameraConfig;
	
	public CameraOptionsTab() {
		
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
		
		//-------------- Controllers List  ------------------------ 
		gc.gridy++;
		
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.EAST;
		controlsPanel.add(new JLabel("Selected Camera Mode:"), gc);
		
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.WEST;
		DefaultListModel<String> modeList = new DefaultListModel<>();
		modeList.addElement(CameraMode.COCKPIT_2D.toString());
		modeList.addElement(CameraMode.CHASE.toString());
		cameraMode = new JList<String>(modeList);
		cameraMode.setToolTipText("Chooses camera mode will be used on the out the window display");
		cameraMode.setSelectedIndex(0);
		cameraMode.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		cameraMode.addMouseListener(new MouseAdapter() {
			@SuppressWarnings("rawtypes")
			@Override
			public void mouseReleased(MouseEvent e) {
				cameraConfig.setMode(((JList)e.getSource()).getSelectedIndex() == 0 ? CameraMode.COCKPIT_2D : CameraMode.CHASE);
			}
		});
		controlsPanel.add(cameraMode, gc);
		
		//------------ Use Instrument Panel ----------------------
		gc.gridy++;
		
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.EAST;
		controlsPanel.add(new JLabel("Instrument Panel:"), gc);
		
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.WEST;
		showInstrumentPanel = new JCheckBox("Show Panel");
		showInstrumentPanel.setToolTipText("Chooses whether a 2D instrument panel will display");
		showInstrumentPanel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cameraConfig.setShowPanel(((JCheckBox) e.getSource()).isSelected());
			}
		});
		controlsPanel.add(showInstrumentPanel, gc);
		
		//--------- Field of View Spinner ----------------- 
		gc.gridy++;
		
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.EAST;
		controlsPanel.add(new JLabel("Field of View (deg):"), gc);
		
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.WEST;
		fieldOfView = new JSpinner(new SpinnerNumberModel(90,60,120,5));
		fieldOfView.setToolTipText("Sets the field of view of the out the window view");
		fieldOfView.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				cameraConfig.setFieldOfView((int) fieldOfView.getValue());
			}
		});
		controlsPanel.add(fieldOfView, gc);
		
		//========================== Window Settings ===============================================
		
		Dimension dims = new Dimension(400, 400);
		setSize(dims);
		setPreferredSize(dims);
	}

	/**
	 * Reads audioOptions EnumMap to determine how to set {@link CameraOptionsTab} panel objects
	 * 
	 * @param audioOptions
	 */
	public void setOptionsTab(CameraConfiguration cameraConfig) {
		this.cameraConfig = cameraConfig;
		
		cameraMode.setSelectedIndex(cameraConfig.getMode().ordinal());
		showInstrumentPanel.setSelected(cameraConfig.isShowPanel());
		fieldOfView.setValue(cameraConfig.getFieldOfView());
	}
}
