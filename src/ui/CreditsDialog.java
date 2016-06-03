package ui;

import utils.Vector2;
import engine.BaseGame;

public class CreditsDialog extends BaseDialog {

	public CreditsDialog(BaseGame game, String name) {
		super(game, name);
	}
	
	@Override
	protected void initialize() {
		super.initialize();
		
		Button back = new Button(game, null, null, "BACK"){
			public void clickFunction() {				
				GameStartDialog dialog = (GameStartDialog)game.getDialog("start");
				if (dialog == null){
					dialog = new GameStartDialog(game, "start");
					game.addDialog(dialog);
				}
				dialog.setVisible(true);
				
				CreditsDialog.this.setVisible(false);
				game.removeDialog(CreditsDialog.this.name);
			};
		};
		back.setScale(new Vector2(0.45f, 0.45f));
		back.setPosition(new Vector2(0.0f, -0.7f));
		addChild(back);
	}
}
