package com.example.android.sunshine.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.sunshine.R;
import com.example.android.sunshine.interfaces.ForecastListItemClickListener;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    private String[] weatherData;
    private ForecastListItemClickListener clickHandler;

    public ForecastAdapter(ForecastListItemClickListener clickHandler) {
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
        holder.weatherTextView.setText(weatherData[position]);
    }

    @Override
    public int getItemCount() {
        if(weatherData == null) {
            return 0;
        } else {
            return weatherData.length;
        }
    }

    public void setWeatherData(String[] weatherData) {
        this.weatherData = weatherData;
        notifyDataSetChanged();
    }

    class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView weatherTextView;

        public ForecastAdapterViewHolder(View view) {
            super(view);
            weatherTextView = (TextView) view.findViewById(R.id.tv_weather_item);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String weatherForDay = weatherData[adapterPosition];
            clickHandler.onClick(weatherForDay);
        }
    }
}
