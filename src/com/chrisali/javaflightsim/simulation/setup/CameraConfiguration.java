/**
 * 
 */
package com.chrisali.javaflightsim.simulation.setup;

import java.util.EnumMap;

/**
 * Contains collections and fields used to configure the position and type of camera used in the out the window view
 */
public class CameraConfiguration {
		
	private CameraMode mode;
	
	private boolean showPanel;
	
	private int fieldOfView;
	
	private EnumMap<InitialConditions, Double> initialConditions;
	
	public CameraConfiguration() {}
			
	public CameraMode getMode() { return mode; }

	public void setMode(CameraMode mode) { this.mode = mode; }

	public boolean isShowPanel() { return showPanel; }

	public void setShowPanel(boolean showPanel) { this.showPanel = showPanel; }

	public int getFieldOfView() { return fieldOfView; }

	public void setFieldOfView(int fieldOfView) { this.fieldOfView = fieldOfView; }

	public EnumMap<InitialConditions, Double> getInitialConditions() { return initialConditions; }

	public void setInitialConditions(EnumMap<InitialConditions, Double> initialConditions) { this.initialConditions = initialConditions; }
}