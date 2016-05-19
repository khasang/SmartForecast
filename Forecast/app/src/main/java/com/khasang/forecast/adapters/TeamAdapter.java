package com.khasang.forecast.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.khasang.forecast.R;
import com.khasang.forecast.models.Developer;

import java.util.List;

public class TeamAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Developer> developers;

    public TeamAdapter(List<Developer> developers) {
        this.developers = developers;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.item_developer, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return developers.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;

        Developer developer = developers.get(position);

        holder.nameView.setText(developer.getName());
        holder.descriptionView.setText(developer.getDescription());
        holder.imageView.setImageResource(developer.getResId());
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nameView;
        private TextView descriptionView;
        private ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);

            nameView = (TextView) itemView.findViewById(R.id.name);
            descriptionView = (TextView) itemView.findViewById(R.id.description);
            imageView = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}
