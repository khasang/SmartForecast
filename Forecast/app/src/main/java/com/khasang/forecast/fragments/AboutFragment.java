package com.khasang.forecast.fragments;

import android.content.Intent;

import com.khasang.forecast.R;
import com.khasang.forecast.activities.ChangelogActivity;
import com.khasang.forecast.activities.TeamActivity;

import butterknife.OnClick;

public class AboutFragment extends BaseFragment {

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_about;
    }

    @OnClick(R.id.changelog)
    public void showChangelog() {
        startActivity(new Intent(getContext(), ChangelogActivity.class));
    }

    @OnClick(R.id.team)
    public void showTeam() {
        startActivity(new Intent(getContext(), TeamActivity.class));
    }
}
