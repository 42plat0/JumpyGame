package game.jumpy;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class JumpyGame implements ApplicationListener {
	private SpriteBatch batch;
	private FitViewport viewport;
	private Texture playerImage;
	private Texture obstacleImage;
	private Texture tilemap;
	private Sprite playerSprite;
	private Sprite obstacleSprite;

	private float playerVelY = 0f;
	private final float GRAVITY = -15f;
	private final float JUMP_VELOCITY = 6f;
	private boolean onGround = false;

	private final float TILE_WIDTH = 0.5f;
	private final float TILE_HEIGHT = 1f;

	private int TILE_W = 32;
	private int TILE_H = 32;
	TextureRegion[][] tiles;

	@Override
	public void create() {
		batch = new SpriteBatch();
		viewport = new FitViewport(12, 10);

		// Player
		playerImage = new Texture("rectangle.png");
		playerSprite = new Sprite(playerImage);
		playerSprite.setSize(TILE_WIDTH, TILE_HEIGHT);

		obstacleImage = new Texture("obstacle.png");
		obstacleSprite = new Sprite(obstacleImage);
		obstacleSprite.setSize(TILE_WIDTH * 2, TILE_HEIGHT / 2);
		obstacleSprite.setX(3f);
		obstacleSprite.setY(0.5f);
//
//		tilemap = new Texture("Castle2.png");
//		tiles = TextureRegion.split(tilemap, TILE_W, TILE_H);

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
//		for (int i = 0; i < 15; i++) {
//			batch.draw(tiles[9][i], i, 0.5f, 0.5f, 0.5f);
//			batch.draw(tiles[9][i], i, 0.5f, 0.5f, 0.5f);
//		}

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

	private void doMap() {

	}

	private void logic() {
		// prevent from going out of window
		playerSprite.setX(MathUtils.clamp(playerSprite.getX(), 0, viewport.getWorldWidth() - playerSprite.getWidth()));
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
		if (playerSprite.getY() <= 0) {
			onGround = true;
		}

		if (isJump() && onGround) {
			playerSprite.translateY(2.0f);
		} else if (playerSprite.getY() > 0) {
			// if not on floor, go down
			playerSprite.translateY(-jumpDownSpeed * tpf);
			onGround = false;
		}

		// Resolving collisions
		if (isACollidingWithB(playerSprite, obstacleSprite)) {
			// Calculate overlap on X
			float overlapX1 = playerSprite.getX() + playerSprite.getWidth() - obstacleSprite.getX();
			float overlapX2 = obstacleSprite.getX() + obstacleSprite.getWidth() - playerSprite.getX();
			float overlapX = Math.min(overlapX1, overlapX2);

			float overlapY1 = playerSprite.getY() + playerSprite.getHeight() - obstacleSprite.getY();
			float overlapY2 = obstacleSprite.getY() + obstacleSprite.getHeight() - playerSprite.getY();
			float overlapY = Math.min(overlapY1, overlapY2);

			// Resolve collision
			if (overlapX < overlapY) {
				// Side collision
				if (playerSprite.getX() < obstacleSprite.getX()) {
					playerSprite.setX(obstacleSprite.getX() - playerSprite.getWidth());
				} else {
					playerSprite.setX(obstacleSprite.getX() + obstacleSprite.getWidth());
				}
			} else {
				// Top/bottom collision - land on top
				if (playerSprite.getY() < obstacleSprite.getY()) {
					playerSprite.setY(obstacleSprite.getY() - playerSprite.getHeight());
				} else {
					playerSprite.setY(obstacleSprite.getY() + obstacleSprite.getHeight());
					onGround = true;
				}
			}
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

	private boolean isACollidingWithB(Sprite aSprite, Sprite bSprite) {
		return aSprite.getX() + aSprite.getWidth() > bSprite.getX()
				&& aSprite.getX() < bSprite.getX() + bSprite.getWidth()
				&& aSprite.getY() < bSprite.getY() + bSprite.getHeight()
				&& aSprite.getY() + aSprite.getHeight() > bSprite.getY();

	}

}
