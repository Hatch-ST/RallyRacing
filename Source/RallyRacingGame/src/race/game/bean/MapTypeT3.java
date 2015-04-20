package race.game.bean;

public class MapTypeT3 extends MapChip {
	public MapTypeT3(int mapCoodX, int mapCoodY) {
		super(mapCoodX, mapCoodY);
		// 移動可能の設定
		setMovable(true);
		// 移動可能方向の設定
		boolean[] movableWay = { true, true, true, false };
		setMovableWay(movableWay);

		// 移動可能方向の番号リストの設定
		for (int i = 0; i < movableWay.length; i++) {
			// 移動可能であればその方向の番号を追加
			if (movableWay[i]) {
				movableWayNumList.add(i);
			}
		}
	}

}
