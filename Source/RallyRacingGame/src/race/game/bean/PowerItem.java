package race.game.bean;

public class PowerItem extends Item {
	public PowerItem(int itemCoodX, int itemCoodY) {
		super(itemCoodX, itemCoodY);
		// アイテム番号の設定
		super.setItemNumber(Data.POWER_ITEM);
	}
}
