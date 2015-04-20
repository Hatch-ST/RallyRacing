package race.game.bean;

public class MapTypeObj extends MapChip {

	public MapTypeObj(int mapCoodX, int mapCoodY) {
		super(mapCoodX, mapCoodY);
		// 移動可能の設定
		setMovable(false);
		// 移動可能方向の設定
		boolean[] movableWay = { false, false, false, false };
		setMovableWay(movableWay);
	}

}
