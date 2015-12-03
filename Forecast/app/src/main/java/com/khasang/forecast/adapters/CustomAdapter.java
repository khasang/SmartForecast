package com.khasang.forecast.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.khasang.forecast.R;

/**
 * Created by aleksandrlihovidov on 03.12.15.
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder>{
    private String[] dataset;

    public CustomAdapter(String[] dataset) {
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
        holder.tvDayOfWeek.setText(dataset[position]);
    }

    @Override
    public int getItemCount() {
        return dataset.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayOfWeek;

        public ViewHolder(View itemView) {
            super(itemView);

            tvDayOfWeek = (TextView) itemView.findViewById(R.id.tvDayOfWeek);
        }

        TextView getTextView() {
            return tvDayOfWeek;
        }
    }
}
