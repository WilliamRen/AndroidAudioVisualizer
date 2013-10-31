package com.dentonposs.visualizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SoundCloud extends Activity {

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_sound_cloud);

	// instantiate it within the onCreate method
	mProgressDialog = new ProgressDialog(SoundCloud.this);
	mProgressDialog.setMessage("A message");
	mProgressDialog.setIndeterminate(true);
	mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	mProgressDialog.setCancelable(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	getMenuInflater().inflate(R.menu.sound_cloud, menu);
	return true;
    }

    public void startDownload(View v) {

	// execute this when the downloader must be fired
	final DownloadTask downloadTask = new DownloadTask(SoundCloud.this);
	EditText edit = (EditText) findViewById(R.id.editText1);
	String input = edit.getText().toString();

	if ((input.contains("http://soundcloud.com/") || input.contains("https://soundcloud.com/"))) {
	    downloadTask.setSaveLoc("/soundcloud_temp");
	    downloadTask.execute("http://api.soundcloud.com/resolve.json?url=" + input + "&client_id=a72c2a754915b7598e1252b30fdcb233");

	    mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
		@Override
		public void onCancel(DialogInterface dialog) {
		    downloadTask.cancel(true);
		}
	    });
	} else {
	    Toast.makeText(getBaseContext(), "Invalid Soundcloud URL", Toast.LENGTH_SHORT).show();
	}
	
	getID();
	
	//do{
	//    ;
	//}while(downloadTask.isDownloading);

    }

    private void getID() {
	File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/AudioVisualizer/");
	File info = new File(dir + "/soundcloud_temp");

	FileInputStream fis = null;

	try {
	    fis = new FileInputStream(info);
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}

	InputStreamReader isr = new InputStreamReader(fis);
	BufferedReader br = new BufferedReader(isr);

	String line;

	try {
	    final DownloadTask downloadTask = new DownloadTask(SoundCloud.this);
	    line = br.readLine();
	    line = line.substring(0, Math.min(line.length(), 30));
	    line = line.substring(Math.max(0, line.length() - 9));
	    String id = line;
	    downloadTask.setSaveLoc("/temp_audio.mp3");
	    downloadTask.execute("http://api.soundcloud.com/tracks/" + id + "/stream?client_id=a72c2a754915b7598e1252b30fdcb233");
	} catch (IOException e) {
	    e.printStackTrace();
	}
	
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {

	private Context context;
	private String fileLoc;

	public DownloadTask(Context context) {
	    this.context = context;
	}

	public void setSaveLoc(String loc) {
	    fileLoc = loc;
	   // File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/AudioVisualizer/");
	   // File tmpfile = new File(dir + fileLoc);
	   // if(tmpfile.exists()){
	//	tmpfile.delete();
	  //  }
	}

	@Override
	protected void onPreExecute() {
	    super.onPreExecute();
	    if(fileLoc != "/soundcloud_tmp"){
		mProgressDialog.show();
	    }
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
	    super.onProgressUpdate(progress);
	    // if we get here, length is known, now set indeterminate to false
	    mProgressDialog.setIndeterminate(false);
	    mProgressDialog.setMax(100);
	    mProgressDialog.setProgress(progress[0]);
	}

	@Override
	protected void onPostExecute(String result) {
	    mProgressDialog.dismiss();
	    if (result != null)
		Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
	    else
		if(fileLoc != "/soundcloud_temp"){
		    Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
		}
	    
	    if(fileLoc == "/temp_audio.mp3"){
		File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/AudioVisualizer/");
		File audiofile = new File(dir + fileLoc);
		Uri songURI = Uri.fromFile(audiofile);
		Toast.makeText(getBaseContext(), songURI.toString(), Toast.LENGTH_LONG).show();
		SongPickActivity.songURI = songURI;
		startActivity(new Intent(getBaseContext(), MainActivity.class));
	    }
	    
	    
	}

	@Override
	protected String doInBackground(String... sUrl) {
	    // take CPU lock to prevent CPU from going off if the user
	    // presses the power button during download
	    PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
	    PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
	    wl.acquire();

	    try {
		InputStream input = null;
		OutputStream output = null;
		HttpURLConnection connection = null;
		try {
		    URL url = new URL(sUrl[0]);
		    connection = (HttpURLConnection) url.openConnection();
		    connection.connect();

		    // expect HTTP 200 OK, so we don't mistakenly save error
		    // report
		    // instead of the file
		    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
			return "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();

		    // this will be useful to display download percentage
		    // might be -1: server did not report the length
		    int fileLength = connection.getContentLength();

		    // download the file
		    input = connection.getInputStream();

		    File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/AudioVisualizer/");

		    if (!dir.exists()) {
			dir.mkdirs();
		    }

		    output = new FileOutputStream(dir + fileLoc);

		    byte data[] = new byte[4096];
		    long total = 0;
		    int count;
		    while ((count = input.read(data)) != -1) {
			// allow canceling with back button
			if (isCancelled())
			    return null;
			total += count;
			// publishing the progress....
			if (fileLength > 0) // only if total length is known
			    publishProgress((int) (total * 100 / fileLength));
			output.write(data, 0, count);
		    }
		} catch (Exception e) {
		    return e.toString();
		} finally {
		    try {
			if (output != null)
			    output.close();
			if (input != null)
			    input.close();
		    } catch (IOException ignored) {
		    }

		    if (connection != null)
			connection.disconnect();
		}
	    } finally {
		wl.release();
	    }
	    return null;
	}
    }

}
