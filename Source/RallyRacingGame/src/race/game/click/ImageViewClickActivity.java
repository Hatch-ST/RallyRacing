package race.game.click;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import race.game.bean.Data;
import race.game.bean.EnemyCar;
import race.game.bean.MapChip;
import race.game.bean.MapTypeI02;
import race.game.bean.MapTypeI13;
import race.game.bean.MapTypeL01;
import race.game.bean.MapTypeL03;
import race.game.bean.MapTypeL12;
import race.game.bean.MapTypeL23;
import race.game.bean.MapTypeObj;
import race.game.bean.MapTypePlus;
import race.game.bean.MapTypeT0;
import race.game.bean.MapTypeT1;
import race.game.bean.MapTypeT2;
import race.game.bean.MapTypeT3;
import race.game.bean.MyCar;
import race.game.bean.PowerItem;
import race.game.bean.PursueEnemyCar;
import race.game.bean.RandomEnemyCar;
import race.game.bean.ScoreItem;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageViewClickActivity extends Activity implements OnTouchListener {
	// サーフェイスビュー
	SurfaceView mainSurfaceView;

	// ボタン用イメージビュー
	ImageView upImageView, leftImageView, rightImageView, downImageView;

	// スコア用テキストビュー
	TextView scoreNumText;
	// スコアカウント用変数
	int score = 0;
	// 時間用テキストビュー
	TextView timeNumText;
	// 時間カウント用変数
	int time = 0;
	// 時間加算用変数
	int timeCounter = 0;
	// パワーアイテム取得時表示用イメージビュー
	ImageView powerItemImageView;

	// ゲーム用サーフェイスビュー
	private MainSurfaceView mainGameSurfaceView;

	// タイトル画面中のフラグ
	boolean titleFlag = true;
	// ゲームプレイ中のフラグ
	boolean playingFlag = false;
	// ゲームオーバー中のフラグ
	boolean gameoverFlag = false;

	// ハンドラ
	private Handler handler;
	// ゲームメイン処理用スレッド
	private Runnable mainGameThread;
	// ゲーム前用スレッド
	private Runnable prepareGameThread;
	// ゲーム開始カウンター
	private int gameStartCounter = 3;
	// ゲームオーバー用スレッド
	private Runnable gameoverThread;

	// 描画用キャンバス
	public Canvas canvas;

	// 描画用ペイント
	Paint p = new Paint();

	// ボタン押下判定
	public static boolean upButtonPressed = false;
	public static boolean leftButtonPressed = false;
	public static boolean rightButtonPressed = false;
	public static boolean downButtonPressed = false;

	// 画像読み込み用
	Resources r;

	// マップリスト
	final static int MAP_CHIP_SIZE_X = 12;
	final static int MAP_CHIP_SIZE_Y = 14;
	MapChip[][] mapList = new MapChip[MAP_CHIP_SIZE_X][MAP_CHIP_SIZE_Y];

	// チップサイズ
	final static int CHIP_SIZE = 32;

	// MyCar
	MyCar objMyCar;
	// 自機画像
	Bitmap myCarBmpUp;
	Bitmap myCarBmpDown;
	Bitmap myCarBmpRight;
	Bitmap myCarBmpLeft;
	// マップチップ画像
	Bitmap mapObjBmp;
	// アイテム画像
	Bitmap scoreItemBmp;
	Bitmap powerItemBmp;
	// 敵1機画像
	Bitmap enemyCar1BmpUp;
	Bitmap enemyCar1BmpDown;
	Bitmap enemyCar1BmpRight;
	Bitmap enemyCar1BmpLeft;
	// 敵2機画像
	Bitmap enemyCar2BmpUp;
	Bitmap enemyCar2BmpDown;
	Bitmap enemyCar2BmpRight;
	Bitmap enemyCar2BmpLeft;

	// スコアアイテム出現位置リスト
	ArrayList<Integer[]> scoreItemAppearCood;
	// パワーアイテム出現位置リスト
	ArrayList<Integer[]> powerItemAppearCood;
	// 自機＆敵機出現位置リスト
	ArrayList<Integer[]> carAppearCood;

	// スコアアイテム管理用セット（同じ座標に配置されないようにするため)
	HashSet<Integer[]> scoreItemRandomNumbers;
	// スコアアイテムを表示する数
	final int MAX_SCORE_ITEM = 3;
	// スコアアイテムカウント用変数
	int scoreItemCounter;
	// パワーアイテムが出現するまでの個数
	int powerItemCounter = 3;
	// パワーアイテムが出現しているフラグ
	boolean powerItemAppeared = false;
	// パワーアイテムを手に入れてるフラグ
	boolean powerItemGot = false;

	// スコアアイテムリスト
	ArrayList<ScoreItem> scoreItemList;
	// パワーアイテムリスト
	ArrayList<PowerItem> powerItemList;

	// 追跡敵機リスト
	ArrayList<PursueEnemyCar> pursueEnemyList;
	// 追跡敵機出現用フラグ
	boolean pursueEnemyAppeared = false;
	// ランダム敵機リスト
	ArrayList<RandomEnemyCar> randomEnemyList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// タイトル非表示
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// レイアウトからビュー取得
		mainSurfaceView = (SurfaceView) findViewById(R.id.mainSurfaceView);

		upImageView = (ImageView) findViewById(R.id.upImageID);
		leftImageView = (ImageView) findViewById(R.id.leftImageID);
		rightImageView = (ImageView) findViewById(R.id.rightImageID);
		downImageView = (ImageView) findViewById(R.id.downImageID);

		scoreNumText = (TextView) findViewById(R.id.scoreNumText);
		timeNumText = (TextView) findViewById(R.id.timeNumText);
		powerItemImageView = (ImageView) findViewById(R.id.powerItemImageID);

		// onTouchListenerをセット
		upImageView.setOnTouchListener(this);
		leftImageView.setOnTouchListener(this);
		rightImageView.setOnTouchListener(this);
		downImageView.setOnTouchListener(this);

		// サーフェイスビューの生成
		mainGameSurfaceView = new MainSurfaceView(this, mainSurfaceView);

		// 画像回転用
		Matrix rightMatrix = new Matrix();
		Matrix downMatrix = new Matrix();
		Matrix leftMatrix = new Matrix();

		// 回転角度をセット
		rightMatrix.setRotate(90.0f);
		downMatrix.setRotate(180.0f);
		leftMatrix.setRotate(270.0f);

		// --------------ここから画像の読み込み--------------//
		// 自機画像の読み込み
		Resources r = getResources();
		myCarBmpUp = BitmapFactory.decodeResource(r, R.drawable.mycar);

		// 回転させた画像をBitmapとして保存
		myCarBmpRight = Bitmap.createBitmap(myCarBmpUp, 0, 0,
				myCarBmpUp.getWidth(), myCarBmpUp.getHeight(), rightMatrix,
				true);
		myCarBmpDown = Bitmap
				.createBitmap(myCarBmpUp, 0, 0, myCarBmpUp.getWidth(),
						myCarBmpUp.getHeight(), downMatrix, true);
		myCarBmpLeft = Bitmap
				.createBitmap(myCarBmpUp, 0, 0, myCarBmpUp.getWidth(),
						myCarBmpUp.getHeight(), leftMatrix, true);

		// マップチップ画像の読み込み
		mapObjBmp = BitmapFactory.decodeResource(r, R.drawable.rock1);
		// アイテム画像の読み込み
		scoreItemBmp = BitmapFactory.decodeResource(r, R.drawable.item1);
		powerItemBmp = BitmapFactory.decodeResource(r, R.drawable.item2);

		// 敵機画像1の読み込み
		enemyCar1BmpUp = BitmapFactory.decodeResource(r, R.drawable.enemy_car1);

		// 回転させた画像をBitmapとして保存
		enemyCar1BmpRight = Bitmap.createBitmap(enemyCar1BmpUp, 0, 0,
				enemyCar1BmpUp.getWidth(), enemyCar1BmpUp.getHeight(),
				rightMatrix, true);
		enemyCar1BmpDown = Bitmap.createBitmap(enemyCar1BmpUp, 0, 0,
				enemyCar1BmpUp.getWidth(), enemyCar1BmpUp.getHeight(),
				downMatrix, true);
		enemyCar1BmpLeft = Bitmap.createBitmap(enemyCar1BmpUp, 0, 0,
				enemyCar1BmpUp.getWidth(), enemyCar1BmpUp.getHeight(),
				leftMatrix, true);

		// 敵機画像2の読み込み
		enemyCar2BmpUp = BitmapFactory.decodeResource(r, R.drawable.enemy_car2);

		// 回転させた画像をBitmapとして保存
		enemyCar2BmpRight = Bitmap.createBitmap(enemyCar2BmpUp, 0, 0,
				enemyCar2BmpUp.getWidth(), enemyCar2BmpUp.getHeight(),
				rightMatrix, true);
		enemyCar2BmpDown = Bitmap.createBitmap(enemyCar2BmpUp, 0, 0,
				enemyCar2BmpUp.getWidth(), enemyCar2BmpUp.getHeight(),
				downMatrix, true);
		enemyCar2BmpLeft = Bitmap.createBitmap(enemyCar2BmpUp, 0, 0,
				enemyCar2BmpUp.getWidth(), enemyCar2BmpUp.getHeight(),
				leftMatrix, true);
		// --------------画像の読み込み終了--------------//

		// ハンドラ生成
		handler = new Handler();
		mainGameThread = new Runnable() {
			// ゲームメイン処理スレッド
			public void run() {
				// ダブルバッファリング開始
				canvas = mainSurfaceView.getHolder().lockCanvas();
				// 背景を塗る
				canvas.drawColor(Color.argb(255, 181, 166, 66));
				// マップチップの表示
				for (int y = 0; y < MAP_CHIP_SIZE_Y; y++) {
					for (int x = 0; x < MAP_CHIP_SIZE_X; x++) {
						// 移動不可の場合
						if (!mapList[x][y].isMovable()) {
							// オブジェクトを表示
							canvas.drawBitmap(mapObjBmp,
									mapList[x][y].getMapCoodX(),
									mapList[x][y].getMapCoodY(), p);
						}
					}
				}

				// スコアアイテムの表示
				displayScoreItem();
				// パワーアイテムの表示
				displayPowerItem();

				// ランダム敵機表示
				for (int i = 0; i < randomEnemyList.size(); i++) {
					// 移動処理
					moveEnemyCar(randomEnemyList.get(i));
					// 表示処理
					displayEnemyCar(randomEnemyList.get(i));
				}

				// 追従敵機表示
				for (int i = 0; i < pursueEnemyList.size(); i++) {
					// 移動処理
					moveEnemyCar(pursueEnemyList.get(i));
					// 表示処理
					displayEnemyCar(pursueEnemyList.get(i));
				}

				// 自機移動
				moveMyCar();
				// 自機表示
				displayMyCar();

				// 当たり判定チェック
				checkHitting();

				// スコアの表示
				setScore();

				// 時間の表示
				setTime();

				// サーフェイスロック解除
				mainSurfaceView.getHolder().unlockCanvasAndPost(canvas);

				// プレイフラグチェック
				// プレイ中の場合
				if (playingFlag) {
					// ゲームスレッド繰り返し
					handler.postDelayed(mainGameThread, 50);
				}
				// ゲームオーバーの場合
				else {
					// ゲームオーバー処理
					gameoverFlag = true;

					// ゲームオーバースレッドの呼び出し
					handler.post(gameoverThread);
				}
			}
		};

		prepareGameThread = new Runnable() {
			// ゲーム前処理スレッド
			public void run() {
				// ダブルバッファリング開始
				canvas = mainSurfaceView.getHolder().lockCanvas();
				// 背景を塗る
				canvas.drawColor(Color.argb(255, 181, 166, 66));
				// マップチップの表示
				for (int y = 0; y < MAP_CHIP_SIZE_Y; y++) {
					for (int x = 0; x < MAP_CHIP_SIZE_X; x++) {
						// 移動不可の場合
						if (!mapList[x][y].isMovable()) {
							// オブジェクトを表示
							canvas.drawBitmap(mapObjBmp,
									mapList[x][y].getMapCoodX(),
									mapList[x][y].getMapCoodY(), p);
						}
					}
				}
				// スコアアイテムの表示
				displayScoreItem();
				// ランダム敵機表示
				for (int i = 0; i < randomEnemyList.size(); i++) {
					displayEnemyCar(randomEnemyList.get(i));
				}
				// 自機表示
				displayMyCar();

				// ペイントの設定
				p.setAntiAlias(true);
				p.setTextSize(150.0f);
				p.setColor(Color.BLACK);

				// ゲームスタートカウンターの分岐
				switch (gameStartCounter) {
				case 3:
					// ゲーム開始のカウントの表示
					canvas.drawText("3", 155, 275, p);
					break;
				case 2:
					// ゲーム開始のカウントの表示
					canvas.drawText("2", 155, 275, p);
					break;
				case 1:
					// ゲーム開始のカウントの表示
					canvas.drawText("1", 155, 275, p);
					break;
				case 0:
					// ペイントの設定
					p.setColor(Color.RED);
					// ゲーム開始のカウントの表示
					canvas.drawText("GO", 90, 280, p);
					break;
				}
				// ゲームスタートカウンターをー１する
				gameStartCounter--;

				// サーフェイスロック解除
				mainSurfaceView.getHolder().unlockCanvasAndPost(canvas);
				// ゲームスタートカウンターが-1の場合
				if (gameStartCounter == -1) {
					// ゲームメインスレッド開始
					handler.postDelayed(mainGameThread, 750);
				}
				// その他の場合
				else {
					// １秒ごとに繰り返す
					handler.postDelayed(prepareGameThread, 750);
				}
			}
		};

		// スレッドの生成
		gameoverThread = new Runnable() {
			// ゲームオーバースレッド
			public void run() {
				// ダブルバッファリング開始
				canvas = mainSurfaceView.getHolder().lockCanvas();
				// 背景を塗る
				canvas.drawColor(Color.argb(255, 181, 166, 66));
				// マップチップの表示
				for (int y = 0; y < MAP_CHIP_SIZE_Y; y++) {
					for (int x = 0; x < MAP_CHIP_SIZE_X; x++) {
						// 移動不可の場合
						if (!mapList[x][y].isMovable()) {
							// オブジェクトを表示
							canvas.drawBitmap(mapObjBmp,
									mapList[x][y].getMapCoodX(),
									mapList[x][y].getMapCoodY(), p);
						}
					}
				}

				// スコアアイテムの表示
				displayScoreItem();
				// パワーアイテムの表示
				displayPowerItem();
				// ランダム敵機表示
				for (int i = 0; i < randomEnemyList.size(); i++) {
					// 表示処理
					displayEnemyCar(randomEnemyList.get(i));
				}
				// 追従敵機表示
				for (int i = 0; i < pursueEnemyList.size(); i++) {
					// 表示処理
					displayEnemyCar(pursueEnemyList.get(i));
				}
				// 自機表示
				displayMyCar();

				// 最終スコアをテキストビューにセット
				scoreNumText.setText(String.format("%1$010d", score));

				// 最終時間テキストビューにセット
				timeNumText.setText(String.format("%1$05d", time));

				// ペイントの設定
				p.setAntiAlias(true);
				p.setTextSize(50.0f);
				p.setColor(Color.RED);
				// ゲームオーバーの表示
				canvas.drawText("Game Over", 55, 100, p);

				// ペイントの設定
				p.setTextSize(40.0f);
				p.setColor(Color.YELLOW);
				// スコアの表示
				canvas.drawText("Score", 30, 200, p);
				// ペイントの設定
				p.setTextSize(35.0f);
				p.setColor(Color.BLUE);
				// スコアの点数表示
				canvas.drawText(String.format("%1$010d", score), 150, 200, p);

				// ペイントの設定
				p.setTextSize(25.0f);
				p.setColor(Color.BLACK);
				// 説明文の表示
				canvas.drawText("Aボタンを押してください", 45, 300, p);
				// サーフェイスロック解除
				mainSurfaceView.getHolder().unlockCanvasAndPost(canvas);
			}
		};
	}

	// onPause
	@Override
	public void onPause() {
		super.onPause();
		// mainGameThreadの停止
		handler.removeCallbacks(mainGameThread);
		// prepareGameThreadの停止
		handler.removeCallbacks(prepareGameThread);
		// gameoverThreadの停止
		handler.removeCallbacks(gameoverThread);
		// スコア等のリセット
		resetNumbersEtc();
		// フラグのリセット
		resetFlags();
	}

	// ゲームの初期化をするメソッド
	public void firstSetting() {
		// 出現位置リストの初期化
		scoreItemAppearCood = new ArrayList<Integer[]>();
		powerItemAppearCood = new ArrayList<Integer[]>();
		carAppearCood = new ArrayList<Integer[]>();
		// MapChipの生成
		setMapChip();

		// ランダム変数の発生
		int randomNum = (int) (Math.random() * carAppearCood.size());
		// 自機の初期位置の設定
		Integer[] myCarCood = carAppearCood.get(randomNum);
		// MyCarのインスタンス
		objMyCar = new MyCar(myCarCood[0].intValue(), myCarCood[1].intValue());
		// 初期位置を決定したときに自動的に向きを変更する
		changeMyCarMovingWay();

		// リストの初期化
		scoreItemRandomNumbers = new HashSet<Integer[]>();
		scoreItemList = new ArrayList<ScoreItem>();
		powerItemList = new ArrayList<PowerItem>();
		pursueEnemyList = new ArrayList<PursueEnemyCar>();
		randomEnemyList = new ArrayList<RandomEnemyCar>();

		for (int i = 0; i < 3; i++) {
			// 自機の初期位置とは別の場所の変数を用意
			randomNum++;
			// 3を超えている場合
			if (randomNum > 3) {
				// -4をして3以下の数字にする
				randomNum -= 4;
			}
			// 敵機の初期位置の設定
			Integer[] enemyCarCood = carAppearCood.get(randomNum);

			// 敵機リストに追加
			randomEnemyList.add(new RandomEnemyCar(enemyCarCood[0].intValue(),
					enemyCarCood[1].intValue()));
			// 初期位置を決定したときに自動的に向きを変更する
			changeEnemyCarMovingWay(randomEnemyList.get(i));
		}

		// スコアアイテムの生成
		setScoreItem(MAX_SCORE_ITEM);
	}

	// 自機を移動するメソッド
	public void moveMyCar() {
		// 自機のスピード変更
		objMyCar.accellMyCar();
		// 自機の移動方向変更処理
		changeMyCarMovingWay();
		// 自機座標移動処理
		objMyCar.moveMyCar();

	}

	// 自機を表示するメソッド
	public void displayMyCar() {
		// 自機の移動向き取得
		int movingWay = objMyCar.getMovingWay();
		switch (movingWay) {
		// 上向きの場合
		case Data.MOVING_UP:
			// 自機表示
			canvas.drawBitmap(myCarBmpUp, objMyCar.getMyCarCoodX(),
					objMyCar.getMyCarCoodY(), p);
			break;
		// 右向きの場合
		case Data.MOVING_RIGHT:
			// 自機表示
			canvas.drawBitmap(myCarBmpRight, objMyCar.getMyCarCoodX(),
					objMyCar.getMyCarCoodY(), p);
			break;
		// 下向きの場合
		case Data.MOVING_DOWN:
			// 自機表示
			canvas.drawBitmap(myCarBmpDown, objMyCar.getMyCarCoodX(),
					objMyCar.getMyCarCoodY(), p);
			break;
		// 左向きの場合
		case Data.MOVING_LEFT:
			// 自機表示
			canvas.drawBitmap(myCarBmpLeft, objMyCar.getMyCarCoodX(),
					objMyCar.getMyCarCoodY(), p);
			break;
		}
	}

	// 自機の座標判定メソッド
	public int[] checkMyCarCood() {
		// x方向のfor文
		for (int x = 0; x < MAP_CHIP_SIZE_X; x++) {
			// 自機座標xとマップチップ座標xが一致する場合
			if (objMyCar.getMyCarCoodX() == (x * CHIP_SIZE)) {
				// y方向のfor文
				for (int y = 0; y < MAP_CHIP_SIZE_Y; y++) {
					// 自機座標yとマップチップ座標yが一致する場合
					if (objMyCar.getMyCarCoodY() == (y * CHIP_SIZE)) {
						// 座標配列
						int[] mapCood = { x, y };
						// スコアアイテムがある場合
						if (mapList[x][y].getCharcter() == Data.SCORE_ITEM) {
							// 座標にチップサイズをかけた座標配列
							int[] itemCood = { x * CHIP_SIZE, y * CHIP_SIZE };
							// 取ったアイテムを消し新しいアイテムを追加する
							removeAndSetNewScoreItem(itemCood);
						} else if (mapList[x][y].getCharcter() == Data.POWER_ITEM) {
							// 座標にチップサイズをかけた座標配列
							int[] itemCood = { x * CHIP_SIZE, y * CHIP_SIZE };
							// 取ったパワーアイテムを消す
							removePowerItem(itemCood);
						}

						// 座標を返す
						return mapCood;
					}
				}
			}
		}
		// 一致しない場合はnullを返す
		return null;
	}

	// 自機の移動方向を変更するメソッド
	public void changeMyCarMovingWay() {
		// 座標チェック
		int[] mapCood = checkMyCarCood();
		// 座標が一致しない場合
		if (mapCood == null) {
			// 変更不可のためメソッドを抜ける
			return;
		}
		// 移動可能方向の取得
		boolean[] movableWay = mapList[mapCood[0]][mapCood[1]].getMovableWay();
		// 自機の移動方向取得
		int myCarMovingWay = objMyCar.getMovingWay();
		// ボタンを押している場合
		// 上ボタンが押されている場合
		if (upButtonPressed) {
			// 移動可能か判定
			if (movableWay[Data.MOVING_UP]) {
				// 反対向きでないかの判定
				if (myCarMovingWay != Data.MOVING_DOWN) {
					// 可能な場合上向きに変更
					objMyCar.setMovingWay(Data.MOVING_UP);
					// 変更完了しメソッドを抜ける
					return;
				}
			}
		}
		// 右ボタンが押されている場合
		else if (rightButtonPressed) {
			// 移動可能か判定
			if (movableWay[Data.MOVING_RIGHT]) {
				// 反対向きでないかの判定
				if (myCarMovingWay != Data.MOVING_LEFT) {
					// 可能な場合右向きに変更
					objMyCar.setMovingWay(Data.MOVING_RIGHT);
					// 変更完了しメソッドを抜ける
					return;
				}
			}
		}
		// 下ボタンが押されている場合
		else if (downButtonPressed) {
			// 移動可能か判定
			if (movableWay[Data.MOVING_DOWN]) {
				// 反対向きでないかの判定
				if (myCarMovingWay != Data.MOVING_UP) {
					// 可能な場合右向きに変更
					objMyCar.setMovingWay(Data.MOVING_DOWN);
					// 変更完了しメソッドを抜ける
					return;
				}
			}
		}
		// 左ボタンが押されている場合
		else if (leftButtonPressed) {
			// 移動可能か判定
			if (movableWay[Data.MOVING_LEFT]) {
				// 反対向きでないかの判定
				if (myCarMovingWay != Data.MOVING_RIGHT) {
					// 可能な場合右向きに変更
					objMyCar.setMovingWay(Data.MOVING_LEFT);
					// 変更完了しメソッドを抜ける
					return;
				}
			}
		}

		// 自動向き変更
		// 現在の移動向きが移動可能か判定
		if (movableWay[myCarMovingWay]) {
			// 移動可能であればそのままの向きでメソッドを抜ける
			return;
		}
		// 移動不可の場合
		else {
			// 移動可能方向の番号リストの取得
			ArrayList<Integer> movableWayNumList = mapList[mapCood[0]][mapCood[1]]
					.getMovableWayNumList();
			// 移動方向の反対向きの変数
			int movingWayBack;
			// 反対向き方向番号の設定
			if (myCarMovingWay < 2) {
				movingWayBack = myCarMovingWay + 2;
			} else {
				movingWayBack = myCarMovingWay - 2;
			}
			// 反対向き方向をリストから削除
			for (int i = 0; i < movableWayNumList.size(); i++) {
				if (movableWayNumList.get(i) == movingWayBack) {
					movableWayNumList.remove(i);
					break;
				}
			}
			// 向き変更用変数
			int randomNum = (int) (Math.random() * movableWayNumList.size());
			// 向きを変更
			objMyCar.setMovingWay(movableWayNumList.get(randomNum));
			// 変更完了しメソッドを抜ける
			return;
		}
	}

	// 敵機を移動するメソッド
	public void moveEnemyCar(EnemyCar objEnemyCar) {
		// 敵機のスピード変更
		objEnemyCar.accellEnemyCar();
		// 敵機の移動方向変更処理
		changeEnemyCarMovingWay(objEnemyCar);
		// 敵機座標移動処理
		objEnemyCar.moveEnemyCar();

	}

	// 敵機を表示するメソッド
	public void displayEnemyCar(EnemyCar objEnemyCar) {
		// 敵機の移動向き取得
		int movingWay = objEnemyCar.getMovingWay();
		// 敵機の種類を取得
		int enemyNum = objEnemyCar.getEnemyNumber();
		switch (movingWay) {
		// 上向きの場合
		case Data.MOVING_UP:
			// ランダム敵機の場合
			if (enemyNum == Data.RANDOM_ENEMY) {
				// 敵機表示
				canvas.drawBitmap(enemyCar1BmpUp,
						objEnemyCar.getEnemyCarCoodX(),
						objEnemyCar.getEnemyCarCoodY(), p);
			}
			// 追跡敵機の場合
			if (enemyNum == Data.PURSUE_ENEMY) {
				// 敵機表示
				canvas.drawBitmap(enemyCar2BmpUp,
						objEnemyCar.getEnemyCarCoodX(),
						objEnemyCar.getEnemyCarCoodY(), p);
			}
			break;
		// 右向きの場合
		case Data.MOVING_RIGHT:
			// ランダム敵機の場合
			if (enemyNum == Data.RANDOM_ENEMY) {
				// 敵機表示
				canvas.drawBitmap(enemyCar1BmpRight,
						objEnemyCar.getEnemyCarCoodX(),
						objEnemyCar.getEnemyCarCoodY(), p);
			}
			// 追跡敵機の場合
			if (enemyNum == Data.PURSUE_ENEMY) {
				// 敵機表示
				canvas.drawBitmap(enemyCar2BmpRight,
						objEnemyCar.getEnemyCarCoodX(),
						objEnemyCar.getEnemyCarCoodY(), p);
			}
			break;
		// 下向きの場合
		case Data.MOVING_DOWN:
			// ランダム敵機の場合
			if (enemyNum == Data.RANDOM_ENEMY) {
				// 敵機表示
				canvas.drawBitmap(enemyCar1BmpDown,
						objEnemyCar.getEnemyCarCoodX(),
						objEnemyCar.getEnemyCarCoodY(), p);
			}
			// 追跡敵機の場合
			if (enemyNum == Data.PURSUE_ENEMY) {
				// 敵機表示
				canvas.drawBitmap(enemyCar2BmpDown,
						objEnemyCar.getEnemyCarCoodX(),
						objEnemyCar.getEnemyCarCoodY(), p);
			}
			break;
		// 左向きの場合
		case Data.MOVING_LEFT:
			// ランダム敵機の場合
			if (enemyNum == Data.RANDOM_ENEMY) {
				// 敵機表示
				canvas.drawBitmap(enemyCar1BmpLeft,
						objEnemyCar.getEnemyCarCoodX(),
						objEnemyCar.getEnemyCarCoodY(), p);
			}
			// 追跡敵機の場合
			if (enemyNum == Data.PURSUE_ENEMY) {
				// 敵機表示
				canvas.drawBitmap(enemyCar2BmpLeft,
						objEnemyCar.getEnemyCarCoodX(),
						objEnemyCar.getEnemyCarCoodY(), p);
			}
			break;
		}
	}

	// 敵機の座標判定メソッド
	public int[] checkEnemyCarCood(EnemyCar objEnemyCar) {
		// x方向のfor文
		for (int x = 0; x < MAP_CHIP_SIZE_X; x++) {
			// 自機座標xとマップチップ座標xが一致する場合
			if (objEnemyCar.getEnemyCarCoodX() == (x * CHIP_SIZE)) {
				// y方向のfor文
				for (int y = 0; y < MAP_CHIP_SIZE_Y; y++) {
					// 自機座標yとマップチップ座標yが一致する場合
					if (objEnemyCar.getEnemyCarCoodY() == (y * CHIP_SIZE)) {
						// 座標配列
						int[] mapCood = { x, y };
						// 座標を返す
						return mapCood;
					}
				}
			}
		}
		// 一致しない場合はnullを返す
		return null;
	}

	// 敵機の移動方向を変更するメソッド
	public void changeEnemyCarMovingWay(EnemyCar objEnemyCar) {
		// 座標チェック
		int[] mapCood = checkEnemyCarCood(objEnemyCar);
		// 座標が一致しない場合
		if (mapCood == null) {
			// 変更不可のためメソッドを抜ける
			return;
		}
		// 移動可能方向の取得
		boolean[] movableWay = mapList[mapCood[0]][mapCood[1]].getMovableWay();
		// 敵機の移動方向取得
		int enemyCarMovingWay = objEnemyCar.getMovingWay();

		// 自動向き変更

		// 移動可能方向の番号リストの取得
		ArrayList<Integer> movableWayNumList = mapList[mapCood[0]][mapCood[1]]
				.getMovableWayNumList();
		// 移動方向の反対向きの変数
		int movingWayBack;
		// 反対向き方向番号の設定
		if (enemyCarMovingWay < 2) {
			movingWayBack = enemyCarMovingWay + 2;
		} else {
			movingWayBack = enemyCarMovingWay - 2;
		}
		// 反対向き方向をリストから削除
		for (int i = 0; i < movableWayNumList.size(); i++) {
			if (movableWayNumList.get(i) == movingWayBack) {
				movableWayNumList.remove(i);
				break;
			}
		}
		// 敵機の種類を取得
		int enemyCarNum = objEnemyCar.getEnemyNumber();
		// ランダム敵機の場合
		if (enemyCarNum == Data.RANDOM_ENEMY) {
			// 向き変更用変数
			int randomNum = (int) (Math.random() * movableWayNumList.size());
			// 向きを変更
			objEnemyCar.setMovingWay(movableWayNumList.get(randomNum));
		}
		// 追従敵機の場合
		else if (enemyCarNum == Data.PURSUE_ENEMY) {
			// 自機の座標取得
			int myCarCoodX = objMyCar.getMyCarCoodX();
			int myCarCoodY = objMyCar.getMyCarCoodY();
			// 追従敵機のX座標取得
			int pursueEnemyCarCoodX = objEnemyCar.getEnemyCarCoodX();
			int pursueEnemyCarCoodY = objEnemyCar.getEnemyCarCoodY();
			// 自機が追従敵機より左にいて かつ 左に移動可能 かつ 左が後ろ向きで無い場合
			if (myCarCoodX < pursueEnemyCarCoodX
					&& movableWay[Data.MOVING_LEFT]
					&& movingWayBack != Data.MOVING_LEFT) {
				// 向きを左に変更
				objEnemyCar.setMovingWay(Data.MOVING_LEFT);
			}
			// 自機が追従敵機より右にいて かつ 右に移動可能 かつ 右が後ろ向きで無い場合
			else if (myCarCoodX > pursueEnemyCarCoodX
					&& movableWay[Data.MOVING_RIGHT]
					&& movingWayBack != Data.MOVING_RIGHT) {
				// 向きを右に変更
				objEnemyCar.setMovingWay(Data.MOVING_RIGHT);
			}
			// 自機が追従敵機より上にいて かつ 上に移動可能 かつ 上が後ろ向きで無い場合
			else if (myCarCoodY < pursueEnemyCarCoodY
					&& movableWay[Data.MOVING_UP]
					&& movingWayBack != Data.MOVING_UP) {
				// 向きを上に変更
				objEnemyCar.setMovingWay(Data.MOVING_UP);
			}
			// 自機が追従敵機より下にいて かつ 下に移動可能 かつ 下が後ろ向きで無い場合
			else if (myCarCoodY > pursueEnemyCarCoodY
					&& movableWay[Data.MOVING_DOWN]
					&& movingWayBack != Data.MOVING_DOWN) {
				// 向きを下に変更
				objEnemyCar.setMovingWay(Data.MOVING_DOWN);
			}
			// その他の場合ランダムに変更
			else {
				// 向き変更用変数
				int randomNum = (int) (Math.random() * movableWayNumList.size());
				// 向きを変更
				objEnemyCar.setMovingWay(movableWayNumList.get(randomNum));
			}
		}
		// 変更完了しメソッドを抜ける
		return;
	}

	// スコアアイテムを表示するメソッド
	public void displayScoreItem() {
		// スコアアイテムの数だけ繰り返す
		for (int i = 0; i < scoreItemList.size(); i++) {
			// スコアアイテム表示
			canvas.drawBitmap(scoreItemBmp,
					scoreItemList.get(i).getItemCoodX(), scoreItemList.get(i)
							.getItemCoodY(), p);
		}
	}

	// パワーアイテムを表示するメソッド
	public void displayPowerItem() {
		// パワーアイテムの数だけ繰り返す
		for (int i = 0; i < powerItemList.size(); i++) {
			// パワーアイテム表示
			canvas.drawBitmap(powerItemBmp,
					powerItemList.get(i).getItemCoodX(), powerItemList.get(i)
							.getItemCoodY(), p);
		}
	}

	// スコアアイテムリストを生成する
	public void setScoreItem(int makingNum) {
		// スコアアイテム管理用セットを受け取った数だけ作るループ
		while (scoreItemRandomNumbers.size() < makingNum) {
			// ランダム変数の発生
			int randomNum = (int) (Math.random() * scoreItemAppearCood.size());
			// スコアアイテムの初期位置の設定
			Integer[] scoreItemCood = scoreItemAppearCood.get(randomNum);
			// スコアアイテム管理用セットに追加
			scoreItemRandomNumbers.add(scoreItemCood);
		}

		// スコアアイテムリストをリセット
		scoreItemList.removeAll(scoreItemList);
		// イテレイター
		Iterator<Integer[]> ite = scoreItemRandomNumbers.iterator();
		// スコアアイテムリスト追加のループ
		while (ite.hasNext()) {
			// セットから座標データの取得
			Integer[] itemCood = ite.next();
			// スコアアイテムのインスタンス
			ScoreItem scoreItem = new ScoreItem(itemCood[0], itemCood[1]);
			// スコアアイテムリストに追加
			scoreItemList.add(scoreItem);
			// マップ上にいるキャラクタをセット
			mapList[itemCood[0] / CHIP_SIZE][itemCood[1] / CHIP_SIZE]
					.setCharcter(Data.SCORE_ITEM);
		}
	}

	// 当たり判定を確認するメソッド
	public void checkHitting() {
		// レクト縮小用変数
		int size = 12;
		// 自機のレクト生成(自機画像サイズより左右上下を-12してある)
		Rect myCarRect = new Rect(objMyCar.getMyCarCoodX() + size,
				objMyCar.getMyCarCoodY() + size, objMyCar.getMyCarCoodX()
						+ CHIP_SIZE - size, objMyCar.getMyCarCoodY()
						+ CHIP_SIZE - size);
		// ランダム敵機のレクト
		for (int i = 0; i < randomEnemyList.size(); i++) {
			// 敵機のレクト生成
			Rect randomEnemyCarRect = new Rect(randomEnemyList.get(i)
					.getEnemyCarCoodX() + size, randomEnemyList.get(i)
					.getEnemyCarCoodY() + size, randomEnemyList.get(i)
					.getEnemyCarCoodX() + CHIP_SIZE - size, randomEnemyList
					.get(i).getEnemyCarCoodY() + CHIP_SIZE - size);
			// 当たり判定チェック
			if (Rect.intersects(myCarRect, randomEnemyCarRect)) {
				// パワーアイテムを持っている場合
				if (powerItemGot) {
					// ぶつかった敵を倒す(消去)
					randomEnemyList.remove(i);
					// パワーアイテム所持欄から画像を消去
					powerItemImageView.setImageBitmap(null);
					// パワーアイテム所持フラグ消去
					powerItemGot = false;
				}
				// パワーアイテムを持っていない場合
				else {
					// ゲームプレイ中フラグ消去
					playingFlag = false;
				}
			}
		}

		// 追跡敵機のレクト
		for (int i = 0; i < pursueEnemyList.size(); i++) {
			// 敵機のレクト生成
			Rect pursueEnemyCarRect = new Rect(pursueEnemyList.get(i)
					.getEnemyCarCoodX() + size, pursueEnemyList.get(i)
					.getEnemyCarCoodY() + size, pursueEnemyList.get(i)
					.getEnemyCarCoodX() + CHIP_SIZE - size, pursueEnemyList
					.get(i).getEnemyCarCoodY() + CHIP_SIZE - size);
			// 当たり判定チェック
			if (Rect.intersects(myCarRect, pursueEnemyCarRect)) {
				// パワーアイテムを持っている場合
				if (powerItemGot) {
					// ぶつかった敵を倒す(消去)
					pursueEnemyList.remove(i);
					// パワーアイテム所持欄から画像を消去
					powerItemImageView.setImageBitmap(null);
					// パワーアイテム所持フラグ消去
					powerItemGot = false;
				}
				// パワーアイテムを持っていない場合
				else {
					// ゲームプレイ中フラグ消去
					playingFlag = false;
				}
			}
		}

	}

	// スコアアイテムリストを取ったときの処理をするメソッド
	public void removeAndSetNewScoreItem(int[] itemCood) {
		// マップ座標（マップチップサイズで割ったもの）
		Integer[] mapCood = { itemCood[0] / CHIP_SIZE, itemCood[1] / CHIP_SIZE };
		// とったアイテムを消す前に新しいアイテムを生成する
		setScoreItem(MAX_SCORE_ITEM + 1);
		// スコアアイテムリストと座標が一致するものを探す
		for (int i = 0; i < scoreItemList.size(); i++) {
			// x座標が一致する場合
			if (itemCood[0] == scoreItemList.get(i).getItemCoodX()) {
				// y座標が一致する場合
				if (itemCood[1] == scoreItemList.get(i).getItemCoodY()) {
					// スコアアイテムリストから削除する
					scoreItemList.remove(i);
					// マップ上にいるキャラクタをリセット
					mapList[mapCood[0]][mapCood[1]].setCharcter(Data.NOTHING);
				}
			}
		}

		// イテレイター
		Iterator<Integer[]> ite = scoreItemRandomNumbers.iterator();
		// スコアアイテムリスト追加のループ
		while (ite.hasNext()) {
			// セットから座標データの取得
			Integer[] setInteger = ite.next();
			// int型に変換
			int[] setInt = new int[2];
			setInt[0] = setInteger[0].intValue();
			setInt[1] = setInteger[1].intValue();
			// 一致するものか判定
			if (setInt[0] == itemCood[0]) {
				if (setInt[1] == itemCood[1]) {
					// スコアアイテム管理用セットから削除
					ite.remove();
				}
			}
		}

		// スコアを加算する
		score += 100;
		// パワーアイテムが出現していない場合
		if (!powerItemAppeared) {
			// スコアアイテムカウンターを＋１する
			scoreItemCounter++;
			// スコアアイテムカウンターがパワーアイテムカウンターと一致する場合
			if (scoreItemCounter == powerItemCounter) {
				// スコアアイテムカウンターをリセット
				scoreItemCounter = 0;
				// パワーカウンターを＋１する
				powerItemCounter++;
				// パワーアイテムリストを生成する
				setPowerItem();
			}
		}
	}

	// パワーアイテムリストを生成するメソッド
	public void setPowerItem() {
		// ランダム変数の発生
		int randomNum = (int) (Math.random() * powerItemAppearCood.size());
		// パワーアイテムの初期位置の設定
		Integer[] powerItemCood = powerItemAppearCood.get(randomNum);
		// パワーアイテムリストをリセット
		powerItemList.removeAll(powerItemList);
		// パワーアイテムのインスタンス
		PowerItem powerItem = new PowerItem(powerItemCood[0].intValue(),
				powerItemCood[1].intValue());
		// パワーアイテムリストに追加
		powerItemList.add(powerItem);
		// マップ上にいるキャラクタをセット
		mapList[powerItemCood[0].intValue() / CHIP_SIZE][powerItemCood[1]
				.intValue() / CHIP_SIZE].setCharcter(Data.POWER_ITEM);
		// パワーアイテム出現フラグ
		powerItemAppeared = true;
	}

	// パワーアイテムリストを取ったときの処理をするメソッド
	public void removePowerItem(int[] itemCood) {
		// スコアアイテムリストと座標が一致するものを探す
		for (int i = 0; i < powerItemList.size(); i++) {
			// x座標が一致する場合
			if (itemCood[0] == powerItemList.get(i).getItemCoodX()) {
				// y座標が一致する場合
				if (itemCood[1] == powerItemList.get(i).getItemCoodY()) {
					// スコアアイテムリストから削除する
					powerItemList.remove(i);
					// マップ上にいるキャラクタをリセット
					mapList[itemCood[0] / CHIP_SIZE][itemCood[1] / CHIP_SIZE]
							.setCharcter(Data.NOTHING);
				}
			}
		}
		// パワーアイテム取得イメージビューに画像を表示
		powerItemImageView.setImageBitmap(powerItemBmp);
		// パワーアイテム出現フラグ消去
		powerItemAppeared = false;
		// パワーアイテムをすでに持っている場合
		if (powerItemGot) {
			// スコアを加算
			score += 1000;
		}
		// パワーアイテムを持っていない場合
		else {
			// パワーアイテム取得フラグ
			powerItemGot = true;
		}
	}

	// スコア表示処理をするメソッド
	public void setScore() {
		// スコアを＋１する
		score++;
		// スコアテキストビューにセット
		scoreNumText.setText(String.format("%1$010d", score));
	}

	// 追跡敵機リストを生成するメソッド
	public void setPursueEnemyCar() {
		// ランダム変数の発生
		int randomNum = (int) (Math.random() * carAppearCood.size());
		// 追跡敵機の初期位置の設定
		Integer[] pursueEnemyCarCood = carAppearCood.get(randomNum);
		// PursueEnemyCarのインスタンス
		PursueEnemyCar pursueEnemyCar = new PursueEnemyCar(
				pursueEnemyCarCood[0].intValue(),
				pursueEnemyCarCood[1].intValue());
		// 追跡敵機リストに追加
		pursueEnemyList.add(pursueEnemyCar);
	}

	// 時間表示処理をするメソッド
	public void setTime() {
		// 時間カウンターを＋１する
		timeCounter++;
		// 時間カウンターが２０になった場合
		if (timeCounter == 20) {
			// 時間カウンターをリセット
			timeCounter = 0;
			// 時間を加算
			time++;
			// 追跡敵機出現フラグを消去
			pursueEnemyAppeared = false;
		}
		// 時間が30の倍数になったとき
		if (time % 30 == 0 && time != 0) {
			// 追跡敵機出現フラグが立っていない場合
			if (!pursueEnemyAppeared) {
				// 追跡敵機を追加する
				setPursueEnemyCar();
				// 追跡敵機出現フラグ
				pursueEnemyAppeared = true;
			}
		}
		// 時間テキストビューにセット
		timeNumText.setText(String.format("%1$05d", time));
	}

	// スコアや時間、カウンターなどをリセットする
	public void resetNumbersEtc() {
		// スコアのリセット
		score = 0;
		// 時間のリセット
		time = 0;
		// 時間カウンターのリセット
		timeCounter = 0;
		// スコアアイテムカウンターをリセット
		scoreItemCounter = 0;
		// パワーアイテムカウンターをリセット
		powerItemCounter = 3;
		// パワーアイテム出現フラグ消去
		powerItemAppeared = false;
		// 追跡敵機出現フラグの消去
		pursueEnemyAppeared = false;

		// ゲームスタートカウンターをリセット
		gameStartCounter = 3;
		// スコアテキストビューにセット
		scoreNumText.setText(String.format("%1$010d", score));
		// 時間テキストビューにセット
		timeNumText.setText(String.format("%1$05d", time));

	}

	// フラグをリセットする
	public void resetFlags() {
		// タイトル画面中のフラグ
		titleFlag = true;
		// ゲームプレイ中のフラグ
		playingFlag = false;
		// ゲームオーバー中のフラグ
		gameoverFlag = false;
		// パワーアイテムを入手している場合
		if (powerItemGot) {
			// パワーアイテム所持欄から画像を消去
			powerItemImageView.setImageBitmap(null);
			// パワーアイテム所持フラグ消去
			powerItemGot = false;
		}
	}

	// マップチップを生成するメソッド
	public void setMapChip() {
		// マップチップの生成
		// 0行目
		int y = 0;
		mapList[0][y] = new MapTypeL12(0 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[1][y] = new MapTypeI13(1 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[2][y] = new MapTypeI13(2 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[3][y] = new MapTypeT0(3 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[4][y] = new MapTypeI13(4 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[5][y] = new MapTypeI13(5 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[6][y] = new MapTypeL23(6 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[7][y] = new MapTypeObj(7 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[8][y] = new MapTypeObj(8 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[9][y] = new MapTypeL12(9 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[10][y] = new MapTypeI13(10 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[11][y] = new MapTypeL23(11 * CHIP_SIZE, y * CHIP_SIZE);

		// 1行目
		y = 1;
		mapList[0][y] = new MapTypeI02(0 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[1][y] = new MapTypeObj(1 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[2][y] = new MapTypeObj(2 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[3][y] = new MapTypeI02(3 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[4][y] = new MapTypeObj(4 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[5][y] = new MapTypeObj(5 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[6][y] = new MapTypeT3(6 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[7][y] = new MapTypeI13(7 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[8][y] = new MapTypeT0(8 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[9][y] = new MapTypeL03(9 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[10][y] = new MapTypeObj(10 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[11][y] = new MapTypeI02(11 * CHIP_SIZE, y * CHIP_SIZE);

		// 2行目
		y = 2;
		mapList[0][y] = new MapTypeI02(0 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[1][y] = new MapTypeObj(1 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[2][y] = new MapTypeL12(2 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[3][y] = new MapTypeT2(3 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[4][y] = new MapTypeT0(4 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[5][y] = new MapTypeI13(5 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[6][y] = new MapTypeT1(6 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[7][y] = new MapTypeObj(7 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[8][y] = new MapTypeI02(8 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[9][y] = new MapTypeObj(9 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[10][y] = new MapTypeObj(10 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[11][y] = new MapTypeI02(11 * CHIP_SIZE, y * CHIP_SIZE);

		// 3行目
		y = 3;
		mapList[0][y] = new MapTypeT3(0 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[1][y] = new MapTypeI13(1 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[2][y] = new MapTypeT1(2 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[3][y] = new MapTypeObj(3 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[4][y] = new MapTypeI02(4 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[5][y] = new MapTypeObj(5 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[6][y] = new MapTypeT3(6 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[7][y] = new MapTypeI13(7 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[8][y] = new MapTypeT2(8 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[9][y] = new MapTypeT0(9 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[10][y] = new MapTypeI13(10 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[11][y] = new MapTypeT1(11 * CHIP_SIZE, y * CHIP_SIZE);

		// 4行目
		y = 4;
		mapList[0][y] = new MapTypeI02(0 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[1][y] = new MapTypeObj(1 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[2][y] = new MapTypeI02(2 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[3][y] = new MapTypeObj(3 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[4][y] = new MapTypeT3(4 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[5][y] = new MapTypeI13(5 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[6][y] = new MapTypeT1(6 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[7][y] = new MapTypeObj(7 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[8][y] = new MapTypeObj(8 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[9][y] = new MapTypeI02(9 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[10][y] = new MapTypeObj(10 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[11][y] = new MapTypeI02(11 * CHIP_SIZE, y * CHIP_SIZE);

		// 5行目
		y = 5;
		mapList[0][y] = new MapTypeT3(0 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[1][y] = new MapTypeI13(1 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[2][y] = new MapTypePlus(2 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[3][y] = new MapTypeI13(3 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[4][y] = new MapTypeT1(4 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[5][y] = new MapTypeObj(5 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[6][y] = new MapTypeT3(6 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[7][y] = new MapTypeI13(7 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[8][y] = new MapTypeT0(8 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[9][y] = new MapTypeT2(9 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[10][y] = new MapTypeT0(10 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[11][y] = new MapTypeL03(11 * CHIP_SIZE, y * CHIP_SIZE);

		// 6行目
		y = 6;
		mapList[0][y] = new MapTypeI02(0 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[1][y] = new MapTypeObj(1 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[2][y] = new MapTypeI02(2 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[3][y] = new MapTypeObj(3 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[4][y] = new MapTypeL01(4 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[5][y] = new MapTypeT0(5 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[6][y] = new MapTypeL03(6 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[7][y] = new MapTypeObj(7 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[8][y] = new MapTypeI02(8 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[9][y] = new MapTypeObj(9 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[10][y] = new MapTypeI02(10 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[11][y] = new MapTypeObj(11 * CHIP_SIZE, y * CHIP_SIZE);

		// 7行目
		y = 7;
		mapList[0][y] = new MapTypeT3(0 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[1][y] = new MapTypeI13(1 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[2][y] = new MapTypeT2(2 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[3][y] = new MapTypeL23(3 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[4][y] = new MapTypeObj(4 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[5][y] = new MapTypeI02(5 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[6][y] = new MapTypeObj(6 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[7][y] = new MapTypeL12(7 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[8][y] = new MapTypeT2(8 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[9][y] = new MapTypeI13(9 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[10][y] = new MapTypeT1(10 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[11][y] = new MapTypeObj(11 * CHIP_SIZE, y * CHIP_SIZE);

		// 8行目
		y = 8;
		mapList[0][y] = new MapTypeI02(0 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[1][y] = new MapTypeObj(1 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[2][y] = new MapTypeObj(2 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[3][y] = new MapTypeL01(3 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[4][y] = new MapTypeT0(4 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[5][y] = new MapTypeT2(5 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[6][y] = new MapTypeI13(6 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[7][y] = new MapTypeT1(7 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[8][y] = new MapTypeObj(8 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[9][y] = new MapTypeObj(9 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[10][y] = new MapTypeI02(10 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[11][y] = new MapTypeObj(11 * CHIP_SIZE, y * CHIP_SIZE);

		// 9行目
		y = 9;
		mapList[0][y] = new MapTypeT3(0 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[1][y] = new MapTypeI13(1 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[2][y] = new MapTypeL23(2 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[3][y] = new MapTypeObj(3 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[4][y] = new MapTypeI02(4 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[5][y] = new MapTypeObj(5 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[6][y] = new MapTypeObj(6 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[7][y] = new MapTypeT3(7 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[8][y] = new MapTypeI13(8 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[9][y] = new MapTypeT0(9 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[10][y] = new MapTypeT2(10 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[11][y] = new MapTypeL23(11 * CHIP_SIZE, y * CHIP_SIZE);

		// 10行目
		y = 10;
		mapList[0][y] = new MapTypeI02(0 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[1][y] = new MapTypeObj(1 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[2][y] = new MapTypeI02(2 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[3][y] = new MapTypeObj(3 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[4][y] = new MapTypeI02(4 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[5][y] = new MapTypeObj(5 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[6][y] = new MapTypeL12(6 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[7][y] = new MapTypeL03(7 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[8][y] = new MapTypeObj(8 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[9][y] = new MapTypeI02(9 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[10][y] = new MapTypeObj(10 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[11][y] = new MapTypeI02(11 * CHIP_SIZE, y * CHIP_SIZE);

		// 11行目
		y = 11;
		mapList[0][y] = new MapTypeT3(0 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[1][y] = new MapTypeI13(1 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[2][y] = new MapTypeT1(2 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[3][y] = new MapTypeObj(3 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[4][y] = new MapTypeT3(4 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[5][y] = new MapTypeI13(5 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[6][y] = new MapTypeT1(6 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[7][y] = new MapTypeObj(7 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[8][y] = new MapTypeL12(8 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[9][y] = new MapTypeT2(9 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[10][y] = new MapTypeI13(10 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[11][y] = new MapTypeT1(11 * CHIP_SIZE, y * CHIP_SIZE);

		// 12行目
		y = 12;
		mapList[0][y] = new MapTypeI02(0 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[1][y] = new MapTypeObj(1 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[2][y] = new MapTypeI02(2 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[3][y] = new MapTypeObj(3 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[4][y] = new MapTypeI02(4 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[5][y] = new MapTypeObj(5 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[6][y] = new MapTypeT3(6 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[7][y] = new MapTypeI13(7 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[8][y] = new MapTypeT1(8 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[9][y] = new MapTypeObj(9 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[10][y] = new MapTypeObj(10 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[11][y] = new MapTypeI02(11 * CHIP_SIZE, y * CHIP_SIZE);

		// 13行目
		y = 13;
		mapList[0][y] = new MapTypeL01(0 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[1][y] = new MapTypeI13(1 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[2][y] = new MapTypeT2(2 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[3][y] = new MapTypeI13(3 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[4][y] = new MapTypeT2(4 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[5][y] = new MapTypeI13(5 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[6][y] = new MapTypeL03(6 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[7][y] = new MapTypeObj(7 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[8][y] = new MapTypeL01(8 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[9][y] = new MapTypeI13(9 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[10][y] = new MapTypeI13(10 * CHIP_SIZE, y * CHIP_SIZE);
		mapList[11][y] = new MapTypeL03(11 * CHIP_SIZE, y * CHIP_SIZE);

		// 出現位置の設定
		// スコアアイテム
		scoreItemAppearCood
				.add(new Integer[] { 10 * CHIP_SIZE, 0 * CHIP_SIZE });
		scoreItemAppearCood.add(new Integer[] { 7 * CHIP_SIZE, 1 * CHIP_SIZE });
		scoreItemAppearCood.add(new Integer[] { 3 * CHIP_SIZE, 2 * CHIP_SIZE });
		scoreItemAppearCood.add(new Integer[] { 0 * CHIP_SIZE, 3 * CHIP_SIZE });
		scoreItemAppearCood.add(new Integer[] { 8 * CHIP_SIZE, 3 * CHIP_SIZE });
		scoreItemAppearCood.add(new Integer[] { 2 * CHIP_SIZE, 4 * CHIP_SIZE });
		scoreItemAppearCood.add(new Integer[] { 6 * CHIP_SIZE, 5 * CHIP_SIZE });
		scoreItemAppearCood
				.add(new Integer[] { 10 * CHIP_SIZE, 6 * CHIP_SIZE });
		scoreItemAppearCood.add(new Integer[] { 3 * CHIP_SIZE, 7 * CHIP_SIZE });
		scoreItemAppearCood.add(new Integer[] { 8 * CHIP_SIZE, 7 * CHIP_SIZE });
		scoreItemAppearCood
				.add(new Integer[] { 2 * CHIP_SIZE, 10 * CHIP_SIZE });
		scoreItemAppearCood
				.add(new Integer[] { 6 * CHIP_SIZE, 11 * CHIP_SIZE });
		scoreItemAppearCood
				.add(new Integer[] { 9 * CHIP_SIZE, 11 * CHIP_SIZE });
		scoreItemAppearCood
				.add(new Integer[] { 0 * CHIP_SIZE, 12 * CHIP_SIZE });

		// パワーアイテム
		powerItemAppearCood.add(new Integer[] { 2 * CHIP_SIZE, 0 * CHIP_SIZE });
		powerItemAppearCood
				.add(new Integer[] { 11 * CHIP_SIZE, 1 * CHIP_SIZE });
		powerItemAppearCood
				.add(new Integer[] { 1 * CHIP_SIZE, 11 * CHIP_SIZE });
		powerItemAppearCood
				.add(new Integer[] { 10 * CHIP_SIZE, 13 * CHIP_SIZE });

		// 自機＆敵機
		carAppearCood.add(new Integer[] { 0 * CHIP_SIZE, 0 * CHIP_SIZE });
		carAppearCood
				.add(new Integer[] { (MAP_CHIP_SIZE_X - 1) * CHIP_SIZE, 0 });
		carAppearCood.add(new Integer[] { (MAP_CHIP_SIZE_X - 1) * CHIP_SIZE,
				(MAP_CHIP_SIZE_Y - 1) * CHIP_SIZE });
		carAppearCood.add(new Integer[] { 0 * CHIP_SIZE,
				(MAP_CHIP_SIZE_Y - 1) * CHIP_SIZE });

	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int action = event.getAction();
		Log.w("画面", "view=" + view + "  event=" + event);
		switch (action & MotionEvent.ACTION_MASK) {
		// ボタンが押されたとき
		case MotionEvent.ACTION_DOWN:
			// どのボタンが押されているかの判定
			if (view == upImageView) {
				upButtonPressed = true;
				// 画像を暗くする
				upImageView.setColorFilter(Color.argb(100, 0, 0, 0));
			} else if (view == leftImageView) {
				leftButtonPressed = true;
				// 画像を暗くする
				leftImageView.setColorFilter(Color.argb(100, 0, 0, 0));
			} else if (view == rightImageView) {
				rightButtonPressed = true;
				// 画像を暗くする
				rightImageView.setColorFilter(Color.argb(100, 0, 0, 0));
			} else if (view == downImageView) {
				downButtonPressed = true;
				// 画像を暗くする
				downImageView.setColorFilter(Color.argb(100, 0, 0, 0));
			}
			break;
		// ボタンが離されたとき
		case MotionEvent.ACTION_UP:
			// どのボタンが押されているかの判定
			if (view == upImageView) {
				upButtonPressed = false;
				// 画像の色を元に戻す
				upImageView.setColorFilter(Color.alpha(0));
			} else if (view == leftImageView) {
				leftButtonPressed = false;
				// 画像の色を元に戻す
				leftImageView.setColorFilter(Color.alpha(0));
			} else if (view == rightImageView) {
				rightButtonPressed = false;
				// 画像の色を元に戻す
				rightImageView.setColorFilter(Color.alpha(0));
			} else if (view == downImageView) {
				downButtonPressed = false;
				// 画像の色を元に戻す
				downImageView.setColorFilter(Color.alpha(0));
			}
			break;
		}
		return true;
	}

	public void onClickAbutton(View v) {
		// タイトル画面中で押した場合
		if (titleFlag) {
			// タイトルフラグ消去
			titleFlag = false;
			// ゲームスタートフラグ
			playingFlag = true;
			// ゲームの初期化
			firstSetting();
			// ゲーム前スレッド開始
			handler.post(prepareGameThread);
		} else if (gameoverFlag) {
			// ゲームオーバーフラグ消去
			gameoverFlag = false;
			// タイトルフラグ
			titleFlag = true;
			// 初期化処理
			resetNumbersEtc();
			// タイトル画面呼び出し
			mainGameSurfaceView.run();
		}
	}

}
