package com.example.android.visualizerpreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.visualizerpreferences.AudioVisuals.AudioInputReader;
import com.example.android.visualizerpreferences.AudioVisuals.VisualizerView;

public class VisualizerActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    private static final int MY_PERMISSION_RECORD_AUDIO_REQUEST_CODE = 88;
    private VisualizerView mVisualizerView;
    private AudioInputReader mAudioInputReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizer);
        mVisualizerView = (VisualizerView) findViewById(R.id.activity_visualizer);
        setupSharedPreferences();
        setupPermissions();
    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mVisualizerView.setShowBass(sharedPreferences.getBoolean("show_bass",true));
        mVisualizerView.setShowMid(sharedPreferences.getBoolean("show_mid_range",false));
        mVisualizerView.setShowTreble(sharedPreferences.getBoolean("show_treble",false));
        mVisualizerView.setMinSizeScale(1);
        loadColorFromPreferences(sharedPreferences);


        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }
    private void loadColorFromPreferences(SharedPreferences sharedPreferences){

        mVisualizerView.setColor(sharedPreferences.getString("color",getString(R.string.pref_color_red_value)));



    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if(key.equals("show_bass")){
            mVisualizerView.setShowBass(sharedPreferences.getBoolean(key,true));
        }
        else if(key.equals("show_mid_range")){
            mVisualizerView.setShowMid(sharedPreferences.getBoolean(key,false));
        }
        else if(key.equals("show_treble")){
            mVisualizerView.setShowTreble(sharedPreferences.getBoolean(key,false));
        }
        else if(key.equals("color")){
            loadColorFromPreferences(sharedPreferences);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //To prevent memory leak
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.visualizer_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.action_setting){
            Intent startSettingActivity = new Intent(this,SettingsActivity.class);
            startActivity(startSettingActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Below this point is code you do not need to modify; it deals with permissions
     * and starting/cleaning up the AudioInputReader
     **/

    /**
     * onPause Cleanup audio stream
     **/
    @Override
    protected void onPause() {
        super.onPause();
        if (mAudioInputReader != null) {
            mAudioInputReader.shutdown(isFinishing());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAudioInputReader != null) {
            mAudioInputReader.restart();
        }
    }

    /**
     * App Permissions for Audio
     **/
    private void setupPermissions() {
        // If we don't have the record audio permission...
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // And if we're on SDK M or later...
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Ask again, nicely, for the permissions.
                String[] permissionsWeNeed = new String[]{ Manifest.permission.RECORD_AUDIO };
                requestPermissions(permissionsWeNeed, MY_PERMISSION_RECORD_AUDIO_REQUEST_CODE);
            }
        } else {
            // Otherwise, permissions were granted and we are ready to go!
            mAudioInputReader = new AudioInputReader(mVisualizerView, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                          String permissions[],int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_RECORD_AUDIO_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // The permission was granted! Start up the visualizer!
                    mAudioInputReader = new AudioInputReader(mVisualizerView, this);

                } else {
                    Toast.makeText(this, "Permission for audio not granted. Visualizer can't run.", Toast.LENGTH_LONG).show();
                    finish();

                }
            }


        }
    }
}