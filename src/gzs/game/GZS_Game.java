package gzs.game;

import java.util.ArrayList;
import java.util.List;

import gzs.game.info.Globals;
import gzs.game.misc.Pair;
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
import javafx.scene.input.KeyCode;
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
	
	private List<String> inputs;
	private Pair<Double> lmp; // last mouse position
	private boolean mouseDown;
	
	public GZS_Game(Stage stage_) {
		try {
			mStage = stage_;
			
			gsm = new GameStateManager();
			inputs = new ArrayList<String>();
			lmp = new Pair<Double>(0.0, 0.0);
			mouseDown = false;
			
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
		
	}

	private void render(long cT) {
		if(gsm.getState() != GameState.ERROR) {
			gc.setFill(Color.WHITE);
			gc.fillRect(0, 0, Globals.WIDTH, Globals.HEIGHT);
			
			gc.setFill(Color.BLACK);
			gc.fillText(String.format("(%.1f, %.1f)", lmp.x, lmp.y), 10, 20);
		}
	}
	
	EventHandler<MouseEvent> mouseHandler = (mouse) -> {
		// Update the mouse position.
		lmp.x = mouse.getX();
		lmp.y = mouse.getY();
		
		if(mouse.getButton() == MouseButton.PRIMARY) {
			if(mouse.getEventType() == MouseEvent.MOUSE_PRESSED) {
				mouseDown = true;
			} else if(mouse.getEventType() == MouseEvent.MOUSE_RELEASED) {
				mouseDown = false;
			} else if(mouse.getEventType() == MouseEvent.MOUSE_CLICKED) {
				
			}
		}
	};
	
	EventHandler<KeyEvent> keyHandler = (key) -> {
		String code = key.getCode().toString();
		if(key.getEventType() == KeyEvent.KEY_PRESSED) {
			if(!inputs.contains(code)) {
				if(!key.getCode().equals(KeyCode.SPACE)) {
					inputs.add(code);
				}
			}
		} else if(key.getEventType() == KeyEvent.KEY_RELEASED) {
			inputs.remove(code);
		}
	};
}