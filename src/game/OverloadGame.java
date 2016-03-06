package game;

import engine.BaseGame;
import entities.WeaponEntity;
import managers.MapManager;
import managers.PlayerManager;
import managers.WeaponManager;
import mapping.GameMap;
import mapping.GameMap.Layer;
import utils.Vector2;

public class OverloadGame extends BaseGame {
	public static Vector2 GRAVITY = new Vector2(0.0f, -4.0f);
	
	private MapManager mapManager = new MapManager();
	private PlayerManager playerManager = new PlayerManager();
	private WeaponManager weaponManager = new WeaponManager();
	
	private GameMap map;
	
	@Override
	public void init() {
		super.init();
		physicsWorld.getWorld().setGravity(GRAVITY.toVec2());
		playerManager.loadPlayers();
		weaponManager.loadWeapons();
		map = mapManager.loadMap(Paths.MAPS + "Map_01.json", entityList);
		
		addEntity(playerManager.getPlayer(0));
		playerManager.getPlayer(0).setPosition(new Vector2(0.8f, 1.5f));
		
		WeaponEntity weap = weaponManager.getWeapon("shotgun");
		weap.getPhysicsBody().getBody().setActive(true);
		weap.setPosition(new Vector2(1.0f, 1.8f));
		addEntity(weap);
	}
	
	@Override
	protected void renderGame() {
		map.renderLayer(Layer.BACKGROUND);
		map.renderLayer(Layer.MIDDLE);
		super.renderGame();
		map.renderLayer(Layer.FOREGROUND);
	}
}
