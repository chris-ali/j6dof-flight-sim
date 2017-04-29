/**
 * 
 */
package com.chrisali.javaflightsim.simulation.utilities;

/**
 * Contains files and extensions used by the simulation component of JavaFlightSimulator 
 * 
 * @author Chris Ali
 *
 */
public enum SimFiles {
	// Aircraft Files
	AERO      			("Aero"),
	DESCRIPTION  		("Description"),
	GROUND_REACTION		("GroundReaction"),
	MASS_PROPERTIES     ("MassProperties"),
	PREVIEW_PICTURE  	("PreviewPicture"),
	PROPULSION	  		("Propulsion"),
	WING_GEOMETRY       ("WingGeometry"),
	
	// Sim Config Files
	AUDIO_SETUP  		("AudioSetup"),
	DISPLAY_SETUP	    ("DisplaySetup"),
	INITIAL_CONDITIONS  ("InitialConditions"),
	INITIAL_CONTROLS    ("InitialControls"),
	INTEGRATOR_CONFIG	("IntegratorConfig"),
	SIMULATION_SETUP    ("SimulationSetup"),
	
	// Extensions
	DESCRIPTION_EXT		(".txt"),
	PREVIEW_PIC_EXT     (".jpg");
	
	private String file;
	
	private SimFiles(String file) {this.file = file;}
	
	public String toString() {return file;}
}