package gzs.game.state;

public enum GameState {
	INITIALIZE, MENU, GAME, PAUSE, SHOP, 
	TRAIN, DEATH, GAMEOVER, CREDITS, QUIT, ERROR;
	
	public static int getScreenIndex(GameState state) {
		switch(state) {
			case INITIALIZE:
			case MENU: return 0;
			case CREDITS: return 1;
			case PAUSE:
			case DEATH:
			case GAME: return 2;
			case SHOP: return 3;
			case TRAIN: return 4;
			case GAMEOVER: return 5;
			case QUIT:
			case ERROR: return 6;
			default: return -1;
		}
	}
}
