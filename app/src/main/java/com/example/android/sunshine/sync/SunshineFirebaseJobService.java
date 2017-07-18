package com.example.android.sunshine.sync;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.sunshine.data.SunshinePreferences;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Clase que contiene el código que ejecutará el job.
 *
 * Se utiliza una AsyncTask para realizar una actividad
 * cuyo punto de ejecución será en segundo plano (conexión
 * a internet, acceso a base de datos... etc)
 */
public class SunshineFirebaseJobService extends JobService {
    private AsyncTask<Void, Void, Void> mFetchWeatherTask;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        Log.d("sunshine_sync", "Arrancando el job de sincronización");

        mFetchWeatherTask = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                Context context = getApplicationContext();
                SunshinePreferences preferences = new SunshinePreferences(context);
                SunshineSyncTask syncTask = new SunshineSyncTask(preferences);

                syncTask.syncWeather(
                        preferences.getPreferredWeatherLocation(),
                        preferences.getTemperatureUnits(),
                        context);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(jobParameters, false);
            }
        };

        mFetchWeatherTask.execute();

        return true; // al devolver true se indica que si falla, se desea reinicialización
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
