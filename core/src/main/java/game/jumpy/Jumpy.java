package game.jumpy;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Jumpy extends Game {
	public SpriteBatch batch;
//	public FitViewport viewport;
	public ScreenViewport viewport;

	@Override
	public void create() {
		batch = new SpriteBatch();
//		viewport = new FitViewport(8, 5);
		viewport = new ScreenViewport();

//		this.setScreen(new GameScreen(this));
		this.setScreen(new EditorScreen(this, viewport));
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
