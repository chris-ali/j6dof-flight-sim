package com.chrisali.javaflightsim.menus;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import com.chrisali.javaflightsim.controllers.SimulationController;
import com.chrisali.javaflightsim.datatransfer.FlightDataListener;
import com.chrisali.javaflightsim.instrumentpanel.ClosePanelListener;
import com.chrisali.javaflightsim.instrumentpanel.InstrumentPanel;
import com.chrisali.javaflightsim.menus.optionspanel.DisplayOptions;
import com.chrisali.javaflightsim.otw.RunWorld;
import com.chrisali.javaflightsim.simulation.setup.Options;

/**
 * JPanel that integrates {@link InstrumentPanel} and the OpenGL OTW view from {@link RunWorld},
 * producing a display similar to a traditional flight simulator program; uses addNotify() and removeNodity()
 * stop OTW thread on this thread to destroy OpenGL display correctly when window is closed. The solution is shown here:
 * <p>http://stackoverflow.com/questions/26199534/how-to-attach-opengl-display-to-a-jframe-and-dispose-of-it-properly</p>
 * 
 * @author Christopher Ali
 *
 */
public class SimulationWindow extends JFrame {

	private static final long serialVersionUID = 7290660958478331031L;
	
	private InstrumentPanel instrumentPanel;
	private Canvas outTheWindowCanvas;
	
	private ClosePanelListener closePanelListener;
	
	/**
	 * Constructor for simulation window; takes {@link SimulationController} argument to gain access to
	 * starting and stopping threads for {@link RunWorld} on this thread
	 * 
	 * @param controller
	 */
	public SimulationWindow(SimulationController controller) {
		super("Java Flight Simulator");
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints gc = new GridBagConstraints();
		
		gc.fill    = GridBagConstraints.BOTH;
		gc.gridy   = 0;
		gc.weighty = 100;
		gc.weightx = 100;
		
		//---------------------- Out the Window Canvas -------------------------------------------
		
		outTheWindowCanvas = new Canvas() {

			private static final long serialVersionUID = 6438710048789252704L;

			@Override
			public void addNotify() {
				super.addNotify();
				controller.startOTWThread();
			}

			@Override
			public void removeNotify() {
				controller.stopOTWThread();
				super.removeNotify();
			}	
		};
		
		add(outTheWindowCanvas, gc);
		
		//------------------------- Instrument Panel ---------------------------------------------
		
		gc.gridy++;

		instrumentPanel = new InstrumentPanel();

		if (controller.getSimulationOptions().contains(Options.INSTRUMENT_PANEL))
			add(instrumentPanel, gc);
	
		//========================== Window Settings =============================================

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (closePanelListener != null) {
					closePanelListener.panelWindowClosed();
					dispose(); // calls removeNotify(), which gracefully stops OTW thread and OpenGL display
				}
			}
		});
		
		Dimension windowSize = new Dimension(controller.getDisplayOptions().get(DisplayOptions.DISPLAY_WIDTH), 
											 controller.getDisplayOptions().get(DisplayOptions.DISPLAY_HEIGHT));
		setSize(windowSize.width, windowSize.height);
		setResizable(false);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}
	
	/**
	 * @return {@link InstrumentPanel} object to set {@link FlightDataListener} to the instrument panel
	 * 	in {@link MainFrame}
	 */
	public InstrumentPanel getInstrumentPanel() {
		return instrumentPanel;
	}
	
	/**
	 * @return {@link Canvas} object used to render the {@link RunWorld} out the window display 
	 */
	public Canvas getOutTheWindowCanvas() {
		return outTheWindowCanvas;
	}

	/**
	 * Sets a listener to monitor for a window closing event so that the simulation can stop
	 * 
	 * @param closePanelListener
	 */
	public void setClosePanelListener(ClosePanelListener closePanelListener) {
		this.closePanelListener = closePanelListener;
	}
}
