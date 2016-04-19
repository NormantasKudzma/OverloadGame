package game;

import engine.OverloadEngine;

public class OverloadMain {
	public static final String VERSION = "preview build 2";
	public static final String GAME = "Overload";
	
	public static void main(String[] args){
		OverloadEngine engine = new OverloadEngine();
		OverloadGame game = new OverloadGame();
		engine.setGame(game);
		engine.setDebugDraw(true);
		engine.setFullscreen(false);
		engine.setTitle(GAME + " " + VERSION);
		engine.run();
	}
}
