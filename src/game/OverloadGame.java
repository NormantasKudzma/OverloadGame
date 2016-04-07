package game;

import managers.MapManager;
import managers.PlayerManager;
import managers.WeaponManager;
import ui.Overlay;
import utils.Vector2;
import engine.BaseGame;
import graphics.Layer;

public class OverloadGame extends BaseGame {
	public static Vector2 GRAVITY = new Vector2(0.0f, -4.0f);
	
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
		playerManager.loadPlayers();
		weaponManager.loadWeapons();
		mapManager.loadMap(this, Paths.MAPS + "Map_02.json");	
		
		String playersLayer = mapManager.getPlayersLayer();
		addEntity(playerManager.getPlayer(0), playersLayer);		
		addEntity(playerManager.getPlayer(1), playersLayer);		
		addEntity(playerManager.getPlayer(2), playersLayer);	
		addEntity(playerManager.getPlayer(3), playersLayer);
		
		Layer overlayLayer = new Layer("overlay", 1);
		overlay = new Overlay(this);
		overlayLayer.addEntity(overlay);
		addLayer(overlayLayer);
	}
}
