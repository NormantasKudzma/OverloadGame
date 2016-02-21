package game;

public class OverloadMain {	
	public static final boolean IS_DEBUG_BUILD = true;
	
	public static void main(String[] args){
		OverloadEngine engine = new OverloadEngine();
		OverloadGame game = new OverloadGame();
		engine.setGame(game);
		engine.setDebugDraw(IS_DEBUG_BUILD);
		engine.run();
	}
}
