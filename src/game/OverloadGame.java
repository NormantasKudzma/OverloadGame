package game;

import managers.MapManager;
import managers.PlayerManager;
import managers.WeaponManager;
import mapping.GameMap;
import mapping.GameMap.Layer;
import ui.Overlay;
import utils.Vector2;
import engine.BaseGame;
import entities.WeaponEntity;

public class OverloadGame extends BaseGame {
	public static Vector2 GRAVITY = new Vector2(0.0f, -4.0f);
	
	private MapManager mapManager = new MapManager(this);
	private PlayerManager playerManager = new PlayerManager(this);
	private WeaponManager weaponManager = new WeaponManager(this);
	
	private GameMap map;
	private Overlay overlay;
	
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
		map = mapManager.loadMap(Paths.MAPS + "Map_02.json", entityList);
		
		addEntity(playerManager.getPlayer(0));		
		addEntity(playerManager.getPlayer(1));		
		addEntity(playerManager.getPlayer(2));	
		addEntity(playerManager.getPlayer(3));
		
		overlay = new Overlay(this);
	}
	
	@Override
	protected void renderGame() {
		map.renderLayer(Layer.ZERO);
		map.renderLayer(Layer.BACKGROUND);
		map.renderLayer(Layer.MIDDLE);
		super.renderGame();
		map.renderLayer(Layer.FOREGROUND);
		overlay.render();
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		overlay.update(deltaTime);
		map.update(deltaTime);
	}
}
