package com.khasang.forecast.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.khasang.forecast.R;
import com.khasang.forecast.activities.AboutActivity;
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

        Image changelog_2_7_image = new Image("https://raw.githubusercontent.com/khasang/SmartForecast/gh-pages/img/services/version-2.7.png", 4268, 3840);
        Changelog changelog_2_7 = new Changelog();
        changelog_2_7.setVersionResId(R.string.version_2_7);
        changelog_2_7.setDate(new GregorianCalendar(2016, 6, 25).getTime());
        changelog_2_7.setImage(changelog_2_7_image);
        changelog_2_7.setChangesResId(R.string.version_2_7_changelog);

        Image changelog_2_6_image = new Image("http://khasang.github.io/SmartForecast/img/services/version-2.6.png",
                4268, 3840);
        Changelog changelog_2_6 = new Changelog();
        changelog_2_6.setVersionResId(R.string.version_2_6);
        changelog_2_6.setDate(new GregorianCalendar(2016, 4, 21).getTime());
        changelog_2_6.setImage(changelog_2_6_image);
        changelog_2_6.setChangesResId(R.string.version_2_6_changelog);

        Image changelog_2_3_image = new Image("http://khasang.github.io/SmartForecast/img/services/V.2.3_combo.png",
                4268, 3840);
        Changelog changelog_2_3 = new Changelog();
        changelog_2_3.setVersionResId(R.string.version_2_3);
        changelog_2_3.setDate(new GregorianCalendar(2016, 2, 18).getTime());
        changelog_2_3.setImage(changelog_2_3_image);
        changelog_2_3.setChangesResId(R.string.version_2_3_changelog);

        Image changelog_2_1_image = new Image("http://khasang.github.io/SmartForecast/img/services/V.2.1_1.png",
                4268, 3840);
        Changelog changelog_2_1 = new Changelog();
        changelog_2_1.setVersionResId(R.string.version_2_1);
        changelog_2_1.setDate(new GregorianCalendar(2016, 1, 25).getTime());
        changelog_2_1.setImage(changelog_2_1_image);
        changelog_2_1.setChangesResId(R.string.version_2_1_changelog);

        Image changelog_2_0_image = new Image("http://khasang.github.io/SmartForecast/img/services/V.2.0_combo.png",
                4268, 3840);
        Changelog changelog_2_0 = new Changelog();
        changelog_2_0.setVersionResId(R.string.version_2_0);
        changelog_2_0.setDate(new GregorianCalendar(2016, 1, 23).getTime());
        changelog_2_0.setImage(changelog_2_0_image);
        changelog_2_0.setChangesResId(R.string.version_2_0_changelog);

        Image changelog_1_0_image = new Image("http://khasang.github.io/SmartForecast/img/services/V.1.0_combo.png",
                4268, 3840);
        Changelog changelog_1_0 = new Changelog();
        changelog_1_0.setVersionResId(R.string.version_1_0);
        changelog_1_0.setDate(new GregorianCalendar(2015, 11, 27).getTime());
        changelog_1_0.setImage(changelog_1_0_image);
        changelog_1_0.setChangesResId(R.string.version_1_0_changelog);

        changelogs.add(changelog_2_7);
        changelogs.add(changelog_2_6);
        changelogs.add(changelog_2_3);
        changelogs.add(changelog_2_1);
        changelogs.add(changelog_2_0);
        changelogs.add(changelog_1_0);
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
        ((AboutActivity)getActivity()).showImageDialog();
        /*
        Intent intent = new Intent(getContext(), FullImageActivity.class);
        intent.putExtra(FullImageActivity.URL, image.getUrl());
        intent.putExtra(FullImageActivity.IMAGE_WIDTH, image.getWidth());
        intent.putExtra(FullImageActivity.IMAGE_HEIGHT, image.getHeight());

        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(getActivity(), imageView, getString(R.string.shared_image));
        startActivity(intent, options.toBundle());
        */
    }
}
