package race.game.bean;

public class MyCar {
	// 自機用座標
	private int myCarCoodX = 0;
	private int myCarCoodY = 0;

	// 自機速度
	private int myCarSpeed = 0;

	// 移動方向
	private int movingWay = 1;

	// コンストラクタ
	public MyCar(int myCarCoodX, int myCarCoodY) {
		this.myCarCoodX = myCarCoodX;
		this.myCarCoodY = myCarCoodY;
	}

	public int getMyCarCoodX() {
		return myCarCoodX;
	}

	public void setMyCarCoodX(int myCarCoodX) {
		this.myCarCoodX = myCarCoodX;
	}

	public int getMyCarCoodY() {
		return myCarCoodY;
	}

	public void setMyCarCoodY(int myCarCoodY) {
		this.myCarCoodY = myCarCoodY;
	}

	public int getMyCarSpeed() {
		return myCarSpeed;
	}

	public void setMyCarSpeed(int myCarSpeed) {
		this.myCarSpeed = myCarSpeed;
	}

	public int getMovingWay() {
		return movingWay;
	}

	public void setMovingWay(int movingWay) {
		this.movingWay = movingWay;
	}

	// スピードを変更するメソッド
	public void accellMyCar() {
		if (myCarSpeed == 0) {
			setMyCarSpeed(4);
		}
	}

	// 座標を移動させるメソッド
	public void moveMyCar() {
		if (movingWay == Data.MOVING_UP) {
			myCarCoodY -= myCarSpeed;
		} else if (movingWay == Data.MOVING_DOWN) {
			myCarCoodY += myCarSpeed;
		} else if (movingWay == Data.MOVING_RIGHT) {
			myCarCoodX += myCarSpeed;
		} else if (movingWay == Data.MOVING_LEFT) {
			myCarCoodX -= myCarSpeed;
		}
	}
}
