package race.game.bean;

public class Item {
	// アイテム用座標
	private int itemCoodX = 0;
	private int itemCoodY = 0;

	// アイテム番号
	private int itemNumber;

	//コンストラクタ
	public Item(int itemCoodX, int itemCoodY) {
		super();
		this.itemCoodX = itemCoodX;
		this.itemCoodY = itemCoodY;
	}

	public int getItemCoodX() {
		return itemCoodX;
	}

	public void setItemCoodX(int itemCoodX) {
		this.itemCoodX = itemCoodX;
	}

	public int getItemCoodY() {
		return itemCoodY;
	}

	public void setItemCoodY(int itemCoodY) {
		this.itemCoodY = itemCoodY;
	}

	public int getItemNumber() {
		return itemNumber;
	}

	public void setItemNumber(int itemNumber) {
		this.itemNumber = itemNumber;
	}

}
