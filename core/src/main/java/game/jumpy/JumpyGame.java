package game.jumpy;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class JumpyGame implements ApplicationListener {
	private SpriteBatch batch;
	private FitViewport viewport;
	private Texture image;
	private Sprite imageSprite;

	@Override
	public void create() {
		batch = new SpriteBatch();
		viewport = new FitViewport(8, 6);
		image = new Texture("libgdx.png");
		imageSprite = new Sprite(image);
		imageSprite.setSize(1, 1);
	}

	@Override
	public void render() {
		ScreenUtils.clear(Color.BLACK);
		viewport.apply();
		batch.setProjectionMatrix(viewport.getCamera().combined);
		batch.begin();

		input();

		imageSprite.draw(batch);
//		batch.draw(image, 140, 210);

		batch.end();
	}

	@Override
	public void dispose() {
		batch.dispose();
		image.dispose();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	private void input() {
		float speed = 4f;
		float tpf = Gdx.graphics.getDeltaTime();

		if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			imageSprite.translateX(-speed * tpf);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			imageSprite.translateX(speed * tpf);
		}

		// My configuration
		if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
			Gdx.app.exit();
			System.exit(0);
		}
	}

}
