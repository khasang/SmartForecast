package com.khasang.forecast.adapters.view_holders;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.khasang.forecast.MyApplication;
import com.khasang.forecast.R;

/**
 * Created by CopyPasteStd on 29.11.15.
 */
public class RecyclerItemViewHolder extends RecyclerView.ViewHolder {
    private final TextView mItemTextView;
    private final ImageButton mItemImageButton;
    View cardView;


    public RecyclerItemViewHolder(View itemView, View.OnClickListener listener, View.OnLongClickListener longListener) {
        super(itemView);
        cardView = itemView;
        mItemTextView = (TextView) cardView.findViewById(R.id.cityTW);
        mItemImageButton = ((ImageButton) cardView.findViewById(R.id.starBtn));
        mItemImageButton.setOnClickListener(listener);
        cardView.setOnClickListener(listener);
        cardView.setOnLongClickListener(longListener);
    }


    public void setItemText(CharSequence text) {
        mItemTextView.setText(text);
    }

    public void setItemFavoriteState(boolean isFavorite) {
        int starImageRes = android.R.drawable.btn_star_big_off;
        if (isFavorite) {
            starImageRes = android.R.drawable.btn_star_big_on;
        }
        mItemImageButton.setImageResource(starImageRes);
    }

    public void markCityAsNew(boolean mark) {
        if (mark) {
            cardView.setBackgroundColor(ContextCompat.getColor(MyApplication.getAppContext(), R.color.md_amber_50));
        } else {
            cardView.setBackgroundColor(Color.WHITE);
        }
    }

}
