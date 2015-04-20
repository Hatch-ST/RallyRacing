package race.game.bean;

public class RandomEnemyCar extends EnemyCar {
	public RandomEnemyCar(int enemyCarCoodX, int enemyCarCoodY) {
		super(enemyCarCoodX, enemyCarCoodY);
		super.setEnemyNumber(Data.RANDOM_ENEMY);
	}
}
