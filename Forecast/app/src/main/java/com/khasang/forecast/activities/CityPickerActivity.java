package com.khasang.forecast.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.khasang.forecast.OpenWeatherMap;
import com.khasang.forecast.PositionManager;
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
    String TAG = this.getClass().getSimpleName();

    Intent answerIntent = new Intent();
    public final static String CITY = "ПИТЕР";

    RecyclerView favoriteList;
    List<String> cityList = new ArrayList<>();

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

        favoriteList = (RecyclerView) findViewById(R.id.recyclerView);
        favoriteList.setLayoutManager(new LinearLayoutManager(this));
        //RecyclerAdapter recyclerAdapter = new RecyclerAdapter(createItemList());
        //RecyclerAdapter recyclerAdapter = new RecyclerAdapter(cityList, mListener);
        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(cityList, this);
        favoriteList.setAdapter(recyclerAdapter);

        /** Вычисляет степень прокрутки и выполняет нужное действие.*/
        favoriteList.addOnScrollListener(new HidingScrollListener() {
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
        //if (favoriteList =! null)
        //final int position = favoriteList.getChildAdapterPosition(v);
        switch (v.getId()) {
            case R.id.fabBtn:
                //Toast.makeText(this, "Start AlertDialog", Toast.LENGTH_SHORT).show();
                showChooseCityDialog();
                return;
            case R.id.recycler_item:
                final int position = favoriteList.getChildAdapterPosition(v);
                //final String string = cityList.get(position);
                //TODO Получать текст по ID элемента
                answerIntent.putExtra(CITY, positionName + position);
                setResult(RESULT_OK, answerIntent);
                finish();
                return;

        }

//        Intent answerIntent = new Intent();
//        switch (v.getId()) {
//            case R.id.fabBtn:
//
//                Toast.makeText(this, "Выбран город ПИТЕР", Toast.LENGTH_SHORT).show();
//                answerIntent.putExtra(CITY, "ПИТЕР");
//                break;
//        }
//        setResult(RESULT_OK, answerIntent);
//        finish();
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
        //List<String> cityList = new ArrayList<>();
        List<String> itemList = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            itemList.add("Город " + i);
        }
        return itemList;
    }

    // Вспомогательный метод для добавления города в список
    int i = 0;
    private List<String> addItemToList(String city) {
        cityList.add(city + " " + i);
        i++;
        return cityList;
    }

    private PositionManager manager;
    private String positionName;
    private ArrayList<String> positions;
    private OpenWeatherMap owm;

    private void showChooseCityDialog() {
        final View view = getLayoutInflater().inflate(R.layout.choose_city_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText chooseCity = (EditText) view.findViewById(R.id.editTextCityName);
        builder.setTitle("Введите название города")
                .setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        positionName = chooseCity.getText().toString();
                        //positions.add(positionName);
                        //manager.initPositions(positions);
                        //manager.setCurrPosition(positionName);
                        try {
                            addItemToList(positionName);

                         /*   answerIntent.putExtra(CITY, positionName);
                            setResult(RESULT_OK, answerIntent);
                            finish();*/

                            //owm.updateWeather(manager.getPosition(positionName).getCoordinate(), manager);
                            Log.i(TAG, "Coordinates: " + manager.getPosition(positionName).getCoordinate().getLatitude() + ", " + manager.getPosition(positionName).getCoordinate().getLongitude());
                        } catch (NullPointerException e) {
                            e.printStackTrace();

                            //TODO Check catch
                            //Toast.makeText(CityPickerActivity.this, "Вы ввели некорректный адрес, повторите попытку", Toast.LENGTH_LONG).show();
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
}
