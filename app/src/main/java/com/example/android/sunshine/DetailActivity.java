package com.example.android.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.data.provider.WeatherContract;
import com.example.android.sunshine.utilities.SunshineDateUtils;
import com.example.android.sunshine.utilities.SunshineWeatherUtils;

public class DetailActivity extends AppCompatActivity {

    private TextView weatherDetailDate;
    private TextView weatherDetailDescription;
    private TextView weatherDetailMax;
    private TextView weatherDetailMin;
    private TextView weatherDetailHumidity;
    private TextView weatherDetailPressure;
    private TextView weatherDetailWind;
    private ImageView weatherDetailIcon;

    private Uri queryWeatherByDateUri;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("event_debug", "Creando la actividad DetailActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        weatherDetailDate = (TextView) findViewById(R.id.tv_weather_detail_date);
        weatherDetailDescription = (TextView) findViewById(R.id.tv_weather_detail_description);
        weatherDetailMax = (TextView) findViewById(R.id.tv_weather_detail_max);
        weatherDetailMin = (TextView) findViewById(R.id.tv_weather_detail_min);
        weatherDetailHumidity = (TextView) findViewById(R.id.tv_weather_detail_humidity);
        weatherDetailPressure = (TextView) findViewById(R.id.tv_weather_detail_pressure);
        weatherDetailWind = (TextView) findViewById(R.id.tv_weather_detail_wind);
        weatherDetailIcon = (ImageView) findViewById(R.id.iv_weather_icon);

        Intent intent = getIntent();

        if(intent != null) {
            queryWeatherByDateUri = getIntent().getData();
            if(queryWeatherByDateUri == null) {
                throw new NullPointerException("URI for DetailActivity cannot be null");
            }

            Cursor cursor = getContentResolver().query(
                    queryWeatherByDateUri,
                    WEATHER_DETAIL_PROJECTION,
                    null,
                    null,
                    null);

            // TODO crear un else y mostrar un mensaje de error en caso de no recibir la información
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();

                weatherDetailDate.setText(SunshineWeatherUtils.getWeatherDateFromCursor(cursor, this));
                weatherDetailDescription.setText(SunshineWeatherUtils.getWeatherDescriptionFromCursor(cursor, this));
                weatherDetailMax.setText(SunshineWeatherUtils.getWeatherHighTemperatureFromCursor(cursor));
                weatherDetailMin.setText(SunshineWeatherUtils.getWeatherMinTemperatureFromCursor(cursor));
                weatherDetailIcon.setImageResource(SunshineWeatherUtils.getWeatherIconFromCursor(cursor));
                weatherDetailHumidity.setText(SunshineWeatherUtils.getHumidityFromCursor(cursor));
                weatherDetailPressure.setText(SunshineWeatherUtils.getPressureFromCursor(cursor));
                weatherDetailWind.setText(SunshineWeatherUtils.getWindSpeedFromCursor(cursor));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                shareWeatherDetail(""); // TODO método que devuelva la previsión como String
                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void shareWeatherDetail(String weatherDetail) {
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(weatherDetail)
                .getIntent();

        if (shareIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(shareIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }
}
