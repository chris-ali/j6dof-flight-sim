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

import javax.swing.SwingUtilities;

import com.chrisali.javaflightsim.simulation.integration.SimOuts;
import com.chrisali.javaflightsim.simulation.utilities.FileUtilities;
import com.chrisali.javaflightsim.swing.consoletable.ConsoleTableComponent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

public class ConsoleTable {
    
    private static final Logger logger = LogManager.getLogger(MainMenu.class);

    private Stage stage;

    public ConsoleTable(List<Map<SimOuts, Double>> logsOut) {
        try {
            stage = new Stage();
            stage.setScene(new Scene(createParent(logsOut), 900, 600));
            stage.setTitle("Raw Data Output");
            stage.show();
        } catch (Exception e) {
            logger.error("Could not load Console Table: ", e);
            Dialog.showExceptionDialog(e, "Could not load Console Table: ", "Error Loading Console Table");
        }
    }

    private Parent createParent(List<Map<SimOuts, Double>> logsOut) {
        VBox vbox = new VBox();

        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");

        MenuItem exportItem = new MenuItem("Export to CSV...");
        exportItem.acceleratorProperty().set(KeyCombination.keyCombination("Ctrl+E"));
        exportItem.setOnAction(e -> { exportDataTable(logsOut); });

        MenuItem closeItem = new MenuItem("Close");
        closeItem.acceleratorProperty().set(KeyCombination.keyCombination("Ctrl+X"));
        closeItem.setOnAction(e -> { hide(); });
        
        fileMenu.getItems().add(exportItem);
        fileMenu.getItems().add(closeItem);
        menuBar.getMenus().add(fileMenu);
        vbox.getChildren().add(menuBar);

        SwingNode swingNode = new SwingNode();
        
        SwingUtilities.invokeLater(() -> {
            swingNode.setContent(new ConsoleTableComponent(logsOut));
        });
        
        vbox.getChildren().add(swingNode);

        return vbox;
    }

    private void exportDataTable(List<Map<SimOuts, Double>> logsOut) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Raw Data to CSV");
        fileChooser.getExtensionFilters().add(new ExtensionFilter(".csv (Comma separated values) File", "*.csv"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        File file = fileChooser.showSaveDialog(stage);
        if (file == null)
            return;

        try {
            FileUtilities.saveToCSVFile(file, logsOut);
            Dialog.showDialog("Console output successfully exported to CSV to: " + file.getAbsolutePath(), "Exported to CSV", AlertType.INFORMATION);
        } catch (IOException e) {
            logger.error("Unable to save CSV file!", e);
            Dialog.showExceptionDialog(e, "An error was encountered while saving console output to CSV!", "Unable to save to CSV");
        }
    }

    /**
     * Hides the stage containing this window
     */
    public void hide() {
        Platform.runLater(() -> {
            if (stage != null)
                stage.close();
        });
    }

    /**
     * Shows the stage containing this window
     */
    public void show() {
        Platform.runLater(() -> {
            if (stage != null)
                stage.show();
        });
    }
    
    /**
     * @return if the stage is visible
     */
    public boolean isVisible() {
        if (stage != null)
            return stage.isShowing();
        else 
            return false;
    }
}