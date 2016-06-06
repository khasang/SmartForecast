package com.khasang.forecast.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.khasang.forecast.R;
import com.khasang.forecast.adapters.ChangelogAdapter;
import com.khasang.forecast.models.Changelog;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class ChangelogFragment extends BaseFragment {

    private List<Changelog> changelogs;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_changelog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changelogs = new ArrayList<>();

        Changelog changelog1 = new Changelog();
        changelog1.setVersion("Версия 2.6");
        changelog1.setDate(new GregorianCalendar(2016, 4, 25).getTime());
        changelog1.setImageUrl("http://khasang.github.io/SmartForecast/img/services/version-2.6.png");
        changelog1.setChangesRes(R.string.version_2_6);
        changelog1.setImageWidth(4268);
        changelog1.setImageHeight(3840);

        changelogs.add(changelog1);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        ChangelogAdapter adapter = new ChangelogAdapter(getContext(), changelogs);
        recyclerView.setAdapter(adapter);
    }
}
