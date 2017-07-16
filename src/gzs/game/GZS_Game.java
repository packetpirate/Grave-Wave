package gzs.game;

import gzs.game.gfx.Screen;
import gzs.game.gfx.screens.BlackScreen;
import gzs.game.gfx.screens.CreditsScreen;
import gzs.game.gfx.screens.GameOverScreen;
import gzs.game.gfx.screens.GameScreen;
import gzs.game.gfx.screens.MenuScreen;
import gzs.game.gfx.screens.ScreenIndexException;
import gzs.game.gfx.screens.ShopScreen;
import gzs.game.gfx.screens.TrainScreen;
import gzs.game.info.Globals;
import gzs.game.state.GameState;
import gzs.game.state.GameStateException;
import gzs.game.utils.ExceptionHandler;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
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
	
	private Screen [] allScreens;
	private Screen cScreen;
	
	private Exception lastException;
	
	public GZS_Game(Stage stage_) {
		try {
			mStage = stage_;
			
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
				new GameOverScreen(),
				new BlackScreen()
			};
			
			lastException = null;
			
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
			mScene.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseHandler);
			mScene.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseHandler);
			mScene.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseHandler);
			
			mStage.sizeToScene();
			
			// Main game loop.
			new AnimationTimer() {
				private long lastUpdate = 0L;
				
				{ // Begin Pseudo-Constructor
					Globals.getGSM().transition("initialized");
					changeScreen(Globals.getGSM().getState());
				} // End Pseudo-Constructor
				
				@Override
				public void handle(long nT) {
					if(Globals.getGSM().getState() != GameState.QUIT) {
						// Convert current nano time to milliseconds.
						long now = nT / Globals.NANO_TO_MS;
						
						// Check if elapsed time since last update is greater than sleep time.
						double elapsed = ((double)nT - (double)lastUpdate) / 1_000_000_000.0;
						if(elapsed >= Globals.UPDATE_TIME) {
							update(now);
							try {
								changeScreen(Globals.getGSM().getState());
							} catch (Exception e) {
								// Should never happen, but just in case...
								e.printStackTrace();
								lastException = e;
								try {
									Globals.getGSM().transition("exception");
									ExceptionHandler.Handle(e);
									System.exit(1);
								} catch (GameStateException gse) {} // ignore... base case prevents this
							}
							render(now);
							lastUpdate = now;
							
							if(Globals.getGSM().getState() == GameState.ERROR) {
								ExceptionHandler.Handle(lastException);
								System.exit(1);
							}
						}
					} else {
						stop();
						System.exit(0);
					}
				}
			}.start();
			
			mStage.show();
		} catch(Exception e) {
			// Pass to custom exception handler class which will display the exception visually.
			try {
				Globals.getGSM().transition("exception");
				ExceptionHandler.Handle(e);
				e.printStackTrace();
				System.exit(1);
			} catch (GameStateException gse) {} // ignore this... base case in method prevents this
		}
	}
	
	private void update(long cT) {
		if(Globals.getGSM().getState() != GameState.ERROR) {
			// Update the current screen.
			if(cScreen != null) {
				try {
					cScreen.update(cT);
				} catch (Exception e) {
					try {
						Globals.getGSM().transition("exception");
						ExceptionHandler.Handle(e);
						e.printStackTrace();
						System.exit(1);
					} catch (GameStateException gse) {} // ignore this... base case in method prevents this
				}
			}
		}
	}

	private void render(long cT) {
		if(Globals.getGSM().getState() != GameState.ERROR) {
			gc.setFill(Color.WHITE);
			gc.fillRect(0, 0, Globals.WIDTH, Globals.HEIGHT);
			
			// Render the current screen.
			if(cScreen != null) {
				try {
					cScreen.render(gc, cT);
				} catch (Exception e) {
					try {
						Globals.getGSM().transition("exception");
					} catch (GameStateException gse) {} // ignore this... base case in method prevents this
					e.printStackTrace();
					lastException = e;
					return;
				} 
			}
		} else {
			gc.setFill(Color.BLACK);
			gc.fillRect(0, 0, Globals.WIDTH, Globals.HEIGHT);
		}
	}
	
	private void changeScreen(GameState state) throws Exception {
		int index = GameState.getScreenIndex(state);
		if(index != -1) cScreen = allScreens[index];
		else throw new ScreenIndexException("Invalid screen index!");
		if(cScreen.hidesCursor()) mScene.setCursor(Cursor.NONE);
		else mScene.setCursor(Cursor.DEFAULT);
	}
	
	EventHandler<MouseEvent> mouseHandler = (mouseEvent) -> {
		// Update the mouse position.
		Globals.mouse.setPosition(mouseEvent.getX(), mouseEvent.getY());
		
		if(mouseEvent.getButton() == MouseButton.PRIMARY) {
			if(mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
				Globals.mouse.setMouseDown(true);
			} else if(mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
				Globals.mouse.setMouseDown(false);
			} else if(mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED) {
				try {
					cScreen.dispatchClick(Globals.mouse);
				} catch(Exception e) {
					try {
						Globals.getGSM().transition("exception");
					} catch (GameStateException gse) {} // ignore this... base case in method prevents this
					e.printStackTrace();
					lastException = e;
					return;
				}
			}
		}
	};
	
	EventHandler<KeyEvent> keyHandler = (key) -> {
		String code = key.getCode().toString();
		if(key.getEventType() == KeyEvent.KEY_PRESSED) {
			Globals.inputs.add(code);
		} else if(key.getEventType() == KeyEvent.KEY_RELEASED) {
			Globals.inputs.remove(code);
		}
	};
}