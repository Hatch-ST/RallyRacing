package race.game.bean;

public class PursueEnemyCar extends EnemyCar {
	public PursueEnemyCar(int enemyCarCoodX, int enemyCarCoodY) {
		super(enemyCarCoodX, enemyCarCoodY);
		super.setEnemyNumber(Data.PURSUE_ENEMY);
	}

	// スピードを変更するメソッド
	@Override
	public void accellEnemyCar() {
		if (this.getEnemyCarSpeed() == 0) {
			setEnemyCarSpeed(2);
		}
	}
}
