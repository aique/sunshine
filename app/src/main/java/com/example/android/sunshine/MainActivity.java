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

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sunshine.adapters.ForecastAdapter;
import com.example.android.sunshine.data.SunshinePreferences;
import com.example.android.sunshine.interfaces.ForecastListItemClickListener;
import com.example.android.sunshine.tasks.NetworkRequestTask;

public class MainActivity extends AppCompatActivity implements ForecastListItemClickListener {

    private RecyclerView weatherDisplay;
    private ForecastAdapter forecastAdapter;
    private TextView errorDisplay;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        new NetworkRequestTask(this, weatherDisplay, forecastAdapter, errorDisplay, progressBar)
                .execute(SunshinePreferences.getPreferredWeatherLocation(this));
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
                new NetworkRequestTask(this, weatherDisplay, forecastAdapter, errorDisplay, progressBar)
                        .execute(SunshinePreferences.getPreferredWeatherLocation(this));
                break;
        }

        return true;
    }

    @Override
    public void onClick(String weatherForToday) {
        Context context = this;
        Toast.makeText(context, weatherForToday, Toast.LENGTH_SHORT).show();
    }
}