package game.jumpy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
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

	public final Jumpy game;
	private Texture playerImage;
	private Sprite playerSprite;
	private Texture backgroundTexture;
	private Sprite backgroundSprite;
	private Texture tilemap;
	private FitViewport viewport;
	private MapData mapData;
	private TextureRegion[][] tiles;

	private final float TILE_SIZE = 1f;
	private final float GRAVITY = 3f;
	private final float JUMP_SPEED = TILE_SIZE * 4f;
	private final float VELOCITY = TILE_SIZE * 8f;
	private float jumpVelocity = 0f;
	private boolean onGround = false;
	private boolean isWalking = false;

	private List<Sprite> obstacles = new ArrayList<Sprite>();
	private Map<String, Sprite> objects = new HashMap<String, Sprite>();

	private FileHandle folder = Gdx.files.local("levels/");
	private String mapFile;

	private Texture playerSpriteSheet;
	private Animation<TextureRegion> idleAnimation;
	private Animation<TextureRegion> walkingAnimation;
	private float idleStateTime = 0f;
	private float walkingStateTime = 0f;
	private boolean flip = false;
	private boolean lastFacingLeft = false;

	public GameScreen(final Jumpy game, String mapFile) {
		this.game = game;
		this.viewport = new FitViewport(16, 16);
		this.mapFile = mapFile;

		// Player
		playerImage = new Texture("rectangle.png");
		playerSprite = new Sprite(playerImage);
		playerSprite.setSize(TILE_SIZE, TILE_SIZE);

		backgroundTexture = new Texture("background_layer_1.png");
		backgroundSprite = new Sprite(backgroundTexture);
		backgroundSprite.setSize(16, 16);

		handlePlayerSprite();
	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(Color.BLACK);
		viewport.apply();
		game.batch.setProjectionMatrix(viewport.getCamera().combined);
		loadMap();

		// Animations for player sprite
		// Idle animation
		TextureRegion idleCurrentFrame = idleAnimation.getKeyFrame(idleStateTime, true);

		// Walking animation
		if (isWalking) {
			walkingStateTime += delta;
		} else {
			idleStateTime += delta;
		}
		TextureRegion walkingCurrentFrame = walkingAnimation.getKeyFrame(walkingStateTime, true);

		game.batch.begin();
		backgroundSprite.draw(game.batch);
		input();
		logic();
		drawMap();

		if (isWalking) {
			if (walkingCurrentFrame.isFlipX() != flip) {
				walkingCurrentFrame.flip(true, false);
			}
			game.batch.draw(walkingCurrentFrame, playerSprite.getX(), playerSprite.getY(), TILE_SIZE * 2, TILE_SIZE);
		} else {
			if (idleCurrentFrame.isFlipX() != lastFacingLeft) {
				idleCurrentFrame.flip(true, false);
			}
			game.batch.draw(idleCurrentFrame, playerSprite.getX(), playerSprite.getY(), TILE_SIZE * 2, TILE_SIZE);
		}

		game.batch.end();
		config();

	}

	public void handlePlayerSprite() {
		playerSpriteSheet = new Texture("frog_spritesheet.png");
		TextureRegion[][] sheet = TextureRegion.split(playerSpriteSheet, 64, 32);

		// Get only idle frames from sprite sheet
		int idleFrameSize = 4;
		TextureRegion[] idleFrames = new TextureRegion[idleFrameSize];
		for (int i = 0; i < idleFrameSize; i++) {
			idleFrames[i] = sheet[0][i];
		}
		idleAnimation = new Animation<TextureRegion>(0.1f, idleFrames);
		int walkingFrameSize = 7;
		TextureRegion[] walkingFrames = new TextureRegion[walkingFrameSize];
		for (int i = 0; i < walkingFrameSize; i++) {
			walkingFrames[i] = sheet[1][i];
		}
		walkingAnimation = new Animation<TextureRegion>(0.1f, walkingFrames);
	}

	private void loadMap() {
		// Load mapData for 1st time
		if (mapData == null) {
			Json json = new Json();
			FileHandle fh = folder.child(mapFile);
			if (fh.exists()) {
				mapData = json.fromJson(MapData.class, fh.readString());
				return;
			}
			game.setScreen(new EditorScreen(game));
			dispose();
		}

	}

	@SuppressWarnings("unlikely-arg-type")
	public void drawMap() {
		if (mapData == null) {
			return;
		}

		float HEIGHT = viewport.getWorldHeight() - TILE_SIZE;
		float WIDTH = viewport.getWorldWidth() / 2;
		Tilemap map = mapData.getMap();
		int[][] mapTiles = map.getTiles();

		Tileset tilesetData = mapData.getTileset();
		tilemap = new Texture(tilesetData.getImage());
		tiles = TextureRegion.split(tilemap, tilesetData.getTileWidth(), tilesetData.getTileHeight());

		Map<String, Tile> userObjects = map.getObjects();
		if (objects.isEmpty() && userObjects != null) {
			// Set objects
			for (Entry<String, Tile> object : userObjects.entrySet()) {
				String objectName = object.getKey();
				Tile objectTile = object.getValue();

				if (MapData.START_OBJ.equals(objectName) && playerSprite != null) {
					float x = objectTile.getCol() * TILE_SIZE;
					float y = objectTile.getRow() * TILE_SIZE;
					playerSprite.setPosition(x, y);
					objects.put(objectName, null); // dead object
				}
				if (MapData.END_OBJ.equals(objectName) && playerSprite != null) {
					// Do something when end of the level is reached
					float x = objectTile.getCol() * TILE_SIZE;
					float y = objectTile.getRow() * TILE_SIZE;
					Sprite endSprite = new Sprite();
					endSprite.setPosition(x, y);
					endSprite.setBounds(x, y, TILE_SIZE, TILE_SIZE);
					objects.put(objectName, endSprite);
				}
				if (MapData.SCORE_POINT_OBJ.equals(objectName) && playerSprite != null) {
					// Do something when player gets a point
				}
			}
		}
		Map<Integer, Tile> tilesetTiles = tilesetData.getTiles();

		// Draw tileset
		for (int row = 0; row < mapTiles.length; row++) {
			for (int col = 0; col < mapTiles[row].length; col++) {
				float x = col * TILE_SIZE;
				float y = HEIGHT - row * TILE_SIZE; // start from the top of the window since bathc sprite is bottom
													// left at 0, 0
				int tileId = mapTiles[row][col];
				int tileToPlaceRow = 0;
				int tileToPlaceCol = 0;
				// is not empty tile
				if (tileId != -1) {
					// map gives null when accessing with int
					Tile tileFromTileset = tilesetTiles.get(String.valueOf(tileId));
					tileToPlaceRow = tileFromTileset.getRow();
					tileToPlaceCol = tileFromTileset.getCol();
				}
				// Draw either an empty tile or actual placed tile
				game.batch.draw(tiles[tileToPlaceRow][tileToPlaceCol], x, y, TILE_SIZE, TILE_SIZE);
			}
		}

		// Add collidables only once
		if (obstacles.isEmpty()) {
			for (int row = 0; row < mapTiles.length; row++) {
				for (int col = 0; col < mapTiles[row].length; col++) {
					int tileId = mapTiles[row][col];
					if (tileId != -1) {
						float x = col * TILE_SIZE;
						float y = HEIGHT - row * TILE_SIZE; // start from the top of the window since bathc sprite is
															// bottom
															// left at 0, 0

						Tile tileFromTileset = tilesetTiles.get(String.valueOf(tileId));
						Sprite collisionObject = new Sprite(
								tiles[tileFromTileset.getRow()][tileFromTileset.getCol()].getTexture());
						collisionObject.setBounds(x, y, TILE_SIZE, TILE_SIZE);
						obstacles.add(collisionObject);

					}
				}
			}
		}
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
		viewport.getCamera().position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
		viewport.getCamera().update();
	}

	@Override
	public void dispose() {
		playerImage.dispose();
		playerSpriteSheet.dispose();
	}

	private void logic() {
		// prevent from going out of window
		playerSprite.setX(MathUtils.clamp(playerSprite.getX(), 0, viewport.getWorldWidth() - playerSprite.getWidth()));
	}

	private void input() {
		float tpf = Gdx.graphics.getDeltaTime();

		isWalking = isLeft() || isRight();
		flip = isLeft() ? true : false;
		if (isWalking) {
			if (isLeft()) {
				lastFacingLeft = true;
			} else if (isRight()) {
				lastFacingLeft = false;
			}
		}

		if (isLeft()) {
			playerSprite.translateX(-VELOCITY * tpf);
		}
		if (isRight()) {
			playerSprite.translateX(MathUtils.clamp(VELOCITY * tpf, 0, viewport.getWorldWidth()));
		}

		if (playerSprite.getY() <= 0) {
			onGround = true;
		}

		if (isJump() && onGround) {
			jumpVelocity = JUMP_SPEED;
		} else if (playerSprite.getY() > 0) {
			// if not on floor, go down
			jumpVelocity = -GRAVITY * tpf;
			onGround = false;
		}
		playerSprite.translateY(jumpVelocity);

		for (Entry<String, Sprite> object : objects.entrySet()) {
			String objectName = object.getKey();
			Sprite objectSprite = object.getValue();

			if (MapData.END_OBJ.equals(objectName) && isACollidingWithB(playerSprite, objectSprite)) {
				System.out.println("Reached the end!");
				// Player touched END
				String nextLevel = getLastLevelName();
				if (folder.child(nextLevel).exists()) {
					game.setScreen(new GameScreen(game, nextLevel));
					dispose();
					return;
				}
				game.setScreen(new EndScreen(game, "You win!"));
				dispose();
			}
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
		if (Gdx.input.isKeyPressed(Input.Keys.M)) {
			game.setScreen(new EditorScreen(game));
			dispose();
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

	private String getLastLevelName() {
		String number = mapFile.replace("level", "").replace(".json", "");
		int next = Integer.parseInt(number) + 1;
		return "level" + next + ".json";
	}

	private boolean isACollidingWithB(Sprite aSprite, Sprite bSprite) {
		return aSprite.getX() + aSprite.getWidth() > bSprite.getX()
				&& aSprite.getX() < bSprite.getX() + bSprite.getWidth()
				&& aSprite.getY() < bSprite.getY() + bSprite.getHeight()
				&& aSprite.getY() + aSprite.getHeight() > bSprite.getY();

	}

	private void resolveCollision(Sprite aSprite, Sprite bSprite) {
		// Calculate overlap on X
		float overlapX1 = aSprite.getX() + TILE_SIZE - bSprite.getX();
		float overlapX2 = bSprite.getX() + TILE_SIZE - aSprite.getX();
		float overlapX = Math.min(overlapX1, overlapX2);

		// Calculate overlap on Y
		float overlapY1 = aSprite.getY() + TILE_SIZE - bSprite.getY();
		float overlapY2 = bSprite.getY() + TILE_SIZE - aSprite.getY();
		float overlapY = Math.min(overlapY1, overlapY2);

		// Resolve collision
		if (overlapX < overlapY) {
			// Side collision
			if (aSprite.getX() < bSprite.getX()) {
				// Left
				aSprite.setX(bSprite.getX() - TILE_SIZE);
			} else {
				// Right
				aSprite.setX(bSprite.getX() + TILE_SIZE);
			}
		} else {
			// Top/bottom collision - land on top
			// TODO fix hitting head with player and not teleporting on top
			if (aSprite.getY() < bSprite.getY()) {
				// Bottom
				aSprite.setY(bSprite.getY() - TILE_SIZE);
				jumpVelocity = 0;
			} else {
				// Top
				aSprite.setY(bSprite.getY() + TILE_SIZE);
				onGround = true;
			}
		}

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
	public void show() {
		// TODO Auto-generated method stub

	}
}
