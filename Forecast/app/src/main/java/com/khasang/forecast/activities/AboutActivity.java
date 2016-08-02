package com.khasang.forecast.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.khasang.forecast.R;
import com.khasang.forecast.fragments.ChangelogFragment;
import com.khasang.forecast.fragments.TeamFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Активити "О приложении"
 */
public class AboutActivity extends BaseActivity {

    private static final int MAX_DY_FOR_EXIT = 300;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.tabs)
    TabLayout tabLayout;

    Dialog imageDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_about);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveActivity();
            }
        });

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ChangelogFragment(), getString(R.string.changelog));
        adapter.addFragment(new TeamFragment(), getString(R.string.team));
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragments = new ArrayList<>();
        private final List<String> fragmentsTitles = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            fragmentsTitles.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentsTitles.get(position);
        }
    }

    public void showImageDialog(String url, int imageWidth, int imageHeight) {
        if (url == null || imageWidth == 0 || imageHeight == 0) {
            return;
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

        if (imageDialog == null) {
            imageDialog = new Dialog(this, R.style.dialog_fullscreen_image);
            imageDialog.setCancelable(true);
            imageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            imageDialog.setContentView(R.layout.full_image);
            imageDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT);
            imageDialog.show();
        } else {
            imageDialog.setContentView(R.layout.full_image);
            imageDialog.show();
        }

        final ImageView imageView = ((ImageView) imageDialog.findViewById(R.id.full_image_view));
        imageView.setImageResource(android.R.color.transparent);
        Picasso.with(this)
                .load(url)
                .resize(imageViewWidth, imageViewHeight)
                .into(imageView);

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
                            imageView.setImageResource(android.R.color.transparent);
                            hideImageDialog();
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

        imageDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                imageView.setImageResource(android.R.color.transparent);
            }
        });

        imageDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                imageView.setImageResource(android.R.color.transparent);
            }
        });

    }

    public void hideImageDialog() {
        if (imageDialog != null) {
            if (imageDialog.isShowing()) {
                imageDialog.hide();
            }
        }
    }
}
