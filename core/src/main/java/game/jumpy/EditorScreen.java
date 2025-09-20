package game.jumpy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;

public class EditorScreen implements Screen {
	private Jumpy game;
	private Texture tilemap;

	private int TILE_SIZE = 32;
	TextureRegion[][] tiles;
	TextureRegion selectedTile;

	public EditorScreen(final Jumpy game) {
		this.game = game;
		tilemap = new Texture("Castle2.png");
		tiles = TextureRegion.split(tilemap, TILE_SIZE, TILE_SIZE);

	}

	@Override
	public void render(float delta) {
		config();

		Gdx.graphics.setWindowedMode(1440, 800);
		ScreenUtils.clear(Color.TEAL);
		game.viewport.apply();
		game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
		game.batch.begin();

		drawTileset();
		handleSelectTile();
		handleDrawingTile();

		game.batch.end();

	}

	private void handleDrawingTile() {

	}

	private void handleSelectTile() {
		int mouseX = Gdx.input.getX();
		int mouseY = Gdx.input.getY();
		// Select tile
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
			// compute column/row
			int col = mouseX / TILE_SIZE;
			int row = mouseY / TILE_SIZE;
			if (row >= 0 && row < tiles.length && col >= 0 && col < tiles[0].length) {
				selectedTile = tiles[row][col];
				System.out.println("Selected tile at row " + row + " col " + col);
			}
		}
		// Add it to the mouse
		if (selectedTile != null) {
			game.batch.draw(selectedTile, mouseX, game.viewport.getWorldHeight() - mouseY, TILE_SIZE, TILE_SIZE);
		}

	}

	private void drawTileset() {

		float HEIGHT = game.viewport.getWorldHeight() - TILE_SIZE;
		for (int row = 0; row < tiles.length; row++) {
			for (int col = 0; col < tiles[row].length; col++) {
				float x = col * TILE_SIZE;
				float y = HEIGHT - row * TILE_SIZE; // start from the top of the window since bathc sprite is bottom
													// left at 0, 0

				game.batch.draw(tiles[row][col], x, y, TILE_SIZE, TILE_SIZE);
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
