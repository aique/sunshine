package com.example.android.sunshine.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.MainActivity;
import com.example.android.sunshine.R;
import com.example.android.sunshine.data.provider.WeatherContract;
import com.example.android.sunshine.interfaces.ForecastListItemClickListener;
import com.example.android.sunshine.utilities.SunshineWeatherUtils;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    private Context mContext;
    private ForecastListItemClickListener clickHandler;
    private Cursor mCursor;

    // índices para cada uno de los campos dentro de la definición de la tabla
    public static final int INDEX_WEATHER_DATE = 0;

    public ForecastAdapter(Context mContext, ForecastListItemClickListener clickHandler) {
        this.mContext = mContext;
        this.clickHandler = clickHandler;
    }

    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.forecast_list_item, viewGroup, false);
        return new ForecastAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ForecastAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        holder.weatherIconImageView.setImageResource(SunshineWeatherUtils.getWeatherIconFromCursor(mCursor));
        holder.weatherDayTextView.setText(SunshineWeatherUtils.getWeatherDateFromCursor(mCursor, mContext));
        holder.weatherDescriptionTextView.setText(SunshineWeatherUtils.getWeatherDescriptionFromCursor(mCursor, mContext));
        holder.weatherMaxTextView.setText(SunshineWeatherUtils.getWeatherHighTemperatureFromCursor(mCursor));
        holder.weatherMinTextView.setText(SunshineWeatherUtils.getWeatherMinTemperatureFromCursor(mCursor));
    }

    @Override
    public int getItemCount() {
        if(mCursor != null) {
            return mCursor.getCount();
        }

        return 0;
    }

    public void setWeatherData(Cursor weatherData) {
        this.mCursor = weatherData;
        notifyDataSetChanged();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView weatherDayTextView;
        private TextView weatherDescriptionTextView;
        private TextView weatherMaxTextView;
        private TextView weatherMinTextView;
        private ImageView weatherIconImageView;

        public ForecastAdapterViewHolder(View view) {
            super(view);

            weatherDayTextView = (TextView) view.findViewById(R.id.tv_weather_item_day);
            weatherDescriptionTextView = (TextView) view.findViewById(R.id.tv_weather_item_weather);
            weatherMaxTextView = (TextView) view.findViewById(R.id.tv_weather_item_max);
            weatherMinTextView = (TextView) view.findViewById(R.id.tv_weather_item_min);
            weatherIconImageView = (ImageView) view.findViewById(R.id.iv_weather_item_icon);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            long dateInMillis = mCursor.getLong(INDEX_WEATHER_DATE);
            clickHandler.onClick(dateInMillis);
        }
    }
}
