package managers;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import utils.ConfigManager;
import utils.Vector2;
import engine.BaseGame;
import engine.Entity;
import entities.WeaponEntity;
import game.Paths;
import graphics.Sprite2D;
import graphics.SpriteAnimation;

public class WeaponManager extends EntityManager {
	private HashMap<String, WeaponEntity> weaponMap = new HashMap<String, WeaponEntity>();
	
	public WeaponManager(BaseGame game){
		super(game);
	}

	private void attachCollider(JSONObject childJson, Entity e) {
		if (childJson.has("collider")){
			JSONArray collJson = childJson.getJSONArray("collider");
			Vector2 verts[] = new Vector2[4];
			verts[0] = new Vector2(collJson.getInt(0), collJson.getInt(1));
			verts[1] = new Vector2(collJson.getInt(2), collJson.getInt(1));
			verts[2] = new Vector2(collJson.getInt(2), collJson.getInt(3));
			verts[3] = new Vector2(collJson.getInt(0), collJson.getInt(3));
			
			Vector2 scale = e.getScale();
			for (int i = 0; i < verts.length; ++i){
				Vector2.pixelCoordsToNormal(verts[i]);
				verts[i].mul(scale);
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
		loadChildren(weaponFileJson, "bullets", sheet, BULLET_CATEGORY, BULLET_COLLIDER);
		loadChildren(weaponFileJson, "ammo", sheet, AMMO_CATEGORY, AMMO_COLLIDER);	
	}

	private void loadChildren(JSONObject weaponFileJson, String arrayName, Sprite2D sheet, int category, int mask) {
		JSONArray childrenArrayJson = weaponFileJson.getJSONArray(arrayName);
		JSONObject childJson = null;
		for (int i = 0; i < childrenArrayJson.length(); ++i){
			try {
				childJson = childrenArrayJson.getJSONObject(i);
				
				int x = childJson.getInt("x");
				int y = childJson.getInt("y");
				int w = childJson.getInt("w");
				int h = childJson.getInt("h");
				Sprite2D sprite = Sprite2D.getSpriteFromSheet(x, y, w, h, sheet);
				
				Object obj = Class.forName(childJson.getString("type")).getDeclaredConstructor(BaseGame.class).newInstance(game);
				Entity e = (Entity)obj;
				e.initEntity();
				e.setSprite(sprite);
				e.setScale(e.getScale().mul((float)childJson.getDouble("scale")));
				e.getPhysicsBody().getBody().setActive(false);
				attachCollider(childJson, e);
				e.setCollisionFlags(category, mask);
				
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

	private void loadWeapons(JSONObject weaponFileJson, Sprite2D sheet){
		JSONArray weaponArrayJson = weaponFileJson.getJSONArray("weapons");
		JSONObject weaponJson = null;
		for (int i = 0; i < weaponArrayJson.length(); ++i){
			try {
				weaponJson = weaponArrayJson.getJSONObject(i);
				
				// Load weapon sprite animations (with all states)
				JSONArray allAnimArrayJson = weaponJson.getJSONArray("animations");
				SpriteAnimation spriteAnim = new SpriteAnimation();
				Sprite2D animations[][] = new Sprite2D[allAnimArrayJson.length()][];
				for (int j = 0; j < allAnimArrayJson.length(); ++j){
					JSONArray animationArrayJson = allAnimArrayJson.getJSONArray(j);
					animations[j] = new Sprite2D[animationArrayJson.length()];
					for (int k = 0; k < animationArrayJson.length(); ++k){
						JSONObject animationJson = animationArrayJson.getJSONObject(k);
						int x = animationJson.getInt("x");
						int y = animationJson.getInt("y");
						int w = animationJson.getInt("w");
						int h = animationJson.getInt("h");
						animations[j][k] = Sprite2D.getSpriteFromSheet(x, y, w, h, sheet);
					}
				}
				spriteAnim.setSpriteArray(animations);
				
				Object obj = Class.forName(weaponJson.getString("type")).getDeclaredConstructor(BaseGame.class).newInstance(game);
				WeaponEntity e = (WeaponEntity)obj;
				e.initEntity();
				e.setSprite(spriteAnim);
				e.setScale(e.getScale().mul((float)weaponJson.getDouble("scale")));
				e.getPhysicsBody().getBody().setActive(false);
				attachCollider(weaponJson, e);
				e.setCollisionFlags(WEAPON_CATEGORY, WEAPON_COLLIDER);
				
				if (weaponJson.has("muzzleOffset")){
					JSONArray muzzleOffsetJson = weaponJson.getJSONArray("muzzleOffset");
					int xOffset = muzzleOffsetJson.getInt(0);
					int yOffset = -muzzleOffsetJson.getInt(1);
					Vector2 muzzleOffset = new Vector2(xOffset, yOffset);
					Vector2.pixelCoordsToNormal(muzzleOffset);
					e.setMuzzleOffset(muzzleOffset);
				}
				
				if (weaponJson.has("positionOffset")){
					JSONArray positionOffsetJson = weaponJson.getJSONArray("positionOffset");
					int xOffset = positionOffsetJson.getInt(0);
					int yOffset = -positionOffsetJson.getInt(1);
					Vector2 positionOffset = new Vector2(xOffset, yOffset);
					Vector2.pixelCoordsToNormal(positionOffset);
					e.setPositionOffset(positionOffset);
				}
				
				weaponMap.put(weaponJson.getString("name"), e);
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
	}
}
