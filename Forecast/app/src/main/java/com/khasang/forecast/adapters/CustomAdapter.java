package com.khasang.forecast.adapters;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.khasang.forecast.AppUtils;
import com.khasang.forecast.DrawUtils;
import com.khasang.forecast.R;
import com.khasang.forecast.position.PositionManager;
import com.khasang.forecast.position.Weather;

import java.util.ArrayList;

/**
 * Created by aleksandrlihovidov on 03.12.15.
 * Адаптер отображения карточек
 * на конкрентное время
 * и конкретнкую дату
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private final String TAG = this.getClass().getSimpleName();

    private final DrawUtils utils;
    private ArrayList<String> dateTimeList;
    private ArrayList<Weather> dataset;
    private int width;

    public CustomAdapter(ArrayList<String> dateTimeList, ArrayList<Weather> dataset) {
        this.dateTimeList = dateTimeList;
        this.dataset = dataset;
        utils = DrawUtils.getInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_forecast, parent, false);

//        if (utils.getWidthDpx() > 640f) {
//            width = utils.getWidthPx() / dataset.size();
//            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
//            params.setMargins(4, 4, 4, 4);
//            v.setLayoutParams(params);
//        }

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String dayOfWeek = dateTimeList.get(position);
        holder.tvDayOfWeekOrTime.setText(dayOfWeek);
        int res = (int) (dataset.get(position).getTemperature() + 0.5);
        String tvTemperature = String.format(res == 0 ? "%d" : "%+d", res);
        holder.tvTemperature.setText(tvTemperature);
        holder.tvTempUnit.setText(PositionManager.getInstance().getTemperatureMetric().toStringValue());
        Drawable weatherIcon = PositionManager.getInstance().getWeatherIcon(dataset.get(position).getPrecipitation().getIconNumber(AppUtils.isDayFromString(dayOfWeek)));
        holder.ivWeatherIcon.setImageDrawable(weatherIcon);
        String description = dataset.get(position).getDescription();
        String capitalizedDescription = description.substring(0, 1).toUpperCase() + description.substring(1);
        holder.tvWeatherDescription.setText(capitalizedDescription);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayOfWeekOrTime;
        ImageView ivWeatherIcon;
        TextView tvTemperature;
        TextView tvWeatherDescription;
        TextView tvTempUnit;

        public ViewHolder(View itemView) {
            super(itemView);

            tvDayOfWeekOrTime = (TextView) itemView.findViewById(R.id.tv_day_of_week);
            ivWeatherIcon = (ImageView) itemView.findViewById(R.id.iv_weather_icon);
            tvTemperature = (TextView) itemView.findViewById(R.id.tv_temperature);
            tvWeatherDescription = (TextView) itemView.findViewById(R.id.tv_weather_description);
            tvTempUnit = (TextView) itemView.findViewById(R.id.tv_temp_unit);
        }
    }
}
