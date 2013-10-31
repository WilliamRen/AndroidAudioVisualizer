package com.dentonposs.visualizer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SongPickActivity extends Activity {
    public static Uri songURI;
    public static EditText multiplier;
    public static EditText spacing;
    public static EditText lineWidth;
    private int height, width;

    public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent) {
	if (paramInt2 == Activity.RESULT_OK) {
	    songURI = paramIntent.getData();
	    Toast.makeText(getBaseContext(), songURI.toString(), Toast.LENGTH_LONG).show();
	    startActivity(new Intent(this, MainActivity.class));
	}
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle paramBundle) {
	super.onCreate(paramBundle);
	setContentView(R.layout.activity_song_pick);

	Display localDisplay = ((WindowManager) getBaseContext().getSystemService("window")).getDefaultDisplay();
	if (android.os.Build.VERSION.SDK_INT < 13) {
	    width = localDisplay.getWidth();
	    height = localDisplay.getHeight();
	} else {
	    Point size = new Point();
	    localDisplay.getSize(size);
	    width = size.x;
	    height = size.y;
	}

	TextView barnumtv = (TextView) findViewById(R.id.textView2);
	barnumtv.setText(barnumtv.getText().toString() + " " + width);
	multiplier = (EditText) findViewById(R.id.multiplier);
	spacing = (EditText) findViewById(R.id.spacing);
	spacing.setFilters(new InputFilter[] { new InputFilterMinMax(1, width) });
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
    
    public void startSCActivity(View v){
	startActivity(new Intent(getBaseContext(), SoundCloud.class));
    }
}