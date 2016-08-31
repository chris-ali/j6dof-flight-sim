package com.chrisali.javaflightsim.menus.optionspanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumMap;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

public class DisplayOptionsTab extends JPanel {

	private static final long serialVersionUID = -2865224216075732617L;
	
	private JLabel headerLabel;
	private JSpinner displayWidthSpinner;
	private JSpinner displayHeightSpinner;
	private JCheckBox antiAliasingCheckbox;
	
	private EnumMap<DisplayOptions, Integer> displayOptions;
	
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
				if(((JCheckBox)e.getSource()).isSelected())
					displayOptions.put(DisplayOptions.ANTI_ALIASING, 1);
				else
					displayOptions.put(DisplayOptions.ANTI_ALIASING, 0);
			}
		});
		controlsPanel.add(antiAliasingCheckbox, gc);
		
		//------------- Window Width Spinner -------------------- 
		gc.gridy++;
		
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.EAST;
		controlsPanel.add(new JLabel("Window Width (pixels):"), gc);
		
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.WEST;
		displayWidthSpinner = new JSpinner(new SpinnerNumberModel(1440,320,1920,10));
		controlsPanel.add(displayWidthSpinner, gc);
		
		//------------- Window Height Spinner -------------------- 
		gc.gridy++;
		
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.EAST;
		controlsPanel.add(new JLabel("Window Height (pixels):"), gc);
		
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.WEST;
		displayHeightSpinner = new JSpinner(new SpinnerNumberModel(900,240,1080,10));
		controlsPanel.add(displayHeightSpinner, gc);
		
		//========================== Window Settings ===============================================
		
		Dimension dims = new Dimension(400, 400);
		setSize(dims);
		setPreferredSize(dims);
	}
	
	protected EnumMap<DisplayOptions, Integer> getDisplayOptions() {
		return displayOptions;
	}

	/**
	 * Reads displayOptions EnumMap to determine how to set {@link DisplayOptionsTab} panel objects
	 * 
	 * @param displayOptions
	 */
	public void setOptionsTab(EnumMap<DisplayOptions, Integer> displayOptions) {
		this.displayOptions = displayOptions;
		
		antiAliasingCheckbox.setSelected(displayOptions.get(DisplayOptions.ANTI_ALIASING) != 0);
		
		displayWidthSpinner.setValue(displayOptions.get(DisplayOptions.DISPLAY_WIDTH));
		displayHeightSpinner.setValue(displayOptions.get(DisplayOptions.DISPLAY_HEIGHT));
	}
}
