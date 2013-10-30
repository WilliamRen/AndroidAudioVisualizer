package com.dentonposs.visualizer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class SongPickActivity extends Activity {
	public static Uri songURI;
	public static EditText multiplier;
	public static EditText spacing;
	public static EditText lineWidth;


	public void onActivityResult(int paramInt1, int paramInt2,
			Intent paramIntent) {
		songURI = paramIntent.getData();
		startActivity(new Intent(this, MainActivity.class));
	}

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.activity_song_pick);
		multiplier = (EditText) findViewById(R.id.multiplier);
		spacing = (EditText) findViewById(R.id.spacing);
		lineWidth = (EditText) findViewById(R.id.width);
		multiplier.setText("3");
		spacing.setText("256");
		lineWidth.setText("2");
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