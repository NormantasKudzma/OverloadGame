package game;

import managers.MapManager;
import managers.PlayerManager;
import managers.WeaponManager;

import org.lwjgl.input.Keyboard;

import ui.GameStartDialog;
import ui.Overlay;

import com.ovl.controls.Controller;
import com.ovl.controls.ControllerEventListener;
import com.ovl.controls.ControllerManager;
import com.ovl.controls.pc.KeyboardController;
import com.ovl.controls.pc.MouseController;
import com.ovl.engine.BaseGame;
import com.ovl.engine.OverloadEngine;
import com.ovl.graphics.Layer;
import com.ovl.utils.Vector2;

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
		
		MouseController c = (MouseController) ControllerManager.getInstance().getController(Controller.Type.TYPE_MOUSE);
		c.addKeybind(0, new ControllerEventListener() {
			@Override
			public void handleEvent(long eventArg, Vector2 pos, int... params) {
				if (params[0] == 1) {
					onClick(pos);
				}
			}		
		});

		c.setMouseMoveListener(new ControllerEventListener() {
			@Override
			public void handleEvent(long eventArg, Vector2 pos, int... params) {
				onHover(pos);
			}			
		});
		c.startController();
	
		Controller k = ControllerManager.getInstance().getController(Controller.Type.TYPE_KEYBOARD);
		k.addKeybind(Keyboard.KEY_ESCAPE, new ControllerEventListener(){
			@Override
			public void handleEvent(long eventArg, Vector2 pos, int... params) {
				OverloadEngine.getInstance().requestClose();
			}			
		});
		k.startController();
		
		Layer overlayLayer = new Layer("overlay", 1);
		overlay = new Overlay(this);
		overlayLayer.addObject(overlay);
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
		/*musicManager = new MusicManager<String>();
		
		ArrayList<String> musicList = new ArrayList<String>();
		musicList.add(Paths.MUSIC + "tr 2.ogg");
		//musicList.add(Paths.MUSIC + "Clouds in a dream.ogg");
		
		ArrayList<String> musicKeys = new ArrayList<String>();
		musicKeys.add("1");
		//musicKeys.add("2");

		musicManager.loadAll(musicList, musicKeys);
		musicManager.playAll(musicKeys, true, true);	*/		
	}
	
	public void loadMap(){
		mapManager.loadNextMap();
		overlay.gameStarting();
	}
	
	private void loadSounds(){
		/*soundManager = new SoundManager<ESound>();
		
		for (ESound i : ESound.values()){
			soundManager.loadAudio(i.getPath(), i.getType(), i);
		}*/
	}
}
