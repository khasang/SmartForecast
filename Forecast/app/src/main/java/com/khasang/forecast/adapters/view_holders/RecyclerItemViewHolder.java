package com.khasang.forecast.adapters.view_holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.khasang.forecast.R;

/**
 * Created by CopyPasteStd on 29.11.15.
 */
public class RecyclerItemViewHolder extends RecyclerView.ViewHolder {
    private final TextView mItemTextView;

    public RecyclerItemViewHolder(final View parent, TextView itemTextView) {
        super(parent);
        mItemTextView = itemTextView;
    }

     public RecyclerItemViewHolder (View itemView, View.OnClickListener listener, View.OnLongClickListener longListener) {
         super(itemView);
         mItemTextView = (TextView) itemView.findViewById(R.id.itemTextView);
         itemView.setOnClickListener(listener);
         itemView.setOnLongClickListener(longListener);
     }


    public void setItemText(CharSequence text) {
        mItemTextView.setText(text);
    }
}
