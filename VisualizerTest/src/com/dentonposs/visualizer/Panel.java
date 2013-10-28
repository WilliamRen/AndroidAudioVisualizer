package com.dentonposs.visualizer;

import java.nio.ByteBuffer;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.util.AttributeSet;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class Panel extends SurfaceView implements SurfaceHolder.Callback {
	public static MediaPlayer player;
	private CanvasThread canvasthread;
	int height;
	Paint paint = new Paint(-65536);
	Random rand = new Random();
	Visualizer vis;
	private byte[] waveDataByte;
	int width;

	public Panel(Context paramContext) {
		super(paramContext);
		getHolder().addCallback(this);
		setFocusable(true);
	}

	public Panel(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		getHolder().addCallback(this);
		this.canvasthread = new CanvasThread(getHolder(), this);
		setFocusable(true);
		Display localDisplay = ((WindowManager) paramContext
				.getSystemService("window")).getDefaultDisplay();
		Point localPoint = new Point();
		localDisplay.getSize(localPoint);
		this.width = localPoint.x;
		this.height = localPoint.y;
		this.waveDataByte = new byte[this.width / 5];
		player = MediaPlayer.create(getContext(), SongPickActivity.songURI);
		player.start();
		this.paint.setColor(-1);
		this.paint.setStrokeWidth(2.0F);
		this.vis = new Visualizer(player.getAudioSessionId());
		this.vis.setCaptureSize(this.width / 5);
		this.vis.setEnabled(true);
	}

	private int colorFromARGB(int paramInt1, int paramInt2, int paramInt3,
			int paramInt4) {
		return paramInt4 | (paramInt1 << 24 | paramInt2 << 16 | paramInt3 << 8);
	}

	public static double toDouble(byte[] paramArrayOfByte) {
		return ByteBuffer.wrap(paramArrayOfByte).getDouble();
	}

	public void onDraw(Canvas paramCanvas) {
		paramCanvas.drawColor(-16777216);
		this.vis.getFft(this.waveDataByte);
		for (int i = 0;; i++) {
			if (i >= this.waveDataByte.length)
				return;
			this.paint.setColor(colorFromARGB(255, 255 - i % 255, 0, i % 255));
			paramCanvas.drawLine(i * 5, this.height, i * 5,
					this.height - Math.abs(5 * this.waveDataByte[i]),
					this.paint);
		}
	}

	public void surfaceChanged(SurfaceHolder paramSurfaceHolder, int paramInt1,
			int paramInt2, int paramInt3) {
	}

	public void surfaceCreated(SurfaceHolder paramSurfaceHolder) {
		this.canvasthread = new CanvasThread(getHolder(), this);
		this.canvasthread.setRunning(true);
		this.canvasthread.start();
	}

	public void surfaceDestroyed(SurfaceHolder paramSurfaceHolder) {
		int i = 1;
		this.canvasthread.setRunning(false);
		player.stop();
		while (true) {
			if (i == 0)
				return;
			try {
				this.canvasthread.join();
				i = 0;
			} catch (InterruptedException localInterruptedException) {
			}
		}
	}
}