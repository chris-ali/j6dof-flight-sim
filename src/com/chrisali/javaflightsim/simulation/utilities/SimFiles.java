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
	DESCRIPTION  		("Description"),
	PREVIEW_PICTURE  	("PreviewPicture"),
		
	// Extensions
	DESCRIPTION_EXT		(".txt"),
	PREVIEW_PIC_EXT     (".jpg");
	
	private String file;
	
	private SimFiles(String file) {this.file = file;}
	
	public String toString() {return file;}
}