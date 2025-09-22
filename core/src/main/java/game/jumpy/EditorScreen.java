package game.jumpy;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import game.jumpy.MapData.Tile;

public class EditorScreen implements Screen {
	private Jumpy game;
	private ScreenViewport viewport;
	private Texture tilemap;

	private int TILE_SIZE = 32;
	private TextureRegion[][] tiles;
	private TextureRegion[][] placedTiles;
	private TextureRegion selectedTile;
	private Texture emptyTile = new Texture("emptyTile.png");

	private Map<String, Tile> tilemapObjects = new HashMap<String, Tile>();

	private final String IMPORTED_TILESET = "Castle2.png";

	public EditorScreen(final Jumpy game) {
		this.game = game;
		this.viewport = new ScreenViewport();
		tilemap = new Texture(IMPORTED_TILESET);
		tiles = TextureRegion.split(tilemap, TILE_SIZE, TILE_SIZE);
		placedTiles = new TextureRegion[tiles.length][tiles[0].length];

	}

	@Override
	public void render(float delta) {
		config();

		ScreenUtils.clear(Color.TEAL);
		viewport.apply();
		game.batch.setProjectionMatrix(viewport.getCamera().combined);
		game.batch.begin();

		drawTiles();
		handleSelectTile();

		game.batch.end();

	}

