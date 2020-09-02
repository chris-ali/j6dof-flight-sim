package com.chrisali.javaflightsim.javafx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import com.chrisali.javaflightsim.initializer.PomReader;
import com.chrisali.javaflightsim.simulation.aircraft.Aircraft;
import com.chrisali.javaflightsim.simulation.aircraft.MassProperties;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;

public class MainMenuController {

    private static final Logger logger = LogManager.getLogger(MainMenuController.class);

    private Aircraft aircraft;
    private SimulationConfiguration configuration;

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
    private Text textSummary;

    @FXML
    private Button buttonStartSim;

    @FXML
    private Text textVersion;

    @FXML
    void buttonStartSimClicked(ActionEvent event) {

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
    void sliderEngineVolChanged(KeyEvent event) {
        updateConfigurationAndSave();
    }

    @FXML
    void sliderEnvironmentVolChanged(KeyEvent event) {
        updateConfigurationAndSave();
    }

    @FXML
    void sliderFieldOfViewChanged(KeyEvent event) {
        updateConfigurationAndSave();
    }

    @FXML
    void sliderFuelWeightChanged(KeyEvent event) {
        updateAircraftAndSave();
        recalculateWeights();
    }

    @FXML
    void sliderPayloadWeightChanged(KeyEvent event) {
        updateAircraftAndSave();
        recalculateWeights();
    }

    @FXML
    void sliderSimulationRateChanged(KeyEvent event) {
        updateConfigurationAndSave();
    }

    @FXML
    void sliderSystemsVolChanged(KeyEvent event) {
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
        assert dropDownAircraft != null : "fx:id=\"dropDownAircraft\" was not injected: check your FXML file 'MainMenu.fxml'.";
        assert imageAircraft != null : "fx:id=\"imageAircraft\" was not injected: check your FXML file 'MainMenu.fxml'.";
        assert textDescription != null : "fx:id=\"textDescription\" was not injected: check your FXML file 'MainMenu.fxml'.";
        assert sliderPayloadWeight != null : "fx:id=\"sliderPayloadWeight\" was not injected: check your FXML file 'MainMenu.fxml'.";
        assert sliderFuelWeight != null : "fx:id=\"sliderFuelWeight\" was not injected: check your FXML file 'MainMenu.fxml'.";
        assert textEmptyWeight != null : "fx:id=\"textEmptyWeight\" was not injected: check your FXML file 'MainMenu.fxml'.";
        assert textFuelWeight != null : "fx:id=\"textFuelWeight\" was not injected: check your FXML file 'MainMenu.fxml'.";
        assert textPayloadWeight != null : "fx:id=\"textPayloadWeight\" was not injected: check your FXML file 'MainMenu.fxml'.";
        assert textTotalWeight != null : "fx:id=\"textTotalWeight\" was not injected: check your FXML file 'MainMenu.fxml'.";
        assert textInitialLat != null : "fx:id=\"textInitialLat\" was not injected: check your FXML file 'MainMenu.fxml'.";
        assert textInitialLon != null : "fx:id=\"textInitialLon\" was not injected: check your FXML file 'MainMenu.fxml'.";
        assert textInitialAlt != null : "fx:id=\"textInitialAlt\" was not injected: check your FXML file 'MainMenu.fxml'.";
        assert textInitialHdg != null : "fx:id=\"textInitialHdg\" was not injected: check your FXML file 'MainMenu.fxml'.";
        assert textInitialTas != null : "fx:id=\"txtInitialTas\" was not injected: check your FXML file 'MainMenu.fxml'.";
        assert checkAnalysisMode != null : "fx:id=\"checkAnalysisMode\" was not injected: check your FXML file 'MainMenu.fxml'.";
        assert checkShowConsole != null : "fx:id=\"checkShowConsole\" was not injected: check your FXML file 'MainMenu.fxml'.";
        assert checkDebugMode != null : "fx:id=\"checkDebugMode\" was not injected: check your FXML file 'MainMenu.fxml'.";
        assert sliderSimulationRate != null : "fx:id=\"sliderSimulationRate\" was not injected: check your FXML file 'MainMenu.fxml'.";
        assert checkAntiAliasing != null : "fx:id=\"checkAntiAliasing\" was not injected: check your FXML file 'MainMenu.fxml'.";
        assert checkAnisoFiltering != null : "fx:id=\"checkAnisoFiltering\" was not injected: check your FXML file 'MainMenu.fxml'.";
        assert checkFullScreen != null : "fx:id=\"checkFullScreen\" was not injected: check your FXML file 'MainMenu.fxml'.";
        assert radio800450 != null : "fx:id=\"radio800450\" was not injected: check your FXML file 'MainMenu.fxml'.";
        assert resolutionGroup != null : "fx:id=\"resolutionGroup\" was not injected: check your FXML file 'MainMenu.fxml'.";
        assert radio1440900 != null : "fx:id=\"radio1440900\" was not injected: check your FXML file 'MainMenu.fxml'.";
        assert radio16801050 != null : "fx:id=\"radio16801050\" was not injected: check your FXML file 'MainMenu.fxml'.";
        assert radio19201080 != null : "fx:id=\"radio19201080\" was not injected: check your FXML file 'MainMenu.fxml'.";
        assert sliderEngineVol != null : "fx:id=\"sliderEngineVol\" was not injected: check your FXML file 'MainMenu.fxml'.";
        assert sliderSystemsVol != null : "fx:id=\"sliderSystemsVol\" was not injected: check your FXML file 'MainMenu.fxml'.";
        assert sliderEnvironmentVol != null : "fx:id=\"sliderEnvironmentVol\" was not injected: check your FXML file 'MainMenu.fxml'.";
        assert checkShowPanel != null : "fx:id=\"checkShowPanel\" was not injected: check your FXML file 'MainMenu.fxml'.";
        assert sliderFieldOfView != null : "fx:id=\"sliderFieldOfView\" was not injected: check your FXML file 'MainMenu.fxml'.";
        assert radioChase != null : "fx:id=\"radioChase\" was not injected: check your FXML file 'MainMenu.fxml'.";
        assert cameraGroup != null : "fx:id=\"cameraGroup\" was not injected: check your FXML file 'MainMenu.fxml'.";
        assert radio2dCockpit != null : "fx:id=\"radio2dCockpit\" was not injected: check your FXML file 'MainMenu.fxml'.";
        assert textSummary != null : "fx:id=\"textSummary\" was not injected: check your FXML file 'MainMenu.fxml'.";
        assert buttonStartSim != null : "fx:id=\"buttonStartSim\" was not injected: check your FXML file 'MainMenu.fxml'.";
        assert textVersion != null : "fx:id=\"textVersion\" was not injected: check your FXML file 'MainMenu.fxml'.";

        logger.info("Initializing main menus...");

        configuration = FileUtilities.readSimulationConfiguration();

        configureAircraftTab();
        configureInitialConditionsTab();
        configureSimulationTab();
        configureDisplayTab();
        configureAudioTab();
        configureCameraTab();

        textVersion.setText("Version: " + PomReader.getVersionNumber());
    }

    void updateConfigurationAndSave() {
        configuration.setSelectedAircraft(dropDownAircraft.getValue().toString());
        configuration.getInitialConditions().put(InitialConditions.INITD, Double.parseDouble(textInitialAlt.getText()));
        configuration.getInitialConditions().put(InitialConditions.INITPSI, Math.toRadians(Double.parseDouble(textInitialHdg.getText())));
        configuration.getInitialConditions().put(InitialConditions.INITU, SixDOFUtilities.toFtPerSec(Double.parseDouble(textInitialTas.getText())));
        
        configuration.getSimulationOptions().clear();

        if (checkAnalysisMode.isSelected())
            configuration.getSimulationOptions().add(Options.ANALYSIS_MODE);
        else
            configuration.getSimulationOptions().add(Options.UNLIMITED_FLIGHT);
        
        if (checkDebugMode.isSelected())
            configuration.getSimulationOptions().add(Options.DEBUG_MODE);

        if (checkShowConsole.isSelected())
            configuration.getSimulationOptions().add(Options.CONSOLE_DISPLAY);

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
        textInitialHdg.setText(String.valueOf(Math.toDegrees(configuration.getInitialConditions().get(InitialConditions.INITPSI))));
		textInitialTas.setText(String.valueOf(SixDOFUtilities.toKnots(configuration.getInitialConditions().get(InitialConditions.INITU))));
        textInitialAlt.setText(configuration.getInitialConditions().get(InitialConditions.INITD).toString());
        textInitialLat.setText("0.0");
        textInitialLon.setText("0.0");

        textInitialLat.textProperty().addListener((observable, oldValue, newValue) -> {
            textInitialLat.setText(newValue.replaceAll("[^\\-\\.\\d]", ""));

            float testVal = Float.parseFloat(newValue);

            if (testVal > 90 || testVal < -90)
                textInitialLat.setText("0.0");
        });

        textInitialLon.textProperty().addListener((observable, oldValue, newValue) -> {
            textInitialLon.setText(newValue.replaceAll("[^\\-\\.\\d]", ""));

            float testVal = Float.parseFloat(newValue);

            if (testVal > 180 || testVal < -180)
                textInitialLon.setText("0.0");
        });

        textInitialHdg.textProperty().addListener((observable, oldValue, newValue) -> {
            textInitialHdg.setText(newValue.replaceAll("[^\\d]", ""));

            if (Float.parseFloat(newValue) > 360)
                textInitialHdg.setText("0");
        });

        textInitialTas.textProperty().addListener((observable, oldValue, newValue) -> {
            textInitialTas.setText(newValue.replaceAll("[^\\d]", ""));

            if (Float.parseFloat(newValue) > 400)
                textInitialTas.setText("400");
        });

        textInitialAlt.textProperty().addListener((observable, oldValue, newValue) -> {
            textInitialAlt.setText(newValue.replaceAll("[^\\d]", ""));

            if (Float.parseFloat(newValue) > 40000)
                textInitialAlt.setText("40000");
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
		Image image = new Image("");
		
		try { 
			image = new Image(imageFile.toURI().getPath());
		} catch (IllegalArgumentException e) {
			logger.error("Could not find image: " + fileName + SimFiles.PREVIEW_PIC_EXT.toString() + "!");
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
			logger.error("Could not read: " + fileName + SimFiles.DESCRIPTION_EXT.toString() + "!");
		} 
		
		return readFile.toString();
	}
    
    void fillInAircraftDropdownItems() {
		StringBuilder sb = new StringBuilder();
		sb.append(SimDirectories.AIRCRAFT.toString()).append(File.separator);
		
		File[] directories = new File(sb.toString()).listFiles((File file) -> { return file.isDirectory(); });
		
		for (File file : directories) {
			String[] splitPath = file.toString().split(File.separator.replace("\\", "\\\\"));
			dropDownAircraft.getItems().add(splitPath[splitPath.length - 1]);
		}
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
		
        textPayloadWeight.setText(String.valueOf(payloadWeightValue));
        textFuelWeight.setText(String.valueOf(fuelWeightValue));
        textTotalWeight.setText(String.valueOf(totalWeightValue));
		
        sliderPayloadWeight.setValue((100 * payloadFraction));
        sliderPayloadWeight.setValue((100 * fuelFraction));
    }
}
