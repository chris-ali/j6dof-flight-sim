/**
 * 
 */
package com.chrisali.javaflightsim.otw.utilities;

/**
 * Contains files and extensions used by the out the window (OTW) component of JavaFlightSimulator 
 * 
 * @author Chris Ali
 *
 */
public enum OTWFiles {
	// Files (perhaps put some defaults here)
		
	// Extensions
	TEXTURE_EXT     (".png"),
	MODEL_EXT		(".obj"),
	SOUND_EXT       (".wav"),
	FONT_EXT		(".fnt");
	
	private String file;
	
	private OTWFiles(String file) {this.file = file;}
	
	public String toString() {return file;}
}
