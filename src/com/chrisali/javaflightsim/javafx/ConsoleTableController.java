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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.chrisali.javaflightsim.simulation.integration.SimOuts;
import com.chrisali.javaflightsim.simulation.utilities.FileUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

public class ConsoleTableController {

    private static final Logger logger = LogManager.getLogger(ConsoleTableController.class);

    private List<Map<SimOuts,Double>> logsOut;

    @FXML
    private TableView<Map<SimOuts,Double>> rawDataTable;

    @FXML
    void closeWindow(ActionEvent event) {
        Stage stage = (Stage)rawDataTable.getScene().getWindow();
        
        if (stage != null)
            stage.close();
    }

    @FXML
    void exportDataTable(ActionEvent event) {
        Stage stage = (Stage)rawDataTable.getScene().getWindow();
        if (stage == null)
            return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Raw Data to CSV");
        fileChooser.setSelectedExtensionFilter(new ExtensionFilter(".csv (Comma separated values) File", ".csv"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                FileUtilities.saveToCSVFile(file, rawDataTable.getItems());
                Dialog.showDialog("Console output successfully exported to CSV to: " + file.getAbsolutePath(), "Exported to CSV", AlertType.INFORMATION);
            } catch (IOException e) {
                logger.error("Unable to save CSV file!", e);
                Dialog.showExceptionDialog(e, "An error was encountered while saving console output to CSV!", "Unable to save to CSV");
            }
        }
    }

    @FXML
    void initialize() {
        assert rawDataTable != null : "fx:id=\"rawDataTable\" was not injected: check your FXML file 'ConsoleTable.fxml'.";

        rawDataTable.setItems(FXCollections.observableArrayList(logsOut));

        for (SimOuts simout : SimOuts.values()) {
            rawDataTable.getColumns().add(new TableColumn<>(simout.toString()));
        }
    }

    /**
     * Sets up the console table to accept raw simulation outputs as an ObservableList
     * 
     * @param logsOut
     */
    public void initializeDataTable(List<Map<SimOuts,Double>> logsOut) {
        this.logsOut = logsOut;
    }
}
