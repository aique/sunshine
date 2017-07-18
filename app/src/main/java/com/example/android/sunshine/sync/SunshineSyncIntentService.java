package com.example.android.sunshine.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.example.android.sunshine.data.SunshinePreferences;

/**
 * Clase que simplemente lanza el proceso de sincronización.
 *
 * Se ha creado con el objetivo de poder ser lanzado desde
 * un IntentService, idóneo para procesos que se ejecutan
 * en segundo plano, incluso si la aplicación se cierra,
 * y que tienen un tiempo de ejecución finito, no han de
 * estar corriendo permanentemente en segundo plano.
 */
public class SunshineSyncIntentService extends IntentService {
    public SunshineSyncIntentService() {
        super("SunshineSyncIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        SunshinePreferences preferences = new SunshinePreferences(this);
        SunshineSyncTask syncTask = new SunshineSyncTask(preferences);
        syncTask.syncWeather(
                preferences.getDefaultWeatherLocation(),
                preferences.getTemperatureUnits(),
                this);
    }
}
