package game.jumpy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class EndScreen implements Screen {
	private final Jumpy game;
	private final String message;
	private ScreenViewport viewport;

	public EndScreen(Jumpy game, String message) {
		this.game = game;
		this.message = message;
		viewport = new ScreenViewport();
	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(Color.BLACK);
		viewport.apply();
		game.batch.setProjectionMatrix(viewport.getCamera().combined);
		game.batch.begin();
		game.font.setColor(Color.GREEN);
		game.font.getData().setScale(2f);
		game.font.draw(game.batch, message, 100, 300);
		game.font.draw(game.batch, "Press ENTER to close", 100, 200);
		game.batch.end();

		if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			dispose();
			Gdx.app.exit();
			System.exit(0);
		}
	}

	// Empty methods you don’t need right now
	@Override
	public void show() {
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
		viewport.getCamera().position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
		viewport.getCamera().update();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
	}
}
