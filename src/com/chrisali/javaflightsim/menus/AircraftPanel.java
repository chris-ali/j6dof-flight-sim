package com.chrisali.javaflightsim.menus;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

public class AircraftPanel extends JDialog {

	private static final long serialVersionUID = -4654745584883998137L;
	
	private JComboBox<String> aircraftComboBox;
	private JTextArea descriptionArea;
	private JLabel pictureArea;
	
	private JButton okButton;
	private JButton cancelButton;
	
	private AircraftConfigurationListener aircraftConfigurationListener;
	
	public AircraftPanel(JFrame parent) {
		super(parent, "Aircraft", false);
		
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
		Border titleBorder = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
		
		controlsPanel.setBorder(BorderFactory.createCompoundBorder(emptyBorder, titleBorder));
		
		Insets spacer = new Insets(margins, margins, margins, margins);
		
		//-------------- GridBag Items -------------------------- 
		
		GridBagConstraints gc = new GridBagConstraints();
		
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.anchor = GridBagConstraints.CENTER;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.gridy = 0;
		gc.insets = spacer;
		
		//------------- Aircraft Combobox ------------------------
		gc.gridy++;
		
		gc.weightx = 0.45;
		gc.weighty = 0.5;
		gc.gridx = 0;
		aircraftComboBox = new JComboBox<>();
		aircraftComboBox.setModel(makeComboBox());
		aircraftComboBox.setSelectedIndex(0);
		aircraftComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				pictureArea.setIcon(createPreviewPicture((String)aircraftComboBox.getSelectedItem(), "PreviewPicture.jpg"));
			}
		});
		controlsPanel.add(aircraftComboBox, gc);
		
		//---------------- Picture Area  ------------------------
		
		gc.gridx = 1;
		gc.weightx = 0.5;
		gc.weighty = 0.7;
		gc.gridheight = 2;
		pictureArea = new JLabel(createPreviewPicture((String)aircraftComboBox.getSelectedItem(), "PreviewPicture.jpg"));
		controlsPanel.add(pictureArea, gc);
		
		//------------------ Text Field --------------------------
		gc.gridy++;
		
		gc.gridx = 0;
		gc.weighty = 1;
		gc.weightx = 1;
		gc.gridheight = 1;
		descriptionArea = new JTextArea();
		descriptionArea.setText(createDescriptionText((String)aircraftComboBox.getSelectedItem(), "Description.txt"));
		descriptionArea.setMinimumSize(new Dimension(200, 150));
		descriptionArea.setLineWrap(true);
		descriptionArea.setWrapStyleWord(true);
		descriptionArea.setEditable(false);
		controlsPanel.add(new JScrollPane(descriptionArea), gc);
		
		//----------------- OK Button ----------------------------
		
		okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (aircraftConfigurationListener != null)
					aircraftConfigurationListener.aircraftConfigured((String)aircraftComboBox.getSelectedItem());
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
		Dimension dims = new Dimension(800, 500);
		setSize(dims);
		setMaximumSize(dims);
		setMinimumSize(dims);
		
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
	
	public void setAircraftConfigurationListener(AircraftConfigurationListener aircraftConfigurationListener) {
		this.aircraftConfigurationListener = aircraftConfigurationListener;
	}
}
