package com.khasang.forecast.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    private String[] wallpapers = {
            "https://github.com/khasang/SmartForecast/blob/feature/AboutActivity/Auxiliary_files/Wallpapers/field" +
                    ".jpg?raw=true",
            "https://github.com/khasang/SmartForecast/blob/feature/AboutActivity/Auxiliary_files/Wallpapers/fog" +
                    ".jpg?raw=true",
            "https://github.com/khasang/SmartForecast/blob/feature/AboutActivity/Auxiliary_files/Wallpapers/green_field.jpg?raw=true",
            "https://github.com/khasang/SmartForecast/blob/feature/AboutActivity/Auxiliary_files/Wallpapers/rain.jpg?raw=true"
    };

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
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;

        Developer developer = developers.get(position);

        holder.nameView.setText(developer.getNameResId());
        holder.descriptionView.setText(developer.getDescriptionResId());
        ViewGroup.LayoutParams imageLp = holder.imageView.getLayoutParams();
//        ViewGroup.LayoutParams wallpaperLp = holder.wallpaper.getLayoutParams();
        String url = developer.getImage().getUrl();

        Picasso.with(context)
                .load(wallpapers[position % wallpapers.length])
                .error(R.drawable.field)
                .placeholder(R.drawable.field)
                .fit()

                .into(holder.wallpaper);

        Picasso.with(context)
                .load(url)
                .placeholder(R.drawable.ic_perm_identity_black_24dp)
                .error(R.drawable.ic_perm_identity_black_24dp)
                .resize(imageLp.width, imageLp.height)
                .into(holder.imageView);

        List<Link> links = developer.getLinks();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        params.width = 0;
        holder.links.removeAllViews();
        Typeface typeface = Typeface.create("sans-serif-regular", Typeface.BOLD);
        int buttonPadding = context.getResources().getDimensionPixelSize(R.dimen.social_button_padding);
        for (final Link link : links) {

            Button socialButton;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                socialButton = new Button(context, null, 0, R.style.social_button_style);
            } else {
                socialButton = new Button(context);
                socialButton.setBackgroundColor(Color.WHITE);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                socialButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
            socialButton.setLayoutParams(params);
            socialButton.setText(link.getTitle());
            socialButton.setPadding(buttonPadding, 0, buttonPadding, 0);
            socialButton.setTypeface(typeface);
            socialButton.setTextColor(ContextCompat.getColor(context, R.color.accent));
            socialButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link.getUrl())));
                }
            });
            holder.links.addView(socialButton);
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nameView;
        private TextView descriptionView;
        private CircleImageView imageView;
        private LinearLayout links;
        private ImageView wallpaper;

        public ViewHolder(View itemView) {
            super(itemView);
            wallpaper = (ImageView) itemView.findViewById(R.id.wallpaper);
            nameView = (TextView) itemView.findViewById(R.id.name);
            descriptionView = (TextView) itemView.findViewById(R.id.description);
            imageView = (CircleImageView) itemView.findViewById(R.id.image);
            links = (LinearLayout) itemView.findViewById(R.id.links);
        }
    }
}
