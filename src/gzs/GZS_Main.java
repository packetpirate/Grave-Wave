package gzs;

import gzs.game.GZS_Game;
import gzs.game.info.Globals;
import javafx.application.Application;
import javafx.stage.Stage;

public class GZS_Main extends Application{
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage stage) {
		stage.setTitle("Generic Zombie Shooter Redux v" + Globals.VERSION);
		stage.setResizable(false);
		stage.requestFocus();
		stage.centerOnScreen();
		
		new GZS_Game(stage);
	}
}
