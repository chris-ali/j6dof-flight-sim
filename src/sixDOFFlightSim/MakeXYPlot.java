package sixDOFFlightSim;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.*;

import java.io.File;
import java.io.IOException;

public class MakeXYPlot extends JFrame {

	private static final long serialVersionUID = 1L;
	
	// Constructor for plot object
	public MakeXYPlot(double[] t, double[] y, String applicationTitle) {
		super(applicationTitle);
		
		XYSeries plotData = new XYSeries("XY Plot 1");
		for (int i=0; i<t.length; i++) {
			plotData.add(t[i], y[i]);
		}
		
		// Create XY Series Collection from plotData  
		XYSeriesCollection series1 = new XYSeriesCollection(plotData);
		
		// Create XY Chart with Chart Factory with Series Collection
		JFreeChart xyPlot = ChartFactory.createXYLineChart(applicationTitle, "x", "t", series1); 
		
		// Create Chart Panel to populate AWT window 
		ChartPanel chartPanel = new ChartPanel(xyPlot);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(chartPanel);
	}
	
	// Save plot as a jpg file in project folder
	public static void savePlotJPG(JFreeChart xyPlot)  throws IOException  {
		File plotPic = new File("xyPlot.jpeg");
		ChartUtilities.saveChartAsJPEG(plotPic, xyPlot, 640, 480);
	}

}
