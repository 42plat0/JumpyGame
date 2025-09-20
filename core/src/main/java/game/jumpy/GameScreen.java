package game.jumpy;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen implements Screen {

	final Jumpy game;
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

	public GameScreen(final Jumpy game) {
		this.game = game;

		// Player
		playerImage = new Texture("rectangle.png");
		playerSprite = new Sprite(playerImage);
		playerSprite.setSize(TILE_WIDTH, TILE_HEIGHT);

		obstacleImage = new Texture("obstacle.png");
		obstacleSprite = new Sprite(obstacleImage);
		obstacleSprite.setSize(TILE_WIDTH * 2, TILE_HEIGHT / 2);
		obstacleSprite.setX(3f);
		obstacleSprite.setY(1.5f);

		obstacleSprite2 = new Sprite(obstacleImage);
		obstacleSprite2.setSize(TILE_WIDTH * 2, TILE_HEIGHT / 2);
		obstacleSprite2.setX(5f);
		obstacleSprite2.setY(2.0f);

		obstacles.add(obstacleSprite);
		obstacles.add(obstacleSprite2);

	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(Color.BLACK);
		game.viewport.apply();
		game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
		game.batch.begin();

		input();
		logic();
		// My configuration for turning off window
		// and switching between windows
		config();

		playerSprite.draw(game.batch);
		obstacleSprite.draw(game.batch);
		obstacleSprite2.draw(game.batch);

		game.batch.end();

	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resize(int width, int height) {
		game.viewport.update(width, height, true);

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
		game.batch.dispose();
		playerImage.dispose();
		obstacleImage.dispose();
	}

	private void logic() {
		// prevent from going out of window
		playerSprite
				.setX(MathUtils.clamp(playerSprite.getX(), 0, game.viewport.getWorldWidth() - playerSprite.getWidth()));
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
			playerSprite.translateX(MathUtils.clamp(speed * tpf, 0, game.viewport.getWorldWidth()));
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

	}

	private void config() {
		if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
			Gdx.app.exit();
			System.exit(0);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.F8)) {
			game.setScreen(new EditorScreen(game));
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
				System.out.println("bottom");
			} else {
				// Top
				aSprite.setY(bSprite.getY() + bSprite.getHeight());
				onGround = true;
			}
		}

	}

}
