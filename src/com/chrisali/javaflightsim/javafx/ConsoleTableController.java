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

import com.chrisali.javaflightsim.simulation.integration.SimOuts;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class ConsoleTableController {

    @FXML
    private TableView<List<Map<SimOuts,Double>>> rawDataTable;

    @FXML
    void closeWindow(ActionEvent event) {
        Stage stage = (Stage)rawDataTable.getScene().getWindow();
        
        if (stage != null)
            stage.close();
    }

    @FXML
    void exportDataTable(ActionEvent event) {

    }

    @FXML
    void initialize() {
        assert rawDataTable != null : "fx:id=\"rawDataTable\" was not injected: check your FXML file 'ConsoleTable.fxml'.";

    }
}
