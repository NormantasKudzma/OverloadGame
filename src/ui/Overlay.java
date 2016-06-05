package ui;

import java.awt.Font;

import managers.PlayerManager;

import org.json.JSONArray;
import org.json.JSONObject;

import ui.Component;
import ui.Label;
import ui.SpriteComponent;
import utils.ConfigManager;
import utils.Vector2;
import engine.BaseGame;
import engine.OverloadEngine;
import game.OverloadGame;
import game.Paths;
import graphics.Color;
import graphics.SimpleFont;
import graphics.Sprite;

public class Overlay extends Component{
	private Font overlayFont;
	private Label ammoTexts[];
	private Label scoreTexts[];
	private int ammoValues[];
	private int scoreValues[];
	private SpriteComponent ammoIcons[];
	private SpriteComponent crossIcons[];
	private SpriteComponent playerIcons[];
	private SpriteComponent scoreIcons[];
	private SpriteComponent blurComponent;
	
	private boolean isGameStarting = false;
	private float gameStartTimer = 0.0f;
	private Label gameStartLabel;
	
	private boolean isGameEnding = false;
	private float gameEndTimer = 0.0f;
	private Color blurColor = new Color(1.0f, 1.0f, 1.0f, 0.0f);
	
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

	public SpriteComponent[] getPlayerIcons(){
		return playerIcons;
	}

	public int[] getScores(){
		return scoreValues;
	}
	
	public void gameEnding(){		
		isGameEnding = true;
		isGameStarting = false;
		gameEndTimer = 2.0f;
		
		setBlurVisible(true);
		blurColor.rgba[3] = 0.0f;
		blurComponent.setColor(blurColor);
	}
	
	public void gameStarting(){		
		isGameStarting = true;
		isGameEnding = false;
		gameStartTimer = 5.0f;
		gameStartLabel.setText("" + (int)gameStartTimer);
		gameStartLabel.setVisible(true);
		setBlurVisible(true);
		
		PlayerManager playerManager = ((OverloadGame)game).getPlayerManager();
		playerManager.reset();
		
		for (int i = 0; i < scoreTexts.length; ++i){
			boolean enabled = playerManager.isPlayerEnabled(i);
			ammoTexts[i].setVisible(enabled);
			ammoTexts[i].setText("0");
			scoreTexts[i].setVisible(enabled);
			ammoIcons[i].setVisible(enabled);
			crossIcons[i].setVisible(false);
			playerIcons[i].setVisible(enabled);
			scoreIcons[i].setVisible(enabled);
		}
	}
	
	protected void initialize() {
		setPosition(new Vector2(0.0f, 1.92f));
		
		JSONObject overlayJson = ConfigManager.loadConfigAsJson(Paths.UI + "overlay.json");
		Sprite sheet = new Sprite(Paths.SPRITESHEETS + overlayJson.getString("spritesheet"));
		
		JSONArray blurJsonArray = overlayJson.getJSONArray("blur");
		blurComponent = loadSpriteComponents(blurJsonArray, sheet)[0];
		blurComponent.setVisible(false);
		
		JSONArray backgroundArrayJson = overlayJson.getJSONArray("background");
		SpriteComponent bgComponents[] = loadSpriteComponents(backgroundArrayJson, sheet);
		for (SpriteComponent i : bgComponents){
			((Sprite)i.getSprite()).setInternalScale((int)(OverloadEngine.frameWidth * 0.25f), (int)(OverloadEngine.frameHeight * 0.08f));
		}
		
		// Load score indicator icons
		JSONArray scoreArrayJson = overlayJson.getJSONArray("score");
		scoreIcons = loadSpriteComponents(scoreArrayJson, sheet);

		JSONArray ammoArrayJson = overlayJson.getJSONArray("ammo");
		ammoIcons = loadSpriteComponents(ammoArrayJson, sheet);
		
		JSONArray playerIconArrayJson = overlayJson.getJSONArray("icons");
		playerIcons = loadSpriteComponents(playerIconArrayJson, sheet);
		
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
	
	private SpriteComponent[] loadSpriteComponents(JSONArray json, Sprite sheet){
		SpriteComponent addedComponents[] = new SpriteComponent[json.length()];
		for (int i = 0; i < json.length(); ++i){
			JSONObject itemJson = json.getJSONObject(i);
			JSONArray posJson = itemJson.getJSONArray("pos");
			JSONArray scaleJson = itemJson.getJSONArray("scale");
			int x = itemJson.getInt("x");
			int y = itemJson.getInt("y");
			int w = itemJson.getInt("w");
			int h = itemJson.getInt("h");
			Sprite sprite = Sprite.getSpriteFromSheet(x, y, w, h, sheet);
			
			SpriteComponent spriteComponent = new SpriteComponent(game);
			spriteComponent.setSprite(sprite);
			spriteComponent.setPosition(Vector2.fromJsonArray(posJson));
			spriteComponent.setScale(Vector2.fromJsonArray(scaleJson));			
			addChild(spriteComponent);
			addedComponents[i] = spriteComponent;
		}
		return addedComponents;
	}
	
	public void reset(){
		for (int i = 0; i < scoreTexts.length; ++i){
			ammoTexts[i].setVisible(true);
			ammoTexts[i].setText("0");
			scoreTexts[i].setVisible(true);
			scoreTexts[i].setText("0");
			ammoIcons[i].setVisible(true);
			crossIcons[i].setVisible(false);
			playerIcons[i].setVisible(true);
			scoreIcons[i].setVisible(true);
			ammoValues[i] = 0;
			scoreValues[i] = 0;
		}
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
			}
		}
		
		if (isGameEnding){
			gameEndTimer -= deltaTime;
			
			if (gameEndTimer <= 0.0f){
				isGameEnding = false;
				blurColor.rgba[3] = 1.0f;
				GameEndDialog endDialog = new GameEndDialog(game, "end");
				endDialog.setVisible(true);
				game.addDialog(endDialog);
				((OverloadGame)game).getMapManager().cleanUpLayers();
			}
			else {
				blurColor.rgba[3] += deltaTime * 0.5f;
			}

			blurComponent.setColor(blurColor);
		}
	}
	
	public void updateNumBullets(int index, int num){
		ammoValues[index] = num;
		ammoTexts[index].setText("" + num);
	}
}
