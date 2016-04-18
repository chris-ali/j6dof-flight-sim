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
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import com.chrisali.javaflightsim.menus.CancelButtonListener;

public class DisplayOptionsPanel extends JPanel {

	private static final long serialVersionUID = -2865224216075732617L;
	
	private JLabel headerLabel;
	private JSpinner displayWidthSpinner;
	private JSpinner displayHeightSpinner;
	private JCheckBox antiAliasingCheckbox;
	private JButton okButton;
	private JButton cancelButton;

	private EnumMap<DisplayOptions, Integer> displayOptions = new EnumMap<DisplayOptions,Integer>(DisplayOptions.class);
	
	private OptionsConfigurationListener optionsConfigurationListener;
	private CancelButtonListener cancelButtonListener;
	
	public DisplayOptionsPanel() {
		
		//-------------------- Panels ---------------------------
		
		JPanel headerPanel = new JPanel();
		JPanel controlsPanel = new JPanel();
		JPanel buttonPanel = new JPanel();
		
		headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
		controlsPanel.setLayout(new GridBagLayout());
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		setLayout(new BorderLayout());
		add(headerPanel, BorderLayout.NORTH);
		add(controlsPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		
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
		
		//------------- Window Width Spinner -------------------- 
		gc.gridy++;
		
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.EAST;
		controlsPanel.add(new JLabel("Window Height (pixels):"), gc);
		
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.WEST;
		displayHeightSpinner = new JSpinner(new SpinnerNumberModel(900,240,1080,10));
		controlsPanel.add(displayHeightSpinner, gc);

		//----------------- OK Button ----------------------------
		
		okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {			
				if (optionsConfigurationListener != null)
					optionsConfigurationListener.displayOptionsConfigured(displayOptions);
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
	
	public void setOptionsPanel(EnumMap<DisplayOptions, Integer> displayOptions) {
		this.displayOptions = displayOptions;
		
		antiAliasingCheckbox.setSelected(displayOptions.get(DisplayOptions.ANTI_ALIASING) != 0);
		
		displayWidthSpinner.setValue(displayOptions.get(DisplayOptions.DISPLAY_WIDTH));
		displayHeightSpinner.setValue(displayOptions.get(DisplayOptions.DISPLAY_HEIGHT));
	}
	
	public void setCancelButtonListener(CancelButtonListener cancelButtonListener) {
		this.cancelButtonListener = cancelButtonListener;
	}
	
	public void setOptionsConfigurationListener(OptionsConfigurationListener optionsConfigurationListener) {
		this.optionsConfigurationListener = optionsConfigurationListener;
	}
}
