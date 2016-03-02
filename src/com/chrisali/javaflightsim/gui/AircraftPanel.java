package com.chrisali.javaflightsim.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.Border;

import com.chrisali.javaflightsim.aircraft.Aircraft;

public class AircraftPanel extends JPanel {

	private static final long serialVersionUID = -4654745584883998137L;
	
	private JComboBox<String> aircraftComboBox;
	private JTextArea descriptionArea;
	private JLabel pictureArea;
	
	private JButton okButton;
	private JButton cancelButton;
	
	public AircraftPanel() {
		
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
		Border titleBorder = BorderFactory.createTitledBorder("Database Connection");
		
		controlsPanel.setBorder(BorderFactory.createCompoundBorder(emptyBorder, titleBorder));
		
		Insets rightInsets = new Insets(0, 0, 0, 5);
		Insets noInsets = new Insets(0, 0, 0, 0);
		
		//-------------- GridBag Items -------------------------- 
		
		GridBagConstraints gc = new GridBagConstraints();
		
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.CENTER;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.gridy = 0;
		
		//------------- Aircraft Combobox ------------------------
		gc.gridy++;
		
		gc.gridx = 0;
		aircraftComboBox = new JComboBox<>();
		aircraftComboBox.setModel(makeComboBox());
		//aircraftComboBox.setSelectedIndex(0);
		controlsPanel.add(aircraftComboBox, gc);
		
		//---------------- Picture Area  ------------------------
		
		gc.gridx = 1;
		gc.gridheight = 2;
		pictureArea = new JLabel((String)aircraftComboBox.getSelectedItem());
		controlsPanel.add(pictureArea, gc);
		
		//------------------ Text Field --------------------------
		gc.gridy++;
		
		gc.gridx = 0;
		descriptionArea = new JTextArea();
		controlsPanel.add(descriptionArea, gc);
		
		//----------------- OK Button ----------------------------
		
		okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
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
	}
	
	private DefaultComboBoxModel<String> makeComboBox() {
		DefaultComboBoxModel<String> comboBox = new DefaultComboBoxModel<>();
		
		String path = "AircraftConfigurations";
		File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());

		if(jarFile.isFile()) {  // Run with JAR file
			try {
				JarFile jar = new JarFile(jarFile);
			    Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
			    while(entries.hasMoreElements()) {
			        final String name = entries.nextElement().getName();
			        if (name.startsWith(path + "/")) { //filter according to the path
			            System.out.println(name);
			            comboBox.addElement(name);
			        }
			    }
			    jar.close();
			} catch (IOException e) {
				System.err.println("Error reading folders!");
			}
		} else { // Run with IDE
		    URL url = Aircraft.class.getResource("/" + path);
		    if (url != null) {
		        try {
		            File apps = new File(url.toURI());
		            for (File app : apps.listFiles()) {
		            	System.out.println(app);
		            	comboBox.addElement(app.toString());
	            	}
		        } catch (URISyntaxException ex) {
		        	System.err.println("Error reading URI syntax!");
		        }
		    }
		}
		
		return comboBox;
	}
}
