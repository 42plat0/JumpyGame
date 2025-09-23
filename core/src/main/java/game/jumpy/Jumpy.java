package game.jumpy;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Jumpy extends Game {
	public SpriteBatch batch;
	// even tho we have viewports for screens
	// this prevents initial error
	public ScreenViewport viewport;
	public BitmapFont font;

	@Override
	public void create() {
		batch = new SpriteBatch();
		viewport = new ScreenViewport();
		font = new BitmapFont();

		this.setScreen(new GameScreen(this, "level1.json"));
//		this.setScreen(new EndScreen(this, "asdf"));
	}

	@Override
	public void dispose() {
		batch.dispose();
	}
}
