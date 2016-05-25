package com.khasang.forecast.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.khasang.forecast.R;
import com.khasang.forecast.adapters.TeamAdapter;
import com.khasang.forecast.models.Developer;

import java.util.ArrayList;
import java.util.List;

public class TeamFragment extends BaseFragment {

    private List<Developer> developers;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_team;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        developers = new ArrayList<>();

        Developer developer = new Developer("Vasiliy", "Senior developer", android.R.drawable.star_big_on);
        developers.add(developer);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        TeamAdapter adapter = new TeamAdapter(developers);
        recyclerView.setAdapter(adapter);
    }
}
