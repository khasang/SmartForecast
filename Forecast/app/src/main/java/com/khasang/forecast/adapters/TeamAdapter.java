package com.khasang.forecast.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.khasang.forecast.R;
import com.khasang.forecast.models.Developer;
import com.khasang.forecast.models.Link;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeamAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Developer> developers;
    private int wallpaperWidth;
    private int wallpaperHeight;

    private String[] wallpapers = {
            "https://raw.githubusercontent.com/khasang/SmartForecast/main-develop/Auxiliary_files/Wallpapers/cloudy_day.jpg",
            "https://raw.githubusercontent.com/khasang/SmartForecast/main-develop/Auxiliary_files/Wallpapers/5657821.jpg",
            "https://raw.githubusercontent.com/khasang/SmartForecast/main-develop/Auxiliary_files/Wallpapers/odinokoe-derevo.jpg",
            "https://raw.githubusercontent.com/khasang/SmartForecast/main-develop/Auxiliary_files/Wallpapers/gardex.jpg",
            "https://raw.githubusercontent.com/khasang/SmartForecast/main-develop/Auxiliary_files/Wallpapers/cloudy-sea.jpg",
            "https://raw.githubusercontent.com/khasang/SmartForecast/main-develop/Auxiliary_files/Wallpapers/forest.jpg",
            "https://raw.githubusercontent.com/khasang/SmartForecast/main-develop/Auxiliary_files/Wallpapers/tornado.jpg",
            "https://raw.githubusercontent.com/khasang/SmartForecast/main-develop/Auxiliary_files/Wallpapers/rain.jpg",
            "https://raw.githubusercontent.com/khasang/SmartForecast/main-develop/Auxiliary_files/Wallpapers/field.jpg",
            "https://raw.githubusercontent.com/khasang/SmartForecast/main-develop/Auxiliary_files/Wallpapers/fog.jpg",

            "https://raw.githubusercontent.com/khasang/SmartForecast/main-develop/Auxiliary_files/Wallpapers/green_field.jpg",
            "https://raw.githubusercontent.com/khasang/SmartForecast/main-develop/Auxiliary_files/Wallpapers/pole_leto.jpg",
            "https://raw.githubusercontent.com/khasang/SmartForecast/main-develop/Auxiliary_files/Wallpapers/summer-day.jpg",
            "https://raw.githubusercontent.com/khasang/SmartForecast/main-develop/Auxiliary_files/Wallpapers/sun_flowers.jpg",
            "https://raw.githubusercontent.com/khasang/SmartForecast/main-develop/Auxiliary_files/Wallpapers/unnamed.jpg",
            "https://raw.githubusercontent.com/khasang/SmartForecast/main-develop/Auxiliary_files/Wallpapers/917797.jpg"
    };

    public TeamAdapter(Context context, List<Developer> developers) {
        this.context = context;
        this.developers = developers;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;

        int margin = context.getResources().getDimensionPixelSize(R.dimen.card_developer_margin);
        wallpaperWidth = screenWidth - 2 * margin;
        wallpaperHeight = context.getResources().getDimensionPixelSize(R.dimen.card_developer_main_height);
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

        ViewGroup.LayoutParams iconParams = holder.iconView.getLayoutParams();
        String url = developer.getImage().getUrl();
        Picasso.with(context)
                .load(url)
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .resize(iconParams.width, iconParams.height)
                .into(holder.iconView);

        Picasso.with(context)
                .load(wallpapers[position % wallpapers.length])
                .resize(wallpaperWidth, wallpaperHeight)
                .centerCrop()
                .into(holder.wallpaper, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.nameView.setTextColor(ContextCompat.getColor(context, R.color.white));
                        holder.descriptionView.setTextColor(ContextCompat.getColor(context, R.color.white));
                    }

                    @Override
                    public void onError() {
                        holder.nameView.setTextColor(ContextCompat.getColor(context, R.color.grey));
                        holder.descriptionView.setTextColor(ContextCompat.getColor(context, R.color.grey));
                    }
                });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        Typeface typeface = Typeface.create("sans-serif-regular", Typeface.BOLD);
        int buttonPadding = context.getResources().getDimensionPixelSize(R.dimen.social_button_padding);

        holder.links.removeAllViews();
        List<Link> links = developer.getLinks();
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

        private ImageView wallpaper;
        private TextView nameView;
        private TextView descriptionView;
        private CircleImageView iconView;
        private LinearLayout links;

        ViewHolder(View itemView) {
            super(itemView);
            wallpaper = (ImageView) itemView.findViewById(R.id.wallpaper);
            nameView = (TextView) itemView.findViewById(R.id.name);
            descriptionView = (TextView) itemView.findViewById(R.id.description);
            iconView = (CircleImageView) itemView.findViewById(R.id.icon);
            links = (LinearLayout) itemView.findViewById(R.id.links);
        }
    }
}
