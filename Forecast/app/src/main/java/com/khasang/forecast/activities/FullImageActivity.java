package com.khasang.forecast.activities;

import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.khasang.forecast.R;
import com.squareup.picasso.Picasso;

import butterknife.BindView;

/**
 * Активити с полноразмерной картинкой из Changelog
 */
public class FullImageActivity extends BaseActivity {

    public static final String URL = "url";
    public static final String IMAGE_WIDTH = "width";
    public static final String IMAGE_HEIGHT = "height";

    @BindView(R.id.image) ImageView imageView;
    private View decorView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hide the status bar.
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            decorView = getWindow().getDecorView();
        }

        setContentView(R.layout.activity_full_image);

        String url = getIntent().getStringExtra(URL);
        int imageWidth = getIntent().getIntExtra(IMAGE_WIDTH, 0);
        int imageHeight = getIntent().getIntExtra(IMAGE_HEIGHT, 0);
        if (url == null || imageWidth == 0 || imageHeight == 0) {
            finish();
        }

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;

        int imageViewWidth = screenWidth;
        int imageViewHeight = imageViewWidth * imageHeight / imageWidth;
        if (imageViewHeight > screenHeight) {
            imageViewHeight = screenHeight;
            imageViewWidth = imageViewHeight * imageWidth / imageHeight;
        }

        Picasso.with(this)
                .load(url)
                .resize(imageViewWidth, imageViewHeight)
                .into(imageView);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && decorView != null) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
