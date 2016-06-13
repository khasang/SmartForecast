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
        Developer romanNovoselov = new Developer(R.string.roman_novoselov, romanNovoselovImage, R.string
                .roman_novoselov_desc);
        Link romanNovoselovLink1 = new Link("SOF", "http://ru.stackoverflow" +
                ".com/users/31321/roman-novoselov?tab=profile");
        Link romanNovoselovLink2 = new Link("GitHub", "https://github.com/RNOVOSELOV");
        Link romanNovoselovLink3 = new Link("LinkedIn", "https://www.linkedin.com/in/RNOVOSELOV");
        romanNovoselov.addLink(romanNovoselovLink1);
        romanNovoselov.addLink(romanNovoselovLink2);
        romanNovoselov.addLink(romanNovoselovLink3);

        Image alexandrLivodinovImage = new Image("http://khasang.github" +
                ".io/SmartForecast/img/team/aleksandr.png", 300, 300);
        Developer alexandrLivodinov = new Developer(R.string.alexandr_lihovidov, alexandrLivodinovImage, R.string
                .alexandr_lihovidov_desc);
        Link alexandrLivodinovLink1 = new Link("LinkedIn", "https://ru.linkedin.com/in/aleksandrlihovidov");
        alexandrLivodinov.addLink(alexandrLivodinovLink1);

        Image timofeyKorotkovImage = new Image("http://khasang.github" +
                ".io/SmartForecast/img/team/minime.png", 225, 225);
        Developer timofeyKorotkov = new Developer(R.string.timofey_korotkov, timofeyKorotkovImage, R.string
                .timofey_korotkov_desc);
        Link timofeyKorotkovLink1 = new Link("WWW", "https://www.copypastestd.xyz/");
        Link timofeyKorotkovLink2 = new Link("GitHub", "https://github.com/copypastestd");
        Link timofeyKorotkovLink3 = new Link("LinkedIn", "https://www.linkedin.com/in/copypastestd");
        timofeyKorotkov.addLink(timofeyKorotkovLink1);
        timofeyKorotkov.addLink(timofeyKorotkovLink2);
        timofeyKorotkov.addLink(timofeyKorotkovLink3);

        Image alexandrPerfilevImage = new Image("http://khasang.github.io/SmartForecast/img/team/developer.png", 225,
                225);
        Developer alexandrPerfilev = new Developer(R.string.alexandr_perfilev, alexandrPerfilevImage, R.string
                .alexandr_perfilev_desc);
        Link alexandrPerfilevLink1 = new Link("GitHub", "https://github.com/aperfilyev");
        Link alexandrPerfilevLink2 = new Link("Mail", "alexander.perfilyev@gmail.com");
        alexandrPerfilev.addLink(alexandrPerfilevLink1);
        alexandrPerfilev.addLink(alexandrPerfilevLink2);

        Image maximKulikovImage = new Image("http://khasang.github.io/SmartForecast/img/team/maksim.png", 250, 250);
        Developer maximKulikov = new Developer(R.string.maxim_kulikov, maximKulikovImage, R.string
                .maxim_kulikov_desc);
        Link maximKulikovLink1 = new Link("GitHub", "https://github.com/uoles/");
        Link maximKulikovLink2 = new Link("LinkedIn", "https://www.linkedin" +
                ".com/in/%D0%BC%D0%B0%D0%BA%D1%81%D0%B8%D0%BC-%D0%BA%D1%83%D0%BB%D0%B8%D0%BA%D0%BE%D0%B2-88123782");
        maximKulikov.addLink(maximKulikovLink1);
        maximKulikov.addLink(maximKulikovLink2);

        Image erdeniErdyneevImage = new Image("http://khasang.github.io/SmartForecast/img/team/dEn13L.png", 300, 300);
        Developer erdeniErdyneev = new Developer(R.string.erdeni_erdyneev, erdeniErdyneevImage, R.string
                .erdeni_erdyneev_desc);
        Link erdeniErdyneevLink1 = new Link("GitHub", "https://github.com/dEn13L");
        Link erdeniErdyneevLink2 = new Link("LinkedIn", "https://www.linkedin.com/in/erdeni-erdyneev-89950060");
        erdeniErdyneev.addLink(erdeniErdyneevLink1);
        erdeniErdyneev.addLink(erdeniErdyneevLink2);

        Image airatHairulinImage = new Image("http://khasang.github.io/SmartForecast/img/team/airat.png", 225, 225);
        Developer airatHairulin = new Developer(R.string.airat_hairulin, airatHairulinImage, R.string
                .airat_hairulin_desc);
        Link airatHairulinLink1 = new Link("VK", "https://vk.com/id17864859");
        Link airatHairulinLink2 = new Link("GitHub", "https://github.com/AIRAT1");
        Link airatHairulinLink3 = new Link("LinkedIn", "https://www.linkedin.com/in/ayrat-hairullin-b86744112");
        airatHairulin.addLink(airatHairulinLink1);
        airatHairulin.addLink(airatHairulinLink2);
        airatHairulin.addLink(airatHairulinLink3);

        Image dmitryRyabushkinImage = new Image("http://khasang.github.io/SmartForecast/img/team/dmitry.png", 300, 300);
        Developer dmitryRyabushkin = new Developer(R.string.dmitry_ryabushkin, dmitryRyabushkinImage, R.string
                .dmitry_ryabushkin_desc);
        Link dmitryRyabushkinLink1 = new Link("VK", "https://vk.com/ryabos");
        dmitryRyabushkin.addLink(dmitryRyabushkinLink1);

        developers.add(romanNovoselov);
        developers.add(alexandrLivodinov);
        developers.add(timofeyKorotkov);
        developers.add(alexandrPerfilev);
        developers.add(maximKulikov);
        developers.add(erdeniErdyneev);
        developers.add(airatHairulin);
        developers.add(dmitryRyabushkin);
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