	private void handleSelectTile() {
		float HEIGHT = viewport.getWorldHeight() - TILE_SIZE;
		float WIDTH = viewport.getWorldWidth() / 2;
		int mouseX = Gdx.input.getX();
		int mouseY = Gdx.input.getY();

		// compute column/row
		int col = mouseX / TILE_SIZE;
		int row = mouseY / TILE_SIZE;
		// Select tile
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
			if (row >= 0 && row < tiles.length && col >= 0 && col < tiles[0].length) {
				selectedTile = tiles[row][col];
			}
			// Checks if we're selecting empty tileset
			// To create our own map
			double firstEmptyTileCol = Math.floor(WIDTH / TILE_SIZE);
			if (row >= 0 && row < tiles.length && col >= firstEmptyTileCol
					&& col < tiles[0].length + firstEmptyTileCol) {
				if (selectedTile != null) {
					col -= firstEmptyTileCol;
					placedTiles[row][col] = selectedTile;
				}
			}
			// Object adding
		} else if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
			// Checks if we're selecting empty tileset
			Double firstEmptyTileCol = Math.floor(WIDTH / TILE_SIZE);
			if (row >= 0 && row < tiles.length && col >= firstEmptyTileCol
					&& col < tiles[0].length + firstEmptyTileCol) {
				// Add an object if there's a tile there
				String objectName = null;
				// Hacky way of defining a couple of specific objects
				if (Gdx.input.isKeyPressed(Input.Keys.S)) {
					objectName = MapData.START_OBJ;
				}
				if (Gdx.input.isKeyPressed(Input.Keys.E)) {
					objectName = MapData.END_OBJ;
				}
				if (Gdx.input.isKeyPressed(Input.Keys.P)) {
					objectName = MapData.SCORE_POINT_OBJ;
				}
				// Set an object only when specific buttons are pressed
				if (objectName != null) {
					Tile objectTile = new Tile();
					objectTile.setCol(col - firstEmptyTileCol.intValue()); // since cols are little more to the right
					objectTile.setRow(tiles.length - row); // since we're setting from the bottom up
					boolean isAdd = true;
					for (Entry<String, Tile> insertedObject : tilemapObjects.entrySet()) {
						// Add only unique objects in coordinates and name.
						// Maybe not a great idea but works for now
						if (insertedObject.getKey().equals(objectName)
								&& objectTile.equals(insertedObject.getValue())) {
							isAdd = false;
						}
					}
					if (tilemapObjects.isEmpty() || isAdd) {
						tilemapObjects.put(objectName, objectTile);
						System.out.println("Object " + objectName + " has been added");

					}
				}
			}
		}
		// Add it to the mouse
		if (selectedTile != null) {
			game.batch.draw(selectedTile, mouseX, viewport.getWorldHeight() - mouseY, TILE_SIZE, TILE_SIZE);
		}
	}

	private MapData getParsedMapData() {
		MapData mapData = new MapData();

		// Tileset data
		MapData.Tileset tileSetData = new MapData.Tileset();
		tileSetData.setImage(IMPORTED_TILESET);
		tileSetData.setTileHeight(TILE_SIZE);
		tileSetData.setTileWidth(TILE_SIZE);
		Map<Integer, Tile> tileSetTiles = new HashMap<Integer, Tile>();

		// Created tile data
		MapData.Tilemap tileMapData = new MapData.Tilemap();
		tileMapData.setWidth(placedTiles.length);
		tileMapData.setHeight(placedTiles[0].length);
		int[][] tileMapDataTiles = new int[tileMapData.getWidth()][tileMapData.getHeight()];
		for (int row = 0; row < tileMapData.getWidth(); row++) {
			for (int col = 0; col < tileMapData.getHeight(); col++) {
				TextureRegion currentSelectedTile = placedTiles[row][col];
				// Empty tiles
				if (currentSelectedTile == null) {
					tileMapDataTiles[row][col] = -1;
					continue;
				}
				// Inefficiently loop through originally split tiles and search for a match
				for (int row1 = 0; row1 < tiles.length; row1++) {
					for (int col1 = 0; col1 < tiles[0].length; col1++) {
						if (tiles[row1][col1] == currentSelectedTile) {
							Integer idx = tileSetTiles.size() + 1;
							MapData.Tile tile = new MapData.Tile();
							tile.setRow(row1);
							tile.setCol(col1);
							// Reuse already inserted tiles
							for (Entry<Integer, Tile> entry : tileSetTiles.entrySet()) {
								MapData.Tile t = entry.getValue();
								if (t.getCol() == col1 && t.getRow() == row1) {
									idx = entry.getKey();
								}
							}
							tileSetTiles.put(idx, tile);
							tileMapDataTiles[row][col] = Integer.valueOf(idx);
							break;
						}
					}
				}
			}
		}
		// Add objects if there are none
		if (!tilemapObjects.isEmpty()) {
			tileMapData.setObjectsList(tilemapObjects);
		}
		tileMapData.setTiles(tileMapDataTiles);
		tileSetData.setTiles(tileSetTiles);
		mapData.setMap(tileMapData);
		mapData.setTileset(tileSetData);
		return mapData;
	}

	private void drawTiles() {
		float HEIGHT = viewport.getWorldHeight() - TILE_SIZE;
		float WIDTH = viewport.getWorldWidth() / 2;
		// Draw tileset
		for (int row = 0; row < tiles.length; row++) {
			for (int col = 0; col < tiles[row].length; col++) {
				float x = col * TILE_SIZE;
				float y = HEIGHT - row * TILE_SIZE; // start from the top of the window since bathc sprite is bottom
													// left at 0, 0
				game.batch.draw(tiles[row][col], x, y, TILE_SIZE, TILE_SIZE);
			}
		}
		// draw empty tiles
		for (int row = 0; row < tiles.length; row++) {
			for (int col = 0; col < tiles[row].length; col++) {
				float x = WIDTH + col * TILE_SIZE;
				float y = HEIGHT - row * TILE_SIZE;
				if (placedTiles[row][col] != null) {
					game.batch.draw(placedTiles[row][col], x, y, TILE_SIZE, TILE_SIZE);
				} else {
					game.batch.draw(emptyTile, x, y, TILE_SIZE, TILE_SIZE);
				}
			}
		}
	}

	private void config() {
		if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
			Gdx.app.exit();
			System.exit(0);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.F9)) {
			game.setScreen(new GameScreen(game));
		}
		if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyPressed(Input.Keys.S)) {
			Json json = new Json();
			FileHandle fh = Gdx.files.local("map.json");
			MapData mapData = getParsedMapData();
			json.setUsePrototypes(false); // forces all fields to be saved
			fh.writeString(json.prettyPrint(mapData), false);
			System.out.println("Saved map at: " + Gdx.files.local("map.json").file().getAbsolutePath());
		}
	}

	@Override
	public void show() {
		Gdx.graphics.setWindowedMode(1440, 800);
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);

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
