/*******************************************************************************
 * Copyright (C) 2016-2020 Christopher Ali
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *  If you have any questions about this project, you can visit
 *  the project's GitHub repository at: http://github.com/chris-ali/j6dof-flight-sim/
 ******************************************************************************/
package com.chrisali.javaflightsim.lwjgl.audio;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.util.vector.Vector3f;

import com.chrisali.javaflightsim.lwjgl.LWJGLWorld;
import com.chrisali.javaflightsim.lwjgl.utilities.OTWDirectories;
import com.chrisali.javaflightsim.simulation.aircraft.Aircraft;
import com.chrisali.javaflightsim.simulation.datatransfer.FlightData;
import com.chrisali.javaflightsim.simulation.datatransfer.FlightDataListener;
import com.chrisali.javaflightsim.simulation.datatransfer.FlightDataType;
import com.chrisali.javaflightsim.simulation.propulsion.Engine;
import com.chrisali.javaflightsim.simulation.setup.SimulationConfiguration;
import com.chrisali.javaflightsim.simulation.utilities.FileUtilities;

/**
 * Class that contains a repository of sounds to be played by triggering certain events, such
 * as control surface deflections, engine properties or change in airspeed
 * 
 * @author Christopher Ali
 *
 */
public class SoundCollection {
	
	//Logging
	private static final Logger logger = LogManager.getLogger(SoundCollection.class);
		
	/**
	 * Inner enums used to identify {@link SoundSource} objects in the soundSources
	 * EnumMap 
	 * 
	 * @author Christopher Ali
	 *
	 */
	public enum SoundEvent {
		ENGINE_1_LOW,
		ENGINE_1_MED,
		ENGINE_1_HIGH,
		ENGINE_1_MAX,
		ENGINE_2_LOW,
		ENGINE_2_MED,
		ENGINE_2_HIGH,
		ENGINE_2_MAX,
		ENGINE_3_LOW,
		ENGINE_3_MED,
		ENGINE_3_HIGH,
		ENGINE_3_MAX,
		ENGINE_4_LOW,
		ENGINE_4_MED,
		ENGINE_4_HIGH,
		ENGINE_4_MAX,
		FLAPS,
		GEAR,
		STALL,
		WIND,
		GYRO;
	}
	
	/**
	 * Inner Enum used with the soundValues EnumMap, which is used to store values from {@link FlightData}
	 * to be used with setting sound properties
	 * 
	 * @author Christopher Ali
	 *
	 */
	public enum SoundCategory {
		RPM_1,
		RPM_2,
		RPM_3,
		RPM_4,
		FLAPS,
		PREV_STEP_FLAPS,
		GEAR,
		PREV_STEP_GEAR,
		STALL_HORN,
		WIND,
		GYRO;
	}
	
	private Aircraft aircraft;
		
	private float engineVolume;
	private float systemsVolume;
	private float environmentVolume;
	
	private Map<SoundEvent, SoundSource> soundSources; 
		
	private Map<SoundCategory, Double> soundValues;
	
	/**
	 * Used to record soundValues data to PREV_STEP_* enums to stop sounds looping if a control stops moving
	 */
	private boolean recordToPreviousStep = true; 
	
