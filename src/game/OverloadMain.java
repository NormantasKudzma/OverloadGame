package game;

import engine.EngineConfig;
import engine.OverloadEngine;

public class OverloadMain {
	public static final String VERSION = "v4";
	public static final String GAME = "Overload";
	
	public static void main(String[] args){
		OverloadGame game = new OverloadGame();
		
		EngineConfig config = new EngineConfig();
		config.game = game;
		config.isDebug = true;
		config.isFullscreen = true;
		config.title = GAME + " " + VERSION;
		
		OverloadEngine engine = new OverloadEngine(config);
		engine.run();
	}
}
