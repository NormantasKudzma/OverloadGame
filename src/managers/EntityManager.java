package managers;

import engine.BaseGame;
import graphics.Sprite2D;
import utils.Vector2;

public class EntityManager {
	public static final int NO_COLLISIONS		= 0x0000_0000;
	public static final int ALL_COLLISIONS		= ~NO_COLLISIONS;
	
	// Player colliders (collide with anything that is not a player)
	public static final int PLAYER1_CATEGORY 	= 0x0000_0001;
	public static final int PLAYER2_CATEGORY 	= 0x0000_0002;
	public static final int PLAYER3_CATEGORY 	= 0x0000_0004;
	public static final int PLAYER4_CATEGORY 	= 0x0000_0008;
	public static final int PLAYER_COLLIDER		= ~(PLAYER1_CATEGORY | PLAYER2_CATEGORY | PLAYER3_CATEGORY | PLAYER4_CATEGORY);
	
	// Wall collider (collide with everything that is not a wall)
	public static final int WALL_CATEGORY 		= 0x0000_0010;
	public static final int WALL_COLLIDER		= ~WALL_CATEGORY;
	
	// Weapon collider (collide with players)
	public static final int WEAPON_CATEGORY		= 0x0000_0020;
	public static final int WEAPON_COLLIDER		= ~PLAYER_COLLIDER;
	
	// Ammo collider (don't collide with anything)
	public static final int AMMO_CATEGORY		= 0x0000_0040;
	public static final int AMMO_COLLIDER		= NO_COLLISIONS;
	
	// Bullet collider (collide with walls and players)
	public static final int BULLET_CATEGORY		= 0x0000_0080;
	public static final int BULLET_COLLIDER		= (~PLAYER_COLLIDER | WALL_CATEGORY) & ~BULLET_CATEGORY;	
	
	protected BaseGame game;
	
	public EntityManager(BaseGame game){
		this.game = game;
	}
	
	public Sprite2D getSpriteFromSheet(int x, int y, int w, int h, Sprite2D sheet){
		Vector2 sheetSizeCoef = new Vector2(sheet.getTexture().getWidth(), sheet.getTexture().getHeight());
		sheetSizeCoef.div(sheet.getTexture().getImageWidth(), sheet.getTexture().getImageHeight());
		
		Vector2 topLeft = new Vector2(x, y);
		Vector2 botRight = topLeft.copy().add(new Vector2(w, h));
		
		topLeft.mul(sheetSizeCoef);
		botRight.mul(sheetSizeCoef);
		
		Sprite2D sprite = new Sprite2D(sheet.getTexture(), topLeft, botRight);
		sprite.setInternalScale(w, h);
		return sprite;
	}
}
