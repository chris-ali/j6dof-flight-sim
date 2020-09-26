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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.chrisali.javaflightsim.initializer.PomReader;
import com.chrisali.javaflightsim.simulation.aircraft.Aircraft;
import com.chrisali.javaflightsim.simulation.aircraft.MassProperties;
import com.chrisali.javaflightsim.simulation.datatransfer.SimulationEventListener;
import com.chrisali.javaflightsim.simulation.setup.CameraMode;
import com.chrisali.javaflightsim.simulation.setup.InitialConditions;
import com.chrisali.javaflightsim.simulation.setup.Options;
import com.chrisali.javaflightsim.simulation.setup.SimulationConfiguration;
import com.chrisali.javaflightsim.simulation.utilities.FileUtilities;
import com.chrisali.javaflightsim.simulation.utilities.SimDirectories;
import com.chrisali.javaflightsim.simulation.utilities.SimFiles;
import com.chrisali.javaflightsim.simulation.utilities.SixDOFUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;

public class MainMenuController {

    private static final Logger logger = LogManager.getLogger(MainMenuController.class);

    private Aircraft aircraft;
    private SimulationConfiguration configuration;

    private DecimalFormat df0 = new DecimalFormat("#");
    private DecimalFormat df4 = new DecimalFormat("#.####");

    private List<SimulationEventListener> simulationEventListeners = new ArrayList<>();

    @FXML
    private ComboBox<String> dropDownAircraft;

    @FXML
    private ImageView imageAircraft;

    @FXML
    private TextArea textDescription;

    @FXML
    private Slider sliderPayloadWeight;

    @FXML
    private Slider sliderFuelWeight;

    @FXML
    private Text textEmptyWeight;

    @FXML
    private Text textFuelWeight;

    @FXML
    private Text textPayloadWeight;

    @FXML
    private Text textTotalWeight;

    @FXML
    private TextField textInitialLat;

    @FXML
    private TextField textInitialLon;

    @FXML
    private TextField textInitialAlt;

    @FXML
    private TextField textInitialHdg;

    @FXML
    private TextField textInitialTas;

    @FXML
    private CheckBox checkAnalysisMode;

    @FXML
    private CheckBox checkShowConsole;

    @FXML
    private CheckBox checkDebugMode;

    @FXML
    private Slider sliderSimulationRate;

    @FXML
    private CheckBox checkAntiAliasing;

    @FXML
    private CheckBox checkAnisoFiltering;

    @FXML
    private CheckBox checkFullScreen;

    @FXML
    private RadioButton radio800450;

    @FXML
    private ToggleGroup resolutionGroup;

    @FXML
    private RadioButton radio1440900;

    @FXML
    private RadioButton radio16801050;

    @FXML
    private RadioButton radio19201080;

    @FXML
    private Slider sliderEngineVol;

    @FXML
    private Slider sliderSystemsVol;

    @FXML
    private Slider sliderEnvironmentVol;

    @FXML
    private CheckBox checkShowPanel;

    @FXML
    private Slider sliderFieldOfView;

    @FXML
    private RadioButton radioChase;

    @FXML
    private ToggleGroup cameraGroup;

    @FXML
    private RadioButton radio2dCockpit;

    @FXML
    private WebView webviewSummary;

    @FXML
    private Button buttonStartSim;

    @FXML
    private Text textVersion;

    @FXML
    void buttonStartSimClicked(ActionEvent event) {
        simulationEventListeners.forEach(listener -> {
            if (!listener.onStartSimulation())
                Dialog.showDialog("Simulation is already running! Please wait until it has finished", "Simulation Running", AlertType.WARNING);
        });
    }

    @FXML
    void checkAnalysisModeClicked(ActionEvent event) {
        updateConfigurationAndSave();
    }

    @FXML
    void checkAnisoFilteringClicked(ActionEvent event) {
        updateConfigurationAndSave();
    }

    @FXML
    void checkAntiAliasingClicked(ActionEvent event) {
        updateConfigurationAndSave();
    }

