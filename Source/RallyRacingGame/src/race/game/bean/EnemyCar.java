package race.game.bean;

public class EnemyCar {
	// 自機用座標
	private int enemyCarCoodX = 0;
	private int enemyCarCoodY = 0;

	// 自機速度
	private int enemyCarSpeed = 0;

	// 移動方向
	private int movingWay = 1;

	// 敵番号
	private int enemyNumber;

	public EnemyCar(int enemyCarCoodX, int enemyCarCoodY) {
		this.enemyCarCoodX = enemyCarCoodX;
		this.enemyCarCoodY = enemyCarCoodY;
	}

	public int getEnemyCarCoodX() {
		return enemyCarCoodX;
	}

	public void setEnemyCarCoodX(int enemyCarCoodX) {
		this.enemyCarCoodX = enemyCarCoodX;
	}

	public int getEnemyCarCoodY() {
		return enemyCarCoodY;
	}

	public void setEnemyCarCoodY(int enemyCarCoodY) {
		this.enemyCarCoodY = enemyCarCoodY;
	}

	public int getEnemyCarSpeed() {
		return enemyCarSpeed;
	}

	public void setEnemyCarSpeed(int enemyCarSpeed) {
		this.enemyCarSpeed = enemyCarSpeed;
	}

	public int getMovingWay() {
		return movingWay;
	}

	public void setMovingWay(int movingWay) {
		this.movingWay = movingWay;
	}

	public int getEnemyNumber() {
		return enemyNumber;
	}

	public void setEnemyNumber(int enemyNumber) {
		this.enemyNumber = enemyNumber;
	}

	// スピードを変更するメソッド
	public void accellEnemyCar() {
		if (enemyCarSpeed == 0) {
			setEnemyCarSpeed(4);
		}
	}

	// 座標を移動させるメソッド
	public void moveEnemyCar() {
		if (movingWay == Data.MOVING_UP) {
			enemyCarCoodY -= enemyCarSpeed;
		} else if (movingWay == Data.MOVING_DOWN) {
			enemyCarCoodY += enemyCarSpeed;
		} else if (movingWay == Data.MOVING_RIGHT) {
			enemyCarCoodX += enemyCarSpeed;
		} else if (movingWay == Data.MOVING_LEFT) {
			enemyCarCoodX -= enemyCarSpeed;
		}
	}
}
