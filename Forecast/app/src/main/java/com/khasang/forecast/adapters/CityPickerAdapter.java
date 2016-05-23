package com.khasang.forecast.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.khasang.forecast.MyApplication;
import com.khasang.forecast.R;
import com.khasang.forecast.position.PositionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CopyPasteStd on 29.11.15.
 * Адаптер для выбора карточек городов
 */
public class CityPickerAdapter extends HeaderFooterAdapter<String> {

    private List<String> newCities;
    private View.OnClickListener onClickListener;

    public CityPickerAdapter(List<String> itemList, View.OnClickListener onClickListener) {
        super(itemList);
        this.onClickListener = onClickListener;
        this.newCities = new ArrayList<>();
    }

    public void addCityToNewLocationsList(String city) {
        newCities.add(city);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder parentHolder = super.onCreateViewHolder(parent, viewType);
        if (parentHolder != null) {
            return parentHolder;
        } else {
            Context context = parent.getContext();
            View view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
            return new ViewHolder(view, onClickListener);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (!(isHeaderPosition(position) || isFooterPosition(position))) {
            ViewHolder holder = (ViewHolder) viewHolder;

            // Единицу отнимаем изза Footer элемента в родителе
            String itemText = itemList.get(position - 1);

            if (newCities.contains(itemText)) {
                holder.markCityAsNew(true);
            } else {
                holder.markCityAsNew(false);
            }
            holder.setItemFavoriteState(PositionManager.getInstance().isFavouriteCity(itemText));
            holder.setItemText(itemText);
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView itemTextView;
        private ImageButton itemImageButton;
        private CardView cardView;

        public ViewHolder(View itemView, View.OnClickListener listener) {
            super(itemView);
            cardView = (CardView) itemView;
            itemTextView = (TextView) cardView.findViewById(R.id.cityTW);
            itemImageButton = ((ImageButton) cardView.findViewById(R.id.starBtn));
            itemImageButton.setOnClickListener(listener);
            cardView.setOnClickListener(listener);
        }

        public void setItemText(CharSequence text) {
            itemTextView.setText(text);
        }

        public void setItemFavoriteState(boolean isFavorite) {
            int starImageRes = android.R.drawable.btn_star_big_off;
            if (isFavorite) {
                starImageRes = android.R.drawable.btn_star_big_on;
            }
            itemImageButton.setImageResource(starImageRes);
        }

        public void markCityAsNew(boolean mark) {
            if (mark) {
                cardView.setCardBackgroundColor(ContextCompat.getColor(MyApplication.getAppContext(), R.color.new_city_card));
            } else {
                cardView.setCardBackgroundColor(ContextCompat.getColor(MyApplication.getAppContext(), R.color.city_card_background));
            }
        }
    }
}