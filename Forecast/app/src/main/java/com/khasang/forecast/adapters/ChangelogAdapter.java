package com.khasang.forecast.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.khasang.forecast.R;
import com.khasang.forecast.activities.FullImageActivity;
import com.khasang.forecast.models.Changelog;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.List;

public class ChangelogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Changelog> changelogs;

    public ChangelogAdapter(Context context, List<Changelog> changelogs) {
        this.context = context;
        this.changelogs = changelogs;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.item_changelog, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return changelogs.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        Changelog changelog = changelogs.get(position);

        String version = changelog.getVersion() +
                " (" + DateFormat.getDateInstance().format(changelog.getDate()) + ")";

        ViewGroup.LayoutParams params = holder.imageView.getLayoutParams();

        final String url = changelog.getImageUrl();
        final int imageWidth = changelog.getImageWidth();
        final int imageHeight = changelog.getImageHeight();

        int imageViewHeight = params.height;
        int imageViewWidth = imageViewHeight * imageWidth / imageHeight;

        holder.versionView.setText(version);
        Picasso.with(context)
                .load(url)
                .resize(imageViewWidth, imageViewHeight)
                .into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FullImageActivity.class);
                intent.putExtra(FullImageActivity.URL, url);
                intent.putExtra(FullImageActivity.IMAGE_WIDTH, imageWidth);
                intent.putExtra(FullImageActivity.IMAGE_HEIGHT, imageHeight);
                context.startActivity(intent);
            }
        });

        holder.changesView.setText(changelog.getChangesRes());
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView versionView;
        private ImageView imageView;
        private TextView changesView;

        public ViewHolder(View itemView) {
            super(itemView);

            versionView = (TextView) itemView.findViewById(R.id.version);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            changesView = (TextView) itemView.findViewById(R.id.changes);
        }
    }
}
