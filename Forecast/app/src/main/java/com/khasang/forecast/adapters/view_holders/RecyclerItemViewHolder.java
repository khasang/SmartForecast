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

   /* public static RecyclerItemViewHolder newInstance(View parent) {
        final TextView itemTextView = (TextView) parent.findViewById(R.id.itemTextView);
        itemTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //TODO http://stackoverflow.com/questions/32473804/how-to-get-the-position-of-cardview-item-in-recyclerview
                itemTextView.getText();
                String ID = String.valueOf(view.getId());
                //Toast.makeText(view.getContext(), "Click on city: " + itemTextView.getText() + " ID:" + ID, Toast.LENGTH_SHORT).show();


            }
        });
        return new RecyclerItemViewHolder(parent, itemTextView);
    }*/

     public RecyclerItemViewHolder (View itemView, View.OnClickListener listener) {
         super(itemView);
         mItemTextView = (TextView) itemView.findViewById(R.id.itemTextView);
         itemView.setOnClickListener(listener);
     }


    public void setItemText(CharSequence text) {
        mItemTextView.setText(text);
    }
}
