package com.chrisali.javaflightsim.plotting;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.chrisali.javaflightsim.controllers.SimulationController;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.integration.SimOuts;

/**
 * Generates a window of JFreeChart plots in tabs containing relevant data from the simulation
 */
public class PlotWindow extends JFrame implements ProgressDialogListener {

	private static final long serialVersionUID = -4197697777449504415L;
	
	private List<Map<SimOuts, Double>> logsOut;
	private Set<String> simPlotCategories;
	
	private JTabbedPane tabPane;
	private List<SimulationPlot> plotList;
	private SwingWorker<Void, Integer> tabPaneWorker;
	private Thread refreshPlotThread;
	private ProgressDialog progressDialog;
	
	private SimulationController controller;
	
	/**
	 * Plots data from the simulation in a Swing window. It loops through 
	 * the {@link PlotWindow#simPlotCategories} set to create {@link SimulationPlot} objects using the data 
	 * from {@link Integrate6DOFEquations#getLogsOut()}, and assigns them to tabs in a JTabbedPane. 
	 * 
	 * @param String simPlotCetegories
	 * @param SimulationController controller
	 */
	public PlotWindow(Set<String> simPlotCategories, SimulationController controller) {
		super(controller.getAircraftBuilder().getAircraft().getName() + " Plots");
		setLayout(new BorderLayout());
		
		this.logsOut = controller.getLogsOut();
		this.simPlotCategories = simPlotCategories;
		this.controller = controller;
		plotList = new ArrayList<>();
		
		//-------------- Progress Dialog ----------------------------
		
		progressDialog = new ProgressDialog(this, "Refreshing Plots");
		progressDialog.setProgressDialogListener(this);

		//------------------ Tab Pane ------------------------------
		
		tabPane = new JTabbedPane();
		
		if (this.logsOut != null && this.simPlotCategories != null)
			initializePlots(logsOut);
		
		tabPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				PlotWindow.this.setSize(tabPane.getSelectedComponent().getPreferredSize());
			}
		});
		add(tabPane, BorderLayout.CENTER);
		

		//================== Window Settings ====================================
		
		setJMenuBar(createMenuBar());
		
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				setVisible(false);
				
				if (refreshPlotThread != null)
					refreshPlotThread.interrupt();
				if (tabPaneWorker != null)
					tabPaneWorker.cancel(true);
			}
		});
		
		//setSize(tabPane.getSelectedComponent().getPreferredSize());
		setVisible(true);
	}
	
	private JMenuBar createMenuBar() {

		//+++++++++++++++++++++++++ File Menu ++++++++++++++++++++++++++++++++++++++++++
		
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		
		//----------------------- Close Item -------------------------------
		
		JMenuItem closeItem = new JMenuItem("Close");
		closeItem.setMnemonic(KeyEvent.VK_C);
		closeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		closeItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PlotWindow.this.setVisible(false);
			}
		});
		fileMenu.add(closeItem);
		
		//+++++++++++++++++++++++++ Plots Menu ++++++++++++++++++++++++++++++++++++++++++
		
		JMenu plotsMenu = new JMenu("Plots");
		plotsMenu.setMnemonic(KeyEvent.VK_P);

		//------------------- Refresh Item -------------------------------
		
		JMenuItem refreshItem = new JMenuItem("Refresh");
		refreshItem.setMnemonic(KeyEvent.VK_R);
		refreshItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		refreshItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				controller.plotSimulation();
			}
		});
		plotsMenu.add(refreshItem);
		
		//---------------- Clear Pots Item -------------------------------
		
		JMenuItem clearPlotsItem = new JMenuItem("Clear Plots");
		clearPlotsItem.setMnemonic(KeyEvent.VK_E);
		clearPlotsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		clearPlotsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				controller.clearLogsOut();
				controller.plotSimulation();
			}
		});
		plotsMenu.add(clearPlotsItem);
		
		//===========================================================================
		//                              Menu Bar
		//===========================================================================
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		menuBar.add(plotsMenu);
		
		return menuBar;
	}
	
	/**
	 * Initalizes the plot window by generating plot objects and adding them to a tabbed pane 
	 */
	private void initializePlots(List<Map<SimOuts, Double>> logsOut) {
		progressDialog.setMaximum(simPlotCategories.size());
		progressDialog.setTitle("Generating Plots");
		progressDialog.setVisible(true);
		
		tabPaneWorker = new SwingWorker<Void, Integer>() {
			
			@Override
			protected void done() {
				progressDialog.setVisible(false);
				
				if (isCancelled())
					return;
				
				if (!isVisible())
					setVisible(true);
			}
			
			@Override
			protected void process(List<Integer> counts) {
				int retreived = counts.get(counts.size()-1);
				progressDialog.setValue(retreived);
			}
			
			@Override
			protected Void doInBackground() throws Exception {
				
				try {
					int count = 0;
					
					// Pause a bit to give SimulationPlot object time to initialize 
					Thread.sleep(2000);
					
					for (String plotTitle : simPlotCategories) {
					
						SimulationPlot plotObject = new SimulationPlot(logsOut, plotTitle);
						
						Thread.sleep(250);

						tabPane.add(plotTitle, plotObject);
						plotList.add(plotObject);
						
						count++;
						publish(count);
					}
						
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				
				return null;
			}
		};
		tabPaneWorker.execute();
	}
	
	/**
	 * Commands each plot in the plotList ArrayList to update its XYSeries values using data from logsOut. 
	 * This will also set the plot window visible again if it has been closed.
	 * 
	 * @param logsOut
	 */
	public void refreshPlots(List<Map<SimOuts, Double>> logsOut) {
		progressDialog.setMaximum(simPlotCategories.size());
		progressDialog.setTitle("Refreshing Plots");
		progressDialog.setVisible(true);
		
		// Run process in Thread instead of SwingWorker to allow re-execution
		refreshPlotThread = new Thread(){
			@Override
			public void run() {
				try {
					int count = 0;
					
					SimulationPlot.updateXYSeriesData(logsOut);
					
					for (SimulationPlot plot : plotList) {
						plot.getChartPanel().repaint();
						
						Thread.sleep(125);
						progressDialog.setValue(count);
						
						count++;
					}
				} 
				catch (InterruptedException e) {} 
				finally {progressDialog.setVisible(false);}
			}
		};
		
		refreshPlotThread.start();
	}
	
	@Override
	public void ProgressDialogCancelled() {
		refreshPlotThread.interrupt();
	}
}
