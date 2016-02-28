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
	
	public void loadWeapons(){
		JSONObject weaponFileJson = ConfigManager.loadConfigAsJson(Paths.WEAPONS + "Weapons.json");
		Sprite2D sheet = new Sprite2D(Paths.SPRITESHEETS + weaponFileJson.getString("spritesheet"));
		
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
				e.setScale(e.getScale().mul((float)weaponJson.getDouble("scale")).div(OverloadEngine.aspectRatio, 1.0f / OverloadEngine.aspectRatio));
				weaponMap.put(weaponJson.getString("name"), e);
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
		
		JSONArray miscArrayJson = weaponFileJson.getJSONArray("misc");
		JSONObject miscJson = null;
		for (int i = 0; i < miscArrayJson.length(); ++i){
			try {
				miscJson = miscArrayJson.getJSONObject(i);
				
				int x = miscJson.getInt("x");
				int y = miscJson.getInt("y");
				int w = miscJson.getInt("w");
				int h = miscJson.getInt("h");
				Sprite2D sprite = getSpriteFromSheet(x, y, w, h, sheet);
				
				Object obj = Class.forName(miscJson.getString("type")).newInstance();
				Entity e = (Entity)obj;
				e.initEntity();
				e.setSprite(sprite);
				e.setScale(e.getScale().mul((float)weaponJson.getDouble("scale")).div(OverloadEngine.aspectRatio, 1.0f / OverloadEngine.aspectRatio));
				
				WeaponEntity parent = weaponMap.get(miscJson.getString("parent"));
				if (parent != null){
					parent.addChild(e);
				}
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
	}
}
