package managers;

import entities.WeaponEntity;
import game.Entity;
import game.OverloadEngine;
import game.Paths;
import graphics.Sprite2D;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import utils.ConfigManager;
import utils.Vector2;

public class WeaponManager extends EntityManager {
	private HashMap<String, WeaponEntity> weaponMap = new HashMap<String, WeaponEntity>();
	
	public WeaponManager(){
		
	}

	private void attachCollider(JSONObject childJson, Entity e) {
		if (childJson.has("collider")){
			JSONArray collJson = childJson.getJSONArray("collider");
			Vector2 verts[] = new Vector2[4];
			verts[0] = new Vector2(collJson.getInt(0), collJson.getInt(1));
			verts[1] = new Vector2(collJson.getInt(2), collJson.getInt(1));
			verts[2] = new Vector2(collJson.getInt(2), collJson.getInt(3));
			verts[3] = new Vector2(collJson.getInt(0), collJson.getInt(3));
			
			for (int i = 0; i < verts.length; ++i){
				pixelCoordsToNormal(verts[i]);
			}
			
			e.getPhysicsBody().attachPolygonCollider(verts, true);
		}
	}
	
	public WeaponEntity getWeapon(String name){
		WeaponEntity weapon = weaponMap.get(name);
		if (weapon != null){
			return (WeaponEntity)weapon;
		}
		
		return null;
	}
	
	public void loadWeapons(){
		JSONObject weaponFileJson = ConfigManager.loadConfigAsJson(Paths.WEAPONS + "Weapons.json");
		Sprite2D sheet = new Sprite2D(Paths.SPRITESHEETS + weaponFileJson.getString("spritesheet"));
		
		loadWeapons(weaponFileJson, sheet);
		loadChildren(weaponFileJson, "bullets", sheet);
		loadChildren(weaponFileJson, "misc", sheet);	
	}

	private void loadChildren(JSONObject weaponFileJson, String arrayName, Sprite2D sheet) {
		JSONArray childrenArrayJson = weaponFileJson.getJSONArray(arrayName);
		JSONObject childJson = null;
		for (int i = 0; i < childrenArrayJson.length(); ++i){
			try {
				childJson = childrenArrayJson.getJSONObject(i);
				
				int x = childJson.getInt("x");
				int y = childJson.getInt("y");
				int w = childJson.getInt("w");
				int h = childJson.getInt("h");
				Sprite2D sprite = getSpriteFromSheet(x, y, w, h, sheet);
				
				Object obj = Class.forName(childJson.getString("type")).newInstance();
				Entity e = (Entity)obj;
				e.initEntity();
				e.setSprite(sprite);
				e.setScale(e.getScale().mul((float)childJson.getDouble("scale")).div(OverloadEngine.aspectRatio, 1.0f / OverloadEngine.aspectRatio));
				e.getPhysicsBody().getBody().setActive(false);
				attachCollider(childJson, e);
				
				WeaponEntity parent = weaponMap.get(childJson.getString("parent"));
				if (parent != null){
					parent.addChild(e);
				}
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	private void loadWeapons(JSONObject weaponFileJson, Sprite2D sheet) {	
		JSONArray weaponArrayJson = weaponFileJson.getJSONArray("weapons");
		JSONObject weaponJson = null;
		for (int i = 0; i < weaponArrayJson.length(); ++i){
			try {
				weaponJson = weaponArrayJson.getJSONObject(i);
				
				int x = weaponJson.getInt("x");
				int y = weaponJson.getInt("y");
				int w = weaponJson.getInt("w");
				int h = weaponJson.getInt("h");
				Sprite2D sprite = getSpriteFromSheet(x, y, w, h, sheet);
				
				Object obj = Class.forName(weaponJson.getString("type")).newInstance();
				WeaponEntity e = (WeaponEntity)obj;
				e.initEntity();
				e.setSprite(sprite);
				Vector2 ddddd = new Vector2(128, 32);
				pixelCoordsToNormal(ddddd);
				float dra = ddddd.ratio();
				//e.setScale(e.getScale().mul((float)weaponJson.getDouble("scale")).mul(ddddd));
				e.getPhysicsBody().getBody().setActive(false);
				attachCollider(weaponJson, e);
				weaponMap.put(weaponJson.getString("name"), e);
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
	}
}
