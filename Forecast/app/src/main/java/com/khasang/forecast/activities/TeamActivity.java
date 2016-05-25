package com.khasang.forecast.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.khasang.forecast.R;
import com.khasang.forecast.fragments.TeamFragment;

import butterknife.BindView;

/**
 * Активити про разработчиков
 */
public class TeamActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_fragment);

        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new TeamFragment())
                .commit();
    }
}
