package com.chrisali.javaflightsim.menus.optionspanel;

/**
 *	Used by the displayOptions EnumSet to set options for the out the window display
 */
public enum DisplayOptions {
	DISPLAY_WIDTH   ("display_width"),
	DISPLAY_HEIGHT  ("display_height"),
	ANTI_ALIASING   ("anti_aliasing");
	
	private String option;
	
	private DisplayOptions(String option) {this.option = option;}
	
	public String toString() {return option;}
}
