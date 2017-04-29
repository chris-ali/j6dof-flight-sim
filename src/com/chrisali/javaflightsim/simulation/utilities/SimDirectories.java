/**
 * 
 */
package com.chrisali.javaflightsim.simulation.utilities;

/**
 * Contains directories used by the simulation component of JavaFlightSimulator 
 * 
 * @author Chris Ali
 *
 */
public enum SimDirectories {
	AIRCRAFT      ("Aircraft"),
	LOOKUP_TABLE  ("LookupTables"),
	SIM_CONFIG	  ("SimConfig");
	
	private String directory;
	
	private SimDirectories(String directory) {this.directory = directory;}
	
	public String toString() {return directory;}
}