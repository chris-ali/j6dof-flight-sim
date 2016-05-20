package com.chrisali.javaflightsim.menus.aircraftpanel;

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

import com.chrisali.javaflightsim.menus.CancelButtonListener;

public class AircraftPanel extends JPanel {

	private static final long serialVersionUID = -4654745584883998137L;
	
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
				pictureArea.setIcon(createPreviewPicture((String)aircraftComboBox.getSelectedItem(), "PreviewPicture.jpg"));
				descriptionArea.setText(createDescriptionText((String)aircraftComboBox.getSelectedItem(), "Description.txt"));
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
				weightDialog.setAircraftName((String)aircraftComboBox.getSelectedItem());
				weightDialog.updateFields();
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
		pictureArea = new JLabel(createPreviewPicture((String)aircraftComboBox.getSelectedItem(), "PreviewPicture.jpg"));
		controlsPanel.add(pictureArea, gc);
		
		//------------------ Text Field --------------------------
		gc.gridy++;
		
		gc.gridx = 0;
		gc.anchor = GridBagConstraints.NORTH;
		gc.weighty = 0.5;
		gc.weightx = 0.6;
		gc.gridheight = 1;
		descriptionArea = new JTextArea();
		descriptionArea.setText(createDescriptionText((String)aircraftComboBox.getSelectedItem(), "Description.txt"));
		
		descriptionArea.setLineWrap(true);
		descriptionArea.setWrapStyleWord(true);
		descriptionArea.setEditable(false);
		descriptionScroll = new JScrollPane(descriptionArea);
		descriptionScroll.setPreferredSize(new Dimension(300, 200));
		controlsPanel.add(descriptionScroll, gc);
		
		//--------------- Weight Dialog --------------------------

		weightDialog = new WeightDialog(parent, (String)aircraftComboBox.getSelectedItem());
		weightDialog.setWeightConfiguredListener(new WeightConfiguredListener() {
			@Override
			public void weightConfigured(String aircraftName, double fuelWeight, double payloadWeight) {
				if (weightConfiguredListener != null)
					weightConfiguredListener.weightConfigured(aircraftName, fuelWeight, payloadWeight);
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
		String path = "Aircraft";
		
		File[] directories = new File(".//" + path + "//").listFiles(new FileFilter() {
		    @Override
		    public boolean accept(File file) {return file.isDirectory();}
		});
		
		for (File file : directories)
			comboBox.addElement(file.toString().split("\\\\")[2]);
		
		return comboBox;
	}
	
	private ImageIcon createPreviewPicture(String aircraftName, String fileName) {
		StringBuilder sb = new StringBuilder();
		sb.append("Aircraft//").append(aircraftName).append("//").append(fileName);
		
		File imageFile = new File(sb.toString());
		
		ImageIcon image = new ImageIcon("");
		
		try { 
			image = new ImageIcon(imageFile.toURI().toURL());
		} catch (MalformedURLException e) {
			JOptionPane.showMessageDialog(AircraftPanel.this, "Unable to load image: " + fileName, 
					"Error Loading Message", JOptionPane.ERROR_MESSAGE);
		}

		return image;
	}
	
	private String createDescriptionText(String aircraftName, String fileName) {
		StringBuilder sb = new StringBuilder();
		sb.append("Aircraft//").append(aircraftName).append("//").append(fileName);
		
		String readLine = null;
		StringBuilder readFile = new StringBuilder();
		
		try (BufferedReader br = new BufferedReader(new FileReader(sb.toString()))) {
			while ((readLine = br.readLine()) != null)
				readFile.append(readLine).append("\n");
		} catch (FileNotFoundException e) {System.err.println("Could not find: " + fileName + ".txt!");}
		catch (IOException e) {System.err.println("Could not read: " + fileName + ".txt!");}
		catch (NullPointerException e) {System.err.println("Bad reference when reading: " + fileName + ".txt!");}
		
		return readFile.toString();
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
}
