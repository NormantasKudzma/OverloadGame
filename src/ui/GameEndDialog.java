package ui;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;

import managers.PlayerManager;
import utils.Vector2;
import dialogs.BaseDialog;
import dialogs.Button;
import dialogs.Label;
import dialogs.SpriteComponent;
import engine.BaseGame;
import game.OverloadGame;
import graphics.SimpleFont;

public class GameEndDialog extends BaseDialog{
	public GameEndDialog(BaseGame game, String name) {
		super(game, name);
	}

	@Override
	protected void initialize() {
		super.initialize();

		final OverloadGame overload = (OverloadGame)game;
		Overlay overlay = overload.getOverlay();

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
		
		initializePlayerInfo(scores);
		
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
	}
	
	private void initializePlayerInfo(int scores[]){
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
			SimpleFont text = new SimpleFont(score + " point" + (score == 1 ? "" : "s"), overlay.getFont().deriveFont(40.0f));		
			Label scoreLabel = new Label(game, text);
			scoreLabel.setPosition(center.copy().add(0.5f, 0.0f));
			addChild(scoreLabel);
		}
	}
}
