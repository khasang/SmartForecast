package com.khasang.forecast.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.khasang.forecast.AppUtils;
import com.khasang.forecast.R;
import com.khasang.forecast.position.PositionManager;
import com.khasang.forecast.position.Weather;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aleksandrlihovidov on 03.12.15.
 * Адаптер отображения карточек
 * на конкрентное время
 * и конкретнкую дату
 */
public class WeatherAdapter extends HeaderFooterAdapter<Weather> {

    private List<String> dateTimeList;

    public WeatherAdapter(ArrayList<String> dateTimeList, ArrayList<Weather> dataset) {
        super(dataset);
        this.dateTimeList = dateTimeList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder parentHolder = super.onCreateViewHolder(parent, viewType);
        if (parentHolder != null) {
            return parentHolder;
        } else {
            Context context = parent.getContext();
            View v = LayoutInflater.from(context).inflate(R.layout.list_item_forecast, parent, false);
            return new ViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (!(isHeaderPosition(position) || isFooterPosition(position))) {
            ViewHolder holder = (ViewHolder) viewHolder;

            // Единицу отнимаем изза Footer элемента в родителе
            String dateTime = dateTimeList.get(position - 1);
            Weather weather = itemList.get(position - 1);

            holder.tvDayOfWeekOrTime.setText(dateTime);

            int res = (int) (weather.getTemperature() + 0.5); // Округляем температуру
            String tvTemperature = String.format(res == 0 ? "%d" : "%+d", res);
            holder.tvTemperature.setText(tvTemperature);

            String temperatureMetric = PositionManager.getInstance().getTemperatureMetric().toStringValue();
            holder.tvTempUnit.setText(temperatureMetric);

            int iconId = weather.getPrecipitation().getIconResId(AppUtils.isDayFromString(dateTime));
            holder.ivWeatherIcon.setImageResource(iconId == 0 ? R.mipmap.ic_launcher : iconId);

            String description = weather.getDescription();
            String capitalizedDescription = description.substring(0, 1).toUpperCase() + description.substring(1);
            holder.tvWeatherDescription.setText(capitalizedDescription);
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvDayOfWeekOrTime;
        private ImageView ivWeatherIcon;
        private TextView tvTemperature;
        private TextView tvWeatherDescription;
        private TextView tvTempUnit;

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
