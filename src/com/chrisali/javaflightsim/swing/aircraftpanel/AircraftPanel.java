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
package com.chrisali.javaflightsim.swing.aircraftpanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chrisali.javaflightsim.simulation.utilities.SimDirectories;
import com.chrisali.javaflightsim.simulation.utilities.SimFiles;
import com.chrisali.javaflightsim.swing.CancelButtonListener;

public class AircraftPanel extends JPanel {

	private static final long serialVersionUID = -4654745584883998137L;
	
	private static final Logger logger = LogManager.getLogger(AircraftPanel.class);
	
	private JLabel headerLabel;
	private JComboBox<String> aircraftComboBox;
	private DefaultComboBoxModel<String> aircraftComboBoxModel;
	private JTextArea descriptionArea;
	private JScrollPane descriptionScroll;
	private JLabel pictureArea;
	private JButton weightButton;
	
	private WeightDialog weightDialog;
	
	private JButton okButton;
	private JButton cancelButton;
	
	private AircraftDropDownListener aircraftDropDownListener;
	private AircraftConfigurationListener aircraftConfigurationListener;
	private WeightConfiguredListener weightConfiguredListener;
	private CancelButtonListener cancelButtonListener;
		
	public AircraftPanel(JFrame parent) {
								
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
		Border titleBorder = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
		
		controlsPanel.setBorder(BorderFactory.createCompoundBorder(emptyBorder, titleBorder));
		
		Insets spacer = new Insets(margins, margins, margins, margins);
		
		//------------------- Header ----------------------------
		
		headerLabel = new JLabel("Aircraft");
		headerLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
		headerPanel.add(headerLabel);
		
		//-------------- GridBag Items -------------------------- 
		
		GridBagConstraints gc = new GridBagConstraints();
		
		gc.fill = GridBagConstraints.HORIZONTAL;
		
		gc.weightx = 1;
		gc.weighty = 1;
		gc.gridy = 0;
		gc.insets = spacer;
		
		//------------- Aircraft Combobox ------------------------
		gc.gridy++;
		
		gc.anchor = GridBagConstraints.SOUTH;
		gc.weightx = 0.5;
		gc.weighty = 0.5;
		gc.gridx = 0;
		aircraftComboBox = new JComboBox<>();
		aircraftComboBoxModel = makeComboBox();
		aircraftComboBox.setModel(aircraftComboBoxModel);
		aircraftComboBox.setSelectedIndex(0);
		aircraftComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				String selectedName = (String)aircraftComboBox.getSelectedItem();
				pictureArea.setIcon(createPreviewPicture(selectedName, SimFiles.PREVIEW_PICTURE.toString()));
				descriptionArea.setText(createDescriptionText(selectedName, SimFiles.DESCRIPTION.toString()));
				
				weightDialog.setVisible(false);
				
				if (aircraftDropDownListener != null)
					aircraftDropDownListener.aircraftSelected(selectedName);
			}
		});
		controlsPanel.add(aircraftComboBox, gc);
		
		//---------------- Weight Button ------------------------
		gc.gridy++;
		
		weightButton = new JButton("Configure Weight");
		weightButton.setToolTipText("Configures the fuel and payload weight of the aircraft");
		weightButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				weightDialog.setVisible(true);
			}
		});
		controlsPanel.add(weightButton, gc);
		
		//---------------- Picture Area  ------------------------
		
		gc.anchor = GridBagConstraints.CENTER;
		gc.gridx = 1;
		gc.weightx = 0.5;
		gc.weighty = 0.5;
		gc.gridheight = 2;
		//Picture must be ~430x230 pixels
		pictureArea = new JLabel(createPreviewPicture((String)aircraftComboBox.getSelectedItem(), SimFiles.PREVIEW_PICTURE.toString()));
		controlsPanel.add(pictureArea, gc);
		
		//------------------ Text Field --------------------------
		gc.gridy++;
		
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.NORTH;
		gc.weighty = 0.5;
		gc.weightx = 0.6;
		gc.gridheight = 1;
		descriptionArea = new JTextArea();
		descriptionArea.setText(createDescriptionText((String)aircraftComboBox.getSelectedItem(), SimFiles.DESCRIPTION.toString()));
		
		descriptionArea.setLineWrap(true);
		descriptionArea.setWrapStyleWord(true);
		descriptionArea.setEditable(false);
		descriptionScroll = new JScrollPane(descriptionArea);
		descriptionScroll.setPreferredSize(new Dimension(300, 200));
		controlsPanel.add(descriptionScroll, gc);
		
		//--------------- Weight Dialog --------------------------

		weightDialog = new WeightDialog(parent);
		weightDialog.setWeightConfiguredListener(new WeightConfiguredListener() {
			@Override
			public void weightConfigured(double fuelWeight, double payloadWeight) {
				if (weightConfiguredListener != null)
					weightConfiguredListener.weightConfigured(fuelWeight, payloadWeight);
			}
		});
		
		//----------------- OK Button ----------------------------
		
		okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (aircraftConfigurationListener != null)
					aircraftConfigurationListener.aircraftConfigured((String)aircraftComboBox.getSelectedItem());
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
		
		Dimension dims = new Dimension(800, 400);
		setSize(dims);
	}
	
	private DefaultComboBoxModel<String> makeComboBox() {
		DefaultComboBoxModel<String> comboBox = new DefaultComboBoxModel<>();
		StringBuilder sb = new StringBuilder();
		sb.append(SimDirectories.AIRCRAFT.toString()).append(File.separator);
		
		File[] directories = new File(sb.toString()).listFiles(new FileFilter() {
		    @Override
		    public boolean accept(File file) {return file.isDirectory();}
		});
		
		for (File file : directories) {
			String splitter = File.separator.replace("\\", "\\\\");
			String[] splitPath = file.toString().split(splitter);
			comboBox.addElement(splitPath[splitPath.length - 1]);
		}
		
		return comboBox;
	}
	
	private ImageIcon createPreviewPicture(String aircraftName, String fileName) {
		StringBuilder sb = new StringBuilder();
		sb.append(SimDirectories.AIRCRAFT.toString()).append(File.separator).append(aircraftName).append(File.separator).append(fileName).append(SimFiles.PREVIEW_PIC_EXT.toString());
		
		File imageFile = new File(sb.toString());
		
		ImageIcon image = new ImageIcon("");
		
		try { 
			image = new ImageIcon(imageFile.toURI().toURL());
		} catch (MalformedURLException e) {
			JOptionPane.showMessageDialog(AircraftPanel.this, "Unable to load image: " + fileName + SimFiles.PREVIEW_PIC_EXT + "!", 
					"Error Loading Image", JOptionPane.ERROR_MESSAGE);
			logger.error("Could not find image: " + fileName + SimFiles.DESCRIPTION_EXT.toString() + "!");
		}

		return image;
	}
	
	private String createDescriptionText(String aircraftName, String fileName) {
		StringBuilder sb = new StringBuilder();
		sb.append(SimDirectories.AIRCRAFT.toString()).append(File.separator).append(aircraftName).append(File.separator).append(fileName).append(SimFiles.DESCRIPTION_EXT.toString());
		
		String readLine = null;
		StringBuilder readFile = new StringBuilder();
		
		try (BufferedReader br = new BufferedReader(new FileReader(sb.toString()))) {
			while ((readLine = br.readLine()) != null)
				readFile.append(readLine).append("\n");
		} catch (FileNotFoundException e) {
			logger.error("Could not find: " + fileName + SimFiles.DESCRIPTION_EXT.toString() + "!");
			
			JOptionPane.showMessageDialog(AircraftPanel.this, "Unable to load " + aircraftName + " description! Cannot find " + fileName + SimFiles.DESCRIPTION_EXT.toString() + "!", 
					"Error Loading Description", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			logger.error("Could not read: " + fileName + SimFiles.DESCRIPTION_EXT.toString() + "!");
			
			JOptionPane.showMessageDialog(AircraftPanel.this, "Unable to load " + aircraftName + " description! Cannot read " + fileName + SimFiles.DESCRIPTION_EXT.toString() + "!", 
					"Error Reading Description", JOptionPane.ERROR_MESSAGE);
		} catch (NullPointerException e) {
			logger.error("Bad reference when reading: " + fileName + SimFiles.DESCRIPTION_EXT.toString() + "!");
			
			JOptionPane.showMessageDialog(AircraftPanel.this, "Unable to load " + aircraftName + " description! Bad reference when reading " + fileName + SimFiles.DESCRIPTION_EXT.toString() + "!", 
					"Error Loading Description", JOptionPane.ERROR_MESSAGE);
		}
		
		return readFile.toString();
	}
	
	public WeightDialog getWeightDialog() {
		return weightDialog;
	}

	public void setAircraftPanel(String aircraftName) {
		for(int i=0; i<aircraftComboBoxModel.getSize(); i++) {
			if (aircraftComboBoxModel.getElementAt(i).compareTo(aircraftName) == 0)
				aircraftComboBox.setSelectedIndex(i);
		}
	}
	
	public void setCancelButtonListener(CancelButtonListener cancelButtonListener) {
		this.cancelButtonListener = cancelButtonListener;
	}
	
	public void setAircraftConfigurationListener(AircraftConfigurationListener aircraftConfigurationListener) {
		this.aircraftConfigurationListener = aircraftConfigurationListener;
	}
	
	public void setWeightConfiguredListener(WeightConfiguredListener weightConfiguredListener) {
		this.weightConfiguredListener = weightConfiguredListener;
	}
	
	public void setAircraftSelectedListener(AircraftDropDownListener aircraftDropDownListener) {
		this.aircraftDropDownListener = aircraftDropDownListener;
	}
}
