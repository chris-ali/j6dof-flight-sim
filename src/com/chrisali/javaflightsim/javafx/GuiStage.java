package com.chrisali.javaflightsim.javafx;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.chrisali.javaflightsim.lwjgl.utilities.OTWDirectories;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GuiStage extends Application {

    private static final Logger logger = LogManager.getLogger(GuiStage.class);

    @Override
    public void start(Stage primaryStage) {
        String fxmlName = "ConsoleTable.fxml";

        try {
            FXMLLoader loader = new FXMLLoader();
            FileInputStream fis = new FileInputStream(OTWDirectories.RESOURCES.toString() + File.separator + fxmlName);
            
            primaryStage.setScene(new Scene(loader.load(fis)));
            primaryStage.show();
        } catch (IOException e) {
            logger.error("Could not find FXML: " + fxmlName, e);
        }
    }
    
    public void showConsoleTable() {
        String fxmlName = "ConsoleTable.fxml";
        
        try {
            FXMLLoader loader = new FXMLLoader();
            FileInputStream fis = new FileInputStream(OTWDirectories.RESOURCES.toString() + File.separator + fxmlName);
            
            Stage consoleTable = new Stage();
            consoleTable.setScene(new Scene(loader.load(fis)));
            consoleTable.show();
        } catch (IOException e) {
            logger.error("Could not find FXML: " + fxmlName, e);
        }
    }

    public void showPlotWindow() {
        String fxmlName = "PlotWindow.fxml";
        
        try {
            FXMLLoader loader = new FXMLLoader();
            FileInputStream fis = new FileInputStream(OTWDirectories.RESOURCES.toString() + File.separator + fxmlName);
            
            Stage plotWindow = new Stage();
            plotWindow.setScene(new Scene(loader.load(fis)));
            plotWindow.show();
        } catch (IOException e) {
            logger.error("Could not find FXML: " + fxmlName, e);
        }
    }
}
