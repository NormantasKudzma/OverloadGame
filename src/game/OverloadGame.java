package game;

import mapping.GameMap;
import mapping.GameMap.Layer;
import mapping.MapLoader;
import utils.Vector2;
import entities.PlayerManager;

public class OverloadGame extends BaseGame {
	public static Vector2 GRAVITY = new Vector2(0.0f, -4.0f);
	
	private MapLoader mapLoader = new MapLoader();
	private PlayerManager playerManager = new PlayerManager();
	
	private GameMap map;
	
	@Override
	public void init() {
		super.init();
		physicsWorld.getWorld().setGravity(GRAVITY.toVec2());
		playerManager.loadPlayers();
		map = mapLoader.loadMap(Paths.MAPS + "Map_01.json", entityList);
		
		addEntity(playerManager.getPlayer(0));
		playerManager.getPlayer(0).setPosition(new Vector2(0.2f, 0.8f));
	}
	
	@Override
	protected void renderGame() {
		map.renderLayer(Layer.BACKGROUND);
		map.renderLayer(Layer.MIDDLE);
		super.renderGame();
		map.renderLayer(Layer.FOREGROUND);
	}
}
