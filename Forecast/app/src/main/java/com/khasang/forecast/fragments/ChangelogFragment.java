package com.khasang.forecast.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.khasang.forecast.R;
import com.khasang.forecast.activities.FullImageActivity;
import com.khasang.forecast.adapters.ChangelogAdapter;
import com.khasang.forecast.models.Changelog;
import com.khasang.forecast.models.Image;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class ChangelogFragment extends BaseFragment implements ChangelogAdapter.ImageClickListener {

    private List<Changelog> changelogs;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_changelog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changelogs = new ArrayList<>();

        Image changelog1Image = new Image("http://khasang.github.io/SmartForecast/img/services/version-2.6.png",
                4268, 3840);
        Changelog changelog1 = new Changelog();
        changelog1.setVersion("Версия 2.6");
        changelog1.setDate(new GregorianCalendar(2016, 4, 25).getTime());
        changelog1.setImage(changelog1Image);
        changelog1.setChangesResId(R.string.version_2_6);

        Image changelog2Image = new Image("http://khasang.github.io/SmartForecast/img/services/V.2.3_combo.png",
                4268, 3840);
        Changelog changelog2 = new Changelog();
        changelog2.setVersion("Версия 2.3");
        changelog2.setDate(new GregorianCalendar(2016, 3, 22).getTime());
        changelog2.setImage(changelog2Image);
        changelog2.setChangesResId(R.string.version_2_3);

        changelogs.add(changelog1);
        changelogs.add(changelog2);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        ChangelogAdapter adapter = new ChangelogAdapter(getContext(), changelogs);
        adapter.setImageClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onImageClicked(ImageView imageView, Image image) {
        Intent intent = new Intent(getContext(), FullImageActivity.class);
        intent.putExtra(FullImageActivity.URL, image.getUrl());
        intent.putExtra(FullImageActivity.IMAGE_WIDTH, image.getWidth());
        intent.putExtra(FullImageActivity.IMAGE_HEIGHT, image.getHeight());

        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(getActivity(), imageView, getString(R.string.shared_image));
        startActivity(intent, options.toBundle());
    }
}
