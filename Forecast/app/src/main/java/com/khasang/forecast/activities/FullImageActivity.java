package com.khasang.forecast.activities;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
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
    private static final int MAX_DY_FOR_EXIT = 300;

    @BindView(R.id.image)
    ImageView imageView;


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

        imageView.setOnTouchListener(new View.OnTouchListener() {
            float startY;
            float dY;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        dY = view.getY() - event.getRawY();
                        startY = view.getY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        view.animate()
                                .y(event.getRawY() + dY)
                                .setDuration(0)
                                .start();
                        break;
                    case MotionEvent.ACTION_UP:
                        if (Math.abs(startY - (event.getRawY() + dY)) > MAX_DY_FOR_EXIT) {
                            leaveActivity();
                        }
                        view.animate()
                                .y(startY)
                                .setDuration(0)
                                .start();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });


        Picasso.with(this)
                .load(url)
                .resize(imageViewWidth, imageViewHeight)
                .into(imageView);
    }

    @Override
    public void onBackPressed() {
        leaveActivity();
    }
}
