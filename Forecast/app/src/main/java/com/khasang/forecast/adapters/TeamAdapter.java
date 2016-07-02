package com.khasang.forecast.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.khasang.forecast.R;
import com.khasang.forecast.models.Developer;
import com.khasang.forecast.models.Link;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeamAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Developer> developers;

    public TeamAdapter(Context context, List<Developer> developers) {
        this.context = context;
        this.developers = developers;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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

        holder.wallpaper.setBackgroundResource(R.drawable.header_day_green);
        Picasso.with(context).load("")
        holder.nameView.setText(developer.getNameResId());
        holder.descriptionView.setText(developer.getDescriptionResId());

        ViewGroup.LayoutParams layoutParams = holder.imageView.getLayoutParams();
        String url = developer.getImage().getUrl();
        Picasso.with(context)
                .load(url)
                .resize(layoutParams.width, layoutParams.height)
                .into(holder.imageView);

        List<Link> links = developer.getLinks();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        holder.links.removeAllViews();
        for (final Link link : links) {
            TextView textView = new TextView(context);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setLayoutParams(params);
            textView.setText(link.getTitle());
            textView.setTypeface(null, Typeface.BOLD);
            textView.setTextColor(ContextCompat.getColor(context, R.color.accent));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link.getUrl())));
                }
            });
            holder.links.addView(textView);
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nameView;
        private TextView descriptionView;
        private CircleImageView imageView;
        private LinearLayout links;
        private RelativeLayout wallpaper;

        public ViewHolder(View itemView) {
            super(itemView);
            wallpaper = (RelativeLayout) itemView.findViewById(R.id.wallpaper);
            nameView = (TextView) itemView.findViewById(R.id.name);
            descriptionView = (TextView) itemView.findViewById(R.id.description);
            imageView = (CircleImageView) itemView.findViewById(R.id.image);
            links = (LinearLayout) itemView.findViewById(R.id.links);

            imageView.setBorderWidth(0);
        }
    }
}
