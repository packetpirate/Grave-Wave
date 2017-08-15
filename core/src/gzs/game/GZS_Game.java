package gzs.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;

import gzs.game.info.Globals;

public class GZS_Game extends ApplicationAdapter {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	
	@Override
	public void create () {
		camera = new OrthographicCamera(Globals.WIDTH, Globals.HEIGHT);
		camera.setToOrtho(true, Globals.WIDTH, Globals.HEIGHT);
		
		batch = new SpriteBatch();
	}

	public void update() {
		long cTime = TimeUtils.millis();
	}
	
	@Override
	public void render () {
		update();
		
		long cTime = TimeUtils.millis();
		
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// Begin drawing here.
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
