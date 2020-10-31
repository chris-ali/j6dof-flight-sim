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
import java.util.Optional;

import com.chrisali.javaflightsim.initializer.LWJGLJavaFXSimulationController;
import com.chrisali.javaflightsim.initializer.PomReader;
import com.chrisali.javaflightsim.lwjgl.utilities.OTWDirectories;
import com.chrisali.javaflightsim.simulation.setup.SimulationConfiguration;
import com.chrisali.javaflightsim.simulation.utilities.FileUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainMenu extends Application {

    private static final Logger logger = LogManager.getLogger(MainMenu.class);

    public void launchMenus(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        SimulationConfiguration configuration = FileUtilities.readSimulationConfiguration();
        LWJGLJavaFXSimulationController simulationController = new LWJGLJavaFXSimulationController(configuration);
        
        MainMenuController mainMenuController = new MainMenuController(configuration);
        mainMenuController.addSimulationEventListener(simulationController);
        
        String fxmlName = "MainMenu.fxml";
    
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setController(mainMenuController);
            FileInputStream fis = new FileInputStream(OTWDirectories.RESOURCES.toString() + File.separator + fxmlName);
            
            primaryStage = new Stage();
            primaryStage.setTitle(PomReader.getProjectName());
            primaryStage.setScene(new Scene(loader.load(fis)));
            primaryStage.setOnCloseRequest(event -> { closeWindowEvent(event); });
            primaryStage.show();
        } catch (IOException e) {
            logger.error("Could not load FXML: " + fxmlName, e);
            Dialog.showExceptionDialog(e, "Could not load FXML: " + fxmlName, "Error Loading FXML");
        }
    }

    private void closeWindowEvent(WindowEvent event) {
        String projectName = PomReader.getProjectName();
        
        Optional<ButtonType> result = Dialog.showDialog("Are you sure you wish to close " + projectName + "?", 
            "Close " + projectName, AlertType.CONFIRMATION);

        if(result.get().equals(ButtonType.OK)) {
            logger.info("Closing " + projectName + "...");
            System.exit(1);
        }
        else {
            event.consume();
        }
    }
}
