package game.jumpy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;

public class EditorScreen implements Screen {

	private Jumpy game;

	public EditorScreen(final Jumpy game) {
		this.game = game;
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		if (Gdx.input.isKeyPressed(Input.Keys.F9)) {
			game.setScreen(new GameScreen(game));
		}
	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
