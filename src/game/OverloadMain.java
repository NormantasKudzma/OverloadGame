package game;

public class OverloadMain {	
	public static void main(String[] args){
		OverloadEngine engine = new OverloadEngine();
		OverloadGame game = new OverloadGame();
		engine.setGame(game);
		engine.run();
	}
}
