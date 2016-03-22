package com.chrisali.javaflightsim.plotting;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
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

import org.jfree.ui.RefineryUtilities;

import com.chrisali.javaflightsim.simulation.aircraft.Aircraft;
import com.chrisali.javaflightsim.simulation.integration.Integrate6DOFEquations;
import com.chrisali.javaflightsim.simulation.integration.SimOuts;

/**
 * Generates a window of JFreeChart plots in tabs containing relevant data from the simulation
 */
public class PlotWindow extends JFrame {

	private static final long serialVersionUID = -4197697777449504415L;
	
	private JTabbedPane tabPane;
	private List<SimulationPlot> plotList;
	private PlotCloseListener plotCloseListener;
	private SwingWorker<Void, Integer> tabPaneWorker;
	private SwingWorker<Void, Integer> refreshPlotWorker;
	//private ProgressDialog progressDialog;
	
	/**
	 * Plots data from the simulation in a Swing window. It loops through 
	 * the {@link PlotWindow#simPlotCategories} set to create {@link SimulationPlot} objects using the data 
	 * from {@link Integrate6DOFEquations#getLogsOut()}, and assigns them to tabs in a JTabbedPane. 
	 * 
	 * @param String simPlotCetegories
	 * @param List<EnumMap<SimOuts, Double>> logsOut
	 * @param Aircraft aircraft
	 */
	public PlotWindow(List<EnumMap<SimOuts, Double>> logsOut, 
					 Set<String> simPlotCategories,
					 Aircraft aircraft) {
		super(aircraft.getName() + " Plots");
		setLayout(new BorderLayout());
		
		plotList = new ArrayList<>();
		
		//------------------ Tab Pane ------------------------------
		
		tabPane = new JTabbedPane();
		tabPaneWorker = new SwingWorker<Void, Integer>() {
			@Override
			protected Void doInBackground() throws Exception {
				for (String plotTitle : simPlotCategories) {
					try {Thread.sleep(125);} 
					catch (InterruptedException e) {}
					
					SimulationPlot plotObject = new SimulationPlot(new ArrayList<EnumMap<SimOuts, Double>>(logsOut), plotTitle);
					tabPane.add(plotTitle, plotObject);
					plotList.add(plotObject);
				}
				return null;
			}
		};
		tabPaneWorker.execute();
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
				
				if (plotCloseListener != null)
					plotCloseListener.plotWindowClosed();
			}
		});
		
		//setSize(tabPane.getSelectedComponent().getPreferredSize());
		RefineryUtilities.centerFrameOnScreen(this);
		setVisible(true);
		
	}
	
	private JMenuBar createMenuBar() {

		//+++++++++++++++++++++++++ File Menu ++++++++++++++++++++++++++++++++++++++++++
		
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		
		//------------------- Refresh Item -------------------------------
		
		JMenuItem refreshItem = new JMenuItem("Refresh");
		refreshItem.setMnemonic(KeyEvent.VK_R);
		refreshItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		refreshItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				System.out.println("Refresh Plots");
				//refreshPlots();
			}
		});
		fileMenu.add(refreshItem);
		
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
		
		//===========================================================================
		//                              Menu Bar
		//===========================================================================
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		
		return menuBar;
	}
	
	/**
	 * Commands each plot in the plotList ArrayList to update its XYSeries values using data from logsOut. 
	 * This will also set the plot window visible again if it has been closed.
	 * 
	 * @param logsOut
	 */
	public void refreshPlots(List<EnumMap<SimOuts, Double>> logsOut) {
		refreshPlotWorker = new SwingWorker<Void, Integer>() {
			@Override
			protected void done() {
				if (!isVisible())
					setVisible(true);
			}

			@Override
			protected Void doInBackground() throws Exception {
				for (SimulationPlot plot : plotList) {
					plot.updateXYSeriesData(new ArrayList<EnumMap<SimOuts, Double>>(logsOut));
					plot.getChartPanel().repaint();
				}
				return null;
			}
		};
		refreshPlotWorker.execute();
	}
	
	public void setPlotCloseListener (PlotCloseListener plotCloseListener) {
		this.plotCloseListener = plotCloseListener;
	}
}
