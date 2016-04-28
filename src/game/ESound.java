package game;

import audio.AudioManager.EAudioType;

public enum ESound {
	JUMP(Paths.SOUNDS + "jump.ogg", EAudioType.OGG),
	EXPLODE(Paths.SOUNDS + "explode.ogg", EAudioType.OGG),
	THROW(Paths.SOUNDS + "grenade_throw.ogg", EAudioType.OGG),
	LAND(Paths.SOUNDS + "land.wav", EAudioType.WAV),
	PISTOL_SHOOT(Paths.SOUNDS + "shoot_pistol.ogg", EAudioType.OGG),
	SHOTGUN_SHOOT(Paths.SOUNDS + "shoot_shotgun.wav", EAudioType.WAV),
	SWORD_SLASH(Paths.SOUNDS + "sword_slash.wav", EAudioType.WAV);
	
	private String path;
	private EAudioType type;
	
	private ESound(String path, EAudioType type){
		this.path = path;
		this.type = type;
	}
	
	public String getPath(){
		return path;
	}
	
	public EAudioType getType(){
		return type;
	}
}
