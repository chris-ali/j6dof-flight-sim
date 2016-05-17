package com.chrisali.javaflightsim.datatransfer;

/**
 *	Used by {@link EnvironmentData} as the key to its environmentData EnumMap
 */
public enum EnvironmentDataType {
	TERRAIN_HEIGHT        ("Terrain Height", "ft");
	
	private final String dataType;
	private final String unit;
	
	EnvironmentDataType(String dataType, String unit) {
		this.dataType = dataType;
		this.unit = unit;
	}
	
	public String toString() {return dataType;}
	
	public String getUnit() {return unit;}
}
