package com.khasang.forecast.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.khasang.forecast.R;
import com.khasang.forecast.activities.ChangelogActivity;
import com.khasang.forecast.activities.TeamActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.changelog) public void showChangelog() {
        startActivity(new Intent(getContext(), ChangelogActivity.class));
    }

    @OnClick(R.id.team) public void showTeam() {
        startActivity(new Intent(getContext(), TeamActivity.class));
    }
}
