package game;

import java.util.ArrayList;

import managers.MapManager;
import managers.PlayerManager;
import managers.WeaponManager;
import ui.GameStartDialog;
import ui.Overlay;
import utils.Vector2;
import audio.MusicManager;
import audio.SoundManager;
import engine.BaseGame;
import graphics.Layer;

public class OverloadGame extends BaseGame {
	public static Vector2 GRAVITY = new Vector2(0.0f, -4.8f);
	public static final int MAX_POINTS = 3;
	
	private MapManager mapManager = new MapManager(this);
	private PlayerManager playerManager = new PlayerManager(this);
	private WeaponManager weaponManager = new WeaponManager(this);
	
	private Overlay overlay;
	
	public OverloadGame(){
		super();
	}
	
	@Override
	public void destroy() {
		mapManager.destroy();
		playerManager.destroy();
		weaponManager.destroy();
		super.destroy();
	}
	
	public MapManager getMapManager(){
		return mapManager;
	}
	
	public Overlay getOverlay(){
		return overlay;
	}
	
	public PlayerManager getPlayerManager(){
		return playerManager;
	}
	
	public WeaponManager getWeaponManager(){
		return weaponManager;
	}
	
	@Override
	public void init() {
		super.init();
		physicsWorld.getWorld().setGravity(GRAVITY.toVec2());
		
		Layer overlayLayer = new Layer("overlay", 1);
		overlay = new Overlay(this);
		overlayLayer.addEntity(overlay);
		addLayer(overlayLayer);
		
		playerManager.loadPlayers();
		weaponManager.loadWeapons();
		
		new Thread(){
			public void run() {
				loadMusic();
				loadSounds();
			};
		}.start();
		
		GameStartDialog startDialog = new GameStartDialog(this, "start");
		startDialog.setVisible(true);
		addDialog(startDialog);
	}
	
	private void loadMusic(){
		musicManager = new MusicManager<String>();
		
		ArrayList<String> musicList = new ArrayList<String>();
		musicList.add(Paths.MUSIC + "tr 2.ogg");
		//musicList.add(Paths.MUSIC + "Clouds in a dream.ogg");
		
		ArrayList<String> musicKeys = new ArrayList<String>();
		musicKeys.add("1");
		//musicKeys.add("2");

		musicManager.loadAll(musicList, musicKeys);
		musicManager.playAll(musicKeys, true, true);			
	}
	
	public void loadMap(){
		mapManager.loadNextMap();
		overlay.gameStarting();
	}
	
	private void loadSounds(){
		soundManager = new SoundManager<ESound>();
		
		for (ESound i : ESound.values()){
			soundManager.loadAudio(i.getPath(), i.getType(), i);
		}
	}
}
