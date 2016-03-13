package ui;

import game.Paths;
import graphics.SimpleFont;
import graphics.Sprite2D;
import graphics.Symbol;

import java.util.HashMap;

import managers.PlayerManager;

import org.json.JSONArray;
import org.json.JSONObject;

import utils.ConfigManager;
import utils.Vector2;
import dialogs.Component;
import dialogs.Label;
import dialogs.SpriteComponent;
import engine.BaseGame;
import engine.OverloadEngine;

public class Overlay extends Component{
	private HashMap<Character, Symbol> overlayFont;
	private Label scoreTexts[];
	
	public Overlay(BaseGame game) {
		super(game);
	}

	protected void initialize() {
		setPosition(Vector2.one);
		
		JSONObject overlayJson = ConfigManager.loadConfigAsJson(Paths.UI + "overlay.json");
		Sprite2D sheet = new Sprite2D(Paths.SPRITESHEETS + overlayJson.getString("spritesheet"));
		
		int x, y, w, h;
		
		JSONObject background = overlayJson.getJSONObject("background");
		x = background.getInt("x");
		y = background.getInt("y");
		w = background.getInt("w");
		h = background.getInt("h");
		Sprite2D bgSprite = Sprite2D.getSpriteFromSheet(x, y, w, h, sheet);
		bgSprite.setInternalScale(OverloadEngine.frameWidth, OverloadEngine.frameHeight * 0.12f);
		SpriteComponent bgComponent = new SpriteComponent(game);
		bgComponent.setSprite(bgSprite);
		bgComponent.setPosition(Vector2.pixelCoordsToNormal(new Vector2(OverloadEngine.frameWidth * 0.5f, OverloadEngine.frameHeight * 0.6f)));
		addChild(bgComponent);
		
		// Load score indicator icons
		JSONArray scoreArrayJson = overlayJson.getJSONArray("score");
		for (int i = 0; i < scoreArrayJson.length(); ++i){
			JSONObject scoreJson = scoreArrayJson.getJSONObject(i);
			x = scoreJson.getInt("x");
			y = scoreJson.getInt("y");
			w = scoreJson.getInt("w");
			h = scoreJson.getInt("h");
			Sprite2D scoreSprite = Sprite2D.getSpriteFromSheet(x, y, w, h, sheet);
			
			JSONArray posJson = scoreJson.getJSONArray("pos");
			Vector2 pos = Vector2.pixelCoordsToNormal(new Vector2(posJson.getInt(0), posJson.getInt(1)));
			
			SpriteComponent scoreComponent = new SpriteComponent(game);
			scoreComponent.setSprite(scoreSprite);
			scoreComponent.setPosition(pos);
			
			addChild(scoreComponent);
		}

		// Load score texts
		overlayFont = SimpleFont.createFont(Paths.SPRITESHEETS + "spritesheet_overlay.png", Paths.FONTS + "overlay_font.json");
		scoreTexts = new Label[PlayerManager.NUM_PLAYERS];
		
		JSONArray textArrayJson = overlayJson.getJSONArray("scoreTexts");
		for (int i = 0; i < textArrayJson.length(); ++i){
			JSONObject textJson = textArrayJson.getJSONObject(i);
			x = textJson.getInt("x");
			y = textJson.getInt("y");
			
			Vector2 pos = Vector2.pixelCoordsToNormal(new Vector2(x, y));
			SimpleFont textFont = new SimpleFont("0", overlayFont);
			Label textLabel = new Label(game, textFont);
			textLabel.setPosition(pos);
			
			addChild(textLabel);
			scoreTexts[i] = textLabel;
		}
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
	}
}
