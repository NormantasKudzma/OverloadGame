package managers;

import game.OverloadEngine;
import graphics.Sprite2D;
import utils.Vector2;

public class EntityManager {
	private static final float halfHeight = OverloadEngine.frameHeight * 0.5f;
	private static final float halfWidth = OverloadEngine.frameWidth * 0.5f;
	
	public EntityManager(){
		
	}
	
	public void pixelCoordsToNormal(Vector2 vec){
		vec.div(halfWidth, halfHeight);
	}
	
	public Sprite2D getSpriteFromSheet(int x, int y, int w, int h, Sprite2D sheet){
		Vector2 sheetSizeCoef = new Vector2(sheet.getTexture().getWidth(), sheet.getTexture().getHeight());
		sheetSizeCoef.div(sheet.getTexture().getImageWidth(), sheet.getTexture().getImageHeight());
		
		Vector2 topLeft = new Vector2(x, y);
		Vector2 botRight = topLeft.copy().add(new Vector2(w, h));
		
		topLeft.mul(sheetSizeCoef);
		botRight.mul(sheetSizeCoef);
		
		return new Sprite2D(sheet.getTexture(), topLeft, botRight);
	}
}
