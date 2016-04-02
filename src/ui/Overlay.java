package ui;

import game.OverloadGame;
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
import entities.PlayerEntity;
import entities.WeaponEntity;

public class Overlay extends Component{
	private HashMap<Character, Symbol> overlayFont;
	private Label ammoTexts[];
	private Label scoreTexts[];
	private int ammoValues[];
	private int scoreValues[];
	
	private float textUpdateTimer = 0.0f;
	
	public Overlay(BaseGame game) {
		super(game);
	}

	protected void initialize() {
		setPosition(new Vector2(0.0f, 1.92f));
		
		JSONObject overlayJson = ConfigManager.loadConfigAsJson(Paths.UI + "overlay.json");
		Sprite2D sheet = new Sprite2D(Paths.SPRITESHEETS + overlayJson.getString("spritesheet"));
		
		int x, y, w, h;		
		JSONArray backgroundArrayJson = overlayJson.getJSONArray("background");
		for (int i = 0; i < backgroundArrayJson.length(); ++i){
			JSONObject bgJson = backgroundArrayJson.getJSONObject(i);
			x = bgJson.getInt("x");
			y = bgJson.getInt("y");
			w = bgJson.getInt("w");
			h = bgJson.getInt("h");
			
			Sprite2D bgSprite = Sprite2D.getSpriteFromSheet(x, y, w, h, sheet);
			bgSprite.setInternalScale((int)(OverloadEngine.frameWidth * 0.25f), (int)(OverloadEngine.frameHeight * 0.08f));
			
			SpriteComponent bgComponent = new SpriteComponent(game);
			bgComponent.setSprite(bgSprite);
			JSONArray posJson = bgJson.getJSONArray("pos");
			bgComponent.setPosition(Vector2.fromJsonArray(posJson));
			addChild(bgComponent);
		}
		
		// Load score indicator icons
		JSONArray scoreArrayJson = overlayJson.getJSONArray("score");
		loadSpriteComponents(scoreArrayJson, sheet);

		JSONArray ammoArrayJson = overlayJson.getJSONArray("ammo");
		loadSpriteComponents(ammoArrayJson, sheet);

		// Load score texts
		overlayFont = SimpleFont.createFont(Paths.SPRITESHEETS + "spritesheet_overlay.png", Paths.FONTS + "overlay_font.json");
		scoreTexts = new Label[PlayerManager.NUM_PLAYERS];
		ammoTexts = new Label[PlayerManager.NUM_PLAYERS];
		ammoValues = new int[PlayerManager.NUM_PLAYERS];
		scoreValues = new int[PlayerManager.NUM_PLAYERS];
		
		JSONArray scoreTextArrayJson = overlayJson.getJSONArray("scoreTexts");
		loadLabels(scoreTextArrayJson, scoreTexts);
		
		JSONArray ammoTextArrayJson = overlayJson.getJSONArray("ammoTexts");
		loadLabels(ammoTextArrayJson, ammoTexts);
	}
	
	private void loadLabels(JSONArray json, Label array[]){
		for (int i = 0; i < json.length(); ++i){
			JSONObject textJson = json.getJSONObject(i);
			JSONArray posJson = textJson.getJSONArray("pos");
			JSONArray scaleJson = textJson.getJSONArray("scale");
			
			SimpleFont textFont = new SimpleFont("0", overlayFont);
			Label textLabel = new Label(game, textFont);
			textLabel.setPosition(Vector2.fromJsonArray(posJson));
			textLabel.setScale(Vector2.fromJsonArray(scaleJson));
			addChild(textLabel);
			array[i] = textLabel;
		}
	}
	
	private void loadSpriteComponents(JSONArray json, Sprite2D sheet){
		for (int i = 0; i < json.length(); ++i){
			JSONObject itemJson = json.getJSONObject(i);
			JSONArray posJson = itemJson.getJSONArray("pos");
			JSONArray scaleJson = itemJson.getJSONArray("scale");
			int x = itemJson.getInt("x");
			int y = itemJson.getInt("y");
			int w = itemJson.getInt("w");
			int h = itemJson.getInt("h");
			Sprite2D sprite = Sprite2D.getSpriteFromSheet(x, y, w, h, sheet);
			
			SpriteComponent spriteComponent = new SpriteComponent(game);
			spriteComponent.setSprite(sprite);
			spriteComponent.setPosition(Vector2.fromJsonArray(posJson));
			spriteComponent.setScale(Vector2.fromJsonArray(scaleJson));			
			addChild(spriteComponent);
		}
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		textUpdateTimer -= deltaTime;
		if (textUpdateTimer <= 0.0f){
			textUpdateTimer = 0.25f;
			updateTexts();
		}
	}
	
	public void updateTexts(){
		PlayerEntity player = null;
		PlayerManager manager = ((OverloadGame)game).getPlayerManager();
		WeaponEntity weapon = null;
		int numBullets = -1;
		
		for (int i = 0; i < PlayerManager.NUM_PLAYERS; ++i){
			player = manager.getPlayer(i);
			if (player != null){
				weapon = player.getWeapon();
				if (weapon != null){
					numBullets = weapon.getNumBullets();
					if (numBullets != ammoValues[i]){
						ammoValues[i] = numBullets;
						ammoTexts[i].setText("" + numBullets);
					}
				}
			}
		}
	}
}
