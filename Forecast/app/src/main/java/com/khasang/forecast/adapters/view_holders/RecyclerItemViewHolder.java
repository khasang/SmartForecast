package com.khasang.forecast.adapters.view_holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.khasang.forecast.R;

/**
 * Created by CopyPasteStd on 29.11.15.
 */
public class RecyclerItemViewHolder extends RecyclerView.ViewHolder {
    private final TextView mItemTextView;

    //private final ImageButton mItemImageButton;



    //TODO DELETE
   /* public RecyclerItemViewHolder(final View parent, TextView itemTextView, ImageButton imageButton) {
        super(parent);
        mItemTextView = itemTextView;
        mItemImageButton = imageButton;
    }*/

    public RecyclerItemViewHolder (View itemView, View.OnClickListener listener, View.OnLongClickListener longListener) {
        super(itemView);

      /*  mItemTextView = (TextView) itemView.findViewById(R.id.cityTW);
        mItemImageButton = (ImageButton) itemView.findViewById(R.id.starBtn);
        ((ImageView) itemView.findViewById(R.id.starBtn)).setOnClickListener(listener);
        itemView.setOnClickListener(listener);
        itemView.setOnLongClickListener(longListener);*/

        mItemTextView = (TextView) itemView.findViewById(R.id.cityTW);
        itemView.findViewById(R.id.starBtn).setOnClickListener(listener);
        itemView.setOnClickListener(listener);
        itemView.setOnLongClickListener(longListener);
/*=======
        mItemTextView = (TextView) itemView.findViewById(R.id.cityTW);
        ((ImageView) itemView.findViewById(R.id.starBtn)).setOnClickListener(listener);
>>>>>>> 8ffed8c0f25547abc22b8d6cfe5906c61fe6ab35
        itemView.setOnClickListener(listener);
        itemView.setOnLongClickListener(longListener);*/
    }


    public void setItemText(CharSequence text) {
        mItemTextView.setText(text);
    }

}
