package com.khasang.forecast.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.khasang.forecast.R;
import com.khasang.forecast.adapters.TeamAdapter;
import com.khasang.forecast.models.Developer;
import com.khasang.forecast.models.Link;

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

        Link developer1Link1 = new Link("GitHub", "https://github.com/khasang/SmartForecast");
        Link developer1Link2 = new Link("VK", "https://vk.com/smartforecast");
        Developer developer1 = new Developer("Smart Forecast", "Senior developer", android.R.drawable.star_big_on);
        developer1.addLink(developer1Link1);
        developer1.addLink(developer1Link2);

        Link developer2Link1 = new Link("Google+", "https://plus.google.com/+googleru");
        Link developer2Link2 = new Link("GitHub", "https://github.com/khasang/SmartForecast");
        Developer developer2 = new Developer("Forecast Smart", "UX designer", android.R.drawable.star_big_off);
        developer2.addLink(developer2Link1);
        developer2.addLink(developer2Link2);

        developers.add(developer1);
        developers.add(developer2);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        TeamAdapter adapter = new TeamAdapter(getContext(), developers);
        recyclerView.setAdapter(adapter);
    }
}
