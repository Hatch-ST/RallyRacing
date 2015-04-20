package race.game.bean;

import java.util.ArrayList;

public class MapChip {
	// マップ用座標
	private int mapCoodX = 0;
	private int mapCoodY = 0;

	// このマップが通れるかどうか
	private boolean movable;

	// このマップから移動可能な方向
	private boolean[] movableWay = new boolean[4];

	// このマップ上にいるキャラクタ
	private int charcter = 0;

	// 移動可能方向の番号リスト
	ArrayList<Integer> movableWayNumList = new ArrayList<Integer>();

	// コンストラクタ
	public MapChip(int mapCoodX, int mapCoodY) {
		// 座標の設定
		this.mapCoodX = mapCoodX;
		this.mapCoodY = mapCoodY;
	}

	public int getMapCoodX() {
		return mapCoodX;
	}

	public void setMapCoodX(int mapCoodX) {
		this.mapCoodX = mapCoodX;
	}

	public int getMapCoodY() {
		return mapCoodY;
	}

	public void setMapCoodY(int mapCoodY) {
		this.mapCoodY = mapCoodY;
	}

	public boolean isMovable() {
		return movable;
	}

	public void setMovable(boolean movable) {
		this.movable = movable;
	}

	public boolean[] getMovableWay() {
		return movableWay;
	}

	public void setMovableWay(boolean[] movableWay) {
		this.movableWay = movableWay;
	}

	public void setCharcter(int charcter) {
		this.charcter = charcter;
	}

	public int getCharcter() {
		return charcter;
	}

	public ArrayList<Integer> getMovableWayNumList() {
		return new ArrayList<Integer>(movableWayNumList);
	}

}
