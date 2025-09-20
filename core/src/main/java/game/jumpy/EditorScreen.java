package game.jumpy;

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

public class EditorScreen implements Screen {
	private Jumpy game;
	private ScreenViewport viewport;
	private Texture tilemap;

	private int TILE_SIZE = 32;
	private TextureRegion[][] tiles;
	private TextureRegion[][] placedTiles;
	private TextureRegion selectedTile;
	private Texture emptyTile = new Texture("emptyTile.png");

	public EditorScreen(final Jumpy game, ScreenViewport viewport) {
		this.game = game;
		this.viewport = viewport;
		tilemap = new Texture("Castle2.png");
		tiles = TextureRegion.split(tilemap, TILE_SIZE, TILE_SIZE);
		;
		placedTiles = new TextureRegion[tiles.length][tiles[0].length];

	}

	@Override
	public void render(float delta) {
		config();

		Gdx.graphics.setWindowedMode(1440, 800);
		ScreenUtils.clear(Color.TEAL);
		viewport.apply();
		game.batch.setProjectionMatrix(viewport.getCamera().combined);
		game.batch.begin();

		drawTiles();
		handleSelectTile();

		game.batch.end();

	}

	private void handleSelectTile() {
		float HEIGHT = game.viewport.getWorldHeight() - TILE_SIZE;
		float WIDTH = game.viewport.getWorldWidth() / 2;
		int mouseX = Gdx.input.getX();
		int mouseY = Gdx.input.getY();
		// Select tile
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
			// compute column/row
			int col = mouseX / TILE_SIZE;
			int row = mouseY / TILE_SIZE;
			if (row >= 0 && row < tiles.length && col >= 0 && col < tiles[0].length) {
				selectedTile = tiles[row][col];
			}

			System.out.println(Math.floor(WIDTH / TILE_SIZE));
			// Checks if we're selecting empty tileset
			double firstEmptyTileCol = Math.floor(WIDTH / TILE_SIZE);
			if (row >= 0 && row < tiles.length && col >= firstEmptyTileCol
					&& col < tiles[0].length + firstEmptyTileCol) {
				if (selectedTile != null) {
					col -= firstEmptyTileCol;
					placedTiles[row][col] = selectedTile;
				}
			}
		}
		// Add it to the mouse
		if (selectedTile != null) {
			game.batch.draw(selectedTile, mouseX, game.viewport.getWorldHeight() - mouseY, TILE_SIZE, TILE_SIZE);
		}
	}

	private String placedTileToJsonData() {
		int[][] saveableTiles = new int[placedTiles.length][placedTiles[0].length];
		saveableTiles[0][0] = 123;
		return saveableTiles.toString();
	}

	private void drawTiles() {
		float HEIGHT = game.viewport.getWorldHeight() - TILE_SIZE;
		float WIDTH = game.viewport.getWorldWidth() / 2;
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

			String jsonData = placedTileToJsonData();
			fh.writeString(json.prettyPrint(jsonData), false);
//			System.out.println("Saved at: " + Gdx.files.local("map.json").file().getAbsolutePath());
		}
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
		// TODO Auto-generated method stub

	}

}
