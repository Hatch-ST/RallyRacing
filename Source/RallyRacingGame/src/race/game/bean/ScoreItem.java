package race.game.bean;

public class ScoreItem extends Item {
	public ScoreItem(int itemCoodX, int itemCoodY) {
		super(itemCoodX, itemCoodY);
		// アイテム番号の設定
		super.setItemNumber(Data.SCORE_ITEM);
	}
}
