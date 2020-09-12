package com.chrisali.javaflightsim.javafx;

import com.chrisali.javaflightsim.initializer.LWJGLJavaFXSimulationController;
import com.chrisali.javaflightsim.simulation.setup.SimulationConfiguration;
import com.chrisali.javaflightsim.simulation.utilities.FileUtilities;

import javafx.application.Application;
import javafx.stage.Stage;

public class PrimaryStage extends Application {

    private SimulationConfiguration configuration;

    public PrimaryStage() {
        configuration = FileUtilities.readSimulationConfiguration();
    }

    public void launchPrimaryStage(String[] args) { 
        launch(args); 
    }

    @Override
    public void start(Stage primaryStage) {
        new LWJGLJavaFXSimulationController(configuration);
        
        primaryStage.setHeight(1);
        primaryStage.setWidth(1);
        primaryStage.setX(1);
        primaryStage.setY(1);
        primaryStage.show();
    }
}