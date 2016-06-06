package com.khasang.forecast.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.khasang.forecast.R;
import com.khasang.forecast.adapters.TeamAdapter;
import com.khasang.forecast.models.Developer;
import com.khasang.forecast.models.Image;
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

        Image romanNovoselovImage = new Image("http://khasang.github" +
                ".io/SmartForecast/img/team/roman.png", 400, 400);
        Developer romanNovoselov = new Developer("Roman Novoselov", romanNovoselovImage, R.string.roman_novoselov);
        Link romanNovoselovLink1 = new Link("SOF", "http://ru.stackoverflow.com/users/31321/roman-novoselov?tab=profile");
        Link romanNovoselovLink2 = new Link("GitHub", "https://github.com/RNOVOSELOV");
        Link romanNovoselovLink3 = new Link("LinkedIn", "https://www.linkedin.com/in/RNOVOSELOV");
        romanNovoselov.addLink(romanNovoselovLink1);
        romanNovoselov.addLink(romanNovoselovLink2);
        romanNovoselov.addLink(romanNovoselovLink3);

        Image alexandrLivodinovImage = new Image("http://khasang.github" +
                ".io/SmartForecast/img/team/aleksandr.png", 300, 300);
        Developer alexandrLivodinov = new Developer("Alexandr Livodinov", alexandrLivodinovImage, R.string.alexandr_lihovidov);
        Link alexandrLivodinovLink1 = new Link("LinkedIn", "https://ru.linkedin.com/in/aleksandrlihovidov");
        alexandrLivodinov.addLink(alexandrLivodinovLink1);

        Image timofeyKorotkovImage = new Image("http://khasang.github" +
                ".io/SmartForecast/img/team/minime.png", 225, 225);
        Developer timofeyKorotkov = new Developer("Timofey Korotkov", timofeyKorotkovImage, R.string.timofey_korotkov);
        Link timofeyKorotkovLink1 = new Link("WWW", "https://www.copypastestd.xyz/");
        Link timofeyKorotkovLink2 = new Link("GitHub", "https://github.com/copypastestd");
        Link timofeyKorotkovLink3 = new Link("LinkedIn", "https://www.linkedin.com/in/copypastestd");
        timofeyKorotkov.addLink(timofeyKorotkovLink1);
        timofeyKorotkov.addLink(timofeyKorotkovLink2);
        timofeyKorotkov.addLink(timofeyKorotkovLink3);

        developers.add(romanNovoselov);
        developers.add(alexandrLivodinov);
        developers.add(timofeyKorotkov);
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
