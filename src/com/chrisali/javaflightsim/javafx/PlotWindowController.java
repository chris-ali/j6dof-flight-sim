package com.chrisali.javaflightsim.javafx;

import java.util.List;
import java.util.Map;

import com.chrisali.javaflightsim.simulation.integration.SimOuts;
import com.chrisali.javaflightsim.simulation.utilities.FileUtilities;
import com.chrisali.javaflightsim.swing.plotting.PlotConfiguration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class PlotWindowController {

    private static final Logger logger = LogManager.getLogger(ConsoleTableController.class);

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

    }

    @FXML
    void initialize() {
        assert plotTabPane != null : "fx:id=\"plotTabPane\" was not injected: check your FXML file 'PlotWindow.fxml'.";
    }

    public PlotWindowController(List<Map<SimOuts, Double>> logsOut) {
        this.logsOut = logsOut;
		plotConfiguration = FileUtilities.readPlotConfiguration();
    }
}