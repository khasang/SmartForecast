package com.khasang.forecast.adapters.view_holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.khasang.forecast.R;

/**
 * Created by CopyPasteStd on 29.11.15.
 */
public class RecyclerItemViewHolder extends RecyclerView.ViewHolder {
    private final TextView mItemTextView;
    private final ImageButton mItemImageButton;


    public RecyclerItemViewHolder(View itemView, View.OnClickListener listener, View.OnLongClickListener longListener) {
        super(itemView);
        mItemTextView = (TextView) itemView.findViewById(R.id.cityTW);
        mItemImageButton = ((ImageButton) itemView.findViewById(R.id.starBtn));
        mItemImageButton.setOnClickListener(listener);
        itemView.setOnClickListener(listener);
        itemView.setOnLongClickListener(longListener);
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

}
