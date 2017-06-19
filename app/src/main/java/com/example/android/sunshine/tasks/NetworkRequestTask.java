package com.example.android.sunshine.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.sunshine.adapters.ForecastAdapter;
import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;

/**
 * @deprecated Esta clase ya no se está utilizando, en favor de AsyncTaskLoader.
 */
public class NetworkRequestTask extends AsyncTask<String, Void, String[]> {

    private Context context;
    private RecyclerView weatherDisplay;
    private ForecastAdapter adapter;
    private TextView errorDisplay;
    private ProgressBar progress;

    public NetworkRequestTask(Context context, RecyclerView weatherDisplay, ForecastAdapter adapter, TextView errorDisplay, ProgressBar progress) {
        this.context = context;
        this.weatherDisplay = weatherDisplay;
        this.adapter = adapter;
        this.errorDisplay = errorDisplay;
        this.progress = progress;
    }

    @Override
    protected String[] doInBackground(String[] locations) {
        if(locations.length == 0) {
            return null;
        }

        try {
            String urlResults = NetworkUtils.getResponseFromHttpUrl(NetworkUtils.buildUrl(locations[0]));
            return OpenWeatherJsonUtils.getSimpleWeatherStringsFromJson(context, urlResults);
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPreExecute() {
        progress.setVisibility(ProgressBar.VISIBLE);
    }

    @Override
    protected void onPostExecute(String[] result) {
        progress.setVisibility(ProgressBar.INVISIBLE);

        if(result != null) {
            showWeatherDataView(result);
        } else {
            showErrorMessage();
        }
    }

    private void showWeatherDataView(String[] result)
    {
        weatherDisplay.setVisibility(TextView.VISIBLE);
        errorDisplay.setVisibility(TextView.INVISIBLE);

        adapter.setWeatherData(null);
        adapter.setWeatherData(result);
    }

    private void showErrorMessage()
    {
        weatherDisplay.setVisibility(TextView.INVISIBLE);
        errorDisplay.setVisibility(TextView.VISIBLE);
    }
}
