package race.game.click;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/*
 * サーフェイスビュー(ゲームメイン処理)
 */
class MainSurfaceView implements SurfaceHolder.Callback, Runnable {
	// スレッド
	Thread mainThread = null;

	public SurfaceHolder sHolder;

	// 描画用キャンバス
	public Canvas canvas;

	// 描画用ペイント
	public Paint p;

	// コンストラクタ
	// ※レイアウトから取得したサーフェイスビューを取得する為
	MainSurfaceView(Context ct, SurfaceView sv) {
		// サーフェイスビュー(フォルダー)の取得
		sHolder = sv.getHolder();
		sHolder.addCallback(this);

		// サーフェイスのサイズ設定
		sHolder.setFixedSize(sv.getWidth(), sv.getHeight());

		// ペイント生成
		p = new Paint();

		// ペイント属性設定(テスト用)
		p.setColor(Color.LTGRAY);
		p.setTextSize(16);

	}

	/*
	 * サーフェイス作成時
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mainThread = new Thread(this);
		mainThread.start();
	}

	/*
	 * サーフェイス変更時(画面回転)
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
	}

	/*
	 * サーフェイス破棄時
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// スレッドのストップ
		if (mainThread.isAlive()) {
			mainThread.stop();
		}
	}

	/*
	 * タイトル用スレッド
	 */
	@Override
	public void run() {
		// サーフェイスロック
		canvas = sHolder.lockCanvas();
		// 背景を緑に描画
		canvas.drawColor(Color.GREEN);
		// ペイントの設定
		p.setAntiAlias(true);
		p.setTextSize(40.0f);
		p.setColor(Color.BLUE);
		// タイトルの表示
		canvas.drawText("Rally Racing Game", 25, 170, p);
		// ペイントの設定
		p.setTextSize(25.0f);
		p.setColor(Color.BLACK);
		// 説明文の表示
		canvas.drawText("Aボタンを押してください", 45, 300, p);
		// サーフェイスロック解除
		sHolder.unlockCanvasAndPost(canvas);
	}
}
