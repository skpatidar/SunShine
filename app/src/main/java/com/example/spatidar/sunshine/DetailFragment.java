package com.example.spatidar.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.spatidar.sunshine.data.WeatherContract;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp ";

    private ShareActionProvider mShareActionProvider;
    private String mForecastString;

    private static final int DETAIL_LOADER  = 0;

    private final String[] DETAIL_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
    };
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_WEATHER_HUMIDITY = 4;
    static final int COL_WEATHER_WIND_SPEED = 5;
    static final int COL_WEATHER_DEGREES = 6;
    static final int COL_WEATHER_PRESSURE = 7;

    private ImageView mIconView;
    private TextView mFriendlyDateView;
    private TextView mDateView;
    private TextView mDescriptionView;
    private TextView mHighTempView;
    private TextView mLowTempView;
    private TextView mHumidityView;
    private TextView mWindView;
    private TextView mPressureView;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        // Inflate the menu, this adds items to the action bar if present
        inflater.inflate(R.menu.detailfragment, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);

        ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        } else {
            Log.d(LOG_TAG, "Share Action Provider is null ?");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            mIconView = (ImageView) rootView.findViewById(R.id.detail_icon);
            mDateView = (TextView) rootView.findViewById(R.id.detail_date_textview);
            mFriendlyDateView = (TextView) rootView.findViewById(R.id.detail_day_textview);
            mDescriptionView = (TextView) rootView.findViewById(R.id.detail_forecast_textview);
            mHighTempView = (TextView) rootView.findViewById(R.id.detail_high_textview);
            mLowTempView = (TextView) rootView.findViewById(R.id.detail_low_textview);
            mHumidityView = (TextView) rootView.findViewById(R.id.detail_humidity_textview);
            mWindView = (TextView) rootView.findViewById(R.id.detail_wind_textview);
            mPressureView = (TextView) rootView.findViewById(R.id.detail_pressure_textview);
        return rootView;
    }

    private Intent createShareForecastIntent(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                mForecastString + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    // Loader callback functions
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.v(LOG_TAG, "In onCreateLoader");

        Intent intent = getActivity().getIntent();
        if (null == intent) {
            return null;
        }

        return new CursorLoader(
                getActivity(),
                intent.getData(),
                DETAIL_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursor, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished()");
        if (!data.moveToFirst()) {
            return ;
        }

        // Weather Id
        int weatherId = data.getInt(COL_WEATHER_ID);
        mIconView.setImageResource(R.drawable.ic_launcher);

        // Read date from cursor and update views for day of week and date
        long date = data.getLong(COL_WEATHER_DATE);
        String friendlyDateText = Utility.getDayName(getActivity(), date);
        String dateText = Utility.getFormattedMonthDay(getActivity(), date);
        mFriendlyDateView.setText(friendlyDateText);
        mDateView.setText(dateText);

        // Read description from cursor and update view
        String description = data.getString(COL_WEATHER_DESC);
        mDescriptionView.setText(description);

        // Read high temperature from cursor and update view
        boolean isMetric = Utility.isMetric(getActivity());
        double high = data.getDouble(COL_WEATHER_MAX_TEMP);
        String highString = Utility.formatTemperature(getActivity(), high, isMetric);
        mHighTempView.setText(highString);

        // Read low temperature from cursor and update view
        double low = data.getDouble(COL_WEATHER_MIN_TEMP);
        String lowString = Utility.formatTemperature(getActivity(), low, isMetric);
        mLowTempView.setText(lowString);

        // Read humidity from cursor and update view
        float humidity = data.getFloat(COL_WEATHER_HUMIDITY);
        mHumidityView.setText(getActivity().getString(R.string.format_humidity, humidity));

        // Read wind speed and direction from cursor and update view
        float windSpeedStr = data.getFloat(COL_WEATHER_WIND_SPEED);
        float windDirStr = data.getFloat(COL_WEATHER_DEGREES);
        mWindView.setText(Utility.getFormattedWind(getActivity(), windSpeedStr, windDirStr));

        // Read pressure from cursor and update view
        float pressure = data.getFloat(COL_WEATHER_PRESSURE);
        mPressureView.setText(getActivity().getString(R.string.format_pressure, pressure));

        // We still need this for the share intent
        mForecastString = String.format("%s - %s - %s/%s", dateText, description, high, low);

        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }
}