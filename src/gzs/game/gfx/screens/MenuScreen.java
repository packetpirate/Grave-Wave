package gzs.game.gfx.screens;

import gzs.game.gfx.Screen;
import gzs.game.gfx.screens.components.MenuButton;
import gzs.game.info.Globals;
import gzs.game.misc.MouseInfo;
import gzs.game.utils.FileUtilities;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class MenuScreen implements Screen {
	private Image background;
	private MenuButton gameStart;
	private MenuButton credits;
	private MenuButton exit;
	
	public MenuScreen() {
		background = FileUtilities.LoadImage("GZS_Splash.png");
		
		gameStart = new MenuButton(((Globals.WIDTH / 2) - 60), (Globals.HEIGHT - 250), 
									100, 40, "Start Game");
		credits = new MenuButton(((Globals.WIDTH / 2) - 60), (Globals.HEIGHT - 200),
								 100, 40, "Credits");
		exit = new MenuButton(((Globals.WIDTH / 2) - 60), (Globals.HEIGHT - 150),
							  100, 40, "Exit Game");
	}
	
	@Override
	public void update(long cT) {
		if(gameStart.contains(Globals.mouse)) gameStart.mouseEnter();
		else gameStart.mouseExit();
		
		if(credits.contains(Globals.mouse)) credits.mouseEnter();
		else credits.mouseExit();
		
		if(exit.contains(Globals.mouse)) exit.mouseEnter();
		else exit.mouseExit();
	}

	@Override
	public void render(GraphicsContext gc, long cT) {
		if(background != null) gc.drawImage(background, 0, 0);
		
		gameStart.render(gc, cT);
		credits.render(gc, cT);
		exit.render(gc, cT);
	}

	@Override
	public void dispatchClick(MouseInfo mouse) throws Exception {
		if(gameStart.contains(mouse)) Globals.getGSM().transition("start game");
		else if(credits.contains(mouse)) Globals.getGSM().transition("credits");
		else if(exit.contains(mouse)) Globals.getGSM().transition("quit");
	}

	@Override
	public boolean hidesCursor() {
		return false;
	}
}
