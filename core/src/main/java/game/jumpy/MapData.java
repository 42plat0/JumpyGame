package game.jumpy;

import java.util.Map;

/**
 * Class for saving/loading tiled map
 *
 * @author gytis
 */
public class MapData {
	private Tilemap map;
	private Tileset tileset;

	public final static String START_OBJ = "start";
	public final static String END_OBJ = "end";
	public final static String SCORE_POINT_OBJ = "scorePoint";

	public Tilemap getMap() {
		return map;
	}

	public void setMap(Tilemap map) {
		this.map = map;
	}

	public Tileset getTileset() {
		return tileset;
	}

	public void setTileset(Tileset tileset) {
		this.tileset = tileset;
	}

	/**
	 * Class for created tilemap by user
	 *
	 * @author gytis
	 */
	public static class Tilemap {
		private int width;
		private int height;
		private int[][] tiles;
		private Map<String, Tile> objects;

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

		public int[][] getTiles() {
			return tiles;
		}

		public void setTiles(int[][] tiles) {
			this.tiles = tiles;
		}

		public Map<String, Tile> getObjects() {
			return objects;
		}

		public void setObjectsList(Map<String, Tile> objects) {
			this.objects = objects;
		}
	}

	/**
	 * Class for tile from tileset info
	 *
	 * @author gytis
	 */
	public static class Tile {
		private int row;
		private int col;

		public int getRow() {
			return row;
		}

		public void setRow(int row) {
			this.row = row;
		}

		public int getCol() {
			return col;
		}

		public void setCol(int col) {
			this.col = col;
		}
	}

	/**
	 * Class for tileset used by user
	 *
	 * @author gytis
	 */
	public static class Tileset {
		private String image;
		private int tileWidth;
		private int tileHeight;

		private Map<Integer, Tile> tilesMap;

		public String getImage() {
			return image;
		}

		public void setImage(String image) {
			this.image = image;
		}

		public int getTileWidth() {
			return tileWidth;
		}

		public void setTileWidth(int tileWidth) {
			this.tileWidth = tileWidth;
		}

		public int getTileHeight() {
			return tileHeight;
		}

		public void setTileHeight(int tileHeight) {
			this.tileHeight = tileHeight;
		}

		public Map<Integer, Tile> getTiles() {
			return tilesMap;
		}

		public void setTiles(Map<Integer, Tile> tilesMap) {
			this.tilesMap = tilesMap;
		}

	}
}
