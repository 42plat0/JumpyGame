package game.jumpy;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class JumpyGame implements ApplicationListener {
	private SpriteBatch batch;
	private FitViewport viewport;
	private Texture playerImage;
	private Texture obstacleImage;
	private Sprite playerSprite;
	private Sprite obstacleSprite;

	private final float TILE_WIDTH = 0.5f;
	private final float TILE_HEIGHT = 1f;

	@Override
	public void create() {
		batch = new SpriteBatch();
		viewport = new FitViewport(8, 6);

		// Player
		playerImage = new Texture("rectangle.png");
		playerSprite = new Sprite(playerImage);
		playerSprite.setSize(TILE_WIDTH, TILE_HEIGHT);

		obstacleImage = new Texture("obstacle.png");
		obstacleSprite = new Sprite(obstacleImage);
		obstacleSprite.setSize(TILE_WIDTH * 2, TILE_HEIGHT / 2);
		obstacleSprite.setX(3f);
		obstacleSprite.setY(0);

	}

	@Override
	public void render() {
		ScreenUtils.clear(Color.BLACK);
		viewport.apply();
		batch.setProjectionMatrix(viewport.getCamera().combined);
		batch.begin();

		input();
		logic();

		playerSprite.draw(batch);
		obstacleSprite.draw(batch);

		batch.end();
	}

	@Override
	public void dispose() {
		batch.dispose();
		playerImage.dispose();
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

	private void logic() {
		// prevent from going out of window
		playerSprite.setX(MathUtils.clamp(playerSprite.getX(), 0, viewport.getWorldWidth() - playerSprite.getWidth()));

		// handle collisions with aabb
		boolean isCollisionOnRight = playerSprite.getX() + playerSprite.getWidth() > obstacleSprite.getX();
		boolean isCollisionOnLeft = playerSprite.getX() < obstacleSprite.getX() + obstacleSprite.getWidth();
		boolean isCollidingOnX = isCollisionOnLeft && isCollisionOnRight;
		boolean isPlayerOnObstacle = playerSprite.getY() < obstacleSprite.getY() + obstacleSprite.getHeight();

//		if (isCollidingOnX) {
//			playerSprite.setX(MathUtils.clamp(playerSprite.getX(), 0, obstacleSprite.getX() - playerSprite.getWidth()));
//		}
		if (isPlayerOnObstacle && isCollidingOnX) {
			System.out.println(playerSprite.getY() + " on " + (obstacleSprite.getY() + playerSprite.getHeight()));
		}
		boolean isCollisionOnTop = playerSprite.getX() > obstacleSprite.getX();
	}

	private void input() {
		float jumpDownSpeed = 8f;
		float speed = 4f;
		float tpf = Gdx.graphics.getDeltaTime();

		if (isLeft()) {
			playerSprite.translateX(-speed * tpf);
		}
		if (isRight()) {
			playerSprite.translateX(MathUtils.clamp(speed * tpf, 0, viewport.getWorldWidth()));
		}
		if (isJump()) {
			// jump once ground was touched.
			if (playerSprite.getY() < 0.05) {
				// Random numbers mixed together by feels
				playerSprite.translateY(1.0f);
			}
		} else if (playerSprite.getY() > 0) {
			// if not on floor, go down
			playerSprite.translateY(MathUtils.lerp(playerSprite.getY(), 0, jumpDownSpeed) * tpf);
		}

		// My configuration for turning off window
		if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
			Gdx.app.exit();
			System.exit(0);
		}
	}

	private boolean isLeft() {
		return Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
	}

	private boolean isRight() {
		return Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);
	}

	private boolean isJump() {
		return Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP);
	}

}
