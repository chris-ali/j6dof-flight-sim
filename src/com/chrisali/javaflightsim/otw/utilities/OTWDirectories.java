/**
 * 
 */
package com.chrisali.javaflightsim.otw.utilities;

/**
 * @author root
 *
 */
public enum OTWDirectories {
	
	// Folders
	RESOURCES   ("Resources"),
	AUDIO  		("Audio"),
	ENTITIES	("Entities"),
	FONTS       ("Fonts"),
	PARTICLES  	("Particles"),
    TERRAIN	  	("Terrain"),
	WATER       ("Water");

	private String directory;
	
	private OTWDirectories(String directory) {this.directory = directory;}
	
	public String toString() {return directory;}
}
