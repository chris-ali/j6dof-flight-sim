package com.chrisali.javaflightsim.menus.aircraftpanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.chrisali.javaflightsim.simulation.aircraft.MassProperties;
import com.chrisali.javaflightsim.utilities.FileUtilities;

public class WeightDialog extends JDialog {

	private static final long serialVersionUID = -8651284577042494696L;

	private String aircraftName;
	
	private JSlider fuelSlider;
	private double fuelFraction;
	private JSlider payloadSlider;
	private double payloadFraction;
	
	private Map<MassProperties, Double> massProperties;
	private JLabel fuelWeightLabel;
	private double fuelWeightValue;
	private JLabel payloadWeightLabel;
	private double payloadWeightValue;
	private JLabel totalWeightLabel;
	private double totalWeightValue;
	
	private JButton okButton;
	private JButton cancelButton;
	
	private WeightConfiguredListener weightConfiguredListener;
	
	public WeightDialog(JFrame parent, String aircraft) {
		super(parent, "Weight", false);
		
		aircraftName = aircraft;
		updateFields();
		
		//-------------------- Panels ---------------------------
		
		JPanel controlsPanel = new JPanel();
		JPanel weightPanel = new JPanel();
		JPanel buttonPanel = new JPanel();
		
		controlsPanel.setLayout(new GridBagLayout());
		weightPanel.setLayout(new GridBagLayout());
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		setLayout(new BorderLayout());
		add(controlsPanel, BorderLayout.NORTH);
		add(weightPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		
		//------------------ Borders and Insets -----------------
		
		int margins = 5;
		Border emptyBorder = BorderFactory.createEmptyBorder(margins ,margins, margins, margins);
		Border titleBorder = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
		
		weightPanel.setBorder(BorderFactory.createCompoundBorder(emptyBorder, titleBorder));
		
		Insets spacer = new Insets(margins, margins, margins, margins);
		
		//-------------- GridBag Items -------------------------- 
		
		GridBagConstraints gc_controls = new GridBagConstraints();
		
		gc_controls.fill = GridBagConstraints.HORIZONTAL;
		
		gc_controls.weightx = 1;
		gc_controls.weighty = 1;
		gc_controls.gridy = 0;
		gc_controls.insets = spacer;
		
		//-------------- Fuel Weight Slider ---------------------
		gc_controls.gridy++;
		
		gc_controls.gridx = 0;
		gc_controls.anchor = GridBagConstraints.LINE_START;
		controlsPanel.add(new JLabel("Fuel Weight:"), gc_controls);
		
		gc_controls.gridx = 1;
		gc_controls.anchor = GridBagConstraints.CENTER;
		fuelSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 100, (int)(fuelFraction*100));
		fuelSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				fuelFraction = ((double)fuelSlider.getValue())/((double)fuelSlider.getMaximum());
				recalculateWeights();
			}
		});
		controlsPanel.add(fuelSlider, gc_controls);
		
		//------------ Payload Weight Slider --------------------
		gc_controls.gridy++;
		
		gc_controls.gridx = 0;
		gc_controls.anchor = GridBagConstraints.LINE_START;
		controlsPanel.add(new JLabel("Payload Weight:"), gc_controls);
		
		gc_controls.gridx = 1;
		gc_controls.anchor = GridBagConstraints.CENTER;
		payloadSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 100, (int)(payloadFraction*100));
		payloadSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				payloadFraction = ((double)payloadSlider.getValue())/((double)payloadSlider.getMaximum());
				recalculateWeights();
			}
		});
		controlsPanel.add(payloadSlider, gc_controls);
		
		//====================================================================
		
		//-------------- GridBag Items -------------------------- 
		
		GridBagConstraints gc_weights = new GridBagConstraints();
		
		gc_weights.fill = GridBagConstraints.HORIZONTAL;
		
		gc_weights.weightx = 1;
		gc_weights.weighty = 1;
		gc_weights.gridy = 0;
		gc_weights.insets = spacer;
		
		//------------ Fuel Weight Label ---------------------
		gc_weights.gridy++;
		
		gc_weights.gridx = 0;
		gc_weights.anchor = GridBagConstraints.LINE_END;
		weightPanel.add(new JLabel("Fuel Weight: "), gc_weights);
		
		gc_weights.gridx = 1;
		gc_weights.anchor = GridBagConstraints.LINE_START;
		fuelWeightLabel = new JLabel(String.valueOf(fuelWeightValue));
		weightPanel.add(fuelWeightLabel, gc_weights);
		
		//------------ Payload Weight Label ---------------------
		gc_weights.gridy++;
		
		gc_weights.gridx = 0;
		gc_weights.anchor = GridBagConstraints.LINE_END;
		weightPanel.add(new JLabel("Payload Weight: "), gc_weights);
		
		gc_weights.gridx = 1;
		gc_weights.anchor = GridBagConstraints.LINE_START;
		payloadWeightLabel = new JLabel(String.valueOf(payloadWeightValue));
		weightPanel.add(payloadWeightLabel, gc_weights);
		
		//------------ Total Weight Label ---------------------
		gc_weights.gridy++;
		
		gc_weights.gridx = 0;
		gc_weights.anchor = GridBagConstraints.LINE_END;
		weightPanel.add(new JLabel("Total Weight: "), gc_weights);
		
		gc_weights.gridx = 1;
		gc_weights.anchor = GridBagConstraints.LINE_START;
		totalWeightLabel = new JLabel(String.valueOf(totalWeightValue));
		weightPanel.add(totalWeightLabel, gc_weights);
		
		//====================================================================
		
		//----------------- OK Button ---------------------------
		
		okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (weightConfiguredListener != null)
					weightConfiguredListener.weightConfigured(aircraftName, fuelFraction, payloadFraction);
				setVisible(false);
			}
		});
		buttonPanel.add(okButton);
		
		//------------------- Cancel Button ------------------------
		
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		buttonPanel.add(cancelButton);
		okButton.setPreferredSize(cancelButton.getPreferredSize());
		
		//========================== Window Settings ===============================================
		
		setLocationRelativeTo(parent);
		Dimension dims = new Dimension(400, 200);
		setSize(dims);
		setResizable(false);
	}
	
	private void recalculateWeights() {
		fuelWeightValue = fuelFraction*massProperties.get(MassProperties.MAX_WEIGHT_FUEL);
		payloadWeightValue = payloadFraction*massProperties.get(MassProperties.MAX_WEIGHT_PAYLOAD);
		totalWeightValue = (fuelFraction*massProperties.get(MassProperties.MAX_WEIGHT_PAYLOAD) +
				 		   (payloadFraction*massProperties.get(MassProperties.MAX_WEIGHT_PAYLOAD) +
						    massProperties.get(MassProperties.WEIGHT_EMPTY)));
		
		if (payloadWeightLabel != null) {
			payloadWeightLabel.setText(String.valueOf(payloadWeightValue));
			fuelWeightLabel.setText(String.valueOf(fuelWeightValue));
			totalWeightLabel.setText(String.valueOf(totalWeightValue));
		}
	}
	
	protected void updateFields() {
		massProperties = FileUtilities.parseMassProperties(aircraftName);
		fuelFraction = massProperties.get(MassProperties.WEIGHT_FUEL);
		payloadFraction = massProperties.get(MassProperties.WEIGHT_PAYLOAD);
		
		recalculateWeights();
	}
		
	public void setAircraftName(String newAircraftName) {aircraftName = newAircraftName;}
	
	public void setWeightConfiguredListener(WeightConfiguredListener weightConfiguredListener) {
		this.weightConfiguredListener = weightConfiguredListener;
	}
}
