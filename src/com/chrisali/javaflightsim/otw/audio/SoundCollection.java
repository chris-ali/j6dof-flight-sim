package com.chrisali.javaflightsim.otw.audio;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import org.lwjgl.util.vector.Vector3f;

import com.chrisali.javaflightsim.controllers.SimulationController;
import com.chrisali.javaflightsim.datatransfer.FlightData;
import com.chrisali.javaflightsim.datatransfer.FlightDataListener;
import com.chrisali.javaflightsim.menus.optionspanel.AudioOptions;
import com.chrisali.javaflightsim.otw.RunWorld;
import com.chrisali.javaflightsim.simulation.aircraft.AircraftBuilder;
import com.chrisali.javaflightsim.simulation.propulsion.Engine;

/**
 * Static class that contains a repository of sounds to be played by triggering certain events, such
 * as control surface deflections, engine properties or change in airspeed
 * 
 * @author Christopher Ali
 *
 */
public class SoundCollection {
	
	/**
	 * Inner enums used to identify {@link SoundSource} objects in the soundSources
	 * EnumMap 
	 * 
	 * @author Christopher Ali
	 *
	 */
	public static enum SoundEvent {
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
	public static enum SoundCategory {
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
	
	private static float engineVolume;
	private static float systemsVolume;
	private static float environmentVolume;
	
	private static Map<SoundEvent, SoundSource> soundSources = new EnumMap<>(SoundEvent.class);
	
	/**
	 *	Fills soundSources EnumMap with {@link SoundSource} objects, which are references to audio
	 *  files in .Resources/Audio, and sets their initial properties. Uses {@link SimulationController}
	 *  to get {@link AircraftBuilder} reference to determine how many engines to assign sounds to, and where 
	 *  to position them relative to the listener. Also uses {@link SimulationController} to get Map of audio 
	 *  options to set the volumes of various types of sounds
	 *  
	 *  @param controller
	 */
	public static void initializeSounds(SimulationController controller) {
		
		Map<AudioOptions, Float> audioOptions = controller.getAudioOptions();
		AircraftBuilder ab = controller.getAircraftBuilder();
		
		engineVolume = audioOptions.get(AudioOptions.ENGINE_VOLUME);
		systemsVolume = audioOptions.get(AudioOptions.SYSTEMS_VOLUME);
		environmentVolume = audioOptions.get(AudioOptions.ENVIRONMENT_VOLUME);
		
		//================================ Engine =========================================
		
		Vector3f enginePosVector = new Vector3f();
		int engineNumber;
		double[] enginePosition;
		Set<Engine> engineList = ab.getEngineList();
				
		for (Engine engine : engineList) {
			engineNumber    = engine.getEngineNumber(); 
			enginePosition  = engine.getEnginePosition();
			enginePosVector = new Vector3f((float) enginePosition[0]/5, (float) enginePosition[1]/5, (float) enginePosition[2]/5);
			
			SoundEvent engLow  = Enum.valueOf(SoundEvent.class, "ENGINE_" + engineNumber + "_LOW");
			SoundEvent engMed  = Enum.valueOf(SoundEvent.class, "ENGINE_" + engineNumber + "_MED");
			SoundEvent engHigh = Enum.valueOf(SoundEvent.class, "ENGINE_" + engineNumber + "_HIGH");
			SoundEvent engMax  = Enum.valueOf(SoundEvent.class, "ENGINE_" + engineNumber + "_MAX");
			
			soundSources.put(engLow, new SoundSource("Audio", "engineLow"));
			soundSources.get(engLow).setVolume(0);
			soundSources.get(engLow).setLooping(true);
			soundSources.get(engLow).play();
			soundSources.get(engLow).setPosition(enginePosVector);
			
			soundSources.put(engMed, new SoundSource("Audio", "engineMed"));
			soundSources.get(engMed).setVolume(0);
			soundSources.get(engMed).setLooping(true);
			soundSources.get(engMed).play();
			soundSources.get(engMed).setPosition(enginePosVector);
			
			soundSources.put(engHigh, new SoundSource("Audio", "engineHigh"));
			soundSources.get(engHigh).setVolume(0);
			soundSources.get(engHigh).setLooping(true);
			soundSources.get(engHigh).play();
			soundSources.get(engHigh).setPosition(enginePosVector);
			
			soundSources.put(engMax, new SoundSource("Audio", "engineMax"));
			soundSources.get(engMax).setVolume(0);
			soundSources.get(engMax).setLooping(true);
			soundSources.get(engMax).play();
			soundSources.get(engMax).setPosition(enginePosVector);
		}
			
		
		//================================ Systems =========================================
		
		soundSources.put(SoundEvent.FLAPS, new SoundSource("Audio", "flap"));
		soundSources.get(SoundEvent.FLAPS).setVolume(0.5f*systemsVolume);
		
		soundSources.put(SoundEvent.GEAR, new SoundSource("Audio", "gear"));
		soundSources.get(SoundEvent.GEAR).setVolume(0.5f*systemsVolume);
		
		soundSources.put(SoundEvent.STALL, new SoundSource("Audio", "stall"));
		soundSources.get(SoundEvent.STALL).setVolume(0.5f*systemsVolume);
		soundSources.get(SoundEvent.STALL).setLooping(true);
		
		soundSources.put(SoundEvent.GYRO, new SoundSource("Audio", "gyroLoop"));
		soundSources.get(SoundEvent.GYRO).setVolume(0.25f*systemsVolume);
		soundSources.get(SoundEvent.GYRO).setLooping(true);
		soundSources.get(SoundEvent.GYRO).play();
		
		//================================ Environment ======================================
		
		soundSources.put(SoundEvent.WIND, new SoundSource("Audio", "wind"));
		soundSources.get(SoundEvent.WIND).setVolume(0.25f);
		soundSources.get(SoundEvent.WIND).setLooping(true);
		soundSources.get(SoundEvent.WIND).play();
		
	}
	
