package game;

import com.ovl.engine.EngineConfig;
import com.ovl.engine.OverloadEngine;
import com.ovl.engine.pc.OverloadEnginePc;

public class OverloadMain {
	public static final String VERSION = "v5";
	public static final String GAME = "Overload";
	
	public static void main(String[] args){
		OverloadGame game = new OverloadGame();
		
		EngineConfig config = new EngineConfig();
		config.game = game;
		config.isDebug = true;
		//config.isFullscreen = false;
		config.title = GAME + " " + VERSION;
		
		OverloadEngine engine = new OverloadEnginePc(config);
		engine.run();
	}
}
