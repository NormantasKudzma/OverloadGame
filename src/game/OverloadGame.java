package game;

import managers.MapManager;
import managers.PlayerManager;
import managers.WeaponManager;
import ui.GameStartDialog;
import ui.Overlay;
import utils.Vector2;
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
		
		GameStartDialog startDialog = new GameStartDialog(this, "start");
		startDialog.setPosition(Vector2.one);
		startDialog.setVisible(true);
		addDialog(startDialog);
	}
	
	public void loadMap(){
		mapManager.loadNextMap();
		overlay.gameStarting();
	}
}
