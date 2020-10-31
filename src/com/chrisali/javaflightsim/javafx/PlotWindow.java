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
import com.chrisali.javaflightsim.swing.plotting.PlotConfiguration.SubPlotBundle;
import com.chrisali.javaflightsim.swing.plotting.SimulationPlot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.fx.ChartViewer;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PlotWindow {
    
    private static final Logger logger = LogManager.getLogger(PlotWindow.class);

    private Stage stage;
    private TabPane plotTabPane;
    private List<Map<SimOuts, Double>> logsOut;

    /**
     * Constructor that initializes the JavaFX controller and loads the stage from the associated FXML file
     */
    public PlotWindow(String aircraftName, List<Map<SimOuts, Double>> logsOut) {
        this.logsOut = logsOut;

        try {
            Platform.runLater(() -> {
                stage = new Stage();
                stage.setScene(new Scene(createParent(), 900, 600));
                stage.setTitle(aircraftName + " Plots");
                stage.show();

                initializePlots();
            });
        } catch (Exception e) {
            logger.error("Could not load Plot Window", e);
            Dialog.showExceptionDialog(e, "Could not load Plot Window", "Error Loading Plot Window");
        }
    }

    private Parent createParent() {
        VBox vbox = new VBox();

        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        Menu plotsMenu = new Menu("Plots");

        MenuItem refreshItem = new MenuItem("Refresh");
        refreshItem.acceleratorProperty().set(KeyCombination.keyCombination("Ctrl+R"));
        refreshItem.setOnAction(e -> { initializePlots(); });

        MenuItem clearItem = new MenuItem("Clear");
        clearItem.acceleratorProperty().set(KeyCombination.keyCombination("Ctrl+C"));
        clearItem.setOnAction(e -> { clearPlots();; });

        MenuItem closeItem = new MenuItem("Close");
        closeItem.acceleratorProperty().set(KeyCombination.keyCombination("Ctrl+X"));
        closeItem.setOnAction(e -> { hide(); });
        
        fileMenu.getItems().add(closeItem);
        plotsMenu.getItems().add(clearItem);
        plotsMenu.getItems().add(refreshItem);
        menuBar.getMenus().add(fileMenu);
        menuBar.getMenus().add(plotsMenu);
        vbox.getChildren().add(menuBar);

        plotTabPane = new TabPane();
        vbox.getChildren().add(plotTabPane);

        return vbox;
    }

    /**
     * Clears any visible tabs in the plot tab pane, reads the plot configuration and regenerates new tabs based 
     * on that configuration
     */
    private void initializePlots() {
        plotTabPane.getTabs().clear();
        
        Map<String, SubPlotBundle> subPlotBundles = FileUtilities.readPlotConfiguration().getSubPlotBundles();
        
        // Copy to thread-safe ArrayList
        CopyOnWriteArrayList<Map<SimOuts, Double>> cowLogsOut = new CopyOnWriteArrayList<>(logsOut);
        
        try {
            for (Map.Entry<String, SubPlotBundle> entry : subPlotBundles.entrySet()) {
                SimulationPlot plot = new SimulationPlot(cowLogsOut, entry.getValue());

                ChartViewer cv = new ChartViewer(plot.getChart());
                cv.setPrefHeight(6000);
                cv.setPrefWidth(9000);

                Tab tab = new Tab(entry.getKey(), cv);
                tab.setClosable(false);

                plotTabPane.getTabs().add(tab);
            }
        } catch (Exception ex) {
            logger.error("Error encountered when adding plots to tab panel!", ex);
            Dialog.showExceptionDialog(ex, "Error encountered when adding plots to tab panel!", "Plot Window");
        }
    }

    private void clearPlots() {
        plotTabPane.getTabs().clear();
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