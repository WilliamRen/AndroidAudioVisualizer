package com.dentonposs.visualizer;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public void onDestroy() {
		Panel.player.stop();
		super.onDestroy();
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {

		if (e.getPointerCount() == 4) {
			finish();
		}

		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	public void pauseSong(View v){
		Panel.pauseSong();
	}
	
	public void playSong(View v){
		Panel.playSong();
	}
	
}
