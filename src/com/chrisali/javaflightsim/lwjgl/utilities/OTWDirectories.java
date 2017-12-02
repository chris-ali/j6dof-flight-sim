/**
 * 
 */
package com.chrisali.javaflightsim.lwjgl.utilities;

/**
 * Enumerated directory names for folders
 * 
 * @author Christopher Ali
 */
public enum OTWDirectories {
	
	// Folders
	RESOURCES   ("Resources"),
	AUDIO  		("Audio"),
	ENTITIES	("Entities"),
	FONTS       ("Fonts"),
	PARTICLES  	("Particles"),
    TERRAIN	  	("Terrain"),
	WATER       ("Water"),
	GAUGES		("Gauges");

	private String directory;
	
	private OTWDirectories(String directory) {this.directory = directory;}
	
	public String toString() {return directory;}
}
