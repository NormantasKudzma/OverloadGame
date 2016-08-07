package ui;

import com.ovl.engine.BaseGame;
import com.ovl.ui.BaseDialog;
import com.ovl.ui.Button;
import com.ovl.ui.OnClickListener;
import com.ovl.utils.Vector2;

public class CreditsDialog extends BaseDialog {

	public CreditsDialog(BaseGame game, String name) {
		super(game, name);
	}
	
	@Override
	protected void initialize() {
		super.initialize();
		
		Button back = new Button(game, "BACK");
		OnClickListener backListener = new OnClickListener(){
			public void clickFunction(Vector2 pos) {				
				GameStartDialog dialog = (GameStartDialog)game.getDialog("start");
				if (dialog == null){
					dialog = new GameStartDialog(game, "start");
					game.addDialog(dialog);
				}
				dialog.setVisible(true);
				
				CreditsDialog.this.setVisible(false);
				game.removeDialog(CreditsDialog.this.name);
			}
		};
		back.setClickListener(backListener);
		back.setScale(new Vector2(0.45f, 0.45f));
		back.setPosition(new Vector2(0.0f, -0.7f));
		addChild(back);
	}
}
