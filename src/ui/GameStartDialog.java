package ui;

import utils.FastMath;
import utils.Vector2;
import dialogs.AnimatedLabel;
import dialogs.BaseDialog;
import dialogs.Button;
import engine.BaseGame;
import game.OverloadGame;
import game.OverloadMain;

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
		title.setFont(((OverloadGame)game).getOverlay().getFont().deriveFont(110.0f));
		title.setInfinite(true);
		title.setPosition(new Vector2(0.0f, 0.8f));
		addChild(title);
		
		Button play = new Button(game, null, null, "PLAY"){
			public void clickFunction() {
				OverloadGame overload = (OverloadGame)game;
				overload.loadMap();
				overload.getOverlay().gameStarting();
				GameStartDialog.this.setVisible(false);
			};
		};
		play.setScale(new Vector2(0.8f, 0.8f));
		play.setPosition(new Vector2(0.0f, -0.7f));
		addChild(play);
	}
}
