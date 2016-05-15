package com.khasang.forecast.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.khasang.forecast.R;

/*
 * TeamFragment.java     15.04.2016
 *
 * Copyright (c) 2016 Vladislav Laptev,
 * All rights reserved. Used by permission.
 */

public class TeamFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team, container, false);
        return view;
    }
}
