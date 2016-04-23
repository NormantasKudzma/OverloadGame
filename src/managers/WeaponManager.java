package managers;

import java.util.HashMap;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.json.JSONArray;
import org.json.JSONObject;

import physics.PhysicsBody;
import physics.PhysicsBody.EBodyType;
import utils.ConfigManager;
import utils.Vector2;
import engine.BaseGame;
import engine.Entity;
import entities.bullets.AmmoEntity;
import entities.bullets.BulletEntity;
import entities.weapons.WeaponEntity;
import game.Paths;
import graphics.Sprite;
import graphics.SpriteAnimation;

public class WeaponManager extends EntityManager {
	private HashMap<String, WeaponEntity> weaponMap = new HashMap<String, WeaponEntity>();
	
	public WeaponManager(BaseGame game){
		super(game);
	}

	private void attachCollider(JSONObject childJson, Entity e) {
		if (childJson.has("collider")){
			JSONArray collJson = childJson.getJSONArray("collider");
			if (collJson.length() == 4){
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
			else {
				Vector2 pos = new Vector2(collJson.getInt(0), collJson.getInt(1));
				Vector2 r = new Vector2(collJson.getInt(2), 0);
				r = Vector2.pixelCoordsToNormal(r);
				e.getPhysicsBody().attachCircleCollider(pos, r.x, true);
			}
		}
	}
	
	public void destroy(){
		for (WeaponEntity i : weaponMap.values()){
			i.destroy();
		}
		weaponMap.clear();
	}
	
	public WeaponEntity getWeapon(String name){
		WeaponEntity weapon = weaponMap.get(name);
		if (weapon != null){
			return (WeaponEntity)weapon;
		}
		
		return null;
	}
	
	public HashMap<String, WeaponEntity> getWeaponMap(){
		return weaponMap;
	}
	
	public void loadWeapons(){
		JSONObject weaponFileJson = ConfigManager.loadConfigAsJson(Paths.WEAPONS + "Weapons.json");
		Sprite sheet = new Sprite(Paths.SPRITESHEETS + weaponFileJson.getString("spritesheet"));
		
		loadWeapons(weaponFileJson, sheet);
		loadChildren(weaponFileJson, "bullets", sheet, EBodyType.INTERACTIVE, BULLET_CATEGORY, BULLET_COLLIDER);
		loadChildren(weaponFileJson, "ammo", sheet, EBodyType.INTERACTIVE, AMMO_CATEGORY, AMMO_COLLIDER);
		loadChildren(weaponFileJson, "effects", sheet, EBodyType.NON_INTERACTIVE, NO_COLLISIONS, NO_COLLISIONS);	
	}

	private void loadChildren(JSONObject weaponFileJson, String arrayName, Sprite sheet, EBodyType type, int category, int mask) {
		JSONArray childrenArrayJson = weaponFileJson.getJSONArray(arrayName);
		JSONObject childJson = null;
		for (int i = 0; i < childrenArrayJson.length(); ++i){
			try {
				childJson = childrenArrayJson.getJSONObject(i);
				
				Object obj = Class.forName(childJson.getString("type")).getDeclaredConstructor(BaseGame.class).newInstance(game);
				Entity e = (Entity)obj;
				e.initEntity(type);
				if (type == EBodyType.INTERACTIVE){
					e.getPhysicsBody().getBody().setActive(false);
				}
				
				e.setScale(e.getScale().mul((float)childJson.getDouble("scale")));
				
				if (childJson.has("animations")){
					JSONArray animArrayJson = childJson.getJSONArray("animations");
					SpriteAnimation spriteAnim = spriteAnimationFromJson(animArrayJson, sheet);
					e.setSprite(spriteAnim);
				}
				else {
					int x = childJson.getInt("x");
					int y = childJson.getInt("y");
					int w = childJson.getInt("w");
					int h = childJson.getInt("h");
					Sprite sprite = Sprite.getSpriteFromSheet(x, y, w, h, sheet);
					e.setSprite(sprite);
				}

				if (childJson.has("collider")){
					attachCollider(childJson, e);
					e.setCollisionFlags(category, mask);
				}
				
				if (childJson.has("parent")){
					WeaponEntity parent = weaponMap.get(childJson.getString("parent"));
					if (parent != null){
						parent.addChild(e);
					}
				}
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	private void loadWeapons(JSONObject weaponFileJson, Sprite sheet){
		JSONArray weaponArrayJson = weaponFileJson.getJSONArray("weapons");
		JSONObject weaponJson = null;
		for (int i = 0; i < weaponArrayJson.length(); ++i){
			try {
				weaponJson = weaponArrayJson.getJSONObject(i);
				
				// Load weapon sprite animations (with all states)
				JSONArray allAnimArrayJson = weaponJson.getJSONArray("animations");
				SpriteAnimation spriteAnim = spriteAnimationFromJson(allAnimArrayJson, sheet);
				
				Object obj = Class.forName(weaponJson.getString("type")).getDeclaredConstructor(BaseGame.class).newInstance(game);
				WeaponEntity e = (WeaponEntity)obj;
				e.initEntity(PhysicsBody.EBodyType.INTERACTIVE);
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

	public void scaleWeapons(Vector2 scale){
		for (WeaponEntity w : weaponMap.values()){
			w.setScale(w.getScale().mul(scale));
			w.getPhysicsBody().resizeColliders(scale);
			
			AmmoEntity ammo = w.getAmmo();
			if (ammo != null){
				ammo.setScale(ammo.getScale().mul(scale));
				ammo.getPhysicsBody().resizeColliders(scale);
			}
			
			BulletEntity bullet = w.getBullet();
			if (bullet != null){
				bullet.setScale(bullet.getScale().mul(scale));
				bullet.getPhysicsBody().resizeColliders(scale);
			}
		}
	}
	
	private SpriteAnimation spriteAnimationFromJson(JSONArray animArrayJson, Sprite sheet) {
		SpriteAnimation spriteAnim = new SpriteAnimation();
		Sprite animations[][] = new Sprite[animArrayJson.length()][];
		for (int j = 0; j < animArrayJson.length(); ++j){
			JSONArray animationArrayJson = animArrayJson.getJSONArray(j);
			animations[j] = new Sprite[animationArrayJson.length()];
			for (int k = 0; k < animationArrayJson.length(); ++k){
				JSONObject animationJson = animationArrayJson.getJSONObject(k);
				int x = animationJson.getInt("x");
				int y = animationJson.getInt("y");
				int w = animationJson.getInt("w");
				int h = animationJson.getInt("h");
				animations[j][k] = Sprite.getSpriteFromSheet(x, y, w, h, sheet);
			}
		}
		spriteAnim.setSpriteArray(animations);
		return spriteAnim;
	}
}
