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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.chrisali.javaflightsim.lwjgl.utilities.OTWDirectories;
import com.chrisali.javaflightsim.simulation.integration.SimOuts;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ConsoleTable {
    
    private static final Logger logger = LogManager.getLogger(MainMenu.class);

    private ConsoleTableController controller;
    private Stage stage;

    /**
     * Constructor that initializes the JavaFX controller and loads the stage from the associated FXML file
     */
    public ConsoleTable(List<Map<SimOuts, Double>> logsOut) {
        controller = new ConsoleTableController();
        
        String fxmlName = "ConsoleTable.fxml";
        
        try {
            controller.initializeDataTable(logsOut);

            FXMLLoader loader = new FXMLLoader();
            loader.setController(controller);
            FileInputStream fis = new FileInputStream(OTWDirectories.RESOURCES.toString() + File.separator + fxmlName);
            Parent parent = loader.load(fis);
    
            stage = new Stage();
            stage.setScene(new Scene(parent));
            stage.setTitle("Raw Data Output");
            stage.show();
        } catch (IOException e) {
            logger.error("Could not load FXML: " + fxmlName, e);
            Dialog.showExceptionDialog(e, "Could not load FXML: " + fxmlName, "Unable to find FXML");
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