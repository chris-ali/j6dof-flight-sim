/**
 * 
 */
package com.chrisali.javaflightsim.simulation.setup;

/**
 * Contains collections and fields used to configure various audio options for the out the window view
 */
public class AudioConfiguration {
	
	private float engineVolume;
	
	private float systemsVolume;
	
	private float environmentVolume;
		
	public AudioConfiguration() {}

	public float getEngineVolume() { return engineVolume; }

	public void setEngineVolume(float engineVolume) { this.engineVolume = engineVolume;	}

	public float getSystemsVolume() { return systemsVolume;	}

	public void setSystemsVolume(float systemsVolume) { this.systemsVolume = systemsVolume;	}

	public float getEnvironmentVolume() { return environmentVolume;	}

	public void setEnvironmentVolume(float environmentVolume) { this.environmentVolume = environmentVolume;	}
}