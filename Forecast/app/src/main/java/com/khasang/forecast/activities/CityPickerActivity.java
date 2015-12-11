package com.khasang.forecast.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.khasang.forecast.PositionManager;
import com.khasang.forecast.R;
import com.khasang.forecast.adapters.RecyclerAdapter;
import com.khasang.forecast.adapters.etc.HidingScrollListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by CopyPasteStd on 29.11.15.
 *
 * Activity для выбора местоположения
 */
public class CityPickerActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    String TAG = "MyTAG";
    public final static String CITY_PICKER_TAG = "com.khasang.forecast.activities.CityPickerActivity";

    RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    List<String> cityList;

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
    //    getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle(getString(R.string.city_list));
        //TODO Проверить код кнопки HOME - цвет должен быть белый (не работает)
        //toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white));

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        cityList = new ArrayList<>();

        recyclerAdapter = new RecyclerAdapter(cityList, this, this);
        recyclerView.setAdapter(recyclerAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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
        createItemList();
        Log.d(TAG, String.valueOf(PositionManager.getInstance().getPositions()));

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = recyclerView.getChildAdapterPosition(viewHolder.itemView) - 1;
                PositionManager.getInstance().removePosition(cityList.get(position));
                cityList.remove(position);
                recyclerAdapter.notifyDataSetChanged();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
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
                showChooseCityDialog();
                return;
            case R.id.recycler_item:
                final int position = recyclerView.getChildAdapterPosition(v);
                Intent answerIntent = new Intent();
                answerIntent.putExtra(CITY_PICKER_TAG, cityList.get(position - 1));
                setResult(RESULT_OK, answerIntent);
                finish();
                return;
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
            case R.id.clear_favorite:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Вы действительно хотите очистить список городов?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CityPickerActivity.this.clearList();
                        recyclerAdapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // Вспомогательный метод для подготовки списка
    private List<String> createItemList() {
        Set<String> cities = PositionManager.getInstance().getPositions();
        for (String city : cities) {
            cityList.add(city);
        }
        Collections.sort(cityList);
        return cityList;
    }

    // Вспомогательный метод для добавления города в список
    private void addItem(String city) {
        city = city.trim().toLowerCase();
        city = city.substring(0, 1).toUpperCase() + city.substring(1);
        if (!PositionManager.getInstance().positionIsPresent(city)) {
            PositionManager.getInstance().addPosition(city);
        }
        Intent answerIntent = new Intent();
        answerIntent.putExtra(CITY_PICKER_TAG, city);
        setResult(RESULT_OK, answerIntent);
        finish();
    }

    private void clearList () {
        PositionManager.getInstance().removePositions();
        cityList.clear();
    }

    private void showChooseCityDialog() {
        final View view = getLayoutInflater().inflate(R.layout.choose_city_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText chooseCity = (EditText) view.findViewById(R.id.editTextCityName);
        builder.setTitle("Введите название города")
                .setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String positionName = chooseCity.getText().toString();
                        try {
                            addItem(positionName);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_activity_city_picker, menu);
        menu.findItem(R.id.clear_favorite).setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.recycler_item:
                final int position = recyclerView.getChildAdapterPosition(v);
                TextView thisCity = (TextView) recyclerView.getChildAt(position).findViewById(R.id.cityTW);
                String cityName = String.valueOf(thisCity.getText());
                Log.i(TAG, "OnLongClick: город - " + cityName);

                //TODO реализовать удаление города через Context Menu
    /*            Toast.makeText(this, "click on " + thisCity.getText(), Toast.LENGTH_SHORT).show();
                cityList.remove(cityName);
                recyclerAdapter.notifyDataSetChanged();
                PositionManager.getInstance().removePosition(cityName);*/

        }
    return true;
    }
}
