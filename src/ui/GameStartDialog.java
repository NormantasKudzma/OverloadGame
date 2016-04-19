package ui;

import utils.FastMath;
import utils.Vector2;
import dialogs.AnimatedLabel;
import dialogs.BaseDialog;
import dialogs.Button;
import dialogs.CheckBox;
import dialogs.Label;
import dialogs.SpriteComponent;
import engine.BaseGame;
import game.OverloadGame;
import game.OverloadMain;
import game.Paths;
import graphics.SimpleFont;
import graphics.Sprite2D;

public class GameStartDialog extends BaseDialog{
	public GameStartDialog(BaseGame game, String name) {
		super(game, name);
	}

	@Override
	protected void initialize() {
		super.initialize();
		
		AnimatedLabel title = new AnimatedLabel(OverloadMain.GAME){
			float progress = 0.0f;
			float offset = 0.0f;
			
			@Override
			public void animationStep(float deltaTime) {
				progress += deltaTime;
				if (progress >= 6.28f){
					progress = 0.0f;
				}
				offset = FastMath.sin(progress) * 0.00025f;
				setPosition(getPosition().add(0.0f, offset));
				offset *= 2;
				setScale(getScale().add(offset, offset));
			}
		};
		title.setFont(((OverloadGame)game).getOverlay().getFont().deriveFont(90.0f));
		title.setInfinite(true);
		title.setPosition(new Vector2(0.0f, 0.8f));
		title.setScale(new Vector2(0.95f, 0.95f));
		addChild(title);
		
		Button play = new Button(game, null, null, "PLAY"){
			public void clickFunction() {
				OverloadGame overload = (OverloadGame)game;
				overload.loadMap();
				overload.getOverlay().gameStarting();
				GameStartDialog.this.setVisible(false);
			};
		};
		play.setScale(new Vector2(0.45f, 0.45f));
		play.setPosition(new Vector2(0.0f, -0.7f));
		addChild(play);

		initializePlayerInfo();
	}
	
	private void initializePlayerInfo(){
		Overlay overlay = ((OverloadGame)game).getOverlay();
		Vector2 infoPosition[] = new Vector2[]{new Vector2(-0.4f, 0.25f), new Vector2(0.4f, 0.25f),
												new Vector2(-0.4f, -0.25f), new Vector2(0.4f, -0.25f)};
		SpriteComponent[] playerIcons = overlay.getPlayerIcons();
		
		for (int i = 0; i < infoPosition.length; ++i){
			Vector2 center = infoPosition[i];
			
			SpriteComponent icon = (SpriteComponent)playerIcons[i].clone();
			icon.setPosition(center.copy().add(-0.1f, 0.1f));
			icon.setScale(1.0f, 1.0f);
			addChild(icon);
			
			SpriteComponent controls = new SpriteComponent(game);
			controls.setSprite(new Sprite2D(Paths.UI + "player" + (i + 1) + "_controls.png"));
			controls.setPosition(center.copy().add(0.1f, 0.1f));
			controls.setScale(0.4f, 0.4f);
			addChild(controls);
			
			SimpleFont text = new SimpleFont("Player " + (i + 1) + " ready?", overlay.getFont().deriveFont(22.0f));		
			CheckBox readyBox = new CheckBox(game);
			readyBox.setPosition(center.copy().add(-0.16f, -0.12f));
			readyBox.setText(text);
			readyBox.setChecked(true);
			addChild(readyBox);
		}
	}
}