	/**
	 * Wrapper method to call setRPM(), setControl(), setWind() and setStallHorn() at once;
	 * uses an EnumMap of {@link SoundCategory} enums to set the double values retrieved by 
	 * {@link FlightDataListener} in {@link RunWorld}. Uses {@link AircraftBuilder} from 
	 * {@link SimulationController} argument to set RPM values
	 * 
	 * @param soundValues
	 * @param controller
	 */
	public static void update(Map<SoundCategory, Double> soundValues, SimulationController controller) {
		if (!soundValues.isEmpty()) {
			setRPM(controller.getAircraftBuilder(), soundValues);
			setControl(SoundEvent.FLAPS, soundValues);
			setControl(SoundEvent.GEAR, soundValues);
			setWind(soundValues.get(SoundCategory.WIND));
			setStallHorn(soundValues.get(SoundCategory.STALL_HORN), Math.PI/17);
		}
	}
	
	/**
	 * Sounds the stall warning if angle of attack (radians) passes a specified threshold (radians)
	 * 
	 * @param alpha
	 * @param threshold
	 */
	public static void setStallHorn(double alpha, double threshold) {
		if ((alpha > threshold) && !(soundSources.get(SoundEvent.STALL).isPlaying()))
			soundSources.get(SoundEvent.STALL).play();
		else if ((alpha < threshold) && (soundSources.get(SoundEvent.STALL).isPlaying()))
			soundSources.get(SoundEvent.STALL).stop();
	}
	
	/**
	 * Plays sound for a specified control deflection if the difference between the received value (control)
	 * and the previously received value is greater than 0, indicating a change in deflection over the integration step
	 * 
	 * @param event
	 * @param controls
	 */
	public static void setControl(SoundEvent event, Map<SoundCategory, Double> controls) {
		double currentControlValue = 0.0, previousControlValue = 0.0;
		
		switch(event) {
		case FLAPS:
			currentControlValue = controls.get(SoundCategory.FLAPS);
			previousControlValue = controls.get(SoundCategory.PREV_STEP_FLAPS);
			break;
		case GEAR:
			currentControlValue = controls.get(SoundCategory.GEAR);
			previousControlValue = controls.get(SoundCategory.PREV_STEP_GEAR);
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
	 * 
	 * @param trueAirspeed
	 */
	public static void setWind(double trueAirspeed) {
		float gainWind = (float) ((trueAirspeed >  50 && trueAirspeed < 300) ? ((2.0-0.25)*(trueAirspeed-50))/(300-50) + 0.25 : 0);
		soundSources.get(SoundEvent.WIND).setVolume(gainWind*=environmentVolume);
	}
	
	/**
	 * Uses sound blending with cosine and linear functions with volume and pitch properties, respectively 
	 * to mesh together engine sounds as a function of RPM
	 * 
	 * @param ab
	 * @param RPM
	 */
	public static void setRPM(AircraftBuilder ab, Map<SoundCategory, Double> soundValues) {
		float gainLow, pitchLow, gainMed, pitchMed, gainHi, pitchHi, gainMax, pitchMax;
		double RPM;
		int engineNumber;
		Set<Engine> engineList = ab.getEngineList();
				
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

	public static void play(SoundEvent event) {
		soundSources.get(event).play();
	}
	
	public static void stop(SoundEvent event) {
		soundSources.get(event).stop();
	}
	
	public static void setPitch(SoundEvent event, float pitch) {
		soundSources.get(event).setPitch(pitch);
	}
	
	public static void setVolume(SoundEvent event, float volume) {
		soundSources.get(event).setVolume(volume);
	}
	
	public static void setPosition(SoundEvent event, Vector3f position) {
		soundSources.get(event).setPosition(position);
	}
	
	public static void setVelocity(SoundEvent event, Vector3f velocity) {
		soundSources.get(event).setVelocity(velocity);
	}
	
	public static void cleanUp() {
		for (Map.Entry<SoundEvent, SoundSource> entry : soundSources.entrySet())
			entry.getValue().delete();
	}
}
