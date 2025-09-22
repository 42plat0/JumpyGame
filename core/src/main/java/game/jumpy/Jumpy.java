package game.jumpy;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Jumpy extends Game {
	public SpriteBatch batch;
	// even tho we have viewports for screens
	// this prevents initial error
	public ScreenViewport viewport;

	@Override
	public void create() {
		batch = new SpriteBatch();
		viewport = new ScreenViewport();

//		this.setScreen(new GameScreen(this));
		this.setScreen(new EditorScreen(this));
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void dispose() {
		batch.dispose();
	}
}
