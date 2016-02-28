package managers;

import entities.WallEntity;
import game.Entity;
import game.OverloadEngine;
import game.Paths;
import graphics.Sprite2D;

import java.util.ArrayList;
import java.util.HashMap;

import mapping.GameMap;
import mapping.GameMap.Layer;

import org.json.JSONArray;
import org.json.JSONObject;

import utils.ConfigManager;
import utils.Vector2;

public class MapManager extends EntityManager{
	public MapManager(){
		
	}
	
	public GameMap loadMap(String path, ArrayList<Entity> entityList){
		JSONObject json = ConfigManager.loadConfigAsJson(path);
		GameMap map = new GameMap(path);
		
		HashMap<String, Sprite2D> spriteSheets = new HashMap<String, Sprite2D>();
		HashMap<String, Entity> entities = new HashMap<String, Entity>();
		
		loadSpriteSheets(json, spriteSheets);
		loadEntities(json, spriteSheets, entities);
		loadLayer(json, GameMap.Layer.BACKGROUND, entities, map);
		loadLayer(json, GameMap.Layer.MIDDLE, entities, map);
		loadLayer(json, GameMap.Layer.FOREGROUND, entities, map);
		loadColliders(json, map);
		
		return map;
	}

	private void loadColliders(JSONObject json, GameMap map) {
		JSONArray colliderArrayJson = json.getJSONArray("colliders");
		JSONArray mapSizeJson = json.getJSONArray("mapsize");
		Vector2 mapSize = new Vector2((float)mapSizeJson.getDouble(0), (float)mapSizeJson.getDouble(1)).div(2.0f);
		WallEntity colliderEntity = new WallEntity();
		colliderEntity.initEntity();
		colliderEntity.setVisible(false);
		
		for (int i = 0; i < colliderArrayJson.length(); ++i){
			JSONArray collJson = colliderArrayJson.getJSONArray(i);
			Vector2[] collVerts = new Vector2[collJson.length()];
			for (int j = 0; j < collJson.length(); ++j){
				JSONObject coll = collJson.getJSONObject(j);
				collVerts[j] = new Vector2((float)coll.getDouble("x"), (float)coll.getDouble("y"));
				collVerts[j].div(mapSize);
			}
			colliderEntity.getBody().attachPolygonCollider(collVerts);
		}
		
		map.addEntity(Layer.MIDDLE, colliderEntity);
	}

	private void loadLayer(JSONObject json, Layer layer, HashMap<String, Entity> mapEntities, GameMap map) {
		JSONArray layerArrayJson = json.getJSONArray(layer.getLayerName());
		JSONArray mapSizeJson = json.getJSONArray("mapsize");
		Vector2 mapSize = new Vector2((float)mapSizeJson.getDouble(0), (float)mapSizeJson.getDouble(1)).div(2.0f);
		
		for (int i = 0; i < layerArrayJson.length(); ++i){
			try {
				JSONObject entityJson = layerArrayJson.getJSONObject(i);
				JSONArray scaleJson = entityJson.getJSONArray("scale");
				JSONArray positionJson = entityJson.getJSONArray("position");
				Entity e = mapEntities.get(entityJson.getString("entity"));
				Entity clone = (Entity)e.getClass().newInstance();
				clone.initEntity();
				clone.setSprite(e.getSprite());
				clone.setScale(e.getScale().copy().mul((float)scaleJson.getDouble(0), (float)scaleJson.getDouble(1)));
				clone.setScale(clone.getScale().mul(mapSize.x * 0.5f / mapSize.y, 1.0f));
				clone.setPosition(new Vector2((float)positionJson.getDouble(0), (float)positionJson.getDouble(1)).div(mapSize));
				map.addEntity(layer, clone);
				//System.out.printf("Entity pos[%s], scale[%s]\n", clone.getPosition(), clone.getScale());
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	private void loadEntities(JSONObject json, HashMap<String, Sprite2D> spriteSheets, HashMap<String, Entity> entities) {
		JSONArray entityArrayJson = json.getJSONArray("entities");
		
		for (int i = 0; i < entityArrayJson.length(); ++i){
			try {
				JSONObject entityJson = entityArrayJson.getJSONObject(i);
				Object obj = Class.forName(entityJson.getString("type")).newInstance();
				if (obj instanceof Entity){
					Entity e = (Entity) obj;
					e.initEntity();
					Sprite2D sheet = spriteSheets.get(entityJson.get("sheet"));
					int x = entityJson.getInt("x");
					int y = entityJson.getInt("y");
					int w = entityJson.getInt("w");
					int h = entityJson.getInt("h");
					e.setSprite(getSpriteFromSheet(x, y, w, h, sheet));
					// some magic (probably not)
					e.setScale(e.getScale().mul((float)entityJson.getDouble("scale")).div(OverloadEngine.aspectRatio, 1.0f / OverloadEngine.aspectRatio));
					entities.put(entityJson.getString("name"), e);
				}
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	private void loadSpriteSheets(JSONObject json, HashMap<String, Sprite2D> spriteSheets) {
		JSONArray spriteSheetJson = json.getJSONArray("spritesheets");
		
		for (int i = 0; i < spriteSheetJson.length(); ++i){
			JSONObject sheet = spriteSheetJson.getJSONObject(i);
			spriteSheets.put(sheet.getString("name"), new Sprite2D(Paths.SPRITESHEETS + sheet.getString("path")));
		}
	}
}
