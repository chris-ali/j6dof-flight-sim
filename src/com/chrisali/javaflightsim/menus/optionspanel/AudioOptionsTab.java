package com.chrisali.javaflightsim.menus.optionspanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.EnumMap;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class AudioOptionsTab extends JPanel {

	private static final long serialVersionUID = -2865224216075732617L;
	
	private JLabel headerLabel;
	private JSlider engineVolumeSlider;
	private float engineVolume;
	private JSlider systemsVolumeSlider;
	private float systemsVolume;
	private JSlider environmentVolumeSlider;
	private float environmentVolume;
	
	private EnumMap<AudioOptions, Float> audioOptions;
	
	public AudioOptionsTab() {
		
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
		
		//------------ Engine Volume Slider ---------------------
		gc.gridy++;
		
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.LINE_START;
		controlsPanel.add(new JLabel("Engine Volume:"), gc);
		
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.CENTER;
		engineVolumeSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 100, (int)(engineVolume*100));
		engineVolumeSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				engineVolume = ((float)engineVolumeSlider.getValue())/((float)engineVolumeSlider.getMaximum());
				audioOptions.put(AudioOptions.ENGINE_VOLUME, engineVolume);
			}
		});
		controlsPanel.add(engineVolumeSlider, gc);
		
		//------------ Systems Volume Slider ---------------------
		gc.gridy++;
		
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.LINE_START;
		controlsPanel.add(new JLabel("Systems Volume:"), gc);
		
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.CENTER;
		systemsVolumeSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 100, (int)(systemsVolume*100));
		systemsVolumeSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				systemsVolume = ((float)systemsVolumeSlider.getValue())/((float)systemsVolumeSlider.getMaximum());
				audioOptions.put(AudioOptions.SYSTEMS_VOLUME, systemsVolume);
			}
		});
		controlsPanel.add(systemsVolumeSlider, gc);
		
		//---------- Environment Volume Slider -------------------
		gc.gridy++;
		
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.LINE_START;
		controlsPanel.add(new JLabel("Environment Volume:"), gc);
		
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.CENTER;
		environmentVolumeSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 100, (int)(environmentVolume*100));
		environmentVolumeSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				environmentVolume = ((float)environmentVolumeSlider.getValue())/((float)environmentVolumeSlider.getMaximum());
				audioOptions.put(AudioOptions.ENVIRONMENT_VOLUME, environmentVolume);
			}
		});
		controlsPanel.add(environmentVolumeSlider, gc);
		
		//========================== Window Settings ===============================================
		
		Dimension dims = new Dimension(400, 400);
		setSize(dims);
		setPreferredSize(dims);
	}
	
	protected EnumMap<AudioOptions, Float> getAudioOptions() {
		return audioOptions;
	}

	/**
	 * Reads audioOptions EnumMap to determine how to set {@link AudioOptionsTab} panel objects
	 * 
	 * @param audioOptions
	 */
	public void setOptionsTab(EnumMap<AudioOptions, Float> audioOptions) {
		this.audioOptions = audioOptions;
		
		engineVolumeSlider.setValue((int)(100*audioOptions.get(AudioOptions.ENGINE_VOLUME)));
		systemsVolumeSlider.setValue((int)(100*audioOptions.get(AudioOptions.SYSTEMS_VOLUME)));
		environmentVolumeSlider.setValue((int)(100*audioOptions.get(AudioOptions.ENVIRONMENT_VOLUME)));
	}
}
