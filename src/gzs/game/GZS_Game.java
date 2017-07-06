package gzs.game;

import java.util.HashSet;
import java.util.Set;

import gzs.game.gfx.Screen;
import gzs.game.gfx.screens.*;
import gzs.game.info.Globals;
import gzs.game.misc.MouseInfo;
import gzs.game.state.GameState;
import gzs.game.state.GameStateException;
import gzs.game.state.GameStateManager;
import gzs.game.utils.ExceptionHandler;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GZS_Game {
	private Stage mStage;
	private Scene mScene;
	
	private Canvas canvas;
	private GraphicsContext gc;
	
	private GameStateManager gsm;
	private Screen [] allScreens;
	private Screen cScreen;
	
	private Set<String> inputs;
	private MouseInfo mouse;
	
	public GZS_Game(Stage stage_) {
		try {
			mStage = stage_;
			
			gsm = new GameStateManager();
			
			/* IMPORTANT!!!!!!!!!
			 * If the order of the screens is changed or more are added, you must also
			 * modify the GameState.getScreenIndex() static method to accommodate the
			 * new indices of each screen.
			 */
			allScreens = new Screen[] {
				new MenuScreen(),
				new CreditsScreen(),
				new GameScreen(),
				new ShopScreen(),
				new TrainScreen(),
				new GameOverScreen()
			};
			
			inputs = new HashSet<String>();
			mouse = new MouseInfo();
			
			Group root = new Group();
			mScene = new Scene(root);
			mStage.setScene(mScene);
			
			// Initialize the canvas and add it to the scene.
			canvas = new Canvas(Globals.WIDTH, Globals.HEIGHT);
			gc = canvas.getGraphicsContext2D();
			root.getChildren().add(canvas);
			
			// Register input handlers.
			mScene.addEventHandler(KeyEvent.KEY_PRESSED, keyHandler);
			mScene.addEventHandler(KeyEvent.KEY_RELEASED, keyHandler);
			mScene.addEventHandler(MouseEvent.MOUSE_MOVED, mouseHandler);
			mScene.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
			mScene.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseHandler);
			mScene.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseHandler);
			
			mStage.sizeToScene();
			
			// Main game loop.
			new AnimationTimer() {
				private long lastUpdate = 0L;
				
				{ // Begin Pseudo-Constructor
					gsm.transition("initialized");
					changeScreen(gsm.getState());
				} // End Pseudo-Constructor
				
				@Override
				public void handle(long nT) {
					// Convert current nano time to milliseconds.
					long now = nT / Globals.NANO_TO_MS;
					
					// Check if elapsed time since last update is greater than sleep time.
					double elapsed = ((double)nT - (double)lastUpdate) / 1_000_000_000.0;
					if(elapsed >= Globals.UPDATE_TIME) {
						update(now);
						render(now);
						lastUpdate = now;
					}
				}
			}.start();
			
			mStage.show();
		} catch(Exception e) {
			// Pass to custom exception handler class which will display the exception visually.
			try {
				gsm.transition("exception");
				new ExceptionHandler(e);
			} catch (GameStateException gse) {} // ignore this... base case in method prevents this
			e.printStackTrace();
		}
	}
	
	private void update(long cT) {
		if(gsm.getState() != GameState.ERROR) {
			// Update the current screen.
			if(cScreen != null) cScreen.update(cT, mouse);
		}
	}

	private void render(long cT) {
		if(gsm.getState() != GameState.ERROR) {
			gc.setFill(Color.WHITE);
			gc.fillRect(0, 0, Globals.WIDTH, Globals.HEIGHT);
			
			gc.setFill(Color.BLACK);
			gc.fillText(("Screen: " + cScreen), 10, 20);
		} else {
			gc.setFill(Color.BLACK);
			gc.fillRect(0, 0, Globals.WIDTH, Globals.HEIGHT);
		}
	}
	
	private void changeScreen(GameState state) throws ScreenIndexException {
		int index = GameState.getScreenIndex(state);
		if(index != -1) cScreen = allScreens[index];
		else throw new ScreenIndexException("Invalid screen index!");
	}
	
	EventHandler<MouseEvent> mouseHandler = (mouseEvent) -> {
		// Update the mouse position.
		mouse.setPosition(mouseEvent.getX(), mouseEvent.getY());
		
		if(mouseEvent.getButton() == MouseButton.PRIMARY) {
			if(mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
				mouse.setMouseDown(true);
			} else if(mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
				mouse.setMouseDown(false);
			} else if(mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED) {
				cScreen.dispatchClick(mouse);
			}
		}
	};
	
	EventHandler<KeyEvent> keyHandler = (key) -> {
		String code = key.getCode().toString();
		if(key.getEventType() == KeyEvent.KEY_PRESSED) {
			inputs.add(code);
		} else if(key.getEventType() == KeyEvent.KEY_RELEASED) {
			inputs.remove(code);
		}
	};
}