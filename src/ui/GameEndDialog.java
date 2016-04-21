package ui;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;

import managers.PlayerManager;
import ui.BaseDialog;
import ui.Button;
import ui.Label;
import ui.SpriteComponent;
import utils.Vector2;
import engine.BaseGame;
import game.OverloadGame;
import graphics.Color;
import graphics.SimpleFont;

public class GameEndDialog extends BaseDialog{
	public GameEndDialog(BaseGame game, String name) {
		super(game, name);
	}

	@Override
	protected void initialize() {
		super.initialize();

		final OverloadGame overload = (OverloadGame)game;
		final Overlay overlay = overload.getOverlay();

		int scores[] = overlay.getScores();
		int winnerIndex = -1;
		boolean isGameOver = false;
		
		for (int i = 0; i < scores.length; ++i){
			if (scores[i] >= OverloadGame.MAX_POINTS){
				winnerIndex = i;
				isGameOver = true;
				break;
			}
		}
		
		Font labelFont = overlay.getFont().deriveFont(80.0f);
		SimpleFont labelText = new SimpleFont(isGameOver ? "Final scores :" : "Leaderboard", labelFont);
		Label title = new Label(game, labelText);
		title.setPosition(new Vector2(0.0f, 0.75f));
		addChild(title);
		
		initializePlayerInfo(scores, winnerIndex);
		
		if (!isGameOver){
			Button play = new Button(game, null, null, "PLAY"){
				public void clickFunction() {		
					overload.loadMap();
					GameEndDialog.this.setVisible(false);
				};
			};
			play.setScale(new Vector2(0.45f, 0.45f));
			play.setPosition(new Vector2(0.0f, -0.7f));
			addChild(play);
		}
		else {			
			Button back = new Button(game, null, null, "BACK"){
				public void clickFunction() {
					overlay.reset();
					
					GameStartDialog startDialog = new GameStartDialog(game, "start");
					startDialog.setVisible(true);
					game.addDialog(startDialog);
					
					GameEndDialog.this.setVisible(false);
					game.removeDialog(GameEndDialog.this.name);
				};
			};
			back.setScale(new Vector2(0.45f, 0.45f));
			back.setPosition(new Vector2(0.0f, -0.7f));
			addChild(back);
		}
	}
	
	private void initializePlayerInfo(int scores[], int winnerIndex){
		class Score implements Comparable<Score>{
			public int index;
			public int value;
			public boolean enabled;
			
			public Score(int i, int v, boolean isEnabled){
				index = i;
				value = v;
				enabled = isEnabled;
			}

			// Player has to be enabled, so increase points by some arbitrary value
			@Override
			public int compareTo(Score o) {
				return (o.value + (o.enabled ? 1000 : 0))- (value + (enabled ? 1000 : 0));
			}
		}

		OverloadGame overload = (OverloadGame)game;
		PlayerManager playerManager = overload.getPlayerManager();
		Overlay overlay = overload.getOverlay();
		
		ArrayList<Score> playerScores = new ArrayList<Score>(scores.length);
		for (int i = 0; i < scores.length; ++i){
			playerScores.add(new Score(i, scores[i], playerManager.isPlayerEnabled(i)));
		}
		Collections.sort(playerScores);

		Vector2 infoPosition[] = new Vector2[]{new Vector2(-0.25f, 0.4f), new Vector2(-0.25f, 0.15f),
												new Vector2(-0.25f, -0.1f), new Vector2(-0.25f, -0.35f)};
		SpriteComponent[] playerIcons = overlay.getPlayerIcons();
		
		for (int i = 0; i < infoPosition.length; ++i){
			Vector2 center = infoPosition[i];
			
			Score s = playerScores.get(i);
			int index = s.index;
			
			if (!playerManager.isPlayerEnabled(index)){
				break;
			}
			
			SpriteComponent icon = (SpriteComponent)playerIcons[index].clone();
			icon.setPosition(center.copy());
			icon.setScale(1.0f, 1.0f);
			addChild(icon);
			
			int score = s.value;
			
			String scoreStr = score + " point" + (score == 1 ? "" : "s");
			if (winnerIndex == index){
				scoreStr += ", WINNER!";
			}
			SimpleFont text = new SimpleFont(scoreStr, overlay.getFont().deriveFont(40.0f));		
			Label scoreLabel = new Label(game, text);
			scoreLabel.setPosition(center.copy().add(0.5f, 0.0f));
			if (winnerIndex == index){
				scoreLabel.setColor(new Color(1.0f, 0.84f, 0.02f));
			}
			addChild(scoreLabel);
		}
	}
}
