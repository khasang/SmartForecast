package com.khasang.forecast.activities;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.khasang.forecast.R;
import com.khasang.forecast.adapters.RecyclerAdapter;
import com.khasang.forecast.adapters.etc.HidingScrollListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CopyPasteStd on 29.11.15.
 *
 * Activity для выбора местоположения
 */
public class CityPickerActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private ImageButton fabBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_picker);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final Drawable upArrow = ContextCompat.getDrawable(this, R.mipmap.ic_arrow_back_white_24dp);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.back_arrow), PorterDuff.Mode.SRC_ATOP);
        //TODO fix NullPointerException
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle(getString(R.string.city_list));
        //toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white));

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(createItemList());
        recyclerView.setAdapter(recyclerAdapter);

        /** Вычисляет степень прокрутки и выполняет нужное действие.*/
        recyclerView.addOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                hideViews();
            }

            @Override
            public void onShow() {
                showViews();
            }
        });

        fabBtn = (ImageButton) findViewById(R.id.fabBtn);
        fabBtn.setOnClickListener(this);
    }

    // Вспомогательные методы
    private void hideViews() {
        toolbar.animate().translationY(-toolbar.getHeight())
                .setInterpolator(new AccelerateInterpolator(2));

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) fabBtn.getLayoutParams();
        int fabBottomMargin = lp.bottomMargin;
        fabBtn.animate().translationY(fabBtn.getHeight() +
                fabBottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();
    }

    private void showViews() {
        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
        fabBtn.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabBtn:
                Toast.makeText(this, "Кнопка добавления города", Toast.LENGTH_SHORT).show();
                break;
        }

    }

    /**
     * HomeButton for API 14
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // Вспомогательный метод для подготовки списка
    private List<String> createItemList() {
        List<String> itemList = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            itemList.add("Город " + i);
        }
        return itemList;
    }
}
