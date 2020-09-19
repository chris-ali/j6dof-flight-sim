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
package com.chrisali.javaflightsim.javafx;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.chrisali.javaflightsim.simulation.integration.SimOuts;
import com.chrisali.javaflightsim.simulation.utilities.FileUtilities;
import com.chrisali.javaflightsim.swing.plotting.PlotConfiguration;
import com.chrisali.javaflightsim.swing.plotting.SimulationPlot;
import com.chrisali.javaflightsim.swing.plotting.PlotConfiguration.SubPlotBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.fx.ChartViewer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class PlotWindowController {

    private static final Logger logger = LogManager.getLogger(PlotWindowController.class);

    private PlotConfiguration plotConfiguration;
	private List<Map<SimOuts, Double>> logsOut;

    @FXML
    private TabPane plotTabPane;

    @FXML
    void clearPlots(ActionEvent event) {
        plotTabPane.getTabs().clear();
    }

    @FXML
    void closeWindow(ActionEvent event) {
        Stage stage = (Stage)plotTabPane.getScene().getWindow();
        
        if (stage != null)
            stage.close();
    }

    @FXML
    void refreshPlots(ActionEvent event) {
        initialize();
    }

    @FXML
    void initialize() {
        assert plotTabPane != null : "fx:id=\"plotTabPane\" was not injected: check your FXML file 'PlotWindow.fxml'.";

        plotTabPane.getTabs().clear();
        
        Map<String, SubPlotBundle> subPlotBundles = plotConfiguration.getSubPlotBundles();
        
        // Copy to thread-safe ArrayList
        CopyOnWriteArrayList<Map<SimOuts, Double>> cowLogsOut = new CopyOnWriteArrayList<>(logsOut);
        
        try {
            for (Map.Entry<String, SubPlotBundle> entry : subPlotBundles.entrySet()) {
                SimulationPlot plot = new SimulationPlot(cowLogsOut, entry.getValue());
                ChartViewer cv = new ChartViewer(plot.getChart());
                Tab tab = new Tab(entry.getKey(), cv);
                plotTabPane.getTabs().add(tab);
            }
        } catch (Exception ex) {
            logger.error("Error encountered when adding plots to tab panel!", ex);
            Dialog.showExceptionDialog(ex, "Error encountered when adding plots to tab panel!", "Plot Window");
        }
    }

    public PlotWindowController(List<Map<SimOuts, Double>> logsOut) {
        this.logsOut = logsOut;
		plotConfiguration = FileUtilities.readPlotConfiguration();
    }
}