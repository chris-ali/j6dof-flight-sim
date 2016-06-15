package com.chrisali.javaflightsim.menus.optionspanel;

/**
 *	Used by the audioOptions EnumSet to set volume settings for different types of sounds
 */
public enum AudioOptions {
	ENGINE_VOLUME      ("engine_volume"),
	SYSTEMS_VOLUME     ("systems_volume"),
	ENVIRONMENT_VOLUME ("environment_volume");
	
	private String option;
	
	private AudioOptions(String option) {this.option = option;}
	
	public String toString() {return option;}
}
