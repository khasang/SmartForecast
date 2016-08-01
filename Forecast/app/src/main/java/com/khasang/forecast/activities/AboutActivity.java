package com.khasang.forecast.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.khasang.forecast.R;
import com.khasang.forecast.fragments.ChangelogFragment;
import com.khasang.forecast.fragments.TeamFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Активити "О приложении"
 */
public class AboutActivity extends BaseActivity {

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

    public void showImageDialog() {
        if(imageDialog == null) {
            imageDialog = new Dialog(this);
            imageDialog.setCancelable(false);
            imageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            imageDialog.show();
            imageDialog.setContentView(R.layout.full_image);
        } else {
            imageDialog.show();
            imageDialog.setContentView(R.layout.full_image);
        }
    }

    public void hideImageDialog() {
        if(imageDialog != null){
            if (imageDialog.isShowing()) {
                imageDialog.hide();
            }
        }
    }
}
