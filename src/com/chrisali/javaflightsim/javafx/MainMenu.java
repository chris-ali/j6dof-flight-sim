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

import com.chrisali.javaflightsim.initializer.PomReader;
import com.chrisali.javaflightsim.lwjgl.utilities.OTWDirectories;
import com.chrisali.javaflightsim.simulation.flightcontrols.SimulationEventListener;
import com.chrisali.javaflightsim.simulation.setup.SimulationConfiguration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainMenu {

    private static final Logger logger = LogManager.getLogger(MainMenu.class);

    private MainMenuController mainMenuController;

    /**
     * Constructor that initializes the JavaFX controller(s)
     */
    public MainMenu(SimulationConfiguration configuration) {
        mainMenuController = new MainMenuController(configuration);
        
        String fxmlName = "MainMenu.fxml";
    
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setController(mainMenuController);
            FileInputStream fis = new FileInputStream(OTWDirectories.RESOURCES.toString() + File.separator + fxmlName);
            
            Stage stage = new Stage();
            stage.setTitle(PomReader.getProjectName());
            stage.setScene(new Scene(loader.load(fis)));
            stage.show();
        } catch (IOException e) {
            logger.error("Could not find FXML: " + fxmlName, e);
        }
    }

    public void addMainMenuSimulationEventListener(SimulationEventListener listener) {
        if (mainMenuController != null) {
            mainMenuController.addSimulationEventListener(listener);
        }
    }
}
