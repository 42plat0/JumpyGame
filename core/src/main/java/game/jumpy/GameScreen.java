package game.jumpy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import game.jumpy.MapData.Tile;
import game.jumpy.MapData.Tilemap;
import game.jumpy.MapData.Tileset;

public class GameScreen implements Screen {

	final Jumpy game;
	private Texture playerImage;
	private Texture obstacleImage;
	private Sprite playerSprite;
	private Texture tilemap;

	private TextureRegion[][] tiles;
	private final String IMPORTED_TILESET = "Castle2.png";
	private MapData mapData;
	private boolean onGround = false;
	private FitViewport viewport;

	private final float TILE_SIZE = 0.5f;
	private final float TILE_WIDTH = 1f;
	private final float TILE_HEIGHT = 1f;

	private List<Sprite> obstacles = new ArrayList<Sprite>();

	public GameScreen(final Jumpy game) {
		this.game = game;

		// Player
		playerImage = new Texture("rectangle.png");
		playerSprite = new Sprite(playerImage);
		playerSprite.setSize(TILE_WIDTH, TILE_HEIGHT);

		this.viewport = new FitViewport(8, 5);
//		obstacles.add(obstacleSprite);
//		obstacles.add(obstacleSprite2);

	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(Color.BLACK);

		viewport.apply();
		game.batch.setProjectionMatrix(viewport.getCamera().combined);
		game.batch.begin();

		input();
		logic();
		// My configuration for turning off window
		// and switching between windows
		config();
		drawMap();
		playerSprite.draw(game.batch);
//		obstacleSprite.draw(game.batch);
//		obstacleSprite2.draw(game.batch);

		game.batch.end();

	}

	public void drawMap() {
		// Load mapData for 1st time
		if (mapData == null) {
			Json json = new Json();
			FileHandle fh = Gdx.files.local("map.json");
			mapData = json.fromJson(MapData.class, fh.readString());
			return;
		}

		float HEIGHT = viewport.getWorldHeight() - TILE_SIZE;
		float WIDTH = viewport.getWorldWidth() / 2;
		Tilemap map = mapData.getMap();
		int[][] mapTiles = map.getTiles();

		Tileset tilesetData = mapData.getTileset();
		tilemap = new Texture(tilesetData.getImage());
		tiles = TextureRegion.split(tilemap, tilesetData.getTileWidth(), tilesetData.getTileHeight());

		Map<Integer, Tile> tilesetTiles = tilesetData.getTiles();

		// Draw tileset
		for (int row = 0; row < mapTiles.length; row++) {
			for (int col = 0; col < mapTiles[row].length; col++) {
				float x = col * TILE_SIZE;
				float y = HEIGHT - row * TILE_SIZE; // start from the top of the window since bathc sprite is bottom
													// left at 0, 0
				int tileId = mapTiles[row][col];
				System.out.println(tileId);
				if (tileId != -1) {
					Tile tileFromTileset = tilesetTiles.get(String.valueOf(tileId));
					System.out.println(mapTiles[row][col]);
					game.batch.draw(tiles[tileFromTileset.getCol()][tileFromTileset.getRow()], x, y, TILE_SIZE,
							TILE_SIZE);
				} else {
					game.batch.draw(tiles[0][0], x, y, TILE_SIZE, TILE_SIZE);
				}
//				game.batch.draw(tiles[row][col], x, y, TILE_SIZE, TILE_SIZE);
			}
		}
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
		viewport.getCamera().position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
		viewport.getCamera().update();

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
		playerSprite.setX(MathUtils.clamp(playerSprite.getX(), 0, viewport.getWorldWidth() - playerSprite.getWidth()));
	}

	private void input() {
		float jumpDownSpeed = 32f;
		float jumpSize = 32f;
		float speed = 32f * 3;
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
			// TODO fix hitting head with player and not teleporting on top
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
