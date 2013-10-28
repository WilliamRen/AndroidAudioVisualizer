package com.dentonposs.visualizer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class SongPickActivity extends Activity {
	public static Uri songURI;

	public void onActivityResult(int paramInt1, int paramInt2,
			Intent paramIntent) {
		songURI = paramIntent.getData();
		startActivity(new Intent(this, MainActivity.class));
	}

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.activity_song_pick);
	}

	public boolean onCreateOptionsMenu(Menu paramMenu) {
		getMenuInflater().inflate(R.menu.activity_main, paramMenu);
		return true;
	}

	public void runVisualizer(View paramView) {
		Intent localIntent = new Intent("android.intent.action.GET_CONTENT");
		localIntent.setType("audio/*");
		startActivityForResult(Intent.createChooser(localIntent, "Select soundfile"), 1);
	}
}