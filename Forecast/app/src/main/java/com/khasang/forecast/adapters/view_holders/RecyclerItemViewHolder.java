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

    public static RecyclerItemViewHolder newInstance(View parent) {
        TextView itemTextView = (TextView) parent.findViewById(R.id.itemTextView);
        itemTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //TODO http://stackoverflow.com/questions/32473804/how-to-get-the-position-of-cardview-item-in-recyclerview
                    Toast.makeText(view.getContext(), "Click", Toast.LENGTH_SHORT).show();
            }
        });
        return new RecyclerItemViewHolder(parent, itemTextView);
    }

    public void setItemText(CharSequence text) {
        mItemTextView.setText(text);
    }
}
