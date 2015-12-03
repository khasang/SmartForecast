package com.khasang.forecast.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.khasang.forecast.R;
import com.khasang.forecast.Weather;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aleksandrlihovidov on 03.12.15.
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder>{
//    private String[] dataset;
    private ArrayList<String> dateTimeList;
    private ArrayList<Weather> dataset;

//    public CustomAdapter(String[] dataset) {
    public CustomAdapter(ArrayList<String> dateTimeList, ArrayList<Weather> dataset) {
        this.dateTimeList = dateTimeList;
        this.dataset = dataset;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item_view, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        holder.tvDayOfWeek.setText(dataset[position]);
// TODO: добавить заполнение иконки и т.д.
        String dayOfWeek = dateTimeList.get(position);
        holder.tvDayOfWeek.setText(dayOfWeek);
//        String dayOfWeek = dateTimeList.get(position);
//        holder.tvDayOfWeek.setText(dayOfWeek);
        String tvTemperature = String.valueOf(dataset.get(position).getTemperature());
        holder.tvTemperature.setText(tvTemperature);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayOfWeek;
        ImageView ivWeatherIcon;
//        TextView tvTemperatureDay;
//        TextView tvTemperatureNight;
        TextView tvTemperature;

        public ViewHolder(View itemView) {
            super(itemView);

            tvDayOfWeek = (TextView) itemView.findViewById(R.id.tvDayOfWeek);
            ivWeatherIcon = (ImageView) itemView.findViewById(R.id.ivWeatherIcon);
            tvTemperature = (TextView) itemView.findViewById(R.id.tvTemperature);
        }

        TextView getTextView() {
            return tvDayOfWeek;
        }
    }
}
