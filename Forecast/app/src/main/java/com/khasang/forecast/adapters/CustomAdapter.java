package com.khasang.forecast.adapters;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.khasang.forecast.DrawUtils;
import com.khasang.forecast.R;
import com.khasang.forecast.Weather;

import java.util.ArrayList;

/**
 * Created by aleksandrlihovidov on 03.12.15.
 * Адаптер отображения карточек
 * на конкрентное время
 * и конкретнкую дату
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder>{
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
                .inflate(R.layout.recycler_item_view, parent, false);

        if (utils.getWidthDpx() > 640f) {
            width = utils.getWidthPx() / dataset.size();
            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMargins(4, 4, 4, 4);
            v.setLayoutParams(params);
        }

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
// TODO: добавить заполнение иконки и т.д.
        String dayOfWeek = dateTimeList.get(position);
        holder.tvDayOfWeekOrTime.setText(dayOfWeek);
        int res = (int) (dataset.get(position).getTemperature() + 0.5);
        String tvTemperature = String.format(res == 0 ? "%d" : "%+d", res);
        holder.tvTemperature.setText(tvTemperature);

        int iconId = dataset.get(position).getPrecipitation().getIconResId();
        holder.ivWeatherIcon.setImageResource(iconId == 0 ? R.mipmap.ic_launcher : iconId);

    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayOfWeekOrTime;
        ImageView ivWeatherIcon;
        TextView tvTemperature;

        public ViewHolder(View itemView) {
            super(itemView);

            tvDayOfWeekOrTime = (TextView) itemView.findViewById(R.id.tvDayOfWeek);
            ivWeatherIcon = (ImageView) itemView.findViewById(R.id.ivWeatherIcon);
            tvTemperature = (TextView) itemView.findViewById(R.id.tvTemperature);
        }
    }
}
