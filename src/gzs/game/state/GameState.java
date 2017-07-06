package gzs.game.state;

public enum GameState {
	INITIALIZE, MENU, GAME, PAUSE, SHOP, 
	TRAIN, DEATH, GAMEOVER, CREDITS, ERROR;
	
	public static int getScreenIndex(GameState state) {
		switch(state) {
			case MENU: return 0;
			case CREDITS: return 1;
			case GAME: return 2;
			case SHOP: return 3;
			case TRAIN: return 4;
			case GAMEOVER: return 5;
			default: return -1;
		}
	}
}
