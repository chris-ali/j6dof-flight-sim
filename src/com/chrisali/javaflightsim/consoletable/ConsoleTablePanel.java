package com.chrisali.javaflightsim.consoletable;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;

import com.chrisali.javaflightsim.controllers.SimulationController;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;

public class ConsoleTablePanel extends JFrame {

	private static final long serialVersionUID = 555700867777925736L;
	
	private JTable table;
	private ConsoleTableModel consoleTableModel;
	private SimulationController simController;
	private SwingWorker<Void,Integer> tableRefreshWorker;
	
	public ConsoleTablePanel(SimulationController controller) {
		super("Raw Data Output");
		
		this.simController = controller;
		setLayout(new BorderLayout());
		
		//-------------- Table Panel ------------------------
		
		consoleTableModel = new ConsoleTableModel();
		consoleTableModel.setData(simController.getLogsOut());
		table = new JTable(consoleTableModel);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setColumnSelectionAllowed(true);
		table.setRowSelectionAllowed(true);
		tableRefreshWorker = new SwingWorker<Void, Integer>() {
			@Override
			protected void done() {
				//if (!simController.getSimulation().isRunning())
				//	ConsoleTablePanel.this.setVisible(false);
			}

			@Override
			protected Void doInBackground() throws Exception {
				while (Integrate6DOFEquations.isRunning()) {
					consoleTableModel.fireTableDataChanged();
					Thread.sleep(50);
				}
				return null;
			}
		};
		add(new JScrollPane(table), BorderLayout.CENTER);
		
		//=================== Window Settings =======================
		
		setJMenuBar(createMenuBar());
		
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				setVisible(false);
			}
		});
		
		Dimension dims = new Dimension(1000, 800);
		setSize(dims);
		setMinimumSize(dims);
		setVisible(true);
	}
	
	private JMenuBar createMenuBar() {

		//+++++++++++++++++++++++++ File Menu ++++++++++++++++++++++++++++++++++++++++++
		
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		
		//------------------- File Chooser -------------------------------
		
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.addChoosableFileFilter(new CSVFileFilter());
		
		//------------------- Export Item -------------------------------
		
		JMenuItem exportItem = new JMenuItem("Export as CSV...");
		exportItem.setMnemonic(KeyEvent.VK_E);
		exportItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		exportItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				if (fileChooser.showSaveDialog(ConsoleTablePanel.this) == JFileChooser.APPROVE_OPTION) {
					try {
						simController.saveConsoleOutput(fileChooser.getSelectedFile());
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(ConsoleTablePanel.this, 
								"Could not save data to file", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		fileMenu.add(exportItem);
		
		//----------------------- Exit Item -------------------------------
		
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.setMnemonic(KeyEvent.VK_X);
		exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ConsoleTablePanel.this.setVisible(false);
			}
		});
		fileMenu.add(exitItem);
		
		//===========================================================================
		//                              Menu Bar
		//===========================================================================
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		
		return menuBar;
	}
	
	public void startTableRefresh() {
		tableRefreshWorker.execute();
	}

}