	/**
	 *	Fills soundSources EnumMap with {@link SoundSource} objects, which are references to audio
	 *  files in .Resources/Audio, and sets their initial properties. Uses {@link SimulationConfiguration}
	 *  to get {@link AircraftBuilder} reference to determine how many engines to assign sounds to, and where 
	 *  to position them relative to the listener
	 *  
	 *  @param configuration
	 */
	public SoundCollection(SimulationConfiguration configuration) {
		
		logger.info("Initializing Sound Collections...");
		
		aircraft = FileUtilities.readAircraftConfiguration(configuration.getSelectedAircraft());
		
		engineVolume = configuration.getAudioConfiguration().getEngineVolume();
		systemsVolume = configuration.getAudioConfiguration().getSystemsVolume();
		environmentVolume = configuration.getAudioConfiguration().getEnvironmentVolume();
		
		soundSources = new EnumMap<>(SoundEvent.class);
		soundValues = new EnumMap<>(SoundCategory.class);
		
		//================================ Engine =========================================
		
		Vector3f enginePosVector = new Vector3f();
		int engineNumber;
		double[] enginePosition;
		Set<Engine> engineList = aircraft.getEngines();
				
		for (Engine engine : engineList) {
			engineNumber    = engine.getEngineNumber(); 
			enginePosition  = engine.getEnginePosition();
			enginePosVector = new Vector3f((float) enginePosition[0]/5, (float) enginePosition[1]/5, (float) enginePosition[2]/5);
			
			SoundEvent engLow  = Enum.valueOf(SoundEvent.class, "ENGINE_" + engineNumber + "_LOW");
			SoundEvent engMed  = Enum.valueOf(SoundEvent.class, "ENGINE_" + engineNumber + "_MED");
			SoundEvent engHigh = Enum.valueOf(SoundEvent.class, "ENGINE_" + engineNumber + "_HIGH");
			SoundEvent engMax  = Enum.valueOf(SoundEvent.class, "ENGINE_" + engineNumber + "_MAX");
			
			soundSources.put(engLow, new SoundSource(OTWDirectories.AUDIO.toString(), "engineLow"));
			soundSources.get(engLow).setVolume(0);
			soundSources.get(engLow).setLooping(true);
			soundSources.get(engLow).play();
			soundSources.get(engLow).setPosition(enginePosVector);
			
			soundSources.put(engMed, new SoundSource(OTWDirectories.AUDIO.toString(), "engineMed"));
			soundSources.get(engMed).setVolume(0);
			soundSources.get(engMed).setLooping(true);
			soundSources.get(engMed).play();
			soundSources.get(engMed).setPosition(enginePosVector);
			
			soundSources.put(engHigh, new SoundSource(OTWDirectories.AUDIO.toString(), "engineHigh"));
			soundSources.get(engHigh).setVolume(0);
			soundSources.get(engHigh).setLooping(true);
			soundSources.get(engHigh).play();
			soundSources.get(engHigh).setPosition(enginePosVector);
			
			soundSources.put(engMax, new SoundSource(OTWDirectories.AUDIO.toString(), "engineMax"));
			soundSources.get(engMax).setVolume(0);
			soundSources.get(engMax).setLooping(true);
			soundSources.get(engMax).play();
			soundSources.get(engMax).setPosition(enginePosVector);
		}
					
		//================================ Systems =========================================
		
		soundSources.put(SoundEvent.FLAPS, new SoundSource(OTWDirectories.AUDIO.toString(), "flap"));
		soundSources.get(SoundEvent.FLAPS).setVolume(0.5f*systemsVolume);
		
		soundSources.put(SoundEvent.GEAR, new SoundSource(OTWDirectories.AUDIO.toString(), "gear"));
		soundSources.get(SoundEvent.GEAR).setVolume(0.5f*systemsVolume);
		
		soundSources.put(SoundEvent.STALL, new SoundSource(OTWDirectories.AUDIO.toString(), "stall"));
		soundSources.get(SoundEvent.STALL).setVolume(0.5f*systemsVolume);
		soundSources.get(SoundEvent.STALL).setLooping(true);
		
		soundSources.put(SoundEvent.GYRO, new SoundSource(OTWDirectories.AUDIO.toString(), "gyroLoop"));
		soundSources.get(SoundEvent.GYRO).setVolume(0.25f*systemsVolume);
		soundSources.get(SoundEvent.GYRO).setLooping(true);
		soundSources.get(SoundEvent.GYRO).play();
		
		//================================ Environment ======================================
		
		soundSources.put(SoundEvent.WIND, new SoundSource(OTWDirectories.AUDIO.toString(), "wind"));
		soundSources.get(SoundEvent.WIND).setVolume(0.5f*environmentVolume);
		soundSources.get(SoundEvent.WIND).setLooping(true);
		soundSources.get(SoundEvent.WIND).play();
	}
	
