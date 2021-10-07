package com.sonicether.soundphysics.config;

public class ReverbParams
{
	public float decayTime;				// min: 0.1f 	max: 10.0f
	public float density;				// min: 0.0f 	max: 1.0f
	public float diffusion;				// min: 0.0f 	max: 1.0f
	public float gain;					// min: 0.0f 	max: 1.0f
	public float gainHF;				// min: 0.0f 	max: 1.0f
	public float decayHFRatio;			// min: 0.1f 	max: 2.0f
	public float reflectionsGain;		// min: 0.1f 	max: 3.16f
	public float reflectionsDelay;		// min: 0.0f 	max: 0.3f
	public float lateReverbGain;		// min: 0.0f 	max: 10.0f
	public float lateReverbDelay;		// min: 0.0f 	max: 0.1f
	public float airAbsorptionGainHF;	// min: 0.892f 	max: 1.0f
	public float roomRolloffFactor; 	// min: 0.0f	max: 10.0f
	
	public static ReverbParams getReverb0()
	{
		ReverbParams r = new ReverbParams();
		r.decayTime = 0.15f;
		r.density = 0.0f;
		r.diffusion = 1.0f;
		r.gain = 0.2f * globalReverbMultiplier() * 0.85f;
		r.gainHF = 0.99f;
		r.decayHFRatio = (float) (0.6f * ConfigManager.getConfig().General.globalReverbBrightness);
		r.reflectionsGain = 2.5f;
		r.reflectionsDelay = 0.001f;
		r.lateReverbGain = 1.26f;
		r.lateReverbDelay = 0.011f;
		r.airAbsorptionGainHF = 0.994f;
		r.roomRolloffFactor = (float) (0.16f * ConfigManager.getConfig().General.attenuationFactor);
		
		return r;
	}
	
	public static ReverbParams getReverb1()
	{
		ReverbParams r = new ReverbParams();
		r.decayTime = 0.55f;
		r.density = 0.0f;
		r.diffusion = 1.0f;
		r.gain = 0.3f * globalReverbMultiplier() * 0.85f;
		r.gainHF = 0.99f;
		r.decayHFRatio = (float) (0.7f * ConfigManager.getConfig().General.globalReverbBrightness);
		r.reflectionsGain = 0.2f;
		r.reflectionsDelay = 0.015f;
		r.lateReverbGain = 1.26f;
		r.lateReverbDelay = 0.011f;
		r.airAbsorptionGainHF = 0.994f;
		r.roomRolloffFactor = (float) (0.15f * ConfigManager.getConfig().General.attenuationFactor);
		
		return r;
	}
	
	public static ReverbParams getReverb2()
	{
		ReverbParams r = new ReverbParams();
		r.decayTime = 1.68f;
		r.density = 0.1f;
		r.diffusion = 1.0f;
		r.gain = 0.5f * globalReverbMultiplier() * 0.85f;
		r.gainHF = 0.99f;
		r.decayHFRatio = (float) (0.7f * ConfigManager.getConfig().General.globalReverbBrightness);
		r.reflectionsGain = 0.0f;
		r.reflectionsDelay = 0.021f;
		r.lateReverbGain = 1.26f;
		r.lateReverbDelay = 0.021f;
		r.airAbsorptionGainHF = 0.994f;
		r.roomRolloffFactor = (float) (0.13f * ConfigManager.getConfig().General.attenuationFactor);
		
		return r;
	}
	
	public static ReverbParams getReverb3()
	{
		ReverbParams r = new ReverbParams();
		r.decayTime = 4.142f;
		r.density = 0.5f;
		r.diffusion = 1.0f;
		r.gain = 0.4f * globalReverbMultiplier() * 0.85f;
		r.gainHF = 0.89f;
		r.decayHFRatio = (float) (0.7f * ConfigManager.getConfig().General.globalReverbBrightness);
		r.reflectionsGain = 0.0f;
		r.reflectionsDelay = 0.025f;
		r.lateReverbGain = 1.26f;
		r.lateReverbDelay = 0.021f;
		r.airAbsorptionGainHF = 0.994f;
		r.roomRolloffFactor = (float) (0.11f * ConfigManager.getConfig().General.attenuationFactor);
		
		return r;
	}
	
	ReverbParams() { }

	private static float globalReverbMultiplier() { return (float) (0.7f * ConfigManager.getConfig().General.globalReverbGain); }
}
