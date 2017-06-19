/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.sunshine.adapters.ForecastAdapter;
import com.example.android.sunshine.data.SunshinePreferences;
import com.example.android.sunshine.interfaces.ForecastListItemClickListener;
import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;

public class MainActivity extends AppCompatActivity implements ForecastListItemClickListener, LoaderManager.LoaderCallbacks<String[]> {

    private RecyclerView weatherDisplay;
    private ForecastAdapter forecastAdapter;
    private TextView errorDisplay;
    private ProgressBar progressBar;

    private static final int FORECAST_NETWORK_REQUEST_LOADER = 0;
    private static final String WEATHER_LOCATION_EXTRA = "weather_location_extra";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("event_debug", "Creando la actividad MainActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        weatherDisplay = (RecyclerView) findViewById(R.id.rv_forecast);
        errorDisplay = (TextView) findViewById(R.id.weather_error);
        progressBar = (ProgressBar) findViewById(R.id.progress);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        weatherDisplay.setLayoutManager(layoutManager);
        weatherDisplay.setHasFixedSize(true); // todos los elementos del listado tendrán el mismo tamaño
        forecastAdapter = new ForecastAdapter(this);
        weatherDisplay.setAdapter(forecastAdapter);

        getWeatherInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.forecast, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                invalidateData();
                getWeatherInfo();
                break;
        }

        return true;
    }

    private void invalidateData() {
        forecastAdapter.setWeatherData(null);
    }

    private void getWeatherInfo() {
        Bundle queryBundle = new Bundle();
        queryBundle.putString(WEATHER_LOCATION_EXTRA, SunshinePreferences.getPreferredWeatherLocation(this));

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String[]> networkRequestLoader = loaderManager.getLoader(FORECAST_NETWORK_REQUEST_LOADER);

        if(networkRequestLoader == null) {
            Log.d("event_debug", "Inicializando el loader ForecastNetworkRequest");
            loaderManager.initLoader(FORECAST_NETWORK_REQUEST_LOADER, queryBundle, this);
        } else {
            Log.d("event_debug", "Relanzando el loader ForecastNetworkRequest");
            loaderManager.restartLoader(FORECAST_NETWORK_REQUEST_LOADER, queryBundle, this);
        }
    }

    @Override
    public void onClick(String weatherForToday) {
        openDetail(weatherForToday);
    }

    private void openDetail(String weatherForToday) {
        Intent detailActivityIntent = new Intent(this, DetailActivity.class);
        detailActivityIntent.putExtra(Intent.EXTRA_TEXT, weatherForToday);
        startActivity(detailActivityIntent);
    }

    @Override
    public Loader<String[]> onCreateLoader(int id, final Bundle args) {
        Log.d("event_debug", "Evento onCreate del loader principal lanzado");
        return new AsyncTaskLoader<String[]>(this) {
            String[] weatherData = null;

            @Override
            public void onStartLoading() {
                Log.d("event_debug", "Evento onStart del loader principal lanzado");
                if(weatherData != null) {
                    Log.d("event_debug", "Información del tiempo en caché");
                    deliverResult(weatherData);
                } else {
                    Log.d("event_debug", "Información del tiempo no encontrada, solicitando el servicio...");
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public String[] loadInBackground() {
                Log.d("event_debug", "Evento loadInBackground del loader principal lanzado");
                String weatherLocation = args.getString(WEATHER_LOCATION_EXTRA);

                if(weatherLocation == null || TextUtils.isEmpty(weatherLocation)) {
                    return null;
                }

                try {
                    String urlResults = NetworkUtils.getResponseFromHttpUrl(NetworkUtils.buildUrl(weatherLocation));
                    return OpenWeatherJsonUtils.getSimpleWeatherStringsFromJson(getContext(), urlResults);
                } catch(Exception ex) {
                    ex.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(String[] data) {
                weatherData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        Log.d("event_debug", "Evento onLoadFinished del loader principal lanzado");
        progressBar.setVisibility(ProgressBar.INVISIBLE);

        if(data != null && data.length > 0) {
            showWeatherDataView(data);
        } else {
            showErrorMessage();
        }
    }

    private void showWeatherDataView(String[] result)
    {
        weatherDisplay.setVisibility(TextView.VISIBLE);
        errorDisplay.setVisibility(TextView.INVISIBLE);

        forecastAdapter.setWeatherData(null);
        forecastAdapter.setWeatherData(result);
    }

    private void showErrorMessage()
    {
        weatherDisplay.setVisibility(TextView.INVISIBLE);
        errorDisplay.setVisibility(TextView.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {

    }
}