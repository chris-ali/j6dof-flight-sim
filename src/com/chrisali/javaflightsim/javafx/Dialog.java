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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * Contains static methods to display various type of JavaFX dialogs
 */
public class Dialog {
    /**
     * Displays a dialog with behavior and properties defined by the alertType argument
     * 
     * @param contentText body text of dialog
     * @param dialogTitle title of dialog
     * @param alertType type of dialog to display
     * 
     * @return Optional that contains a ButtonType result if alertType is AlertType.CONFIRMATION 
     */
    public static Optional<ButtonType> showDialog(String contentText, String dialogTitle, AlertType dialogType) {
        Alert alert = new Alert(dialogType);
        alert.setTitle(dialogTitle);
        alert.setHeaderText(null);
        alert.setContentText(contentText);
        
        return alert.showAndWait();
    }

    /**
     * Displays an exception dialog containing a text area of the exception's stack trace
     * 
     * @param ex encountered exception
     * @param contentText body text of dialog
     * @param dialogTitle title of dialog
     */
    public static void showExceptionDialog(Exception ex, String contentText, String dialogTitle) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(dialogTitle);
        alert.setHeaderText(null);
        alert.setContentText(contentText);

        // Create expandable Exception
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("Exception stack trace:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane
        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
    }
}