    @FXML
    void checkDebugModeClicked(ActionEvent event) {
        updateConfigurationAndSave();
    }

    @FXML
    void checkFullScreenClicked(ActionEvent event) {
        radio1440900.setDisable(checkFullScreen.isSelected());
        radio16801050.setDisable(checkFullScreen.isSelected());
        radio16801050.setDisable(checkFullScreen.isSelected());
        
        updateConfigurationAndSave();
    }

    @FXML
    void checkShowConsoleClicked(ActionEvent event) {
        updateConfigurationAndSave();
    }

    @FXML
    void checkShowPanelClicked(ActionEvent event) {
        updateConfigurationAndSave();
    }

    @FXML
    void dropDownAircraftChanged(ActionEvent event) {
        updateConfigurationAndSave();
        configureAircraftTab();
    }

    @FXML
    void radio1440900Selected(ActionEvent event) {
        updateConfigurationAndSave();
    }

    @FXML
    void radio16801050Selected(ActionEvent event) {
        updateConfigurationAndSave();
    }

    @FXML
    void radio19201080Selected(ActionEvent event) {
        updateConfigurationAndSave();
    }

    @FXML
    void radio2dCockpitSelected(ActionEvent event) {
        updateConfigurationAndSave();
    }

    @FXML
    void radio800450Selected(ActionEvent event) {
        updateConfigurationAndSave();
    }

    @FXML
    void radioChaseSelected(ActionEvent event) {
        updateConfigurationAndSave();
    }

    @FXML
    void sliderEngineVolChanged(MouseEvent event) {
        updateConfigurationAndSave();
    }

    @FXML
    void sliderEnvironmentVolChanged(MouseEvent event) {
        updateConfigurationAndSave();
    }

    @FXML
    void sliderFieldOfViewChanged(MouseEvent event) {
        updateConfigurationAndSave();
    }

    @FXML
    void sliderFuelWeightChanged(MouseEvent event) {
        updateAircraftAndSave();
        recalculateWeights();
    }

    @FXML
    void sliderPayloadWeightChanged(MouseEvent event) {
        updateAircraftAndSave();
        recalculateWeights();
    }

    @FXML
    void sliderSimulationRateChanged(MouseEvent event) {
        updateConfigurationAndSave();
    }

    @FXML
    void sliderSystemsVolChanged(MouseEvent event) {
        updateConfigurationAndSave();
    }

    @FXML
    void textInitialAltChanged(ActionEvent event) {
        updateConfigurationAndSave();
    }

    @FXML
    void textInitialHdgChanged(ActionEvent event) {
        updateConfigurationAndSave();
    }

    @FXML
    void textInitialLatChanged(ActionEvent event) {
        updateConfigurationAndSave();
    }

    @FXML
    void textInitialLonChanged(ActionEvent event) {
        updateConfigurationAndSave();
    }

    @FXML
    void textInitialTasChanged(ActionEvent event) {
        updateConfigurationAndSave();
    }
    	
    @FXML
    void initialize() {
        logger.info("Initializing main menus...");

        configureAircraftTab();
        configureInitialConditionsTab();
        configureSimulationTab();
        configureDisplayTab();
        configureAudioTab();
        configureCameraTab();

        setSummaryText();
        textVersion.setText("Version: " + PomReader.getVersionNumber());
    }

    public MainMenuController(SimulationConfiguration configuration) {
        this.configuration = configuration;
    }

