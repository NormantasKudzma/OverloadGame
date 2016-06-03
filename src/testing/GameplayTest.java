package testing;

import java.util.Iterator;

import org.sikuli.basics.Debug;
import org.sikuli.script.Key;
import org.sikuli.script.Match;
import org.sikuli.script.Screen;

public class GameplayTest {
	private Screen screen;
	private Match play;
	
	public GameplayTest(){
		Debug.setDebugLevel(3);
		screen = new Screen();
		new Thread(){
			public void run() {
				try {
					Thread.sleep(2000);
					runTests();
				}
				catch (Exception e){
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public void runTests(){
		try {
			screen.getCenter().click();
			Thread.sleep(350);
			screen.click();

			System.out.println("Turning off usb players");
			Iterator<Match> match = screen.findAll("testing/checkbox.png");
			int index = 0;
			while (match.hasNext()){
				Match m = match.next();
				if (index < 2){
					index++;
					continue;
				}
				m.click();
				m.click();
				Thread.sleep(400);
			}
			Thread.sleep(200);

			System.out.println("Clicking play");
			play = screen.find("testing/play.png");
			play.click();
			play.click();
			Thread.sleep(5800);

			roundOne();
			roundTwo();
			roundThree();
			
			System.out.println("Clicking finish");		
			play.click();
			play.click();
			
			play = screen.find("testing/play.png");
			if (play == null){
				System.err.println("Test failed.");
			}
			else {
				System.out.println("Done");
			}
		}
		catch (Exception e){
			e.printStackTrace();
			System.err.println("Test failed.");
		}
	}
	
	private void roundOne() throws InterruptedException{
		System.out.println("Moving player two offscreen");
		screen.keyDown(Key.UP);
		Thread.sleep(20);
		screen.keyDown(Key.RIGHT);
		Thread.sleep(250);
		screen.keyUp();
		screen.keyUp();
		Thread.sleep(1500);

		System.out.println("Clicking continue");
		for (int i = 0; i < 3; ++i){
			play.click();
		}
		Thread.sleep(5800);
	}
	
	private void roundTwo() throws InterruptedException{
		System.out.println("Moving player two");
		screen.keyDown(Key.UP);
		Thread.sleep(50);
		screen.keyDown(Key.LEFT);
		Thread.sleep(250);
		screen.keyUp();
		screen.keyUp();
		Thread.sleep(250);

		System.out.println("Moving player one to right");
		screen.keyDown("w");
		Thread.sleep(20);
		screen.keyDown("d");
		Thread.sleep(250);
		screen.keyUp();
		screen.keyUp();
		Thread.sleep(1700);
			
		screen.keyDown("w");
		Thread.sleep(20);
		screen.keyDown("d");
		Thread.sleep(250);
		screen.keyUp("w");
		Thread.sleep(380);
		screen.keyUp();
		Thread.sleep(1700);

		System.out.println("Player one shoot");
		screen.keyDown("r");
		Thread.sleep(100);
		screen.keyUp();
		Thread.sleep(1500);
		
		System.out.println("Clicking continue");
		for (int i = 0; i < 3; ++i){
			play.click();
		}
		Thread.sleep(5800);
	}
	
	private void roundThree() throws InterruptedException{
		System.out.println("Round three");
		screen.keyDown(Key.LEFT);
		Thread.sleep(215);
		screen.keyUp();
		Thread.sleep(250);

		screen.keyDown("d");
		Thread.sleep(200);
		screen.keyUp();
		Thread.sleep(250);

		System.out.println("Player two go to the other side");
		screen.keyDown(Key.LEFT);
		Thread.sleep(1000);
		screen.keyUp();
		Thread.sleep(250);
		
		screen.keyDown(Key.UP);
		Thread.sleep(50);
		screen.keyDown(Key.LEFT);
		Thread.sleep(2000);
		screen.keyUp();
		screen.keyUp();
		Thread.sleep(250);

		screen.keyDown("a");
		Thread.sleep(500);
		screen.keyUp();
		screen.keyDown("d");
		Thread.sleep(250);
		screen.keyDown("a");
		Thread.sleep(20);
		screen.keyUp();
		
		System.out.println("Player one shoot again");
		screen.keyDown("r");
		Thread.sleep(100);
		screen.keyUp();
		Thread.sleep(1500);
	}
}
