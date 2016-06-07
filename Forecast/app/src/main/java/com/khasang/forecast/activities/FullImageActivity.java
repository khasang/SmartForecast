package com.khasang.forecast.activities;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.Display;
import android.widget.ImageView;

import com.khasang.forecast.R;
import com.squareup.picasso.Picasso;

/**
 * Активити с полноразмерной картинкой из Changelog
 */
public class FullImageActivity extends BaseActivity {

    public static final String URL = "url";
    public static final String IMAGE_WIDTH = "width";
    public static final String IMAGE_HEIGHT = "height";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        int imageViewWidth;
        int imageViewHeight;
        if (imageWidth > screenWidth) {
            imageViewWidth = screenWidth;
            imageViewHeight = imageViewWidth * imageHeight / imageWidth;
        } else {
            imageViewWidth = imageWidth;
            imageViewHeight = imageViewWidth * imageHeight / imageWidth;
        }
        if (imageViewHeight > screenHeight) {
            imageViewHeight = screenHeight;
            imageViewWidth = imageViewHeight * imageWidth / imageHeight;
        }

        ImageView imageView = (ImageView) findViewById(R.id.image);
        Picasso.with(this)
                .load(url)
                .resize(imageViewWidth, imageViewHeight)
                .into(imageView);
    }

    @Override
    public void onBackPressed() {
        ActivityCompat.finishAfterTransition(this);
    }
}