	/**
	 * Wrapper method to call setRPM(), setControl(), setWind() and setStallHorn() at once;
	 * uses an EnumMap of {@link SoundCategory} enums to set the double values retrieved by 
	 * {@link FlightDataListener} in {@link LWJGLWorld}.
	 * 
	 * @param soundValues
	 */
	public void update(Map<FlightDataType, Double> flightData) {
		// Set values for each sound in the simulation that depends on flight data
		soundValues.put(SoundCategory.RPM_1, flightData.get(FlightDataType.RPM_1));
		soundValues.put(SoundCategory.RPM_2, flightData.get(FlightDataType.RPM_2));
		soundValues.put(SoundCategory.RPM_3, flightData.get(FlightDataType.RPM_3));
		soundValues.put(SoundCategory.RPM_4, flightData.get(FlightDataType.RPM_4));
		soundValues.put(SoundCategory.WIND, flightData.get(FlightDataType.TAS));
		soundValues.put(SoundCategory.FLAPS, flightData.get(FlightDataType.FLAPS));
		soundValues.put(SoundCategory.GEAR, flightData.get(FlightDataType.GEAR));
		soundValues.put(SoundCategory.STALL_HORN, flightData.get(FlightDataType.AOA));
					
		// Record value every other step to ensure a difference between previous and current values; used to 
		// trigger flaps and gear sounds
		if (recordToPreviousStep) { 
			soundValues.put(SoundCategory.PREV_STEP_FLAPS, flightData.get(FlightDataType.FLAPS));
			soundValues.put(SoundCategory.PREV_STEP_GEAR, flightData.get(FlightDataType.GEAR));
		} recordToPreviousStep ^= true; 

		// Update gain/pitch of each sound
		setRPM();
		setControl(SoundEvent.FLAPS);
		setControl(SoundEvent.GEAR);
		setWind();
		setStallHorn(Math.PI/17);
	}
	
	/**
	 * Sounds the stall warning if angle of attack (radians) passes a specified threshold (radians)
	 * 
	 * @param alpha
	 * @param threshold
	 */
	public void setStallHorn(double threshold) {
		double alpha = soundValues.get(SoundCategory.STALL_HORN);
		
		if ((alpha > threshold) && !(soundSources.get(SoundEvent.STALL).isPlaying()))
			soundSources.get(SoundEvent.STALL).play();
		else if ((alpha < threshold) && (soundSources.get(SoundEvent.STALL).isPlaying()))
			soundSources.get(SoundEvent.STALL).stop();
	}
	
	/**
	 * Plays sound for a specified control deflection if the difference between the received value
	 * and the PREV_STEP_* value is greater than 0, indicating a change in deflection over the integration step
	 * 
	 * @param event
	 */
	public void setControl(SoundEvent event) {
		double currentControlValue = 0.0, previousControlValue = 0.0;
		
		switch(event) {
		case FLAPS:
			currentControlValue = soundValues.get(SoundCategory.FLAPS);
			previousControlValue = soundValues.get(SoundCategory.PREV_STEP_FLAPS);
			break;
		case GEAR:
			currentControlValue = soundValues.get(SoundCategory.GEAR);
			previousControlValue = soundValues.get(SoundCategory.PREV_STEP_GEAR);
			break;
		default:
			break;
		}
		
		boolean dXdt = (Math.abs(currentControlValue-previousControlValue) > 0);
		
		if (dXdt && !soundSources.get(event).isPlaying())
			soundSources.get(event).play();
	}
	
	/**
	 * Sets volume of wind as a function of true airspeed (kts)
	 */
	public void setWind() {
		double trueAirspeed = soundValues.get(SoundCategory.WIND);
		
		float gainWind = (float) ((trueAirspeed >  50 && trueAirspeed < 300) ? ((2.0-0.5)*(trueAirspeed-50))/(300-50) + 0.5 : 0);
		soundSources.get(SoundEvent.WIND).setVolume(gainWind*=environmentVolume);
	}
	
