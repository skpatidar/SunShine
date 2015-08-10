package com.example.spatidar.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {
    final String LOG_TAG = MainActivity.class.getSimpleName();
    private String mLocation;
    final String FORECASTFRAGMENT_TAG = "FRAG_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment(), FORECASTFRAGMENT_TAG)
                    .commit();
        }
        mLocation = Utility.getPreferredLocation(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean openPreferredLocationInMap() {
        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(this);
        String location = sharedPref.getString(
                getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));

        Uri mapUri = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", location)
                .build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(mapUri);
        if (intent.resolveActivity(this.getPackageManager()) != null) {
            startActivity(intent);
            return true;
        } else {
            Log.d(LOG_TAG, "Could not load Map application\n");
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (item.getItemId() == R.id.action_viewonmap) {
            openPreferredLocationInMap();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause(){
        Log.v(LOG_TAG, "Paused");
        super.onPause();
    }

    @Override
    protected void onResume(){
        Log.v(LOG_TAG, "Resumed");

        if (Utility.getPreferredLocation(this) != mLocation) {
            ForecastFragment ff = (ForecastFragment)getSupportFragmentManager().findFragmentByTag(FORECASTFRAGMENT_TAG);
            ff.onLocationChanged();
            mLocation = Utility.getPreferredLocation(this);
        }

        super.onResume();
    }

    @Override
    protected void onStop(){
        Log.v(LOG_TAG, "Stopped");
        super.onStop();
    }

    @Override
    protected void onStart(){
        Log.v(LOG_TAG, "Started");
        super.onStart();
    }

    protected void onCreate(){
        Log.v(LOG_TAG, "Created");
    }

    @Override
    protected void onDestroy(){
        Log.v(LOG_TAG, "Destroyed");
        super.onDestroy();
    }

}
