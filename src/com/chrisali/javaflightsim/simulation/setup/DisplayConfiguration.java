/**
 * 
 */
package com.chrisali.javaflightsim.simulation.setup;

/**
 * Contains collections and fields used to configure various display options for the out the window view
 */
public class DisplayConfiguration {
		
	private boolean useAntiAliasing;
	
	private int anisotropicFiltering;
	
	private int displayHeight;
	
	private int displayWidth;
		
	public DisplayConfiguration() {}

	public boolean isUseAntiAliasing() { return useAntiAliasing; }

	public void setUseAntiAliasing(boolean useAntiAliasing) { this.useAntiAliasing = useAntiAliasing; }

	public int getAnisotropicFiltering() { return anisotropicFiltering;	}

	public void setAnisotropicFiltering(int anisotropicFiltering) { this.anisotropicFiltering = anisotropicFiltering; }

	public int getDisplayHeight() { return displayHeight; }

	public void setDisplayHeight(int displayHeight) { this.displayHeight = displayHeight; }

	public int getDisplayWidth() { return displayWidth; }

	public void setDisplayWidth(int displayWidth) { this.displayWidth = displayWidth; }
}