package gzs.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.TimeUtils;

import gzs.entities.Player;
import gzs.game.info.Globals;

public class GZS_Game extends ApplicationAdapter implements InputProcessor {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private ShapeRenderer sr;
	
	private Player player;
	
	@Override
	public void create () {
		camera = new OrthographicCamera(Globals.WIDTH, Globals.HEIGHT);
		camera.setToOrtho(true, Globals.WIDTH, Globals.HEIGHT);
		
		ShaderProgram shader = ImmediateModeRenderer20.createDefaultShader(false, true, 0);
		
		batch = new SpriteBatch(5000, shader);
		sr = new ShapeRenderer();
		
		player = new Player();
		
		Cursor customCursor = Gdx.graphics.newCursor(new Pixmap(Gdx.files.internal("images/GZS_Crosshair.png")), 16, 16);
		Gdx.graphics.setCursor(customCursor);
		
		Gdx.input.setInputProcessor(this);
	}

	public void update() {
		long cTime = TimeUtils.millis();
		player.update(cTime);
	}
	
	@Override
	public void render () {
		update();
		
		long cTime = TimeUtils.millis();
		
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		sr.setProjectionMatrix(camera.combined);
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// Begin drawing here.
		player.render(batch, sr, cTime);
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		Globals.mouse.setPosition(screenX, screenY);
		return true;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(button == Input.Buttons.LEFT) {
			Globals.mouse.setMouseDown(true);
			return true;
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(button == Input.Buttons.LEFT) {
			Globals.mouse.setMouseDown(false);
			return true;
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}
}