	/**
	 * Uses sound blending with cosine and linear functions with volume and pitch properties, respectively 
	 * to mesh together engine sounds as a function of RPM
	 */
	public void setRPM() {
		float gainLow, pitchLow, gainMed, pitchMed, gainHi, pitchHi, gainMax, pitchMax;
		double RPM;
		int engineNumber;
		Set<Engine> engineList = aircraft.getEngines();
				
		for (Engine engine : engineList) {
			engineNumber = engine.getEngineNumber(); 
		
			SoundEvent engLow  = Enum.valueOf(SoundEvent.class, "ENGINE_" + engineNumber + "_LOW");
			SoundEvent engMed  = Enum.valueOf(SoundEvent.class, "ENGINE_" + engineNumber + "_MED");
			SoundEvent engHigh = Enum.valueOf(SoundEvent.class, "ENGINE_" + engineNumber + "_HIGH");
			SoundEvent engMax  = Enum.valueOf(SoundEvent.class, "ENGINE_" + engineNumber + "_MAX");
			
			SoundCategory rpmEnum = Enum.valueOf(SoundCategory.class, "RPM_" + engineNumber);
			
			RPM = soundValues.get(rpmEnum);
			
			gainLow  = (float) ((RPM >  300 && RPM < 1800) ? Math.cos((RPM-600)/500) : 0);
			pitchLow = (float) ((RPM >  300 && RPM < 1800) ? ((1.5-0.75)*(RPM-300))/(1800-300) + 0.75 : 0);
			soundSources.get(engLow).setVolume(gainLow*=engineVolume);
			soundSources.get(engLow).setPitch(pitchLow);
			
			gainMed  = (float) ((RPM > 600 && RPM < 2000) ? Math.cos((RPM-1500)/400) : 0);
			pitchMed = (float) ((RPM > 600 && RPM < 2000) ? ((1.5-0.75)*(RPM-600))/(2000-600) + 0.75 : 0);
			soundSources.get(engMed).setVolume(gainMed*=engineVolume);
			soundSources.get(engMed).setPitch(pitchMed);
			
			gainHi   = (float) ((RPM > 1500 && RPM < 2500) ? Math.cos((RPM-2000)/300) : 0);
			pitchHi  = (float) ((RPM > 1500 && RPM < 2500) ? ((1.5-0.75)*(RPM-1500))/(2500-1500) + 0.75 : 0);
			soundSources.get(engHigh).setVolume(gainHi*=engineVolume);		
			soundSources.get(engHigh).setPitch(pitchHi);
			
			gainMax  = (float) ((RPM > 1900 && RPM < 3000) ? Math.cos((RPM-2600)/400)*2 : 0);
			pitchMax = (float) ((RPM > 1900 && RPM < 3000) ? ((1.25-0.95)*(RPM-1900))/(3000-1900) + 0.95 : 0);
			soundSources.get(engMax).setVolume(gainMax*=engineVolume);
			soundSources.get(engMax).setPitch(pitchMax);
		}
	}

	public void play(SoundEvent event) {
		soundSources.get(event).play();
	}
	
	public void stop(SoundEvent event) {
		soundSources.get(event).stop();
	}
	
	public void setPitch(SoundEvent event, float pitch) {
		soundSources.get(event).setPitch(pitch);
	}
	
	public void setVolume(SoundEvent event, float volume) {
		soundSources.get(event).setVolume(volume);
	}
	
	public void setPosition(SoundEvent event, Vector3f position) {
		soundSources.get(event).setPosition(position);
	}
	
	public void setVelocity(SoundEvent event, Vector3f velocity) {
		soundSources.get(event).setVelocity(velocity);
	}
	
	public void cleanUp() {
		for (Map.Entry<SoundEvent, SoundSource> entry : soundSources.entrySet())
			entry.getValue().delete();
	}
}
