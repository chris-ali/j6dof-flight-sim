package com.chrisali.javaflightsim.menus.initialconditionspanel;

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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.chrisali.javaflightsim.menus.CancelButtonListener;
import com.chrisali.javaflightsim.simulation.setup.InitialConditions;
import com.chrisali.javaflightsim.utilities.FileUtilities;

public class InitialConditionsPanel extends JPanel {

	private static final long serialVersionUID = -6719504365922614073L;
	
	private JLabel headerLabel;
	private JTextArea latitudeArea;
	private JTextArea longitudeArea;
	private JSpinner headingSpinner;
	private SpinnerNumberModel headingSpinnerModel;
	private JSpinner altitudeSpinner;
	private SpinnerNumberModel altitudeSpinnerModel;
	private JSpinner airspeedSpinner;
	private SpinnerNumberModel airspeedSpinnerModel;
	
	private JButton okButton;
	private JButton cancelButton;
	
	private InitialConditionsConfigurationListener initialConditionsConfigurationListener;
	private CancelButtonListener cancelButtonListener;
	
	public InitialConditionsPanel() {
		
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

		//--------- Dimensions Borders and Insets ---------------
		
		int margins = 5;
		Border emptyBorder = BorderFactory.createEmptyBorder(margins ,margins, margins, margins);
		Border etchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
		
		controlsPanel.setBorder(BorderFactory.createCompoundBorder(emptyBorder, etchedBorder));
		
		Insets spacer = new Insets(margins, margins, margins, margins);
		
		Dimension componentSize = new Dimension(60, 25);
		Dimension windowSize = new Dimension(300, 400);
		
		//------------------- Header ----------------------------
		
		headerLabel = new JLabel("Initial Conditions");
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
		
		//------- Coordinates Text Labels Row -------------------
		gc.gridy++;
		gc.weighty = 0.05;
		
		gc.gridx = 1;
		gc.weightx = 0.005;
		gc.anchor = GridBagConstraints.LAST_LINE_START;
		controlsPanel.add(new JLabel("Latitude:"), gc);
		
		gc.gridx = 2;
		controlsPanel.add(new JLabel("Longitude:"), gc);
		
		//------- Coordinates Text Area Row ---------------------
		gc.gridy++;
		
		gc.gridx = 0;
		gc.weightx = 1;
		gc.anchor = GridBagConstraints.LINE_END;
		controlsPanel.add(new JLabel("Position:"), gc);
		
		gc.gridx = 1;
		gc.weightx = 0.005;
		gc.anchor = GridBagConstraints.LINE_START;
		latitudeArea = new JTextArea("0.0");
		latitudeArea.setPreferredSize(componentSize);
		latitudeArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		latitudeArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {	
				try {
					Double typedValue = Double.parseDouble(KeyEvent.getKeyText(e.getKeyCode()));
					latitudeArea.append(String.valueOf(typedValue));
				} catch (NumberFormatException ex) {
					latitudeArea.append("");
				}
			}
		});
		controlsPanel.add(latitudeArea, gc);
		
		gc.gridx = 2;
		longitudeArea = new JTextArea("0.0");
		longitudeArea.setPreferredSize(componentSize);
		longitudeArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		longitudeArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {	
				try {
					Double typedValue = Double.parseDouble(KeyEvent.getKeyText(e.getKeyCode()));
					longitudeArea.append(String.valueOf(typedValue));
				} catch (NumberFormatException ex) {
					longitudeArea.append("");
				}
			}
		});
		controlsPanel.add(longitudeArea, gc);
		
		//----------------- Heading Spinner ----------------------
		gc.gridy++;
		gc.weightx = 1;
		
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.LINE_END;
		controlsPanel.add(new JLabel("Heading:"), gc);
		
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.LINE_START;
		headingSpinnerModel = new SpinnerNumberModel(45.0, 0.0, 359.0, 1.0); 
		headingSpinner = new JSpinner(headingSpinnerModel);
		headingSpinner.setPreferredSize(componentSize);
		headingSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if ((double)headingSpinner.getValue() > 359.0)
					headingSpinner.setValue((double)0.0);
				else if ((double)headingSpinner.getValue() < 0.0)
					headingSpinner.setValue((double)359.0);
			}
		});
		controlsPanel.add(headingSpinner, gc);
		
		//----------------- Altitude Spinner ----------------------
		gc.gridy++;
		
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.LINE_END;
		controlsPanel.add(new JLabel("Altitude:"), gc);
		
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.LINE_START;
		altitudeSpinnerModel = new SpinnerNumberModel(5000.0, 0.0, 60000.0, 10.0);
		altitudeSpinner = new JSpinner(altitudeSpinnerModel);
		altitudeSpinner.setPreferredSize(componentSize);
		controlsPanel.add(altitudeSpinner, gc);
		
		//----------------- Airspeed Spinner ----------------------
		gc.gridy++;
		gc.weighty = 1.0;
		
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.FIRST_LINE_END;
		controlsPanel.add(new JLabel("True Airspeed:"), gc);
		
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		airspeedSpinnerModel = new SpinnerNumberModel(115.0, 0.0, 300.0, 1.0);
		airspeedSpinner = new JSpinner(airspeedSpinnerModel);
		airspeedSpinner.setPreferredSize(componentSize);
		controlsPanel.add(airspeedSpinner, gc);
		
		//----------------- OK Button ----------------------------
		
		okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (initialConditionsConfigurationListener != null) {
					initialConditionsConfigurationListener.initialConditonsConfigured(
							new double[]{Double.parseDouble(latitudeArea.getText()), Double.parseDouble(longitudeArea.getText())}, 
							(double)headingSpinner.getValue(), (double)altitudeSpinner.getValue(), (double)airspeedSpinner.getValue());
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
		
		setSize(windowSize);
		setPreferredSize(windowSize);
	}
	
	public void setInitialConditionsPanel(Map<InitialConditions, Double> initialConditions) {
		headingSpinnerModel.setValue(Math.toDegrees(initialConditions.get(InitialConditions.INITPSI)));
		airspeedSpinnerModel.setValue(FileUtilities.toKnots(initialConditions.get(InitialConditions.INITU)));
		altitudeSpinnerModel.setValue(initialConditions.get(InitialConditions.INITD));
	}
	
	public void setCancelButtonListener(CancelButtonListener cancelButtonListener) {
		this.cancelButtonListener = cancelButtonListener;
	}
	
	public void setInitialConditionsConfigurationListener (InitialConditionsConfigurationListener initialConditionsConfigurationListener) {
		this.initialConditionsConfigurationListener = initialConditionsConfigurationListener;
	}
}
