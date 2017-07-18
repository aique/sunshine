package com.example.android.sunshine.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.sunshine.data.SunshinePreferences;
import com.example.android.sunshine.data.provider.WeatherContract;
import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.NotificationUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;

import java.net.URL;

/**
 * Definición de la tarea de sincronización propiamente
 * dicha.
 *
 * Se utiliza tanto para actualizar el listado de
 * temperaturas inicialmente como para actualizar
 * la base de datos.
 */
public class SunshineSyncTask {
    private SunshinePreferences preferences;

    public static final String[] WEATHER_DETAIL_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };

    public SunshineSyncTask(SunshinePreferences preferences) {
        this.preferences = preferences;
    }

    synchronized public Cursor syncWeather(String weatherLocation, String units, Context context) {
        Log.d("sunshine_sync", "Location: " +  weatherLocation);
        Log.d("sunshine_sync", "Units: " +  units);

        if(weatherLocation == null || TextUtils.isEmpty(weatherLocation)) {
            return null;
        }

        if(units == null || TextUtils.isEmpty(units)) {
            return null;
        }

        try {
            URL weatherApiUrl = NetworkUtils.buildUrl(weatherLocation, units);

            Log.d("event_debug", "Realizando la consulta a la api: " + weatherApiUrl.toString());

            String urlResults = NetworkUtils.getResponseFromHttpUrl(weatherApiUrl);
            ContentValues[] weatherValues = OpenWeatherJsonUtils.getWeatherContentValuesFromJson(context, urlResults);

            if(weatherValues != null && weatherValues.length != 0) {
                ContentResolver sunshineContentResolver = context.getContentResolver();
                // se eliminan los datos existentes hasta el momento
                Log.d("sunshine_sync", "Eliminando los datos existentes en base de datos");
                sunshineContentResolver.delete(
                        WeatherContract.WeatherEntry.CONTENT_URI,
                        null,
                        null);

                Log.d("sunshine_sync", "Insertando los nuevos datos recibidos en base de datos");
                sunshineContentResolver.bulkInsert(
                        WeatherContract.WeatherEntry.CONTENT_URI,
                        weatherValues);

                NotificationUtils.notifyUserOfNewWeather(context);

                Log.d("sunshine_sync", "Devolviendo la información actual en base de datos");
                return context.getContentResolver().query(
                        WeatherContract.WeatherEntry.CONTENT_URI,
                        WEATHER_DETAIL_PROJECTION,
                        null,
                        null,
                        null);
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
