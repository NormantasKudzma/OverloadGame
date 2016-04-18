package ui;

import java.awt.Font;

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
import entities.weapons.WeaponEntity;
import game.OverloadGame;
import game.Paths;
import graphics.SimpleFont;
import graphics.Sprite2D;

public class Overlay extends Component{
	private Font overlayFont;
	private Label ammoTexts[];
	private Label scoreTexts[];
	private int ammoValues[];
	private int scoreValues[];
	private SpriteComponent crossIcons[];
	private SpriteComponent blurComponent;
	
	private boolean isGameStarting = false;
	private float gameStartTimer = 3.0f;
	private Label gameStartLabel;
	
	private float textUpdateTimer = 0.0f;
	
	public Overlay(BaseGame game) {
		super(game);
	}

	public void addPoint(int index){
		if (index < 0 || index >= scoreValues.length){
			return;
		}
		++scoreValues[index];
		scoreTexts[index].setText("" + scoreValues[index]);
	}
	
	public void clearPoints(){
		for (int i = 0; i < scoreValues.length; ++i){
			scoreValues[i] = 0;
			scoreTexts[i].setText("0");
		}
	}
	
	public Font getFont(){
		return overlayFont;
	}
	
	public void gameStarting(){
		isGameStarting = true;
		gameStartTimer = 5.0f;
		gameStartLabel.setText("" + (int)gameStartTimer);
		gameStartLabel.setVisible(true);
		setBlurVisible(true);
	}
	
	protected void initialize() {
		setPosition(new Vector2(0.0f, 1.92f));
		
		JSONObject overlayJson = ConfigManager.loadConfigAsJson(Paths.UI + "overlay.json");
		Sprite2D sheet = new Sprite2D(Paths.SPRITESHEETS + overlayJson.getString("spritesheet"));
		
		JSONArray blurJsonArray = overlayJson.getJSONArray("blur");
		blurComponent = loadSpriteComponents(blurJsonArray, sheet)[0];
		blurComponent.setVisible(false);
		
		JSONArray backgroundArrayJson = overlayJson.getJSONArray("background");
		SpriteComponent bgComponents[] = loadSpriteComponents(backgroundArrayJson, sheet);
		for (SpriteComponent i : bgComponents){
			i.getSprite().setInternalScale((int)(OverloadEngine.frameWidth * 0.25f), (int)(OverloadEngine.frameHeight * 0.08f));
		}
		
		// Load score indicator icons
		JSONArray scoreArrayJson = overlayJson.getJSONArray("score");
		loadSpriteComponents(scoreArrayJson, sheet);

		JSONArray ammoArrayJson = overlayJson.getJSONArray("ammo");
		loadSpriteComponents(ammoArrayJson, sheet);
		
		JSONArray playerIconArrayJson = overlayJson.getJSONArray("icons");
		loadSpriteComponents(playerIconArrayJson, sheet);
		
		JSONArray crossArrayJson = overlayJson.getJSONArray("crosses");
		crossIcons = loadSpriteComponents(crossArrayJson, sheet);
		for (int i = 0; i < crossIcons.length; ++i){
			crossIcons[i].setVisible(false);
		}

		// Load score texts
		String fontPath = Paths.FONTS + overlayJson.getString("font");
		
		overlayFont = ConfigManager.loadFont(fontPath, 16);
		scoreTexts = new Label[PlayerManager.NUM_PLAYERS];
		ammoTexts = new Label[PlayerManager.NUM_PLAYERS];
		ammoValues = new int[PlayerManager.NUM_PLAYERS];
		scoreValues = new int[PlayerManager.NUM_PLAYERS];
		
		JSONArray scoreTextArrayJson = overlayJson.getJSONArray("scoreTexts");
		loadLabels(scoreTextArrayJson, scoreTexts);
		
		JSONArray ammoTextArrayJson = overlayJson.getJSONArray("ammoTexts");
		loadLabels(ammoTextArrayJson, ammoTexts); 
		
		gameStartLabel = new Label(game, new SimpleFont("0", overlayFont.deriveFont(110.0f)));
		gameStartLabel.setPosition(Vector2.one);
		gameStartLabel.setVisible(false);
		game.addEntity(gameStartLabel);
	}
	
	public boolean isUIBlurred(){
		return blurComponent.isVisible();
	}
	
	private void loadLabels(JSONArray json, Label array[]){
		for (int i = 0; i < json.length(); ++i){
			JSONObject textJson = json.getJSONObject(i);
			JSONArray posJson = textJson.getJSONArray("pos");
			float fontSize = (float)textJson.getDouble("size");
			
			SimpleFont textFont = new SimpleFont("0", overlayFont.deriveFont(fontSize));
			Label textLabel = new Label(game, textFont);
			textLabel.setPosition(Vector2.fromJsonArray(posJson));
			addChild(textLabel);
			array[i] = textLabel;
		}
	}
	
	private SpriteComponent[] loadSpriteComponents(JSONArray json, Sprite2D sheet){
		SpriteComponent addedComponents[] = new SpriteComponent[json.length()];
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
			addedComponents[i] = spriteComponent;
		}
		return addedComponents;
	}
	
	public void setBlurVisible(boolean isVisible){
		blurComponent.setVisible(isVisible);
	}
	
	public void setPlayerDead(int index, boolean isDead){
		if (index < 0 || index >= crossIcons.length){
			return;
		}
		crossIcons[index].setVisible(isDead);
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		if (isGameStarting){
			gameStartTimer -= deltaTime;
			
			gameStartLabel.setText("" + (int)gameStartTimer);
			
			if (gameStartTimer <= 0.0f){
				isGameStarting = false;
				gameStartLabel.setVisible(false);
				setBlurVisible(false);
				((OverloadGame)game).startMatch();
			}
		}
	}
	
	public void updateNumBullets(int index, int num){
		ammoValues[index] = num;
		ammoTexts[index].setText("" + num);
	}
}