    void updateConfigurationAndSave() {
        configuration.setSelectedAircraft(dropDownAircraft.getValue().toString());
        
        configuration.setInitialConditions(new double[] {0,0}, 
                                            Double.parseDouble(textInitialHdg.getText()), 
                                            Double.parseDouble(textInitialAlt.getText()), 
                                            Double.parseDouble(textInitialTas.getText()));

        configuration.getSimulationOptions().clear();

        if (checkAnalysisMode.isSelected())
            configuration.getSimulationOptions().add(Options.ANALYSIS_MODE);
        else
            configuration.getSimulationOptions().add(Options.UNLIMITED_FLIGHT);
        
        if (checkDebugMode.isSelected())
            configuration.getSimulationOptions().add(Options.DEBUG_MODE);

        if (checkShowConsole.isSelected())
            configuration.getSimulationOptions().add(Options.CONSOLE_DISPLAY);

        configuration.setSimulationRateHz((int)sliderSimulationRate.getValue());

        configuration.getDisplayConfiguration().setUseAntiAliasing(checkAntiAliasing.isSelected());
        configuration.getDisplayConfiguration().setAnisotropicFiltering(checkAntiAliasing.isSelected() ? 16 : 0);
        configuration.getDisplayConfiguration().setUseFullScreen(checkFullScreen.isSelected());
        
        int windowWidth = 1440, windowHeight = 900;
        if (radio16801050.isSelected()) {
            windowWidth = 1680; 
            windowHeight = 1050;
        } else if (radio19201080.isSelected()) {
            windowWidth = 1920; 
            windowHeight = 1080;
        }

        configuration.getDisplayConfiguration().setDisplayWidth(windowWidth);
        configuration.getDisplayConfiguration().setDisplayHeight(windowHeight);

        configuration.getAudioConfiguration().setEngineVolume((float)sliderEngineVol.getValue() / 100);
        configuration.getAudioConfiguration().setSystemsVolume((float)sliderSystemsVol.getValue() / 100);
        configuration.getAudioConfiguration().setEnvironmentVolume((float)sliderEnvironmentVol.getValue() / 100);

        configuration.getCameraConfiguration().setFieldOfView((int)sliderFieldOfView.getValue());
        configuration.getCameraConfiguration().setShowPanel(checkShowPanel.isSelected());

        if(radio2dCockpit.isSelected())
            configuration.getCameraConfiguration().setMode(CameraMode.COCKPIT_2D);
        else
            configuration.getCameraConfiguration().setMode(CameraMode.CHASE);

        configuration.save();

        setSummaryText();
    }

    void updateAircraftAndSave() {
        aircraft.getMassProps().put(MassProperties.WEIGHT_FUEL, sliderFuelWeight.getValue() / 100);
        aircraft.getMassProps().put(MassProperties.WEIGHT_PAYLOAD, sliderPayloadWeight.getValue() / 100);
        aircraft.save();
    }

    void configureSimulationTab() {
        checkAnalysisMode.setSelected(configuration.getSimulationOptions().contains(Options.ANALYSIS_MODE));
        checkShowConsole.setSelected(configuration.getSimulationOptions().contains(Options.CONSOLE_DISPLAY));
        checkDebugMode.setSelected(configuration.getSimulationOptions().contains(Options.DEBUG_MODE));
        sliderSimulationRate.setValue(configuration.getSimulationRateHz());
    }

    void configureDisplayTab() {
        checkAnisoFiltering.setSelected(configuration.getDisplayConfiguration().getAnisotropicFiltering() > 0);
        checkAntiAliasing.setSelected(configuration.getDisplayConfiguration().isUseAntiAliasing());
        checkFullScreen.setSelected(configuration.getDisplayConfiguration().isUseFullScreen());
        radio1440900.setSelected(configuration.getDisplayConfiguration().getDisplayWidth() == 1440);
        radio16801050.setSelected(configuration.getDisplayConfiguration().getDisplayWidth() == 1680);
        radio19201080.setSelected(configuration.getDisplayConfiguration().getDisplayWidth() == 1920);
    }

    void configureAudioTab() {
        sliderEngineVol.setValue(configuration.getAudioConfiguration().getEngineVolume() * 100);
        sliderSystemsVol.setValue(configuration.getAudioConfiguration().getSystemsVolume() * 100);
        sliderEnvironmentVol.setValue(configuration.getAudioConfiguration().getEnvironmentVolume() * 100);
    }

