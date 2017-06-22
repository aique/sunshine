package com.example.android.sunshine;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    private TextView weatherDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("event_debug", "Creando la actividad DetailActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        weatherDetail = (TextView) findViewById(R.id.tv_weather_detail);

        Intent intent = getIntent();

        if(intent != null) {
            if(getIntent().hasExtra(Intent.EXTRA_TEXT)) {
                String weatherDetailText = getIntent().getStringExtra(Intent.EXTRA_TEXT);
                weatherDetail.setText(weatherDetailText);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                shareWeatherDetail(weatherDetail.getText().toString());
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
