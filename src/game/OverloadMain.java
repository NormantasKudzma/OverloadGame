package game;

import engine.OverloadEngine;

public class OverloadMain {	
	public static final boolean IS_DEBUG_BUILD = true;
	public static final String VERSION = "v0.25";
	public static final String GAME = "Overload";
	
	public static void main(String[] args){
		OverloadEngine engine = new OverloadEngine();
		OverloadGame game = new OverloadGame();
		engine.setGame(game);
		engine.setDebugDraw(IS_DEBUG_BUILD);
		engine.setTitle(GAME + " " + VERSION);
		engine.run();
	}
}
