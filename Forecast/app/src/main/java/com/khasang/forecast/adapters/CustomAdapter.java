package com.khasang.forecast.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.khasang.forecast.AppUtils;
import com.khasang.forecast.R;
import com.khasang.forecast.adapters.view_holders.RecyclerEmptyViewHolder;
import com.khasang.forecast.position.PositionManager;
import com.khasang.forecast.position.Weather;
import java.util.ArrayList;

/**
 * Created by aleksandrlihovidov on 03.12.15.
 * Адаптер отображения карточек
 * на конкрентное время
 * и конкретнкую дату
 */
public class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = this.getClass().getSimpleName();

    private ArrayList<String> dateTimeList;
    private ArrayList<Weather> dataset;

    private float headerHeight;
    private float footerHeight;

    private enum ItemType {
        CARD_VIEW {
            @Override
            public int number() {
                return 1;
            }
        },
        HEADER {
            @Override
            public int number() {
                return 2;
            }
        },
        FOOTER {
            @Override
            public int number() {
                return 3;
            }
        };

        public abstract int number();
    }

    public CustomAdapter(ArrayList<String> dateTimeList, ArrayList<Weather> dataset) {
        this.dateTimeList = dateTimeList;
        this.dataset = dataset;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ItemType.HEADER.number()) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_header, parent, false);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.height = (int) headerHeight;
            view.requestLayout();
            return new RecyclerEmptyViewHolder(view);
        } else if (viewType == ItemType.FOOTER.number()) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_footer, parent, false);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.height = (int) footerHeight;
            view.requestLayout();
            return new RecyclerEmptyViewHolder(view);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_forecast, parent, false);

            return new ViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (isPositionHeader(position)) {

        } else if (isLastPosition(position)) {

        } else {
            ViewHolder holder = (ViewHolder) viewHolder;

            String dayOfWeek = dateTimeList.get(position - 1);
            holder.tvDayOfWeekOrTime.setText(dayOfWeek);
            int res = (int) (dataset.get(position - 1).getTemperature() + 0.5);
            String tvTemperature = String.format(res == 0 ? "%d" : "%+d", res);
            holder.tvTemperature.setText(tvTemperature);
            holder.tvTempUnit.setText(PositionManager.getInstance().getTemperatureMetric().toStringValue());
            int iconId = dataset.get(position - 1).getPrecipitation().getIconResId(AppUtils.isDayFromString(dayOfWeek));
            holder.ivWeatherIcon.setImageResource(iconId == 0 ? R.mipmap.ic_launcher : iconId);
            String description = dataset.get(position - 1).getDescription();
            String capitalizedDescription = description.substring(0, 1).toUpperCase() + description.substring(1);
            holder.tvWeatherDescription.setText(capitalizedDescription);
        }
    }

    public int getBasicItemCount() {
        return dataset.size();
    }

    //our new getItemCount() that includes header View
    @Override
    public int getItemCount() {
        return getBasicItemCount() + 1 + 1; // header
    }


    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return ItemType.HEADER.number();
        }
        if (isLastPosition(position)) {
            return ItemType.FOOTER.number();
        }
        return ItemType.CARD_VIEW.number();
    }

    // check if given position is a header
    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private boolean isLastPosition(int position) {
        return position == getItemCount() - 1;
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

    public void setHeaderHeight(float headerHeight) {
        this.headerHeight = headerHeight;
    }

    public void setFooterHeight(float footerHeight) {
        this.footerHeight = footerHeight;
    }
}
