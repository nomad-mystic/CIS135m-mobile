package com.nomad_mystic_meals.lifecycle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;


public class MainActivity extends ActionBarActivity {

    public static final String LOG_TAG = "MainActivity";
    private static final String RUNNING = "mRunning";
    private static final String CURRENT_POS = "mCurrentPos";
    private int mRunning = 0;
    private boolean mStoppedRunning = false;
    private VideoView mView;
    private int mCurrentPos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Called on Create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mView = (VideoView) findViewById(R.id.video_view);
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        if(sharedPref != null) {
            mRunning = 0;
            mCurrentPos = 0;
        } else {
            mRunning = sharedPref.getInt(RUNNING, 0);
            mCurrentPos =  sharedPref.getInt(CURRENT_POS, 0);
        }
    }
    @Override
    public void onStart() {
        Log.d(LOG_TAG, "Called on onStart");
        super.onStart();
        mStoppedRunning = false;
        Thread runningTimer = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!mStoppedRunning) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        return;
                    }
                    mRunning++;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView tv = (TextView) findViewById(R.id.running_counter);
                            tv.setText("Running:" + mRunning);
                        }
                    });
                }
            }
        });
        runningTimer.start();

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(mView);

        Uri uri = Uri.parse("http://spot.pcc.edu/~mgoodman/Videos/135M/tomandjerry.3gp");
        mView.setVideoURI(uri);
        mView.setMediaController(mediaController);

    }
    @Override
    public void onResume() {
        Log.d(LOG_TAG, "Called on onResume");
        super.onResume();
        mView.seekTo(mCurrentPos);
        mView.start();
    }

    @Override
    public void onPause() {
        Log.d(LOG_TAG, "Called on onPause");
        super.onPause();
        mCurrentPos = mView.getCurrentPosition();
        mView.pause();
    }
    @Override
    public void onStop() {
        Log.d(LOG_TAG, "Called on onStop");
        super.onStop();
        mStoppedRunning = true;
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "Called on onDestroy");
        super.onDestroy();
        SharedPreferences sharedPrefs = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(RUNNING, mRunning);
        editor.putInt(CURRENT_POS, mCurrentPos);
        editor.commit();
    }
//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        savedInstanceState.putInt(RUNNING, mRunning);
//        savedInstanceState.putInt(CURRENT_POS, mCurrentPos);
//        super.onSaveInstanceState(savedInstanceState);
//    }
    @Override
    public void onRestart() {
        Log.d(LOG_TAG, "Called on onRestart");
        super.onRestart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.d(LOG_TAG, "Called onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(LOG_TAG, "Called onOptionsItemSelected");
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if(id == R.id.action_pause) {
            Intent intent = new Intent(this, PauseActivity.class);
            startActivity(intent);
        } else if(id == R.id.action_second) {
            Intent intent = new Intent(this, SecondActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
