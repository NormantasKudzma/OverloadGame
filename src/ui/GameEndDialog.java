package ui;

import game.OverloadGame;

import java.util.ArrayList;
import java.util.Collections;

import managers.PlayerManager;

import com.ovl.engine.BaseGame;
import com.ovl.graphics.Color;
import com.ovl.graphics.CustomFont;
import com.ovl.graphics.SimpleFont;
import com.ovl.ui.BaseDialog;
import com.ovl.ui.Button;
import com.ovl.ui.Label;
import com.ovl.ui.OnClickListener;
import com.ovl.ui.SpriteComponent;
import com.ovl.utils.Vector2;

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
		
		CustomFont labelFont = overlay.getFont().deriveFont(80.0f);
		SimpleFont labelText = SimpleFont.create(isGameOver ? "Final scores :" : "Leaderboard", labelFont);
		Label title = new Label(game, labelText);
		title.setPosition(new Vector2(0.0f, 0.75f));
		addChild(title);
		
		initializePlayerInfo(scores, winnerIndex);
		
		if (!isGameOver){
			Button play = new Button(game, "PLAY");
			OnClickListener playListener = new OnClickListener(){
				public void clickFunction(Vector2 pos) {		
					overload.loadMap();
					GameEndDialog.this.setVisible(false);
					game.removeDialog(GameEndDialog.this.name);
				};
			};
			play.setClickListener(playListener);
			play.setScale(new Vector2(0.45f, 0.45f));
			play.setPosition(new Vector2(0.0f, -0.7f));
			addChild(play);
		}
		else {			
			Button back = new Button(game, "BACK");
			OnClickListener backListener = new OnClickListener(){
				public void clickFunction(Vector2 pos) {
					overlay.reset();
					
					GameStartDialog dialog = (GameStartDialog)game.getDialog("start");
					if (dialog == null){
						dialog = new GameStartDialog(game, "start");
						game.addDialog(dialog);
					}
					dialog.setVisible(true);
					
					GameEndDialog.this.setVisible(false);
					game.removeDialog(GameEndDialog.this.name);
				};
			};
			back.setClickListener(backListener);
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
			SimpleFont text = SimpleFont.create(scoreStr, overlay.getFont().deriveFont(40.0f));		
			Label scoreLabel = new Label(game, text);
			scoreLabel.setPosition(center.copy().add(0.5f, 0.0f));
			if (winnerIndex == index){
				scoreLabel.setColor(new Color(1.0f, 0.84f, 0.02f));
			}
			addChild(scoreLabel);
		}
	}
}
