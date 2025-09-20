package game.jumpy;

import java.util.ArrayList;
import java.util.List;

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
	private Sprite obstacleSprite2;

	private boolean onGround = false;

	private final float TILE_WIDTH = 0.5f;
	private final float TILE_HEIGHT = 1f;

	private int TILE_W = 32;
	private int TILE_H = 32;
	TextureRegion[][] tiles;
	private List<Sprite> obstacles = new ArrayList<Sprite>();

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

		obstacleSprite2 = new Sprite(obstacleImage);
		obstacleSprite2.setSize(TILE_WIDTH * 2, TILE_HEIGHT / 2);
		obstacleSprite2.setX(5f);
		obstacleSprite2.setY(2.0f);

		obstacles.add(obstacleSprite);
		obstacles.add(obstacleSprite2);

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
		obstacleSprite2.draw(batch);

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
		float jumpSize = 3f;
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
			playerSprite.translateY(jumpSize);
		} else if (playerSprite.getY() > 0) {
			// if not on floor, go down
			playerSprite.translateY(-jumpDownSpeed * tpf);
			onGround = false;
		}

		for (Sprite thing : obstacles) {
			handleCollisions(playerSprite, thing);
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

	private void handleCollisions(Sprite aSprite, Sprite bSprite) {
		// Resolving collisions
		if (isACollidingWithB(aSprite, bSprite)) {
			resolveCollision(aSprite, bSprite);
		}
	}

	private boolean isACollidingWithB(Sprite aSprite, Sprite bSprite) {
		return aSprite.getX() + aSprite.getWidth() > bSprite.getX()
				&& aSprite.getX() < bSprite.getX() + bSprite.getWidth()
				&& aSprite.getY() < bSprite.getY() + bSprite.getHeight()
				&& aSprite.getY() + aSprite.getHeight() > bSprite.getY();

	}

	private void resolveCollision(Sprite aSprite, Sprite bSprite) {
		// Calculate overlap on X
		float overlapX1 = aSprite.getX() + aSprite.getWidth() - bSprite.getX();
		float overlapX2 = bSprite.getX() + bSprite.getWidth() - aSprite.getX();
		float overlapX = Math.min(overlapX1, overlapX2);

		// Calculate overlap on Y
		float overlapY1 = aSprite.getY() + aSprite.getHeight() - bSprite.getY();
		float overlapY2 = bSprite.getY() + bSprite.getHeight() - aSprite.getY();
		float overlapY = Math.min(overlapY1, overlapY2);

		// Resolve collision
		if (overlapX < overlapY) {
			// Side collision
			if (aSprite.getX() < bSprite.getX()) {
				// Left
				aSprite.setX(bSprite.getX() - aSprite.getWidth());
			} else {
				// Right
				aSprite.setX(bSprite.getX() + bSprite.getWidth());
			}
		} else {
			// Top/bottom collision - land on top
			if (aSprite.getY() < bSprite.getY()) {
				// Bottom
				aSprite.setY(bSprite.getY() - aSprite.getHeight());
			} else {
				// Top
				aSprite.setY(bSprite.getY() + bSprite.getHeight());
				onGround = true;
			}
		}

	}

}