    void configureCameraTab() {
        radioChase.setSelected(configuration.getCameraConfiguration().getMode() == CameraMode.CHASE);
        radio2dCockpit.setSelected(configuration.getCameraConfiguration().getMode() == CameraMode.COCKPIT_2D);
        checkShowPanel.setSelected(configuration.getCameraConfiguration().isShowPanel());
        sliderFieldOfView.setValue(configuration.getCameraConfiguration().getFieldOfView());
    }

    void configureInitialConditionsTab() {
        textInitialHdg.setText(df0.format(Math.toDegrees(configuration.getInitialConditions().get(InitialConditions.INITPSI))));
		textInitialTas.setText(df0.format(SixDOFUtilities.toKnots(configuration.getInitialConditions().get(InitialConditions.INITU))));
        textInitialAlt.setText(df0.format(configuration.getInitialConditions().get(InitialConditions.INITD)));
        textInitialLat.setText("0.0");
        textInitialLon.setText("0.0");

        textInitialLat.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                float parsed = Float.parseFloat(newValue.replaceAll("[^\\-\\.\\d]", ""));
        
                if (parsed > 90 || parsed < -90)
                    parsed = 0;
                
                textInitialLat.setText(df4.format(parsed));
            } catch (NumberFormatException e) {
                return;
            }
        });

        textInitialLon.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                float parsed = Float.parseFloat(newValue.replaceAll("[^\\-\\.\\d]", ""));
                
                if (parsed > 180 || parsed < -180)
                    parsed = 0;

                textInitialLon.setText(df4.format(parsed));
            } catch (NumberFormatException e) {
                return;
            }
        });

        textInitialHdg.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                float parsed = Float.parseFloat(newValue.replaceAll("[^\\d]", ""));
                
                if (parsed > 360)
                    parsed = 360;
                    
                textInitialHdg.setText(df0.format(parsed));
            } catch (NumberFormatException e) {
                return;
            }
        });

        textInitialTas.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                float parsed = Float.parseFloat(newValue.replaceAll("[^\\d]", ""));
                
                if (parsed > 400)
                    parsed = 400;
                    
                textInitialTas.setText(df0.format(parsed));
            } catch (NumberFormatException e) {
                return;
            }
        });

        textInitialAlt.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                float parsed = Float.parseFloat(newValue.replaceAll("[^\\d]", ""));
                
                if (parsed > 30000)
                    parsed = 30000;
                    
                textInitialAlt.setText(df0.format(parsed));
            } catch (NumberFormatException e) {
                return;
            }
        });
    }

    void configureAircraftTab() {
        fillInAircraftDropdownItems();

        String selectedAircraft = configuration.getSelectedAircraft();
        dropDownAircraft.getSelectionModel().select(selectedAircraft);
        aircraft = FileUtilities.readAircraftConfiguration(selectedAircraft);

        recalculateWeights();
        imageAircraft.setImage(createPreviewPicture(selectedAircraft, SimFiles.PREVIEW_PICTURE.toString()));
        textDescription.setText(createDescriptionText(selectedAircraft, SimFiles.DESCRIPTION.toString()));
    }

    Image createPreviewPicture(String aircraftName, String fileName) {
		StringBuilder sb = new StringBuilder();
        sb.append(SimDirectories.AIRCRAFT.toString()).append(File.separator).append(aircraftName)
          .append(File.separator).append(fileName).append(SimFiles.PREVIEW_PIC_EXT.toString());
		
		File imageFile = new File(sb.toString());
		Image image = null;
		
		try { 
			image = new Image("file:" + imageFile.toURI().getPath());
		} catch (IllegalArgumentException e) {
			logger.error("Error loading image: " + fileName + SimFiles.PREVIEW_PIC_EXT.toString() + "!", e);
		}

		return image;
    }
    
    String createDescriptionText(String aircraftName, String fileName) {
		StringBuilder sb = new StringBuilder();
        sb.append(SimDirectories.AIRCRAFT.toString()).append(File.separator).append(aircraftName)
          .append(File.separator).append(fileName).append(SimFiles.DESCRIPTION_EXT.toString());
		
		String readLine = null;
		StringBuilder readFile = new StringBuilder();
		
		try (BufferedReader br = new BufferedReader(new FileReader(sb.toString()))) {
			while ((readLine = br.readLine()) != null)
				readFile.append(readLine).append("\n");
		} catch (IOException e) {
			logger.error("Error reading: " + fileName + SimFiles.DESCRIPTION_EXT.toString() + "!", e);
		} 
		
		return readFile.toString();
	}
    
    void fillInAircraftDropdownItems() {
		StringBuilder sb = new StringBuilder();
		sb.append(SimDirectories.AIRCRAFT.toString()).append(File.separator);
		
		File[] directories = new File(sb.toString()).listFiles((File file) -> { return file.isDirectory(); });
        
        ArrayList<String> dropdownItems = new ArrayList<>();
		for (File file : directories) {
			String[] splitPath = file.toString().split(File.separator.replace("\\", "\\\\"));
			dropdownItems.add(splitPath[splitPath.length - 1]);
        }
        
        dropDownAircraft.setItems(FXCollections.observableArrayList(dropdownItems));
    }
    
    private void recalculateWeights() {
        Map<MassProperties, Double> massProperties = aircraft.getMassProps();
		double fuelFraction = massProperties.get(MassProperties.WEIGHT_FUEL);
        double payloadFraction = massProperties.get(MassProperties.WEIGHT_PAYLOAD);
        
		double fuelWeightValue = fuelFraction * massProperties.get(MassProperties.MAX_WEIGHT_FUEL);
		double payloadWeightValue = payloadFraction * massProperties.get(MassProperties.MAX_WEIGHT_PAYLOAD);
		double totalWeightValue = (fuelFraction * massProperties.get(MassProperties.MAX_WEIGHT_FUEL) +
                                  (payloadFraction * massProperties.get(MassProperties.MAX_WEIGHT_PAYLOAD) +
                                                     massProperties.get(MassProperties.WEIGHT_EMPTY)));
        
        textEmptyWeight.setText(df0.format(massProperties.get(MassProperties.WEIGHT_EMPTY)));
        textPayloadWeight.setText(df0.format(payloadWeightValue));
        textFuelWeight.setText(df0.format(fuelWeightValue));
        textTotalWeight.setText(df0.format(totalWeightValue));
		
        sliderPayloadWeight.setValue(100 * payloadFraction);
        sliderFuelWeight.setValue(100 * fuelFraction);
    }

    private void setSummaryText() {
        StringBuilder sb = new StringBuilder();

        String htmlBodyOpen = "<html><body style='font-family:sans-serif; font-size:11px'>";
	    String parOpen = "<p>";
	    String parClose = "</p>";
        String htmlBodyClose = "</body></html>";

        sb.append(htmlBodyOpen);
        
        sb.append(parOpen).append("<b>Selected Aircraft: </b>").append(configuration.getSelectedAircraft()).append(parClose);
        
        sb.append(parOpen).append("<b>Initial Conditions: </b>")
          .append("Latitude: ").append(textInitialLat.getText()).append(" deg | ")
		  .append("Longitude: ").append(textInitialLon.getText()).append(" deg | ")
		  .append("Heading: ").append(textInitialHdg.getText()).append(" deg | ")
		  .append("Altitude: ").append(textInitialAlt.getText()).append(" ft | ")
          .append("Airspeed: ").append(textInitialTas.getText()).append(" kts")
          .append(parClose);
        
        sb.append(parOpen).append("<b>Simulation Options: </b>");
        configuration.getSimulationOptions().forEach(option -> { sb.append(option.toString()).append(" | "); });
		sb.append("Update Rate: ").append(sliderSimulationRate.getValue()).append(" Hz").append(parClose);

        sb.append(htmlBodyClose);

        webviewSummary.getEngine().loadContent(sb.toString());
    }

    public void addSimulationEventListener(SimulationEventListener listener) {
        if (simulationEventListeners != null) {
			logger.info("Adding simulation event listener: " + listener.getClass());
			simulationEventListeners.add(listener);
		}
    }
}
