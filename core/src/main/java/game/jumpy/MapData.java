package game.jumpy;

import java.util.List;

public class MapData {
	private int[][] tiles;
	private List<TileData> tileData;

	public void setTiles(int[][] tiles) {
		this.tiles = tiles;
	}

	public int[][] getTiles() {
		return tiles;
	}

	public class TileData {
		int idx;
		int row;
		int col;
		int tileRow;
		int tileCol;
	}
}
