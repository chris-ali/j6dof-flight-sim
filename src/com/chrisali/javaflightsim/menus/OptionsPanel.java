package com.chrisali.javaflightsim.menus;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import com.chrisali.javaflightsim.simulation.setup.Options;

public class OptionsPanel extends JDialog {

	private static final long serialVersionUID = -2865224216075732617L;
	
	private JCheckBox analysisMode;
	private JCheckBox consoleDisplay;
	private JList<String> controllers;
	private JSpinner stepSizeSpinner;
	private JButton okButton;
	private JButton cancelButton;

	private EnumSet<Options> options = EnumSet.noneOf(Options.class);
	private OptionsConfigurationListener optionsConfigurationListener;
	
	public OptionsPanel(JFrame parent) {
		super(parent, "Options", false);
		
		//-------------------- Panels ---------------------------
		
		JPanel controlsPanel = new JPanel();
		JPanel buttonPanel = new JPanel();
		
		controlsPanel.setLayout(new GridBagLayout());
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		setLayout(new BorderLayout());
		add(controlsPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		
		//------------------ Borders and Insets -----------------
		
		int margins = 5;
		Border emptyBorder = BorderFactory.createEmptyBorder(margins ,margins, margins, margins);
		Border etchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
		
		controlsPanel.setBorder(BorderFactory.createCompoundBorder(emptyBorder, etchedBorder));
		
		Insets spacer = new Insets(margins, margins, margins, margins);
		
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
									"dynamic stability. At the end of the analysis, the results will be plotted in popup windows.");
		analysisMode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(((JCheckBox)e.getSource()).isSelected()) {
					options.removeAll(options);
					options.add(Options.ANALYSIS_MODE);
					controllers.setEnabled(false);
				} else {
					options.remove(Options.ANALYSIS_MODE);
					options.add(Options.UNLIMITED_FLIGHT);
					controllers.setEnabled(true);
				}
			}
		});
		controlsPanel.add(analysisMode, gc);
		
		//----------- Analysis Mode Checkbox --------------------- 
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
					options.add(Options.CONSOLE_DISPLAY);
				else
					options.remove(Options.CONSOLE_DISPLAY);
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
		controllerList.addElement("CH Controls Suite");
		controllers = new JList<String>(controllerList);
		controllers.setSelectedIndex(0);
		controllers.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				switch (controllers.getSelectedValue()) {
				case ("Joystick"):
					options.removeIf(p -> (p == Options.USE_MOUSE || p == Options.USE_CH_CONTROLS));
					options.add(Options.USE_JOYSTICK);
					break;
				case ("Mouse"):
					options.removeIf(p -> (p == Options.USE_JOYSTICK || p == Options.USE_CH_CONTROLS));
					options.add(Options.USE_MOUSE);
					break;
				case ("CH Controls Suite"):
					options.removeIf(p -> (p == Options.USE_MOUSE || p == Options.USE_JOYSTICK));
					options.add(Options.USE_CH_CONTROLS);
					break;
				default:
					break;
				}
			}
		});
		controlsPanel.add(controllers, gc);
		
		//--------- Simulation Step Size Spinner ----------------- 
		gc.gridy++;
		
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.EAST;
		controlsPanel.add(new JLabel("Simulation Refresh Rate (cycles/sec):"), gc);
		
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.WEST;
		stepSizeSpinner = new JSpinner(new SpinnerNumberModel(20,10,500,10));
		controlsPanel.add(stepSizeSpinner, gc);

		//----------------- OK Button ----------------------------
		
		okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!options.contains(Options.ANALYSIS_MODE))
					options.add(Options.UNLIMITED_FLIGHT);
				
				if (optionsConfigurationListener != null)
					optionsConfigurationListener.optionsConfigured(options, (int)stepSizeSpinner.getValue());
				
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
		Dimension dims = new Dimension(500, 400);
		setSize(dims);
		setResizable(false);
	}
	
	public void setOptionsConfigurationListener(OptionsConfigurationListener optionsConfigurationListener) {
		this.optionsConfigurationListener = optionsConfigurationListener;
	}
}
